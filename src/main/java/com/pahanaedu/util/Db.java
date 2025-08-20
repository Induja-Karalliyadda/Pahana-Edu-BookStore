package com.pahanaedu.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Thread-safe Singleton for JDBC configuration/driver. */
public final class Db {
    // keep your DB config in one place
    private static final String URL =
        "jdbc:mysql://localhost:3306/pahana_edu_book_store" +
        "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "1234";

    private Db() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // load once
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("MySQL driver not found", e);
        }
    }

    // Initialization-on-Demand Holder (lazy + thread-safe)
    private static class Holder {
        private static final Db INSTANCE = new Db();
    }

    public static Db get() {
        return Holder.INSTANCE;
    }

    /** Always returns a NEW connection; caller must close (try-with-resources). */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    public boolean test() {
        try (Connection c = getConnection()) {
            return c != null && !c.isClosed();
        } catch (SQLException e) { return false; }
    }
}
