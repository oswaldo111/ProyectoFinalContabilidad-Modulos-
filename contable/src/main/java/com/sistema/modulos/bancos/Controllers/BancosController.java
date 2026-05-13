package com.sistema.modulos.bancos.Controllers;

import com.sistema.modulos.bancos.Models.ConciliacionBancaria;
import com.sistema.modulos.bancos.Models.CuentaBancaria;
import com.sistema.modulos.bancos.Models.MovimientoBancario;
import com.sistema.modulos.bancos.Services.ConciliacionService;
import com.sistema.modulos.bancos.Services.CuentaBancariaService;
import com.sistema.modulos.bancos.Services.ExportadorCSV;
import com.sistema.modulos.bancos.Services.MovimientoBancarioService;
import com.sistema.modulos.bancos.Views.ConciliacionView;
import com.sistema.modulos.bancos.Views.CuentasBancariasView;
import com.sistema.modulos.bancos.Views.MovimientosView;

import javax.swing.*;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Controlador central del Módulo de Bancos.
 * Coordina la interacción entre las vistas y los servicios.
 * Módulo de Bancos — Grupo 4
 */
public class BancosController {

    private final CuentasBancariasView cuentasView;
    private final MovimientosView movimientosView;
    private final ConciliacionView conciliacionView;

    private final CuentaBancariaService cuentaService;
    private final MovimientoBancarioService movimientoService;
    private final ConciliacionService conciliacionService;
    private final ExportadorCSV exportadorCSV;

    public BancosController(CuentasBancariasView cuentasView, MovimientosView movimientosView,
                            ConciliacionView conciliacionView) {
        this.cuentasView = cuentasView;
        this.movimientosView = movimientosView;
        this.conciliacionView = conciliacionView;

        this.cuentaService = new CuentaBancariaService();
        this.movimientoService = new MovimientoBancarioService();
        this.conciliacionService = new ConciliacionService();
        this.exportadorCSV = new ExportadorCSV();
    }

    // ==========================================
    // CUENTAS BANCARIAS
    // ==========================================

    public void cargarCuentas() {
        try {
            List<CuentaBancaria> cuentas = cuentaService.listarCuentas();
            cuentasView.cargarDatos(cuentas);
            cuentasView.mostrarTotales(cuentaService.obtenerSaldoTotal());
            
            // Actualizar ComboBoxes en otras vistas
            List<CuentaBancaria> activas = cuentaService.listarCuentasActivas();
            movimientosView.cargarCuentas(activas);
            conciliacionView.cargarCuentas(activas);
        } catch (Exception e) {
            cuentasView.mostrarMensajeError(e.getMessage());
        }
    }

    public void agregarCuenta(CuentaBancaria c) {
        try {
            cuentaService.agregarCuenta(c);
            cuentasView.mostrarMensajeExito("Cuenta bancaria agregada correctamente.");
            cargarCuentas();
        } catch (Exception e) {
            cuentasView.mostrarMensajeError(e.getMessage());
        }
    }

    public void editarCuenta(CuentaBancaria c) {
        try {
            cuentaService.editarCuenta(c);
            cuentasView.mostrarMensajeExito("Cuenta bancaria actualizada.");
            cargarCuentas();
        } catch (Exception e) {
            cuentasView.mostrarMensajeError(e.getMessage());
        }
    }

    public void eliminarCuenta(int idBanco) {
        try {
            cuentaService.eliminarCuenta(idBanco);
            cuentasView.mostrarMensajeExito("Cuenta bancaria eliminada.");
            cargarCuentas();
        } catch (Exception e) {
            cuentasView.mostrarMensajeError(e.getMessage());
        }
    }

    // ==========================================
    // MOVIMIENTOS BANCARIOS
    // ==========================================

    public void cargarMovimientos() {
        int idBanco = movimientosView.getIdBancoSeleccionado();
        int mes = movimientosView.getMes();
        int anio = movimientosView.getAnio();

        if (idBanco == -1) return;

        try {
            List<MovimientoBancario> movs = movimientoService.consultarMovimientos(idBanco, mes, anio);
            movimientosView.cargarDatos(movs);

            Map<String, BigDecimal> totales = movimientoService.obtenerTotales(idBanco, mes, anio);
            movimientosView.mostrarTotales(totales);
        } catch (Exception e) {
            movimientosView.mostrarMensajeError(e.getMessage());
        }
    }

    public void registrarMovimiento(MovimientoBancario m) {
        try {
            movimientoService.registrarMovimiento(m);
            movimientosView.mostrarMensajeExito("Movimiento registrado correctamente.");
            cargarMovimientos();
            cargarCuentas(); // Para actualizar saldos
        } catch (Exception e) {
            movimientosView.mostrarMensajeError(e.getMessage());
        }
    }

