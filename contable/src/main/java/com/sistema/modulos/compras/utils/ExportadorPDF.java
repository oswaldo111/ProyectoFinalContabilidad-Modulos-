package com.sistema.modulos.compras.utils;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.FileOutputStream;

public class ExportadorPDF {
    
    public static boolean exportar(JTable tabla, String titulo, String nombreArchivo) {
        TableModel model = tabla.getModel();
        
        try {
            Document document = new Document(PageSize.A4.rotate());
            String nombreFinal = nombreArchivo.replaceAll("[/:?\"<>|*]", "_");
            PdfWriter.getInstance(document, new FileOutputStream(nombreFinal + ".pdf"));
            document.open();
            
            // Título
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph title = new Paragraph(titulo, titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" "));
            
            // Fecha de generación
            Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Paragraph date = new Paragraph("Generado: " + new java.util.Date(), dateFont);
            date.setAlignment(Element.ALIGN_RIGHT);
            document.add(date);
            document.add(new Paragraph(" "));
            
            // Tabla
            PdfPTable pdfTable = new PdfPTable(model.getColumnCount());
            pdfTable.setWidthPercentage(100);
            
            // Encabezados
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
            for (int col = 0; col < model.getColumnCount(); col++) {
                PdfPCell header = new PdfPCell(new Phrase(model.getColumnName(col), headerFont));
                header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                header.setHorizontalAlignment(Element.ALIGN_CENTER);
                pdfTable.addCell(header);
            }
            
            // Datos
            Font cellFont = FontFactory.getFont(FontFactory.HELVETICA, 8);
            for (int row = 0; row < model.getRowCount(); row++) {
                for (int col = 0; col < model.getColumnCount(); col++) {
                    Object value = model.getValueAt(row, col);
                    String text = value != null ? value.toString() : "";
                    PdfPCell cell = new PdfPCell(new Phrase(text, cellFont));
                    if (value instanceof Number) {
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    }
                    pdfTable.addCell(cell);
                }
            }
            
            document.add(pdfTable);
            document.close();
            
            JOptionPane.showMessageDialog(null, 
                "Reporte exportado exitosamente a:\n" + System.getProperty("user.dir") + "\\" + nombreFinal + ".pdf",
                "Exportar PDF", JOptionPane.INFORMATION_MESSAGE);
            return true;
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Error al exportar a PDF: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}