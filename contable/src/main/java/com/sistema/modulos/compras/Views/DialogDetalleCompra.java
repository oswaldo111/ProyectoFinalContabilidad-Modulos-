package com.sistema.modulos.compras.Views;

import com.sistema.modulos.compras.Models.DetalleCompra;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class DialogDetalleCompra extends JDialog {
    
    public DialogDetalleCompra(Window owner, String proveedor, List<DetalleCompra> detalles, BigDecimal porcentajeIVA) {
        super(owner, "Detalle de Compra - " + proveedor, ModalityType.APPLICATION_MODAL);
        initComponents(detalles, porcentajeIVA);
        setSize(700, 500);
        setLocationRelativeTo(owner);
    }
    
    private void initComponents(List<DetalleCompra> detalles, BigDecimal porcentajeIVA) {
        setLayout(new BorderLayout(10, 10));
        
        String[] columnas = {"Producto", "Cantidad", "Precio Unitario", "Subtotal"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        BigDecimal totalCompra = BigDecimal.ZERO;
        for (DetalleCompra d : detalles) {
            modelo.addRow(new Object[]{d.getNombreProducto(), d.getCantidad(), "$" + d.getPrecioUnitario(), "$" + d.getSubtotal()});
            totalCompra = totalCompra.add(d.getSubtotal());
        }
        
        BigDecimal iva = totalCompra.multiply(porcentajeIVA.divide(new BigDecimal("100")));
        BigDecimal totalFinal = totalCompra.add(iva);
        
        JTable tabla = new JTable(modelo);
        JScrollPane scroll = new JScrollPane(tabla);
        
        JPanel panelTotales = new JPanel(new GridLayout(3, 2, 10, 5));
        panelTotales.setBorder(BorderFactory.createTitledBorder("Resumen"));
        panelTotales.add(new JLabel("Subtotal:"));
        panelTotales.add(new JLabel("$" + totalCompra));
        panelTotales.add(new JLabel("IVA (" + porcentajeIVA + "%):"));
        panelTotales.add(new JLabel("$" + iva));
        panelTotales.add(new JLabel("Total:"));
        JLabel lblTotal = new JLabel("$" + totalFinal);
        lblTotal.setFont(new Font("Arial", Font.BOLD, 14));
        lblTotal.setForeground(new Color(40, 167, 69));
        panelTotales.add(lblTotal);
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        
        JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBoton.add(btnCerrar);
        
        add(panelTotales, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(panelBoton, BorderLayout.SOUTH);
    }
}