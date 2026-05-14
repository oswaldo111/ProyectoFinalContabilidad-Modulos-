package com.sistema.modulos.compras.Views;

import com.sistema.modulos.compras.Controllers.ProveedorController;
import com.sistema.modulos.compras.Models.Proveedor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.util.List;

public class PanelProveedores extends JPanel {
    
    // CAMPO DEL CONTROLADOR (ya lo tenías, perfecto)
    private ProveedorController controller;
    
    private JTable tablaProveedores;
    private DefaultTableModel modeloTabla;
    private JTextField txtNombre, txtNRC, txtNIT, txtDUI, txtTelefono, txtDireccion, txtBuscar;
    private JButton btnGuardar, btnEditar, btnEliminar, btnLimpiar, btnBuscar;
    private int idSeleccionado = -1;
    
    public PanelProveedores() {
        // ELIMINAMOS: this.controller = new ProveedorController();
        // Ahora el controller se inyectará desde fuera vía setController()
        initComponents();
        // OPCIONAL: Si quieres un fallback para pruebas independientes:
        if (this.controller == null) {
            this.controller = new ProveedorController();
        }
        cargarProveedores();
    }
    
    // MÉTODO NUEVO: Setter para inyección de dependencias
    public void setController(ProveedorController controller) {
        this.controller = controller;
    }
    
    // Getter opcional (útil para testing)
    public ProveedorController getController() {
        return controller;
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        // Panel de búsqueda
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        panelBusqueda.setBorder(BorderFactory.createTitledBorder("Buscar Proveedor"));
        panelBusqueda.add(new JLabel("Buscar:"));
        txtBuscar = new JTextField(15);
        panelBusqueda.add(txtBuscar);
        btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarProveedores());
        panelBusqueda.add(btnBuscar);
        
        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.addActionListener(e -> cargarProveedores());
        panelBusqueda.add(btnRefrescar);
        
