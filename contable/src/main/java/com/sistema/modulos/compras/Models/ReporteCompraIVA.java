package com.sistema.modulos.compras.Models;

import java.math.BigDecimal;
import java.sql.Date;

public class ReporteCompraIVA {
    private String tipoDocumento;
    private String numeroDocumento;
    private Date fechaEmision;
    private Date fechaVencimiento;
    private String proveedor;
    private String nitProveedor;
    private String nrcProveedor;
    private BigDecimal montoGravado;
    private BigDecimal montoIva;
    private BigDecimal montoTotal;
    private String estadoPago;
    
    // Getters y Setters
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
    public Date getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(Date fechaEmision) { this.fechaEmision = fechaEmision; }
    public Date getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(Date fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }
    public String getNitProveedor() { return nitProveedor; }
    public void setNitProveedor(String nitProveedor) { this.nitProveedor = nitProveedor; }
    public String getNrcProveedor() { return nrcProveedor; }
    public void setNrcProveedor(String nrcProveedor) { this.nrcProveedor = nrcProveedor; }
    public BigDecimal getMontoGravado() { return montoGravado; }
    public void setMontoGravado(BigDecimal montoGravado) { this.montoGravado = montoGravado; }
    public BigDecimal getMontoIva() { return montoIva; }
    public void setMontoIva(BigDecimal montoIva) { this.montoIva = montoIva; }
    public BigDecimal getMontoTotal() { return montoTotal; }
    public void setMontoTotal(BigDecimal montoTotal) { this.montoTotal = montoTotal; }
    public String getEstadoPago() { return estadoPago; }
    public void setEstadoPago(String estadoPago) { this.estadoPago = estadoPago; }
}