package com.sistema.modulos.bancos.Services;

import com.sistema.core.security.SessionManager;
import com.sistema.modulos.bancos.DAO.ConciliacionBancariaDAO;
import com.sistema.modulos.bancos.DAO.CuentaBancariaDAO;
import com.sistema.modulos.bancos.DAO.MovimientoBancarioDAO;
import com.sistema.modulos.bancos.Models.ConciliacionBancaria;
import com.sistema.modulos.bancos.Models.CuentaBancaria;
import com.sistema.modulos.bancos.Models.MovimientoBancario;

import java.math.BigDecimal;
import java.util.List;

/**
 * Servicio de lógica de negocio para conciliación bancaria.
 * Módulo de Bancos — Grupo 4
 */
public class ConciliacionService {

    private final MovimientoBancarioDAO movimientoDAO;
    private final CuentaBancariaDAO cuentaDAO;
    private final ConciliacionBancariaDAO conciliacionDAO;

    public ConciliacionService() {
        this.movimientoDAO = new MovimientoBancarioDAO();
        this.cuentaDAO = new CuentaBancariaDAO();
        this.conciliacionDAO = new ConciliacionBancariaDAO();
    }

    /**
     * Obtiene los movimientos pendientes de conciliar de una cuenta.
     */
    public List<MovimientoBancario> obtenerPendientes(int idBanco) throws Exception {
        validarSesionActiva();
        try {
            return movimientoDAO.obtenerNoConciliados(idBanco);
        } catch (Exception e) {
            throw new Exception("Error al obtener pendientes: " + e.getMessage());
        }
    }

    /**
     * Concilia (marca) los movimientos seleccionados.
     */
    public void conciliarMovimientos(List<Integer> idsMovimientos) throws Exception {
        validarSesionActiva();
        if (idsMovimientos == null || idsMovimientos.isEmpty()) {
            throw new Exception("Debe seleccionar al menos un movimiento para conciliar.");
        }
        try {
            movimientoDAO.marcarConciliadosEnLote(idsMovimientos);
        } catch (Exception e) {
            throw new Exception("Error al conciliar movimientos: " + e.getMessage());
        }
    }

    /**
     * Calcula la conciliación bancaria comparando saldo del sistema vs saldo del banco.
     */
    public ConciliacionBancaria calcularConciliacion(int idBanco, int mes, int anio,
                                                      BigDecimal saldoEstadoCuenta) throws Exception {
        validarSesionActiva();
        if (saldoEstadoCuenta == null || saldoEstadoCuenta.compareTo(BigDecimal.ZERO) < 0) {
            throw new Exception("El saldo del estado de cuenta no puede ser negativo.");
        }

        try {
            CuentaBancaria cuenta = cuentaDAO.obtenerPorId(idBanco);
            if (cuenta == null) {
                throw new Exception("Cuenta bancaria no encontrada.");
            }

            ConciliacionBancaria conc = new ConciliacionBancaria();
            conc.setIdEmpresa(SessionManager.getIdEmpresa());
            conc.setIdBanco(idBanco);
            conc.setMes(mes);
            conc.setAnio(anio);
            conc.setSaldoSegunBanco(saldoEstadoCuenta);
            conc.setSaldoSegunLibros(cuenta.getSaldoBanco());
            conc.setNombreBanco(cuenta.getNombreBanco());
            conc.calcular();

            return conc;
        } catch (Exception e) {
            throw new Exception("Error al calcular conciliación: " + e.getMessage());
        }
    }

    /**
     * Guarda o actualiza una conciliación en la base de datos.
     */
    public void guardarConciliacion(ConciliacionBancaria c) throws Exception {
        validarSesionActiva();
        try {
            ConciliacionBancaria existente = conciliacionDAO.obtenerPorPeriodo(
                    c.getIdBanco(), c.getMes(), c.getAnio());
            if (existente != null) {
                c.setIdConciliacion(existente.getIdConciliacion());
                conciliacionDAO.actualizar(c);
            } else {
                conciliacionDAO.insertar(c);
            }
        } catch (Exception e) {
            throw new Exception("Error al guardar conciliación: " + e.getMessage());
        }
    }

    /**
     * Obtiene el historial de conciliaciones de una cuenta.
     */
    public List<ConciliacionBancaria> obtenerHistorial(int idBanco) throws Exception {
        validarSesionActiva();
        try {
            return conciliacionDAO.obtenerPorBanco(idBanco);
        } catch (Exception e) {
            throw new Exception("Error al obtener historial: " + e.getMessage());
        }
    }

    /**
     * Genera el reporte de disponibilidad diaria (todas las cuentas activas con saldo).
     */
    public List<CuentaBancaria> generarReporteDisponibilidad() throws Exception {
        validarSesionActiva();
        int idEmpresa = SessionManager.getIdEmpresa();
        try {
            return cuentaDAO.obtenerActivas(idEmpresa);
        } catch (Exception e) {
            throw new Exception("Error al generar reporte de disponibilidad: " + e.getMessage());
        }
    }

    private void validarSesionActiva() throws Exception {
        if (!SessionManager.haySesionActiva()) {
            throw new Exception("No hay una sesión activa. Por favor, inicie sesión primero.");
        }
    }
}
