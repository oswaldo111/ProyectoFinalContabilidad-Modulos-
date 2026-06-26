package com.sistema.modulos.ventas.Model;

import java.time.LocalDate;

public class LibroVenta {

    private int idFactura;
    private int idEmpresa;
    private int idEntidad;
    private String nombreCliente;
    private String nit;
    private String nrc;
    private String tipoDocumento;
    private String numeroDocumento;
    private LocalDate fechaEmision;
    private double montoGravado;
    private double montoIva;
    private double montoExento;
    private double montoTotal;
    private String estadoPago;

    public LibroVenta() {
    }

    public LibroVenta(int idFactura, int idEmpresa, int idEntidad, String nombreCliente,
            String nit, String nrc, String tipoDocumento, String numeroDocumento,
            LocalDate fechaEmision, double montoGravado, double montoIva,
            double montoExento, double montoTotal, String estadoPago) {
        this.idFactura = idFactura;
        this.idEmpresa = idEmpresa;
        this.idEntidad = idEntidad;
        this.nombreCliente = nombreCliente;
        this.nit = nit;
        this.nrc = nrc;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.fechaEmision = fechaEmision;
        this.montoGravado = montoGravado;
        this.montoIva = montoIva;
        this.montoExento = montoExento;
        this.montoTotal = montoTotal;
        this.estadoPago = estadoPago;
    }

    public int getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(int idFactura) {
        this.idFactura = idFactura;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public int getIdEntidad() {
        return idEntidad;
    }

    public void setIdEntidad(int idEntidad) {
        this.idEntidad = idEntidad;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getNrc() {
        return nrc;
    }

    public void setNrc(String nrc) {
        this.nrc = nrc;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
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

    public double getMontoGravado() {
        return montoGravado;
    }

    public void setMontoGravado(double montoGravado) {
        this.montoGravado = montoGravado;
    }

    public double getMontoIva() {
        return montoIva;
    }

    public void setMontoIva(double montoIva) {
        this.montoIva = montoIva;
    }

    public double getMontoExento() {
        return montoExento;
    }

    public void setMontoExento(double montoExento) {
        this.montoExento = montoExento;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(double montoTotal) {
        this.montoTotal = montoTotal;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }
}
