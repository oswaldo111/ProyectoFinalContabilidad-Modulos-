package com.sistema.modulos.contabilidad.Controllers;

import java.util.List;
import com.sistema.modulos.contabilidad.DAO.CuentaDAO;
import com.sistema.modulos.contabilidad.Models.Cuenta;

public class CuentaController {

    private final CuentaDAO cuentaDAO;

    public CuentaController() {
        this.cuentaDAO = new CuentaDAO();
    }

    // ==========================================
    // GUARDAR CUENTA
    // ==========================================
    public boolean guardarCuenta(Cuenta cuenta) {
        if (cuenta == null) return false;
        
        try {
            cuentaDAO.insertar(cuenta);
            return true;
        } catch (Exception e) {
            System.err.println("Error en controlador al guardar: " + e.getMessage());
            return false;
        }
    }

    // ==========================================
    // LISTAR CUENTAS
    // ==========================================
    public List<Cuenta> obtenerTodasLasCuentas() {
        return cuentaDAO.listar();
    }

    // ==========================================
    // BUSCAR POR ID
    // ==========================================
    public Cuenta obtenerCuentaPorId(int idCuenta) {
        if (idCuenta <= 0) return null;
        return cuentaDAO.buscarPorId(idCuenta);
    }

    // ==========================================
    // MODIFICAR CUENTA
    // ==========================================
    public boolean modificarCuenta(Cuenta cuenta) {
        if (cuenta == null) return false;

        try {
            // Se usa getIdCuenta() que corresponde a tu modelo
            Cuenta existente = cuentaDAO.buscarPorId(cuenta.getIdCuenta()); 
            if (existente != null) {
                cuentaDAO.actualizar(cuenta);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error en controlador al modificar: " + e.getMessage());
            return false;
        }
    }

    // ==========================================
    // ELIMINAR CUENTA
    // ==========================================
    public boolean borrarCuenta(int idCuenta) {
        try {
            Cuenta existente = cuentaDAO.buscarPorId(idCuenta);
            if (existente != null) {
                cuentaDAO.eliminar(idCuenta);
                return true;
            }
            return false; 
        } catch (Exception e) {
            System.err.println("Error en controlador al eliminar: " + e.getMessage());
            return false;
        }
    }
    
    // ==========================================
    // CERRAR CONEXIONES
    // ==========================================
    public void finalizarConexion() {
        CuentaDAO.cerrarFactory();
    }
}