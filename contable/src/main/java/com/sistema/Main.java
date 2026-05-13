package com.sistema;

import com.sistema.ui.FormPrincipal;

public class Main {
    public static void main(String[] args) {
        FormPrincipal formPrincipal = new FormPrincipal();
        formPrincipal.setExtendedState(FormPrincipal.MAXIMIZED_BOTH);
        formPrincipal.setLocationRelativeTo(null);
        formPrincipal.setVisible(true);
    }
}