package com.sistema.modulos.compras.Views;

import com.sistema.modulos.compras.Controllers.CompraController;
import com.sistema.modulos.compras.DAO.ProveedorDAO;
import com.sistema.modulos.compras.DAO.ProductoDAO;
import com.sistema.modulos.compras.Models.DetalleCompra;
import com.sistema.modulos.compras.Models.Producto;
import com.sistema.modulos.compras.Models.Proveedor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DialogRegistrarCompra extends JDialog {
    
    private CompraController controller;
    private ProveedorDAO proveedorDAO;
    private ProductoDAO productoDAO;
    
    private JComboBox<Proveedor> comboProveedores;
    private JComboBox<Producto> comboProductos;
    private JTextField txtCantidad, txtPrecioUnitario, txtNumeroFactura;
    private JComboBox<String> comboTipoDoc;
    private JTextField txtFechaVencimiento;
    private JTable tablaDetalles;
    private DefaultTableModel modeloTabla;
    private JLabel lblSubtotal, lblIVA, lblTotal;
    private JButton btnAgregar, btnEliminar, btnGuardar;
    
    private List<DetalleCompra> detalles = new ArrayList<>();
    private boolean compraRegistrada = false;
    
    public DialogRegistrarCompra(Window owner, CompraController controller) {
        super(owner, "Registrar Nueva Compra", ModalityType.APPLICATION_MODAL);
        this.controller = controller;
        this.proveedorDAO = new ProveedorDAO();
        this.productoDAO = new ProductoDAO();
        initComponents();
        cargarDatos();
        setSize(850, 650);
        setLocationRelativeTo(owner);
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(8, 8));
        
        JPanel panelPrincipal = new JPanel(new BorderLayout(8, 8));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        JPanel panelDatos = new JPanel(new GridBagLayout());
        panelDatos.setBorder(BorderFactory.createTitledBorder("Datos de la Compra"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0; gbc.gridy = 0;
        panelDatos.add(new JLabel("Proveedor:*"), gbc);
        gbc.gridx = 1;
        comboProveedores = new JComboBox<>();
        comboProveedores.setPreferredSize(new Dimension(200, 22));
        panelDatos.add(comboProveedores, gbc);
        
        gbc.gridx = 2;
        panelDatos.add(new JLabel("Tipo Doc:*"), gbc);
        gbc.gridx = 3;
        comboTipoDoc = new JComboBox<>(new String[]{"CCF", "FC", "EX"});
        panelDatos.add(comboTipoDoc, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panelDatos.add(new JLabel("N° Documento:*"), gbc);
        gbc.gridx = 1;
        txtNumeroFactura = new JTextField(12);
        panelDatos.add(txtNumeroFactura, gbc);
        
        gbc.gridx = 2;
        panelDatos.add(new JLabel("Fecha Vencimiento:*"), gbc);
        gbc.gridx = 3;
        txtFechaVencimiento = new JTextField(LocalDate.now().plusDays(30).toString(), 10);
        panelDatos.add(txtFechaVencimiento, gbc);
        
        JPanel panelProductos = new JPanel(new GridBagLayout());
        panelProductos.setBorder(BorderFactory.createTitledBorder("Agregar Productos"));
        GridBagConstraints gbcProd = new GridBagConstraints();
        gbcProd.insets = new Insets(3, 5, 3, 5);
        gbcProd.fill = GridBagConstraints.HORIZONTAL;
        
        gbcProd.gridx = 0; gbcProd.gridy = 0;
        panelProductos.add(new JLabel("Producto:*"), gbcProd);
        gbcProd.gridx = 1;
        comboProductos = new JComboBox<>();
        comboProductos.setPreferredSize(new Dimension(250, 22));
        comboProductos.addActionListener(e -> cargarPrecioProducto());
        panelProductos.add(comboProductos, gbcProd);
        
        gbcProd.gridx = 2;
        panelProductos.add(new JLabel("Cantidad:*"), gbcProd);
        gbcProd.gridx = 3;
        txtCantidad = new JTextField(6);
        panelProductos.add(txtCantidad, gbcProd);
        
        gbcProd.gridx = 4;
        panelProductos.add(new JLabel("Precio Unit.:*"), gbcProd);
        gbcProd.gridx = 5;
        txtPrecioUnitario = new JTextField(8);
        panelProductos.add(txtPrecioUnitario, gbcProd);
        
        gbcProd.gridx = 6;
        btnAgregar = new JButton("➕ Agregar");
        btnAgregar.addActionListener(e -> agregarProducto());
        panelProductos.add(btnAgregar, gbcProd);
        
        String[] columnas = {"ID", "Producto", "Cantidad", "Precio Unit.", "Subtotal"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        tablaDetalles = new JTable(modeloTabla);
        tablaDetalles.setRowHeight(20);
        
        JScrollPane scrollTabla = new JScrollPane(tablaDetalles);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Productos de la Compra"));
        scrollTabla.setPreferredSize(new Dimension(800, 150));
        
        JPanel panelTotales = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 4));
        panelTotales.setBorder(BorderFactory.createTitledBorder("Totales"));
        panelTotales.add(new JLabel("Subtotal:"));
        lblSubtotal = new JLabel("$0.00");
        panelTotales.add(lblSubtotal);
        panelTotales.add(new JLabel("IVA (13%):"));
        lblIVA = new JLabel("$0.00");
        panelTotales.add(lblIVA);
        panelTotales.add(new JLabel("Total:"));
        lblTotal = new JLabel("$0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 12));
        lblTotal.setForeground(new Color(40, 167, 69));
        panelTotales.add(lblTotal);
        
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnEliminar = new JButton("🗑️ Eliminar");
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnGuardar = new JButton("💾 Guardar Compra");
        btnGuardar.addActionListener(e -> guardarCompra());
        JButton btnCancelar = new JButton("❌ Cancelar");
        btnCancelar.addActionListener(e -> dispose());
        
        panelBotones.add(btnEliminar);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        
        JPanel panelContenido = new JPanel(new BorderLayout(8, 8));
        panelContenido.add(panelDatos, BorderLayout.NORTH);
        panelContenido.add(panelProductos, BorderLayout.CENTER);
        panelContenido.add(scrollTabla, BorderLayout.SOUTH);
        
        JPanel panelInferior = new JPanel(new BorderLayout());
        panelInferior.add(panelTotales, BorderLayout.NORTH);
        panelInferior.add(panelBotones, BorderLayout.SOUTH);
        
        add(panelContenido, BorderLayout.CENTER);
        add(panelInferior, BorderLayout.SOUTH);
    }
    
    private void cargarDatos() {
        for (Proveedor p : proveedorDAO.obtenerTodos()) comboProveedores.addItem(p);
        cargarProductos();
    }
    
    private void cargarProductos() {
        comboProductos.removeAllItems();
        for (Producto p : productoDAO.obtenerTodos()) comboProductos.addItem(p);
    }
    
    private void cargarPrecioProducto() {
        Producto p = (Producto) comboProductos.getSelectedItem();
        if (p != null && p.getCostoUnitario() != null) {
            txtPrecioUnitario.setText(p.getCostoUnitario().toString());
        }
    }
    
    private void agregarProducto() {
        Producto producto = (Producto) comboProductos.getSelectedItem();
        if (producto == null) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            if (cantidad <= 0) throw new NumberFormatException();
            
            BigDecimal precioUnitario = new BigDecimal(txtPrecioUnitario.getText().trim());
            if (precioUnitario.compareTo(BigDecimal.ZERO) <= 0) throw new NumberFormatException();
            
            BigDecimal subtotal = precioUnitario.multiply(new BigDecimal(cantidad));
            
            DetalleCompra detalle = new DetalleCompra();
            detalle.setIdProducto(producto.getIdProducto());
            detalle.setNombreProducto(producto.getNombreProducto());
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(precioUnitario);
            detalle.setSubtotal(subtotal);
            
            detalles.add(detalle);
            modeloTabla.addRow(new Object[]{producto.getIdProducto(), producto.getNombreProducto(), cantidad, "$" + precioUnitario, "$" + subtotal});
            
            txtCantidad.setText("");
            calcularTotales();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese valores numéricos válidos", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void eliminarProducto() {
        int fila = tablaDetalles.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        detalles.remove(fila);
        modeloTabla.removeRow(fila);
        calcularTotales();
    }
    
    private void calcularTotales() {
        BigDecimal subtotal = controller.calcularSubtotal(detalles);
        BigDecimal iva = controller.calcularIVA(subtotal);
        BigDecimal total = controller.calcularTotal(subtotal, iva);
        
        lblSubtotal.setText("$" + subtotal);
        lblIVA.setText("$" + iva);
        lblTotal.setText("$" + total);
    }
    
    private void guardarCompra() {
        if (comboProveedores.getSelectedItem() == null || txtNumeroFactura.getText().trim().isEmpty() || detalles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Date fechaVencimiento;
        try {
            fechaVencimiento = Date.valueOf(txtFechaVencimiento.getText().trim());
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Formato de fecha inválido. Use YYYY-MM-DD", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Proveedor proveedor = (Proveedor) comboProveedores.getSelectedItem();
        
        if (controller.registrarCompra(proveedor.getIdEntidad(), (String) comboTipoDoc.getSelectedItem(),
                txtNumeroFactura.getText().trim(), fechaVencimiento, detalles, (JFrame) SwingUtilities.getWindowAncestor(this))) {
            compraRegistrada = true;
            dispose();
        }
    }
    
    public boolean isCompraRegistrada() { return compraRegistrada; }
}