package com.pahanaedu.util;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource; // <-- important
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class Mailer {

    // ====== SMTP SETTINGS (Gmail example) ======
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int    SMTP_PORT = 587;             // TLS
    private static final String SMTP_USER = "devinduja@gmail.com"; // your Gmail
    private static final String SMTP_PASS = "heda dcrr yefd lssx"; // your Gmail App Password
    private static final boolean DEBUG = true;

    private static Session buildSession() {
        Properties p = new Properties();
        p.put("mail.smtp.auth", "true");
        p.put("mail.smtp.starttls.enable", "true");
        p.put("mail.smtp.starttls.required", "true");
        p.put("mail.smtp.host", SMTP_HOST);
        p.put("mail.smtp.port", String.valueOf(SMTP_PORT));
        p.put("mail.smtp.ssl.trust", SMTP_HOST);
        p.put("mail.smtp.ssl.protocols", "TLSv1.2");
        p.put("mail.smtp.connectiontimeout", "10000");
        p.put("mail.smtp.timeout", "10000");
        p.put("mail.smtp.writetimeout", "10000");

        Session s = Session.getInstance(p, new Authenticator() {
            @Override protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_USER, SMTP_PASS);
            }
        });
        s.setDebug(DEBUG);
        return s;
    }

    /** HTML only (no attachment). */
    public static void sendHtml(String to, String subject, String html) throws MessagingException {
        Session session = buildSession();
        MimeMessage msg = new MimeMessage(session);
        try { msg.setFrom(new InternetAddress(SMTP_USER, "Pahana Edu Bookshop")); }
        catch (UnsupportedEncodingException e) { msg.setFrom(new InternetAddress(SMTP_USER)); }
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
        msg.setSubject(subject, "UTF-8");
        msg.setContent(html, "text/html; charset=UTF-8");
        try (Transport t = session.getTransport("smtp")) {
            t.connect(SMTP_HOST, SMTP_PORT, SMTP_USER, SMTP_PASS);
            t.sendMessage(msg, msg.getAllRecipients());
        }
    }

    public static void sendHtmlAsync(String to, String subject, String html) {
        new Thread(() -> {
            try { sendHtml(to, subject, html); if (DEBUG) System.out.println("[Mailer] Email sent to " + to); }
            catch (MessagingException e) { System.err.println("[Mailer] FAILED: " + e.getMessage()); e.printStackTrace(); }
        }, "mailer-thread").start();
    }

    /** HTML + ONE PDF attachment (synchronous). */
    public static void sendHtmlWithPdf(String to, String subject, String html, String pdfFileName, byte[] pdfBytes)
            throws MessagingException {

        Session session = buildSession();

        MimeMessage msg = new MimeMessage(session);
        try { msg.setFrom(new InternetAddress(SMTP_USER, "Pahana Edu Bookshop")); }
        catch (UnsupportedEncodingException e) { msg.setFrom(new InternetAddress(SMTP_USER)); }
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
        msg.setSubject(subject, "UTF-8");

        // Body (HTML)
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(html, "text/html; charset=UTF-8");

        // Attachment (PDF) — no InputStream needed
        MimeBodyPart pdfPart = new MimeBodyPart();
        ByteArrayDataSource ds = new ByteArrayDataSource(pdfBytes, "application/pdf");
        pdfPart.setDataHandler(new DataHandler(ds));
        pdfPart.setFileName(pdfFileName);

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(htmlPart);
        multipart.addBodyPart(pdfPart);

        msg.setContent(multipart);

        try (Transport t = session.getTransport("smtp")) {
            t.connect(SMTP_HOST, SMTP_PORT, SMTP_USER, SMTP_PASS);
            t.sendMessage(msg, msg.getAllRecipients());
        }
    }

    /** HTML + ONE PDF attachment (async). */
    public static void sendHtmlWithPdfAsync(String to, String subject, String html, String pdfFileName, byte[] pdfBytes) {
        new Thread(() -> {
            try { sendHtmlWithPdf(to, subject, html, pdfFileName, pdfBytes);
                  if (DEBUG) System.out.println("[Mailer] Email (with PDF) sent to " + to); }
            catch (MessagingException e) { System.err.println("[Mailer] FAILED PDF: " + e.getMessage()); e.printStackTrace(); }
        }, "mailer-thread").start();
    }
}

