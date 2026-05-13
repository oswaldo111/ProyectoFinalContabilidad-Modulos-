package com.sistema.modulos.compras.Controllers;

import com.sistema.modulos.compras.Models.Compra;
import com.sistema.modulos.compras.Models.DetalleCompra;
import com.sistema.modulos.compras.Services.CompraService;
import com.sistema.modulos.compras.Services.PagoService;

import javax.swing.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

public class CompraController {
    
    private CompraService compraService;
    private PagoService pagoService;
    
    public CompraController() {
        this.compraService = new CompraService();
        this.pagoService = new PagoService();
    }
    
    public boolean registrarCompra(int idProveedor, String tipoDocumento, String numeroDocumento,
                                   Date fechaVencimiento, List<DetalleCompra> detalles, JFrame parentFrame) {
        try {
            int idFactura = compraService.registrarCompra(idProveedor, tipoDocumento, numeroDocumento, fechaVencimiento, detalles);
            
            JOptionPane.showMessageDialog(parentFrame, 
                "Compra registrada exitosamente\nN° Factura: " + idFactura,
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            return true;
            
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(parentFrame, e.getMessage(), "Error de validación", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parentFrame, "Error en base de datos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean registrarPago(int idFactura, BigDecimal montoPagado, BigDecimal saldoActual, JFrame parentFrame) {
        try {
            boolean exito = pagoService.registrarPago(idFactura, montoPagado, saldoActual);
            
            if (exito) {
                JOptionPane.showMessageDialog(parentFrame, 
                    "Pago registrado exitosamente\nMonto: $" + montoPagado,
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            return exito;
            
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(parentFrame, e.getMessage(), "Error de validación", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(parentFrame, "Error al registrar el pago: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Compra> obtenerTodasCompras() {
        return compraService.obtenerTodasCompras();
    }
    
    public List<Compra> buscarCompras(String proveedor, String estado, String fechaDesde, String fechaHasta, Integer diasVencimiento) {
        return compraService.buscarCompras(proveedor, estado, fechaDesde, fechaHasta, diasVencimiento);
    }
    
    public List<DetalleCompra> obtenerDetallesCompra(int idFactura) {
        return compraService.obtenerDetallesCompra(idFactura);
    }
    
    public BigDecimal calcularTotalPendiente(List<Compra> compras) {
        return compraService.calcularTotalPendiente(compras);
    }
    
    public BigDecimal getPorcentajeIVA() {
        return compraService.getPorcentajeIVA();
    }
    
    public BigDecimal calcularSubtotal(List<DetalleCompra> detalles) {
        return compraService.calcularSubtotal(detalles);
    }
    
    public BigDecimal calcularIVA(BigDecimal subtotal) {
        return compraService.calcularIVA(subtotal, getPorcentajeIVA());
    }
    
    public BigDecimal calcularTotal(BigDecimal subtotal, BigDecimal iva) {
        return compraService.calcularTotal(subtotal, iva);
    }
}