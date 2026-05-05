package com.sistema.modulos.compras.Views;

import com.sistema.modulos.compras.Controllers.FacturaCompraController;
import com.sistema.modulos.compras.Models.Proveedor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class FacturaCompraView extends JPanel {

    private FacturaCompraController controller;
    private JComboBox<Proveedor> cmbProveedor;
    private JComboBox<String> cmbTipoDoc;
    private JTextField txtNumeroDoc, txtFecha, txtGravado, txtIva, txtTotal;
    private JTextArea txtObservaciones;
    private JTable tablaFacturas;
    private DefaultTableModel modeloTabla;

    public FacturaCompraView() {
        super(new BorderLayout());
         initComponents();
        this.controller = new FacturaCompraController(this);
       
    }

    private void initComponents() {
        this.setLayout(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.setPreferredSize(new Dimension(1000, 650)); // Tamaño sugerido para el CardLayout

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));

        JPanel panelForm = new JPanel(new GridBagLayout());
        panelForm.setBorder(BorderFactory.createTitledBorder("Datos de la Factura"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; panelForm.add(new JLabel("Proveedor:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1;
        cmbProveedor = new JComboBox<>();
        cmbProveedor.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof Proveedor p) value = p.getNombre() + " (NIT: " + p.getNit() + ")";
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        panelForm.add(cmbProveedor, gbc);
        gbc.gridx = 2; gbc.weightx = 0; panelForm.add(new JLabel("Tipo Doc:"), gbc);
        gbc.gridx = 3; gbc.weightx = 0.5;
        cmbTipoDoc = new JComboBox<>(new String[]{"CCF", "FAC", "EXP"});
        panelForm.add(cmbTipoDoc, gbc);

        gbc.gridx = 0; gbc.gridy = 1; panelForm.add(new JLabel("N° Documento:"), gbc);
        gbc.gridx = 1; txtNumeroDoc = new JTextField(); panelForm.add(txtNumeroDoc, gbc);

        gbc.gridx = 2; panelForm.add(new JLabel("Fecha:"), gbc);
        gbc.gridx = 3; txtFecha = new JTextField(java.time.LocalDate.now().toString()); panelForm.add(txtFecha, gbc);

        gbc.gridx = 0; gbc.gridy = 2; panelForm.add(new JLabel("Gravado:"), gbc);
        gbc.gridx = 1; txtGravado = new JTextField("0.00"); txtGravado.setHorizontalAlignment(JTextField.RIGHT); panelForm.add(txtGravado, gbc);

        gbc.gridx = 2; panelForm.add(new JLabel("IVA (13%):"), gbc);
        gbc.gridx = 3; txtIva = new JTextField("0.00"); txtIva.setEditable(false); txtIva.setHorizontalAlignment(JTextField.RIGHT); panelForm.add(txtIva, gbc);

        gbc.gridx = 0; gbc.gridy = 3; panelForm.add(new JLabel("TOTAL:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3; txtTotal = new JTextField("0.00"); txtTotal.setFont(new Font("Arial", Font.BOLD, 14)); txtTotal.setHorizontalAlignment(JTextField.RIGHT); panelForm.add(txtTotal, gbc);
        gbc.gridwidth = 1; // 🔹 Reset gridwidth para evitar efectos en filas siguientes

        gbc.gridy = 4; gbc.gridwidth = 4;
        txtObservaciones = new JTextArea(3, 20); txtObservaciones.setLineWrap(true);
        panelForm.add(new JScrollPane(txtObservaciones), gbc);
        gbc.gridwidth = 1;

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        JButton btnGuardar = new JButton(" Guardar");
        btnGuardar.setBackground(new Color(76, 175, 80)); btnGuardar.setForeground(Color.WHITE);
        JButton btnCalcular = new JButton(" Calcular");
        btnCalcular.setBackground(new Color(255, 152, 0)); btnCalcular.setForeground(Color.WHITE);
        JButton btnLimpiar = new JButton(" Limpiar");
        
        panelBotones.add(btnGuardar); panelBotones.add(btnCalcular); panelBotones.add(btnLimpiar);
        gbc.gridy = 5; panelForm.add(panelBotones, gbc);

        JPanel panelTabla = new JPanel(new BorderLayout());
        panelTabla.setBorder(BorderFactory.createTitledBorder("Cuentas por Pagar"));
        String[] cols = {"ID", "Prov ID", "Doc #", "Fecha", "Total", "Saldo", "Estado"};
        modeloTabla = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        tablaFacturas = new JTable(modeloTabla); tablaFacturas.setRowHeight(25);
        panelTabla.add(new JScrollPane(tablaFacturas), BorderLayout.CENTER);

        panelPrincipal.add(panelForm, BorderLayout.NORTH);
        panelPrincipal.add(panelTabla, BorderLayout.CENTER);

        this.add(panelPrincipal, BorderLayout.CENTER);

        btnGuardar.addActionListener(e -> controller.guardarFactura());
        btnCalcular.addActionListener(e -> controller.calcularMontos());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        txtTotal.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent e) { controller.calcularMontos(); }
        });
    }

    public void cargarProveedoresCombo(List<Proveedor> proveedores) {
        cmbProveedor.removeAllItems();
        proveedores.forEach(cmbProveedor::addItem);
    }

    public Proveedor getProveedorSeleccionado() { return (Proveedor) cmbProveedor.getSelectedItem(); }
    public String getNumeroDocumento() { return txtNumeroDoc.getText(); }
    public String getTipoDocumento() { return (String) cmbTipoDoc.getSelectedItem(); }
    public String getFecha() { return txtFecha.getText(); }
    public String getTotal() { return txtTotal.getText(); }
    public String getGravado() { return txtGravado.getText(); }
    public String getIva() { return txtIva.getText(); }
    public String getObservaciones() { return txtObservaciones.getText(); }

    public void setGravado(String v) { txtGravado.setText(v); }
    public void setIva(String v) { txtIva.setText(v); }
    public void setTotal(String v) { txtTotal.setText(v); }

    public void cargarFacturasTabla(List<Object[]> datos) {
        modeloTabla.setRowCount(0);
        datos.forEach(modeloTabla::addRow);
    }

    public void limpiarFormulario() {
        txtNumeroDoc.setText(""); txtTotal.setText("0.00"); txtGravado.setText("0.00");
        txtIva.setText("0.00"); txtObservaciones.setText("");
        cmbProveedor.setSelectedIndex(0);
    }

    public void mostrarMensaje(String msg, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, msg, titulo, tipo);
    }
}