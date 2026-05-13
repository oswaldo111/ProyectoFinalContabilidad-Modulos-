package com.sistema.modulos.bancos.Models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * POJO que representa un movimiento bancario (tabla movimientos_bancarios).
 * Tipos válidos: INGRESO, EGRESO, CHEQUE, TRANSFERENCIA
 * Módulo de Bancos — Grupo 4
 */
public class MovimientoBancario {
    private int idMovimiento;
    private int idEmpresa;
    private int idBanco;
    private String tipoMovimiento;      // INGRESO, EGRESO, CHEQUE, TRANSFERENCIA
    private BigDecimal monto;
    private LocalDateTime fecha;
    private String descripcion;
    private boolean conciliado;
    private String numeroCheque;        // solo para tipo CHEQUE
    private String beneficiario;        // para CHEQUE y TRANSFERENCIA
    private String numeroReferencia;    // para TRANSFERENCIA
    private LocalDateTime fechaConciliacion;

    // Campo extra para display en tablas (viene de JOIN)
    private String nombreBanco;

    public MovimientoBancario() {
        this.monto = BigDecimal.ZERO;
        this.conciliado = false;
    }

    // Getters y Setters
    public int getIdMovimiento() { return idMovimiento; }
    public void setIdMovimiento(int idMovimiento) { this.idMovimiento = idMovimiento; }

    public int getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(int idEmpresa) { this.idEmpresa = idEmpresa; }

    public int getIdBanco() { return idBanco; }
    public void setIdBanco(int idBanco) { this.idBanco = idBanco; }

    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public boolean isConciliado() { return conciliado; }
    public void setConciliado(boolean conciliado) { this.conciliado = conciliado; }

    public String getNumeroCheque() { return numeroCheque; }
    public void setNumeroCheque(String numeroCheque) { this.numeroCheque = numeroCheque; }

    public String getBeneficiario() { return beneficiario; }
    public void setBeneficiario(String beneficiario) { this.beneficiario = beneficiario; }

    public String getNumeroReferencia() { return numeroReferencia; }
    public void setNumeroReferencia(String numeroReferencia) { this.numeroReferencia = numeroReferencia; }

    public LocalDateTime getFechaConciliacion() { return fechaConciliacion; }
    public void setFechaConciliacion(LocalDateTime fechaConciliacion) { this.fechaConciliacion = fechaConciliacion; }

    public String getNombreBanco() { return nombreBanco; }
    public void setNombreBanco(String nombreBanco) { this.nombreBanco = nombreBanco; }

    @Override
    public String toString() {
        return tipoMovimiento + " #" + idMovimiento + " - $" + monto;
    }
}
