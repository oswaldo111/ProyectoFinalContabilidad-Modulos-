package com.sistema.modulos.compras.DAO;

import com.sistema.core.DBConnection;
import com.sistema.core.security.SessionManager;
import com.sistema.modulos.compras.Models.ReporteCompraIVA;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReporteCompraIVADAO {
    
    public List<ReporteCompraIVA> obtenerLibroCompras(Date fechaInicio, Date fechaFin) {
        List<ReporteCompraIVA> reportes = new ArrayList<>();
        String sql = "SELECT f.tipo_documento, f.numero_documento, f.fecha_emision, f.fecha_vencimiento, " +
                     "e.nombre as proveedor, e.nit as nit_proveedor, e.nrc as nrc_proveedor, " +
                     "f.monto_gravado, f.monto_iva, f.monto_total, f.estado_pago " +
                     "FROM facturacion f " +
                     "JOIN entidades e ON f.id_entidad = e.id_entidad " +
                     "WHERE f.id_empresa = ? AND f.tipo_operacion = 'COMPRA' " +
                     "AND f.fecha_emision BETWEEN ? AND ? " +
                     "ORDER BY f.fecha_emision DESC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, SessionManager.getIdEmpresa());
            pstmt.setDate(2, fechaInicio);
            pstmt.setDate(3, fechaFin);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                ReporteCompraIVA r = new ReporteCompraIVA();
                r.setTipoDocumento(rs.getString("tipo_documento"));
                r.setNumeroDocumento(rs.getString("numero_documento"));
                r.setFechaEmision(rs.getDate("fecha_emision"));
                r.setFechaVencimiento(rs.getDate("fecha_vencimiento"));
                r.setProveedor(rs.getString("proveedor"));
                r.setNitProveedor(rs.getString("nit_proveedor"));
                r.setNrcProveedor(rs.getString("nrc_proveedor"));
                r.setMontoGravado(rs.getBigDecimal("monto_gravado"));
                r.setMontoIva(rs.getBigDecimal("monto_iva"));
                r.setMontoTotal(rs.getBigDecimal("monto_total"));
                r.setEstadoPago(rs.getString("estado_pago"));
                reportes.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reportes;
    }
}