        // Panel de formulario
        JPanel panelFormulario = new JPanel(new GridBagLayout());
        panelFormulario.setBorder(BorderFactory.createTitledBorder("Registro de Proveedores"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        panelFormulario.add(new JLabel("Nombre:*"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtNombre = new JTextField(25);
        panelFormulario.add(txtNombre, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1;
        panelFormulario.add(new JLabel("NRC:"), gbc);
        gbc.gridx = 1;
        txtNRC = new JTextField(12);
        panelFormulario.add(txtNRC, gbc);
        gbc.gridx = 2;
        panelFormulario.add(new JLabel("NIT:*"), gbc);
        gbc.gridx = 3;
        txtNIT = new JTextField(12);
        panelFormulario.add(txtNIT, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panelFormulario.add(new JLabel("DUI:"), gbc);
        gbc.gridx = 1;
        txtDUI = new JTextField(12);
        panelFormulario.add(txtDUI, gbc);
        gbc.gridx = 2;
        panelFormulario.add(new JLabel("Teléfono:"), gbc);
        gbc.gridx = 3;
        txtTelefono = new JTextField(12);
        panelFormulario.add(txtTelefono, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panelFormulario.add(new JLabel("Dirección:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 3;
        txtDireccion = new JTextField(25);
        panelFormulario.add(txtDireccion, gbc);
        
        JPanel panelBotonesForm = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        btnGuardar = new JButton("Guardar");
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");
        btnLimpiar = new JButton("Limpiar");
        
        // Los listeners ya llaman a métodos del controller, ¡perfecto!
        btnGuardar.addActionListener(e -> guardarProveedor());
        btnEditar.addActionListener(e -> editarProveedor());
        btnEliminar.addActionListener(e -> eliminarProveedor());
        btnLimpiar.addActionListener(e -> limpiarFormulario());
        
        panelBotonesForm.add(btnGuardar);
        panelBotonesForm.add(btnEditar);
        panelBotonesForm.add(btnEliminar);
        panelBotonesForm.add(btnLimpiar);
        
        // Tabla
        String[] columnas = {"ID", "Nombre", "NRC", "NIT", "DUI", "Teléfono", "Dirección"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tablaProveedores = new JTable(modeloTabla);
        tablaProveedores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaProveedores.setRowHeight(22);
        tablaProveedores.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) cargarSeleccionado();
        });
        
        JScrollPane scrollTabla = new JScrollPane(tablaProveedores);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Listado de Proveedores"));
        
        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.add(panelBusqueda, BorderLayout.NORTH);
        panelNorte.add(panelFormulario, BorderLayout.CENTER);
        panelNorte.add(panelBotonesForm, BorderLayout.SOUTH);
        
        add(panelNorte, BorderLayout.NORTH);
        add(scrollTabla, BorderLayout.CENTER);
    }
    
    // ==================== MÉTODOS EXISTENTES (sin cambios) ====================
    
    private void cargarProveedores() {
        if (controller == null) return;
        List<Proveedor> proveedores = controller.obtenerTodos();
        actualizarTabla(proveedores);
        txtBuscar.setText("");
    }
    
    private void buscarProveedores() {
        if (controller == null) return;
        String filtro = txtBuscar.getText().trim();
        List<Proveedor> proveedores = controller.buscar(filtro);
        actualizarTabla(proveedores);
    }
    
    private void actualizarTabla(List<Proveedor> proveedores) {
        modeloTabla.setRowCount(0);
        for (Proveedor p : proveedores) {
            modeloTabla.addRow(new Object[]{
                p.getIdEntidad(), p.getNombre(),
                p.getNrc() != null ? p.getNrc() : "",
                p.getNit() != null ? p.getNit() : "",
                p.getDui() != null ? p.getDui() : "",
                p.getTelefono() != null ? p.getTelefono() : "",
                p.getDireccion() != null ? p.getDireccion() : ""
            });
        }
    }
    
    private void cargarSeleccionado() {
        int fila = tablaProveedores.getSelectedRow();
        if (fila >= 0) {
            idSeleccionado = (int) modeloTabla.getValueAt(fila, 0);
            txtNombre.setText((String) modeloTabla.getValueAt(fila, 1));
            txtNRC.setText((String) modeloTabla.getValueAt(fila, 2));
            txtNIT.setText((String) modeloTabla.getValueAt(fila, 3));
            txtDUI.setText((String) modeloTabla.getValueAt(fila, 4));
            txtTelefono.setText((String) modeloTabla.getValueAt(fila, 5));
            txtDireccion.setText((String) modeloTabla.getValueAt(fila, 6));
        }
    }
    
    private void guardarProveedor() {
        if (controller == null) {
            JOptionPane.showMessageDialog(this, "Controller no inicializado", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (txtNombre.getText().trim().isEmpty() || txtNIT.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre y NIT son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Proveedor p = new Proveedor();
        p.setNombre(txtNombre.getText().trim());
        p.setNrc(txtNRC.getText().trim());
        p.setNit(txtNIT.getText().trim());
        p.setDui(txtDUI.getText().trim());
        p.setTelefono(txtTelefono.getText().trim());
        p.setDireccion(txtDireccion.getText().trim());
        
        if (controller.registrarProveedor(p, (JFrame) SwingUtilities.getWindowAncestor(this))) {
            limpiarFormulario();
            cargarProveedores();
        }
    }
    
    private void editarProveedor() {
        if (controller == null) return;
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un proveedor", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Proveedor p = new Proveedor();
        p.setIdEntidad(idSeleccionado);
        p.setNombre(txtNombre.getText().trim());
        p.setNrc(txtNRC.getText().trim());
        p.setNit(txtNIT.getText().trim());
        p.setDui(txtDUI.getText().trim());
        p.setTelefono(txtTelefono.getText().trim());
        p.setDireccion(txtDireccion.getText().trim());
        
        if (controller.actualizarProveedor(p, (JFrame) SwingUtilities.getWindowAncestor(this))) {
            limpiarFormulario();
            cargarProveedores();
        }
    }
    
    private void eliminarProveedor() {
        if (controller == null) return;
        if (idSeleccionado == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un proveedor", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String nombre = txtNombre.getText().trim();
        if (controller.eliminarProveedor(idSeleccionado, nombre, (JFrame) SwingUtilities.getWindowAncestor(this))) {
            limpiarFormulario();
            cargarProveedores();
        }
    }
    
    private void limpiarFormulario() {
        idSeleccionado = -1;
        txtNombre.setText("");
        txtNRC.setText("");
        txtNIT.setText("");
        txtDUI.setText("");
        txtTelefono.setText("");
        txtDireccion.setText("");
        tablaProveedores.clearSelection();
    }
}