package com.sistema.modulos.contabilidad.Controllers;

import com.sistema.modulos.contabilidad.DAO.EmpresaDAO;
import com.sistema.modulos.contabilidad.Models.Cuenta;
import com.sistema.modulos.contabilidad.Models.Empresa;
import com.sistema.modulos.contabilidad.DAO.CuentaDAO;
import com.sistema.modulos.contabilidad.Services.CuentaService;
import com.sistema.modulos.contabilidad.Views.CatalogoCuentas;

import java.util.List;

/**
 * Controlador del Catálogo de Cuentas Contables.
 * Módulo de Contabilidad
 */
public class CatalogoController {

    private final CatalogoCuentas vista;
    private final CuentaDAO cuentaDAO;
    private final EmpresaDAO empresaDAO;

    private int idEmpresaActual = -1;

    public CatalogoController(CatalogoCuentas vista) {
        this.vista = vista;
        this.cuentaDAO = new CuentaDAO();
        this.empresaDAO = new EmpresaDAO();
    }

    public void cargarEmpresas() {
        try {
            List<Empresa> empresas = empresaDAO.obtenerTodas();
            vista.cargarEmpresas(empresas);
        } catch (Exception e) {
            vista.mostrarError("Error al cargar empresas: " + e.getMessage());
        }
    }

    public void cargarCatalogo() {
        try {
            if (idEmpresaActual == -1) return;
            List<Cuenta> cuentas = cuentaDAO.obtenerTodas(idEmpresaActual);
            vista.cargarArbol(cuentas);
        } catch (Exception e) {
            vista.mostrarError(e.getMessage());
        }
    }

    public void seleccionarEmpresa(int idEmpresa) {
        this.idEmpresaActual = idEmpresa;
        cargarCatalogo();
    }

    public void agregarCuenta(Cuenta cuenta) {
        try {
            if (idEmpresaActual == -1) {
                vista.mostrarError("Seleccione una empresa primero.");
                return;
            }
            cuenta.setIdEmpresa(idEmpresaActual);
            cuentaDAO.insertar(cuenta);
            vista.mostrarExito("Cuenta agregada correctamente.");
            cargarCatalogo();
        } catch (Exception e) {
            vista.mostrarError(e.getMessage());
        }
    }

    public void editarCuenta(Cuenta cuenta) {
        try {
            cuentaDAO.actualizar(cuenta);
            vista.mostrarExito("Cuenta actualizada correctamente.");
            cargarCatalogo();
        } catch (Exception e) {
            vista.mostrarError(e.getMessage());
        }
    }

    public void eliminarCuenta(int idCuenta) {
        try {
            if (cuentaDAO.tieneSubcuentas(idCuenta)) {
                vista.mostrarError("No se puede eliminar: esta cuenta tiene subcuentas asociadas.");
                return;
            }
            cuentaDAO.eliminar(idCuenta);
            vista.mostrarExito("Cuenta eliminada correctamente.");
            cargarCatalogo();
        } catch (Exception e) {
            vista.mostrarError(e.getMessage());
        }
    }

    public int getIdEmpresaActual() {
        return idEmpresaActual;
    }
}