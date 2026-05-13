package com.sistema.modulos.compras.Models;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

public class Compra {
    private int idFactura;
    private int idEntidad;
    private String nombreProveedor;
    private String tipoDocumento;
    private String numeroDocumento;
    private Timestamp fechaEmision;
    private Date fechaVencimiento;
    private BigDecimal montoGravado;
    private BigDecimal montoIva;
    private BigDecimal montoExento;
    private BigDecimal montoTotal;
    private BigDecimal saldoPendiente;
    private String estadoPago;
    
    // Getters y Setters
    public int getIdFactura() { return idFactura; }
    public void setIdFactura(int idFactura) { this.idFactura = idFactura; }
    public int getIdEntidad() { return idEntidad; }
    public void setIdEntidad(int idEntidad) { this.idEntidad = idEntidad; }
    public String getNombreProveedor() { return nombreProveedor; }
    public void setNombreProveedor(String nombreProveedor) { this.nombreProveedor = nombreProveedor; }
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
    public Timestamp getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(Timestamp fechaEmision) { this.fechaEmision = fechaEmision; }
    public Date getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(Date fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public BigDecimal getMontoGravado() { return montoGravado; }
    public void setMontoGravado(BigDecimal montoGravado) { this.montoGravado = montoGravado; }
    public BigDecimal getMontoIva() { return montoIva; }
    public void setMontoIva(BigDecimal montoIva) { this.montoIva = montoIva; }
    public BigDecimal getMontoExento() { return montoExento; }
    public void setMontoExento(BigDecimal montoExento) { this.montoExento = montoExento; }
    public BigDecimal getMontoTotal() { return montoTotal; }
    public void setMontoTotal(BigDecimal montoTotal) { this.montoTotal = montoTotal; }
    public BigDecimal getSaldoPendiente() { return saldoPendiente; }
    public void setSaldoPendiente(BigDecimal saldoPendiente) { this.saldoPendiente = saldoPendiente; }
    public String getEstadoPago() { return estadoPago; }
    public void setEstadoPago(String estadoPago) { this.estadoPago = estadoPago; }
}