package com.sistema.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // bd proporcionada oswaldo
    // solo se modifico los nombres en español a ingles obtener conexion, commit, rollback y cerrar conexion
    private static final String URL = "jdbc:postgresql://aws-1-us-west-2.pooler.supabase.com:6543/postgres";
    private static final String USER = "postgres.acywsjxomurvklledhrb";
    private static final String PASSWORD = "isU0hqUbGHmr55xb";

    private static Connection cachedConnection = null;

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            if (cachedConnection == null || cachedConnection.isClosed()) {
                cachedConnection = DriverManager.getConnection(URL, USER, PASSWORD);
                cachedConnection.setAutoCommit(false);
                System.out.println("Conexión exitosa a la base de datos");
                System.out.println("URL: " + URL + " | Usuario: " + USER);
            }
            return cachedConnection;
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

    public static void closeConnection(Connection conn) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                if (conn == cachedConnection) {
                    cachedConnection = null;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al cerrar conexión: " + e.getMessage());
        }
    }

    // Métodos de compatibilidad
    public static Connection obtenerConexion() throws SQLException {
        return getConnection();
    }

    public static void cerrar(Connection conn) {
        closeConnection(conn);
    }
}
