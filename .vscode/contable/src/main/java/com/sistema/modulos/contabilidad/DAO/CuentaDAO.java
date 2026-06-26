package com.sistema.modulos.contabilidad.DAO;

import com.sistema.core.DBConnection;
import com.sistema.modulos.contabilidad.Models.Cuenta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operaciones CRUD sobre la tabla cuentas.
 * Módulo de Contabilidad — Catálogo de Cuentas
 */
public class CuentaDAO {

    /**
     * Obtiene todas las cuentas de una empresa ordenadas por código.
     */
    public List<Cuenta> obtenerTodas(int idEmpresa) throws SQLException {
        List<Cuenta> lista = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql = """
                SELECT c.*, p.codigo_cuenta AS codigo_padre, p.nombre_cuenta AS nombre_padre
                FROM cuentas c
                LEFT JOIN cuentas p ON c.id_cuenta_padre = p.id_cuenta
                WHERE c.id_empresa = ?
                ORDER BY c.codigo_cuenta
                """;
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
     * Obtiene una cuenta por su ID.
     */
    public Cuenta obtenerPorId(int idCuenta) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql = """
                SELECT c.*, p.codigo_cuenta AS codigo_padre, p.nombre_cuenta AS nombre_padre
                FROM cuentas c
                LEFT JOIN cuentas p ON c.id_cuenta_padre = p.id_cuenta
                WHERE c.id_cuenta = ?
                """;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idCuenta);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return mapearCuenta(rs);
                }
            }
            return null;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    /**
     * Obtiene solo las cuentas de detalle (sin hijos) para uso en partidas.
     */
    public List<Cuenta> obtenerCuentasDetalle(int idEmpresa) throws SQLException {
        List<Cuenta> lista = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql = """
                SELECT c.*
                FROM cuentas c
                WHERE c.id_empresa = ?
                  AND c.id_cuenta NOT IN (SELECT DISTINCT id_cuenta_padre FROM cuentas WHERE id_cuenta_padre IS NOT NULL)
                ORDER BY c.codigo_cuenta
                """;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idEmpresa);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) lista.add(mapearCuenta(rs));
                }
            }
            return lista;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    /**
     * Inserta una nueva cuenta contable.
     */
    public void insertar(Cuenta c) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql = """
                INSERT INTO cuentas (id_empresa, codigo_cuenta, nombre_cuenta, tipo_cuenta, id_cuenta_padre)
                VALUES (?, ?, ?, ?, ?)
                """;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, c.getIdEmpresa());
                ps.setString(2, c.getCodigoCuenta());
                ps.setString(3, c.getNombreCuenta());
                ps.setString(4, c.getTipoCuenta());
                if (c.getCuentaPadre() != null) {
                    ps.setInt(5, c.getCuentaPadre().getIdCuenta());
                } else {
                    ps.setNull(5, Types.INTEGER);
                }
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
     * Actualiza una cuenta contable existente.
     */
    public void actualizar(Cuenta c) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql = """
                UPDATE cuentas SET codigo_cuenta = ?, nombre_cuenta = ?, tipo_cuenta = ?, id_cuenta_padre = ?
                WHERE id_cuenta = ?
                """;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, c.getCodigoCuenta());
                ps.setString(2, c.getNombreCuenta());
                ps.setString(3, c.getTipoCuenta());
                if (c.getCuentaPadre() != null) {
                    ps.setInt(4, c.getCuentaPadre().getIdCuenta());
                } else {
                    ps.setNull(4, Types.INTEGER);
                }
                ps.setInt(5, c.getIdCuenta());
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
     * Elimina una cuenta por su ID.
     */
    public void eliminar(int idCuenta) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "DELETE FROM cuentas WHERE id_cuenta = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idCuenta);
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
     * Verifica si una cuenta tiene subcuentas hijas.
     */
    public boolean tieneSubcuentas(int idCuenta) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT COUNT(*) AS total FROM cuentas WHERE id_cuenta_padre = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idCuenta);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("total") > 0;
                }
            }
            return false;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    /**
     * Verifica si un código de cuenta ya existe en la empresa.
     */
    public boolean existeCodigo(String codigo, int idEmpresa) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT COUNT(*) AS total FROM cuentas WHERE codigo_cuenta = ? AND id_empresa = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, codigo);
                ps.setInt(2, idEmpresa);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt("total") > 0;
                }
            }
            return false;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }

    /**
     * Convierte un ResultSet en un objeto Cuenta.
     */
    private Cuenta mapearCuenta(ResultSet rs) throws SQLException {
        Cuenta c = new Cuenta();
        c.setIdCuenta(rs.getInt("id_cuenta"));
        c.setIdEmpresa(rs.getInt("id_empresa"));
        c.setCodigoCuenta(rs.getString("codigo_cuenta"));
        c.setNombreCuenta(rs.getString("nombre_cuenta"));
        c.setTipoCuenta(rs.getString("tipo_cuenta"));

        int idPadre = rs.getInt("id_cuenta_padre");
        if (!rs.wasNull()) {
            Cuenta padre = new Cuenta();
            padre.setIdCuenta(idPadre);
            // Intentamos leer código y nombre del padre si el JOIN los trajo
            try {
                String codigoPadre = rs.getString("codigo_padre");
                String nombrePadre = rs.getString("nombre_padre");
                if (codigoPadre != null) padre.setCodigoCuenta(codigoPadre);
                if (nombrePadre != null) padre.setNombreCuenta(nombrePadre);
            } catch (SQLException ignored) {}
            c.setCuentaPadre(padre);
        }
        return c;
    }
}