package com.sistema.modulos.bancos.DAO;

import com.sistema.core.DBConnection;
import com.sistema.modulos.bancos.Models.ConciliacionBancaria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones CRUD sobre la tabla conciliaciones_bancarias.
 * Módulo de Bancos — Grupo 4
 */
public class ConciliacionBancariaDAO {

    /**
     * Inserta una nueva conciliación bancaria.
     */
    public void insertar(ConciliacionBancaria c) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql = """
                INSERT INTO conciliaciones_bancarias (id_empresa, id_banco, mes, anio,
                    saldo_segun_banco, saldo_segun_libros, diferencia, estado,
                    fecha_conciliacion, observaciones)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?)
                """;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, c.getIdEmpresa());
                ps.setInt(2, c.getIdBanco());
                ps.setInt(3, c.getMes());
                ps.setInt(4, c.getAnio());
                ps.setBigDecimal(5, c.getSaldoSegunBanco());
                ps.setBigDecimal(6, c.getSaldoSegunLibros());
                ps.setBigDecimal(7, c.getDiferencia());
                ps.setString(8, c.getEstado());
                ps.setString(9, c.getObservaciones());
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
     * Obtiene el historial de conciliaciones de una cuenta.
     */
    public List<ConciliacionBancaria> obtenerPorBanco(int idBanco) throws SQLException {
        List<ConciliacionBancaria> lista = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql = """
                SELECT c.*, cb.nombre_banco
                FROM conciliaciones_bancarias c
                INNER JOIN cuentas_bancarias cb ON c.id_banco = cb.id_banco
                WHERE c.id_banco = ?
                ORDER BY c.anio DESC, c.mes DESC
                """;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idBanco);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lista.add(mapearConciliacion(rs));
                    }
                }
            }
            return lista;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    /**
     * Obtiene una conciliación específica por cuenta y período.
     */
    public ConciliacionBancaria obtenerPorPeriodo(int idBanco, int mes, int anio) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql = """
                SELECT c.*, cb.nombre_banco
                FROM conciliaciones_bancarias c
                INNER JOIN cuentas_bancarias cb ON c.id_banco = cb.id_banco
                WHERE c.id_banco = ? AND c.mes = ? AND c.anio = ?
                """;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idBanco);
                ps.setInt(2, mes);
                ps.setInt(3, anio);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapearConciliacion(rs);
                    }
                }
            }
            return null;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    /**
     * Actualiza una conciliación existente.
     */
    public void actualizar(ConciliacionBancaria c) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql = """
                UPDATE conciliaciones_bancarias SET saldo_segun_banco = ?,
                    saldo_segun_libros = ?, diferencia = ?, estado = ?,
                    fecha_conciliacion = CURRENT_TIMESTAMP, observaciones = ?
                WHERE id_conciliacion = ?
                """;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setBigDecimal(1, c.getSaldoSegunBanco());
                ps.setBigDecimal(2, c.getSaldoSegunLibros());
                ps.setBigDecimal(3, c.getDiferencia());
                ps.setString(4, c.getEstado());
                ps.setString(5, c.getObservaciones());
                ps.setInt(6, c.getIdConciliacion());
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
     * Convierte un ResultSet en un objeto ConciliacionBancaria.
     */
    private ConciliacionBancaria mapearConciliacion(ResultSet rs) throws SQLException {
        ConciliacionBancaria c = new ConciliacionBancaria();
        c.setIdConciliacion(rs.getInt("id_conciliacion"));
        c.setIdEmpresa(rs.getInt("id_empresa"));
        c.setIdBanco(rs.getInt("id_banco"));
        c.setMes(rs.getInt("mes"));
        c.setAnio(rs.getInt("anio"));
        c.setSaldoSegunBanco(rs.getBigDecimal("saldo_segun_banco"));
        c.setSaldoSegunLibros(rs.getBigDecimal("saldo_segun_libros"));
        c.setDiferencia(rs.getBigDecimal("diferencia"));
        c.setEstado(rs.getString("estado"));
        Timestamp ts = rs.getTimestamp("fecha_conciliacion");
        if (ts != null) c.setFechaConciliacion(ts.toLocalDateTime());
        c.setObservaciones(rs.getString("observaciones"));
        c.setNombreBanco(rs.getString("nombre_banco"));
        return c;
    }
}
