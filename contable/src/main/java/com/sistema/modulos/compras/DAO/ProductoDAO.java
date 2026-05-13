package com.sistema.modulos.compras.DAO;

import com.sistema.core.DBConnection;
import com.sistema.core.security.SessionManager;
import com.sistema.modulos.compras.Models.Producto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {
    
    public List<Producto> obtenerTodos() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.id_producto, p.nombre_producto, p.existencias, " +
                     "p.precio_venta, p.costo_unitario, c.nombre_categoria " +
                     "FROM productos p " +
                     "LEFT JOIN categorias c ON p.id_categoria = c.id_categoria " +
                     "WHERE p.id_empresa = ? ORDER BY p.nombre_producto";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, SessionManager.getIdEmpresa());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Producto prod = new Producto();
                prod.setIdProducto(rs.getInt("id_producto"));
                prod.setNombreProducto(rs.getString("nombre_producto"));
                prod.setExistencias(rs.getInt("existencias"));
                prod.setPrecioVenta(rs.getBigDecimal("precio_venta"));
                prod.setCostoUnitario(rs.getBigDecimal("costo_unitario"));
                prod.setNombreCategoria(rs.getString("nombre_categoria"));
                productos.add(prod);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productos;
    }
    
    public List<Producto> buscarPorNombre(String filtro) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.id_producto, p.nombre_producto, p.existencias, " +
                     "p.precio_venta, p.costo_unitario, c.nombre_categoria " +
                     "FROM productos p " +
                     "LEFT JOIN categorias c ON p.id_categoria = c.id_categoria " +
                     "WHERE p.id_empresa = ? AND p.nombre_producto ILIKE ? ORDER BY p.nombre_producto";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, SessionManager.getIdEmpresa());
            pstmt.setString(2, "%" + filtro + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Producto prod = new Producto();
                prod.setIdProducto(rs.getInt("id_producto"));
                prod.setNombreProducto(rs.getString("nombre_producto"));
                prod.setExistencias(rs.getInt("existencias"));
                prod.setPrecioVenta(rs.getBigDecimal("precio_venta"));
                prod.setCostoUnitario(rs.getBigDecimal("costo_unitario"));
                prod.setNombreCategoria(rs.getString("nombre_categoria"));
                productos.add(prod);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productos;
    }
}