package com.sistema.modulos.ventas.Daos;

import com.sistema.core.DBConnection;
import com.sistema.core.security.SessionManager;
import com.sistema.modulos.ventas.Model.LibroVenta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LibroVentasDAO {

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

    public List<LibroVenta> listarVentas(LocalDate desde, LocalDate hasta, String tipoLibro) {
        List<LibroVenta> ventas = new ArrayList<>();
        List<Object> parametros = new ArrayList<>();
        Connection conn = null;

        StringBuilder sql = new StringBuilder("""
                SELECT
                    f.id_factura,
                    f.id_empresa,
                    f.id_entidad,
                    COALESCE(e.nombre, 'SIN CLIENTE') AS nombre_cliente,
                    e.nit,
                    e.nrc,
                    f.tipo_documento,
                    f.numero_documento,
                    f.fecha_emision,
                    f.monto_gravado,
                    f.monto_iva,
                    f.monto_exento,
                    f.monto_total,
                    f.estado_pago
                FROM facturacion f
                LEFT JOIN entidades e
                    ON e.id_entidad = f.id_entidad
                    AND e.id_empresa = f.id_empresa
                WHERE f.id_empresa = ?
                AND f.tipo_operacion = 'VENTA'
                """);

        parametros.add(obtenerIdEmpresaActual());

        if (desde != null) {
            sql.append("AND DATE(f.fecha_emision) >= ?\n");
            parametros.add(java.sql.Date.valueOf(desde));
        }

        if (hasta != null) {
            sql.append("AND DATE(f.fecha_emision) <= ?\n");
            parametros.add(java.sql.Date.valueOf(hasta));
        }

        String tipo = tipoLibro == null ? "TODOS" : tipoLibro.trim().toUpperCase();
        if ("CONSUMIDOR FINAL".equals(tipo)) {
            sql.append("AND f.tipo_documento = ?\n");
            parametros.add("FC");
        } else if ("CREDITO FISCAL".equals(tipo)) {
            sql.append("AND f.tipo_documento = ?\n");
            parametros.add("CCF");
        }

        sql.append("ORDER BY f.fecha_emision ASC, f.numero_documento ASC");

        try {
            conn = DBConnection.obtenerConexion();

            try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
                asignarParametros(ps, parametros);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        ventas.add(mapearLibroVenta(rs));
                    }
                }
            }

            return ventas;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new IllegalStateException("Error al listar libro de ventas: " + e.getMessage(), e);

        } finally {
            DBConnection.cerrar(conn);
        }
    }

    private void asignarParametros(PreparedStatement ps, List<Object> parametros) throws SQLException {
        for (int i = 0; i < parametros.size(); i++) {
            ps.setObject(i + 1, parametros.get(i));
        }
    }

    private LibroVenta mapearLibroVenta(ResultSet rs) throws SQLException {
        LibroVenta venta = new LibroVenta();

        venta.setIdFactura(rs.getInt("id_factura"));
        venta.setIdEmpresa(rs.getInt("id_empresa"));
        venta.setIdEntidad(rs.getInt("id_entidad"));
        venta.setNombreCliente(rs.getString("nombre_cliente"));
        venta.setNit(rs.getString("nit"));
        venta.setNrc(rs.getString("nrc"));
        venta.setTipoDocumento(rs.getString("tipo_documento"));
        venta.setNumeroDocumento(rs.getString("numero_documento"));
        venta.setFechaEmision(convertirFecha(rs, "fecha_emision"));
        venta.setMontoGravado(rs.getDouble("monto_gravado"));
        venta.setMontoIva(rs.getDouble("monto_iva"));
        venta.setMontoExento(rs.getDouble("monto_exento"));
        venta.setMontoTotal(rs.getDouble("monto_total"));
        venta.setEstadoPago(rs.getString("estado_pago"));

        return venta;
    }

    private LocalDate convertirFecha(ResultSet rs, String columna) throws SQLException {
        Timestamp fecha = rs.getTimestamp(columna);
        return fecha == null ? null : fecha.toLocalDateTime().toLocalDate();
    }
}
