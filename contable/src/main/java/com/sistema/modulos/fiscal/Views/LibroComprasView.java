package com.sistema.modulos.fiscal.Views;

import com.sistema.modulos.fiscal.Controllers.FiscalController;
import com.sistema.modulos.fiscal.Models.RegistroLibroCompras;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class LibroComprasView extends JPanel {

    private FiscalController controller;

    private JComboBox<String> cmbMes;
    private JSpinner spnAnio;
    private JButton btnGenerar;
    private JButton btnExportar;

    private JTable tablaCompras;
    private DefaultTableModel modeloTabla;

    private JLabel lblTotalExentas;
    private JLabel lblTotalGravadas;
    private JLabel lblTotalCredito;
    private JLabel lblTotalGeneral;

    private static final DecimalFormat FORMATO = new DecimalFormat("$#,##0.00");

    public LibroComprasView() {
        super(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.setPreferredSize(new Dimension(950, 600));
        initComponents();
    }

    /**
     * Inyecta el controlador después de la construcción (evita dependencia circular).
     */
    public void setController(FiscalController controller) {
        this.controller = controller;
    }

    private void initComponents() {
        // ============ PANEL SUPERIOR: Filtros ============
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Período Fiscal"));

        panelFiltros.add(new JLabel("Mes:"));
        cmbMes = new JComboBox<>(new String[]{
                "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        });
        cmbMes.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        panelFiltros.add(cmbMes);

        panelFiltros.add(new JLabel("Año:"));
        spnAnio = new JSpinner(new SpinnerNumberModel(LocalDate.now().getYear(), 2000, 2100, 1));
        spnAnio.setEditor(new JSpinner.NumberEditor(spnAnio, "#"));
        panelFiltros.add(spnAnio);

        btnGenerar = new JButton("Generar Libro");
        btnGenerar.setBackground(new Color(33, 150, 243));
        btnGenerar.setForeground(Color.WHITE);
        panelFiltros.add(btnGenerar);

        btnExportar = new JButton("Exportar CSV");
        btnExportar.setBackground(new Color(76, 175, 80));
        btnExportar.setForeground(Color.WHITE);
        panelFiltros.add(btnExportar);

        // ============ PANEL CENTRAL: Tabla ============
        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBorder(BorderFactory.createTitledBorder("Libro de Compras IVA"));

        String[] columnas = {"N°", "Fecha", "N° Doc", "Tipo", "Proveedor", "NRC",
                "Exentas", "Gravadas", "Crédito Fiscal", "Total"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaCompras = new JTable(modeloTabla);
        tablaCompras.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaCompras.setRowHeight(25);
        tablaCompras.setAutoCreateRowSorter(true);

        // Alinear columnas numéricas a la derecha
        DefaultTableCellRenderer rendererDerecha = new DefaultTableCellRenderer();
        rendererDerecha.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int i = 6; i <= 9; i++) {
            tablaCompras.getColumnModel().getColumn(i).setCellRenderer(rendererDerecha);
        }

        // Ancho de columnas
        tablaCompras.getColumnModel().getColumn(0).setPreferredWidth(30);
        tablaCompras.getColumnModel().getColumn(4).setPreferredWidth(180);

        JScrollPane scrollPane = new JScrollPane(tablaCompras);
        panelTabla.add(scrollPane, BorderLayout.CENTER);

        // ============ PANEL INFERIOR: Totales ============
        JPanel panelTotales = new JPanel(new GridLayout(1, 4, 15, 5));
        panelTotales.setBorder(BorderFactory.createTitledBorder("Totales del Período"));

        lblTotalExentas = crearLabelTotal("Exentas: $0.00");
        lblTotalGravadas = crearLabelTotal("Gravadas: $0.00");
        lblTotalCredito = crearLabelTotal("Crédito Fiscal: $0.00");
        lblTotalCredito.setForeground(new Color(33, 150, 243));
        lblTotalCredito.setFont(new Font("Arial", Font.BOLD, 14));
        lblTotalGeneral = crearLabelTotal("Total General: $0.00");

        panelTotales.add(lblTotalExentas);
        panelTotales.add(lblTotalGravadas);
        panelTotales.add(lblTotalCredito);
        panelTotales.add(lblTotalGeneral);

        // ============ Ensamblar ============
        this.add(panelFiltros, BorderLayout.NORTH);
        this.add(panelTabla, BorderLayout.CENTER);
        this.add(panelTotales, BorderLayout.SOUTH);

        // ============ Listeners ============
        btnGenerar.addActionListener(e -> {
            if (controller != null) controller.cargarLibroCompras();
        });
        btnExportar.addActionListener(e -> {
            if (controller != null) controller.exportarLibroComprasCSV();
        });
    }

    private JLabel crearLabelTotal(String texto) {
        JLabel label = new JLabel(texto, SwingConstants.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setOpaque(true);
        label.setBackground(new Color(245, 245, 245));
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        return label;
    }

    // ============ Métodos públicos para el Controller ============

    public int getMes() {
        return cmbMes.getSelectedIndex() + 1;
    }

    public int getAnio() {
        return (int) spnAnio.getValue();
    }

    public void cargarDatos(List<RegistroLibroCompras> registros) {
        modeloTabla.setRowCount(0);
        int correlativo = 1;
        for (RegistroLibroCompras r : registros) {
            modeloTabla.addRow(new Object[]{
                    correlativo++,
                    r.getFechaEmision(),
                    r.getNumeroDocumento(),
                    r.getTipoDocumento(),
                    r.getNombreProveedor(),
                    r.getNrc() != null ? r.getNrc() : "",
                    FORMATO.format(r.getComprasExentas()),
                    FORMATO.format(r.getComprasGravadas()),
                    FORMATO.format(r.getCreditoFiscal()),
                    FORMATO.format(r.getCompraTotal())
            });
        }
    }

    public void mostrarTotales(Map<String, BigDecimal> totales) {
        lblTotalExentas.setText("Exentas: " + FORMATO.format(totales.get("exentas")));
        lblTotalGravadas.setText("Gravadas: " + FORMATO.format(totales.get("gravadas")));
        lblTotalCredito.setText("Crédito Fiscal: " + FORMATO.format(totales.get("creditoFiscal")));
        lblTotalGeneral.setText("Total General: " + FORMATO.format(totales.get("totalGeneral")));
    }

    public void mostrarMensaje(String mensaje, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipo);
    }
}
