package com.sistema.modulos.compras.Views;

import com.sistema.modulos.compras.Controllers.PagoController;
import com.sistema.modulos.compras.Models.FacturaCompra;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PagoView extends JPanel {

    private PagoController controller;
    private JTextField txtIdFactura;
    private JTextField txtProveedor;
    private JTextField txtNumero;
    private JTextField txtTotal;
    private JTextField txtSaldo;
    private JTextField txtMontoPago;
    private JTable tablaFacturas;
    private DefaultTableModel modeloTabla;
    private JButton btnRegistrarPago;
    private JButton btnRefrescar;
    private JButton btnLimpiar;

    public PagoView() {
        super(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.setPreferredSize(new Dimension(900, 500));
        initComponents();
        this.controller = new PagoController(this);
        
    }

    private void initComponents() {
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 10, 5));
        formPanel.setBorder(BorderFactory.createTitledBorder("Datos de la Factura"));

        formPanel.add(new JLabel("ID Factura:"));
        txtIdFactura = new JTextField();
        txtIdFactura.setEditable(false);
        formPanel.add(txtIdFactura);

        formPanel.add(new JLabel("Proveedor:"));
        txtProveedor = new JTextField();
        txtProveedor.setEditable(false);
        formPanel.add(txtProveedor);

        formPanel.add(new JLabel("N° Doc:"));
        txtNumero = new JTextField();
        txtNumero.setEditable(false);
        formPanel.add(txtNumero);

        formPanel.add(new JLabel("Total:"));
        txtTotal = new JTextField();
        txtTotal.setEditable(false);
        formPanel.add(txtTotal);

        formPanel.add(new JLabel("Saldo Pendiente:"));
        txtSaldo = new JTextField();
        txtSaldo.setFont(new Font("Arial", Font.BOLD, 12));
        txtSaldo.setForeground(new Color(200, 0, 0));
        formPanel.add(txtSaldo);

        formPanel.add(new JLabel("Monto a Pagar:"));
        txtMontoPago = new JTextField();
        txtMontoPago.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(txtMontoPago);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));

        btnRegistrarPago = new JButton("Registrar Pago");
        btnRegistrarPago.setBackground(new Color(0, 150, 136));
        btnRegistrarPago.setForeground(Color.WHITE);
        btnRegistrarPago.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRegistrarPago.setFocusPainted(false);
        btnRegistrarPago.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegistrarPago.setPreferredSize(new Dimension(140, 35));

        btnRefrescar = new JButton("Refrescar");
        btnRefrescar.setBackground(new Color(33, 150, 243));
        btnRefrescar.setForeground(Color.WHITE);
        btnRefrescar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRefrescar.setFocusPainted(false);
        btnRefrescar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefrescar.setPreferredSize(new Dimension(120, 35));

        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBackground(new Color(158, 158, 158));
        btnLimpiar.setForeground(Color.WHITE);
        btnLimpiar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLimpiar.setFocusPainted(false);
        btnLimpiar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLimpiar.setPreferredSize(new Dimension(120, 35));

        panelBotones.add(btnRegistrarPago);
        panelBotones.add(btnRefrescar);
        panelBotones.add(btnLimpiar);

        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBorder(BorderFactory.createTitledBorder("Facturas Pendientes"));
        
        String[] cols = {"ID", "Proveedor", "Doc #", "Fecha", "Total", "Saldo", "Estado"};
        modeloTabla = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaFacturas = new JTable(modeloTabla);
        tablaFacturas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaFacturas.setRowHeight(25);
        panelTabla.add(new JScrollPane(tablaFacturas), BorderLayout.CENTER);

        this.add(formPanel, BorderLayout.NORTH);
        this.add(panelTabla, BorderLayout.CENTER);
        this.add(panelBotones, BorderLayout.SOUTH);

        btnRegistrarPago.addActionListener(e -> controller.registrarPago());
        btnRefrescar.addActionListener(e -> controller.cargarFacturas());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        tablaFacturas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                controller.seleccionarFactura();
            }
        });
    }

    public void cargarFacturas(List<FacturaCompra> facturas) {
        modeloTabla.setRowCount(0);
        for (FacturaCompra f : facturas) {
            modeloTabla.addRow(new Object[]{
                f.getIdFactura(),
                f.getNombreProveedor(),
                f.getNumeroDocumento(),
                f.getFechaEmision(),
                f.getMontoTotal(),
                f.getSaldoPendiente(),
                f.getEstadoPago()
            });
        }
    }

    public void mostrarMensaje(String msg, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, msg, titulo, tipo);
    }

    public FacturaCompra getFacturaSeleccionada() {
        int row = tablaFacturas.getSelectedRow();
        if (row >= 0) {
            FacturaCompra f = new FacturaCompra();
            f.setIdFactura((Long) modeloTabla.getValueAt(row, 0));
            f.setNombreProveedor((String) modeloTabla.getValueAt(row, 1));
            f.setNumeroDocumento((String) modeloTabla.getValueAt(row, 2));
            f.setFechaEmision((java.time.LocalDate) modeloTabla.getValueAt(row, 3));
            f.setMontoTotal((java.math.BigDecimal) modeloTabla.getValueAt(row, 4));
            f.setSaldoPendiente((java.math.BigDecimal) modeloTabla.getValueAt(row, 5));
            f.setEstadoPago((String) modeloTabla.getValueAt(row, 6));
            return f;
        }
        return null;
    }

    public String getMontoPago() { return txtMontoPago.getText(); }
    public void setMontoPago(String val) { txtMontoPago.setText(val); }
    
    public void setDatosFactura(String prov, String num, String total, String saldo) {
        txtProveedor.setText(prov);
        txtNumero.setText(num);
        txtTotal.setText(total);
        txtSaldo.setText(saldo);
    }
    
    public void limpiarFormulario() {
        txtIdFactura.setText("");
        txtProveedor.setText("");
        txtNumero.setText("");
        txtTotal.setText("");
        txtSaldo.setText("");
        txtMontoPago.setText("");
    }
}