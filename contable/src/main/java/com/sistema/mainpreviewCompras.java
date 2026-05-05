package com.sistema;

import com.sistema.core.security.SessionManager;
import com.sistema.modulos.compras.Views.FacturaCompraView;
import com.sistema.modulos.compras.Views.PagoView;
import com.sistema.modulos.compras.Views.ProveedorView;

import javax.swing.*;
import java.awt.*;

public class mainpreviewCompras {

    private static final String CARD_PROVEEDORES = "PROVEEDORES";
    private static final String CARD_FACTURAS = "FACTURAS";
    private static final String CARD_PAGOS = "PAGOS";
    
    private static JPanel pnlContenedor;

    public static void main(String[] args) {
        JOptionPane.showMessageDialog(null,"PREVIEW MÓDULO DE COMPRAS\n\nESperar 5 sgd en cargar", "LA BD CARGA LENTO", JOptionPane.INFORMATION_MESSAGE  );
        SessionManager.getInstancia().iniciarSesion(1L, "Empresa Demo S.A.", 1L, "Admin");
        SwingUtilities.invokeLater(() -> crearYMostrarVentana());
    }

    private static void crearYMostrarVentana() {
        JFrame frm = new JFrame("SISTEMA GT 1 - Módulo de Compras");
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frm.setResizable(false);
        
        frm.setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        frm.setLayout(new BorderLayout());

        JPanel pnlHeader = crearHeader();
        frm.add(pnlHeader, BorderLayout.NORTH);

        pnlContenedor = new JPanel(new CardLayout());
        pnlContenedor.setBackground(new Color(245, 245, 245));

        pnlContenedor.add(new ProveedorView(), CARD_PROVEEDORES);
        pnlContenedor.add(new FacturaCompraView(), CARD_FACTURAS);
        pnlContenedor.add(new PagoView(), CARD_PAGOS);

        frm.add(pnlContenedor, BorderLayout.CENTER);
        frm.setVisible(true);
        
        cambiarVista(CARD_PROVEEDORES);
    }

    private static JPanel crearHeader() {
        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        pnlHeader.setBackground(new Color(52, 73, 94));
        pnlHeader.setPreferredSize(new Dimension(0, 50));

        JLabel lblTitulo = new JLabel("SISTEMA DE GESTIÓN");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setForeground(Color.WHITE);
        pnlHeader.add(lblTitulo);
        pnlHeader.add(Box.createHorizontalStrut(20));

        JButton btnProveedores = crearBotonHeader("Proveedores", CARD_PROVEEDORES);
        JButton btnFacturas = crearBotonHeader("Facturas", CARD_FACTURAS);
        JButton btnPagos = crearBotonHeader("Pagos", CARD_PAGOS);

        pnlHeader.add(btnProveedores);
        pnlHeader.add(btnFacturas);
        pnlHeader.add(btnPagos);
        pnlHeader.add(Box.createHorizontalGlue());

        JLabel lblUsuario = new JLabel("BYDM");
        lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblUsuario.setForeground(new Color(200, 200, 200));
        pnlHeader.add(lblUsuario);

        return pnlHeader;
    }

    private static JButton crearBotonHeader(String texto, String cardName) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btn.setBackground(new Color(70, 130, 180));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(110, 28));
        btn.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
        
        btn.addActionListener(e -> cambiarVista(cardName));
        
        return btn;
    }

    public static void cambiarVista(String cardName) {
        if (pnlContenedor != null) {
            CardLayout cl = (CardLayout) pnlContenedor.getLayout();
            cl.show(pnlContenedor, cardName);
        }
    }
}