package com.sistema.modulos.ventas.Daos;

import com.sistema.core.DBConnection;
import com.sistema.core.security.SessionManager;
import com.sistema.modulos.ventas.Model.CuentaPorCobrar;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CuentaPorCobrarDAO {

    private static final int ID_EMPRESA_PRUEBA = 1;

    public int obtenerIdEmpresaActual() {
        SessionManager session = SessionManager.getInstancia();

        if (session != null && session.haySesionActiva()) {
            return Math.toIntExact(session.getIdEmpresa());
        }

        return ID_EMPRESA_PRUEBA;
    }

    public String obtenerNombreEmpresaActual() {
        int idEmpresa = obtenerIdEmpresaActual();
        SessionManager session = SessionManager.getInstancia();

        if (session != null && session.haySesionActiva()) {
            String nombreSesion = session.getNombreEmpresa();
            if (nombreSesion != null && !nombreSesion.trim().isEmpty()) {
                return nombreSesion.trim();
            }
        }

        Connection conn = null;

        try {
            conn = DBConnection.obtenerConexion();

            String sql = """
                    SELECT nombre_empresa
                    FROM configuracion_empresa
                    WHERE id = ?
                    LIMIT 1
                    """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idEmpresa);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String nombre = rs.getString("nombre_empresa");
                        if (nombre != null && !nombre.trim().isEmpty()) {
                            return nombre.trim();
                        }
                    }
                }
            }

            return "Empresa ID: " + idEmpresa;

        } catch (SQLException e) {
            e.printStackTrace();
            return "Empresa ID: " + idEmpresa;

        } finally {
            DBConnection.cerrar(conn);
        }
    }

    public List<CuentaPorCobrar> listarCuentasPendientes() {
        List<CuentaPorCobrar> cuentas = new ArrayList<>();
        Connection conn = null;

        String sql = """
                SELECT
                    f.id_factura,
                    f.id_empresa,
                    f.id_entidad,
                    e.nombre AS nombre_cliente,
                    e.nit,
                    e.nrc,
                    f.tipo_documento,
                    f.numero_documento,
                    f.fecha_emision,
                    f.fecha_vencimiento,
                    f.monto_total,
                    f.saldo_pendiente,
                    f.estado_pago,
                    CASE
                        WHEN f.fecha_vencimiento IS NOT NULL
                        AND f.fecha_vencimiento < CURRENT_DATE
                        THEN CURRENT_DATE - f.fecha_vencimiento
                        ELSE 0
                    END AS dias_vencidos
                FROM facturacion f
                INNER JOIN entidades e ON e.id_entidad = f.id_entidad
                WHERE f.id_empresa = ?
                AND e.id_empresa = f.id_empresa
                AND f.tipo_operacion = 'VENTA'
                AND f.saldo_pendiente > 0
                AND f.estado_pago IN ('PENDIENTE', 'PARCIAL')
                ORDER BY f.fecha_vencimiento ASC, f.fecha_emision ASC
                """;

        try {
            conn = DBConnection.obtenerConexion();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, obtenerIdEmpresaActual());

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        cuentas.add(mapearCuenta(rs));
                    }
                }
            }

            return cuentas;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new IllegalStateException("Error al listar cuentas por cobrar: " + e.getMessage(), e);

        } finally {
            DBConnection.cerrar(conn);
        }
    }

    public List<CuentaPorCobrar> buscarCuentas(String filtro, String estado, LocalDate desde, LocalDate hasta) {
        List<CuentaPorCobrar> cuentas = new ArrayList<>();
        List<Object> parametros = new ArrayList<>();
        Connection conn = null;

        StringBuilder sql = new StringBuilder("""
                SELECT
                    f.id_factura,
                    f.id_empresa,
                    f.id_entidad,
                    e.nombre AS nombre_cliente,
                    e.nit,
                    e.nrc,
                    f.tipo_documento,
                    f.numero_documento,
                    f.fecha_emision,
                    f.fecha_vencimiento,
                    f.monto_total,
                    f.saldo_pendiente,
                    f.estado_pago,
                    CASE
                        WHEN f.fecha_vencimiento IS NOT NULL
                        AND f.fecha_vencimiento < CURRENT_DATE
                        THEN CURRENT_DATE - f.fecha_vencimiento
                        ELSE 0
                    END AS dias_vencidos
                FROM facturacion f
                INNER JOIN entidades e ON e.id_entidad = f.id_entidad
                WHERE f.id_empresa = ?
                AND e.id_empresa = f.id_empresa
                AND f.tipo_operacion = 'VENTA'
                AND f.saldo_pendiente > 0
                """);

        parametros.add(obtenerIdEmpresaActual());

        String texto = filtro == null ? "" : filtro.trim();
        if (!texto.isEmpty()) {
            sql.append("""
                    AND (
                        LOWER(COALESCE(e.nombre, '')) LIKE LOWER(?)
                        OR LOWER(COALESCE(f.numero_documento, '')) LIKE LOWER(?)
                        OR LOWER(COALESCE(e.nit, '')) LIKE LOWER(?)
                        OR LOWER(COALESCE(e.nrc, '')) LIKE LOWER(?)
                    )
                    """);
            String patron = "%" + texto + "%";
            parametros.add(patron);
            parametros.add(patron);
            parametros.add(patron);
            parametros.add(patron);
        }

        String estadoFiltro = estado == null ? "TODOS" : estado.trim().toUpperCase();
        if ("PENDIENTE".equals(estadoFiltro) || "PARCIAL".equals(estadoFiltro)) {
            sql.append("AND f.estado_pago = ?\n");
            parametros.add(estadoFiltro);
        } else if ("VENCIDO".equals(estadoFiltro)) {
            sql.append("AND f.fecha_vencimiento IS NOT NULL AND f.fecha_vencimiento < CURRENT_DATE\n");
        }

        if (desde != null && hasta != null) {
            sql.append("AND f.fecha_emision BETWEEN ? AND ?\n");
            parametros.add(java.sql.Date.valueOf(desde));
            parametros.add(java.sql.Date.valueOf(hasta));
        } else if (desde != null) {
            sql.append("AND f.fecha_emision >= ?\n");
            parametros.add(java.sql.Date.valueOf(desde));
        } else if (hasta != null) {
            sql.append("AND f.fecha_emision <= ?\n");
            parametros.add(java.sql.Date.valueOf(hasta));
        }

        sql.append("ORDER BY f.fecha_vencimiento ASC, f.fecha_emision ASC");

        try {
            conn = DBConnection.obtenerConexion();

            try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
                asignarParametros(ps, parametros);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        cuentas.add(mapearCuenta(rs));
                    }
                }
            }

            return cuentas;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new IllegalStateException("Error al buscar cuentas por cobrar: " + e.getMessage(), e);

        } finally {
            DBConnection.cerrar(conn);
        }
    }

    private void asignarParametros(PreparedStatement ps, List<Object> parametros) throws SQLException {
        for (int i = 0; i < parametros.size(); i++) {
            ps.setObject(i + 1, parametros.get(i));
        }
    }

    private CuentaPorCobrar mapearCuenta(ResultSet rs) throws SQLException {
        CuentaPorCobrar cuenta = new CuentaPorCobrar();

        cuenta.setIdFactura(rs.getInt("id_factura"));
        cuenta.setIdEmpresa(rs.getInt("id_empresa"));
        cuenta.setIdEntidad(rs.getInt("id_entidad"));
        cuenta.setNombreCliente(rs.getString("nombre_cliente"));
        cuenta.setNit(rs.getString("nit"));
        cuenta.setNrc(rs.getString("nrc"));
        cuenta.setTipoDocumento(rs.getString("tipo_documento"));
        cuenta.setNumeroDocumento(rs.getString("numero_documento"));
        cuenta.setFechaEmision(convertirFecha(rs, "fecha_emision"));
        cuenta.setFechaVencimiento(convertirFecha(rs, "fecha_vencimiento"));
        cuenta.setMontoTotal(rs.getDouble("monto_total"));
        cuenta.setSaldoPendiente(rs.getDouble("saldo_pendiente"));
        cuenta.setEstadoPago(rs.getString("estado_pago"));
        cuenta.setDiasVencidos(rs.getInt("dias_vencidos"));

        return cuenta;
    }

    private LocalDate convertirFecha(ResultSet rs, String columna) throws SQLException {
        java.sql.Date fecha = rs.getDate(columna);
        return fecha == null ? null : fecha.toLocalDate();
    }
}
