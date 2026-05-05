package com.sistema.modulos.compras.Controllers;

import com.sistema.modulos.compras.Models.Proveedor;
import com.sistema.modulos.compras.Services.ProveedorService;
import com.sistema.modulos.compras.Views.ProveedorView;

import java.util.List;

public class ProveedorController {
    
    private final ProveedorView view;
    private final ProveedorService service;
    
    public ProveedorController(ProveedorView view) {
        this.view = view;
        this.service = new ProveedorService();
    }
    
    public void guardarProveedor() {
        try {
            if (view.getNombre().trim().isEmpty() || view.getNit().trim().isEmpty()) {
                view.mostrarMensaje("Nombre y NIT son obligatorios", "Validación", 
                    javax.swing.JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Proveedor proveedor = new Proveedor(
                null,
                view.getNombre(),
                view.getNit(),
                view.getNrc(),
                view.isEsContribuyente()
            );
            
            Long id = service.registrarProveedor(proveedor);
            view.mostrarMensaje("Proveedor registrado con ID: " + id, "Éxito", 
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
            
            view.limpiarFormulario();
            view.cargarProveedores();
            
        } catch (Exception e) {
            view.mostrarMensaje("Error: " + e.getMessage(), "Error", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void actualizarProveedor() {
        try {
            Long id = view.getIdProveedor();
            if (id == null) {
                view.mostrarMensaje("Seleccione un proveedor de la tabla", "Validación", 
                    javax.swing.JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Proveedor proveedor = new Proveedor();
            proveedor.setIdEntidad(id);
            proveedor.setNombre(view.getNombre());
            proveedor.setNit(view.getNit());
            proveedor.setNrc(view.getNrc());
            proveedor.setEsContribuyente(view.isEsContribuyente());
            
            boolean actualizado = service.actualizarProveedor(proveedor);
            if (actualizado) {
                view.mostrarMensaje("Proveedor actualizado correctamente", "Éxito", 
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);
                view.limpiarFormulario();
                view.cargarProveedores();
            }
            
        } catch (Exception e) {
            view.mostrarMensaje("Error: " + e.getMessage(), "Error", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public void eliminarProveedor() {
        try {
            Long id = view.getIdProveedor();
            if (id == null) {
                view.mostrarMensaje("Seleccione un proveedor de la tabla", "Validación", 
                    javax.swing.JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int confirmacion = javax.swing.JOptionPane.showConfirmDialog(view,
                "¿Está seguro de eliminar este proveedor?",
                "Confirmar eliminación",
                javax.swing.JOptionPane.YES_NO_OPTION);
            
            if (confirmacion == javax.swing.JOptionPane.YES_OPTION) {
                boolean eliminado = service.eliminarProveedor(id);
                if (eliminado) {
                    view.mostrarMensaje("Proveedor eliminado correctamente", "Éxito", 
                        javax.swing.JOptionPane.INFORMATION_MESSAGE);
                    view.limpiarFormulario();
                    view.cargarProveedores();
                }
            }
            
        } catch (Exception e) {
            view.mostrarMensaje("Error: " + e.getMessage(), "Error", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
    
    public List<Proveedor> listarProveedores() throws Exception {
        return service.listarProveedores();
    }
}