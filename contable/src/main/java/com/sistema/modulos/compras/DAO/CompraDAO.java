package com.sistema.modulos.compras.DAO;

import com.sistema.core.DBConnection;
import com.sistema.core.security.SessionManager;
import com.sistema.modulos.compras.Models.Compra;
import com.sistema.modulos.compras.Models.DetalleCompra;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
//test prueba
public class CompraDAO {
    
    public List<Compra> obtenerTodas() {
        List<Compra> compras = new ArrayList<>();
        String sql = "SELECT f.id_factura, f.id_entidad, e.nombre as proveedor, " +
                     "f.tipo_documento, f.numero_documento, f.fecha_emision, f.fecha_vencimiento, " +
                     "f.monto_total, f.saldo_pendiente, f.estado_pago " +
                     "FROM facturacion f " +
                     "JOIN entidades e ON f.id_entidad = e.id_entidad " +
                     "WHERE f.id_empresa = ? AND f.tipo_operacion = 'COMPRA' " +
                     "ORDER BY f.fecha_emision DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, SessionManager.getIdEmpresa());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Compra c = new Compra();
                c.setIdFactura(rs.getInt("id_factura"));
                c.setIdEntidad(rs.getInt("id_entidad"));
                c.setNombreProveedor(rs.getString("proveedor"));
                c.setTipoDocumento(rs.getString("tipo_documento"));
                c.setNumeroDocumento(rs.getString("numero_documento"));
                c.setFechaEmision(rs.getTimestamp("fecha_emision"));
                c.setFechaVencimiento(rs.getDate("fecha_vencimiento"));
                c.setMontoTotal(rs.getBigDecimal("monto_total"));
                c.setSaldoPendiente(rs.getBigDecimal("saldo_pendiente"));
                c.setEstadoPago(rs.getString("estado_pago"));
                compras.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return compras;
    }
    
    public List<Compra> buscarCompras(String filtroProveedor, String estadoPago, String fechaDesde, String fechaHasta, Integer diasVencimiento) {
        List<Compra> compras = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT f.id_factura, f.id_entidad, e.nombre as proveedor, " +
            "f.tipo_documento, f.numero_documento, f.fecha_emision, f.fecha_vencimiento, " +
            "f.monto_total, f.saldo_pendiente, f.estado_pago " +
            "FROM facturacion f " +
            "JOIN entidades e ON f.id_entidad = e.id_entidad " +
            "WHERE f.id_empresa = ? AND f.tipo_operacion = 'COMPRA' "
        );
        
        List<Object> params = new ArrayList<>();
        params.add(SessionManager.getIdEmpresa());
        
        if (filtroProveedor != null && !filtroProveedor.isEmpty()) {
            sql.append("AND e.nombre ILIKE ? ");
            params.add("%" + filtroProveedor + "%");
        }
        if (estadoPago != null && !estadoPago.isEmpty() && !estadoPago.equals("TODOS")) {
            sql.append("AND f.estado_pago = ? ");
            params.add(estadoPago);
        }
        if (fechaDesde != null && !fechaDesde.isEmpty()) {
            sql.append("AND f.fecha_emision >= ? ");
            params.add(Date.valueOf(fechaDesde));
        }
        if (fechaHasta != null && !fechaHasta.isEmpty()) {
            sql.append("AND f.fecha_emision <= ? ");
            params.add(Date.valueOf(fechaHasta));
        }
        if (diasVencimiento != null && diasVencimiento > 0) {
            sql.append("AND f.fecha_vencimiento <= CURRENT_DATE + ? ");
            sql.append("AND f.fecha_vencimiento >= CURRENT_DATE ");
            sql.append("AND f.estado_pago != 'PAGADO' ");
            params.add(diasVencimiento);
        }
        
