package com.sistema.modulos.contabilidad.Controllers;

import com.sistema.modulos.contabilidad.Models.Cuenta;
import com.sistema.modulos.contabilidad.Services.CuentaService;
import com.sistema.modulos.contabilidad.Views.CatalogoCuentas;

import java.util.List;

/**
 * Controlador del Catálogo de Cuentas Contables.
 * Coordina la vista CatalogoCuentas con el CuentaService.
 * Módulo de Contabilidad
 */
public class CatalogoController {

    private final CatalogoCuentas vista;
    private final CuentaService cuentaService;

    public CatalogoController(CatalogoCuentas vista) {
        this.vista = vista;
        this.cuentaService = new CuentaService();
    }

    /**
     * Carga todas las cuentas y construye el árbol jerárquico en la vista.
     */
    public void cargarCatalogo() {
        try {
            List<Cuenta> cuentas = cuentaService.listarCuentas();
            vista.cargarArbol(cuentas);
        } catch (Exception e) {
            vista.mostrarError(e.getMessage());
        }
    }

    /**
     * Agrega una nueva cuenta contable.
     */
    public void agregarCuenta(Cuenta cuenta) {
        try {
            cuentaService.agregarCuenta(cuenta);
            vista.mostrarExito("Cuenta agregada correctamente.");
            cargarCatalogo();
        } catch (Exception e) {
            vista.mostrarError(e.getMessage());
        }
    }

    /**
     * Edita una cuenta existente.
     */
    public void editarCuenta(Cuenta cuenta) {
        try {
            cuentaService.editarCuenta(cuenta);
            vista.mostrarExito("Cuenta actualizada correctamente.");
            cargarCatalogo();
        } catch (Exception e) {
            vista.mostrarError(e.getMessage());
        }
    }

    /**
     * Elimina una cuenta por su ID.
     */
    public void eliminarCuenta(int idCuenta) {
        try {
            cuentaService.eliminarCuenta(idCuenta);
            vista.mostrarExito("Cuenta eliminada correctamente.");
            cargarCatalogo();
        } catch (Exception e) {
            vista.mostrarError(e.getMessage());
        }
    }
}