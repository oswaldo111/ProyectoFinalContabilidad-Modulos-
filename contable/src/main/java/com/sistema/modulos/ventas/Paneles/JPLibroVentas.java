package com.sistema.modulos.ventas.Paneles;

import com.sistema.modulos.ventas.Daos.LibroVentasDAO;
import com.sistema.modulos.ventas.Model.LibroVenta;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

public class JPLibroVentas extends JPanel {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final LibroVentasDAO libroVentasDAO;
    private final DefaultTableModel modeloTabla;
    private final DecimalFormat formatoMoneda;

    private JTextField txtEmpresaActual;
    private JTextField txtFechaDesde;
    private JTextField txtFechaHasta;
    private JComboBox<String> cmbTipoLibro;
    private JTable tblVentas;
    private JTextField txtTotalGravado;
    private JTextField txtTotalIva;
    private JTextField txtTotalExento;
    private JTextField txtTotalGeneral;

    public JPLibroVentas() {
        this.libroVentasDAO = new LibroVentasDAO();
        this.formatoMoneda = new DecimalFormat("#,##0.00");
        this.modeloTabla = new DefaultTableModel(
                new Object[]{
                    "Factura ID",
                    "Fecha",
                    "Tipo Doc",
                    "No. Documento",
                    "Cliente",
                    "NIT",
                    "NRC",
                    "Monto Gravado",
                    "IVA",
                    "Exento",
                    "Total",
                    "Estado Pago"
                }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        initComponentsManual();
        cargarNombreEmpresa();
        cargarVentas();
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

        txtFechaDesde = new JTextField(10);
        txtFechaHasta = new JTextField(10);
        cmbTipoLibro = new JComboBox<>(new String[]{"TODOS", "CONSUMIDOR FINAL", "CREDITO FISCAL"});

        agregarCampo(panelFiltros, gbc, 0, "Desde:", txtFechaDesde);
        agregarCampo(panelFiltros, gbc, 1, "Hasta:", txtFechaHasta);
        agregarCampo(panelFiltros, gbc, 2, "Tipo libro:", cmbTipoLibro);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnBuscar = new JButton("Buscar");
        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnRefrescar = new JButton("Refrescar");
        JButton btnExportar = new JButton("Exportar CSV");

        panelBotones.add(btnBuscar);
        panelBotones.add(btnLimpiar);
        panelBotones.add(btnRefrescar);
        panelBotones.add(btnExportar);

        panelSuperior.add(panelEmpresa, BorderLayout.NORTH);
        panelSuperior.add(panelFiltros, BorderLayout.CENTER);
        panelSuperior.add(panelBotones, BorderLayout.SOUTH);
        add(panelSuperior, BorderLayout.NORTH);

        tblVentas = new JTable(modeloTabla);
        tblVentas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblVentas.getTableHeader().setReorderingAllowed(false);
        add(new JScrollPane(tblVentas), BorderLayout.CENTER);

        add(crearPanelResumen(), BorderLayout.SOUTH);

        btnBuscar.addActionListener(e -> buscarVentas());
        btnLimpiar.addActionListener(e -> limpiarFiltros());
        btnRefrescar.addActionListener(e -> buscarVentas());
        btnExportar.addActionListener(e -> exportarCSV());
        txtFechaDesde.addActionListener(e -> buscarVentas());
        txtFechaHasta.addActionListener(e -> buscarVentas());
    }

    public void cargarNombreEmpresa() {
        txtEmpresaActual.setText(libroVentasDAO.obtenerNombreEmpresaActual());
    }

    public void cargarVentas() {
        try {
            llenarTabla(libroVentasDAO.listarVentas(null, null, "TODOS"));
        } catch (RuntimeException e) {
            mostrarError("No se pudieron cargar las ventas.", e);
        }
    }

    public void buscarVentas() {
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

            String tipoLibro = cmbTipoLibro.getSelectedItem() == null
                    ? "TODOS"
                    : cmbTipoLibro.getSelectedItem().toString();

            llenarTabla(libroVentasDAO.listarVentas(desde, hasta, tipoLibro));

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Validacion", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException e) {
            mostrarError("No se pudo realizar la busqueda.", e);
        }
    }

    public void recargarDatos() {
        cargarNombreEmpresa();
        cargarVentas();
    }

    public void limpiarFiltros() {
        txtFechaDesde.setText("");
        txtFechaHasta.setText("");
        cmbTipoLibro.setSelectedItem("TODOS");
        tblVentas.clearSelection();
        cargarVentas();
        txtFechaDesde.requestFocus();
    }

