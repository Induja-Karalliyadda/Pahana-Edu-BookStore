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
        + "    Welcome to <strong>Pahana Edu Bookshop</strong>, your trusted destination for books and stationery in Colombo City!<br/>"
        + "    Your account has been successfully created. You can now enjoy a seamless shopping experience with us."
        + "  </p>"

        + "  <div style='background:#f7f5ff;border:1px solid #e6e1ff;padding:14px 16px;border-radius:8px;'>"
        + "    <p style='margin:0 0 4px;color:#333;'>Here are your login credentials:</p>"
        + "    <p style='margin:4px 0;font-size:15px;'>"
        + "      🔐 <strong>Customer Code:</strong> <span style='font-family:monospace'>" + esc(code) + "</span><br/>"
        + "      🔑 <strong>Temporary Password:</strong> <span style='font-family:monospace'>" + esc(tempPassword) + "</span>"
        + "    </p>"
        + "  </div>"

        + "  <p style='font-size:14px;color:#333;'>"
        + "    Please log in at <a href='https://www.pahanaedu.lk/login' style='color:#5a38c6;text-decoration:none;'>www.pahanaedu.lk/login</a>."
        + "    For security reasons, change your password after first login."
        + "  </p>"

        + "  <p style='font-size:14px;color:#333;'>"
        + "    Need help? Email <a href='mailto:support@pahanaedu.lk' style='color:#5a38c6;text-decoration:none;'>support@pahanaedu.lk</a>"
        + "    or call <strong>011-2030400</strong>."
        + "  </p>"

        + "  <p style='font-size:14px;color:#333;'>Thank you for choosing Pahana Edu!</p>"
        + "  <p style='font-size:14px;color:#333;margin-top:18px;'>Warm regards,<br/><strong>The Pahana Edu Team</strong><br/>"
        + "     <a href='https://www.pahanaedu.lk' style='color:#5a38c6;text-decoration:none;'>www.pahanaedu.lk</a></p>"

        + "  <hr style='border:none;border-top:1px dashed #ccc;margin:16px 0'/>"
        + "  <p style='font-size:12px;color:#999;margin:0;'>This is an automated message. Please do not reply.</p>"
        + "</div>";
    }

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
    }
}
