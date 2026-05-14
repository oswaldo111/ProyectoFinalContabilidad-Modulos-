package com.sistema.modulos.compras;

import com.sistema.modulos.compras.Controllers.CompraController;
import com.sistema.modulos.compras.Controllers.ProveedorController;
import com.sistema.modulos.compras.Controllers.ReporteController;
import com.sistema.modulos.compras.Views.PanelCompras;
import com.sistema.modulos.compras.Views.PanelProveedores;
import com.sistema.modulos.compras.Views.PanelReporteCompras;

import javax.swing.*;
import java.awt.*;

/**
 * Formulario principal del módulo de Compras.
 * Implementa JTabbedPane con inyección de controllers individuales.
 */
public class MainSimple extends JFrame {

    public MainSimple() {
        // Configurar la ventana
        setTitle("Módulo de Compras - Grupo 1");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        // 1. Crear las vistas
        PanelProveedores proveedoresView = new PanelProveedores();
        PanelCompras comprasView = new PanelCompras();
        PanelReporteCompras reporteView = new PanelReporteCompras();

        // 2. Crear controllers individuales (uno por vista)
        ProveedorController proveedorCtrl = new ProveedorController();
        CompraController compraCtrl = new CompraController();
        ReporteController reporteCtrl = new ReporteController();

        // 3. Inyectar cada controller en su vista correspondiente
        proveedoresView.setController(proveedorCtrl);
        comprasView.setController(compraCtrl);
        reporteView.setController(reporteCtrl);

        // 4. Crear JTabbedPane con las vistas configuradas
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tabbedPane.setBackground(new Color(245, 245, 245));

        tabbedPane.addTab("Proveedores", proveedoresView);
        tabbedPane.addTab("Compras", comprasView);
        tabbedPane.addTab("Libro de Compras IVA", reporteView);

        // 5. Panel de información superior
        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        panelInfo.setBackground(new Color(63, 81, 181));
        panelInfo.setPreferredSize(new Dimension(0, 35));

        JLabel lblTitulo = new JLabel("MÓDULO DE COMPRAS — Grupo 1");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelInfo.add(lblTitulo);

        JLabel lblEmpresa = new JLabel("| Empresa ID: " +
                com.sistema.core.security.SessionManager.getIdEmpresa());
        lblEmpresa.setForeground(new Color(200, 200, 255));
        lblEmpresa.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panelInfo.add(lblEmpresa);

        // 6. Barra de estado inferior
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        statusBar.setBackground(new Color(52, 73, 94));

        JLabel lblStatus = new JLabel("Módulo de Compras - Grupo 1 | BY DM");
        lblStatus.setForeground(Color.WHITE);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusBar.add(lblStatus, BorderLayout.WEST);

        // 7. Ensamblar componentes
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panelInfo, BorderLayout.NORTH);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
        getContentPane().add(statusBar, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        // Aplicar Look and Feel Nimbus
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        SwingUtilities.invokeLater(() -> {
            MainSimple frame = new MainSimple();
            frame.setVisible(true);
        });
    }
}