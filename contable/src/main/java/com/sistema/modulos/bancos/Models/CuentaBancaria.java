package com.sistema.modulos.bancos.Models;

import java.math.BigDecimal;

/**
 * POJO que representa una cuenta bancaria (tabla cuentas_bancarias).
 * Módulo de Bancos — Grupo 4
 */
public class CuentaBancaria {
    private int idBanco;
    private int idEmpresa;
    private String nombreBanco;
    private String numeroCuenta;
    private String tipoCuenta;      // "AHORRO" o "CORRIENTE"
    private BigDecimal saldoBanco;
    private boolean estado;         // true = activa, false = inactiva

    public CuentaBancaria() {
        this.saldoBanco = BigDecimal.ZERO;
        this.tipoCuenta = "CORRIENTE";
        this.estado = true;
    }

    // Getters y Setters
    public int getIdBanco() { return idBanco; }
    public void setIdBanco(int idBanco) { this.idBanco = idBanco; }

    public int getIdEmpresa() { return idEmpresa; }
    public void setIdEmpresa(int idEmpresa) { this.idEmpresa = idEmpresa; }

    public String getNombreBanco() { return nombreBanco; }
    public void setNombreBanco(String nombreBanco) { this.nombreBanco = nombreBanco; }

    public String getNumeroCuenta() { return numeroCuenta; }
    public void setNumeroCuenta(String numeroCuenta) { this.numeroCuenta = numeroCuenta; }

    public String getTipoCuenta() { return tipoCuenta; }
    public void setTipoCuenta(String tipoCuenta) { this.tipoCuenta = tipoCuenta; }

    public BigDecimal getSaldoBanco() { return saldoBanco; }
    public void setSaldoBanco(BigDecimal saldoBanco) { this.saldoBanco = saldoBanco; }

    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }

    @Override
    public String toString() {
        return nombreBanco + " - " + numeroCuenta;
    }
}
