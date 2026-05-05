package com.sistema.modulos.compras.DAO;

import com.sistema.core.DBConnection;
import com.sistema.modulos.compras.Models.Proveedor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ProveedorDAO {

    public Long guardar(Proveedor proveedor) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.obtenerConexion();
            
            String sql = """
                INSERT INTO entidades (id_empresa, tipo_entidad, nombre, nit, nrc, es_contribuyente)
                VALUES (?, 'PROVEEDOR', ?, ?, ?, ?)
                RETURNING id_entidad
                """;
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, proveedor.getIdEmpresa());
                ps.setString(2, proveedor.getNombre());
                ps.setString(3, proveedor.getNit());
                ps.setString(4, proveedor.getNrc());
                ps.setBoolean(5, proveedor.isEsContribuyente());

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        DBConnection.commit(conn);
                        return rs.getLong("id_entidad");
                    }
                }
            }

            DBConnection.rollback(conn);
            return null;

        } catch (SQLException e) {
            if (conn != null) {
                DBConnection.rollback(conn);
            }
            throw e;

        } finally {
            DBConnection.cerrar(conn);
        }
    }

 
    public boolean actualizar(Proveedor proveedor) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.obtenerConexion();
            
            String sql = """
                UPDATE entidades 
                SET nombre = ?, nit = ?, nrc = ?, es_contribuyente = ?
                WHERE id_entidad = ? AND id_empresa = ? AND tipo_entidad IN ('PROVEEDOR', 'AMBOS')
                """;
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, proveedor.getNombre());
                ps.setString(2, proveedor.getNit());
                ps.setString(3, proveedor.getNrc());
                ps.setBoolean(4, proveedor.isEsContribuyente());
                ps.setLong(5, proveedor.getIdEntidad());
                ps.setLong(6, proveedor.getIdEmpresa());

                int filasAfectadas = ps.executeUpdate();
                
                if (filasAfectadas > 0) {
                    DBConnection.commit(conn);
                    return true;
                } else {
                    DBConnection.rollback(conn);
                    return false;
                }
                
            }
        } catch (SQLException e) {
            if (conn != null) {
                DBConnection.rollback(conn);
            }
            throw e;
            
        } finally {
            DBConnection.cerrar(conn);
        }
    }


    public boolean eliminar(Long idEntidad, Long idEmpresa) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.obtenerConexion();
            
            String sql = """
                DELETE FROM entidades 
                WHERE id_entidad = ? AND id_empresa = ? AND tipo_entidad IN ('PROVEEDOR', 'AMBOS')
                """;
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, idEntidad);
                ps.setLong(2, idEmpresa);

                int filasAfectadas = ps.executeUpdate();
                
                if (filasAfectadas > 0) {
                    DBConnection.commit(conn);
                    return true;
                } else {
                    DBConnection.rollback(conn);
                    return false;
                }
                
            }
        } catch (SQLException e) {
            if (conn != null) {
                DBConnection.rollback(conn);
            }
            throw e;
            
        } finally {
            DBConnection.cerrar(conn);
        }
    }

 
    public Proveedor obtenerPorId(Long idEntidad, Long idEmpresa) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.obtenerConexion();
            
            String sql = """
                SELECT * FROM entidades 
                WHERE id_entidad = ? AND id_empresa = ? AND tipo_entidad IN ('PROVEEDOR', 'AMBOS')
                """;
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, idEntidad);
                ps.setLong(2, idEmpresa);
                
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapearProveedor(rs);
                    }
                }
            }
            return null;
            
        } finally {
            DBConnection.cerrar(conn);
        }
    }

    public List<Proveedor> listarPorEmpresa(Long idEmpresa) throws SQLException {
        Connection conn = null;
        List<Proveedor> lista = new ArrayList<>();
        
        try {
            conn = DBConnection.obtenerConexion();
            
            String sql = """
                SELECT * FROM entidades 
                WHERE id_empresa = ? AND tipo_entidad IN ('PROVEEDOR', 'AMBOS')
                ORDER BY nombre ASC
                """;
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, idEmpresa);
                
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lista.add(mapearProveedor(rs));
                    }
                }
            }
            return lista;
            
        } finally {
            DBConnection.cerrar(conn);
        }
    }

    public List<Proveedor> buscarPorTermino(String termino, Long idEmpresa) throws SQLException {
        Connection conn = null;
        List<Proveedor> lista = new ArrayList<>();
        
        try {
            conn = DBConnection.obtenerConexion();
            
            String sql = """
                SELECT * FROM entidades 
                WHERE id_empresa = ? 
                AND tipo_entidad IN ('PROVEEDOR', 'AMBOS')
                AND (nombre ILIKE ? OR nit ILIKE ?)
                ORDER BY nombre ASC
                """;
            
            String patronBusqueda = "%" + termino + "%";
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, idEmpresa);
                ps.setString(2, patronBusqueda);
                ps.setString(3, patronBusqueda);
                
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        lista.add(mapearProveedor(rs));
                    }
                }
            }
            return lista;
            
        } finally {
            DBConnection.cerrar(conn);
        }
    }

    public boolean existeNit(String nit, Long idEmpresa) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.obtenerConexion();
            
            String sql = """
                SELECT 1 FROM entidades 
                WHERE nit = ? AND id_empresa = ? AND tipo_entidad IN ('PROVEEDOR', 'AMBOS')
                """;
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, nit);
                ps.setLong(2, idEmpresa);
                
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
            
        } finally {
            DBConnection.cerrar(conn);
        }
    }

    public boolean tieneFacturasAsociadas(Long idEntidad) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.obtenerConexion();
            
            String sql = """
                SELECT 1 FROM facturacion 
                WHERE id_entidad = ? AND tipo_operacion = 'COMPRA'
                LIMIT 1
                """;
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, idEntidad);
                
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next();
                }
            }
            
        } finally {
            DBConnection.cerrar(conn);
        }
    }


    public int contarPorEmpresa(Long idEmpresa) throws SQLException {
        Connection conn = null;
        try {
            conn = DBConnection.obtenerConexion();
            
            String sql = """
                SELECT COUNT(*) as total FROM entidades 
                WHERE id_empresa = ? AND tipo_entidad IN ('PROVEEDOR', 'AMBOS')
                """;
            
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, idEmpresa);
                
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("total");
                    }
                }
            }
            return 0;
            
        } finally {
            DBConnection.cerrar(conn);
        }
    }

    private Proveedor mapearProveedor(ResultSet rs) throws SQLException {
        Proveedor p = new Proveedor();
        
        p.setIdEntidad(rs.getLong("id_entidad"));
        p.setIdEmpresa(rs.getLong("id_empresa"));
        p.setTipoEntidad(rs.getString("tipo_entidad"));
        p.setNombre(rs.getString("nombre"));
        p.setNit(rs.getString("nit"));
        p.setNrc(rs.getString("nrc"));
        p.setEsContribuyente(rs.getBoolean("es_contribuyente"));
        
        return p;
    }
}