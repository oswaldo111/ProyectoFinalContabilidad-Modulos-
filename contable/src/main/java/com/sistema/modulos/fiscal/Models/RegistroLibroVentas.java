package com.sistema.modulos.fiscal.Models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RegistroLibroVentas {
    private Long idFactura;
    private LocalDate fechaEmision;
    private String numeroDocumento;
    private String tipoDocumento;
    private String nombreCliente;
    private String nrc;
    private BigDecimal ventasExentas;
    private BigDecimal ventasGravadas;
    private BigDecimal debitoFiscal;
    private BigDecimal ventaTotal;

    public RegistroLibroVentas() {}

    // Getters y Setters
    public Long getIdFactura() { return idFactura; }
    public void setIdFactura(Long idFactura) { this.idFactura = idFactura; }

    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNombreCliente() { return nombreCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }

    public String getNrc() { return nrc; }
    public void setNrc(String nrc) { this.nrc = nrc; }

    public BigDecimal getVentasExentas() { return ventasExentas; }
    public void setVentasExentas(BigDecimal ventasExentas) { this.ventasExentas = ventasExentas; }

    public BigDecimal getVentasGravadas() { return ventasGravadas; }
    public void setVentasGravadas(BigDecimal ventasGravadas) { this.ventasGravadas = ventasGravadas; }

    public BigDecimal getDebitoFiscal() { return debitoFiscal; }
    public void setDebitoFiscal(BigDecimal debitoFiscal) { this.debitoFiscal = debitoFiscal; }

    public BigDecimal getVentaTotal() { return ventaTotal; }
    public void setVentaTotal(BigDecimal ventaTotal) { this.ventaTotal = ventaTotal; }

    @Override
    public String toString() {
        return "Venta #" + numeroDocumento + " - " + nombreCliente + " - Total: " + ventaTotal;
    }
}
