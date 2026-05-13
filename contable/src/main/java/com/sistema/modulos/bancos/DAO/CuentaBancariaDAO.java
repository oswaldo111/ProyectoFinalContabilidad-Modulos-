package com.sistema.modulos.bancos.DAO;

import com.sistema.core.DBConnection;
import com.sistema.modulos.bancos.Models.CuentaBancaria;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones CRUD sobre la tabla cuentas_bancarias.
 * Módulo de Bancos — Grupo 4
 */
public class CuentaBancariaDAO {

    /**
     * Obtiene todas las cuentas bancarias de una empresa.
     */
    public List<CuentaBancaria> obtenerTodas(int idEmpresa) throws SQLException {
        List<CuentaBancaria> lista = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM cuentas_bancarias WHERE id_empresa = ? ORDER BY nombre_banco";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idEmpresa);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lista.add(mapearCuenta(rs));
                    }
                }
            }
            return lista;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    /**
     * Obtiene solo las cuentas activas (para ComboBoxes y operaciones).
     */
    public List<CuentaBancaria> obtenerActivas(int idEmpresa) throws SQLException {
        List<CuentaBancaria> lista = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM cuentas_bancarias WHERE id_empresa = ? AND estado = true ORDER BY nombre_banco";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idEmpresa);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lista.add(mapearCuenta(rs));
                    }
                }
            }
            return lista;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    /**
     * Obtiene una cuenta bancaria por su ID.
     */
    public CuentaBancaria obtenerPorId(int idBanco) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT * FROM cuentas_bancarias WHERE id_banco = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idBanco);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapearCuenta(rs);
                    }
                }
            }
            return null;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    /**
     * Inserta una nueva cuenta bancaria.
     */
    public void insertar(CuentaBancaria c) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql = """
                INSERT INTO cuentas_bancarias (id_empresa, nombre_banco, numero_cuenta,
                    tipo_cuenta, saldo_banco, estado)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, c.getIdEmpresa());
                ps.setString(2, c.getNombreBanco());
                ps.setString(3, c.getNumeroCuenta());
                ps.setString(4, c.getTipoCuenta());
                ps.setBigDecimal(5, c.getSaldoBanco());
                ps.setBoolean(6, c.isEstado());
                ps.executeUpdate();
            }
            DBConnection.commit(conn);
        } catch (SQLException e) {
            DBConnection.rollback(conn);
            throw e;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    /**
     * Actualiza una cuenta bancaria existente.
     */
    public void actualizar(CuentaBancaria c) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql = """
                UPDATE cuentas_bancarias SET nombre_banco = ?, numero_cuenta = ?,
                    tipo_cuenta = ?, saldo_banco = ?, estado = ?
                WHERE id_banco = ?
                """;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, c.getNombreBanco());
                ps.setString(2, c.getNumeroCuenta());
                ps.setString(3, c.getTipoCuenta());
                ps.setBigDecimal(4, c.getSaldoBanco());
                ps.setBoolean(5, c.isEstado());
                ps.setInt(6, c.getIdBanco());
                ps.executeUpdate();
            }
            DBConnection.commit(conn);
        } catch (SQLException e) {
            DBConnection.rollback(conn);
            throw e;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    /**
     * Elimina una cuenta bancaria por su ID.
     */
    public void eliminar(int idBanco) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM cuentas_bancarias WHERE id_banco = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idBanco);
                ps.executeUpdate();
            }
            DBConnection.commit(conn);
        } catch (SQLException e) {
            DBConnection.rollback(conn);
            throw e;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    /**
     * Actualiza el saldo de una cuenta. Recibe conexión externa para usar en transacciones.
     */
    public void actualizarSaldo(int idBanco, BigDecimal nuevoSaldo, Connection conn) throws SQLException {
        String sql = "UPDATE cuentas_bancarias SET saldo_banco = ? WHERE id_banco = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, nuevoSaldo);
            ps.setInt(2, idBanco);
            ps.executeUpdate();
        }
    }

    /**
     * Obtiene el saldo total de todas las cuentas activas de una empresa.
     */
    public BigDecimal obtenerSaldoTotal(int idEmpresa) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT COALESCE(SUM(saldo_banco), 0) AS total FROM cuentas_bancarias WHERE id_empresa = ? AND estado = true";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idEmpresa);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getBigDecimal("total");
                    }
                }
            }
            return BigDecimal.ZERO;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    /**
     * Verifica si una cuenta tiene movimientos asociados.
     */
    public boolean tieneMovimientos(int idBanco) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT COUNT(*) AS total FROM movimientos_bancarios WHERE id_banco = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idBanco);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("total") > 0;
                    }
                }
            }
            return false;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    /**
     * Convierte un ResultSet en un objeto CuentaBancaria.
     */
    private CuentaBancaria mapearCuenta(ResultSet rs) throws SQLException {
        CuentaBancaria c = new CuentaBancaria();
        c.setIdBanco(rs.getInt("id_banco"));
        c.setIdEmpresa(rs.getInt("id_empresa"));
        c.setNombreBanco(rs.getString("nombre_banco"));
        c.setNumeroCuenta(rs.getString("numero_cuenta"));
        c.setTipoCuenta(rs.getString("tipo_cuenta"));
        c.setSaldoBanco(rs.getBigDecimal("saldo_banco") != null ? rs.getBigDecimal("saldo_banco") : BigDecimal.ZERO);
        c.setEstado(rs.getBoolean("estado"));
        return c;
    }
}
