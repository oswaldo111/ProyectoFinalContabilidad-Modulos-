package com.sistema;

import com.sistema.ui.FormPrincipal;
import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
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

        EventQueue.invokeLater(() -> {
            FormPrincipal formPrincipal = new FormPrincipal();
            formPrincipal.setExtendedState(FormPrincipal.MAXIMIZED_BOTH);
            formPrincipal.setLocationRelativeTo(null);
            formPrincipal.setVisible(true);
        });
    }
}