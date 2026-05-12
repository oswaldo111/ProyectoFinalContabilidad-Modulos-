package com.sistema.modulos.fiscal.Models;

import java.math.BigDecimal;

public class LiquidacionIva {
    private int mes;
    private int anio;
    private BigDecimal totalVentasGravadas;
    private BigDecimal totalVentasExentas;
    private BigDecimal totalDebitoFiscal;
    private BigDecimal totalComprasGravadas;
    private BigDecimal totalComprasExentas;
    private BigDecimal totalCreditoFiscal;
    private BigDecimal remanenteMesAnterior;
    private BigDecimal impuestoAPagar;
    private BigDecimal remanenteAFavor;

    public LiquidacionIva() {
        this.totalVentasGravadas = BigDecimal.ZERO;
        this.totalVentasExentas = BigDecimal.ZERO;
        this.totalDebitoFiscal = BigDecimal.ZERO;
        this.totalComprasGravadas = BigDecimal.ZERO;
        this.totalComprasExentas = BigDecimal.ZERO;
        this.totalCreditoFiscal = BigDecimal.ZERO;
        this.remanenteMesAnterior = BigDecimal.ZERO;
        this.impuestoAPagar = BigDecimal.ZERO;
        this.remanenteAFavor = BigDecimal.ZERO;
    }

    /**
     * Calcula el resultado de la liquidación del período.
     * Si Débito > (Crédito + Remanente) → Impuesto a Pagar
     * Si (Crédito + Remanente) > Débito → Remanente a Favor
     */
    public void calcular() {
        BigDecimal creditoTotal = totalCreditoFiscal.add(remanenteMesAnterior);
        BigDecimal diferencia = totalDebitoFiscal.subtract(creditoTotal);

        if (diferencia.compareTo(BigDecimal.ZERO) > 0) {
            this.impuestoAPagar = diferencia;
            this.remanenteAFavor = BigDecimal.ZERO;
        } else {
            this.impuestoAPagar = BigDecimal.ZERO;
            this.remanenteAFavor = diferencia.abs();
        }
    }

    /**
     * Indica si el resultado es un impuesto a pagar (true) o un remanente a favor (false).
     */
    public boolean hayImpuestoAPagar() {
        return impuestoAPagar.compareTo(BigDecimal.ZERO) > 0;
    }

    // Getters y Setters
    public int getMes() { return mes; }
    public void setMes(int mes) { this.mes = mes; }

    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    public BigDecimal getTotalVentasGravadas() { return totalVentasGravadas; }
    public void setTotalVentasGravadas(BigDecimal totalVentasGravadas) { this.totalVentasGravadas = totalVentasGravadas; }

    public BigDecimal getTotalVentasExentas() { return totalVentasExentas; }
    public void setTotalVentasExentas(BigDecimal totalVentasExentas) { this.totalVentasExentas = totalVentasExentas; }

    public BigDecimal getTotalDebitoFiscal() { return totalDebitoFiscal; }
    public void setTotalDebitoFiscal(BigDecimal totalDebitoFiscal) { this.totalDebitoFiscal = totalDebitoFiscal; }

    public BigDecimal getTotalComprasGravadas() { return totalComprasGravadas; }
    public void setTotalComprasGravadas(BigDecimal totalComprasGravadas) { this.totalComprasGravadas = totalComprasGravadas; }

    public BigDecimal getTotalComprasExentas() { return totalComprasExentas; }
    public void setTotalComprasExentas(BigDecimal totalComprasExentas) { this.totalComprasExentas = totalComprasExentas; }

    public BigDecimal getTotalCreditoFiscal() { return totalCreditoFiscal; }
    public void setTotalCreditoFiscal(BigDecimal totalCreditoFiscal) { this.totalCreditoFiscal = totalCreditoFiscal; }

    public BigDecimal getRemanenteMesAnterior() { return remanenteMesAnterior; }
    public void setRemanenteMesAnterior(BigDecimal remanenteMesAnterior) { this.remanenteMesAnterior = remanenteMesAnterior; }

    public BigDecimal getImpuestoAPagar() { return impuestoAPagar; }
    public void setImpuestoAPagar(BigDecimal impuestoAPagar) { this.impuestoAPagar = impuestoAPagar; }

    public BigDecimal getRemanenteAFavor() { return remanenteAFavor; }
    public void setRemanenteAFavor(BigDecimal remanenteAFavor) { this.remanenteAFavor = remanenteAFavor; }
}
