package com.sistema.modulos.ventas.Paneles;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class pruebaCliente {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Prueba JPCliente");

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1200, 700);
            frame.setLocationRelativeTo(null);

            JPCliente panel = new JPCliente();

            frame.setContentPane(panel);

            frame.setVisible(true);
        });
    }
}