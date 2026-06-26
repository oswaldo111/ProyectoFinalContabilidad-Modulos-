package com.sistema.modulos.ventas.Model;

import java.time.LocalDate;

public class Venta {

    private int idFactura;
    private int idEmpresa;
    private int idEntidad;

    private String tipoDocumento;
    private String numeroDocumento;

    private LocalDate fechaEmision;
    private LocalDate fechaVencimiento;

    private String formaPago;

    private double montoGravado;
    private double montoIVA;
    private double montoExento;
    private double montoTotal;

    private double saldoPendiente;

    private String estadoPago;

    public Venta() {
    }

    public Venta(int idFactura, int idEmpresa, int idEntidad,
                 String tipoDocumento, String numeroDocumento,
                 LocalDate fechaEmision, LocalDate fechaVencimiento,
                 String formaPago, double montoGravado,
                 double montoIVA, double montoExento,
                 double montoTotal, double saldoPendiente,
                 String estadoPago) {

        this.idFactura = idFactura;
        this.idEmpresa = idEmpresa;
        this.idEntidad = idEntidad;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.fechaEmision = fechaEmision;
        this.fechaVencimiento = fechaVencimiento;
        this.formaPago = formaPago;
        this.montoGravado = montoGravado;
        this.montoIVA = montoIVA;
        this.montoExento = montoExento;
        this.montoTotal = montoTotal;
        this.saldoPendiente = saldoPendiente;
        this.estadoPago = estadoPago;
    }

    // GETTERS Y SETTERS

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

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }

    public double getMontoGravado() {
        return montoGravado;
    }

    public void setMontoGravado(double montoGravado) {
        this.montoGravado = montoGravado;
    }

    public double getMontoIVA() {
        return montoIVA;
    }

    public void setMontoIVA(double montoIVA) {
        this.montoIVA = montoIVA;
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

    public double getSaldoPendiente() {
        return saldoPendiente;
    }

    public void setSaldoPendiente(double saldoPendiente) {
        this.saldoPendiente = saldoPendiente;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }
}