package com.sistema.modulos.ventas.Daos;

import com.sistema.core.DBConnection;
import com.sistema.core.security.SessionManager;
import com.sistema.modulos.ventas.Model.AbonoCliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class AbonoClienteDAO {

    private static final int ID_EMPRESA_PRUEBA = 1;

    public int obtenerIdEmpresaActual() {
        SessionManager session = SessionManager.getInstancia();

        if (session != null && session.haySesionActiva()) {
            return Math.toIntExact(session.getIdEmpresa());
        }

        return ID_EMPRESA_PRUEBA;
    }

    public boolean registrarAbono(int idFactura, double montoAbono, String metodoPago, String referencia) {
        validarDatosAbono(idFactura, montoAbono, metodoPago);

        Connection conn = null;
        int idEmpresa = obtenerIdEmpresaActual();

        try {
            conn = DBConnection.obtenerConexion();

            double saldoPendienteActual = obtenerSaldoPendienteBloqueado(conn, idFactura, idEmpresa);

            if (saldoPendienteActual <= 0) {
                throw new IllegalArgumentException("La factura seleccionada no tiene saldo pendiente.");
            }

            if (montoAbono > saldoPendienteActual) {
                throw new IllegalArgumentException("El monto del abono no puede ser mayor al saldo pendiente.");
            }

            double nuevoSaldo = saldoPendienteActual - montoAbono;
            String nuevoEstado;

            if (nuevoSaldo <= 0) {
                nuevoSaldo = 0;
                nuevoEstado = "PAGADO";
            } else {
                nuevoEstado = "PARCIAL";
            }

            insertarAbono(conn, idEmpresa, idFactura, montoAbono, metodoPago.trim(), limpiarVacio(referencia));
            actualizarFactura(conn, idEmpresa, idFactura, nuevoSaldo, nuevoEstado);

            DBConnection.commit(conn);
            return true;

        } catch (SQLException | RuntimeException e) {
            rollbackSeguro(conn);
            throw new IllegalArgumentException("No se pudo registrar el abono: " + e.getMessage(), e);

        } finally {
            DBConnection.cerrar(conn);
        }
    }

    public List<AbonoCliente> listarAbonosPorFactura(int idFactura) {
        if (idFactura <= 0) {
            throw new IllegalArgumentException("El id de factura debe ser mayor que cero.");
        }

        List<AbonoCliente> abonos = new ArrayList<>();
        Connection conn = null;

        String sql = """
                SELECT id_abono, id_empresa, id_factura, fecha, monto, metodo_pago, referencia
                FROM abonos
                WHERE id_empresa = ?
                AND id_factura = ?
                ORDER BY fecha DESC
                """;

        try {
            conn = DBConnection.obtenerConexion();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, obtenerIdEmpresaActual());
                ps.setInt(2, idFactura);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        abonos.add(mapearAbono(rs));
                    }
                }
            }

            return abonos;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new IllegalStateException("Error al listar abonos de la factura: " + e.getMessage(), e);

        } finally {
            DBConnection.cerrar(conn);
        }
    }

    private void validarDatosAbono(int idFactura, double montoAbono, String metodoPago) {
        if (idFactura <= 0) {
            throw new IllegalArgumentException("El id de factura debe ser mayor que cero.");
        }

        if (montoAbono <= 0) {
            throw new IllegalArgumentException("El monto del abono debe ser mayor que cero.");
        }

        if (metodoPago == null || metodoPago.trim().isEmpty()) {
            throw new IllegalArgumentException("El metodo de pago es obligatorio.");
        }
    }

    private double obtenerSaldoPendienteBloqueado(Connection conn, int idFactura, int idEmpresa) throws SQLException {
        String sql = """
                SELECT id_factura, saldo_pendiente
                FROM facturacion
                WHERE id_factura = ?
                AND id_empresa = ?
                AND tipo_operacion = 'VENTA'
                FOR UPDATE
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idFactura);
            ps.setInt(2, idEmpresa);

            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException("No se encontro la factura de venta para la empresa actual.");
                }

                return rs.getDouble("saldo_pendiente");
            }
        }
    }

    private void insertarAbono(Connection conn, int idEmpresa, int idFactura, double montoAbono,
            String metodoPago, String referencia) throws SQLException {
        String sql = """
                INSERT INTO abonos
                (id_empresa, id_factura, fecha, monto, metodo_pago, referencia)
                VALUES (?, ?, CURRENT_TIMESTAMP, ?, ?, ?)
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEmpresa);
            ps.setInt(2, idFactura);
            ps.setDouble(3, montoAbono);
            ps.setString(4, metodoPago);
            ps.setString(5, referencia);
            ps.executeUpdate();
        }
    }

    private void actualizarFactura(Connection conn, int idEmpresa, int idFactura,
            double nuevoSaldo, String nuevoEstado) throws SQLException {
        String sql = """
                UPDATE facturacion
                SET saldo_pendiente = ?,
                    estado_pago = ?
                WHERE id_factura = ?
                AND id_empresa = ?
                AND tipo_operacion = 'VENTA'
                """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, nuevoSaldo);
            ps.setString(2, nuevoEstado);
            ps.setInt(3, idFactura);
            ps.setInt(4, idEmpresa);

            int filas = ps.executeUpdate();
            if (filas == 0) {
                throw new SQLException("No se pudo actualizar el saldo de la factura.");
            }
        }
    }

    private AbonoCliente mapearAbono(ResultSet rs) throws SQLException {
        AbonoCliente abono = new AbonoCliente();
        Timestamp fecha = rs.getTimestamp("fecha");

        abono.setIdAbono(rs.getInt("id_abono"));
        abono.setIdEmpresa(rs.getInt("id_empresa"));
        abono.setIdFactura(rs.getInt("id_factura"));
        abono.setFecha(fecha == null ? null : fecha.toLocalDateTime());
        abono.setMonto(rs.getDouble("monto"));
        abono.setMetodoPago(rs.getString("metodo_pago"));
        abono.setReferencia(rs.getString("referencia"));

        return abono;
    }

    private String limpiarVacio(String valor) {
        return valor == null || valor.trim().isEmpty() ? null : valor.trim();
    }

    private void rollbackSeguro(Connection conn) {
        if (conn == null) {
            return;
        }

        try {
            DBConnection.rollback(conn);
        } catch (SQLException ex) {
            System.err.println("Error al hacer rollback en AbonoClienteDAO: " + ex.getMessage());
        }
    }
}
