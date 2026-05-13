package com.sistema.modulos.bancos.Services;

import com.sistema.core.security.SessionManager;
import com.sistema.modulos.bancos.DAO.CuentaBancariaDAO;
import com.sistema.modulos.bancos.Models.CuentaBancaria;

import java.math.BigDecimal;
import java.util.List;

/**
 * Servicio de lógica de negocio para cuentas bancarias.
 * Módulo de Bancos — Grupo 4
 */
public class CuentaBancariaService {

    private final CuentaBancariaDAO cuentaDAO;

    public CuentaBancariaService() {
        this.cuentaDAO = new CuentaBancariaDAO();
    }

    public List<CuentaBancaria> listarCuentas() throws Exception {
        validarSesionActiva();
        int idEmpresa = SessionManager.getIdEmpresa();
        try {
            return cuentaDAO.obtenerTodas(idEmpresa);
        } catch (Exception e) {
            throw new Exception("Error al listar cuentas bancarias: " + e.getMessage());
        }
    }

    public List<CuentaBancaria> listarCuentasActivas() throws Exception {
        validarSesionActiva();
        int idEmpresa = SessionManager.getIdEmpresa();
        try {
            return cuentaDAO.obtenerActivas(idEmpresa);
        } catch (Exception e) {
            throw new Exception("Error al listar cuentas activas: " + e.getMessage());
        }
    }

    public void agregarCuenta(CuentaBancaria c) throws Exception {
        validarSesionActiva();
        validarDatosCuenta(c);
        c.setIdEmpresa(SessionManager.getIdEmpresa());
        try {
            cuentaDAO.insertar(c);
        } catch (Exception e) {
            throw new Exception("Error al agregar cuenta bancaria: " + e.getMessage());
        }
    }

    public void editarCuenta(CuentaBancaria c) throws Exception {
        validarSesionActiva();
        validarDatosCuenta(c);
        try {
            cuentaDAO.actualizar(c);
        } catch (Exception e) {
            throw new Exception("Error al actualizar cuenta bancaria: " + e.getMessage());
        }
    }

    public void eliminarCuenta(int idBanco) throws Exception {
        validarSesionActiva();
        try {
            if (cuentaDAO.tieneMovimientos(idBanco)) {
                throw new Exception("No se puede eliminar: la cuenta tiene movimientos registrados.\nUse la opción de desactivar en su lugar.");
            }
            cuentaDAO.eliminar(idBanco);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    public BigDecimal obtenerSaldoTotal() throws Exception {
        validarSesionActiva();
        int idEmpresa = SessionManager.getIdEmpresa();
        try {
            return cuentaDAO.obtenerSaldoTotal(idEmpresa);
        } catch (Exception e) {
            throw new Exception("Error al obtener saldo total: " + e.getMessage());
        }
    }

    private void validarDatosCuenta(CuentaBancaria c) throws Exception {
        if (c.getNombreBanco() == null || c.getNombreBanco().trim().isEmpty()) {
            throw new Exception("El nombre del banco es obligatorio.");
        }
        if (c.getNumeroCuenta() == null || c.getNumeroCuenta().trim().isEmpty()) {
            throw new Exception("El número de cuenta es obligatorio.");
        }
        if (c.getSaldoBanco() != null && c.getSaldoBanco().compareTo(BigDecimal.ZERO) < 0) {
            throw new Exception("El saldo inicial no puede ser negativo.");
        }
        if (c.getTipoCuenta() == null || (!"AHORRO".equals(c.getTipoCuenta()) && !"CORRIENTE".equals(c.getTipoCuenta()))) {
            throw new Exception("El tipo de cuenta debe ser AHORRO o CORRIENTE.");
        }
    }

    private void validarSesionActiva() throws Exception {
        if (!SessionManager.haySesionActiva()) {
            throw new Exception("No hay una sesión activa. Por favor, inicie sesión primero.");
        }
    }
}
