package com.sistema.modulos.ventas.Daos;

import com.sistema.core.DBConnection;
import com.sistema.core.security.SessionManager;
import com.sistema.modulos.ventas.Model.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDAO {

    public List<Categoria> listarCategorias() {
        List<Categoria> lista = new ArrayList<>();
        Connection conn = null;

        try {
            conn = DBConnection.obtenerConexion();

            String sql = """
                SELECT id_categoria, id_empresa, nombre_categoria
                FROM categorias
                WHERE id_empresa = ?
                ORDER BY nombre_categoria
            """;

            PreparedStatement ps = conn.prepareStatement(sql);

            // CONTROL DE MODO DE PRUEBA BASADO EN TU SESSIONMANAGER
            Long idEmpresa = 1L; 
            if (SessionManager.getInstancia().haySesionActiva()) {
               idEmpresa = (long) SessionManager.getInstancia().getIdEmpresa();
            }

            ps.setLong(1, idEmpresa);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setIdCategoria(rs.getInt("id_categoria"));
                categoria.setIdEmpresa(rs.getInt("id_empresa"));
                categoria.setCategoria(rs.getString("nombre_categoria"));

                lista.add(categoria);
            }

        } catch (Exception e) {
            System.out.println("Error listarCategorias: " + e.getMessage());
        } finally {
            // Indispensable con setAutoCommit(false) para liberar el pooler de Supabase
            DBConnection.cerrar(conn);
        }

        return lista;
    }
}