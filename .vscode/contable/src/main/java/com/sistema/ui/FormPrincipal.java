package com.sistema.ui;

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
import com.sistema.modulos.contabilidad.Controllers.CatalogoController;
import com.sistema.core.security.SessionManager;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

/**
 * FORMULARIO PRINCIPAL UNIFICADO - GT6/GT1
 * Integra Módulos: Fiscal (IVA), Compras y Contabilidad con patrón MVC
 */
public class FormPrincipal extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FormPrincipal.class.getName());

    private FiscalController fiscalController;
    private ProveedorController proveedorController;
    private CompraController compraController;
    private ReporteController reporteController;

    public FormPrincipal() {
        initComponents();
        inicializarUnificado();
    }

    private void inicializarUnificado() {
        configurarVentana();
        crearInterfazUnificada();
    }

    private void configurarVentana() {
        setTitle("TEST MODULOS FISCAL & COMPRAS");
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setExtendedState(MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1200, 700));
    }

    private void crearInterfazUnificada() {
        JPanel panelRaiz = new JPanel(new BorderLayout());
        panelRaiz.setBackground(Color.WHITE);

        JPanel header = crearHeader();

        JTabbedPane tabsModulos = new JTabbedPane(JTabbedPane.TOP);
        tabsModulos.setFont(new Font("Segoe UI", Font.BOLD, 14));

        tabsModulos.addTab("Fiscal (IVA)", crearPanelFiscal());
        tabsModulos.addTab("Compras", crearPanelComprasConTabs());
        tabsModulos.addTab("Contabilidad", crearPanelContabilidad());

        JPanel statusBar = crearStatusBar();

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

        JLabel lblTitulo = new JLabel("TEST MODULOS FISCAL & COMPRAS");
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

        JButton btnAyuda = new JButton("Ayuda");
        btnAyuda.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnAyuda.setBackground(new Color(52, 152, 219));
        btnAyuda.setForeground(Color.WHITE);
        btnAyuda.setFocusPainted(false);
        btnAyuda.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAyuda.addActionListener(e -> mostrarAyuda());

        acciones.add(btnAyuda);
        panel.add(info, BorderLayout.WEST);
        panel.add(acciones, BorderLayout.EAST);
        return panel;
    }

    // ==================== MÓDULO CONTABILIDAD ====================

    private JTabbedPane crearPanelContabilidad() {
        CatalogoCuentas catalogoView = new CatalogoCuentas();
        CatalogoController catalogoController = new CatalogoController(catalogoView);
        catalogoView.setController(catalogoController);
        catalogoController.cargarCatalogo();

        JTabbedPane tabsContabilidad = new JTabbedPane();
        tabsContabilidad.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabsContabilidad.addTab("Catálogo de Cuentas", catalogoView);
        return tabsContabilidad;
    }

    // ==================== MÓDULO FISCAL ====================

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

    // ==================== MÓDULO COMPRAS ====================

    private JTabbedPane crearPanelComprasConTabs() {
        PanelProveedores proveedoresView = new PanelProveedores();
        PanelCompras comprasView = new PanelCompras();
        PanelReporteCompras reporteView = new PanelReporteCompras();

        proveedorController = new ProveedorController();
        compraController = new CompraController();
        reporteController = new ReporteController();

        proveedoresView.setController(proveedorController);
        comprasView.setController(compraController);
        reporteView.setController(reporteController);

        JTabbedPane tabsCompras = new JTabbedPane();
        tabsCompras.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabsCompras.setBackground(new Color(245, 245, 245));
        tabsCompras.addTab("Proveedores", proveedoresView);
        tabsCompras.addTab("Registro de Compras", comprasView);
        tabsCompras.addTab("Libro Compras IVA", reporteView);

        return tabsCompras;
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
                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(color.brighter()); }
            public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(color); }
        });
        return btn;
    }

    private void mostrarAyuda() {
        JOptionPane.showMessageDialog(this,
                "Ayuda:\n• Pestañas superiores = cambiar entre módulos principales\n" +
                "• Dentro de cada módulo = pestañas internas para cada funcionalidad\n" +
                "• La sesión se comparte automáticamente vía SessionManager\n" +
                "• Todos los módulos siguen el patrón MVC para mejor mantenimiento",
                "Ayuda del Sistema", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void abrirDesdeLogin() {
        java.awt.EventQueue.invokeLater(() -> {
            try {
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

    public static void main(String args[]) {
        abrirDesdeLogin();
    }

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