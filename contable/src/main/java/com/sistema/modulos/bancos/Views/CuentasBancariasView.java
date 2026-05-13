package com.sistema.modulos.bancos.Views;

import com.sistema.modulos.bancos.Controllers.BancosController;
import com.sistema.modulos.bancos.Models.CuentaBancaria;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Vista para el Catálogo de Cuentas Bancarias (Pestaña 1).
 * Módulo de Bancos — Grupo 4
 */
public class CuentasBancariasView extends JPanel {

    private BancosController controller;
    private JTable tablaCuentas;
    private DefaultTableModel modeloTabla;
    private JLabel lblTotalDisponible;
    
    private final DecimalFormat formatoMoneda = new DecimalFormat("$#,##0.00");

    public CuentasBancariasView() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inicializarComponentes();
    }

    public void setController(BancosController controller) {
        this.controller = controller;
        // Cargar datos iniciales al asignar el controlador
        this.controller.cargarCuentas();
    }

    private void inicializarComponentes() {
        // --- PANEL SUPERIOR: Botones ---
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAgregar = new JButton("Agregar Cuenta");
        JButton btnEditar = new JButton("Editar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnActualizar = new JButton("Actualizar");

        panelBotones.add(btnAgregar);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnActualizar);
        add(panelBotones, BorderLayout.NORTH);

        // --- PANEL CENTRAL: Tabla ---
        String[] columnas = {"ID", "Banco", "N° Cuenta", "Tipo", "Saldo", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Tabla de solo lectura
            }
        };
        tablaCuentas = new JTable(modeloTabla);
        
        // Alinear saldo a la derecha
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tablaCuentas.getColumnModel().getColumn(4).setCellRenderer(rightRenderer);
        
        // Alinear centro
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tablaCuentas.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tablaCuentas.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        tablaCuentas.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(tablaCuentas);
        add(scrollPane, BorderLayout.CENTER);

        // --- PANEL INFERIOR: Totales ---
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        lblTotalDisponible = new JLabel("Saldo Total Disponible: $0.00");
        lblTotalDisponible.setFont(new Font("Arial", Font.BOLD, 14));
        panelInferior.add(lblTotalDisponible);
        add(panelInferior, BorderLayout.SOUTH);

        // --- EVENTOS ---
        btnActualizar.addActionListener(e -> {
            if (controller != null) controller.cargarCuentas();
        });

        btnAgregar.addActionListener(e -> mostrarDialogoCuenta(null));

        btnEditar.addActionListener(e -> {
            CuentaBancaria seleccionada = getCuentaSeleccionada();
            if (seleccionada != null) {
                mostrarDialogoCuenta(seleccionada);
            }
        });

        btnEliminar.addActionListener(e -> {
            CuentaBancaria seleccionada = getCuentaSeleccionada();
            if (seleccionada != null) {
                int confirm = JOptionPane.showConfirmDialog(this, 
                    "¿Está seguro de eliminar la cuenta de " + seleccionada.getNombreBanco() + "?", 
                    "Confirmar Eliminación", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    controller.eliminarCuenta(seleccionada.getIdBanco());
                }
            }
        });
    }

    public void cargarDatos(List<CuentaBancaria> cuentas) {
        modeloTabla.setRowCount(0);
        for (CuentaBancaria c : cuentas) {
            Object[] fila = {
                c.getIdBanco(),
                c.getNombreBanco(),
                c.getNumeroCuenta(),
                c.getTipoCuenta(),
                formatoMoneda.format(c.getSaldoBanco()),
                c.isEstado() ? "ACTIVA" : "INACTIVA"
            };
            modeloTabla.addRow(fila);
        }
    }

    public void mostrarTotales(BigDecimal total) {
        lblTotalDisponible.setText("Saldo Total Disponible: " + formatoMoneda.format(total));
    }

    private CuentaBancaria getCuentaSeleccionada() {
        int fila = tablaCuentas.getSelectedRow();
        if (fila == -1) {
            mostrarMensajeAdvertencia("Debe seleccionar una cuenta de la tabla.");
            return null;
        }
        
        CuentaBancaria c = new CuentaBancaria();
        c.setIdBanco((int) modeloTabla.getValueAt(fila, 0));
        c.setNombreBanco((String) modeloTabla.getValueAt(fila, 1));
        c.setNumeroCuenta((String) modeloTabla.getValueAt(fila, 2));
        c.setTipoCuenta((String) modeloTabla.getValueAt(fila, 3));
        
        // Parsear saldo quitando el formato de moneda
        String saldoStr = (String) modeloTabla.getValueAt(fila, 4);
        saldoStr = saldoStr.replace("$", "").replace(",", "");
        c.setSaldoBanco(new BigDecimal(saldoStr));
        
        c.setEstado("ACTIVA".equals(modeloTabla.getValueAt(fila, 5)));
        return c;
    }

    private void mostrarDialogoCuenta(CuentaBancaria cuentaEdicion) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), 
                cuentaEdicion == null ? "Nueva Cuenta Bancaria" : "Editar Cuenta Bancaria", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextField txtBanco = new JTextField(cuentaEdicion != null ? cuentaEdicion.getNombreBanco() : "");
        JTextField txtCuenta = new JTextField(cuentaEdicion != null ? cuentaEdicion.getNumeroCuenta() : "");
        JComboBox<String> cmbTipo = new JComboBox<>(new String[]{"CORRIENTE", "AHORRO"});
        if (cuentaEdicion != null) cmbTipo.setSelectedItem(cuentaEdicion.getTipoCuenta());
        
        JTextField txtSaldo = new JTextField(cuentaEdicion != null ? cuentaEdicion.getSaldoBanco().toString() : "0.00");
        txtSaldo.setEditable(cuentaEdicion == null); // Solo editable al crear
        
        JCheckBox chkActiva = new JCheckBox("Cuenta Activa");
        chkActiva.setSelected(cuentaEdicion == null || cuentaEdicion.isEstado());

        panel.add(new JLabel("Nombre del Banco:"));
        panel.add(txtBanco);
        panel.add(new JLabel("Número de Cuenta:"));
        panel.add(txtCuenta);
        panel.add(new JLabel("Tipo de Cuenta:"));
        panel.add(cmbTipo);
        panel.add(new JLabel("Saldo Inicial ($):"));
        panel.add(txtSaldo);
        panel.add(new JLabel("Estado:"));
        panel.add(chkActiva);

        JButton btnGuardar = new JButton("Guardar");
        btnGuardar.addActionListener(e -> {
            try {
                CuentaBancaria c = cuentaEdicion != null ? cuentaEdicion : new CuentaBancaria();
                c.setNombreBanco(txtBanco.getText());
                c.setNumeroCuenta(txtCuenta.getText());
                c.setTipoCuenta((String) cmbTipo.getSelectedItem());
                c.setSaldoBanco(new BigDecimal(txtSaldo.getText()));
                c.setEstado(chkActiva.isSelected());

                if (cuentaEdicion == null) {
                    controller.agregarCuenta(c);
                } else {
                    controller.editarCuenta(c);
                }
                dialog.dispose();
            } catch (NumberFormatException ex) {
                mostrarMensajeError("El saldo debe ser un número válido.");
            }
        });

        panel.add(new JLabel("")); // spacer
        panel.add(btnGuardar);

        dialog.add(panel);
        dialog.setVisible(true);
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
