package com.sistema.modulos.ventas.Paneles;

import com.sistema.modulos.ventas.Daos.CuentaPorCobrarDAO;
import com.sistema.modulos.ventas.Model.CuentaPorCobrar;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class JPCuentasPorCobrar extends JPanel {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final CuentaPorCobrarDAO cuentaDAO;
    private final DefaultTableModel modeloTabla;
    private final DecimalFormat formatoMoneda;

    private JTextField txtEmpresaActual;
    private JTextField txtBuscar;
    private JComboBox<String> cmbEstado;
    private JTextField txtFechaDesde;
    private JTextField txtFechaHasta;
    private JTable tblCuentas;

    public JPCuentasPorCobrar() {
        this.cuentaDAO = new CuentaPorCobrarDAO();
        this.formatoMoneda = new DecimalFormat("#,##0.00");
        this.modeloTabla = new DefaultTableModel(
                new Object[]{
                    "Factura ID",
                    "Cliente",
                    "NIT",
                    "NRC",
                    "Tipo Doc",
                    "No. Documento",
                    "Fecha Emision",
                    "Fecha Vencimiento",
                    "Total",
                    "Saldo Pendiente",
                    "Estado",
                    "Dias Vencidos"
                }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        initComponentsManual();
        cargarNombreEmpresa();
        cargarCuentasPendientes();
    }

    public void initComponentsManual() {
        setLayout(new BorderLayout(10, 10));

        JPanel panelSuperior = new JPanel(new BorderLayout(5, 5));
        JPanel panelEmpresa = new JPanel(new FlowLayout(FlowLayout.LEFT));

        txtEmpresaActual = new JTextField(30);
        txtEmpresaActual.setEditable(false);

        panelEmpresa.add(new JLabel("Empresa actual:"));
        panelEmpresa.add(txtEmpresaActual);

        JPanel panelFiltros = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtBuscar = new JTextField(22);
        cmbEstado = new JComboBox<>(new String[]{"TODOS", "PENDIENTE", "PARCIAL", "VENCIDO"});
        txtFechaDesde = new JTextField(10);
        txtFechaHasta = new JTextField(10);

        agregarCampo(panelFiltros, gbc, 0, "Buscar:", txtBuscar);
        agregarCampo(panelFiltros, gbc, 1, "Estado:", cmbEstado);
        agregarCampo(panelFiltros, gbc, 2, "Desde:", txtFechaDesde);
        agregarCampo(panelFiltros, gbc, 3, "Hasta:", txtFechaHasta);

        JPanel panelBotonesBusqueda = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnBuscar = new JButton("Buscar");
        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnRefrescar = new JButton("Refrescar");
        JButton btnRegistrarAbono = new JButton("Registrar Abono");

        panelBotonesBusqueda.add(btnBuscar);
        panelBotonesBusqueda.add(btnLimpiar);
        panelBotonesBusqueda.add(btnRefrescar);
        panelBotonesBusqueda.add(btnRegistrarAbono);

        panelSuperior.add(panelEmpresa, BorderLayout.NORTH);
        panelSuperior.add(panelFiltros, BorderLayout.CENTER);
        panelSuperior.add(panelBotonesBusqueda, BorderLayout.SOUTH);
        add(panelSuperior, BorderLayout.NORTH);

        tblCuentas = new JTable(modeloTabla);
        tblCuentas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblCuentas.getTableHeader().setReorderingAllowed(false);
        add(new JScrollPane(tblCuentas), BorderLayout.CENTER);

        btnBuscar.addActionListener(e -> buscarCuentas());
        btnLimpiar.addActionListener(e -> limpiarFiltros());
        btnRefrescar.addActionListener(e -> cargarCuentasPendientes());
        btnRegistrarAbono.addActionListener(e -> abrirDialogoAbono());
        txtBuscar.addActionListener(e -> buscarCuentas());
        txtFechaDesde.addActionListener(e -> buscarCuentas());
        txtFechaHasta.addActionListener(e -> buscarCuentas());
    }

    public void cargarNombreEmpresa() {
        txtEmpresaActual.setText(cuentaDAO.obtenerNombreEmpresaActual());
    }

    public void recargarDatos() {
        cargarNombreEmpresa();
        cargarCuentasPendientes();
    }

    public void cargarCuentasPendientes() {
        try {
            llenarTabla(cuentaDAO.listarCuentasPendientes());
        } catch (RuntimeException e) {
            mostrarError("No se pudieron cargar las cuentas por cobrar.", e);
        }
    }

    public void buscarCuentas() {
        try {
            LocalDate desde = parsearFecha(txtFechaDesde.getText(), "Fecha desde");
            LocalDate hasta = parsearFecha(txtFechaHasta.getText(), "Fecha hasta");

            if (desde != null && hasta != null && desde.isAfter(hasta)) {
                JOptionPane.showMessageDialog(
                        this,
                        "La fecha desde no puede ser mayor que la fecha hasta.",
                        "Validacion",
                        JOptionPane.WARNING_MESSAGE
                );
                txtFechaDesde.requestFocus();
                return;
            }

            String estado = cmbEstado.getSelectedItem() == null
                    ? "TODOS"
                    : cmbEstado.getSelectedItem().toString();

            llenarTabla(cuentaDAO.buscarCuentas(txtBuscar.getText(), estado, desde, hasta));

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Validacion", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException e) {
            mostrarError("No se pudo realizar la busqueda.", e);
        }
    }

    public void limpiarFiltros() {
        txtBuscar.setText("");
        cmbEstado.setSelectedItem("TODOS");
        txtFechaDesde.setText("");
        txtFechaHasta.setText("");
        tblCuentas.clearSelection();
        cargarCuentasPendientes();
        txtBuscar.requestFocus();
    }

    public void llenarTabla(List<CuentaPorCobrar> cuentas) {
        modeloTabla.setRowCount(0);

        for (CuentaPorCobrar cuenta : cuentas) {
            modeloTabla.addRow(new Object[]{
                cuenta.getIdFactura(),
                valorTexto(cuenta.getNombreCliente()),
                valorTexto(cuenta.getNit()),
                valorTexto(cuenta.getNrc()),
                valorTexto(cuenta.getTipoDocumento()),
                valorTexto(cuenta.getNumeroDocumento()),
                formatearFecha(cuenta.getFechaEmision()),
                formatearFecha(cuenta.getFechaVencimiento()),
                formatoMoneda.format(cuenta.getMontoTotal()),
                formatoMoneda.format(cuenta.getSaldoPendiente()),
                valorTexto(cuenta.getEstadoPago()),
                cuenta.getDiasVencidos()
            });
        }
    }

    public LocalDate parsearFecha(String texto, String nombreCampo) {
        String valor = texto == null ? "" : texto.trim();

        if (valor.isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(valor, FORMATO_FECHA);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    nombreCampo + " debe tener formato dd/MM/yyyy.",
                    e
            );
        }
    }

    public void mostrarError(String mensaje, Exception e) {
        e.printStackTrace();

        String detalle = e.getMessage();
        Throwable causa = e.getCause();

        if (causa != null && causa.getMessage() != null) {
            detalle += "\n\nCausa: " + causa.getMessage();
        }

        JOptionPane.showMessageDialog(
                this,
                mensaje + "\n\n" + detalle,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void abrirDialogoAbono() {
        int filaVista = tblCuentas.getSelectedRow();

        if (filaVista < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una cuenta por cobrar para registrar un abono.");
            return;
        }

        try {
            int fila = tblCuentas.convertRowIndexToModel(filaVista);
            int idFactura = (Integer) modeloTabla.getValueAt(fila, 0);
            String cliente = valorTabla(fila, 1);
            String tipoDoc = valorTabla(fila, 4);
            String numeroDocumento = valorTabla(fila, 5);
            double total = obtenerDoubleDeTabla(modeloTabla.getValueAt(fila, 8));
            double saldoPendiente = obtenerDoubleDeTabla(modeloTabla.getValueAt(fila, 9));
            String estado = valorTabla(fila, 10);

            Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);

            JDAbonoCliente dialog = new JDAbonoCliente(
                    parent,
                    true,
                    idFactura,
                    cliente,
                    tipoDoc,
                    numeroDocumento,
                    total,
                    saldoPendiente,
                    estado
            );

            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

            if (dialog.isAbonoRegistrado()) {
                cargarCuentasPendientes();
            }

        } catch (RuntimeException e) {
            mostrarError("No se pudo abrir el registro de abono.", e);
        }
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int columna, String etiqueta, java.awt.Component campo) {
        gbc.gridx = columna * 2;
        gbc.gridy = 0;
        gbc.weightx = 0;
        panel.add(new JLabel(etiqueta), gbc);

        gbc.gridx = columna * 2 + 1;
        gbc.gridy = 0;
        gbc.weightx = 1;
        panel.add(campo, gbc);
    }

    private String formatearFecha(LocalDate fecha) {
        return fecha == null ? "" : fecha.format(FORMATO_FECHA);
    }

    private String valorTexto(String valor) {
        return valor == null ? "" : valor;
    }

    private String valorTabla(int fila, int columna) {
        Object valor = modeloTabla.getValueAt(fila, columna);
        return valor == null ? "" : valor.toString();
    }

    private double obtenerDoubleDeTabla(Object valor) {
        if (valor == null) {
            return 0.0;
        }

        String texto = valor.toString().replace(",", "").trim();
        if (texto.isEmpty()) {
            return 0.0;
        }

        return Double.parseDouble(texto);
    }
}
