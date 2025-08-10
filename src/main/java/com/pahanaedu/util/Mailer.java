package com.pahanaedu.util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class Mailer {

    // ====== SMTP SETTINGS (Gmail example) ======
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final int    SMTP_PORT = 587;             // TLS
    private static final String SMTP_USER = "devinduja@gmail.com";
    private static final String SMTP_PASS = "heda dcrr yefd lssx"; // App Password
    private static final boolean DEBUG = true;               // set true to see logs in Tomcat console

    private static Session buildSession() {
        Properties p = new Properties();
        p.put("mail.smtp.auth", "true");
        p.put("mail.smtp.starttls.enable", "true");
        p.put("mail.smtp.starttls.required", "true");
        p.put("mail.smtp.host", SMTP_HOST);
        p.put("mail.smtp.port", String.valueOf(SMTP_PORT));

        // Robustness & modern TLS
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

    /** Synchronous send (throws MessagingException on failure). */
    public static void sendHtml(String to, String subject, String html) throws MessagingException {
        Session session = buildSession();

        MimeMessage msg = new MimeMessage(session);
        try {
            msg.setFrom(new InternetAddress(SMTP_USER, "Pahana Edu Bookshop"));
        } catch (UnsupportedEncodingException e) {
            msg.setFrom(new InternetAddress(SMTP_USER));
        }
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
        msg.setSubject(subject, "UTF-8");
        msg.setContent(html, "text/html; charset=UTF-8");

        // You can use Transport.send(msg); this explicit connect gives clearer errors
        try (Transport t = session.getTransport("smtp")) {
            t.connect(SMTP_HOST, SMTP_PORT, SMTP_USER, SMTP_PASS);
            t.sendMessage(msg, msg.getAllRecipients());
        }
    }

    /** Fire-and-forget send; logs exceptions but doesn't block the HTTP request. */
    public static void sendHtmlAsync(String to, String subject, String html) {
        new Thread(() -> {
            try {
                sendHtml(to, subject, html);
                if (DEBUG) System.out.println("[Mailer] Email sent to " + to);
            } catch (MessagingException e) {
                System.err.println("[Mailer] FAILED to send mail to " + to + ": " + e.getMessage());
                e.printStackTrace();
            }
        }, "mailer-thread").start();
    }
}

