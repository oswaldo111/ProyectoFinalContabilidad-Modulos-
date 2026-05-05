package com.sistema.modulos.compras.Services;

import com.sistema.core.security.SessionManager;
import com.sistema.modulos.compras.DAO.ProveedorDAO;
import com.sistema.modulos.compras.Models.Proveedor;

import java.util.List;
import java.util.regex.Pattern;

public class ProveedorService {
    
    private final ProveedorDAO proveedorDAO;
    private static final Pattern NIT_PATTERN = Pattern.compile("^\\d{4,}-?\\d{1,3}$");
    
    public ProveedorService() {
        this.proveedorDAO = new ProveedorDAO();
    }

    public Long registrarProveedor(Proveedor proveedor) throws Exception {
        validarSesionActiva();

        Long idEmpresa = SessionManager.getInstancia().getIdEmpresa();
        proveedor.setIdEmpresa(idEmpresa);

        validarDatosProveedor(proveedor);

        if (proveedorDAO.existeNit(proveedor.getNit(), idEmpresa)) {
            throw new Exception("Ya existe un proveedor con el NIT: " + proveedor.getNit());
        }

        try {
            Long idGenerado = proveedorDAO.guardar(proveedor);

            if (idGenerado == null) {
                throw new Exception("No se pudo registrar el proveedor");
            }

            return idGenerado;

        } catch (Exception e) {
            throw new Exception("Error al registrar proveedor: " + e.getMessage());
        }
    }
    
    public boolean actualizarProveedor(Proveedor proveedor) throws Exception {
        validarSesionActiva();
        
        if (proveedor.getIdEntidad() == null) {
            throw new Exception("El ID del proveedor es requerido");
        }

        Long idEmpresa = SessionManager.getInstancia().getIdEmpresa();
        proveedor.setIdEmpresa(idEmpresa);
        
        validarDatosProveedor(proveedor);
        
        Proveedor existente = proveedorDAO.obtenerPorId(proveedor.getIdEntidad(), idEmpresa);
        if (existente != null && !existente.getNit().equals(proveedor.getNit())) {
            if (proveedorDAO.existeNit(proveedor.getNit(), idEmpresa)) {
                throw new Exception("Ya existe otro proveedor con el NIT: " + proveedor.getNit());
            }
        }
        
        try {
            boolean actualizado = proveedorDAO.actualizar(proveedor);
            if (!actualizado) {
                throw new Exception("No se encontró el proveedor para actualizar");
            }
            return true;
        } catch (Exception e) {
            throw new Exception("Error al actualizar proveedor: " + e.getMessage());
        }
    }
    
    public boolean eliminarProveedor(Long idProveedor) throws Exception {
        validarSesionActiva();
        Long idEmpresa = SessionManager.getInstancia().getIdEmpresa();
        
        try {
            boolean eliminado = proveedorDAO.eliminar(idProveedor, idEmpresa);
            if (!eliminado) {
                throw new Exception("No se encontró el proveedor para eliminar");
            }
            return true;
        } catch (Exception e) {
            throw new Exception("Error al eliminar proveedor: " + e.getMessage());
        }
    }
    
    public Proveedor obtenerProveedor(Long idProveedor) throws Exception {
        validarSesionActiva();
        Long idEmpresa = SessionManager.getInstancia().getIdEmpresa();
        
        try {
            return proveedorDAO.obtenerPorId(idProveedor, idEmpresa);
        } catch (Exception e) {
            throw new Exception("Error al consultar proveedor: " + e.getMessage());
        }
    }
    
    public List<Proveedor> listarProveedores() throws Exception {
        validarSesionActiva();
        Long idEmpresa = SessionManager.getInstancia().getIdEmpresa();
        
        try {
            return proveedorDAO.listarPorEmpresa(idEmpresa);
        } catch (Exception e) {
            throw new Exception("Error al listar proveedores: " + e.getMessage());
        }
    }
    
    private void validarDatosProveedor(Proveedor proveedor) throws Exception {
        if (proveedor.getNombre() == null || proveedor.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre o razón social es obligatorio");
        }
        
        if (proveedor.getNit() == null || proveedor.getNit().trim().isEmpty()) {
            throw new Exception("El NIT es obligatorio");
        }
        
        if (!NIT_PATTERN.matcher(proveedor.getNit().trim()).matches()) {
            throw new Exception("Formato de NIT inválido. Ejemplo: 1234-5 o 123456789-0");
        }
        
        if (proveedor.getNrc() != null && proveedor.getNrc().trim().isEmpty()) {
            proveedor.setNrc(null);
        }
        
        if (proveedor.getIdEmpresa() == null) {
            throw new Exception("Error interno: El ID de empresa no está asignado");
        }
    }
    private void validarSesionActiva() throws Exception {
        if (!SessionManager.getInstancia().haySesionActiva()) {
            throw new Exception("No hay una sesión activa. Por favor, inicie sesión primero.");
        }
    }
}