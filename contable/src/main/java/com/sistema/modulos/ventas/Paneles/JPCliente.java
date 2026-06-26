package com.sistema.modulos.ventas.Paneles;

import com.sistema.modulos.ventas.Daos.ClienteDAO;
import com.sistema.modulos.ventas.Model.Cliente;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class JPCliente extends JPanel {

    private final ClienteDAO clienteDAO;
    private final DefaultTableModel modeloTabla;

    private Integer idClienteSeleccionado;
    private JTextField txtNombre;
    private JTextField txtNrc;
    private JTextField txtNit;
    private JTextField txtDui;
    private JTextField txtTelefono;
    private JTextArea txtDireccion;
    private JTextField txtBuscar;
    private JTextField txtEmpresaActual;
    private JTable tblClientes;

    public JPCliente() {
        this.clienteDAO = new ClienteDAO();
        this.modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "NRC", "NIT", "DUI", "Teléfono", "Dirección"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        initComponents();
        cargarNombreEmpresa();
        cargarClientes();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNombre = new JTextField(25);
        txtNrc = new JTextField(15);
        txtNit = new JTextField(15);
        txtDui = new JTextField(15);
        txtTelefono = new JTextField(15);
        txtDireccion = new JTextArea(3, 25);
        txtDireccion.setLineWrap(true);
        txtDireccion.setWrapStyleWord(true);
        txtBuscar = new JTextField(25);
        txtEmpresaActual = new JTextField(30);
        txtEmpresaActual.setEditable(false);

        agregarCampo(panelFormulario, gbc, 0, 0, "Nombre:", txtNombre);
        agregarCampo(panelFormulario, gbc, 1, 0, "NRC:", txtNrc);
        agregarCampo(panelFormulario, gbc, 0, 1, "NIT:", txtNit);
        agregarCampo(panelFormulario, gbc, 1, 1, "DUI:", txtDui);
        agregarCampo(panelFormulario, gbc, 0, 2, "Teléfono:", txtTelefono);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panelFormulario.add(new JLabel("Dirección:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        panelFormulario.add(new JScrollPane(txtDireccion), gbc);
        gbc.gridwidth = 1;

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnBuscar = new JButton("Buscar");
        panelBusqueda.add(new JLabel("Buscar:"));
        panelBusqueda.add(txtBuscar);
        panelBusqueda.add(btnBuscar);

        JPanel panelSuperior = new JPanel(new BorderLayout());
        JPanel panelEmpresa = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelEmpresa.add(new JLabel("Empresa actual:"));
        panelEmpresa.add(txtEmpresaActual);

        panelSuperior.add(panelEmpresa, BorderLayout.NORTH);
        panelSuperior.add(panelFormulario, BorderLayout.CENTER);
        panelSuperior.add(panelBusqueda, BorderLayout.SOUTH);
        add(panelSuperior, BorderLayout.NORTH);

        tblClientes = new JTable(modeloTabla);
        tblClientes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblClientes.getTableHeader().setReorderingAllowed(false);
        add(new JScrollPane(tblClientes), BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnNuevo = new JButton("Nuevo / Limpiar");
        JButton btnGuardar = new JButton("Guardar");
        JButton btnActualizar = new JButton("Actualizar");
        JButton btnEliminar = new JButton("Eliminar");

        panelBotones.add(btnNuevo);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnActualizar);
        panelBotones.add(btnEliminar);
        add(panelBotones, BorderLayout.SOUTH);

        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnGuardar.addActionListener(e -> guardarCliente());
        btnActualizar.addActionListener(e -> actualizarCliente());
        btnEliminar.addActionListener(e -> eliminarCliente());
        btnBuscar.addActionListener(e -> buscarClientes());
        txtBuscar.addActionListener(e -> buscarClientes());

        tblClientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarClienteSeleccionado();
            }
        });
    }

    private void cargarNombreEmpresa() {
        txtEmpresaActual.setText(clienteDAO.obtenerNombreEmpresaActual());
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int columna, int fila, String etiqueta, JTextField campo) {
        gbc.gridx = columna * 2;
        gbc.gridy = fila;
        gbc.weightx = 0;
        panel.add(new JLabel(etiqueta), gbc);

        gbc.gridx = columna * 2 + 1;
        gbc.gridy = fila;
        gbc.weightx = 1;
        panel.add(campo, gbc);
    }

    
    private void cargarClientes() {
        try {
            llenarTabla(clienteDAO.listarClientes());
        } catch (RuntimeException e) {
            mostrarError("No se pudieron cargar los clientes.", e);
        }
    }

    private void buscarClientes() {
        try {
            llenarTabla(clienteDAO.buscarClientes(txtBuscar.getText()));
        } catch (RuntimeException e) {
            mostrarError("No se pudo realizar la busqueda.", e);
        }
    }

    private void llenarTabla(List<Cliente> clientes) {
        modeloTabla.setRowCount(0);
        for (Cliente cliente : clientes) {
            modeloTabla.addRow(new Object[]{
                cliente.getIdEntidad(),
                cliente.getNombre(),
                cliente.getNrc(),
                cliente.getNit(),
                cliente.getDui(),
                cliente.getTelefono(),
                cliente.getDireccion()
            });
        }
    }

    private void cargarClienteSeleccionado() {
        int fila = tblClientes.getSelectedRow();
        if (fila < 0) {
            return;
        }

        idClienteSeleccionado = (Integer) modeloTabla.getValueAt(fila, 0);
        txtNombre.setText(valorTabla(fila, 1));
        txtNrc.setText(valorTabla(fila, 2));
        txtNit.setText(valorTabla(fila, 3));
        txtDui.setText(valorTabla(fila, 4));
        txtTelefono.setText(valorTabla(fila, 5));
        txtDireccion.setText(valorTabla(fila, 6));
    }

    private void guardarCliente() {
        Cliente cliente = construirClienteDesdeFormulario();
        if (cliente == null) {
            return;
        }

        try {
            clienteDAO.insertarCliente(cliente);
            cargarClientes();
            limpiarFormulario();
            JOptionPane.showMessageDialog(this, "Cliente guardado correctamente.");
        } catch (RuntimeException e) {
            mostrarError("No se pudo guardar el cliente.", e);
        }
    }

    private void actualizarCliente() {
        if (idClienteSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente para actualizar.");
            return;
        }

        Cliente cliente = construirClienteDesdeFormulario();
        if (cliente == null) {
            return;
        }
        cliente.setIdEntidad(idClienteSeleccionado);

        try {
            clienteDAO.actualizarCliente(cliente);
            cargarClientes();
            limpiarFormulario();
            JOptionPane.showMessageDialog(this, "Cliente actualizado correctamente.");
        } catch (RuntimeException e) {
            mostrarError("No se pudo actualizar el cliente.", e);
        }
    }

    private void eliminarCliente() {
        if (idClienteSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente para eliminar.");
            return;
        }

        int opcion = JOptionPane.showConfirmDialog(
                this,
                "¿Desea eliminar el cliente seleccionado?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            clienteDAO.eliminarCliente(idClienteSeleccionado);
            cargarClientes();
            limpiarFormulario();
            JOptionPane.showMessageDialog(this, "Cliente eliminado correctamente.");
        } catch (RuntimeException e) {
            mostrarError("No se pudo eliminar el cliente.", e);
        }
    }

    private Cliente construirClienteDesdeFormulario() {
        String nombre = txtNombre.getText().trim();
        String nrc = txtNrc.getText().trim();
        String nit = txtNit.getText().trim();
        String dui = txtDui.getText().trim();
        String telefono = txtTelefono.getText().trim();
        String direccion = txtDireccion.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio.");
            txtNombre.requestFocus();
            return null;
        }

        if (!nrc.isEmpty() && nit.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Si ingresa NRC, también debe ingresar NIT.");
            txtNit.requestFocus();
            return null;
        }

        if (!validarLongitud(nrc, 20, "NRC")
                || !validarLongitud(nit, 20, "NIT")
                || !validarLongitud(dui, 10, "DUI")
                || !validarLongitud(telefono, 15, "Teléfono")) {
            return null;
        }

        Cliente cliente = new Cliente();
        cliente.setIdEmpresa(clienteDAO.obtenerIdEmpresaActual());
        cliente.setTipoEntidad("CLIENTE");
        cliente.setNombre(nombre);
        cliente.setNrc(nrc);
        cliente.setNit(nit);
        cliente.setDui(dui);
        cliente.setTelefono(telefono);
        cliente.setDireccion(direccion);
        return cliente;
    }

    private boolean validarLongitud(String valor, int maximo, String campo) {
        if (valor.length() > maximo) {
            JOptionPane.showMessageDialog(this, campo + " no puede superar " + maximo + " caracteres.");
            return false;
        }
        return true;
    }

    private void limpiarFormulario() {
        idClienteSeleccionado = null;
        txtNombre.setText("");
        txtNrc.setText("");
        txtNit.setText("");
        txtDui.setText("");
        txtTelefono.setText("");
        txtDireccion.setText("");
        tblClientes.clearSelection();
        txtNombre.requestFocus();
        cargarClientes();
    }

    private String valorTabla(int fila, int columna) {
        Object valor = modeloTabla.getValueAt(fila, columna);
        return valor == null ? "" : valor.toString();
    }

    private void mostrarError(String mensaje, RuntimeException e) {
        e.printStackTrace();

        Throwable causa = e.getCause();

        String detalle = e.getMessage();

        if (causa != null) {
            detalle += "\n\nCausa: " + causa.getMessage();
        }

        JOptionPane.showMessageDialog(
                this,
                mensaje + "\n\n" + detalle,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
