package com.sistema.modulos.bancos.Views;

import com.sistema.modulos.bancos.Controllers.BancosController;
import com.sistema.modulos.bancos.Models.CuentaBancaria;
import com.sistema.modulos.bancos.Models.MovimientoBancario;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Vista para el Registro de Movimientos (Pestaña 2).
 * Módulo de Bancos — Grupo 4
 */
public class MovimientosView extends JPanel {

    private BancosController controller;
    private JComboBox<CuentaBancaria> cmbCuentas;
    private JComboBox<String> cmbMeses;
    private JSpinner spinAnio;
    
    private JTable tablaMovimientos;
    private DefaultTableModel modeloTabla;
    
    private JLabel lblTotalIngresos;
    private JLabel lblTotalEgresos;
    private JLabel lblSaldoNeto;
    
    private final DecimalFormat formatoMoneda = new DecimalFormat("$#,##0.00");
    private final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public MovimientosView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inicializarComponentes();
    }

    public void setController(BancosController controller) {
        this.controller = controller;
    }

    private void inicializarComponentes() {
        // --- PANEL SUPERIOR: Filtros y Botones ---
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        
        cmbCuentas = new JComboBox<>();
        cmbCuentas.setPreferredSize(new Dimension(250, 25));
        
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", 
                          "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        cmbMeses = new JComboBox<>(meses);
        cmbMeses.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        
        spinAnio = new JSpinner(new SpinnerNumberModel(LocalDate.now().getYear(), 2000, 2100, 1));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinAnio, "#");
        spinAnio.setEditor(editor);

        JButton btnConsultar = new JButton("Consultar");
        JButton btnNuevo = new JButton("Nuevo Movimiento");
        JButton btnExportar = new JButton("Exportar CSV");

        panelSuperior.add(new JLabel("Cuenta:"));
        panelSuperior.add(cmbCuentas);
        panelSuperior.add(new JLabel("Mes:"));
        panelSuperior.add(cmbMeses);
        panelSuperior.add(new JLabel("Año:"));
        panelSuperior.add(spinAnio);
        panelSuperior.add(btnConsultar);
        panelSuperior.add(btnNuevo);
        panelSuperior.add(btnExportar);
        
        add(panelSuperior, BorderLayout.NORTH);

        // --- PANEL CENTRAL: Tabla ---
        String[] columnas = {"N°", "Fecha", "Tipo", "Ref/Cheque", "Beneficiario", "Descripción", "Monto", "Conciliado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaMovimientos = new JTable(modeloTabla);
        
        // Renderizadores personalizados
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tablaMovimientos.getColumnModel().getColumn(6).setCellRenderer(rightRenderer); // Monto
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tablaMovimientos.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // N°
        tablaMovimientos.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); // Fecha
        tablaMovimientos.getColumnModel().getColumn(7).setCellRenderer(centerRenderer); // Conciliado
        
        // Renderizador de colores para el Tipo
        tablaMovimientos.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    String tipo = value.toString();
                    switch (tipo) {
                        case "INGRESO": c.setForeground(new Color(0, 128, 0)); break;
                        case "EGRESO": c.setForeground(Color.RED); break;
                        case "CHEQUE": c.setForeground(new Color(200, 100, 0)); break;
                        case "TRANSFERENCIA": c.setForeground(Color.BLUE); break;
                        default: c.setForeground(Color.BLACK);
                    }
                    setFont(getFont().deriveFont(Font.BOLD));
                }
                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        });

        JScrollPane scrollPane = new JScrollPane(tablaMovimientos);
        add(scrollPane, BorderLayout.CENTER);

        // --- PANEL INFERIOR: Totales ---
        JPanel panelInferior = new JPanel(new GridLayout(1, 3, 10, 10));
        panelInferior.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        lblTotalIngresos = new JLabel("Ingresos: $0.00", SwingConstants.CENTER);
        lblTotalIngresos.setForeground(new Color(0, 128, 0));
        lblTotalIngresos.setFont(new Font("Arial", Font.BOLD, 14));
        
        lblTotalEgresos = new JLabel("Egresos: $0.00", SwingConstants.CENTER);
        lblTotalEgresos.setForeground(Color.RED);
        lblTotalEgresos.setFont(new Font("Arial", Font.BOLD, 14));
        
        lblSaldoNeto = new JLabel("Saldo Neto: $0.00", SwingConstants.CENTER);
        lblSaldoNeto.setFont(new Font("Arial", Font.BOLD, 14));

        panelInferior.add(lblTotalIngresos);
        panelInferior.add(lblTotalEgresos);
        panelInferior.add(lblSaldoNeto);
        add(panelInferior, BorderLayout.SOUTH);

        // --- EVENTOS ---
        btnConsultar.addActionListener(e -> {
            if (controller != null) controller.cargarMovimientos();
        });

        btnNuevo.addActionListener(e -> mostrarDialogoNuevoMovimiento());

        btnExportar.addActionListener(e -> {
            if (modeloTabla.getRowCount() == 0) {
                mostrarMensajeAdvertencia("No hay datos para exportar.");
                return;
            }
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Guardar Reporte CSV");
            chooser.setSelectedFile(new File("Movimientos_Bancarios.csv"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                controller.exportarMovimientosCSV(chooser.getSelectedFile());
            }
        });
    }

    public void cargarCuentas(List<CuentaBancaria> cuentas) {
        CuentaBancaria seleccionada = (CuentaBancaria) cmbCuentas.getSelectedItem();
        cmbCuentas.removeAllItems();
        for (CuentaBancaria c : cuentas) {
            cmbCuentas.addItem(c);
        }
        if (seleccionada != null) {
            for (int i = 0; i < cmbCuentas.getItemCount(); i++) {
                if (cmbCuentas.getItemAt(i).getIdBanco() == seleccionada.getIdBanco()) {
                    cmbCuentas.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    public void cargarDatos(List<MovimientoBancario> movs) {
        modeloTabla.setRowCount(0);
        int i = 1;
        for (MovimientoBancario m : movs) {
            String refCheque = "";
            if ("CHEQUE".equals(m.getTipoMovimiento())) refCheque = m.getNumeroCheque();
            else if ("TRANSFERENCIA".equals(m.getTipoMovimiento())) refCheque = m.getNumeroReferencia();

            Object[] fila = {
                i++,
                m.getFecha() != null ? m.getFecha().format(formatoFecha) : "",
                m.getTipoMovimiento(),
                refCheque,
                m.getBeneficiario() != null ? m.getBeneficiario() : "",
                m.getDescripcion(),
                formatoMoneda.format(m.getMonto()),
                m.isConciliado() ? "✓ SÍ" : "✗ NO"
            };
            modeloTabla.addRow(fila);
        }
    }

    public void mostrarTotales(Map<String, BigDecimal> totales) {
        BigDecimal ingresos = totales.getOrDefault("totalIngresos", BigDecimal.ZERO);
        BigDecimal egresos = totales.getOrDefault("totalEgresos", BigDecimal.ZERO);
        BigDecimal neto = totales.getOrDefault("saldoNeto", BigDecimal.ZERO);

        lblTotalIngresos.setText("Ingresos: " + formatoMoneda.format(ingresos));
        lblTotalEgresos.setText("Egresos: " + formatoMoneda.format(egresos));
        lblSaldoNeto.setText("Saldo Neto: " + formatoMoneda.format(neto));
    }

    private void mostrarDialogoNuevoMovimiento() {
        if (cmbCuentas.getSelectedItem() == null) {
            mostrarMensajeAdvertencia("Primero debe seleccionar una cuenta bancaria.");
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Registrar Movimiento Bancario", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JComboBox<String> cmbTipo = new JComboBox<>(new String[]{"INGRESO", "EGRESO", "CHEQUE", "TRANSFERENCIA"});
        JTextField txtMonto = new JTextField("0.00");
        JTextField txtDescripcion = new JTextField();
        JTextField txtReferencia = new JTextField();
        JTextField txtBeneficiario = new JTextField();
        
        JLabel lblRef = new JLabel("Referencia:");
        JLabel lblBen = new JLabel("Beneficiario:");
        
        // Lógica para mostrar/ocultar campos según tipo
        cmbTipo.addActionListener(e -> {
            String tipo = (String) cmbTipo.getSelectedItem();
            if ("CHEQUE".equals(tipo)) {
                lblRef.setText("N° de Cheque (*):");
                txtReferencia.setEnabled(true);
                txtBeneficiario.setEnabled(true);
            } else if ("TRANSFERENCIA".equals(tipo)) {
                lblRef.setText("N° Referencia:");
                txtReferencia.setEnabled(true);
                txtBeneficiario.setEnabled(true);
            } else {
                lblRef.setText("Referencia:");
                txtReferencia.setEnabled(false);
                txtReferencia.setText("");
                txtBeneficiario.setEnabled(false);
                txtBeneficiario.setText("");
            }
        });
        cmbTipo.setSelectedIndex(0); // Forzar trigger

        panel.add(new JLabel("Tipo de Movimiento:"));
        panel.add(cmbTipo);
        panel.add(new JLabel("Monto ($):"));
        panel.add(txtMonto);
        panel.add(lblRef);
        panel.add(txtReferencia);
        panel.add(lblBen);
        panel.add(txtBeneficiario);
        panel.add(new JLabel("Descripción:"));
        panel.add(txtDescripcion);

        JButton btnGuardar = new JButton("Registrar");
        btnGuardar.addActionListener(e -> {
            try {
                MovimientoBancario m = new MovimientoBancario();
                m.setIdBanco(getIdBancoSeleccionado());
                m.setTipoMovimiento((String) cmbTipo.getSelectedItem());
                m.setMonto(new BigDecimal(txtMonto.getText()));
                m.setDescripcion(txtDescripcion.getText());
                
                if ("CHEQUE".equals(m.getTipoMovimiento())) {
                    m.setNumeroCheque(txtReferencia.getText());
                } else if ("TRANSFERENCIA".equals(m.getTipoMovimiento())) {
                    m.setNumeroReferencia(txtReferencia.getText());
                }
                
                if (txtBeneficiario.isEnabled()) {
                    m.setBeneficiario(txtBeneficiario.getText());
                }

                controller.registrarMovimiento(m);
                dialog.dispose();
            } catch (NumberFormatException ex) {
                mostrarMensajeError("El monto debe ser un número válido.");
            }
        });

        panel.add(new JLabel(""));
        panel.add(btnGuardar);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    // Getters para filtros
    public int getIdBancoSeleccionado() {
        CuentaBancaria c = (CuentaBancaria) cmbCuentas.getSelectedItem();
        return c != null ? c.getIdBanco() : -1;
    }
    
    public String getNombreBancoSeleccionado() {
        CuentaBancaria c = (CuentaBancaria) cmbCuentas.getSelectedItem();
        return c != null ? c.getNombreBanco() + " - " + c.getNumeroCuenta() : "";
    }

    public int getMes() {
        return cmbMeses.getSelectedIndex() + 1;
    }

    public int getAnio() {
        return (int) spinAnio.getValue();
    }

    public void mostrarMensajeExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarMensajeError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void mostrarMensajeAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }
}