    public void llenarTabla(List<LibroVenta> ventas) {
        modeloTabla.setRowCount(0);

        for (LibroVenta venta : ventas) {
            modeloTabla.addRow(new Object[]{
                venta.getIdFactura(),
                formatearFecha(venta.getFechaEmision()),
                valorTexto(venta.getTipoDocumento()),
                valorTexto(venta.getNumeroDocumento()),
                valorTexto(venta.getNombreCliente()),
                valorTexto(venta.getNit()),
                valorTexto(venta.getNrc()),
                formatoMoneda.format(venta.getMontoGravado()),
                formatoMoneda.format(venta.getMontoIva()),
                formatoMoneda.format(venta.getMontoExento()),
                formatoMoneda.format(venta.getMontoTotal()),
                valorTexto(venta.getEstadoPago())
            });
        }

        calcularTotales(ventas);
    }

    public LocalDate parsearFecha(String texto, String nombreCampo) {
        String valor = texto == null ? "" : texto.trim();

        if (valor.isEmpty()) {
            return null;
        }

        try {
            return LocalDate.parse(valor, FORMATO_FECHA);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(nombreCampo + " debe tener formato dd/MM/yyyy.", e);
        }
    }

    public void exportarCSV() {
        if (modeloTabla.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay datos para exportar.");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exportar Libro de Ventas");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos CSV (*.csv)", "csv"));
        fileChooser.setSelectedFile(new File("libro_ventas.csv"));

        int opcion = fileChooser.showSaveDialog(this);
        if (opcion != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File archivo = fileChooser.getSelectedFile();
        if (!archivo.getName().toLowerCase().endsWith(".csv")) {
            archivo = new File(archivo.getParentFile(), archivo.getName() + ".csv");
        }

        try (BufferedWriter writer = Files.newBufferedWriter(archivo.toPath(), StandardCharsets.UTF_8)) {
            escribirEncabezadosCSV(writer);
            escribirFilasCSV(writer);
            JOptionPane.showMessageDialog(this, "Archivo CSV generado correctamente.");

        } catch (IOException e) {
            mostrarError("No se pudo exportar el archivo CSV.", e);
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

    private JPanel crearPanelResumen() {
        JPanel panelResumen = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        txtTotalGravado = crearCampoResumen();
        txtTotalIva = crearCampoResumen();
        txtTotalExento = crearCampoResumen();
        txtTotalGeneral = crearCampoResumen();

        panelResumen.add(new JLabel("Total gravado:"));
        panelResumen.add(txtTotalGravado);
        panelResumen.add(new JLabel("IVA:"));
        panelResumen.add(txtTotalIva);
        panelResumen.add(new JLabel("Exento:"));
        panelResumen.add(txtTotalExento);
        panelResumen.add(new JLabel("Total general:"));
        panelResumen.add(txtTotalGeneral);

        return panelResumen;
    }

    private JTextField crearCampoResumen() {
        JTextField campo = new JTextField(12);
        campo.setEditable(false);
        return campo;
    }

    private void calcularTotales(List<LibroVenta> ventas) {
        double totalGravado = 0;
        double totalIva = 0;
        double totalExento = 0;
        double totalGeneral = 0;

        for (LibroVenta venta : ventas) {
            totalGravado += venta.getMontoGravado();
            totalIva += venta.getMontoIva();
            totalExento += venta.getMontoExento();
            totalGeneral += venta.getMontoTotal();
        }

        txtTotalGravado.setText(formatoMoneda.format(totalGravado));
        txtTotalIva.setText(formatoMoneda.format(totalIva));
        txtTotalExento.setText(formatoMoneda.format(totalExento));
        txtTotalGeneral.setText(formatoMoneda.format(totalGeneral));
    }

    private void escribirEncabezadosCSV(BufferedWriter writer) throws IOException {
        for (int columna = 0; columna < modeloTabla.getColumnCount(); columna++) {
            if (columna > 0) {
                writer.write(";");
            }
            writer.write(escaparCSV(modeloTabla.getColumnName(columna)));
        }
        writer.newLine();
    }

    private void escribirFilasCSV(BufferedWriter writer) throws IOException {
        for (int fila = 0; fila < modeloTabla.getRowCount(); fila++) {
            for (int columna = 0; columna < modeloTabla.getColumnCount(); columna++) {
                if (columna > 0) {
                    writer.write(";");
                }
                writer.write(escaparCSV(modeloTabla.getValueAt(fila, columna)));
            }
            writer.newLine();
        }
    }

    private String escaparCSV(Object valor) {
        if (valor == null) {
            return "";
        }

        String texto = valor.toString();
        boolean requiereComillas = texto.contains(";")
                || texto.contains("\"")
                || texto.contains("\n")
                || texto.contains("\r");

        texto = texto.replace("\"", "\"\"");

        if (requiereComillas) {
            return "\"" + texto + "\"";
        }

        return texto;
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int columna,
            String etiqueta, java.awt.Component campo) {
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
}