        sql.append("ORDER BY f.fecha_vencimiento ASC, f.fecha_emision DESC");
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Compra c = new Compra();
                c.setIdFactura(rs.getInt("id_factura"));
                c.setIdEntidad(rs.getInt("id_entidad"));
                c.setNombreProveedor(rs.getString("proveedor"));
                c.setTipoDocumento(rs.getString("tipo_documento"));
                c.setNumeroDocumento(rs.getString("numero_documento"));
                c.setFechaEmision(rs.getTimestamp("fecha_emision"));
                c.setFechaVencimiento(rs.getDate("fecha_vencimiento"));
                c.setMontoTotal(rs.getBigDecimal("monto_total"));
                c.setSaldoPendiente(rs.getBigDecimal("saldo_pendiente"));
                c.setEstadoPago(rs.getString("estado_pago"));
                compras.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return compras;
    }
    
    public List<DetalleCompra> obtenerDetalles(int idFactura) {
        List<DetalleCompra> detalles = new ArrayList<>();
        String sql = "SELECT fd.id_detalle, fd.id_producto, p.nombre_producto, " +
                     "fd.cantidad, fd.precio_unitario, fd.subtotal " +
                     "FROM facturacion_detalle fd " +
                     "JOIN productos p ON fd.id_producto = p.id_producto " +
                     "WHERE fd.id_factura = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idFactura);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                DetalleCompra d = new DetalleCompra();
                d.setIdDetalle(rs.getInt("id_detalle"));
                d.setIdProducto(rs.getInt("id_producto"));
                d.setNombreProducto(rs.getString("nombre_producto"));
                d.setCantidad(rs.getInt("cantidad"));
                d.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
                d.setSubtotal(rs.getBigDecimal("subtotal"));
                detalles.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return detalles;
    }
    
    public int registrarCompra(int idProveedor, String tipoDocumento, String numeroDocumento,
                               Date fechaVencimiento, BigDecimal montoGravado, BigDecimal montoIva, 
                               BigDecimal montoExento, BigDecimal montoTotal, List<DetalleCompra> detalles) throws SQLException {
        
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            
            String sqlFactura = "INSERT INTO facturacion " +
                "(id_empresa, id_entidad, tipo_operacion, tipo_documento, numero_documento, " +
                "fecha_emision, fecha_vencimiento, monto_gravado, monto_iva, monto_exento, monto_total, saldo_pendiente, estado_pago) " +
                "VALUES (?, ?, 'COMPRA', ?, ?, CURRENT_TIMESTAMP, ?, ?, ?, ?, ?, ?, 'PENDIENTE') RETURNING id_factura";
            
            int idFactura;
            try (PreparedStatement pstmt = conn.prepareStatement(sqlFactura)) {
                pstmt.setInt(1, SessionManager.getIdEmpresa());
                pstmt.setInt(2, idProveedor);
                pstmt.setString(3, tipoDocumento);
                pstmt.setString(4, numeroDocumento);
                pstmt.setDate(5, fechaVencimiento);
                pstmt.setBigDecimal(6, montoGravado);
                pstmt.setBigDecimal(7, montoIva);
                pstmt.setBigDecimal(8, montoExento);
                pstmt.setBigDecimal(9, montoTotal);
                pstmt.setBigDecimal(10, montoTotal);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    idFactura = rs.getInt(1);
                } else {
                    throw new SQLException("No se pudo obtener el ID de la factura");
                }
            }
            
            String sqlDetalle = "INSERT INTO facturacion_detalle " +
                "(id_factura, id_producto, cantidad, precio_unitario, subtotal) " +
                "VALUES (?, ?, ?, ?, ?)";
            
            String sqlMovimientoInv = "INSERT INTO movimientos_inventario " +
                "(id_empresa, id_producto, tipo_movimiento, cantidad, costo_unitario, id_factura_referencia) " +
                "VALUES (?, ?, 'ENTRADA', ?, ?, ?)";
            
            String sqlActualizarStock = "UPDATE productos SET existencias = existencias + ? WHERE id_producto = ?";
            
            for (DetalleCompra detalle : detalles) {
                try (PreparedStatement pstmt = conn.prepareStatement(sqlDetalle)) {
                    pstmt.setInt(1, idFactura);
                    pstmt.setInt(2, detalle.getIdProducto());
                    pstmt.setInt(3, detalle.getCantidad());
                    pstmt.setBigDecimal(4, detalle.getPrecioUnitario());
                    pstmt.setBigDecimal(5, detalle.getSubtotal());
                    pstmt.executeUpdate();
                }
                
                try (PreparedStatement pstmt = conn.prepareStatement(sqlMovimientoInv)) {
                    pstmt.setInt(1, SessionManager.getIdEmpresa());
                    pstmt.setInt(2, detalle.getIdProducto());
                    pstmt.setInt(3, detalle.getCantidad());
                    pstmt.setBigDecimal(4, detalle.getPrecioUnitario());
                    pstmt.setInt(5, idFactura);
                    pstmt.executeUpdate();
                }
                
                try (PreparedStatement pstmt = conn.prepareStatement(sqlActualizarStock)) {
                    pstmt.setInt(1, detalle.getCantidad());
                    pstmt.setInt(2, detalle.getIdProducto());
                    pstmt.executeUpdate();
                }
            }
            
            String sqlBitacora = "INSERT INTO bitacora (id_empresa, id_usuario, accion, modulo) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlBitacora)) {
                pstmt.setInt(1, SessionManager.getIdEmpresa());
                pstmt.setInt(2, SessionManager.getIdUsuario());
                pstmt.setString(3, "Registró compra: " + numeroDocumento + " - Total: $" + montoTotal);
                pstmt.setString(4, "COMPRAS");
                pstmt.executeUpdate();
            }
            
            conn.commit();
            return idFactura;
            
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw e;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }
    
    public boolean actualizarPago(int idFactura, BigDecimal nuevoSaldo, String nuevoEstado) throws SQLException {
        String sql = "UPDATE facturacion SET saldo_pendiente = ?, estado_pago = ? WHERE id_factura = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBigDecimal(1, nuevoSaldo);
            pstmt.setString(2, nuevoEstado);
            pstmt.setInt(3, idFactura);
            return pstmt.executeUpdate() > 0;
        }
    }
}