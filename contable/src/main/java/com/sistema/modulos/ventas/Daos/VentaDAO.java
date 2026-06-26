package com.sistema.modulos.ventas.Daos;

import com.sistema.core.DBConnection;
import com.sistema.core.security.SessionManager;
import com.sistema.modulos.ventas.Model.DetalleVenta;
import com.sistema.modulos.ventas.Model.Venta;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class VentaDAO {

    private static final int ID_EMPRESA_PRUEBA = 1;

    public int obtenerIdEmpresaActual() {
        try {
            if (SessionManager.haySesionActiva()) {
                return SessionManager.getIdEmpresa();
            }

        } catch (Exception e) {
            System.out.println("No hay sesion activa. Usando empresa de prueba ID 1.");
        }

        return ID_EMPRESA_PRUEBA;
    }

    public String obtenerNombreEmpresaActual() {

        int idEmpresa = obtenerIdEmpresaActual();
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
                        return rs.getString("nombre_empresa");
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.cerrar(conn);
        }

        return "Empresa ID: " + idEmpresa;
    }

    public String generarNumeroDocumento(String tipoDocumento) {

        Connection conn = null;

        try {
            conn = DBConnection.obtenerConexion();

            int idEmpresa = obtenerIdEmpresaActual();

            String prefijo;

            if (tipoDocumento != null && tipoDocumento.toLowerCase().contains("credito")) {
                prefijo = "CCF";
            } else {
                prefijo = "FC";
            }
            String sql = """
                SELECT numero_documento
                FROM facturacion
                WHERE id_empresa = ?
                AND tipo_documento = ?
                ORDER BY id_factura DESC
                LIMIT 1
            """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, idEmpresa);
                ps.setString(2, prefijo);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {

                        String ultimoNumero = rs.getString("numero_documento");

                        if (ultimoNumero != null && ultimoNumero.contains("-")) {

                            String[] partes = ultimoNumero.split("-");
                            int correlativo = Integer.parseInt(partes[1]);
                            correlativo++;

                            return String.format("%s-%06d", prefijo, correlativo);
                        }
                    }
                }
            }

            return String.format("%s-%06d", prefijo, 1);

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        } finally {
            DBConnection.cerrar(conn);
        }
    }

    public int procesarVenta(Venta venta, java.util.List<DetalleVenta> detalles) {

        Connection conn = null;

        try {
            conn = DBConnection.obtenerConexion();

            if (detalles == null || detalles.isEmpty()) {
                throw new SQLException("No se puede procesar una venta sin detalles.");
            }

            int idEmpresaActual = obtenerIdEmpresaActual();

            String sqlVenta = """
                INSERT INTO facturacion
                (
                    id_empresa,
                    id_entidad,
                    tipo_operacion,
                    tipo_documento,
                    numero_documento,
                    fecha_emision,
                    fecha_vencimiento,
                    monto_gravado,
                    monto_iva,
                    monto_exento,
                    monto_total,
                    saldo_pendiente,
                    estado_pago
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

            int idFacturaGenerada;

            try (PreparedStatement ps = conn.prepareStatement(sqlVenta, Statement.RETURN_GENERATED_KEYS)) {

                ps.setInt(1, idEmpresaActual);
                ps.setInt(2, venta.getIdEntidad());
                ps.setString(3, "VENTA");
                ps.setString(4, venta.getTipoDocumento());
                ps.setString(5, venta.getNumeroDocumento());
                ps.setDate(6, Date.valueOf(venta.getFechaEmision()));

                if (venta.getFechaVencimiento() != null) {
                    ps.setDate(7, Date.valueOf(venta.getFechaVencimiento()));
                } else {
                    ps.setNull(7, java.sql.Types.DATE);
                }

                ps.setDouble(8, venta.getMontoGravado());
                ps.setDouble(9, venta.getMontoIVA());
                ps.setDouble(10, venta.getMontoExento());
                ps.setDouble(11, venta.getMontoTotal());
                ps.setDouble(12, venta.getSaldoPendiente());
                ps.setString(13, venta.getEstadoPago());

                int filasInsertadas = ps.executeUpdate();

                if (filasInsertadas == 0) {
                    throw new SQLException("No se insertó el encabezado de la venta.");
                }

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        idFacturaGenerada = rs.getInt(1);
                    } else {
                        throw new SQLException("No se obtuvo el id_factura generado.");
                    }
                }
            }

            String sqlDetalle = """
                INSERT INTO facturacion_detalle
                (
                    id_factura,
                    id_producto,
                    cantidad,
                    precio_unitario,
                    subtotal
                )
                VALUES (?, ?, ?, ?, ?)
            """;

            try (PreparedStatement psDetalle = conn.prepareStatement(sqlDetalle)) {

                for (DetalleVenta detalle : detalles) {

                    psDetalle.setInt(1, idFacturaGenerada);
                    psDetalle.setInt(2, detalle.getIdProducto());
                    psDetalle.setInt(3, detalle.getCantidad());
                    psDetalle.setDouble(4, detalle.getPrecioUnitario());
                    psDetalle.setDouble(5, detalle.getSubtotal());

                    psDetalle.addBatch();
                }

                psDetalle.executeBatch();
            }

            descontarExistencias(conn, idEmpresaActual, detalles);

            DBConnection.commit(conn);

            return idFacturaGenerada;

        } catch (Exception e) {

            try {
                if (conn != null) {
                    DBConnection.rollback(conn);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }

            e.printStackTrace();

            javax.swing.JOptionPane.showMessageDialog(
                    null,
                    "Error al procesar venta:\n" + e.getMessage(),
                    "Error DAO",
                    javax.swing.JOptionPane.ERROR_MESSAGE
            );

            return -1;

        } finally {
            DBConnection.cerrar(conn);
        }
    }

    private void descontarExistencias(Connection conn, int idEmpresa, java.util.List<DetalleVenta> detalles) throws SQLException {

        String sql = """
        UPDATE productos
        SET existencias = existencias - ?
        WHERE id_producto = ?
        AND id_empresa = ?
        AND existencias >= ?
    """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            for (DetalleVenta detalle : detalles) {

                ps.setInt(1, detalle.getCantidad());
                ps.setInt(2, detalle.getIdProducto());
                ps.setInt(3, idEmpresa);
                ps.setInt(4, detalle.getCantidad());

                int filas = ps.executeUpdate();

                if (filas == 0) {
                    throw new SQLException(
                            "No hay existencias suficientes para el producto ID: "
                            + detalle.getIdProducto()
                    );
                }
            }
        }
    }
}
