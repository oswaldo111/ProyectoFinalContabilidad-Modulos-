package com.sistema.modulos.compras.Services;

import com.sistema.modulos.compras.DAO.ReporteCompraIVADAO;
import com.sistema.modulos.compras.Models.ReporteCompraIVA;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public class ReporteService {
    
    private ReporteCompraIVADAO reporteDAO;
    
    public ReporteService() {
        this.reporteDAO = new ReporteCompraIVADAO();
    }
    
    public List<ReporteCompraIVA> generarLibroCompras(Date fechaInicio, Date fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Debe seleccionar un rango de fechas");
        }
        if (fechaInicio.after(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser mayor a la fecha fin");
        }
        return reporteDAO.obtenerLibroCompras(fechaInicio, fechaFin);
    }
    
    public BigDecimal sumarMontoGravado(List<ReporteCompraIVA> reportes) {
        return reportes.stream()
            .map(ReporteCompraIVA::getMontoGravado)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public BigDecimal sumarMontoIVA(List<ReporteCompraIVA> reportes) {
        return reportes.stream()
            .map(ReporteCompraIVA::getMontoIva)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    public BigDecimal sumarMontoTotal(List<ReporteCompraIVA> reportes) {
        return reportes.stream()
            .map(ReporteCompraIVA::getMontoTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}