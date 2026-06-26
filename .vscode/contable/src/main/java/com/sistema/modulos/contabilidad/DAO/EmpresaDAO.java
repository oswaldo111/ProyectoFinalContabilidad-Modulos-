package com.sistema.modulos.contabilidad.DAO;

import com.sistema.core.DBConnection;
import com.sistema.modulos.contabilidad.Models.Empresa;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmpresaDAO {

    public List<Empresa> obtenerTodas() throws SQLException {
        List<Empresa> lista = new ArrayList<>();
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            String sql = "SELECT id, nombre_empresa FROM configuracion_empresa ORDER BY nombre_empresa";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Empresa(
                        rs.getInt("id"),
                        rs.getString("nombre_empresa")
                    ));
                }
            }
            return lista;
        } finally {
            DBConnection.closeConnection(conn);
        }
    }
}