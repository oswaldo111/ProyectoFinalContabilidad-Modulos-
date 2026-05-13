package com.sistema.modulos.bancos.Services;

import com.sistema.modulos.bancos.Models.ConciliacionBancaria;
import com.sistema.modulos.bancos.Models.CuentaBancaria;
import com.sistema.modulos.bancos.Models.MovimientoBancario;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Exporta reportes bancarios a archivos CSV.
 * Módulo de Bancos — Grupo 4
 */
public class ExportadorCSV {

    private static final DecimalFormat FORMATO_MONTO = new DecimalFormat("#,##0.00");
    private static final String SEP = ",";
    private static final DateTimeFormatter FMT_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Exporta movimientos bancarios de una cuenta a CSV.
     */
    public File exportarMovimientos(List<MovimientoBancario> movs, int mes, int anio,
                                     String nombreBanco, File destino) throws IOException {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(destino))) {
            w.write("MOVIMIENTOS BANCARIOS"); w.newLine();
            w.write("Banco: " + nombreBanco); w.newLine();
            w.write("Período: " + obtenerNombreMes(mes) + " " + anio); w.newLine();
            w.newLine();

            w.write("N°" + SEP + "Fecha" + SEP + "Tipo" + SEP + "N° Cheque" + SEP
                    + "Beneficiario" + SEP + "Referencia" + SEP + "Descripción" + SEP
                    + "Monto" + SEP + "Conciliado");
            w.newLine();

            BigDecimal totalIngresos = BigDecimal.ZERO;
            BigDecimal totalEgresos = BigDecimal.ZERO;
            int correlativo = 1;

            for (MovimientoBancario m : movs) {
                w.write(correlativo + SEP
                        + (m.getFecha() != null ? m.getFecha().format(FMT_FECHA) : "") + SEP
                        + m.getTipoMovimiento() + SEP
                        + limpiar(m.getNumeroCheque()) + SEP
                        + limpiar(m.getBeneficiario()) + SEP
                        + limpiar(m.getNumeroReferencia()) + SEP
                        + limpiar(m.getDescripcion()) + SEP
                        + FORMATO_MONTO.format(m.getMonto()) + SEP
                        + (m.isConciliado() ? "SÍ" : "NO"));
                w.newLine();

                if ("INGRESO".equals(m.getTipoMovimiento())) {
                    totalIngresos = totalIngresos.add(m.getMonto());
                } else {
                    totalEgresos = totalEgresos.add(m.getMonto());
                }
                correlativo++;
            }

            w.newLine();
            w.write("Total Ingresos" + SEP + FORMATO_MONTO.format(totalIngresos)); w.newLine();
            w.write("Total Egresos" + SEP + FORMATO_MONTO.format(totalEgresos)); w.newLine();
            w.write("Saldo Neto" + SEP + FORMATO_MONTO.format(totalIngresos.subtract(totalEgresos))); w.newLine();
        }
        return destino;
    }

    /**
     * Exporta el reporte de disponibilidad diaria a CSV.
     */
    public File exportarDisponibilidadDiaria(List<CuentaBancaria> cuentas, File destino) throws IOException {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(destino))) {
            w.write("REPORTE DE DISPONIBILIDAD DIARIA"); w.newLine();
            w.write("Fecha: " + LocalDate.now()); w.newLine();
            w.newLine();

            w.write("N°" + SEP + "Banco" + SEP + "N° Cuenta" + SEP + "Tipo" + SEP
                    + "Saldo" + SEP + "Estado");
            w.newLine();

            BigDecimal total = BigDecimal.ZERO;
            int correlativo = 1;

            for (CuentaBancaria c : cuentas) {
                w.write(correlativo + SEP
                        + limpiar(c.getNombreBanco()) + SEP
                        + limpiar(c.getNumeroCuenta()) + SEP
                        + c.getTipoCuenta() + SEP
                        + FORMATO_MONTO.format(c.getSaldoBanco()) + SEP
                        + (c.isEstado() ? "Activa" : "Inactiva"));
                w.newLine();
                total = total.add(c.getSaldoBanco());
                correlativo++;
            }

            w.newLine();
            w.write("TOTAL DISPONIBLE" + SEP + SEP + SEP + SEP + FORMATO_MONTO.format(total));
            w.newLine();
        }
        return destino;
    }

    /**
     * Exporta la conciliación bancaria a CSV.
     */
    public File exportarConciliacion(ConciliacionBancaria conc,
                                      List<MovimientoBancario> pendientes, File destino) throws IOException {
        try (BufferedWriter w = new BufferedWriter(new FileWriter(destino))) {
            w.write("CONCILIACIÓN BANCARIA"); w.newLine();
            w.write("Banco: " + conc.getNombreBanco()); w.newLine();
            w.write("Período: " + obtenerNombreMes(conc.getMes()) + " " + conc.getAnio()); w.newLine();
            w.newLine();

            w.write("=== RESUMEN ==="); w.newLine();
            w.write("Saldo según libros" + SEP + FORMATO_MONTO.format(conc.getSaldoSegunLibros())); w.newLine();
            w.write("Saldo según banco" + SEP + FORMATO_MONTO.format(conc.getSaldoSegunBanco())); w.newLine();
            w.write("Diferencia" + SEP + FORMATO_MONTO.format(conc.getDiferencia())); w.newLine();
            w.write("Estado" + SEP + conc.getEstado()); w.newLine();
            w.newLine();

            if (pendientes != null && !pendientes.isEmpty()) {
                w.write("=== PARTIDAS PENDIENTES DE CONCILIAR ==="); w.newLine();
                w.write("N°" + SEP + "Fecha" + SEP + "Tipo" + SEP + "Descripción" + SEP + "Monto");
                w.newLine();
                int i = 1;
                for (MovimientoBancario m : pendientes) {
                    w.write(i + SEP
                            + (m.getFecha() != null ? m.getFecha().format(FMT_FECHA) : "") + SEP
                            + m.getTipoMovimiento() + SEP
                            + limpiar(m.getDescripcion()) + SEP
                            + FORMATO_MONTO.format(m.getMonto()));
                    w.newLine();
                    i++;
                }
            }
        }
        return destino;
    }

    private String limpiar(String texto) {
        if (texto == null) return "";
        return texto.replace(",", " ").replace("\"", "'");
    }

    private String obtenerNombreMes(int mes) {
        String[] meses = {"", "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        return (mes >= 1 && mes <= 12) ? meses[mes] : "Desconocido";
    }
}
