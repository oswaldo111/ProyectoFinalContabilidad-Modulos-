package com.sistema.modulos.ventas.Paneles;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class PruebaLibroVentas {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Prueba JP Libro de Ventas");

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1400, 700);
            frame.setLocationRelativeTo(null);

            JPLibroVentas panel = new JPLibroVentas();

            frame.setContentPane(panel);

            frame.setVisible(true);
        });
    }
}
