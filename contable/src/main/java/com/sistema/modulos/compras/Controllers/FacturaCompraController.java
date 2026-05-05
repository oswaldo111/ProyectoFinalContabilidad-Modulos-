package com.sistema.modulos.compras.Controllers;

import com.sistema.modulos.compras.Models.FacturaCompra;
import com.sistema.modulos.compras.Models.Proveedor;
import com.sistema.modulos.compras.Services.FacturaCompraService;
import com.sistema.modulos.compras.Services.ProveedorService;
import com.sistema.modulos.compras.Views.FacturaCompraView;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FacturaCompraController {

    private final FacturaCompraView view;
    private final FacturaCompraService facturaService;
    private final ProveedorService proveedorService;

    public FacturaCompraController(FacturaCompraView view) {
        this.view = view;
        this.facturaService = new FacturaCompraService();
        this.proveedorService = new ProveedorService();
        cargarProveedores();
        cargarFacturasPendientes();
    }

    public void cargarProveedores() {
        try {
            List<Proveedor> proveedores = proveedorService.listarProveedores();
            view.cargarProveedoresCombo(proveedores);
        } catch (Exception e) {
            view.mostrarMensaje("Error cargando proveedores: " + e.getMessage(), "Error", 0);
        }
    }

    public void calcularMontos() {
        try {
            String totalStr = view.getTotal().replace(",", ".");
            String gravadoStr = view.getGravado().replace(",", ".");
            String tipoDoc = view.getTipoDocumento();
            
            BigDecimal total = totalStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(totalStr);
            BigDecimal gravado = gravadoStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(gravadoStr);
            BigDecimal iva = BigDecimal.ZERO;

            if ("EXP".equals(tipoDoc)) {
                iva = BigDecimal.ZERO;
                gravado = total;
            } else {
                if (total.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal divisor = new BigDecimal("1.13");
                    gravado = total.divide(divisor, 2, BigDecimal.ROUND_HALF_UP);
                    iva = total.subtract(gravado);
                } else if (gravado.compareTo(BigDecimal.ZERO) > 0) {
                    iva = gravado.multiply(new BigDecimal("0.13")).setScale(2, BigDecimal.ROUND_HALF_UP);
                    total = gravado.add(iva);
                }
            }

            view.setGravado(gravado.toPlainString());
            view.setIva(iva.toPlainString());
            view.setTotal(total.toPlainString());

        } catch (NumberFormatException e) {
        }
    }

    public void guardarFactura() {
        try {
            if (view.getNumeroDocumento().trim().isEmpty()) {
                view.mostrarMensaje("Ingrese el número de documento", "Validación", 0);
                return;
            }

            Proveedor prov = view.getProveedorSeleccionado();
            if (prov == null) {
                view.mostrarMensaje("Seleccione un proveedor", "Validación", 0);
                return;
            }

            FacturaCompra factura = new FacturaCompra();
            factura.setIdEntidad(prov.getIdEntidad());
            factura.setNumeroDocumento(view.getNumeroDocumento());
            factura.setTipoDocumento(view.getTipoDocumento());
            factura.setFechaEmision(LocalDate.parse(view.getFecha()));
            factura.setMontoTotal(new BigDecimal(view.getTotal().replace(",", ".")));
            factura.setMontoGravado(new BigDecimal(view.getGravado().replace(",", ".")));
            factura.setMontoIva(new BigDecimal(view.getIva().replace(",", ".")));
          

            facturaService.registrarFacturaCompra(factura);
            view.mostrarMensaje("Factura registrada exitosamente", "Éxito", 1);
            view.limpiarFormulario();
            cargarFacturasPendientes();

        } catch (Exception e) {
            view.mostrarMensaje(e.getMessage(), "Error", 0);
        }
    }

    public void cargarFacturasPendientes() {
        try {
            List<FacturaCompra> facturas = facturaService.listarFacturasPendientes();
            List<Object[]> datos = new ArrayList<>();

            for (FacturaCompra f : facturas) {
                datos.add(new Object[]{
                    f.getIdFactura(),
                    f.getIdEntidad(),
                    f.getNumeroDocumento(),
                    f.getFechaEmision(),
                    f.getMontoTotal(),
                    f.getSaldoPendiente(),
                    f.getEstadoPago()
                });
            }
            view.cargarFacturasTabla(datos);
        } catch (Exception e) {
            view.mostrarMensaje("Error cargando facturas: " + e.getMessage(), "Error", 0);
        }
    }
}
