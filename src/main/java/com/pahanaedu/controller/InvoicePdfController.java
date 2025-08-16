package com.pahanaedu.controller;

import com.pahanaedu.dao.OrderDAO;
import com.pahanaedu.dao.UserDAO;
import com.pahanaedu.model.Order;
import com.pahanaedu.model.OrderDetail;
import com.pahanaedu.model.User;
import com.pahanaedu.util.EmailTemplate;
import com.pahanaedu.util.Mailer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.PDFont;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@WebServlet("/InvoicePdfController")
public class InvoicePdfController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final float MARGIN_LEFT = 36f;
    private static final float MARGIN_TOP  = 36f;
    private static final float FONT_SIZE   = 11f;
    private static final float LEADING     = 14f;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idStr = request.getParameter("orderId");
        if (idStr == null || idStr.isBlank()) {
            response.sendError(400, "Missing orderId");
            return;
        }

        final int orderId;
        try { orderId = Integer.parseInt(idStr); }
        catch (NumberFormatException e) { response.sendError(400, "Invalid orderId"); return; }

        OrderDAO dao = new OrderDAO();
        Order order = dao.getOrderWithDetails(orderId);
        if (order == null) { response.sendError(404, "Order not found"); return; }

        // Use orderDate from DB if present, otherwise now()
        LocalDateTime dt = (order.getOrderDate() != null)
                ? order.getOrderDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                : LocalDateTime.now();

        String dateStr = dt.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String timeStr = dt.format(DateTimeFormatter.ofPattern("hh:mm a"));

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);

        // === Build PDF into memory (byte[])
        byte[] pdfBytes;
        String fileName = order.getInvoiceNumber() + ".pdf";
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            doc.addPage(page);

            PDFont mono  = PDType1Font.COURIER;
            PDFont monoB = PDType1Font.COURIER_BOLD;

            PDPageContentStream cs = new PDPageContentStream(doc, page);
            float y = page.getMediaBox().getHeight() - MARGIN_TOP;

            y = draw(cs, mono,  FONT_SIZE, MARGIN_LEFT, y, "------------------------------------------------------------");
            y = draw(cs, monoB, FONT_SIZE+1, MARGIN_LEFT, y, "                    PAHANA EDU BOOKSHOP");
            y = draw(cs, mono,  FONT_SIZE, MARGIN_LEFT, y, "                No. 02, Main Street, Colombo City");
            y = draw(cs, mono,  FONT_SIZE, MARGIN_LEFT, y, "               Tel: 011-2030400 | Email: info@pahanaedu.lk");
            y = draw(cs, mono,  FONT_SIZE, MARGIN_LEFT, y, "------------------------------------------------------------");
            y = draw(cs, mono,  FONT_SIZE, MARGIN_LEFT, y,
                    String.format("Date: %-16s          Time: %-10s", dateStr, timeStr));
            y = draw(cs, mono,  FONT_SIZE, MARGIN_LEFT, y,
                    String.format("Customer: %-14s   Invoice No: %s",
                            safe(order.getCustomerCode(),14), order.getInvoiceNumber()));
            y = draw(cs, mono,  FONT_SIZE, MARGIN_LEFT, y, "------------------------------------------------------------");
            y = draw(cs, monoB, FONT_SIZE, MARGIN_LEFT, y, fixCols("Item", "Qty", "Unit Price", "Total"));
            y = draw(cs, mono,  FONT_SIZE, MARGIN_LEFT, y, "------------------------------------------------------------");

            List<OrderDetail> rows = order.getDetails();
            for (OrderDetail d : rows) {
                String name = d.getItemName();
                String qty  = String.valueOf(d.getQuantity());
                String unit = nf.format(d.getUnitPrice());
                String tot  = nf.format(d.getUnitPrice().multiply(new java.math.BigDecimal(d.getQuantity())));
                y = draw(cs, mono, FONT_SIZE, MARGIN_LEFT, y, fixCols(name, qty, unit, tot));

                if (y < 72) { // new page if needed
                    cs.close();
                    page = new PDPage(PDRectangle.A4);
                    doc.addPage(page);
                    cs = new PDPageContentStream(doc, page);
                    y = page.getMediaBox().getHeight() - MARGIN_TOP;
                    y = draw(cs, mono, FONT_SIZE, MARGIN_LEFT, y, "------------------------------------------------------------");
                }
            }

            y = draw(cs, mono,  FONT_SIZE, MARGIN_LEFT, y, "");
            y = draw(cs, mono,  FONT_SIZE, MARGIN_LEFT, y, "------------------------------------------------------------");
            String totalLine = padRight("**Total Payable:**", 54) + "Rs. " + nf.format(order.getTotalAmount());
            y = draw(cs, monoB, FONT_SIZE, MARGIN_LEFT, y, totalLine);
            y = draw(cs, mono,  FONT_SIZE, MARGIN_LEFT, y, "");
            y = draw(cs, mono,  FONT_SIZE, MARGIN_LEFT, y, "------------------------------------------------------------");
            y = draw(cs, mono,  FONT_SIZE, MARGIN_LEFT, y, "         Thank you for shopping at Pahana Edu!");
            y = draw(cs, mono,  FONT_SIZE, MARGIN_LEFT, y, "     Visit again for the best in books and stationery.");
            y = draw(cs, mono,  FONT_SIZE, MARGIN_LEFT, y, "------------------------------------------------------------");

            cs.close();

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                doc.save(baos);
                pdfBytes = baos.toByteArray();
            }
        }

        // === Look up customer's email (by id if available, else by customerCode)
        String customerEmail = null;
        String customerName  = "Customer";
        try {
            UserDAO userDao = new UserDAO();
            User u = null;

            // Try by customerId if Order has it (reflection keeps this compiling either way)
            try {
                java.lang.reflect.Method m = order.getClass().getMethod("getCustomerId");
                Object idObj = m.invoke(order);
                if (idObj instanceof Integer) {
                    Integer customerId = (Integer) idObj;
                    if (customerId != null) {
                        u = userDao.findById(customerId);
                    }
                }
            } catch (NoSuchMethodException ignored) {
                // fall back to code
            }

            // Fallback: by customerCode (Order definitely has it, you just printed it)
            if (u == null && order.getCustomerCode() != null && !order.getCustomerCode().isBlank()) {
                u = userDao.findByCustomerCode(order.getCustomerCode());
            }

            if (u != null) {
                if (u.getEmail() != null && !u.getEmail().isBlank()) customerEmail = u.getEmail();
                if (u.getUsername() != null && !u.getUsername().isBlank()) customerName = u.getUsername();
            } else {
                // last resort: if Order exposes a name, use that (reflection again)
                try {
                    java.lang.reflect.Method m2 = order.getClass().getMethod("getCustomerName");
                    Object nameObj = m2.invoke(order);
                    if (nameObj instanceof String && !((String) nameObj).isBlank()) {
                        customerName = (String) nameObj;
                    }
                } catch (NoSuchMethodException ignored2) { /* ok */ }
            }
        } catch (Exception ignored) { }

        // === Email invoice (async) if we have an email
        if (customerEmail != null && !customerEmail.isBlank()) {
            String html = EmailTemplate.invoice(
                    customerName,
                    order.getInvoiceNumber(),
                    nf.format(order.getTotalAmount()),
                    dateStr
            );
            Mailer.sendHtmlWithPdfAsync(
                    customerEmail,
                    "Your Invoice " + order.getInvoiceNumber(),
                    html,
                    fileName,
                    pdfBytes
            );
        } else {
            System.out.println("[InvoicePdfController] No customer email found for orderId="
                    + orderId + " (code=" + order.getCustomerCode() + ")");
        }

        // === Stream PDF to browser (unchanged behavior)
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=" + fileName);
        response.setContentLength(pdfBytes.length);
        response.getOutputStream().write(pdfBytes);
        response.getOutputStream().flush();
    }

    private float draw(PDPageContentStream cs, PDFont font, float size, float x, float y, String text) throws IOException {
        if (text == null) text = "";
        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();
        return y - LEADING;
    }

    // Monospace columns (Item:30, Qty:6, Unit:15, Total:13)
    private String fixCols(String item, String qty, String unit, String total) {
        return padRight(item, 30) + padLeft(qty, 6) + padLeft(unit, 15) + padLeft(total, 13);
    }
    private static String padRight(String s, int n) {
        if (s == null) s = "";
        if (s.length() > n) return s.substring(0, n);
        StringBuilder b = new StringBuilder(s);
        while (b.length() < n) b.append(' ');
        return b.toString();
    }
    private static String padLeft(String s, int n) {
        if (s == null) s = "";
        if (s.length() > n) return s.substring(0, n);
        StringBuilder b = new StringBuilder();
        while (b.length() + s.length() < n) b.append(' ');
        b.append(s);
        return b.toString();
    }
    private static String safe(String s, int max) {
        if (s == null) return "";
        return s.length() > max ? s.substring(0, max) : s;
    }
}