/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.sistema.ui;

import com.sistema.modulos.bancos.Controllers.BancosController;
import com.sistema.modulos.bancos.Views.ConciliacionView;
import com.sistema.modulos.bancos.Views.CuentasBancariasView;
import com.sistema.modulos.bancos.Views.MovimientosView;

import com.sistema.modulos.fiscal.Controllers.FiscalController;
import com.sistema.modulos.fiscal.Views.LibroComprasView;
import com.sistema.modulos.fiscal.Views.LibroVentasView;
import com.sistema.modulos.fiscal.Views.LiquidacionView;

import com.sistema.modulos.compras.Controllers.CompraController;
import com.sistema.modulos.compras.Controllers.ProveedorController;
import com.sistema.modulos.compras.Controllers.ReporteController;
import com.sistema.modulos.compras.Views.PanelCompras;
import com.sistema.modulos.compras.Views.PanelProveedores;
import com.sistema.modulos.compras.Views.PanelReporteCompras;
import com.sistema.modulos.contabilidad.Views.CatalogoCuentas;

import com.sistema.modulos.ventas.Paneles.JPCliente;
import com.sistema.modulos.ventas.Paneles.JPVentas;
import com.sistema.modulos.ventas.Paneles.JPCuentasPorCobrar;
import com.sistema.modulos.ventas.Paneles.JPLibroVentas;
import com.sistema.core.DBConnection;
import com.sistema.core.security.SessionManager;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * FORMULARIO PRINCIPAL UNIFICADO - GT6/GT1 Integra Módulos: Fiscal (IVA) y
 * Compras con patrón MVC y JTabbedPane Compatible con
 * com.sistema.core.security.SessionManager
 *
 * @author R5 8500G
 */
