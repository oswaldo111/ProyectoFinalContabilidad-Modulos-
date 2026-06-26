package com.sistema.modulos.contabilidad.Services;

import com.sistema.core.security.SessionManager;
import com.sistema.modulos.contabilidad.DAO.CuentaDAO;
import com.sistema.modulos.contabilidad.Models.Cuenta;

import java.util.List;

/**
 * Servicio de lógica de negocio para el Catálogo de Cuentas.
 * Módulo de Contabilidad
 */
public class CuentaService {

    private final CuentaDAO cuentaDAO;

    public CuentaService() {
        this.cuentaDAO = new CuentaDAO();
    }

    public List<Cuenta> listarCuentas() throws Exception {
        validarSesion();
        try {
            return cuentaDAO.obtenerTodas(SessionManager.getIdEmpresa());
        } catch (Exception e) {
            throw new Exception("Error al cargar el catálogo de cuentas: " + e.getMessage());
        }
    }

    public List<Cuenta> listarCuentasDetalle() throws Exception {
        validarSesion();
        try {
            return cuentaDAO.obtenerCuentasDetalle(SessionManager.getIdEmpresa());
        } catch (Exception e) {
            throw new Exception("Error al cargar cuentas de detalle: " + e.getMessage());
        }
    }

    public void agregarCuenta(Cuenta c) throws Exception {
        validarSesion();
        validarCuenta(c);
        c.setIdEmpresa(SessionManager.getIdEmpresa());
        try {
            if (cuentaDAO.existeCodigo(c.getCodigoCuenta(), c.getIdEmpresa())) {
                throw new Exception("Ya existe una cuenta con el código: " + c.getCodigoCuenta());
            }
            cuentaDAO.insertar(c);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public void editarCuenta(Cuenta c) throws Exception {
        validarSesion();
        validarCuenta(c);
        try {
            cuentaDAO.actualizar(c);
        } catch (Exception e) {
            throw new Exception("Error al actualizar la cuenta: " + e.getMessage());
        }
    }

    public void eliminarCuenta(int idCuenta) throws Exception {
        validarSesion();
        try {
            if (cuentaDAO.tieneSubcuentas(idCuenta)) {
                throw new Exception("No se puede eliminar: esta cuenta tiene subcuentas asociadas.");
            }
            cuentaDAO.eliminar(idCuenta);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    private void validarCuenta(Cuenta c) throws Exception {
        if (c.getCodigoCuenta() == null || c.getCodigoCuenta().trim().isEmpty()) {
            throw new Exception("El código de cuenta es obligatorio.");
        }
        if (c.getNombreCuenta() == null || c.getNombreCuenta().trim().isEmpty()) {
            throw new Exception("El nombre de la cuenta es obligatorio.");
        }
        if (c.getTipoCuenta() == null || c.getTipoCuenta().trim().isEmpty()) {
            throw new Exception("El tipo de cuenta es obligatorio.");
        }
    }

    private void validarSesion() throws Exception {
        if (!SessionManager.haySesionActiva()) {
            throw new Exception("No hay sesión activa. Por favor, inicie sesión primero.");
        }
    }
}