package com.sistema.modulos.compras.Views;

import com.sistema.modulos.compras.Controllers.CompraController;
import com.sistema.modulos.compras.Models.Compra;
import com.sistema.modulos.compras.Models.DetalleCompra;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.*;

import java.awt.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

public class PanelCompras extends JPanel {
    
    // CAMPO DEL CONTROLADOR (ya lo tenías, perfecto)
    private CompraController controller;
    
    private JTable tablaCompras;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> comboFiltroEstado;
    private JTextField txtFiltroProveedor;
    private JTextField txtFechaDesde, txtFechaHasta;
    private JCheckBox chkProximasVencer;
    private JButton btnBuscar, btnRegistrarCompra, btnVerDetalle, btnPagar;
    private JLabel lblTotalPendiente;
    
    public PanelCompras() {
        // ELIMINAMOS: this.controller = new CompraController();
        // Ahora el controller se inyectará desde fuera vía setController()
        initComponents();
        
        // FALLBACK OPCIONAL: Para pruebas independientes sin MainSimple
        if (this.controller == null) {
            this.controller = new CompraController();
        }
        cargarCompras();
    }
    
    // MÉTODO NUEVO: Setter para inyección de dependencias
    public void setController(CompraController controller) {
        this.controller = controller;
    }
    
    // Getter opcional (útil para testing)
    public CompraController getController() {
        return controller;
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(8, 8));
        setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        JPanel panelSuperior = new JPanel(new BorderLayout(8, 8));
        
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Filtros de búsqueda"));
        
        panelFiltros.add(new JLabel("Proveedor:"));
        txtFiltroProveedor = new JTextField(12);
        panelFiltros.add(txtFiltroProveedor);
        
        panelFiltros.add(new JLabel("Estado:"));
        comboFiltroEstado = new JComboBox<>(new String[]{"TODOS", "PENDIENTE", "PARCIAL", "PAGADO"});
        panelFiltros.add(comboFiltroEstado);
        
        panelFiltros.add(new JLabel("Fecha Desde:"));
        txtFechaDesde = new JTextField(12);
        txtFechaDesde.setText(java.time.LocalDate.now().withDayOfMonth(1).toString());
        configurarFecha(txtFechaDesde);
        panelFiltros.add(txtFechaDesde);
        
        panelFiltros.add(new JLabel("Hasta:"));
        txtFechaHasta = new JTextField(12);
        txtFechaHasta.setText(java.time.LocalDate.now().toString());
        configurarFecha(txtFechaHasta);
        panelFiltros.add(txtFechaHasta);
        
        chkProximasVencer = new JCheckBox("Próximas a vencer (7 días)");
        chkProximasVencer.addActionListener(e -> {
            boolean selected = chkProximasVencer.isSelected();
            txtFechaDesde.setEnabled(!selected);
            txtFechaHasta.setEnabled(!selected);
        });
        panelFiltros.add(chkProximasVencer);
        
        btnBuscar = new JButton("Buscar");
        btnBuscar.addActionListener(e -> buscarCompras());
        panelFiltros.add(btnBuscar);
        
        JButton btnLimpiar = new JButton("Limpiar");
        btnLimpiar.addActionListener(e -> limpiarFiltros());
        panelFiltros.add(btnLimpiar);
        
        JPanel panelResumen = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        panelResumen.setBorder(BorderFactory.createTitledBorder("Resumen"));
        panelResumen.add(new JLabel("Total pendiente por pagar:"));
        lblTotalPendiente = new JLabel("$0.00");
        lblTotalPendiente.setFont(new Font("Arial", Font.BOLD, 12));
        lblTotalPendiente.setForeground(new Color(220, 53, 69));
        panelResumen.add(lblTotalPendiente);
        
        panelSuperior.add(panelFiltros, BorderLayout.WEST);
        panelSuperior.add(panelResumen, BorderLayout.EAST);
        
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        btnRegistrarCompra = new JButton("Nueva Compra");
        btnRegistrarCompra.addActionListener(e -> abrirDialogRegistroCompra());
        btnVerDetalle = new JButton("Ver Detalle");
        btnVerDetalle.addActionListener(e -> verDetalleCompra());
        btnPagar = new JButton("Pagar Factura");
        btnPagar.addActionListener(e -> abrirDialogPago());
        
        panelBotones.add(btnRegistrarCompra);
        panelBotones.add(btnVerDetalle);
        panelBotones.add(btnPagar);
        
