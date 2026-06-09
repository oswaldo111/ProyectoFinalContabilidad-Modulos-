package com.sistema.modulos.contabilidad.Services;

import java.util.List;

import com.sistema.modulos.contabilidad.DAO.CuentaDAO;
import com.sistema.modulos.contabilidad.Models.Cuenta;

public class CuentaService {

    private CuentaDAO cuentaDAO;

    // Constructor
    public CuentaService() {
        this.cuentaDAO = new CuentaDAO();
    }

    // =========================
    // GUARDAR CUENTA
    // =========================
    public void guardarCuenta(Cuenta cuenta) {
        cuentaDAO.insertar(cuenta);
    }

    // =========================
    // OBTENER TODAS LAS CUENTAS
    // =========================
    public List<Cuenta> obtenerCuentas() {
        return cuentaDAO.listar();
    }

    // =========================
    // BUSCAR CUENTA POR ID
    // =========================
    public Cuenta obtenerCuentaPorId(int id) {
        return cuentaDAO.buscarPorId(id);
    }

    // =========================
    // ACTUALIZAR CUENTA
    // =========================
    public void actualizarCuenta(Cuenta cuenta) {
        cuentaDAO.actualizar(cuenta);
    }

    // =========================
    // ELIMINAR CUENTA
    // =========================
    public void eliminarCuenta(int id) {
        cuentaDAO.eliminar(id);
    }

    // =========================
    // CERRAR SESSION FACTORY
    // =========================
    public void cerrarConexion() {
        CuentaDAO.cerrarFactory();
    }
}