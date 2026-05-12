package com.sistema.modulos.fiscal.Services;

import com.sistema.modulos.fiscal.Models.LiquidacionIva;
import com.sistema.modulos.fiscal.Models.RegistroLibroCompras;
import com.sistema.modulos.fiscal.Models.RegistroLibroVentas;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

public class ExportadorCSV {

    private static final DecimalFormat FORMATO_MONTO = new DecimalFormat("#,##0.00");
    private static final String SEPARADOR = ",";

    /**
     * Exporta el Libro de Ventas IVA a un archivo CSV.
     */
    public File exportarLibroVentas(List<RegistroLibroVentas> registros, int mes, int anio, File destino) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(destino))) {
            // Encabezado del reporte
            writer.write("LIBRO DE VENTAS IVA");
            writer.newLine();
            writer.write("Período: " + obtenerNombreMes(mes) + " " + anio);
            writer.newLine();
            writer.newLine();

            // Columnas
            writer.write("N°" + SEPARADOR
                    + "Fecha" + SEPARADOR
                    + "N° Documento" + SEPARADOR
                    + "Tipo Doc" + SEPARADOR
                    + "Cliente" + SEPARADOR
                    + "NRC" + SEPARADOR
                    + "Ventas Exentas" + SEPARADOR
                    + "Ventas Gravadas" + SEPARADOR
                    + "Débito Fiscal" + SEPARADOR
                    + "Total");
            writer.newLine();

            // Datos
            BigDecimal totalExentas = BigDecimal.ZERO;
            BigDecimal totalGravadas = BigDecimal.ZERO;
            BigDecimal totalDebito = BigDecimal.ZERO;
            BigDecimal totalGeneral = BigDecimal.ZERO;

            int correlativo = 1;
            for (RegistroLibroVentas r : registros) {
                writer.write(correlativo + SEPARADOR
                        + r.getFechaEmision() + SEPARADOR
                        + limpiar(r.getNumeroDocumento()) + SEPARADOR
                        + limpiar(r.getTipoDocumento()) + SEPARADOR
                        + limpiar(r.getNombreCliente()) + SEPARADOR
                        + limpiar(r.getNrc()) + SEPARADOR
                        + FORMATO_MONTO.format(r.getVentasExentas()) + SEPARADOR
                        + FORMATO_MONTO.format(r.getVentasGravadas()) + SEPARADOR
                        + FORMATO_MONTO.format(r.getDebitoFiscal()) + SEPARADOR
                        + FORMATO_MONTO.format(r.getVentaTotal()));
                writer.newLine();

                totalExentas = totalExentas.add(r.getVentasExentas());
                totalGravadas = totalGravadas.add(r.getVentasGravadas());
                totalDebito = totalDebito.add(r.getDebitoFiscal());
                totalGeneral = totalGeneral.add(r.getVentaTotal());
                correlativo++;
            }

            // Fila de totales
            writer.newLine();
            writer.write("TOTALES" + SEPARADOR
                    + SEPARADOR + SEPARADOR + SEPARADOR + SEPARADOR + SEPARADOR
                    + FORMATO_MONTO.format(totalExentas) + SEPARADOR
                    + FORMATO_MONTO.format(totalGravadas) + SEPARADOR
                    + FORMATO_MONTO.format(totalDebito) + SEPARADOR
                    + FORMATO_MONTO.format(totalGeneral));
            writer.newLine();
        }

        return destino;
    }

    /**
     * Exporta el Libro de Compras IVA a un archivo CSV.
     */
    public File exportarLibroCompras(List<RegistroLibroCompras> registros, int mes, int anio, File destino) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(destino))) {
            // Encabezado del reporte
            writer.write("LIBRO DE COMPRAS IVA");
            writer.newLine();
            writer.write("Período: " + obtenerNombreMes(mes) + " " + anio);
            writer.newLine();
            writer.newLine();

            // Columnas
            writer.write("N°" + SEPARADOR
                    + "Fecha" + SEPARADOR
                    + "N° Documento" + SEPARADOR
                    + "Tipo Doc" + SEPARADOR
                    + "Proveedor" + SEPARADOR
                    + "NRC" + SEPARADOR
                    + "Compras Exentas" + SEPARADOR
                    + "Compras Gravadas" + SEPARADOR
                    + "Crédito Fiscal" + SEPARADOR
                    + "Total");
            writer.newLine();

            // Datos
            BigDecimal totalExentas = BigDecimal.ZERO;
            BigDecimal totalGravadas = BigDecimal.ZERO;
            BigDecimal totalCredito = BigDecimal.ZERO;
            BigDecimal totalGeneral = BigDecimal.ZERO;

            int correlativo = 1;
            for (RegistroLibroCompras r : registros) {
                writer.write(correlativo + SEPARADOR
                        + r.getFechaEmision() + SEPARADOR
                        + limpiar(r.getNumeroDocumento()) + SEPARADOR
                        + limpiar(r.getTipoDocumento()) + SEPARADOR
                        + limpiar(r.getNombreProveedor()) + SEPARADOR
                        + limpiar(r.getNrc()) + SEPARADOR
                        + FORMATO_MONTO.format(r.getComprasExentas()) + SEPARADOR
                        + FORMATO_MONTO.format(r.getComprasGravadas()) + SEPARADOR
                        + FORMATO_MONTO.format(r.getCreditoFiscal()) + SEPARADOR
                        + FORMATO_MONTO.format(r.getCompraTotal()));
                writer.newLine();

                totalExentas = totalExentas.add(r.getComprasExentas());
                totalGravadas = totalGravadas.add(r.getComprasGravadas());
                totalCredito = totalCredito.add(r.getCreditoFiscal());
                totalGeneral = totalGeneral.add(r.getCompraTotal());
                correlativo++;
            }

            // Fila de totales
            writer.newLine();
            writer.write("TOTALES" + SEPARADOR
                    + SEPARADOR + SEPARADOR + SEPARADOR + SEPARADOR + SEPARADOR
                    + FORMATO_MONTO.format(totalExentas) + SEPARADOR
                    + FORMATO_MONTO.format(totalGravadas) + SEPARADOR
                    + FORMATO_MONTO.format(totalCredito) + SEPARADOR
                    + FORMATO_MONTO.format(totalGeneral));
            writer.newLine();
        }

        return destino;
    }

    /**
     * Exporta la Liquidación de IVA a un archivo CSV.
     */
    public File exportarLiquidacion(LiquidacionIva liq, File destino) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(destino))) {
            writer.write("LIQUIDACIÓN DE IVA");
            writer.newLine();
            writer.write("Período: " + obtenerNombreMes(liq.getMes()) + " " + liq.getAnio());
            writer.newLine();
            writer.newLine();

            // Sección Ventas
            writer.write("=== VENTAS ===");
            writer.newLine();
            writer.write("Ventas Gravadas" + SEPARADOR + FORMATO_MONTO.format(liq.getTotalVentasGravadas()));
            writer.newLine();
            writer.write("Ventas Exentas" + SEPARADOR + FORMATO_MONTO.format(liq.getTotalVentasExentas()));
            writer.newLine();
            writer.write("DÉBITO FISCAL (IVA Cobrado)" + SEPARADOR + FORMATO_MONTO.format(liq.getTotalDebitoFiscal()));
            writer.newLine();
            writer.newLine();

            // Sección Compras
            writer.write("=== COMPRAS ===");
            writer.newLine();
            writer.write("Compras Gravadas" + SEPARADOR + FORMATO_MONTO.format(liq.getTotalComprasGravadas()));
            writer.newLine();
            writer.write("Compras Exentas" + SEPARADOR + FORMATO_MONTO.format(liq.getTotalComprasExentas()));
            writer.newLine();
            writer.write("CRÉDITO FISCAL (IVA Pagado)" + SEPARADOR + FORMATO_MONTO.format(liq.getTotalCreditoFiscal()));
            writer.newLine();
            writer.newLine();

            // Sección Resultado
            writer.write("=== RESULTADO ===");
            writer.newLine();
            writer.write("Débito Fiscal" + SEPARADOR + FORMATO_MONTO.format(liq.getTotalDebitoFiscal()));
            writer.newLine();
            writer.write("(-) Crédito Fiscal" + SEPARADOR + FORMATO_MONTO.format(liq.getTotalCreditoFiscal()));
            writer.newLine();
            writer.write("(-) Remanente Mes Anterior" + SEPARADOR + FORMATO_MONTO.format(liq.getRemanenteMesAnterior()));
            writer.newLine();
            writer.write("────────────────────────");
            writer.newLine();

            if (liq.hayImpuestoAPagar()) {
                writer.write("IMPUESTO A PAGAR" + SEPARADOR + FORMATO_MONTO.format(liq.getImpuestoAPagar()));
            } else {
                writer.write("REMANENTE A FAVOR" + SEPARADOR + FORMATO_MONTO.format(liq.getRemanenteAFavor()));
            }
            writer.newLine();
        }

        return destino;
    }

    /**
     * Limpia un texto para que no rompa el formato CSV (elimina comas internas).
     */
    private String limpiar(String texto) {
        if (texto == null) return "";
        return texto.replace(",", " ").replace("\"", "'");
    }

    /**
     * Retorna el nombre del mes en español.
     */
    private String obtenerNombreMes(int mes) {
        String[] meses = {"", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        return (mes >= 1 && mes <= 12) ? meses[mes] : "Desconocido";
    }
}
