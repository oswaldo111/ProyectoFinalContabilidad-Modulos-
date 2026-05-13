package com.sistema.modulos.fiscal;

import com.sistema.modulos.fiscal.Controllers.FiscalController;
import com.sistema.modulos.fiscal.Views.LibroComprasView;
import com.sistema.modulos.fiscal.Views.LibroVentasView;
import com.sistema.modulos.fiscal.Views.LiquidacionView;

import javax.swing.*;
import java.awt.*;

/**
 * Formulario de prueba para el módulo de Cumplimiento Fiscal (IVA).
 * Contiene los 3 paneles en pestañas para verificar su funcionamiento.
 * Este archivo es solo para pruebas internas del grupo y no afecta a los demás módulos.
 */
public class frmPruebaIVA extends JFrame {

    public frmPruebaIVA() {
        // Configurar la ventana
        setTitle("Módulo Fiscal IVA - Prueba de Vistas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1050, 700);
        setLocationRelativeTo(null);

        // Crear las 3 vistas
        LibroVentasView ventasView = new LibroVentasView();
        LibroComprasView comprasView = new LibroComprasView();
        LiquidacionView liquidacionView = new LiquidacionView();

        // Crear el controlador e inyectarlo en las vistas
        FiscalController controller = new FiscalController(ventasView, comprasView, liquidacionView);
        ventasView.setController(controller);
        comprasView.setController(controller);
        liquidacionView.setController(controller);

        // Crear las pestañas con iconos Unicode
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 13));

        tabbedPane.addTab("📗 Libro de Ventas", ventasView);
        tabbedPane.addTab("📘 Libro de Compras", comprasView);
        tabbedPane.addTab("📊 Liquidación IVA", liquidacionView);

        // Panel de info en la parte superior
        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        panelInfo.setBackground(new Color(63, 81, 181));
        panelInfo.setPreferredSize(new Dimension(0, 35));

        JLabel lblTitulo = new JLabel("MÓDULO DE CUMPLIMIENTO FISCAL (IVA) — Grupo 6");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        panelInfo.add(lblTitulo);

        JLabel lblEmpresa = new JLabel("| Empresa ID: " +
                com.sistema.core.security.SessionManager.getIdEmpresa());
        lblEmpresa.setForeground(new Color(200, 200, 255));
        lblEmpresa.setFont(new Font("Arial", Font.PLAIN, 12));
        panelInfo.add(lblEmpresa);

        // Ensamblar
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(panelInfo, BorderLayout.NORTH);
        getContentPane().add(tabbedPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        // Usar Look and Feel Nimbus para que se vea más profesional
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Si Nimbus no está disponible, usar el default
        }

        // Lanzar la ventana
        SwingUtilities.invokeLater(() -> {
            frmPruebaIVA frame = new frmPruebaIVA();
            frame.setVisible(true);
        });
    }
}
