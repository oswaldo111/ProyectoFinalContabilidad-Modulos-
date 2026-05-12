package com.sistema.modulos.fiscal.Models;

import java.math.BigDecimal;
import java.time.LocalDate;

public class RegistroLibroCompras {
    private Long idFactura;
    private LocalDate fechaEmision;
    private String numeroDocumento;
    private String tipoDocumento;
    private String nombreProveedor;
    private String nrc;
    private BigDecimal comprasExentas;
    private BigDecimal comprasGravadas;
    private BigDecimal creditoFiscal;
    private BigDecimal compraTotal;

    public RegistroLibroCompras() {}

    // Getters y Setters
    public Long getIdFactura() { return idFactura; }
    public void setIdFactura(Long idFactura) { this.idFactura = idFactura; }

    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }

    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }

    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }

    public String getNombreProveedor() { return nombreProveedor; }
    public void setNombreProveedor(String nombreProveedor) { this.nombreProveedor = nombreProveedor; }

    public String getNrc() { return nrc; }
    public void setNrc(String nrc) { this.nrc = nrc; }

    public BigDecimal getComprasExentas() { return comprasExentas; }
    public void setComprasExentas(BigDecimal comprasExentas) { this.comprasExentas = comprasExentas; }

    public BigDecimal getComprasGravadas() { return comprasGravadas; }
    public void setComprasGravadas(BigDecimal comprasGravadas) { this.comprasGravadas = comprasGravadas; }

    public BigDecimal getCreditoFiscal() { return creditoFiscal; }
    public void setCreditoFiscal(BigDecimal creditoFiscal) { this.creditoFiscal = creditoFiscal; }

    public BigDecimal getCompraTotal() { return compraTotal; }
    public void setCompraTotal(BigDecimal compraTotal) { this.compraTotal = compraTotal; }

    @Override
    public String toString() {
        return "Compra #" + numeroDocumento + " - " + nombreProveedor + " - Total: " + compraTotal;
    }
}
