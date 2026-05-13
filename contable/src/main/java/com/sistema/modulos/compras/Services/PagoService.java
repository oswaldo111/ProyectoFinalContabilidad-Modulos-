package com.sistema.modulos.compras.Services;

import com.sistema.modulos.compras.DAO.CompraDAO;
import com.sistema.core.security.SessionManager;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import com.sistema.core.DBConnection;

public class PagoService {
    
    private CompraDAO compraDAO;
    
    public PagoService() {
        this.compraDAO = new CompraDAO();
    }
    
    public void validarPago(int idFactura, BigDecimal montoPagado, BigDecimal saldoActual) {
        if (idFactura <= 0) {
            throw new IllegalArgumentException("Factura no válida");
        }
        if (montoPagado == null || montoPagado.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto a pagar debe ser mayor a cero");
        }
        if (montoPagado.compareTo(saldoActual) > 0) {
            throw new IllegalArgumentException("El monto no puede ser mayor al saldo pendiente ($" + saldoActual + ")");
        }
    }
    
    public boolean registrarPago(int idFactura, BigDecimal montoPagado, BigDecimal saldoActual) throws SQLException {
        
        validarPago(idFactura, montoPagado, saldoActual);
        
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            BigDecimal nuevoSaldo = saldoActual.subtract(montoPagado);
            String nuevoEstado = nuevoSaldo.compareTo(BigDecimal.ZERO) == 0 ? "PAGADO" : "PARCIAL";
            
            boolean actualizado = compraDAO.actualizarPago(idFactura, nuevoSaldo, nuevoEstado);
            if (!actualizado) {
                throw new SQLException("No se pudo actualizar la factura");
            }
            
            String sqlBitacora = "INSERT INTO bitacora (id_empresa, id_usuario, accion, modulo) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sqlBitacora)) {
                pstmt.setInt(1, SessionManager.getIdEmpresa());
                pstmt.setInt(2, SessionManager.getIdUsuario());
                pstmt.setString(3, "Registró pago de $" + montoPagado + " para factura #" + idFactura + " - Nuevo saldo: $" + nuevoSaldo);
                pstmt.setString(4, "COMPRAS");
                pstmt.executeUpdate();
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw e;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }
}