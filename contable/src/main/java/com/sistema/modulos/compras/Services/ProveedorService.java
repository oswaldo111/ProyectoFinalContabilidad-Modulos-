package com.sistema.modulos.compras.Services;

import com.sistema.modulos.compras.DAO.ProveedorDAO;
import com.sistema.modulos.compras.Models.Proveedor;

import java.sql.SQLException;
import java.util.List;

public class ProveedorService {
    
    private ProveedorDAO proveedorDAO;
    
    public ProveedorService() {
        this.proveedorDAO = new ProveedorDAO();
    }
    
    public void validarProveedor(Proveedor proveedor) {
        String nombre = proveedor.getNombre();
        String nrc = proveedor.getNrc();
        String nit = proveedor.getNit();

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del proveedor es obligatorio");
        }
        if (nrc == null || nrc.trim().isEmpty()) {
            throw new IllegalArgumentException("El NRC es obligatorio");
        }
        if (!nrc.trim().matches("\\d{8}")) {
            throw new IllegalArgumentException("El NRC debe tener exactamente 8 d\u00EDgitos num\u00E9ricos (ej: 12345678)");
        }
        if (nit == null || nit.trim().isEmpty()) {
            throw new IllegalArgumentException("El NIT del proveedor es obligatorio");
        }
        if (!nit.trim().matches("\\d{4}-\\d{6}-\\d{3}-\\d{1}")) {
            throw new IllegalArgumentException("El NIT debe tener el formato 0000-000000-000-0 (14 d\u00EDgitos)");
        }
    }
    
    public List<Proveedor> obtenerTodos() {
        return proveedorDAO.obtenerTodos();
    }
    
    public List<Proveedor> buscar(String filtro) {
        if (filtro == null || filtro.trim().isEmpty()) {
            return obtenerTodos();
        }
        return proveedorDAO.buscar(filtro);
    }
    
    public Proveedor obtenerPorId(int id) {
        return proveedorDAO.obtenerPorId(id);
    }
    
    public boolean registrarProveedor(Proveedor proveedor) throws SQLException {
        validarProveedor(proveedor);
        
        if (proveedorDAO.nitExiste(proveedor.getNit(), 0)) {
            throw new IllegalArgumentException("Ya existe un proveedor con el NIT: " + proveedor.getNit());
        }
        
        return proveedorDAO.insertar(proveedor);
    }
    
    public boolean actualizarProveedor(Proveedor proveedor) throws SQLException {
        validarProveedor(proveedor);
        
        if (proveedorDAO.nitExiste(proveedor.getNit(), proveedor.getIdEntidad())) {
            throw new IllegalArgumentException("Ya existe otro proveedor con el NIT: " + proveedor.getNit());
        }
        
        return proveedorDAO.actualizar(proveedor);
    }
    
    public boolean eliminarProveedor(int id) throws SQLException {
        return proveedorDAO.eliminar(id);
    }
}