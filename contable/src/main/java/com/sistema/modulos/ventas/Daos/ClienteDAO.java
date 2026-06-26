package com.sistema.modulos.ventas.Daos;

import com.sistema.core.DBConnection;
import com.sistema.core.security.SessionManager;
import com.sistema.modulos.ventas.Model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    private static final String TIPO_CLIENTE = "CLIENTE";
    private static final int ID_EMPRESA_PRUEBA = 1;

    public Integer obtenerIdEmpresaActual() {
        SessionManager session = SessionManager.getInstancia();

        if (session.haySesionActiva()) {
            return Math.toIntExact(session.getIdEmpresa());
        }

        return ID_EMPRESA_PRUEBA;
    }

    public String obtenerNombreEmpresaActual() {
        int idEmpresa = obtenerIdEmpresaActual();
        SessionManager session = SessionManager.getInstancia();

        if (session.haySesionActiva()) {
            String nombreSesion = session.getNombreEmpresa();
            if (nombreSesion != null && !nombreSesion.trim().isEmpty()) {
                return nombreSesion.trim();
            }
        }

        Connection conn = null;

        try {
            conn = DBConnection.obtenerConexion();

            String sql = """
                    SELECT nombre_empresa
                    FROM configuracion_empresa
                    WHERE id = ?
                    LIMIT 1
                    """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idEmpresa);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String nombre = rs.getString("nombre_empresa");
                        if (nombre != null && !nombre.trim().isEmpty()) {
                            return nombre.trim();
                        }
                    }
                }
            }

            return "Empresa ID: " + idEmpresa;

        } catch (SQLException e) {
            e.printStackTrace();
            return "Empresa ID: " + idEmpresa;

        } finally {
            DBConnection.cerrar(conn);
        }
    }

    public List<Cliente> listarClientes() {
        List<Cliente> clientes = new ArrayList<>();
        Connection conn = null;

        int idEmpresa = obtenerIdEmpresaActual();

        String sql = """
                SELECT id_entidad, id_empresa, tipo_entidad, nombre, nrc, nit, dui, telefono, direccion
                FROM entidades
                WHERE id_empresa = %d
                AND tipo_entidad = 'CLIENTE'
                ORDER BY nombre
                """.formatted(idEmpresa);

        try {
            conn = DBConnection.obtenerConexion();

            try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {

                while (rs.next()) {
                    clientes.add(mapearCliente(rs));
                }
            }

            return clientes;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new IllegalStateException("Error al listar clientes: " + e.getMessage(), e);

        } finally {
            DBConnection.cerrar(conn);
        }
    }

    public List<Cliente> listarClientesPorEmpresa() {
        return listarClientes();
    }

    public List<Cliente> buscarClientes(String filtro) {
        String texto = filtro == null ? "" : filtro.trim();

        if (texto.isEmpty()) {
            return listarClientes();
        }

        List<Cliente> clientes = new ArrayList<>();
        Connection conn = null;

        int idEmpresa = obtenerIdEmpresaActual();
        String patron = escaparSQL(texto);

        String sql = """
                SELECT id_entidad, id_empresa, tipo_entidad, nombre, nrc, nit, dui, telefono, direccion
                FROM entidades
                WHERE id_empresa = %d
                AND tipo_entidad = 'CLIENTE'
                AND (
                    LOWER(COALESCE(nombre, '')) LIKE LOWER('%%%s%%')
                    OR LOWER(COALESCE(nrc, '')) LIKE LOWER('%%%s%%')
                    OR LOWER(COALESCE(nit, '')) LIKE LOWER('%%%s%%')
                    OR LOWER(COALESCE(dui, '')) LIKE LOWER('%%%s%%')
                    OR LOWER(COALESCE(telefono, '')) LIKE LOWER('%%%s%%')
                    OR LOWER(COALESCE(direccion, '')) LIKE LOWER('%%%s%%')
                )
                ORDER BY nombre
                """.formatted(idEmpresa, patron, patron, patron, patron, patron, patron);

        try {
            conn = DBConnection.obtenerConexion();

            try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {

                while (rs.next()) {
                    clientes.add(mapearCliente(rs));
                }
            }

            return clientes;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new IllegalStateException("Error al buscar clientes: " + e.getMessage(), e);

        } finally {
            DBConnection.cerrar(conn);
        }
    }

    public boolean insertarCliente(Cliente cliente) {
        Connection conn = null;

        String sql = """
                INSERT INTO entidades
                (id_empresa, tipo_entidad, nombre, nrc, nit, dui, telefono, direccion)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try {
            conn = DBConnection.obtenerConexion();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, obtenerIdEmpresaActual());
                ps.setString(2, TIPO_CLIENTE);
                ps.setString(3, limpiarVacio(cliente.getNombre()));
                ps.setString(4, limpiarVacio(cliente.getNrc()));
                ps.setString(5, limpiarVacio(cliente.getNit()));
                ps.setString(6, limpiarVacio(cliente.getDui()));
                ps.setString(7, limpiarVacio(cliente.getTelefono()));
                ps.setString(8, limpiarVacio(cliente.getDireccion()));

                int filas = ps.executeUpdate();

                DBConnection.commit(conn);
                return filas > 0;
            }

        } catch (SQLException e) {
            rollbackSeguro(conn);
            e.printStackTrace();
            throw new IllegalStateException("Error al insertar cliente: " + e.getMessage(), e);

        } finally {
            DBConnection.cerrar(conn);
        }
    }

    public boolean actualizarCliente(Cliente cliente) {
        Connection conn = null;

        String sql = """
                UPDATE entidades
                SET nombre = ?, nrc = ?, nit = ?, dui = ?, telefono = ?, direccion = ?
                WHERE id_entidad = ?
                AND id_empresa = ?
                AND tipo_entidad = ?
                """;

        try {
            conn = DBConnection.obtenerConexion();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, limpiarVacio(cliente.getNombre()));
                ps.setString(2, limpiarVacio(cliente.getNrc()));
                ps.setString(3, limpiarVacio(cliente.getNit()));
                ps.setString(4, limpiarVacio(cliente.getDui()));
                ps.setString(5, limpiarVacio(cliente.getTelefono()));
                ps.setString(6, limpiarVacio(cliente.getDireccion()));
                ps.setInt(7, cliente.getIdEntidad());
                ps.setInt(8, obtenerIdEmpresaActual());
                ps.setString(9, TIPO_CLIENTE);

                int filas = ps.executeUpdate();

                DBConnection.commit(conn);
                return filas > 0;
            }

        } catch (SQLException e) {
            rollbackSeguro(conn);
            e.printStackTrace();
            throw new IllegalStateException("Error al actualizar cliente: " + e.getMessage(), e);

        } finally {
            DBConnection.cerrar(conn);
        }
    }

    public boolean eliminarCliente(Integer idEntidad) {
        Connection conn = null;

        String sql = """
                DELETE FROM entidades
                WHERE id_entidad = ?
                AND id_empresa = ?
                AND tipo_entidad = ?
                """;

        try {
            conn = DBConnection.obtenerConexion();

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idEntidad);
                ps.setInt(2, obtenerIdEmpresaActual());
                ps.setString(3, TIPO_CLIENTE);

                int filas = ps.executeUpdate();

                DBConnection.commit(conn);
                return filas > 0;
            }

        } catch (SQLException e) {
            rollbackSeguro(conn);
            e.printStackTrace();
            throw new IllegalStateException("Error al eliminar cliente: " + e.getMessage(), e);

        } finally {
            DBConnection.cerrar(conn);
        }
    }

    private Cliente mapearCliente(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente();

        cliente.setIdEntidad(rs.getInt("id_entidad"));
        cliente.setIdEmpresa(rs.getInt("id_empresa"));
        cliente.setTipoEntidad(rs.getString("tipo_entidad"));
        cliente.setNombre(rs.getString("nombre"));
        cliente.setNrc(rs.getString("nrc"));
        cliente.setNit(rs.getString("nit"));
        cliente.setDui(rs.getString("dui"));
        cliente.setTelefono(rs.getString("telefono"));
        cliente.setDireccion(rs.getString("direccion"));

        return cliente;
    }

    private String limpiarVacio(String valor) {
        return valor == null || valor.trim().isEmpty() ? null : valor.trim();
    }

    private String escaparSQL(String valor) {
        return valor == null ? "" : valor.replace("'", "''");
    }

    private void rollbackSeguro(Connection conn) {
        if (conn == null) {
            return;
        }

        try {
            DBConnection.rollback(conn);
        } catch (SQLException ex) {
            System.err.println("Error al hacer rollback en ClienteDAO: " + ex.getMessage());
        }
    }
}
