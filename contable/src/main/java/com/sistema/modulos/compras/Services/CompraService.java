package com.sistema.modulos.compras.Services;

import com.sistema.modulos.compras.DAO.CompraDAO;
import com.sistema.modulos.compras.Models.Compra;
import com.sistema.modulos.compras.Models.DetalleCompra;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class CompraService {
    
    private CompraDAO compraDAO;
    
    public CompraService() {
        this.compraDAO = new CompraDAO();
    }
    
    public void validarCompra(int idProveedor, List<DetalleCompra> detalles, String numeroDocumento, Date fechaVencimiento) {
        if (idProveedor <= 0) {
            throw new IllegalArgumentException("Debe seleccionar un proveedor válido");
        }
        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("Debe agregar al menos un producto a la compra");
        }
        if (numeroDocumento == null || numeroDocumento.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de documento es obligatorio");
        }
        if (fechaVencimiento == null) {
            throw new IllegalArgumentException("La fecha de vencimiento es obligatoria");
        }
        if (fechaVencimiento.toLocalDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de vencimiento no puede ser anterior a hoy");
        }
        
        for (DetalleCompra d : detalles) {
            if (d.getCantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor a cero para: " + d.getNombreProducto());
            }
            if (d.getPrecioUnitario() == null || d.getPrecioUnitario().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("El precio unitario debe ser mayor a cero para: " + d.getNombreProducto());
            }
        }
    }
    
    public BigDecimal calcularSubtotal(List<DetalleCompra> detalles) {
        return detalles.stream()
            .map(DetalleCompra::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public BigDecimal calcularIVA(BigDecimal subtotal, BigDecimal porcentajeIVA) {
        return subtotal.multiply(porcentajeIVA.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP))
                       .setScale(2, RoundingMode.HALF_UP);
    }
    
    public BigDecimal calcularTotal(BigDecimal subtotal, BigDecimal iva) {
        return subtotal.add(iva).setScale(2, RoundingMode.HALF_UP);
    }
    
    public BigDecimal getPorcentajeIVA() {
        return new BigDecimal("13");
    }
    
    public int registrarCompra(int idProveedor, String tipoDocumento, String numeroDocumento,
                               Date fechaVencimiento, List<DetalleCompra> detalles) throws SQLException {
        
        validarCompra(idProveedor, detalles, numeroDocumento, fechaVencimiento);
        
        BigDecimal subtotal = calcularSubtotal(detalles);
        BigDecimal iva = calcularIVA(subtotal, getPorcentajeIVA());
        BigDecimal total = calcularTotal(subtotal, iva);
        
        return compraDAO.registrarCompra(idProveedor, tipoDocumento, numeroDocumento,
                                         fechaVencimiento, subtotal, iva, BigDecimal.ZERO, total, detalles);
    }
    
    public List<Compra> obtenerTodasCompras() {
        return compraDAO.obtenerTodas();
    }
    
    public List<Compra> buscarCompras(String proveedor, String estado, String fechaDesde, String fechaHasta, Integer diasVencimiento) {
        return compraDAO.buscarCompras(proveedor, estado, fechaDesde, fechaHasta, diasVencimiento);
    }
    
    public List<DetalleCompra> obtenerDetallesCompra(int idFactura) {
        return compraDAO.obtenerDetalles(idFactura);
    }
    
    public BigDecimal calcularTotalPendiente(List<Compra> compras) {
        return compras.stream()
            .filter(c -> !"PAGADO".equals(c.getEstadoPago()))
            .map(Compra::getSaldoPendiente)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}