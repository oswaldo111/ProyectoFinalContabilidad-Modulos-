package com.sistema.modulos.ventas.Paneles;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class pruebaCuentasPorCobrar {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Prueba JP Cuentas por Cobrar");

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1400, 700);
            frame.setLocationRelativeTo(null);

            JPCuentasPorCobrar panel = new JPCuentasPorCobrar();

            frame.setContentPane(panel);

            frame.setVisible(true);
        });
    }
}