package com.sistema.modulos.compras.Controllers;

import com.sistema.modulos.compras.Models.ReporteCompraIVA;
import com.sistema.modulos.compras.Services.ReporteService;

import javax.swing.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

public class ReporteController {
    
    private ReporteService reporteService;
    
    public ReporteController() {
        this.reporteService = new ReporteService();
    }
    
    public List<ReporteCompraIVA> generarLibroCompras(Date fechaInicio, Date fechaFin, JFrame parentFrame) {
        try {
            return reporteService.generarLibroCompras(fechaInicio, fechaFin);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(parentFrame, e.getMessage(), "Error de validación", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    
    public BigDecimal sumarMontoGravado(List<ReporteCompraIVA> reportes) {
        return reporteService.sumarMontoGravado(reportes);
    }
    
    public BigDecimal sumarMontoIVA(List<ReporteCompraIVA> reportes) {
        return reporteService.sumarMontoIVA(reportes);
    }
    
    public BigDecimal sumarMontoTotal(List<ReporteCompraIVA> reportes) {
        return reporteService.sumarMontoTotal(reportes);
    }
}