package com.sistema.modulos.bancos.Views;

import com.sistema.modulos.bancos.Controllers.BancosController;
import com.sistema.modulos.bancos.Models.ConciliacionBancaria;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Vista para la Conciliación Bancaria y Disponibilidad (Pestaña 3).
 * Módulo de Bancos — Grupo 4
 */
public class ConciliacionView extends JPanel {

    private BancosController controller;
    
    // Filtros
    private JComboBox<CuentaBancaria> cmbCuentas;
    private JComboBox<String> cmbMeses;
    private JSpinner spinAnio;
    
    // Tabla pendientes
    private JTable tablaPendientes;
    private DefaultTableModel modeloPendientes;
    
    // Panel Resumen Conciliación
    private JLabel lblSaldoLibros;
    private JTextField txtSaldoBanco;
    private JLabel lblDiferencia;
    private JLabel lblEstadoConciliacion;
    
    // Tabla Disponibilidad
    private JTable tablaDisponibilidad;
    private DefaultTableModel modeloDisponibilidad;

    private final DecimalFormat formatoMoneda = new DecimalFormat("$#,##0.00");
    private final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ConciliacionView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inicializarComponentes();
    }

    public void setController(BancosController controller) {
        this.controller = controller;
    }

    private void inicializarComponentes() {
        // --- PANEL SUPERIOR: Filtros ---
        JPanel panelSuperior = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        cmbCuentas = new JComboBox<>();
        cmbCuentas.setPreferredSize(new Dimension(200, 25));
        
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", 
                          "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        cmbMeses = new JComboBox<>(meses);
        cmbMeses.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        
        spinAnio = new JSpinner(new SpinnerNumberModel(LocalDate.now().getYear(), 2000, 2100, 1));
        JSpinner.NumberEditor editor = new JSpinner.NumberEditor(spinAnio, "#");
        spinAnio.setEditor(editor);

        JButton btnCargar = new JButton("Cargar Pendientes");
        JButton btnConciliarSel = new JButton("Marcar Seleccionados");
        JButton btnExportar = new JButton("Exportar Conciliación");

        panelSuperior.add(new JLabel("Cuenta:"));
        panelSuperior.add(cmbCuentas);
        panelSuperior.add(new JLabel("Mes:"));
        panelSuperior.add(cmbMeses);
        panelSuperior.add(new JLabel("Año:"));
        panelSuperior.add(spinAnio);
        panelSuperior.add(btnCargar);
        panelSuperior.add(btnConciliarSel);
        panelSuperior.add(btnExportar);

        add(panelSuperior, BorderLayout.NORTH);

        // --- PANEL CENTRAL: Dividido en Pendientes (Izq) y Resumen (Der) ---
        JPanel panelCentral = new JPanel(new BorderLayout(10, 0));
        
        // Izquierda: Tabla Pendientes (60%)
        String[] colPendientes = {"Conciliar", "ID", "Fecha", "Tipo", "Descripción", "Monto"};
        modeloPendientes = new DefaultTableModel(colPendientes, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : super.getColumnClass(columnIndex);
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Solo el checkbox es editable
            }
        };
        tablaPendientes = new JTable(modeloPendientes);
        
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tablaPendientes.getColumnModel().getColumn(5).setCellRenderer(rightRenderer); // Monto
        
        // Ocultar ID
        tablaPendientes.getColumnModel().getColumn(1).setMinWidth(0);
        tablaPendientes.getColumnModel().getColumn(1).setMaxWidth(0);
        tablaPendientes.getColumnModel().getColumn(1).setWidth(0);

        JPanel panelIzq = new JPanel(new BorderLayout());
        panelIzq.setBorder(BorderFactory.createTitledBorder("Partidas Pendientes de Conciliar"));
        panelIzq.add(new JScrollPane(tablaPendientes), BorderLayout.CENTER);
        panelCentral.add(panelIzq, BorderLayout.CENTER);

        // Derecha: Panel Resumen (40%)
        JPanel panelDer = new JPanel(new BorderLayout());
        panelDer.setBorder(BorderFactory.createTitledBorder("Cálculo de Conciliación"));
        panelDer.setPreferredSize(new Dimension(350, 0));
        
        JPanel gridResumen = new JPanel(new GridLayout(6, 2, 10, 15));
        gridResumen.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        
        lblSaldoLibros = new JLabel("$0.00");
        lblSaldoLibros.setFont(new Font("Arial", Font.BOLD, 14));
        
        txtSaldoBanco = new JTextField("0.00");
        txtSaldoBanco.setFont(new Font("Arial", Font.PLAIN, 14));
        
        lblDiferencia = new JLabel("$0.00");
        lblDiferencia.setFont(new Font("Arial", Font.BOLD, 14));
        
        lblEstadoConciliacion = new JLabel("PENDIENTE");
        lblEstadoConciliacion.setFont(new Font("Arial", Font.BOLD, 14));

        gridResumen.add(new JLabel("Saldo según Libros:"));
        gridResumen.add(lblSaldoLibros);
        
        gridResumen.add(new JLabel("Saldo según Banco ($):"));
        gridResumen.add(txtSaldoBanco);
        
        gridResumen.add(new JLabel("Diferencia:"));
        gridResumen.add(lblDiferencia);
        
        gridResumen.add(new JLabel("Estado:"));
        gridResumen.add(lblEstadoConciliacion);
        
        JButton btnCalcular = new JButton("Calcular y Guardar");
        gridResumen.add(new JLabel("")); // spacer
        gridResumen.add(btnCalcular);

        panelDer.add(gridResumen, BorderLayout.NORTH);
        panelCentral.add(panelDer, BorderLayout.EAST);
        
        add(panelCentral, BorderLayout.CENTER);

        // --- PANEL INFERIOR: Disponibilidad Diaria ---
        JPanel panelInferior = new JPanel(new BorderLayout(0, 5));
        panelInferior.setBorder(BorderFactory.createTitledBorder("Reporte de Disponibilidad Diaria"));
        panelInferior.setPreferredSize(new Dimension(0, 200));

        JPanel panelDispBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnCargarDisp = new JButton("Actualizar Disponibilidad");
        JButton btnExportarDisp = new JButton("Exportar Disponibilidad CSV");
        panelDispBotones.add(btnCargarDisp);
        panelDispBotones.add(btnExportarDisp);
        
        String[] colDisp = {"Banco", "N° Cuenta", "Tipo", "Saldo Disponible"};
        modeloDisponibilidad = new DefaultTableModel(colDisp, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaDisponibilidad = new JTable(modeloDisponibilidad);
        tablaDisponibilidad.getColumnModel().getColumn(3).setCellRenderer(rightRenderer); // Saldo
        
        panelInferior.add(panelDispBotones, BorderLayout.NORTH);
        panelInferior.add(new JScrollPane(tablaDisponibilidad), BorderLayout.CENTER);
        
        add(panelInferior, BorderLayout.SOUTH);

        // --- EVENTOS ---
        btnCargar.addActionListener(e -> {
            if (controller != null) controller.cargarPendientes();
        });

        btnConciliarSel.addActionListener(e -> {
            if (controller != null) controller.conciliarSeleccionados();
        });

        btnCalcular.addActionListener(e -> {
            if (controller != null) controller.calcularConciliacion();
        });

        btnCargarDisp.addActionListener(e -> {
            if (controller != null) controller.generarReporteDisponibilidad();
        });

        btnExportar.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Guardar Conciliación CSV");
            chooser.setSelectedFile(new File("Conciliacion_Bancaria.csv"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                controller.exportarConciliacionCSV(chooser.getSelectedFile());
            }
        });

        btnExportarDisp.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Guardar Disponibilidad Diaria CSV");
            chooser.setSelectedFile(new File("Disponibilidad_Diaria.csv"));
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                controller.exportarDisponibilidadCSV(chooser.getSelectedFile());
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

    public void cargarPendientes(List<MovimientoBancario> movs) {
        modeloPendientes.setRowCount(0);
        for (MovimientoBancario m : movs) {
            Object[] fila = {
                false, // Checkbox desmarcado por defecto
                m.getIdMovimiento(),
                m.getFecha() != null ? m.getFecha().format(formatoFecha) : "",
                m.getTipoMovimiento(),
                m.getDescripcion(),
                formatoMoneda.format(m.getMonto())
            };
            modeloPendientes.addRow(fila);
        }
        
        // Actualizar saldo según libros
        CuentaBancaria c = (CuentaBancaria) cmbCuentas.getSelectedItem();
        if (c != null) {
            lblSaldoLibros.setText(formatoMoneda.format(c.getSaldoBanco()));
        }
    }

    public void mostrarResultado(ConciliacionBancaria conc) {
        lblSaldoLibros.setText(formatoMoneda.format(conc.getSaldoSegunLibros()));
        txtSaldoBanco.setText(conc.getSaldoSegunBanco().toString());
        
        lblDiferencia.setText(formatoMoneda.format(conc.getDiferencia()));
        if (conc.estaConciliado()) {
            lblDiferencia.setForeground(new Color(0, 128, 0));
            lblEstadoConciliacion.setForeground(new Color(0, 128, 0));
        } else {
            lblDiferencia.setForeground(Color.RED);
            lblEstadoConciliacion.setForeground(Color.RED);
        }
        lblEstadoConciliacion.setText(conc.getEstado());
    }

    public void cargarDisponibilidad(List<CuentaBancaria> cuentas) {
        modeloDisponibilidad.setRowCount(0);
        BigDecimal totalGeneral = BigDecimal.ZERO;
        
        for (CuentaBancaria c : cuentas) {
            Object[] fila = {
                c.getNombreBanco(),
                c.getNumeroCuenta(),
                c.getTipoCuenta(),
                formatoMoneda.format(c.getSaldoBanco())
            };
            modeloDisponibilidad.addRow(fila);
            totalGeneral = totalGeneral.add(c.getSaldoBanco());
        }
        
        Object[] filaTotal = {"", "", "TOTAL GENERAL:", formatoMoneda.format(totalGeneral)};
        modeloDisponibilidad.addRow(filaTotal);
    }

    public List<Integer> getIdsSeleccionados() {
        List<Integer> ids = new ArrayList<>();
        for (int i = 0; i < modeloPendientes.getRowCount(); i++) {
            boolean seleccionado = (Boolean) modeloPendientes.getValueAt(i, 0);
            if (seleccionado) {
                ids.add((Integer) modeloPendientes.getValueAt(i, 1));
            }
        }
        return ids;
    }

    // Getters para controlador
    public int getIdBancoSeleccionado() {
        CuentaBancaria c = (CuentaBancaria) cmbCuentas.getSelectedItem();
        return c != null ? c.getIdBanco() : -1;
    }

    public int getMes() {
        return cmbMeses.getSelectedIndex() + 1;
    }

    public int getAnio() {
        return (int) spinAnio.getValue();
    }

    public BigDecimal getSaldoBanco() {
        try {
            return new BigDecimal(txtSaldoBanco.getText().trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El saldo del banco debe ser un número válido.");
        }
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
