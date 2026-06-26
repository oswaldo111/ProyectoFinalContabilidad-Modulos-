package com.sistema.modulos.contabilidad.Views;

import com.sistema.modulos.contabilidad.Controllers.CatalogoController;
import com.sistema.modulos.contabilidad.Models.Cuenta;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Panel del Catálogo de Cuentas Contables.
 * Muestra las cuentas en un JTree jerárquico (Activo → Corriente → Bancos).
 * Módulo de Contabilidad
 */
public class CatalogoCuentas extends javax.swing.JPanel {

    private CatalogoController controller;

    // Componentes UI
    private JTree treeCuentas;
    private DefaultTreeModel treeModel;
    private JButton btnNueva;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnRefrescar;
    private JLabel lblTitulo;
    private JScrollPane scrollTree;
    private JPanel panelBotones;
    private JPanel panelPrincipal;

    // Cuenta seleccionada actualmente en el árbol
    private Cuenta cuentaSeleccionada = null;

    public CatalogoCuentas() {
        initComponents();
        configurarEventos();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 250));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // --- TÍTULO ---
        lblTitulo = new JLabel("Catálogo de Cuentas Contables");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(new Color(33, 37, 41));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(lblTitulo, BorderLayout.NORTH);

        // --- ÁRBOL ---
        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("Catálogo de Cuentas");
        treeModel = new DefaultTreeModel(raiz);
        treeCuentas = new JTree(treeModel);
        treeCuentas.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        treeCuentas.setRowHeight(26);
        treeCuentas.setShowsRootHandles(true);
        treeCuentas.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        // Renderer personalizado para mostrar íconos por tipo
        treeCuentas.setCellRenderer(new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value,
                    boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                if (value instanceof DefaultMutableTreeNode node) {
                    if (node.getUserObject() instanceof Cuenta cuenta) {
                        setText(cuenta.getCodigoCuenta() + "  " + cuenta.getNombreCuenta());
                        setFont(leaf
                            ? new Font("Segoe UI", Font.PLAIN, 13)
                            : new Font("Segoe UI", Font.BOLD, 13));
                    }
                }
                return this;
            }
        });

        scrollTree = new JScrollPane(treeCuentas);
        scrollTree.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 210)));
        add(scrollTree, BorderLayout.CENTER);

        // --- BOTONES ---
        panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelBotones.setOpaque(false);
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        btnNueva    = crearBoton("+ Nueva Cuenta", new Color(40, 167, 69));
        btnEditar   = crearBoton("✎ Editar",        new Color(0, 123, 255));
        btnEliminar = crearBoton("✕ Eliminar",       new Color(220, 53, 69));
        btnRefrescar= crearBoton("↻ Refrescar",      new Color(108, 117, 125));

        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);

        panelBotones.add(btnNueva);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnRefrescar);
        add(panelBotones, BorderLayout.SOUTH);
    }

    private JButton crearBoton(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void configurarEventos() {
        // Selección en el árbol
        treeCuentas.addTreeSelectionListener(e -> {
            DefaultMutableTreeNode nodo = (DefaultMutableTreeNode) treeCuentas.getLastSelectedPathComponent();
            if (nodo != null && nodo.getUserObject() instanceof Cuenta cuenta) {
                cuentaSeleccionada = cuenta;
                btnEditar.setEnabled(true);
                btnEliminar.setEnabled(true);
            } else {
                cuentaSeleccionada = null;
                btnEditar.setEnabled(false);
                btnEliminar.setEnabled(false);
            }
        });

        // Botón Nueva Cuenta
        btnNueva.addActionListener(e -> mostrarDialogoNuevaCuenta());

        // Botón Editar
        btnEditar.addActionListener(e -> {
            if (cuentaSeleccionada != null) mostrarDialogoEditarCuenta(cuentaSeleccionada);
        });

        // Botón Eliminar
        btnEliminar.addActionListener(e -> {
            if (cuentaSeleccionada != null) {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Desea eliminar la cuenta: " + cuentaSeleccionada + "?",
                    "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                if (confirm == JOptionPane.YES_OPTION) {
                    controller.eliminarCuenta(cuentaSeleccionada.getIdCuenta());
                }
            }
        });

        // Botón Refrescar
        btnRefrescar.addActionListener(e -> controller.cargarCatalogo());
    }

    /**
     * Construye el JTree a partir de la lista plana de cuentas.
     * Agrupa por cuenta padre usando el código para jerarquía.
     */
    public void cargarArbol(List<Cuenta> cuentas) {
        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("Catálogo de Cuentas");
        Map<Integer, DefaultMutableTreeNode> nodos = new LinkedHashMap<>();

        // Primero crear todos los nodos
        for (Cuenta c : cuentas) {
            nodos.put(c.getIdCuenta(), new DefaultMutableTreeNode(c));
        }

        // Luego armar la jerarquía
        for (Cuenta c : cuentas) {
            DefaultMutableTreeNode nodoActual = nodos.get(c.getIdCuenta());
            if (c.getCuentaPadre() != null && nodos.containsKey(c.getCuentaPadre().getIdCuenta())) {
                nodos.get(c.getCuentaPadre().getIdCuenta()).add(nodoActual);
            } else {
                raiz.add(nodoActual);
            }
        }

        treeModel.setRoot(raiz);
        treeModel.reload();

        // Expandir todo el árbol
        for (int i = 0; i < treeCuentas.getRowCount(); i++) {
            treeCuentas.expandRow(i);
        }
    }

    /**
     * Muestra el diálogo para crear una nueva cuenta.
     */
    private void mostrarDialogoNuevaCuenta() {
        JTextField txtCodigo = new JTextField(15);
        JTextField txtNombre = new JTextField(15);
        String[] tipos = {"ACTIVO", "PASIVO", "CAPITAL", "INGRESO", "GASTO", "COSTO"};
        JComboBox<String> cmbTipo = new JComboBox<>(tipos);

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.add(new JLabel("Código:"));   panel.add(txtCodigo);
        panel.add(new JLabel("Nombre:"));   panel.add(txtNombre);
        panel.add(new JLabel("Tipo:"));     panel.add(cmbTipo);

        int result = JOptionPane.showConfirmDialog(this, panel,
            "Nueva Cuenta Contable", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            Cuenta nueva = new Cuenta();
            nueva.setCodigoCuenta(txtCodigo.getText().trim());
            nueva.setNombreCuenta(txtNombre.getText().trim());
            nueva.setTipoCuenta((String) cmbTipo.getSelectedItem());
            // Si hay una cuenta seleccionada, la usamos como padre
            if (cuentaSeleccionada != null) {
                nueva.setCuentaPadre(cuentaSeleccionada);
            }
            controller.agregarCuenta(nueva);
        }
    }

    /**
     * Muestra el diálogo para editar una cuenta existente.
     */
    private void mostrarDialogoEditarCuenta(Cuenta cuenta) {
        JTextField txtCodigo = new JTextField(cuenta.getCodigoCuenta(), 15);
        JTextField txtNombre = new JTextField(cuenta.getNombreCuenta(), 15);
        String[] tipos = {"ACTIVO", "PASIVO", "CAPITAL", "INGRESO", "GASTO", "COSTO"};
        JComboBox<String> cmbTipo = new JComboBox<>(tipos);
        cmbTipo.setSelectedItem(cuenta.getTipoCuenta());

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.add(new JLabel("Código:"));   panel.add(txtCodigo);
        panel.add(new JLabel("Nombre:"));   panel.add(txtNombre);
        panel.add(new JLabel("Tipo:"));     panel.add(cmbTipo);

        int result = JOptionPane.showConfirmDialog(this, panel,
            "Editar Cuenta", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            cuenta.setCodigoCuenta(txtCodigo.getText().trim());
            cuenta.setNombreCuenta(txtNombre.getText().trim());
            cuenta.setTipoCuenta((String) cmbTipo.getSelectedItem());
            controller.editarCuenta(cuenta);
        }
    }

    // ==========================================
    // MÉTODOS PÚBLICOS PARA EL CONTROLLER
    // ==========================================

    public void setController(CatalogoController controller) {
        this.controller = controller;
    }

    public void mostrarExito(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public Cuenta getCuentaSeleccionada() {
        return cuentaSeleccionada;
    }
}