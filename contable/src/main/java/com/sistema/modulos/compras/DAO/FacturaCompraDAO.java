package com.sistema.modulos.compras.DAO;

import com.sistema.core.DBConnection;
import com.sistema.modulos.compras.Models.FacturaCompra;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FacturaCompraDAO {

    public Long guardar(FacturaCompra factura) throws SQLException {
        String sql = """
                INSERT INTO facturacion (id_empresa, id_entidad, tipo_documento, tipo_operacion,
                                         numero_documento, fecha_emision, monto_gravado, monto_iva,
                                         monto_total, saldo_pendiente, estado_pago)
                VALUES (?, ?, ?, 'COMPRA', ?, ?, ?, ?, ?, ?, 'PENDIENTE')
                RETURNING id_factura
                """;

        try (Connection conn = DBConnection.obtenerConexion();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, factura.getIdEmpresa());
            ps.setLong(2, factura.getIdEntidad());
            ps.setString(3, factura.getTipoDocumento());
            ps.setString(4, factura.getNumeroDocumento());
            ps.setObject(5, factura.getFechaEmision());
            ps.setBigDecimal(6, factura.getMontoGravado());
            ps.setBigDecimal(7, factura.getMontoIva());
            ps.setBigDecimal(8, factura.getMontoTotal());
            ps.setBigDecimal(9, factura.getSaldoPendiente());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getLong("id_factura");
            }
        }
        return null;
    }

    public boolean actualizarEstadoPago(Long idFactura, Long idEmpresa, BigDecimal montoPagado) throws SQLException {
        String sql = """
                UPDATE facturacion
                SET saldo_pendiente = saldo_pendiente - ?,
                    estado_pago = CASE
                        WHEN (saldo_pendiente - ?) <= 0 THEN 'PAGADO'
                        WHEN (saldo_pendiente - ?) < saldo_pendiente THEN 'PARCIAL'
                        ELSE estado_pago
                    END
                WHERE id_factura = ? AND id_empresa = ? AND tipo_operacion = 'COMPRA'
                """;

        try (Connection conn = DBConnection.obtenerConexion();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBigDecimal(1, montoPagado);
            ps.setBigDecimal(2, montoPagado);
            ps.setBigDecimal(3, montoPagado);
            ps.setLong(4, idFactura);
            ps.setLong(5, idEmpresa);
            return ps.executeUpdate() > 0;
        }
    }

    public List<FacturaCompra> listarPendientesPorEmpresa(Long idEmpresa) throws SQLException {
        String sql = """
                SELECT * FROM facturacion
                WHERE id_empresa = ? AND tipo_operacion = 'COMPRA' AND estado_pago != 'PAGADO'
                ORDER BY fecha_emision ASC
                """;

        List<FacturaCompra> lista = new ArrayList<>();

        try (Connection conn = DBConnection.obtenerConexion();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idEmpresa);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearFactura(rs));
                }
            }
        }
        return lista;
    }

    public FacturaCompra obtenerPorId(Long idFactura, Long idEmpresa) throws SQLException {
        String sql = """
                SELECT * FROM facturacion
                WHERE id_factura = ? AND id_empresa = ? AND tipo_operacion = 'COMPRA'
                """;

        try (Connection conn = DBConnection.obtenerConexion();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, idFactura);
            ps.setLong(2, idEmpresa);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapearFactura(rs) : null;
            }
        }
    }

    public boolean existeNumeroDocumento(String numero, Long idEmpresa) throws SQLException {
        String sql = """
                SELECT 1 FROM facturacion
                WHERE numero_documento = ? AND id_empresa = ? AND tipo_operacion = 'COMPRA'
                """;

        try (Connection conn = DBConnection.obtenerConexion();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, numero);
            ps.setLong(2, idEmpresa);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }



    public List<FacturaCompra> listarPendientesConNombre(Long idEmpresa) throws SQLException {
        Connection conn = null;
        List<FacturaCompra> lista = new ArrayList<>();

        try {
            conn = DBConnection.obtenerConexion();

            String sql = """
                    SELECT f.*, e.nombre as nombre_proveedor
                    FROM facturacion f
                    INNER JOIN entidades e ON f.id_entidad = e.id_entidad
                    WHERE f.id_empresa = ? AND f.tipo_operacion = 'COMPRA' AND f.estado_pago != 'PAGADO'
                    ORDER BY f.fecha_emision ASC
                    """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, idEmpresa);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        FacturaCompra f = mapearFactura(rs);
                        f.setNombreProveedor(rs.getString("nombre_proveedor"));
                        lista.add(f);
                    }
                }
            }
            return lista;

        } finally {
            DBConnection.cerrar(conn);
        }
    }

    private FacturaCompra mapearFactura(ResultSet rs) throws SQLException {
        FacturaCompra f = new FacturaCompra();
        f.setIdFactura(rs.getLong("id_factura"));
        f.setIdEmpresa(rs.getLong("id_empresa"));
        f.setIdEntidad(rs.getLong("id_entidad"));
        f.setTipoDocumento(rs.getString("tipo_documento"));
        f.setTipoOperacion(rs.getString("tipo_operacion"));
        f.setNumeroDocumento(rs.getString("numero_documento"));
        f.setFechaEmision(rs.getObject("fecha_emision", LocalDate.class));
        f.setMontoGravado(rs.getBigDecimal("monto_gravado"));
        f.setMontoIva(rs.getBigDecimal("monto_iva"));
        f.setMontoTotal(rs.getBigDecimal("monto_total"));
        f.setSaldoPendiente(rs.getBigDecimal("saldo_pendiente"));
        f.setEstadoPago(rs.getString("estado_pago"));
        return f;
    }
}