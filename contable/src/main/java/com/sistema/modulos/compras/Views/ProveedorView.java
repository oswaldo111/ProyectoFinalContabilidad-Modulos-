package com.sistema.modulos.compras.Views;

import com.sistema.modulos.compras.Controllers.ProveedorController;
import com.sistema.modulos.compras.Models.Proveedor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProveedorView extends JPanel {

    private ProveedorController controller;

    private JTextField txtId;
    private JTextField txtNombre;
    private JTextField txtNit;
    private JTextField txtNrc;
    private JCheckBox chkContribuyente;
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnEliminar;
    private JButton btnLimpiar;

    private JTable tablaProveedores;
    private DefaultTableModel modeloTabla;

    public ProveedorView() {
        super(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.setPreferredSize(new Dimension(900, 600)); // Tamaño sugerido para el CardLayout
        initComponents();
        this.controller = new ProveedorController(this);
        
        cargarProveedores(); // Se mantiene la carga inicial
    }

    private void initComponents() {

        JPanel panelFormulario = new JPanel();
        panelFormulario.setLayout(new GridLayout(6, 2, 5, 5));
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Datos del Proveedor"));

        panelFormulario.add(new JLabel("ID:"));
        txtId = new JTextField();
        txtId.setEditable(false);
        panelFormulario.add(txtId);

        panelFormulario.add(new JLabel("Nombre/Razón Social:"));
        txtNombre = new JTextField();
        panelFormulario.add(txtNombre);

        panelFormulario.add(new JLabel("NIT:"));
        txtNit = new JTextField();
        panelFormulario.add(txtNit);

        panelFormulario.add(new JLabel("NRC:"));
        txtNrc = new JTextField();
        panelFormulario.add(txtNrc);

        panelFormulario.add(new JLabel("¿Contribuyente?"));
        chkContribuyente = new JCheckBox();
        chkContribuyente.setSelected(true);
        panelFormulario.add(chkContribuyente);

        JPanel panelBotones = new JPanel();
        panelBotones.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));

        btnGuardar = new JButton(" Guardar");
        btnGuardar.setBackground(new Color(76, 175, 80));
        btnGuardar.setForeground(Color.WHITE);
        panelBotones.add(btnGuardar);

        btnActualizar = new JButton(" Actualizar");
        btnActualizar.setBackground(new Color(33, 150, 243));
        btnActualizar.setForeground(Color.WHITE);
        panelBotones.add(btnActualizar);

        btnEliminar = new JButton(" Eliminar");
        btnEliminar.setBackground(new Color(244, 67, 54));
        btnEliminar.setForeground(Color.WHITE);
        panelBotones.add(btnEliminar);

        btnLimpiar = new JButton(" Limpiar");
        panelBotones.add(btnLimpiar);

        panelFormulario.add(panelBotones);
        panelFormulario.add(new JPanel());

        JPanel panelTabla = new JPanel();
        panelTabla.setLayout(new BorderLayout());
        panelTabla.setBorder(BorderFactory.createTitledBorder("Lista de Proveedores"));

        String[] columnas = { "ID", "Nombre", "NIT", "NRC", "Contribuyente" };
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaProveedores = new JTable(modeloTabla);
        tablaProveedores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaProveedores.setRowHeight(25);
        tablaProveedores.setAutoCreateRowSorter(true);

        JScrollPane scrollPane = new JScrollPane(tablaProveedores);
        panelTabla.add(scrollPane, BorderLayout.CENTER);

        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.add(panelFormulario, BorderLayout.NORTH);
        panelPrincipal.add(panelTabla, BorderLayout.CENTER);

        this.add(panelPrincipal, BorderLayout.CENTER);

        agregarListeners();
    }

    private void agregarListeners() {
        btnGuardar.addActionListener(e -> controller.guardarProveedor());
        btnActualizar.addActionListener(e -> controller.actualizarProveedor());
        btnEliminar.addActionListener(e -> controller.eliminarProveedor());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        tablaProveedores.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 1) {
                    cargarProveedorSeleccionado();
                }
            }
        });
    }

    public void cargarProveedores() {
        try {
            List<Proveedor> proveedores = controller.listarProveedores();
            modeloTabla.setRowCount(0);

            for (Proveedor p : proveedores) {
                modeloTabla.addRow(new Object[] {
                        p.getIdEntidad(),
                        p.getNombre(),
                        p.getNit(),
                        p.getNrc(),
                        p.isEsContribuyente() ? "Sí" : "No"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar proveedores: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void cargarProveedorSeleccionado() {
        int fila = tablaProveedores.getSelectedRow();
        if (fila >= 0) {
            txtId.setText(modeloTabla.getValueAt(fila, 0).toString());
            txtNombre.setText(modeloTabla.getValueAt(fila, 1).toString());
            txtNit.setText(modeloTabla.getValueAt(fila, 2).toString());
            txtNrc.setText(modeloTabla.getValueAt(fila, 3).toString());
            chkContribuyente.setSelected(modeloTabla.getValueAt(fila, 4).toString().equals("Sí"));
        }
    }

    public void limpiarFormulario() {
        txtId.setText("");
        txtNombre.setText("");
        txtNit.setText("");
        txtNrc.setText("");
        chkContribuyente.setSelected(true);
        tablaProveedores.clearSelection();
    }

    public String getNombre() { return txtNombre.getText(); }
    public String getNit() { return txtNit.getText(); }
    public String getNrc() { return txtNrc.getText(); }
    public boolean isEsContribuyente() { return chkContribuyente.isSelected(); }

    public Long getIdProveedor() {
        try {
            return txtId.getText().isEmpty() ? null : Long.parseLong(txtId.getText());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void mostrarMensaje(String mensaje, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipo);
    }
}