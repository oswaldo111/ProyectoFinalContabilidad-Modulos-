package com.sistema.modulos.fiscal.Views;

import com.sistema.modulos.fiscal.Controllers.FiscalController;
import com.sistema.modulos.fiscal.Models.LiquidacionIva;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;

public class LiquidacionView extends JPanel {

    private FiscalController controller;

    private JComboBox<String> cmbMes;
    private JSpinner spnAnio;
    private JTextField txtRemanente;
    private JButton btnCalcular;
    private JButton btnExportar;

    // Labels de la sección de ventas
    private JLabel lblVentasGravadas;
    private JLabel lblVentasExentas;
    private JLabel lblDebitoFiscal;

    // Labels de la sección de compras
    private JLabel lblComprasGravadas;
    private JLabel lblComprasExentas;
    private JLabel lblCreditoFiscal;

    // Labels del resultado
    private JLabel lblResultadoDebito;
    private JLabel lblResultadoCredito;
    private JLabel lblResultadoRemanente;
    private JLabel lblResultadoFinal;
    private JPanel panelResultadoFinal;

    private static final DecimalFormat FORMATO = new DecimalFormat("$#,##0.00");

    public LiquidacionView() {
        super(new BorderLayout(10, 10));
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        this.setPreferredSize(new Dimension(950, 600));
        initComponents();
    }

    /**
     * Inyecta el controlador después de la construcción.
     */
    public void setController(FiscalController controller) {
        this.controller = controller;
    }

