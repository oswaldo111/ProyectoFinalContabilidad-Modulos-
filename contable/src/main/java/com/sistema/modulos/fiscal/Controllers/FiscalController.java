package com.sistema.modulos.fiscal.Controllers;

import com.sistema.modulos.fiscal.Models.LiquidacionIva;
import com.sistema.modulos.fiscal.Models.RegistroLibroCompras;
import com.sistema.modulos.fiscal.Models.RegistroLibroVentas;
import com.sistema.modulos.fiscal.Services.ExportadorCSV;
import com.sistema.modulos.fiscal.Services.LibroComprasService;
import com.sistema.modulos.fiscal.Services.LibroVentasService;
import com.sistema.modulos.fiscal.Services.LiquidacionService;
import com.sistema.modulos.fiscal.Views.LibroComprasView;
import com.sistema.modulos.fiscal.Views.LibroVentasView;
import com.sistema.modulos.fiscal.Views.LiquidacionView;

import javax.swing.*;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class FiscalController {

    private final LibroVentasView ventasView;
    private final LibroComprasView comprasView;
    private final LiquidacionView liquidacionView;

    private final LibroVentasService libroVentasService;
    private final LibroComprasService libroComprasService;
    private final LiquidacionService liquidacionService;
    private final ExportadorCSV exportadorCSV;

    public FiscalController(LibroVentasView ventasView, LibroComprasView comprasView, LiquidacionView liquidacionView) {
        this.ventasView = ventasView;
        this.comprasView = comprasView;
        this.liquidacionView = liquidacionView;

        this.libroVentasService = new LibroVentasService();
        this.libroComprasService = new LibroComprasService();
        this.liquidacionService = new LiquidacionService();
        this.exportadorCSV = new ExportadorCSV();
    }

    // ==================== LIBRO DE VENTAS ====================

    /**
     * Carga el Libro de Ventas IVA en la tabla de la vista.
     */
    public void cargarLibroVentas() {
        try {
            int mes = ventasView.getMes();
            int anio = ventasView.getAnio();

            List<RegistroLibroVentas> registros = libroVentasService.generarLibroVentas(mes, anio);
            ventasView.cargarDatos(registros);

            Map<String, BigDecimal> totales = libroVentasService.obtenerTotalesVentas(mes, anio);
            ventasView.mostrarTotales(totales);

            if (registros.isEmpty()) {
                ventasView.mostrarMensaje("No se encontraron ventas para el período seleccionado.",
                        "Información", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            ventasView.mostrarMensaje("Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Exporta el Libro de Ventas a un archivo CSV.
     */
    public void exportarLibroVentasCSV() {
        try {
            int mes = ventasView.getMes();
            int anio = ventasView.getAnio();

            List<RegistroLibroVentas> registros = libroVentasService.generarLibroVentas(mes, anio);

            if (registros.isEmpty()) {
                ventasView.mostrarMensaje("No hay datos para exportar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar Libro de Ventas CSV");
            fileChooser.setSelectedFile(new File("LibroVentas_" + anio + "_" + String.format("%02d", mes) + ".csv"));

            int resultado = fileChooser.showSaveDialog(ventasView);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                File archivo = fileChooser.getSelectedFile();
                exportadorCSV.exportarLibroVentas(registros, mes, anio, archivo);
                ventasView.mostrarMensaje("Archivo exportado exitosamente:\n" + archivo.getAbsolutePath(),
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            ventasView.mostrarMensaje("Error al exportar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==================== LIBRO DE COMPRAS ====================

    /**
     * Carga el Libro de Compras IVA en la tabla de la vista.
     */
    public void cargarLibroCompras() {
        try {
            int mes = comprasView.getMes();
            int anio = comprasView.getAnio();

            List<RegistroLibroCompras> registros = libroComprasService.generarLibroCompras(mes, anio);
            comprasView.cargarDatos(registros);

            Map<String, BigDecimal> totales = libroComprasService.obtenerTotalesCompras(mes, anio);
            comprasView.mostrarTotales(totales);

            if (registros.isEmpty()) {
                comprasView.mostrarMensaje("No se encontraron compras para el período seleccionado.",
                        "Información", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            comprasView.mostrarMensaje("Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Exporta el Libro de Compras a un archivo CSV.
     */
    public void exportarLibroComprasCSV() {
        try {
            int mes = comprasView.getMes();
            int anio = comprasView.getAnio();

            List<RegistroLibroCompras> registros = libroComprasService.generarLibroCompras(mes, anio);

            if (registros.isEmpty()) {
                comprasView.mostrarMensaje("No hay datos para exportar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar Libro de Compras CSV");
            fileChooser.setSelectedFile(new File("LibroCompras_" + anio + "_" + String.format("%02d", mes) + ".csv"));

            int resultado = fileChooser.showSaveDialog(comprasView);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                File archivo = fileChooser.getSelectedFile();
                exportadorCSV.exportarLibroCompras(registros, mes, anio, archivo);
                comprasView.mostrarMensaje("Archivo exportado exitosamente:\n" + archivo.getAbsolutePath(),
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            comprasView.mostrarMensaje("Error al exportar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ==================== LIQUIDACIÓN ====================

    /**
     * Calcula la liquidación del período fiscal.
     */
    public void calcularLiquidacion() {
        try {
            int mes = liquidacionView.getMes();
            int anio = liquidacionView.getAnio();
            BigDecimal remanente = liquidacionView.getRemanenteMesAnterior();

            LiquidacionIva liquidacion = liquidacionService.calcularLiquidacion(mes, anio, remanente);
            liquidacionView.mostrarResultado(liquidacion);

        } catch (Exception e) {
            liquidacionView.mostrarMensaje("Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Exporta la liquidación del período a un archivo CSV.
     */
    public void exportarLiquidacionCSV() {
        try {
            int mes = liquidacionView.getMes();
            int anio = liquidacionView.getAnio();
            BigDecimal remanente = liquidacionView.getRemanenteMesAnterior();

            LiquidacionIva liquidacion = liquidacionService.calcularLiquidacion(mes, anio, remanente);

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Guardar Liquidación IVA CSV");
            fileChooser.setSelectedFile(new File("Liquidacion_IVA_" + anio + "_" + String.format("%02d", mes) + ".csv"));

            int resultado = fileChooser.showSaveDialog(liquidacionView);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                File archivo = fileChooser.getSelectedFile();
                exportadorCSV.exportarLiquidacion(liquidacion, archivo);
                liquidacionView.mostrarMensaje("Archivo exportado exitosamente:\n" + archivo.getAbsolutePath(),
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception e) {
            liquidacionView.mostrarMensaje("Error al exportar: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
