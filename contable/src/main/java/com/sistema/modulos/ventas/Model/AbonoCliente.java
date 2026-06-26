package com.sistema.modulos.ventas.Model;

import java.time.LocalDateTime;

public class AbonoCliente {

    private int idAbono;
    private int idEmpresa;
    private int idFactura;
    private LocalDateTime fecha;
    private double monto;
    private String metodoPago;
    private String referencia;

    public AbonoCliente() {
    }

    public AbonoCliente(int idAbono, int idEmpresa, int idFactura, LocalDateTime fecha,
            double monto, String metodoPago, String referencia) {
        this.idAbono = idAbono;
        this.idEmpresa = idEmpresa;
        this.idFactura = idFactura;
        this.fecha = fecha;
        this.monto = monto;
        this.metodoPago = metodoPago;
        this.referencia = referencia;
    }

    public int getIdAbono() {
        return idAbono;
    }

    public void setIdAbono(int idAbono) {
        this.idAbono = idAbono;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public int getIdFactura() {
        return idFactura;
    }

    public void setIdFactura(int idFactura) {
        this.idFactura = idFactura;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
}
