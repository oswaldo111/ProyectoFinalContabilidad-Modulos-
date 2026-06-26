package com.sistema.modulos.ventas.Paneles;

import javax.swing.JFrame;

import com.sistema.core.security.SessionManager;
import com.sistema.modulos.ventas.Paneles.JPVentas;

public class TestVentas {

    public static void main(String[] args) {

        // Simular sesión
        SessionManager.getInstancia().iniciarSesion(
                1,
                "Empresa Demo",
                1,
                "Administrador"
        );
        JFrame frame = new JFrame();

        frame.setTitle("Módulo Ventas");

        frame.setSize(1200, 700);

        frame.setLocationRelativeTo(null);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setContentPane(new JPVentas());

        frame.setVisible(true);
    }
}