    public void exportarMovimientosCSV(File destino) {
        int idBanco = movimientosView.getIdBancoSeleccionado();
        int mes = movimientosView.getMes();
        int anio = movimientosView.getAnio();
        String nombreBanco = movimientosView.getNombreBancoSeleccionado();

        try {
            List<MovimientoBancario> movs = movimientoService.consultarMovimientos(idBanco, mes, anio);
            exportadorCSV.exportarMovimientos(movs, mes, anio, nombreBanco, destino);
            movimientosView.mostrarMensajeExito("Reporte exportado a " + destino.getAbsolutePath());
        } catch (Exception e) {
            movimientosView.mostrarMensajeError("Error al exportar: " + e.getMessage());
        }
    }

    // ==========================================
    // CONCILIACIÓN BANCARIA
    // ==========================================

    public void cargarPendientes() {
        int idBanco = conciliacionView.getIdBancoSeleccionado();
        if (idBanco == -1) return;

        try {
            List<MovimientoBancario> pendientes = conciliacionService.obtenerPendientes(idBanco);
            conciliacionView.cargarPendientes(pendientes);
        } catch (Exception e) {
            conciliacionView.mostrarMensajeError(e.getMessage());
        }
    }

    public void conciliarSeleccionados() {
        List<Integer> ids = conciliacionView.getIdsSeleccionados();
        if (ids.isEmpty()) {
            conciliacionView.mostrarMensajeError("Debe seleccionar al menos un movimiento.");
            return;
        }

        try {
            conciliacionService.conciliarMovimientos(ids);
            conciliacionView.mostrarMensajeExito(ids.size() + " movimientos marcados como conciliados.");
            cargarPendientes();
        } catch (Exception e) {
            conciliacionView.mostrarMensajeError(e.getMessage());
        }
    }

    public void calcularConciliacion() {
        int idBanco = conciliacionView.getIdBancoSeleccionado();
        int mes = conciliacionView.getMes();
        int anio = conciliacionView.getAnio();
        BigDecimal saldoBanco = conciliacionView.getSaldoBanco();

        if (idBanco == -1) return;

        try {
            ConciliacionBancaria conc = conciliacionService.calcularConciliacion(idBanco, mes, anio, saldoBanco);
            conciliacionService.guardarConciliacion(conc);
            conciliacionView.mostrarResultado(conc);
            
            if (conc.estaConciliado()) {
                conciliacionView.mostrarMensajeExito("¡Conciliación cuadrada correctamente!");
            } else {
                conciliacionView.mostrarMensajeAdvertencia("La conciliación tiene una diferencia de $" + conc.getDiferencia());
            }
        } catch (Exception e) {
            conciliacionView.mostrarMensajeError(e.getMessage());
        }
    }

    public void generarReporteDisponibilidad() {
        try {
            List<CuentaBancaria> cuentas = conciliacionService.generarReporteDisponibilidad();
            conciliacionView.cargarDisponibilidad(cuentas);
        } catch (Exception e) {
            conciliacionView.mostrarMensajeError(e.getMessage());
        }
    }

    public void exportarDisponibilidadCSV(File destino) {
        try {
            List<CuentaBancaria> cuentas = conciliacionService.generarReporteDisponibilidad();
            exportadorCSV.exportarDisponibilidadDiaria(cuentas, destino);
            conciliacionView.mostrarMensajeExito("Reporte exportado a " + destino.getAbsolutePath());
        } catch (Exception e) {
            conciliacionView.mostrarMensajeError("Error al exportar: " + e.getMessage());
        }
    }

    public void exportarConciliacionCSV(File destino) {
        int idBanco = conciliacionView.getIdBancoSeleccionado();
        int mes = conciliacionView.getMes();
        int anio = conciliacionView.getAnio();
        BigDecimal saldoBanco = conciliacionView.getSaldoBanco();

        if (idBanco == -1) return;

        try {
            ConciliacionBancaria conc = conciliacionService.calcularConciliacion(idBanco, mes, anio, saldoBanco);
            List<MovimientoBancario> pendientes = conciliacionService.obtenerPendientes(idBanco);
            exportadorCSV.exportarConciliacion(conc, pendientes, destino);
            conciliacionView.mostrarMensajeExito("Conciliación exportada a " + destino.getAbsolutePath());
        } catch (Exception e) {
            conciliacionView.mostrarMensajeError("Error al exportar: " + e.getMessage());
        }
    }
}
