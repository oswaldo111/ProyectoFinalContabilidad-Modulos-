package com.sistema.modulos.bancos.DAO;

import com.sistema.core.DBConnection;
import com.sistema.modulos.bancos.Models.MovimientoBancario;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO para operaciones CRUD sobre la tabla movimientos_bancarios.
 * Al insertar un movimiento, actualiza automáticamente el saldo de la cuenta.
 * Módulo de Bancos — Grupo 4
 */
public class MovimientoBancarioDAO {

    private final CuentaBancariaDAO cuentaDAO = new CuentaBancariaDAO();

    /**
     * Obtiene movimientos de una cuenta específica filtrados por período.
     */
    public List<MovimientoBancario> obtenerPorCuenta(int idBanco, int mes, int anio) throws SQLException {
        List<MovimientoBancario> lista = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql = """
                SELECT m.*, cb.nombre_banco
                FROM movimientos_bancarios m
                INNER JOIN cuentas_bancarias cb ON m.id_banco = cb.id_banco
                WHERE m.id_banco = ?
                  AND EXTRACT(MONTH FROM m.fecha) = ?
                  AND EXTRACT(YEAR FROM m.fecha) = ?
                ORDER BY m.fecha, m.id_movimiento
                """;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idBanco);
                ps.setInt(2, mes);
                ps.setInt(3, anio);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lista.add(mapearMovimiento(rs));
                    }
                }
            }
            return lista;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    /**
     * Obtiene todos los movimientos de una empresa filtrados por período.
     */
    public List<MovimientoBancario> obtenerTodos(int idEmpresa, int mes, int anio) throws SQLException {
        List<MovimientoBancario> lista = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql = """
                SELECT m.*, cb.nombre_banco
                FROM movimientos_bancarios m
                INNER JOIN cuentas_bancarias cb ON m.id_banco = cb.id_banco
                WHERE m.id_empresa = ?
                  AND EXTRACT(MONTH FROM m.fecha) = ?
                  AND EXTRACT(YEAR FROM m.fecha) = ?
                ORDER BY m.fecha, m.id_movimiento
                """;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idEmpresa);
                ps.setInt(2, mes);
                ps.setInt(3, anio);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lista.add(mapearMovimiento(rs));
                    }
                }
            }
            return lista;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    /**
     * Obtiene los movimientos no conciliados de una cuenta.
     */
    public List<MovimientoBancario> obtenerNoConciliados(int idBanco) throws SQLException {
        List<MovimientoBancario> lista = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql = """
                SELECT m.*, cb.nombre_banco
                FROM movimientos_bancarios m
                INNER JOIN cuentas_bancarias cb ON m.id_banco = cb.id_banco
                WHERE m.id_banco = ? AND m.conciliado = false
                ORDER BY m.fecha
                """;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idBanco);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lista.add(mapearMovimiento(rs));
                    }
                }
            }
            return lista;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    /**
     * Inserta un movimiento y actualiza el saldo de la cuenta en una transacción.
     * INGRESO → saldo += monto
     * EGRESO, CHEQUE, TRANSFERENCIA → saldo -= monto
     */
    public void insertar(MovimientoBancario m) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();

            // 1. Insertar el movimiento
            String sql = """
                INSERT INTO movimientos_bancarios (id_empresa, id_banco, tipo_movimiento,
                    monto, fecha, descripcion, conciliado, numero_cheque, beneficiario,
                    numero_referencia)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, m.getIdEmpresa());
                ps.setInt(2, m.getIdBanco());
                ps.setString(3, m.getTipoMovimiento());
                ps.setBigDecimal(4, m.getMonto());
                ps.setTimestamp(5, m.getFecha() != null ?
                        Timestamp.valueOf(m.getFecha()) : new Timestamp(System.currentTimeMillis()));
                ps.setString(6, m.getDescripcion());
                ps.setBoolean(7, m.isConciliado());
                ps.setString(8, m.getNumeroCheque());
                ps.setString(9, m.getBeneficiario());
                ps.setString(10, m.getNumeroReferencia());
                ps.executeUpdate();
            }

            // 2. Obtener saldo actual
            BigDecimal saldoActual = BigDecimal.ZERO;
            String sqlSaldo = "SELECT saldo_banco FROM cuentas_bancarias WHERE id_banco = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlSaldo)) {
                ps.setInt(1, m.getIdBanco());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        saldoActual = rs.getBigDecimal("saldo_banco");
                        if (saldoActual == null) saldoActual = BigDecimal.ZERO;
                    }
                }
            }

            // 3. Calcular nuevo saldo
            BigDecimal nuevoSaldo;
            if ("INGRESO".equals(m.getTipoMovimiento())) {
                nuevoSaldo = saldoActual.add(m.getMonto());
            } else {
                nuevoSaldo = saldoActual.subtract(m.getMonto());
            }

            // 4. Actualizar saldo en la cuenta
            cuentaDAO.actualizarSaldo(m.getIdBanco(), nuevoSaldo, conn);

            // 5. Commit de la transacción completa
            DBConnection.commit(conn);

        } catch (SQLException e) {
            DBConnection.rollback(conn);
            throw e;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    /**
     * Marca un movimiento como conciliado o no conciliado.
     */
    public void marcarConciliado(int idMovimiento, boolean conciliado) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql;
            if (conciliado) {
                sql = "UPDATE movimientos_bancarios SET conciliado = true, fecha_conciliacion = CURRENT_TIMESTAMP WHERE id_movimiento = ?";
            } else {
                sql = "UPDATE movimientos_bancarios SET conciliado = false, fecha_conciliacion = NULL WHERE id_movimiento = ?";
            }
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idMovimiento);
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
     * Marca múltiples movimientos como conciliados en lote.
     */
    public void marcarConciliadosEnLote(List<Integer> ids) throws SQLException {
        if (ids == null || ids.isEmpty()) return;
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "UPDATE movimientos_bancarios SET conciliado = true, fecha_conciliacion = CURRENT_TIMESTAMP WHERE id_movimiento = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (int id : ids) {
                    ps.setInt(1, id);
                    ps.addBatch();
                }
                ps.executeBatch();
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
     * Obtiene totales de movimientos por período y cuenta.
     * Retorna: "totalIngresos", "totalEgresos", "saldoNeto"
     */
    public Map<String, BigDecimal> obtenerTotalesPorPeriodo(int idBanco, int mes, int anio) throws SQLException {
        Map<String, BigDecimal> totales = new HashMap<>();
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql = """
                SELECT
                    COALESCE(SUM(CASE WHEN tipo_movimiento = 'INGRESO' THEN monto ELSE 0 END), 0) AS total_ingresos,
                    COALESCE(SUM(CASE WHEN tipo_movimiento IN ('EGRESO','CHEQUE','TRANSFERENCIA') THEN monto ELSE 0 END), 0) AS total_egresos
                FROM movimientos_bancarios
                WHERE id_banco = ?
                  AND EXTRACT(MONTH FROM fecha) = ?
                  AND EXTRACT(YEAR FROM fecha) = ?
                """;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idBanco);
                ps.setInt(2, mes);
                ps.setInt(3, anio);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        BigDecimal ingresos = rs.getBigDecimal("total_ingresos");
                        BigDecimal egresos = rs.getBigDecimal("total_egresos");
                        totales.put("totalIngresos", ingresos);
                        totales.put("totalEgresos", egresos);
                        totales.put("saldoNeto", ingresos.subtract(egresos));
                    }
                }
            }
            return totales;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    /**
     * Convierte un ResultSet en un objeto MovimientoBancario.
     */
    private MovimientoBancario mapearMovimiento(ResultSet rs) throws SQLException {
        MovimientoBancario m = new MovimientoBancario();
        m.setIdMovimiento(rs.getInt("id_movimiento"));
        m.setIdEmpresa(rs.getInt("id_empresa"));
        m.setIdBanco(rs.getInt("id_banco"));
        m.setTipoMovimiento(rs.getString("tipo_movimiento"));
        m.setMonto(rs.getBigDecimal("monto"));
        Timestamp ts = rs.getTimestamp("fecha");
        if (ts != null) m.setFecha(ts.toLocalDateTime());
        m.setDescripcion(rs.getString("descripcion"));
        m.setConciliado(rs.getBoolean("conciliado"));
        m.setNumeroCheque(rs.getString("numero_cheque"));
        m.setBeneficiario(rs.getString("beneficiario"));
        m.setNumeroReferencia(rs.getString("numero_referencia"));
        Timestamp tsc = rs.getTimestamp("fecha_conciliacion");
        if (tsc != null) m.setFechaConciliacion(tsc.toLocalDateTime());
        m.setNombreBanco(rs.getString("nombre_banco"));
        return m;
    }
}