public class FormPrincipal extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FormPrincipal.class.getName());

    // Referencias para comunicación entre módulos (opcional)
    private FiscalController fiscalController;
    private ProveedorController proveedorController;
    private CompraController compraController;
    private ReporteController reporteController;
    private BancosController bancosController;

    /**
     * Creates new form FormPrincipal
     */
    public FormPrincipal() {
        initComponents();
        inicializarUnificado();
    }

    /**
     * Inicializa la interfaz unificada con módulos integrados
     */
    private void inicializarUnificado() {
        configurarVentana();
        crearInterfazUnificada();
    }

    private void configurarVentana() {
        setTitle("Sistema Contable");
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setExtendedState(MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1200, 700));
    }

    private void crearInterfazUnificada() {
        JPanel panelRaiz = new JPanel(new BorderLayout());
        panelRaiz.setBackground(Color.WHITE);

        // 1. Header superior
        JPanel header = crearHeader();

        // 2. Pestañas principales de módulos
        JTabbedPane tabsModulos = new JTabbedPane(JTabbedPane.TOP);
        tabsModulos.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // Módulo Fiscal (IVA) - ya con MVC y JTabbedPane interno
        tabsModulos.addTab("Fiscal (IVA)", crearPanelFiscal());

        // Módulo de Compras - ahora con JTabbedPane interno (sin CardLayout)
        tabsModulos.addTab("Compras", crearPanelComprasConTabs());

        tabsModulos.addTab("Ventas", crearPanelVentas());

        tabsModulos.addTab("Bancos", crearPanelBancos());

        tabsModulos.addTab("Contabilidad", crearPnaleContabilidad());

        // 3. Status bar inferior
        JPanel statusBar = crearStatusBar();

        // Ensamblar
        panelRaiz.add(header, BorderLayout.NORTH);
        panelRaiz.add(tabsModulos, BorderLayout.CENTER);
        panelRaiz.add(statusBar, BorderLayout.SOUTH);

        setContentPane(panelRaiz);
        revalidate();
        repaint();
    }

    private JPanel crearHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(44, 62, 80));
        panel.setPreferredSize(new Dimension(0, 50));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 15));

        JPanel info = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        info.setOpaque(false);

        JLabel lblTitulo = new JLabel("MODULOS CONTABLES");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 16));

        int idEmpresa = SessionManager.getIdEmpresa();
        String nombreEmpresa = SessionManager.getNombreEmpresa();
        String txtEmpresa = (!nombreEmpresa.isEmpty())
                ? nombreEmpresa + " (ID: " + idEmpresa + ")"
                : "Empresa ID: " + idEmpresa;

        JLabel lblEmpresa = new JLabel(" | " + txtEmpresa);
        lblEmpresa.setForeground(new Color(189, 195, 199));
        lblEmpresa.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        info.add(lblTitulo);
        info.add(lblEmpresa);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        acciones.setOpaque(false);

        JButton btnCerrarSesion = new JButton("Cerrar Sesi\u00F3n");
        btnCerrarSesion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnCerrarSesion.setBackground(new Color(192, 57, 43));
        btnCerrarSesion.setForeground(Color.WHITE);
        btnCerrarSesion.setFocusPainted(false);
        btnCerrarSesion.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCerrarSesion.addActionListener(e -> cerrarSesion());

        acciones.add(btnCerrarSesion);
        panel.add(info, BorderLayout.WEST);
        panel.add(acciones, BorderLayout.EAST);
        return panel;
    }

    private JTabbedPane crearPnaleContabilidad() {

        JTabbedPane tabsContabilidad = new JTabbedPane();
        tabsContabilidad.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabsContabilidad.addTab("Catálogo de Cuentas", new CatalogoCuentas());
        tabsContabilidad.addTab("Registro de Partidas", new com.sistema.modulos.contabilidad.Views.PanelRegistroPartida());
        return tabsContabilidad;
    }
    // ==================== MÓDULO FISCAL (Sin cambios - ya está bien) ====================

    private JTabbedPane crearPanelFiscal() {
        LibroVentasView ventasView = new LibroVentasView();
        LibroComprasView comprasView = new LibroComprasView();
        LiquidacionView liquidacionView = new LiquidacionView();

        fiscalController = new FiscalController(ventasView, comprasView, liquidacionView);
        ventasView.setController(fiscalController);
        comprasView.setController(fiscalController);
        liquidacionView.setController(fiscalController);

        JTabbedPane tabsFiscal = new JTabbedPane();
        tabsFiscal.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabsFiscal.addTab("Libro de Ventas", ventasView);
        tabsFiscal.addTab("Libro de Compras", comprasView);
        tabsFiscal.addTab("Liquidacion IVA", liquidacionView);

        return tabsFiscal;
    }

    // ==================== MÓDULO COMPRAS (MODIFICADO: JTabbedPane + MVC) ====================
    private JTabbedPane crearPanelComprasConTabs() {
        // 1. Crear las vistas del módulo Compras
        PanelProveedores proveedoresView = new PanelProveedores();
        PanelCompras comprasView = new PanelCompras();
        PanelReporteCompras reporteView = new PanelReporteCompras();

        // 2. Crear controllers individuales para cada vista
        proveedorController = new ProveedorController();
        compraController = new CompraController();
        reporteController = new ReporteController();

        // 3. Inyectar controllers en las vistas (patrón MVC)
        proveedoresView.setController(proveedorController);
        comprasView.setController(compraController);
        reporteView.setController(reporteController);

        // 4. Crear JTabbedPane interno para el módulo Compras
        JTabbedPane tabsCompras = new JTabbedPane();
        tabsCompras.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabsCompras.setBackground(new Color(245, 245, 245));

        // 5. Agregar vistas como pestañas con iconos Unicode
        tabsCompras.addTab("Proveedores", proveedoresView);
        tabsCompras.addTab("Registro de Compras", comprasView);
        tabsCompras.addTab("Libro Compras IVA", reporteView);

        return tabsCompras;
    }

    //Para el modulo Ventas
    private JTabbedPane crearPanelVentas() {

        JTabbedPane tabsVentas = new JTabbedPane();
        tabsVentas.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JPCliente panelClientes = new JPCliente();
        JPVentas panelVentas = new JPVentas();
        JPCuentasPorCobrar panelCuentas = new JPCuentasPorCobrar();
        JPLibroVentas panelLibro = new JPLibroVentas();

        tabsVentas.addTab("Clientes", panelClientes);
        tabsVentas.addTab("Ventas", panelVentas);
        tabsVentas.addTab("Cuentas por Cobrar", panelCuentas);
        tabsVentas.addTab("Libro de Ventas", panelLibro);

        tabsVentas.addChangeListener(e -> {
            int index = tabsVentas.getSelectedIndex();

            if (index == 1) {
                tabsVentas.setComponentAt(1, new JPVentas());
            } else if (index == 2) {
                tabsVentas.setComponentAt(2, new JPCuentasPorCobrar());
            } else if (index == 3) {
                tabsVentas.setComponentAt(3, new JPLibroVentas());
            }
        });

        return tabsVentas;
    }

    // ==================== MÓDULO BANCOS ====================
    private JTabbedPane crearPanelBancos() {
        CuentasBancariasView cuentasView = new CuentasBancariasView();
        MovimientosView movimientosView = new MovimientosView();
        ConciliacionView conciliacionView = new ConciliacionView();

        bancosController = new BancosController(cuentasView, movimientosView, conciliacionView);
        cuentasView.setController(bancosController);
        movimientosView.setController(bancosController);
        conciliacionView.setController(bancosController);

        JTabbedPane tabsBancos = new JTabbedPane();
        tabsBancos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabsBancos.addTab("Cuentas Bancarias", cuentasView);
        tabsBancos.addTab("Movimientos", movimientosView);
        tabsBancos.addTab("Conciliaci\u00F3n", conciliacionView);

        return tabsBancos;
    }

    // ==================== COMPONENTES COMUNES ====================
    private JPanel crearStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        bar.setBackground(new Color(44, 62, 80));

        String usuario = SessionManager.getNombreUsuario();
        String displayUser = (usuario != null && !usuario.trim().isEmpty()) ? usuario : "Invitado";
        String estado = SessionManager.haySesionActiva() ? "Conectado" : "Sesión no iniciada";

        JLabel lblEstado = new JLabel(estado + " | Usuario: " + displayUser);
        lblEstado.setForeground(new Color(236, 240, 241));
        lblEstado.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        bar.add(lblEstado, BorderLayout.WEST);
        return bar;
    }

    private JButton crearBotonNavegacion(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btn.setBackground(color);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 2),
                BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(color.brighter());
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(color);
            }
        });
        return btn;
    }

    private void cerrarSesion() {
        List<Integer> empresas = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement("SELECT DISTINCT id_empresa FROM entidades ORDER BY id_empresa"); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                empresas.add(rs.getInt(1));
            }
        } catch (Exception e) {
            empresas.add(1);
            empresas.add(2);
        }
        if (empresas.isEmpty()) {
            empresas.add(1);
            empresas.add(2);
        }

        JComboBox<Integer> comboEmpresas = new JComboBox<>(empresas.toArray(new Integer[0]));

        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JLabel lblMensaje = new JLabel("Seleccione con qu\u00E9 empresa desea iniciar sesi\u00F3n:");
        lblMensaje.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(lblMensaje, BorderLayout.NORTH);
        panel.add(comboEmpresas, BorderLayout.CENTER);

        int opcion = JOptionPane.showConfirmDialog(this, panel,
                "Cerrar Sesi\u00F3n", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) {
            return;
        }

        try {
            int idEmpresa = Integer.parseInt(comboEmpresas.getSelectedItem().toString().trim());
            if (idEmpresa <= 0) {
                throw new NumberFormatException();
            }
            SessionManager.iniciarSesion(idEmpresa, "Empresa " + idEmpresa, 1, "usuario");

            dispose();

            JDialog loading = new JDialog((Frame) null, "Cargando", false);
            JPanel panelLoading = new JPanel(new BorderLayout(10, 10));
            panelLoading.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
            JLabel lblCargando = new JLabel("Cargando empresa, espere...");
            lblCargando.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            JProgressBar barra = new JProgressBar(0, 100);
            barra.setStringPainted(true);
            barra.setFont(new Font("Segoe UI", Font.BOLD, 11));
            panelLoading.add(lblCargando, BorderLayout.NORTH);
            panelLoading.add(barra, BorderLayout.CENTER);
            loading.add(panelLoading);
            loading.setSize(300, 120);
            loading.setLocationRelativeTo(null);
            loading.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
            loading.setVisible(true);

            new Thread(() -> {
                for (int i = 0; i <= 100; i++) {
                    final int p = i;
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                        break;
                    }
                    SwingUtilities.invokeLater(() -> barra.setValue(p));
                }
                SwingUtilities.invokeLater(() -> {
                    FormPrincipal nuevo = new FormPrincipal();
                    nuevo.setExtendedState(FormPrincipal.MAXIMIZED_BOTH);
                    nuevo.setLocationRelativeTo(null);
                    nuevo.setVisible(true);
                    loading.dispose();
                });
            }).start();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID de empresa inv\u00E1lido", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Método para llamar desde otro Main o clase externa Ejemplo:
     * FormPrincipal.abrirDesdeLogin();
     */
    public static void abrirDesdeLogin() {
        java.awt.EventQueue.invokeLater(() -> {
            try {
                // Aplicar Look and Feel Nimbus
                for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception ex) {
                logger.log(java.util.logging.Level.WARNING, "Look and Feel error", ex);
            }

            FormPrincipal form = new FormPrincipal();
            form.setVisible(true);
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        // Opcional: Simular sesión para pruebas directas
        // SessionManager.iniciarSesion(101, "Mi Empresa SA", 5, "admin");

        abrirDesdeLogin();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
