package com.sistema.modulos.ventas.Paneles;

import com.sistema.modulos.ventas.Daos.AbonoClienteDAO;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class JDAbonoCliente extends JDialog {

    private final AbonoClienteDAO abonoDAO;
    private final DecimalFormat formatoMoneda;
    private final int idFactura;
    private final double saldoPendiente;

    private boolean abonoRegistrado;

    private JTextField txtIdFactura;
    private JTextField txtCliente;
    private JTextField txtTipoDocumento;
    private JTextField txtNumeroDocumento;
    private JTextField txtMontoTotal;
    private JTextField txtSaldoPendiente;
    private JTextField txtNuevoSaldo;
    private JTextField txtEstadoActual;
    private JTextField txtMontoAbono;
    private JComboBox<String> cmbMetodoPago;
    private JTextField txtReferencia;

    public JDAbonoCliente(
            Frame parent,
            boolean modal,
            int idFactura,
            String nombreCliente,
            String tipoDocumento,
            String numeroDocumento,
            double montoTotal,
            double saldoPendiente,
            String estadoPago
    ) {
        super(parent, modal);
        this.abonoDAO = new AbonoClienteDAO();
        this.formatoMoneda = new DecimalFormat("#,##0.00");
        this.idFactura = idFactura;
        this.saldoPendiente = saldoPendiente;

        initComponentsManual();
        cargarDatosCuenta(nombreCliente, tipoDocumento, numeroDocumento, montoTotal, estadoPago);
    }

    public boolean isAbonoRegistrado() {
        return abonoRegistrado;
    }

    private void initComponentsManual() {
        setTitle("Registrar Abono de Cliente");
        setLayout(new BorderLayout(10, 10));
        setSize(520, 430);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel panelFormulario = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtIdFactura = crearCampoNoEditable(12);
        txtCliente = crearCampoNoEditable(25);
        txtTipoDocumento = crearCampoNoEditable(15);
        txtNumeroDocumento = crearCampoNoEditable(15);
        txtMontoTotal = crearCampoNoEditable(15);
        txtSaldoPendiente = crearCampoNoEditable(15);
        txtNuevoSaldo = crearCampoNoEditable(15);
        txtEstadoActual = crearCampoNoEditable(15);
        txtMontoAbono = new JTextField(15);
        cmbMetodoPago = new JComboBox<>(new String[]{"EFECTIVO", "TRANSFERENCIA", "CHEQUE", "TARJETA", "OTRO"});
        txtReferencia = new JTextField(20);

        agregarCampo(panelFormulario, gbc, 0, "Factura ID:", txtIdFactura);
        agregarCampo(panelFormulario, gbc, 1, "Cliente:", txtCliente);
        agregarCampo(panelFormulario, gbc, 2, "Tipo documento:", txtTipoDocumento);
        agregarCampo(panelFormulario, gbc, 3, "No. documento:", txtNumeroDocumento);
        agregarCampo(panelFormulario, gbc, 4, "Monto total:", txtMontoTotal);
        agregarCampo(panelFormulario, gbc, 5, "Saldo pendiente:", txtSaldoPendiente);
        agregarCampo(panelFormulario, gbc, 6, "Estado actual:", txtEstadoActual);
        agregarCampo(panelFormulario, gbc, 7, "Monto abono:", txtMontoAbono);
        agregarCampo(panelFormulario, gbc, 8, "Metodo pago:", cmbMetodoPago);
        agregarCampo(panelFormulario, gbc, 9, "Referencia:", txtReferencia);
        agregarCampo(panelFormulario, gbc, 10, "Nuevo saldo:", txtNuevoSaldo);

        add(panelFormulario, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGuardar = new JButton("Guardar Abono");
        JButton btnCancelar = new JButton("Cancelar");

        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);

        btnGuardar.addActionListener(e -> guardarAbono());
        btnCancelar.addActionListener(e -> dispose());

        txtMontoAbono.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                actualizarNuevoSaldo();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                actualizarNuevoSaldo();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                actualizarNuevoSaldo();
            }
        });
    }

    private void cargarDatosCuenta(String nombreCliente, String tipoDocumento,
            String numeroDocumento, double montoTotal, String estadoPago) {
        txtIdFactura.setText(String.valueOf(idFactura));
        txtCliente.setText(valorTexto(nombreCliente));
        txtTipoDocumento.setText(valorTexto(tipoDocumento));
        txtNumeroDocumento.setText(valorTexto(numeroDocumento));
        txtMontoTotal.setText(formatoMoneda.format(montoTotal));
        txtSaldoPendiente.setText(formatoMoneda.format(saldoPendiente));
        txtEstadoActual.setText(valorTexto(estadoPago));
        txtNuevoSaldo.setText(formatoMoneda.format(saldoPendiente) + " - " + estadoPagoResultante(saldoPendiente));
    }

    private void guardarAbono() {
        try {
            double montoAbono = validarMontoAbono();
            String metodoPago = obtenerMetodoPago();
            String referencia = txtReferencia.getText() == null ? "" : txtReferencia.getText().trim();

            if (requiereReferencia(metodoPago) && referencia.isEmpty()) {
                int opcion = JOptionPane.showConfirmDialog(
                        this,
                        "No ingreso referencia. Desea continuar?",
                        "Confirmar referencia",
                        JOptionPane.YES_NO_OPTION
                );

                if (opcion != JOptionPane.YES_OPTION) {
                    txtReferencia.requestFocus();
                    return;
                }
            }

            boolean guardado = abonoDAO.registrarAbono(idFactura, montoAbono, metodoPago, referencia);

            if (guardado) {
                abonoRegistrado = true;
                JOptionPane.showMessageDialog(this, "Abono registrado correctamente.");
                dispose();
            }

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Validacion", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException e) {
            mostrarError("No se pudo guardar el abono.", e);
        }
    }

    private double validarMontoAbono() {
        String texto = txtMontoAbono.getText() == null ? "" : txtMontoAbono.getText().trim();

        if (texto.isEmpty()) {
            txtMontoAbono.requestFocus();
            throw new IllegalArgumentException("El monto del abono es obligatorio.");
        }

        double monto;
        try {
            monto = Double.parseDouble(texto.replace(",", ""));
        } catch (NumberFormatException e) {
            txtMontoAbono.requestFocus();
            throw new IllegalArgumentException("El monto del abono debe ser un numero valido.", e);
        }

        if (monto <= 0) {
            txtMontoAbono.requestFocus();
            throw new IllegalArgumentException("El monto del abono debe ser mayor que cero.");
        }

        if (monto > saldoPendiente) {
            txtMontoAbono.requestFocus();
            throw new IllegalArgumentException("El monto del abono no puede ser mayor al saldo pendiente.");
        }

        return monto;
    }

    private void actualizarNuevoSaldo() {
        String texto = txtMontoAbono.getText() == null ? "" : txtMontoAbono.getText().trim();

        if (texto.isEmpty()) {
            txtNuevoSaldo.setText(formatoMoneda.format(saldoPendiente) + " - " + estadoPagoResultante(saldoPendiente));
            return;
        }

        try {
            double monto = Double.parseDouble(texto.replace(",", ""));
            double nuevoSaldo = saldoPendiente - monto;

            if (nuevoSaldo < 0) {
                txtNuevoSaldo.setText("Monto excede saldo");
                return;
            }

            txtNuevoSaldo.setText(formatoMoneda.format(nuevoSaldo) + " - " + estadoPagoResultante(nuevoSaldo));

        } catch (NumberFormatException e) {
            txtNuevoSaldo.setText("Monto invalido");
        }
    }

    private String obtenerMetodoPago() {
        Object seleccionado = cmbMetodoPago.getSelectedItem();
        String metodo = seleccionado == null ? "" : seleccionado.toString().trim();

        if (metodo.isEmpty()) {
            throw new IllegalArgumentException("El metodo de pago es obligatorio.");
        }

        return metodo;
    }

    private boolean requiereReferencia(String metodoPago) {
        return "TRANSFERENCIA".equals(metodoPago)
                || "CHEQUE".equals(metodoPago)
                || "TARJETA".equals(metodoPago);
    }

    private String estadoPagoResultante(double nuevoSaldo) {
        return nuevoSaldo <= 0 ? "PAGADO" : "PARCIAL";
    }

    private JTextField crearCampoNoEditable(int columnas) {
        JTextField campo = new JTextField(columnas);
        campo.setEditable(false);
        return campo;
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc, int fila,
            String etiqueta, java.awt.Component campo) {
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 0;
        panel.add(new JLabel(etiqueta), gbc);

        gbc.gridx = 1;
        gbc.gridy = fila;
        gbc.weightx = 1;
        panel.add(campo, gbc);
    }

    private String valorTexto(String valor) {
        return valor == null ? "" : valor;
    }

    private void mostrarError(String mensaje, Exception e) {
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
}
