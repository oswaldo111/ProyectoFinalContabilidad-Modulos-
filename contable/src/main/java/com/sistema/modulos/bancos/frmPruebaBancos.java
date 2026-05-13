package com.sistema.modulos.bancos;

import com.sistema.core.security.SessionManager;
import com.sistema.modulos.bancos.Controllers.BancosController;
import com.sistema.modulos.bancos.Views.ConciliacionView;
import com.sistema.modulos.bancos.Views.CuentasBancariasView;
import com.sistema.modulos.bancos.Views.MovimientosView;

import javax.swing.*;
import java.awt.*;

/**
 * Formulario de prueba para el módulo de Bancos (Grupo 4).
 * Contiene los 3 paneles en pestañas para verificar su funcionamiento.
 * Este archivo es solo para pruebas internas del grupo y no afecta a los demás módulos.
 */
public class frmPruebaBancos extends JFrame {

    public frmPruebaBancos() {
        // Configurar la ventana
        setTitle("Módulo de Bancos - Prueba de Vistas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);

        // --- Panel de Información (Simula el header del sistema principal) ---
        JPanel panelInfo = new JPanel(new BorderLayout());
        panelInfo.setBackground(new Color(15, 52, 96)); // Azul oscuro
        panelInfo.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel lblTitulo = new JLabel("MÓDULO DE BANCOS — Grupo 4");
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 18));
        
        JLabel lblEmpresa = new JLabel("Empresa Activa ID: " + SessionManager.getIdEmpresa());
        lblEmpresa.setForeground(new Color(233, 69, 96)); // Rojo rosado
        lblEmpresa.setFont(new Font("Arial", Font.BOLD, 14));

        panelInfo.add(lblTitulo, BorderLayout.WEST);
        panelInfo.add(lblEmpresa, BorderLayout.EAST);
        add(panelInfo, BorderLayout.NORTH);

        // --- Crear las 3 vistas (JPanels) ---
        CuentasBancariasView cuentasView = new CuentasBancariasView();
        MovimientosView movimientosView = new MovimientosView();
        ConciliacionView conciliacionView = new ConciliacionView();

        // --- Crear el controlador e inyectarlo en las vistas ---
        BancosController controller = new BancosController(cuentasView, movimientosView, conciliacionView);
        cuentasView.setController(controller);
        movimientosView.setController(controller);
        conciliacionView.setController(controller);

        // --- Configurar las pestañas ---
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));
        
        tabbedPane.addTab("🏦 Cuentas Bancarias", cuentasView);
        tabbedPane.addTab("💳 Movimientos", movimientosView);
        tabbedPane.addTab("📊 Conciliación", conciliacionView);

        add(tabbedPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        // Configurar Look & Feel Nimbus para mejor estética (igual que el main general)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("No se pudo aplicar el Look&Feel Nimbus.");
        }

        // Forzar inicialización de sesión para la prueba (simulando login)
        // El idEmpresa 1 y 2 existen en la BD de prueba
        System.out.println("Iniciando prueba con Empresa ID = 1");

        SwingUtilities.invokeLater(() -> {
            new frmPruebaBancos().setVisible(true);
        });
    }
}
