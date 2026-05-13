package com.sistema.modulos.compras;

import com.sistema.modulos.compras.Views.PanelCompras;
import com.sistema.modulos.compras.Views.PanelProveedores;
import com.sistema.modulos.compras.Views.PanelReporteCompras;

import javax.swing.*;
import java.awt.*;

public class MainSimple extends JFrame {

    public MainSimple() {
        setTitle("Módulo de Compras - Grupo 1");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setResizable(false);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);

        CardLayout cardLayout = new CardLayout();
        JPanel panelPrincipal = new JPanel(cardLayout);

        PanelProveedores panelProveedores = new PanelProveedores();
        PanelCompras panelCompras = new PanelCompras();
        PanelReporteCompras panelReporte = new PanelReporteCompras();

        panelPrincipal.add(panelProveedores, "PROVEEDORES");
        panelPrincipal.add(panelCompras, "COMPRAS");
        panelPrincipal.add(panelReporte, "REPORTE");

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        panelBotones.setBackground(new Color(60, 63, 65));

        JButton btnProveedores = crearBoton("Proveedores", new Color(52, 152, 219));
        JButton btnCompras = crearBoton("Compras", new Color(46, 204, 113));
        JButton btnReporte = crearBoton("Libro de Compras IVA", new Color(241, 196, 15));

        btnProveedores.addActionListener(e -> cardLayout.show(panelPrincipal, "PROVEEDORES"));
        btnCompras.addActionListener(e -> cardLayout.show(panelPrincipal, "COMPRAS"));
        btnReporte.addActionListener(e -> cardLayout.show(panelPrincipal, "REPORTE"));

        panelBotones.add(btnProveedores);
        panelBotones.add(btnCompras);
        panelBotones.add(btnReporte);

        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        statusBar.setBackground(new Color(52, 73, 94));

        JLabel lblStatus = new JLabel("Módulo de Compras - Grupo 1 | BY DM");
        lblStatus.setForeground(Color.WHITE);
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusBar.add(lblStatus, BorderLayout.WEST);

        add(panelBotones, BorderLayout.NORTH);
        add(panelPrincipal, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }

    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        boton.setBackground(color);
        boton.setForeground(Color.BLACK);
        boton.setFocusPainted(false);
        boton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return boton;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> new MainSimple().setVisible(true));
    }
}