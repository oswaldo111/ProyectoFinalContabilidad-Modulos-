package com.sistema.modulos.bancos.Services;

import com.sistema.core.security.SessionManager;
import com.sistema.modulos.bancos.DAO.CuentaBancariaDAO;
import com.sistema.modulos.bancos.DAO.MovimientoBancarioDAO;
import com.sistema.modulos.bancos.Models.CuentaBancaria;
import com.sistema.modulos.bancos.Models.MovimientoBancario;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Servicio de lógica de negocio para movimientos bancarios.
 * Módulo de Bancos — Grupo 4
 */
public class MovimientoBancarioService {

    private final MovimientoBancarioDAO movimientoDAO;
    private final CuentaBancariaDAO cuentaDAO;

    public MovimientoBancarioService() {
        this.movimientoDAO = new MovimientoBancarioDAO();
        this.cuentaDAO = new CuentaBancariaDAO();
    }

    public List<MovimientoBancario> consultarMovimientos(int idBanco, int mes, int anio) throws Exception {
        validarSesionActiva();
        validarPeriodo(mes, anio);
        try {
            return movimientoDAO.obtenerPorCuenta(idBanco, mes, anio);
        } catch (Exception e) {
            throw new Exception("Error al consultar movimientos: " + e.getMessage());
        }
    }

    public List<MovimientoBancario> consultarTodos(int mes, int anio) throws Exception {
        validarSesionActiva();
        validarPeriodo(mes, anio);
        int idEmpresa = SessionManager.getIdEmpresa();
        try {
            return movimientoDAO.obtenerTodos(idEmpresa, mes, anio);
        } catch (Exception e) {
            throw new Exception("Error al consultar movimientos: " + e.getMessage());
        }
    }

    public void registrarMovimiento(MovimientoBancario m) throws Exception {
        validarSesionActiva();
        validarMovimiento(m);
        m.setIdEmpresa(SessionManager.getIdEmpresa());
        try {
            movimientoDAO.insertar(m);
        } catch (Exception e) {
            throw new Exception("Error al registrar movimiento: " + e.getMessage());
        }
    }

    public Map<String, BigDecimal> obtenerTotales(int idBanco, int mes, int anio) throws Exception {
        validarSesionActiva();
        validarPeriodo(mes, anio);
        try {
            return movimientoDAO.obtenerTotalesPorPeriodo(idBanco, mes, anio);
        } catch (Exception e) {
            throw new Exception("Error al obtener totales: " + e.getMessage());
        }
    }

    private void validarMovimiento(MovimientoBancario m) throws Exception {
        if (m.getMonto() == null || m.getMonto().compareTo(BigDecimal.ZERO) <= 0) {
            throw new Exception("El monto debe ser mayor a cero.");
        }

        String tipo = m.getTipoMovimiento();
        if (tipo == null || (!tipo.equals("INGRESO") && !tipo.equals("EGRESO")
                && !tipo.equals("CHEQUE") && !tipo.equals("TRANSFERENCIA"))) {
            throw new Exception("Tipo de movimiento no válido.");
        }

        if ("CHEQUE".equals(tipo) && (m.getNumeroCheque() == null || m.getNumeroCheque().trim().isEmpty())) {
            throw new Exception("El número de cheque es obligatorio para movimientos tipo CHEQUE.");
        }

        // Validar saldo suficiente para egresos
        if ("EGRESO".equals(tipo) || "CHEQUE".equals(tipo) || "TRANSFERENCIA".equals(tipo)) {
            CuentaBancaria cuenta = cuentaDAO.obtenerPorId(m.getIdBanco());
            if (cuenta != null && cuenta.getSaldoBanco().compareTo(m.getMonto()) < 0) {
                throw new Exception("Saldo insuficiente. Disponible: $" + cuenta.getSaldoBanco()
                        + ", Requerido: $" + m.getMonto());
            }
        }
    }

    private void validarPeriodo(int mes, int anio) throws Exception {
        if (mes < 1 || mes > 12) {
            throw new Exception("El mes debe estar entre 1 y 12.");
        }
        if (anio < 2000 || anio > 2100) {
            throw new Exception("El año no es válido.");
        }
    }

    private void validarSesionActiva() throws Exception {
        if (!SessionManager.haySesionActiva()) {
            throw new Exception("No hay una sesión activa. Por favor, inicie sesión primero.");
        }
    }
}
