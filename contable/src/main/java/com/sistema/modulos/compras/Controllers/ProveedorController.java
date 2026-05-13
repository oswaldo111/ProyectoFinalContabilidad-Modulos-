package com.sistema.modulos.compras.Controllers;

import com.sistema.modulos.compras.Models.Proveedor;
import com.sistema.modulos.compras.Services.ProveedorService;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

public class ProveedorController {
    
    private ProveedorService proveedorService;
    
    public ProveedorController() {
        this.proveedorService = new ProveedorService();
    }
    
    public List<Proveedor> obtenerTodos() {
        return proveedorService.obtenerTodos();
    }
    
    public List<Proveedor> buscar(String filtro) {
        return proveedorService.buscar(filtro);
    }
    
    public Proveedor obtenerPorId(int id) {
        return proveedorService.obtenerPorId(id);
    }
    
    public boolean registrarProveedor(Proveedor proveedor, JFrame parentFrame) {
        try {
            boolean exito = proveedorService.registrarProveedor(proveedor);
            if (exito) {
                JOptionPane.showMessageDialog(parentFrame, 
                    "Proveedor registrado exitosamente\n" + proveedor.getNombre(),
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            return exito;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(parentFrame, e.getMessage(), "Error de validación", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parentFrame, "Error en base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public boolean actualizarProveedor(Proveedor proveedor, JFrame parentFrame) {
        try {
            boolean exito = proveedorService.actualizarProveedor(proveedor);
            if (exito) {
                JOptionPane.showMessageDialog(parentFrame, "Proveedor actualizado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            return exito;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(parentFrame, e.getMessage(), "Error de validación", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parentFrame, "Error en base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public boolean eliminarProveedor(int id, String nombre, JFrame parentFrame) {
        int confirm = JOptionPane.showConfirmDialog(parentFrame,
            "¿Está seguro de eliminar al proveedor?\n" + nombre + "\n\nNota: No se puede eliminar si tiene compras asociadas.",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return false;
        }
        
        try {
            boolean exito = proveedorService.eliminarProveedor(id);
            if (exito) {
                JOptionPane.showMessageDialog(parentFrame, "Proveedor eliminado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            return exito;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parentFrame, "Error al eliminar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}