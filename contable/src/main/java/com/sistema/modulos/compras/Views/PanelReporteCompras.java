package com.sistema.modulos.compras.Views;

import com.sistema.modulos.compras.Controllers.ReporteController;
import com.sistema.modulos.compras.Models.ReporteCompraIVA;
import com.sistema.modulos.compras.utils.ExportadorExcel;
import com.sistema.modulos.compras.utils.ExportadorPDF;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class PanelReporteCompras extends JPanel {
    
    // CAMPO DEL CONTROLADOR (ya lo tenías, perfecto)
    private ReporteController controller;
    
    private JTable tablaReporte;
    private DefaultTableModel modeloTabla;
    private JTextField txtFechaInicio, txtFechaFin;
    private JButton btnGenerar, btnExportarExcel, btnExportarPDF;
    private JLabel lblTotalGravado, lblTotalIVA, lblTotalGeneral;
    
    public PanelReporteCompras() {
        // ELIMINAMOS: this.controller = new ReporteController();
        // Ahora el controller se inyectará desde fuera vía setController()
        initComponents();
        
        // FALLBACK OPCIONAL: Para pruebas independientes sin MainSimple
        if (this.controller == null) {
            this.controller = new ReporteController();
        }
    }
    
    // MÉTODO NUEVO: Setter para inyección de dependencias
    public void setController(ReporteController controller) {
        this.controller = controller;
    }
    
    // Getter opcional (útil para testing)
    public ReporteController getController() {
        return controller;
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        // Panel de filtros (superior)
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtros del Reporte"));
        
        panelFiltros.add(new JLabel("Fecha Inicio:"));
        txtFechaInicio = new JTextField(LocalDate.now().withDayOfMonth(1).toString(), 10);
        panelFiltros.add(txtFechaInicio);
        
        panelFiltros.add(new JLabel("Fecha Fin:"));
        txtFechaFin = new JTextField(LocalDate.now().toString(), 10);
        panelFiltros.add(txtFechaFin);
        
        btnGenerar = new JButton("Generar Reporte");
        btnGenerar.addActionListener(e -> generarReporte());
        panelFiltros.add(btnGenerar);
        
        // Panel de botones de exportación
        JPanel panelExportar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        panelExportar.setBorder(BorderFactory.createTitledBorder("Exportar"));
        
        btnExportarExcel = new JButton("Exportar a Excel");
        btnExportarExcel.addActionListener(e -> exportarExcel());
        btnExportarExcel.setEnabled(false);
        
        btnExportarPDF = new JButton("Exportar a PDF");
        btnExportarPDF.addActionListener(e -> exportarPDF());
        btnExportarPDF.setEnabled(false);
        
        panelExportar.add(btnExportarExcel);
        panelExportar.add(btnExportarPDF);
        
        // Panel superior combinado
        JPanel panelSuperior = new JPanel(new BorderLayout(8, 4));
        panelSuperior.add(panelFiltros, BorderLayout.WEST);
        panelSuperior.add(panelExportar, BorderLayout.EAST);
        
        // Tabla de reporte
        String[] columnas = {"Tipo", "N° Doc", "Fecha", "F.Venc", "Proveedor", "NIT", "Gravado", "IVA", "Total", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tablaReporte = new JTable(modeloTabla);
        tablaReporte.setRowHeight(22);
        for (int i = 6; i <= 8; i++) {
            tablaReporte.getColumnModel().getColumn(i).setCellRenderer(new FormatoMonedaRenderer());
        }
        
        // Ajustar anchos de columnas
        tablaReporte.getColumnModel().getColumn(0).setPreferredWidth(50);
        tablaReporte.getColumnModel().getColumn(1).setPreferredWidth(70);
        tablaReporte.getColumnModel().getColumn(2).setPreferredWidth(70);
        tablaReporte.getColumnModel().getColumn(3).setPreferredWidth(70);
        tablaReporte.getColumnModel().getColumn(4).setPreferredWidth(150);
        tablaReporte.getColumnModel().getColumn(5).setPreferredWidth(100);
        tablaReporte.getColumnModel().getColumn(6).setPreferredWidth(70);
        tablaReporte.getColumnModel().getColumn(7).setPreferredWidth(60);
        tablaReporte.getColumnModel().getColumn(8).setPreferredWidth(70);
        tablaReporte.getColumnModel().getColumn(9).setPreferredWidth(60);
        
        JScrollPane scrollTabla = new JScrollPane(tablaReporte);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Libro de Compras IVA"));
        
        // Panel de totales
        JPanel panelTotales = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 4));
        panelTotales.setBorder(BorderFactory.createTitledBorder("Totales"));
        
        panelTotales.add(new JLabel("Total Gravado:"));
        lblTotalGravado = new JLabel("$0.00");
        lblTotalGravado.setFont(new Font("Arial", Font.BOLD, 11));
        panelTotales.add(lblTotalGravado);
        
        panelTotales.add(new JLabel("Total IVA:"));
        lblTotalIVA = new JLabel("$0.00");
        lblTotalIVA.setFont(new Font("Arial", Font.BOLD, 11));
        panelTotales.add(lblTotalIVA);
        
        panelTotales.add(new JLabel("Total General:"));
        lblTotalGeneral = new JLabel("$0.00");
        lblTotalGeneral.setFont(new Font("Arial", Font.BOLD, 12));
        lblTotalGeneral.setForeground(new Color(40, 167, 69));
        panelTotales.add(lblTotalGeneral);
        
        add(panelSuperior, BorderLayout.NORTH);
        add(scrollTabla, BorderLayout.CENTER);
        add(panelTotales, BorderLayout.SOUTH);
    }
    
    // ==================== MÉTODOS CON VALIDACIÓN DE CONTROLLER ====================
    
    private void generarReporte() {
        // Validación crítica: verificar que el controller esté inyectado
        if (controller == null) {
            JOptionPane.showMessageDialog(this, 
                "Controller no inicializado. Contacte al administrador.", 
                "Error de configuración", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            Date fechaInicio = Date.valueOf(txtFechaInicio.getText().trim());
            Date fechaFin = Date.valueOf(txtFechaFin.getText().trim());
            
            List<ReporteCompraIVA> reportes = controller.generarLibroCompras(fechaInicio, fechaFin, 
                (JFrame) SwingUtilities.getWindowAncestor(this));
            
            if (reportes == null) return;
            
            modeloTabla.setRowCount(0);
            for (ReporteCompraIVA r : reportes) {
                modeloTabla.addRow(new Object[]{
                    r.getTipoDocumento(),
                    r.getNumeroDocumento(),
                    r.getFechaEmision(),
                    r.getFechaVencimiento() != null ? r.getFechaVencimiento() : "",
                    r.getProveedor(),
                    r.getNitProveedor() != null ? r.getNitProveedor() : "",
                    r.getMontoGravado(),
                    r.getMontoIva(),
                    r.getMontoTotal(),
                    r.getEstadoPago()
                });
            }
            
            // Usar controller para cálculos de totales
            lblTotalGravado.setText("$" + controller.sumarMontoGravado(reportes).toString());
            lblTotalIVA.setText("$" + controller.sumarMontoIVA(reportes).toString());
            lblTotalGeneral.setText("$" + controller.sumarMontoTotal(reportes).toString());
            
            // Habilitar botones de exportación si hay datos
            boolean hayDatos = !reportes.isEmpty();
            btnExportarExcel.setEnabled(hayDatos);
            btnExportarPDF.setEnabled(hayDatos);
            
            if (!hayDatos) {
                JOptionPane.showMessageDialog(this, 
                    "No hay compras en el período seleccionado", 
                    "Información", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, 
                "Formato de fecha inválido. Use YYYY-MM-DD", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void exportarExcel() {
        if (controller == null) return;
        
        if (modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay datos para exportar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String titulo = "Libro_Compras_IVA_" + txtFechaInicio.getText() + "_a_" + txtFechaFin.getText();
        ExportadorExcel.exportar(tablaReporte, titulo, titulo);
    }
    
    private void exportarPDF() {
        if (controller == null) return;
        
        if (modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay datos para exportar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String titulo = "Libro_Compras_IVA_" + txtFechaInicio.getText() + "_a_" + txtFechaFin.getText();
        ExportadorPDF.exportar(tablaReporte, titulo, titulo);
    }
    
    // Renderer de moneda (sin cambios)
    class FormatoMonedaRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof BigDecimal) value = "$" + ((BigDecimal) value).toString();
            setHorizontalAlignment(RIGHT);
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
}