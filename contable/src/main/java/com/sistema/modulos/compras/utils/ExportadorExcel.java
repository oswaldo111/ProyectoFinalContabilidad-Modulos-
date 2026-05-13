package com.sistema.modulos.compras.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.FileOutputStream;
import java.io.IOException;

public class ExportadorExcel {
    
    public static boolean exportar(JTable tabla, String titulo, String nombreArchivo) {
        TableModel model = tabla.getModel();
        
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet(titulo.length() > 31 ? titulo.substring(0, 31) : titulo);
            
            // Crear estilo para encabezados
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            
            // Crear fila de encabezados
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < model.getColumnCount(); col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(model.getColumnName(col));
                cell.setCellStyle(headerStyle);
            }
            
            // Crear filas de datos
            for (int row = 0; row < model.getRowCount(); row++) {
                Row dataRow = sheet.createRow(row + 1);
                for (int col = 0; col < model.getColumnCount(); col++) {
                    Object value = model.getValueAt(row, col);
                    Cell cell = dataRow.createCell(col);
                    if (value != null) {
                        if (value instanceof Number) {
                            cell.setCellValue(((Number) value).doubleValue());
                        } else {
                            cell.setCellValue(value.toString());
                        }
                    } else {
                        cell.setCellValue("");
                    }
                }
            }
            
            // Autoajustar columnas
            for (int col = 0; col < model.getColumnCount(); col++) {
                sheet.autoSizeColumn(col);
            }
            
            // Guardar archivo
            String nombreFinal = nombreArchivo.replaceAll("[/:?\"<>|*]", "_");
            try (FileOutputStream fileOut = new FileOutputStream(nombreFinal + ".xlsx")) {
                workbook.write(fileOut);
            }
            
            JOptionPane.showMessageDialog(null, 
                "Reporte exportado exitosamente a:\n" + System.getProperty("user.dir") + "\\" + nombreFinal + ".xlsx",
                "Exportar Excel", JOptionPane.INFORMATION_MESSAGE);
            return true;
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, 
                "Error al exportar: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}