package com.pahanaedu.util;

public class EmailTemplate {

    public static String welcome(String username, String code, String tempPassword) {
        return ""
        + "<div style='font-family:Segoe UI,Arial,sans-serif;max-width:620px;margin:0 auto;padding:24px;"
        + "border:1px solid #eee;border-radius:10px;'>"
        + "  <h2 style='margin:0 0 6px;color:#5a38c6;'>Pahana Edu Bookshop</h2>"
        + "  <p style='margin:0;color:#666;'>No. 02, Main Street, Colombo City<br>"
        + "     📞 011-2030400 &nbsp; | &nbsp; 📧 info@pahanaedu.lk</p>"
        + "  <hr style='border:none;border-top:1px dashed #ccc;margin:16px 0'/>"

        + "  <p style='font-size:15px;color:#333;'>Dear <strong>" + esc(username) + "</strong>,</p>"
        + "  <p style='font-size:14px;color:#333;'>"
        + "    Welcome to <strong>Pahana Edu Bookshop</strong>! Your account has been created."
        + "  </p>"

        + "  <div style='background:#f7f5ff;border:1px solid #e6e1ff;padding:14px 16px;border-radius:8px;'>"
        + "    <p style='margin:0 0 4px;color:#333;'>Your login credentials:</p>"
        + "    <p style='margin:4px 0;font-size:15px;'>"
        + "      🔐 <strong>Customer Code:</strong> <span style='font-family:monospace'>" + esc(code) + "</span><br/>"
        + "      🔑 <strong>Temporary Password:</strong> <span style='font-family:monospace'>" + esc(tempPassword) + "</span>"
        + "    </p>"
        + "  </div>"

        + "  <p style='font-size:12px;color:#999;margin:16px 0 0;'>This is an automated message. Please do not reply.</p>"
        + "</div>";
    }

    public static String invoice(String customerName, String invoiceNo, String totalRs, String dateStr) {
        return ""
        + "<div style='font-family:Segoe UI,Arial,sans-serif;max-width:620px;margin:0 auto;padding:24px;"
        + "border:1px solid #eee;border-radius:10px;'>"
        + "  <h2 style='margin:0 0 6px;color:#5a38c6;'>Your Invoice from Pahana Edu</h2>"
        + "  <p style='margin:0;color:#666;'>No. 02, Main Street, Colombo City<br>"
        + "     📞 011-2030400 &nbsp; | &nbsp; 📧 info@pahanaedu.lk</p>"
        + "  <hr style='border:none;border-top:1px dashed #ccc;margin:16px 0'/>"
        + "  <p style='font-size:15px;color:#333;'>Hi <strong>" + esc(customerName) + "</strong>,</p>"
        + "  <p style='font-size:14px;color:#333;'>Thank you for your purchase. Your invoice is attached.</p>"
        + "  <ul style='font-size:14px;color:#333;'>"
        + "    <li><b>Invoice No:</b> " + esc(invoiceNo) + "</li>"
        + "    <li><b>Date:</b> " + esc(dateStr) + "</li>"
        + "    <li><b>Total:</b> Rs. " + esc(totalRs) + "</li>"
        + "  </ul>"
        + "  <p style='font-size:14px;color:#333;margin-top:18px;'>— Pahana Edu Bookshop</p>"
        + "</div>";
    }

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
    }
}