    private void initComponents() {
        // ============ PANEL SUPERIOR: Filtros ============
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelFiltros.setBorder(BorderFactory.createTitledBorder("Período de Liquidación"));

        panelFiltros.add(new JLabel("Mes:"));
        cmbMes = new JComboBox<>(new String[]{
                "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        });
        cmbMes.setSelectedIndex(LocalDate.now().getMonthValue() - 1);
        panelFiltros.add(cmbMes);

        panelFiltros.add(new JLabel("Año:"));
        spnAnio = new JSpinner(new SpinnerNumberModel(LocalDate.now().getYear(), 2000, 2100, 1));
        spnAnio.setEditor(new JSpinner.NumberEditor(spnAnio, "#"));
        panelFiltros.add(spnAnio);

        panelFiltros.add(new JLabel("Remanente Mes Anterior:"));
        txtRemanente = new JTextField("0.00", 10);
        txtRemanente.setHorizontalAlignment(JTextField.RIGHT);
        panelFiltros.add(txtRemanente);

        btnCalcular = new JButton("Calcular Liquidación");
        btnCalcular.setBackground(new Color(33, 150, 243));
        btnCalcular.setForeground(Color.WHITE);
        panelFiltros.add(btnCalcular);

        btnExportar = new JButton("Exportar CSV");
        btnExportar.setBackground(new Color(76, 175, 80));
        btnExportar.setForeground(Color.WHITE);
        panelFiltros.add(btnExportar);

        // ============ PANEL CENTRAL: Resumen de Liquidación ============
        JPanel panelCentral = new JPanel(new GridLayout(1, 3, 10, 10));

        // --- Sección VENTAS ---
        JPanel panelVentas = new JPanel(new GridLayout(4, 1, 5, 5));
        panelVentas.setBorder(BorderFactory.createTitledBorder("VENTAS (Débito Fiscal)"));
        panelVentas.setBackground(new Color(232, 245, 233));

        lblVentasGravadas = crearLabelSeccion("Ventas Gravadas: $0.00");
        lblVentasExentas = crearLabelSeccion("Ventas Exentas: $0.00");
        lblDebitoFiscal = crearLabelSeccion("DÉBITO FISCAL: $0.00");
        lblDebitoFiscal.setFont(new Font("Arial", Font.BOLD, 14));
        lblDebitoFiscal.setForeground(new Color(211, 47, 47));

        panelVentas.add(lblVentasGravadas);
        panelVentas.add(lblVentasExentas);
        panelVentas.add(new JLabel("")); // Espaciador
        panelVentas.add(lblDebitoFiscal);

        // --- Sección COMPRAS ---
        JPanel panelCompras = new JPanel(new GridLayout(4, 1, 5, 5));
        panelCompras.setBorder(BorderFactory.createTitledBorder("COMPRAS (Crédito Fiscal)"));
        panelCompras.setBackground(new Color(227, 242, 253));

        lblComprasGravadas = crearLabelSeccion("Compras Gravadas: $0.00");
        lblComprasExentas = crearLabelSeccion("Compras Exentas: $0.00");
        lblCreditoFiscal = crearLabelSeccion("CRÉDITO FISCAL: $0.00");
        lblCreditoFiscal.setFont(new Font("Arial", Font.BOLD, 14));
        lblCreditoFiscal.setForeground(new Color(33, 150, 243));

        panelCompras.add(lblComprasGravadas);
        panelCompras.add(lblComprasExentas);
        panelCompras.add(new JLabel("")); // Espaciador
        panelCompras.add(lblCreditoFiscal);

        // --- Sección RESULTADO ---
        JPanel panelResultado = new JPanel(new GridLayout(5, 1, 5, 5));
        panelResultado.setBorder(BorderFactory.createTitledBorder("RESULTADO"));
        panelResultado.setBackground(new Color(255, 253, 231));

        lblResultadoDebito = crearLabelSeccion("Débito Fiscal: $0.00");
        lblResultadoCredito = crearLabelSeccion("(-) Crédito Fiscal: $0.00");
        lblResultadoRemanente = crearLabelSeccion("(-) Remanente Anterior: $0.00");

        panelResultadoFinal = new JPanel(new BorderLayout());
        panelResultadoFinal.setOpaque(true);
        panelResultadoFinal.setBackground(new Color(255, 253, 231));
        panelResultadoFinal.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, Color.GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        lblResultadoFinal = new JLabel("RESULTADO: $0.00", SwingConstants.CENTER);
        lblResultadoFinal.setFont(new Font("Arial", Font.BOLD, 18));
        panelResultadoFinal.add(lblResultadoFinal, BorderLayout.CENTER);

        panelResultado.add(lblResultadoDebito);
        panelResultado.add(lblResultadoCredito);
        panelResultado.add(lblResultadoRemanente);
        panelResultado.add(new JLabel("")); // Espaciador
        panelResultado.add(panelResultadoFinal);

        panelCentral.add(panelVentas);
        panelCentral.add(panelCompras);
        panelCentral.add(panelResultado);

        // ============ Ensamblar ============
        this.add(panelFiltros, BorderLayout.NORTH);
        this.add(panelCentral, BorderLayout.CENTER);

        // ============ Listeners ============
        btnCalcular.addActionListener(e -> {
            if (controller != null) controller.calcularLiquidacion();
        });
        btnExportar.addActionListener(e -> {
            if (controller != null) controller.exportarLiquidacionCSV();
        });
    }

    private JLabel crearLabelSeccion(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Arial", Font.PLAIN, 13));
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return label;
    }

    // ============ Métodos públicos para el Controller ============

    public int getMes() {
        return cmbMes.getSelectedIndex() + 1;
    }

    public int getAnio() {
        return (int) spnAnio.getValue();
    }

    public BigDecimal getRemanenteMesAnterior() {
        try {
            String texto = txtRemanente.getText().replace("$", "").replace(",", "").trim();
            return new BigDecimal(texto);
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    /**
     * Muestra el resultado de la liquidación en las tres secciones.
     */
    public void mostrarResultado(LiquidacionIva liq) {
        // Sección Ventas
        lblVentasGravadas.setText("Ventas Gravadas: " + FORMATO.format(liq.getTotalVentasGravadas()));
        lblVentasExentas.setText("Ventas Exentas: " + FORMATO.format(liq.getTotalVentasExentas()));
        lblDebitoFiscal.setText("DÉBITO FISCAL: " + FORMATO.format(liq.getTotalDebitoFiscal()));

        // Sección Compras
        lblComprasGravadas.setText("Compras Gravadas: " + FORMATO.format(liq.getTotalComprasGravadas()));
        lblComprasExentas.setText("Compras Exentas: " + FORMATO.format(liq.getTotalComprasExentas()));
        lblCreditoFiscal.setText("CRÉDITO FISCAL: " + FORMATO.format(liq.getTotalCreditoFiscal()));

        // Sección Resultado
        lblResultadoDebito.setText("Débito Fiscal: " + FORMATO.format(liq.getTotalDebitoFiscal()));
        lblResultadoCredito.setText("(-) Crédito Fiscal: " + FORMATO.format(liq.getTotalCreditoFiscal()));
        lblResultadoRemanente.setText("(-) Remanente Anterior: " + FORMATO.format(liq.getRemanenteMesAnterior()));

        if (liq.hayImpuestoAPagar()) {
            lblResultadoFinal.setText("IMPUESTO A PAGAR: " + FORMATO.format(liq.getImpuestoAPagar()));
            lblResultadoFinal.setForeground(new Color(211, 47, 47)); // Rojo
            panelResultadoFinal.setBackground(new Color(255, 235, 238));
        } else {
            lblResultadoFinal.setText("REMANENTE A FAVOR: " + FORMATO.format(liq.getRemanenteAFavor()));
            lblResultadoFinal.setForeground(new Color(46, 125, 50)); // Verde
            panelResultadoFinal.setBackground(new Color(232, 245, 233));
        }
    }

    public void mostrarMensaje(String mensaje, String titulo, int tipo) {
        JOptionPane.showMessageDialog(this, mensaje, titulo, tipo);
    }
}
