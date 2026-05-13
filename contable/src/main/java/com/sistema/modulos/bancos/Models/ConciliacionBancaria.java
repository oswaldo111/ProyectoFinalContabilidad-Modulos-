package com.sistema.modulos.bancos.Models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * POJO que representa una conciliación bancaria (tabla conciliaciones_bancarias).
 * Registra el cruce entre el saldo del sistema y el estado de cuenta del banco.
 * Módulo de Bancos — Grupo 4
 */
public class ConciliacionBancaria {
    private int idConciliacion;
    private int idEmpresa;
    private int idBanco;
    private int mes;
    private int anio;
    private BigDecimal saldoSegunBanco;
    private BigDecimal saldoSegunLibros;
    private BigDecimal diferencia;
    private String estado;              // PENDIENTE, CONCILIADO, CON_DIFERENCIA
    private LocalDateTime fechaConciliacion;
    private String observaciones;

    // Campo extra para display
    private String nombreBanco;

    public ConciliacionBancaria() {
        this.saldoSegunBanco = BigDecimal.ZERO;
        this.saldoSegunLibros = BigDecimal.ZERO;
        this.diferencia = BigDecimal.ZERO;
        this.estado = "PENDIENTE";
    }

    /**
     * Calcula la diferencia y determina el estado de la conciliación.
     */
    public void calcular() {
        this.diferencia = saldoSegunLibros.subtract(saldoSegunBanco);
        if (diferencia.compareTo(BigDecimal.ZERO) == 0) {
            this.estado = "CONCILIADO";
        } else {
            this.estado = "CON_DIFERENCIA";
        }
    }

    /**
     * Indica si la conciliación está cuadrada (diferencia = 0).
     */
    public boolean estaConciliado() {
        return diferencia.compareTo(BigDecimal.ZERO) == 0;
    }

    // Getters y Setters
    public int getIdConciliacion() { return idConciliacion; }
    public void setIdConciliacion(int idConciliacion) { this.idConciliacion = idConciliacion; }

    public int getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(int idEmpresa) { this.idEmpresa = idEmpresa; }

    public int getIdBanco() { return idBanco; }
    public void setIdBanco(int idBanco) { this.idBanco = idBanco; }

    public int getMes() { return mes; }
    public void setMes(int mes) { this.mes = mes; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public BigDecimal getSaldoSegunBanco() { return saldoSegunBanco; }
    public void setSaldoSegunBanco(BigDecimal saldoSegunBanco) { this.saldoSegunBanco = saldoSegunBanco; }

    public BigDecimal getSaldoSegunLibros() { return saldoSegunLibros; }
    public void setSaldoSegunLibros(BigDecimal saldoSegunLibros) { this.saldoSegunLibros = saldoSegunLibros; }

    public BigDecimal getDiferencia() { return diferencia; }
    public void setDiferencia(BigDecimal diferencia) { this.diferencia = diferencia; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaConciliacion() { return fechaConciliacion; }
    public void setFechaConciliacion(LocalDateTime fechaConciliacion) { this.fechaConciliacion = fechaConciliacion; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getNombreBanco() { return nombreBanco; }
    public void setNombreBanco(String nombreBanco) { this.nombreBanco = nombreBanco; }
}
