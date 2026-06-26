package com.sistema.modulos.contabilidad.Views;

import com.sistema.modulos.contabilidad.Controllers.CatalogoController;
import com.sistema.modulos.contabilidad.Models.Cuenta;
import com.sistema.modulos.contabilidad.Models.Empresa;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Panel del Catálogo de Cuentas Contables.
 * Incluye selector de empresa y árbol jerárquico de cuentas.
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
    private JPanel panelEmpresa;
    private JComboBox<Empresa> cmbEmpresas;
    private JLabel lblEmpresa;

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
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        // --- SELECTOR DE EMPRESA ---
        panelEmpresa = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelEmpresa.setOpaque(false);
        panelEmpresa.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        lblEmpresa = new JLabel("Empresa:");
        lblEmpresa.setFont(new Font("Segoe UI", Font.BOLD, 13));

        cmbEmpresas = new JComboBox<>();
        cmbEmpresas.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        cmbEmpresas.setPreferredSize(new Dimension(280, 30));
        cmbEmpresas.addItem(new Empresa(-1, "-- Seleccione una empresa --"));

        panelEmpresa.add(lblEmpresa);
        panelEmpresa.add(cmbEmpresas);

        // Panel norte: título + selector empresa
        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.setOpaque(false);
        panelNorte.add(lblTitulo, BorderLayout.NORTH);
        panelNorte.add(panelEmpresa, BorderLayout.CENTER);
        add(panelNorte, BorderLayout.NORTH);

        // --- ÁRBOL ---
        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("Seleccione una empresa");
        treeModel = new DefaultTreeModel(raiz);
        treeCuentas = new JTree(treeModel);
        treeCuentas.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        treeCuentas.setRowHeight(26);
        treeCuentas.setShowsRootHandles(true);
        treeCuentas.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

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

        btnNueva     = crearBoton("+ Nueva Cuenta", new Color(40, 167, 69));
        btnEditar    = crearBoton("✎ Editar",        new Color(0, 123, 255));
        btnEliminar  = crearBoton("✕ Eliminar",       new Color(220, 53, 69));
        btnRefrescar = crearBoton("↻ Refrescar",      new Color(108, 117, 125));

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
        // Selección de empresa en ComboBox
        cmbEmpresas.addActionListener(e -> {
            Empresa seleccionada = (Empresa) cmbEmpresas.getSelectedItem();
            if (seleccionada != null && seleccionada.getIdEmpresa() != -1 && controller != null) {
                controller.seleccionarEmpresa(seleccionada.getIdEmpresa());
            }
        });

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
        btnNueva.addActionListener(e -> {
            if (controller != null && controller.getIdEmpresaActual() == -1) {
                mostrarError("Seleccione una empresa primero.");
                return;
            }
            mostrarDialogoNuevaCuenta();
        });

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
        btnRefrescar.addActionListener(e -> {
            if (controller != null) controller.cargarCatalogo();
        });
    }

    /**
     * Carga las empresas en el ComboBox.
     */
    public void cargarEmpresas(List<Empresa> empresas) {
        cmbEmpresas.removeAllItems();
        cmbEmpresas.addItem(new Empresa(-1, "-- Seleccione una empresa --"));
        for (Empresa e : empresas) {
            cmbEmpresas.addItem(e);
        }
    }

    /**
     * Construye el JTree jerárquico a partir de la lista de cuentas.
     */
    public void cargarArbol(List<Cuenta> cuentas) {
        DefaultMutableTreeNode raiz = new DefaultMutableTreeNode("Catálogo de Cuentas");
        Map<Integer, DefaultMutableTreeNode> nodos = new LinkedHashMap<>();

        for (Cuenta c : cuentas) {
            nodos.put(c.getIdCuenta(), new DefaultMutableTreeNode(c));
        }

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

        for (int i = 0; i < treeCuentas.getRowCount(); i++) {
            treeCuentas.expandRow(i);
        }
    }

    private void mostrarDialogoNuevaCuenta() {
        JTextField txtCodigo = new JTextField(15);
        JTextField txtNombre = new JTextField(15);
        String[] tipos = {"ACTIVO", "PASIVO", "CAPITAL", "INGRESO", "GASTO", "COSTO"};
        JComboBox<String> cmbTipo = new JComboBox<>(tipos);

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.add(new JLabel("Código:"));  panel.add(txtCodigo);
        panel.add(new JLabel("Nombre:"));  panel.add(txtNombre);
        panel.add(new JLabel("Tipo:"));    panel.add(cmbTipo);

        int result = JOptionPane.showConfirmDialog(this, panel,
            "Nueva Cuenta Contable", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            Cuenta nueva = new Cuenta();
            nueva.setCodigoCuenta(txtCodigo.getText().trim());
            nueva.setNombreCuenta(txtNombre.getText().trim());
            nueva.setTipoCuenta((String) cmbTipo.getSelectedItem());
            if (cuentaSeleccionada != null) nueva.setCuentaPadre(cuentaSeleccionada);
            controller.agregarCuenta(nueva);
        }
    }

    private void mostrarDialogoEditarCuenta(Cuenta cuenta) {
        JTextField txtCodigo = new JTextField(cuenta.getCodigoCuenta(), 15);
        JTextField txtNombre = new JTextField(cuenta.getNombreCuenta(), 15);
        String[] tipos = {"ACTIVO", "PASIVO", "CAPITAL", "INGRESO", "GASTO", "COSTO"};
        JComboBox<String> cmbTipo = new JComboBox<>(tipos);
        cmbTipo.setSelectedItem(cuenta.getTipoCuenta());

        JPanel panel = new JPanel(new GridLayout(0, 2, 8, 8));
        panel.add(new JLabel("Código:"));  panel.add(txtCodigo);
        panel.add(new JLabel("Nombre:"));  panel.add(txtNombre);
        panel.add(new JLabel("Tipo:"));    panel.add(cmbTipo);

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