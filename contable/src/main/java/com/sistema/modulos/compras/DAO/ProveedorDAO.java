package com.sistema.modulos.compras.DAO;

import com.sistema.core.DBConnection;
import com.sistema.core.security.SessionManager;
import com.sistema.modulos.compras.Models.Proveedor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProveedorDAO {
    
    public List<Proveedor> obtenerTodos() {
        List<Proveedor> proveedores = new ArrayList<>();
        String sql = "SELECT id_entidad, nombre, nrc, nit, dui, telefono, direccion " +
                     "FROM entidades WHERE id_empresa = ? AND tipo_entidad = 'PROVEEDOR' ORDER BY nombre";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, SessionManager.getIdEmpresa());
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Proveedor p = new Proveedor();
                p.setIdEntidad(rs.getInt("id_entidad"));
                p.setNombre(rs.getString("nombre"));
                p.setNrc(rs.getString("nrc"));
                p.setNit(rs.getString("nit"));
                p.setDui(rs.getString("dui"));
                p.setTelefono(rs.getString("telefono"));
                p.setDireccion(rs.getString("direccion"));
                proveedores.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) DBConnection.closeConnection(conn); } catch (Exception e) { e.printStackTrace(); }
        }
        return proveedores;
    }
    
    public List<Proveedor> buscar(String filtro) {
        List<Proveedor> proveedores = new ArrayList<>();
        String sql = "SELECT id_entidad, nombre, nrc, nit, dui, telefono, direccion " +
                     "FROM entidades WHERE id_empresa = ? AND tipo_entidad = 'PROVEEDOR' " +
                     "AND (nombre ILIKE ? OR nit ILIKE ?) ORDER BY nombre";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, SessionManager.getIdEmpresa());
            pstmt.setString(2, "%" + filtro + "%");
            pstmt.setString(3, "%" + filtro + "%");
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Proveedor p = new Proveedor();
                p.setIdEntidad(rs.getInt("id_entidad"));
                p.setNombre(rs.getString("nombre"));
                p.setNrc(rs.getString("nrc"));
                p.setNit(rs.getString("nit"));
                p.setDui(rs.getString("dui"));
                p.setTelefono(rs.getString("telefono"));
                p.setDireccion(rs.getString("direccion"));
                proveedores.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) DBConnection.closeConnection(conn); } catch (Exception e) { e.printStackTrace(); }
        }
        return proveedores;
    }
    
    public Proveedor obtenerPorId(int id) {
        String sql = "SELECT id_entidad, nombre, nrc, nit, dui, telefono, direccion " +
                     "FROM entidades WHERE id_entidad = ? AND tipo_entidad = 'PROVEEDOR'";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Proveedor p = new Proveedor();
                p.setIdEntidad(rs.getInt("id_entidad"));
                p.setNombre(rs.getString("nombre"));
                p.setNrc(rs.getString("nrc"));
                p.setNit(rs.getString("nit"));
                p.setDui(rs.getString("dui"));
                p.setTelefono(rs.getString("telefono"));
                p.setDireccion(rs.getString("direccion"));
                return p;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) DBConnection.closeConnection(conn); } catch (Exception e) { e.printStackTrace(); }
        }
        return null;
    }
    
    public boolean nitExiste(String nit, int idExcluir) {
        String sql = "SELECT COUNT(*) FROM entidades WHERE id_empresa = ? AND tipo_entidad = 'PROVEEDOR' AND nit = ? AND id_entidad != ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, SessionManager.getIdEmpresa());
            pstmt.setString(2, nit);
            pstmt.setInt(3, idExcluir);
            rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) DBConnection.closeConnection(conn); } catch (Exception e) { e.printStackTrace(); }
        }
        return false;
    }
    
    public boolean insertar(Proveedor proveedor) throws SQLException {
        String sql = "INSERT INTO entidades (id_empresa, tipo_entidad, nombre, nrc, nit, dui, telefono, direccion) " +
                     "VALUES (?, 'PROVEEDOR', ?, ?, ?, ?, ?, ?) RETURNING id_entidad";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, SessionManager.getIdEmpresa());
            pstmt.setString(2, proveedor.getNombre());
            pstmt.setString(3, proveedor.getNrc());
            pstmt.setString(4, proveedor.getNit());
            pstmt.setString(5, proveedor.getDui());
            pstmt.setString(6, proveedor.getTelefono());
            pstmt.setString(7, proveedor.getDireccion());
            
            rs = pstmt.executeQuery();
            if (rs.next()) {
                proveedor.setIdEntidad(rs.getInt(1));
                DBConnection.commit(conn);
                return true;
            }
            DBConnection.commit(conn);
            return false;
        } catch (SQLException e) {
            DBConnection.rollback(conn);
            throw e;
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) DBConnection.closeConnection(conn); } catch (Exception e) { e.printStackTrace(); }
        }
    }
    
    public boolean actualizar(Proveedor proveedor) throws SQLException {
        String sql = "UPDATE entidades SET nombre = ?, nrc = ?, nit = ?, dui = ?, telefono = ?, direccion = ? " +
                     "WHERE id_entidad = ? AND id_empresa = ?";
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, proveedor.getNombre());
            pstmt.setString(2, proveedor.getNrc());
            pstmt.setString(3, proveedor.getNit());
            pstmt.setString(4, proveedor.getDui());
            pstmt.setString(5, proveedor.getTelefono());
            pstmt.setString(6, proveedor.getDireccion());
            pstmt.setInt(7, proveedor.getIdEntidad());
            pstmt.setInt(8, SessionManager.getIdEmpresa());
            
            boolean exito = pstmt.executeUpdate() > 0;
            DBConnection.commit(conn);
            return exito;
        } catch (SQLException e) {
            DBConnection.rollback(conn);
            throw e;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) DBConnection.closeConnection(conn); } catch (Exception e) { e.printStackTrace(); }
        }
    }
    
    public boolean eliminar(int id) throws SQLException {
        String sqlCheck = "SELECT COUNT(*) FROM facturacion WHERE id_entidad = ? AND tipo_operacion = 'COMPRA'";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sqlCheck);
            pstmt.setInt(1, id);
            rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                throw new SQLException("No se puede eliminar el proveedor porque tiene compras registradas");
            }
            DBConnection.commit(conn);
        } catch (SQLException e) {
            DBConnection.rollback(conn);
            throw e;
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) DBConnection.closeConnection(conn); } catch (Exception e) { e.printStackTrace(); }
        }
        
        String sql = "DELETE FROM entidades WHERE id_entidad = ? AND id_empresa = ? AND tipo_entidad = 'PROVEEDOR'";
        
        try {
            conn = DBConnection.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.setInt(2, SessionManager.getIdEmpresa());
            boolean exito = pstmt.executeUpdate() > 0;
            DBConnection.commit(conn);
            return exito;
        } catch (SQLException e) {
            DBConnection.rollback(conn);
            throw e;
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (conn != null) DBConnection.closeConnection(conn); } catch (Exception e) { e.printStackTrace(); }
        }
    }
}