        String[] columnas = {"ID", "Proveedor", "Documento", "Fecha Emisión", "Fecha Vencimiento", 
                             "Total", "Saldo Pendiente", "Estado", "Días"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        tablaCompras = new JTable(modeloTabla);
        tablaCompras.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaCompras.setRowHeight(22);
        
        tablaCompras.getColumnModel().getColumn(5).setCellRenderer(new FormatoMonedaRenderer());
        tablaCompras.getColumnModel().getColumn(6).setCellRenderer(new FormatoMonedaRenderer());
        tablaCompras.getColumnModel().getColumn(7).setCellRenderer(new EstadoRenderer());
        tablaCompras.getColumnModel().getColumn(8).setCellRenderer(new DiasRenderer());
        
        // Ajustar anchos
        tablaCompras.getColumnModel().getColumn(0).setPreferredWidth(40);
        tablaCompras.getColumnModel().getColumn(1).setPreferredWidth(150);
        tablaCompras.getColumnModel().getColumn(2).setPreferredWidth(100);
        tablaCompras.getColumnModel().getColumn(3).setPreferredWidth(80);
        tablaCompras.getColumnModel().getColumn(4).setPreferredWidth(80);
        tablaCompras.getColumnModel().getColumn(5).setPreferredWidth(80);
        tablaCompras.getColumnModel().getColumn(6).setPreferredWidth(90);
        tablaCompras.getColumnModel().getColumn(7).setPreferredWidth(70);
        tablaCompras.getColumnModel().getColumn(8).setPreferredWidth(50);
        
        JScrollPane scrollPane = new JScrollPane(tablaCompras);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Listado de Compras"));
        
        JPanel panelNorte = new JPanel(new BorderLayout());
        panelNorte.add(panelSuperior, BorderLayout.NORTH);
        panelNorte.add(panelBotones, BorderLayout.SOUTH);
        
        add(panelNorte, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    // ==================== MÉTODOS CON VALIDACIÓN DE CONTROLLER ====================
    
    private void cargarCompras() {
        if (controller == null) return;
        List<Compra> compras = controller.obtenerTodasCompras();
        actualizarTabla(compras);
    }
    
    private void buscarCompras() {
        if (controller == null) return;
        String proveedor = txtFiltroProveedor.getText().trim();
        String estado = comboFiltroEstado.getSelectedItem().toString();
        estado = estado.equals("TODOS") ? null : estado;
        String fechaDesde = txtFechaDesde.getText().trim();
        String fechaHasta = txtFechaHasta.getText().trim();
        Integer diasVencimiento = chkProximasVencer.isSelected() ? 7 : null;
        
        List<Compra> compras = controller.buscarCompras(proveedor, estado, fechaDesde, fechaHasta, diasVencimiento);
        actualizarTabla(compras);
    }
    
    private void limpiarFiltros() {
        txtFiltroProveedor.setText("");
        comboFiltroEstado.setSelectedIndex(0);
        txtFechaDesde.setText("");
        txtFechaHasta.setText("");
        chkProximasVencer.setSelected(false);
        txtFechaDesde.setEnabled(true);
        txtFechaHasta.setEnabled(true);
        cargarCompras();
    }
    
    private void actualizarTabla(List<Compra> compras) {
        if (compras == null) return;
        
        modeloTabla.setRowCount(0);
        LocalDate hoy = LocalDate.now();
        
        for (Compra c : compras) {
            int diasRestantes = -1;
            if (c.getFechaVencimiento() != null) {
                diasRestantes = (int) java.time.temporal.ChronoUnit.DAYS.between(hoy, c.getFechaVencimiento().toLocalDate());
            }
            
            modeloTabla.addRow(new Object[]{
                c.getIdFactura(),
                c.getNombreProveedor(),
                c.getTipoDocumento() + ": " + c.getNumeroDocumento(),
                c.getFechaEmision() != null ? c.getFechaEmision().toString().substring(0, 10) : "",
                c.getFechaVencimiento() != null ? c.getFechaVencimiento().toString() : "",
                c.getMontoTotal(),
                c.getSaldoPendiente(),
                c.getEstadoPago(),
                diasRestantes >= 0 ? diasRestantes : "---"
            });
        }
        
        if (controller != null) {
            BigDecimal totalPendiente = controller.calcularTotalPendiente(compras);
            lblTotalPendiente.setText("$" + totalPendiente.toString());
        }
    }
    
    private void abrirDialogRegistroCompra() {
        if (controller == null) {
            JOptionPane.showMessageDialog(this, "Controller no inicializado", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        DialogRegistrarCompra dialog = new DialogRegistrarCompra(
            SwingUtilities.getWindowAncestor(this), controller);
        dialog.setVisible(true);
        if (dialog.isCompraRegistrada()) {
            cargarCompras();
        }
    }
    
    private void verDetalleCompra() {
        if (controller == null) return;
        
        int fila = tablaCompras.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una compra", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int idFactura = (int) modeloTabla.getValueAt(fila, 0);
        String proveedor = (String) modeloTabla.getValueAt(fila, 1);
        List<DetalleCompra> detalles = controller.obtenerDetallesCompra(idFactura);
        
        new DialogDetalleCompra(SwingUtilities.getWindowAncestor(this), proveedor, detalles, controller.getPorcentajeIVA()).setVisible(true);
    }
    
    private void abrirDialogPago() {
        if (controller == null) {
            JOptionPane.showMessageDialog(this, "Controller no inicializado", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int fila = tablaCompras.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una factura para pagar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String estado = (String) modeloTabla.getValueAt(fila, 7);
        if ("PAGADO".equals(estado)) {
            JOptionPane.showMessageDialog(this, "Esta factura ya está pagada", "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int idFactura = (int) modeloTabla.getValueAt(fila, 0);
        String proveedor = (String) modeloTabla.getValueAt(fila, 1);
        BigDecimal saldoPendiente = (BigDecimal) modeloTabla.getValueAt(fila, 6);
        
        JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));
        panel.add(new JLabel("Proveedor:"));
        panel.add(new JLabel(proveedor));
        panel.add(new JLabel("Saldo pendiente:"));
        panel.add(new JLabel("$" + saldoPendiente.toString()));
        
        JTextField txtMonto = new JTextField(saldoPendiente.toString(), 10);
        JPanel panelMonto = new JPanel(new BorderLayout());
        panelMonto.add(new JLabel("Monto a pagar: $"), BorderLayout.WEST);
        panelMonto.add(txtMonto, BorderLayout.CENTER);
        
        int option = JOptionPane.showConfirmDialog(this, new Object[]{panel, panelMonto}, 
            "Registrar Pago", JOptionPane.OK_CANCEL_OPTION);
        
        if (option == JOptionPane.OK_OPTION) {
            try {
                BigDecimal monto = new BigDecimal(txtMonto.getText().trim());
                if (controller.registrarPago(idFactura, monto, saldoPendiente, (JFrame) SwingUtilities.getWindowAncestor(this))) {
                    cargarCompras();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Monto inválido", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void configurarFecha(JTextField campo) {
        ((AbstractDocument) campo.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                String digitos = text != null ? text.replaceAll("[^0-9]", "") : "";
                String actual = fb.getDocument().getText(0, fb.getDocument().getLength());
                String soloDigitos = actual.replaceAll("[^0-9]", "");
                int inicio = Math.min(offset, soloDigitos.length());
                int fin = Math.min(offset + length, soloDigitos.length());
                String nuevosDigitos = soloDigitos.substring(0, inicio) + digitos + soloDigitos.substring(fin);
                if (nuevosDigitos.length() > 8) return;
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < nuevosDigitos.length(); i++) {
                    if (sb.length() == 4 || sb.length() == 7) sb.append('-');
                    sb.append(nuevosDigitos.charAt(i));
                }
                super.replace(fb, 0, actual.length(), sb.toString(), attrs);
            }
        });
    }

    // ==================== RENDERERS (sin cambios) ====================
    
    class FormatoMonedaRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof BigDecimal) value = "$" + ((BigDecimal) value).toString();
            setHorizontalAlignment(RIGHT);
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
    
    class EstadoRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                String estado = (String) value;
                if ("PAGADO".equals(estado)) {
                    c.setBackground(new Color(40, 167, 69));
                    c.setForeground(Color.WHITE);
                } else if ("PENDIENTE".equals(estado)) {
                    c.setBackground(new Color(220, 53, 69));
                    c.setForeground(Color.WHITE);
                } else if ("PARCIAL".equals(estado)) {
                    c.setBackground(new Color(255, 193, 7));
                    c.setForeground(Color.BLACK);
                }
            }
            setHorizontalAlignment(CENTER);
            return c;
        }
    }
    
    class DiasRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (!isSelected && value instanceof Integer) {
                int dias = (Integer) value;
                if (dias < 0) {
                    c.setBackground(new Color(220, 53, 69));
                    c.setForeground(Color.WHITE);
                    setText("VENCIDA");
                } else if (dias <= 7) {
                    c.setBackground(new Color(255, 193, 7));
                    c.setForeground(Color.BLACK);
                    setText(dias + "d");
                } else {
                    setText(dias + "d");
                }
            }
            setHorizontalAlignment(CENTER);
            return c;
        }
    }
}