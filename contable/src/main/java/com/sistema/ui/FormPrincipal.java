/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.sistema.ui;

import com.sistema.modulos.fiscal.Controllers.FiscalController;
import com.sistema.modulos.fiscal.Views.LibroComprasView;
import com.sistema.modulos.fiscal.Views.LibroVentasView;
import com.sistema.modulos.fiscal.Views.LiquidacionView;
import com.sistema.modulos.compras.Views.PanelCompras;
import com.sistema.modulos.compras.Views.PanelProveedores;
import com.sistema.modulos.compras.Views.PanelReporteCompras;
import com.sistema.core.security.SessionManager;
import com.sistema.modulos.bancos.Controllers.BancosController;
import com.sistema.modulos.bancos.Views.ConciliacionView;
import com.sistema.modulos.bancos.Views.CuentasBancariasView;
import com.sistema.modulos.bancos.Views.MovimientosView;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
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
 * Integra Módulos: Fiscal (IVA) y Compras
 * Compatible con com.sistema.core.security.SessionManager
 * 
 * @author R5 8500G
 */
public class FormPrincipal extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(FormPrincipal.class.getName());
    
    // Referencias para comunicación entre módulos (opcional)
    private FiscalController fiscalController;
    private BancosController bancosController;
    private PanelCompras panelCompras;
    private PanelProveedores panelProveedores;

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
        setTitle("TEST MODULOS FISCAL & COMPRAS");
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setExtendedState(MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(1200, 700));
    }

    private void crearInterfazUnificada() {
        // Panel principal con BorderLayout para organizar header, tabs y status
        JPanel panelRaiz = new JPanel(new BorderLayout());
        panelRaiz.setBackground(Color.WHITE);
        
        // 1. Header superior
        JPanel header = crearHeader();
        
        // 2. Pestañas de módulos (solo los que existen)
        JTabbedPane tabsModulos = new JTabbedPane(JTabbedPane.TOP);
        tabsModulos.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabsModulos.addTab("Módulo Fiscal (IVA)", crearPanelFiscal());
        tabsModulos.addTab("Módulo de Compras", crearPanelComprasUnificado());
        tabsModulos.addTab("Módulo de Bancos", crearPanelBancos());
        
        // 3. Status bar inferior
        JPanel statusBar = crearStatusBar();
        
        // Ensamblar
        panelRaiz.add(header, BorderLayout.NORTH);
        panelRaiz.add(tabsModulos, BorderLayout.CENTER);
        panelRaiz.add(statusBar, BorderLayout.SOUTH);
        
        // Reemplazar el contenido del JFrame con nuestro panel unificado
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
        tabsFiscal.addTab("Liquidación IVA", liquidacionView);

        return tabsFiscal;
    }

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
        tabsBancos.addTab("Conciliación", conciliacionView);

        return tabsBancos;
    }

    private JPanel crearPanelComprasUnificado() {
        JPanel panelPrincipal = new JPanel(new CardLayout());

        panelProveedores = new PanelProveedores();
        panelCompras = new PanelCompras();
        PanelReporteCompras panelReporte = new PanelReporteCompras();

        panelPrincipal.add(panelProveedores, "PROVEEDORES");
        panelPrincipal.add(panelCompras, "COMPRAS");
        panelPrincipal.add(panelReporte, "REPORTE");

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 8));
        panelBotones.setBackground(new Color(236, 240, 241));
        panelBotones.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JButton btnProv = crearBotonNavegacion("Proveedores", new Color(52, 152, 219));
        JButton btnComp = crearBotonNavegacion("Compras", new Color(46, 204, 113));
        JButton btnRep = crearBotonNavegacion("Libro Compras IVA", new Color(241, 196, 15));

        btnProv.addActionListener(e -> ((CardLayout)panelPrincipal.getLayout()).show(panelPrincipal, "PROVEEDORES"));
        btnComp.addActionListener(e -> ((CardLayout)panelPrincipal.getLayout()).show(panelPrincipal, "COMPRAS"));
        btnRep.addActionListener(e -> ((CardLayout)panelPrincipal.getLayout()).show(panelPrincipal, "REPORTE"));

        panelBotones.add(btnProv);
        panelBotones.add(btnComp);
        panelBotones.add(btnRep);

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.add(panelBotones, BorderLayout.NORTH);
        contenedor.add(panelPrincipal, BorderLayout.CENTER);
        return contenedor;
    }

    private JPanel crearStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        bar.setBackground(new Color(44, 62, 80));
        
        String usuario = SessionManager.getNombreUsuario();
        String displayUser = (usuario != null && !usuario.trim().isEmpty()) ? usuario : "Invitado";
        String estado = SessionManager.haySesionActiva() ? "Conectado" : "Sesion no iniciada";
            
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
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(color.brighter()); }
            public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(color); }
        });
        return btn;
    }

    private void mostrarAyuda() {
        JOptionPane.showMessageDialog(this,
            "Ayuda:\n• Pestañas superiores = cambiar módulo\n" +
            "• Botones en Compras = navegar entre paneles\n" +
            "• La sesión se comparte automáticamente vía SessionManager",
            "Ayuda", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Método para llamar desde otro Main o clase externa
     * Ejemplo: FormPrincipal.abrirDesdeLogin();
     */
    public static void abrirDesdeLogin() {
        java.awt.EventQueue.invokeLater(() -> {
            try {
                // Aplicar Look and Feel antes de crear la ventana
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