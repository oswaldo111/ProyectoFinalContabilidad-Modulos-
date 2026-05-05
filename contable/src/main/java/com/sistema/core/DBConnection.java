package com.sistema.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DBConnection {
    // bd proporcionada oswaldo
    private static final String URL = "jdbc:postgresql://aws-1-us-west-2.pooler.supabase.com:6543/postgres";
    private static final String USER = "postgres.qdgyxauteocampukljjo";
    private static final String PASSWORD = "isU0hqUbGHmr55xb";
    // private static final String URL = "jdbc:postgresql://localhost:5432/sistema_contable";
    // private static final String USER = "postgres";
    // private static final String PASSWORD = "12345";
    private DBConnection() {}
   
    public static Connection obtenerConexion() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            conn.setAutoCommit(false);
            return conn;
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver PostgreSQL no encontrado", e);
        }
    }

    public static void commit(Connection conn) throws SQLException {
        if (conn != null && !conn.isClosed() && !conn.getAutoCommit()) {
            conn.commit();
        }
    }

    public static void rollback(Connection conn) throws SQLException {
        if (conn != null && !conn.isClosed() && !conn.getAutoCommit()) {
            conn.rollback();
        }
    }
    
  
    public static void cerrar(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println(" Error al cerrar conexión: " + e.getMessage());
            }
        }
    }
}