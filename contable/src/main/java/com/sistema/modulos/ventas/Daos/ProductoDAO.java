package com.sistema.modulos.ventas.Daos;

import com.sistema.core.DBConnection;
import com.sistema.modulos.ventas.Model.Productos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    // 1. TU MÉTODO ORIGINAL (Se queda intacto para llenar tus combos)
    public List<Productos> listarProductosPorCategoria(int idCategoria) {
        List<Productos> lista = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.obtenerConexion();

            String sql = """
                SELECT id_producto, id_empresa, id_categoria, nombre_producto, 
                       stock_minimo, existencias, precio_venta, metodo_costeo, costo_unitario 
                FROM productos 
                WHERE id_categoria = ?
                ORDER BY nombre_producto
            """;

            ps = conn.prepareStatement(sql);
            ps.setInt(1, idCategoria);
            rs = ps.executeQuery();

            while (rs.next()) {
                Productos prod = new Productos();
                prod.setIdProducto(rs.getInt("id_producto"));
                prod.setIdEmpresa(rs.getInt("id_empresa"));
                prod.setIdCategoria(rs.getInt("id_categoria"));
                prod.setNombreProducto(rs.getString("nombre_producto"));
                prod.setStockMinimo(rs.getInt("stock_minimo"));
                prod.setExistencias(rs.getInt("existencias"));
                prod.setPrecioVenta(rs.getDouble("precio_venta"));
                prod.setMetodoCosteo(rs.getString("metodo_costeo"));
                prod.setCostoUnitario(rs.getDouble("costo_unitario"));

                lista.add(prod);
            }

        } catch (Exception e) {
            System.out.println("Error listarProductosPorCategoria: " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
            }
            DBConnection.cerrar(conn);
        }

        return lista;
    }

    // 2. EL MÉTODO NUEVO: Busca el stock real de un solo producto por su ID
    public int obtenerExistenciasPorId(int idProducto) {
        int existencias = 0;
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.obtenerConexion();

            // Solo pedimos la columna existencias para que Supabase responda volando
            String sql = "SELECT existencias FROM productos WHERE id_producto = ?";

            ps = conn.prepareStatement(sql);
            ps.setInt(1, idProducto);
            rs = ps.executeQuery();

            if (rs.next()) {
                existencias = rs.getInt("existencias");
            }

        } catch (Exception e) {
            System.out.println("Error obtenerExistenciasPorId: " + e.getMessage());
        } finally {
            // Cerramos todo con la misma seguridad global
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
            }
            DBConnection.cerrar(conn);
        }

        return existencias; // Te devuelve el número exacto (Ej: 10, 5, 0)
    }

    //Para buscar productos en el jd buscar
    public List<Productos> listarProductosPorEmpresa(int idEmpresa) {
        List<Productos> lista = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBConnection.obtenerConexion();

            String sql = """
            SELECT id_producto, id_empresa, id_categoria, nombre_producto,
                   stock_minimo, existencias, precio_venta, metodo_costeo, costo_unitario
            FROM productos
            WHERE id_empresa = ?
            ORDER BY nombre_producto
        """;

            ps = conn.prepareStatement(sql);
            ps.setLong(1, idEmpresa);
            rs = ps.executeQuery();

            while (rs.next()) {
                Productos prod = new Productos();
                prod.setIdProducto(rs.getInt("id_producto"));
                prod.setIdEmpresa(rs.getInt("id_empresa"));
                prod.setIdCategoria(rs.getInt("id_categoria"));
                prod.setNombreProducto(rs.getString("nombre_producto"));
                prod.setStockMinimo(rs.getInt("stock_minimo"));
                prod.setExistencias(rs.getInt("existencias"));
                prod.setPrecioVenta(rs.getDouble("precio_venta"));
                prod.setMetodoCosteo(rs.getString("metodo_costeo"));
                prod.setCostoUnitario(rs.getDouble("costo_unitario"));
                lista.add(prod);
            }

        } catch (Exception e) {
            System.out.println("Error listarProductosPorEmpresa: " + e.getMessage());
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (Exception e) {
            }
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (Exception e) {
            }
            DBConnection.cerrar(conn);
        }

        return lista;
    }
}
