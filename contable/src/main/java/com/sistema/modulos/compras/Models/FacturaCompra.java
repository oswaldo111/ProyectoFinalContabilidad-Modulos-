package com.sistema.modulos.compras.Models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class FacturaCompra {
    private Long idFactura;
    private Long idEmpresa;
    private Long idEntidad;
    private String tipoDocumento;
    private String tipoOperacion;
    private String numeroDocumento;
    private LocalDate fechaEmision;
    private BigDecimal montoGravado;
    private BigDecimal montoIva;
    private BigDecimal montoTotal;
    private BigDecimal saldoPendiente;
    private String estadoPago;
    private String nombreProveedor;
    public FacturaCompra() {
    }

    public FacturaCompra(Long idEmpresa, Long idEntidad, String numeroDocumento, String tipoDocumento,
            LocalDate fechaEmision, BigDecimal montoGravado, BigDecimal montoIva, BigDecimal montoTotal) {
        this.idEmpresa = idEmpresa;
        this.idEntidad = idEntidad;
        this.numeroDocumento = numeroDocumento;
        this.tipoDocumento = tipoDocumento;
        this.tipoOperacion = "COMPRA";
        this.fechaEmision = fechaEmision;
        this.montoGravado = montoGravado;
        this.montoIva = montoIva;
        this.montoTotal = montoTotal;
        this.saldoPendiente = montoTotal;
        this.estadoPago = "PENDIENTE";
    }

    public Long getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(Long idFactura) {
        this.idFactura = idFactura;
    }

    public Long getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(Long idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public Long getIdEntidad() {
        return idEntidad;
    }

    public void setIdEntidad(Long idEntidad) {
        this.idEntidad = idEntidad;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getTipoOperacion() {
        return tipoOperacion;
    }

    public void setTipoOperacion(String tipoOperacion) {
        this.tipoOperacion = tipoOperacion;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public BigDecimal getMontoGravado() {
        return montoGravado;
    }

    public void setMontoGravado(BigDecimal montoGravado) {
        this.montoGravado = montoGravado;
    }

    public BigDecimal getMontoIva() {
        return montoIva;
    }

    public void setMontoIva(BigDecimal montoIva) {
        this.montoIva = montoIva;
    }

    public BigDecimal getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(BigDecimal montoTotal) {
        this.montoTotal = montoTotal;
    }

    public BigDecimal getSaldoPendiente() {
        return saldoPendiente;
    }

    public void setSaldoPendiente(BigDecimal saldoPendiente) {
        this.saldoPendiente = saldoPendiente;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    @Override
    public String toString() {
        return "Factura #" + numeroDocumento + " - Total: " + montoTotal;
    }
}