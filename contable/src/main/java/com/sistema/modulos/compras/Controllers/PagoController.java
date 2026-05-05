package com.sistema.modulos.compras.Controllers;

import com.sistema.modulos.compras.Models.FacturaCompra;
import com.sistema.modulos.compras.Services.FacturaCompraService;
import com.sistema.modulos.compras.Views.PagoView;

import java.math.BigDecimal;
import java.util.List;

public class PagoController {

    private final PagoView view;
    private final FacturaCompraService service;

    public PagoController(PagoView view) {
        this.view = view;
        this.service = new FacturaCompraService();

        cargarFacturas();
    }

    public void cargarFacturas() {
        try {
            List<FacturaCompra> facturas = service.listarFacturasPendientesConNombre();

            view.cargarFacturas(facturas);

        } catch (Exception e) {
            view.mostrarMensaje("Error al cargar facturas: " + e.getMessage(), "Error", 0);
            e.printStackTrace();
        }
    }

    public void seleccionarFactura() {
        FacturaCompra f = view.getFacturaSeleccionada();
        if (f != null) {
            view.setDatosFactura(
                f.getNombreProveedor(),
                f.getNumeroDocumento(),
                f.getMontoTotal() != null ? f.getMontoTotal().toString() : "",
                f.getSaldoPendiente() != null ? f.getSaldoPendiente().toString() : ""
            );
            view.setMontoPago(f.getSaldoPendiente() != null ? f.getSaldoPendiente().toString() : "");
        }
    }

    public void registrarPago() {
        try {
            FacturaCompra f = view.getFacturaSeleccionada();
            if (f == null) {
                view.mostrarMensaje("Selecciona una factura de la tabla", "Aviso", 1);
                return;
            }

            String montoStr = view.getMontoPago();
            if (montoStr == null || montoStr.trim().isEmpty()) {
                view.mostrarMensaje("Ingresa un monto válido", "Aviso", 1);
                return;
            }

            BigDecimal monto = new BigDecimal(montoStr);
            
            if (monto.compareTo(BigDecimal.ZERO) <= 0) {
                view.mostrarMensaje("El monto debe ser mayor a 0", "Error", 0);
                return;
            }

            if (monto.compareTo(f.getSaldoPendiente()) > 0) {
                view.mostrarMensaje("El monto excede el saldo pendiente (" + f.getSaldoPendiente() + ")", "Error", 0);
                return;
            }

            boolean ok = service.procesarPago(f.getIdFactura(), monto);
            
            if (ok) {
                view.mostrarMensaje("Pago registrado exitosamente.", "Éxito", 1);
                view.limpiarFormulario();
                cargarFacturas();
            }

        } catch (NumberFormatException e) {
            view.mostrarMensaje("Formato de monto inválido. Usa números decimales (ej: 100.50)", "Error", 0);
        } catch (Exception e) {
            view.mostrarMensaje(e.getMessage(), "Error", 0);
        }
    }
}