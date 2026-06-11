package com.sistema.modulos.contabilidad.Controllers;

import com.sistema.modulos.contabilidad.Controllers.CuentaController;
import com.sistema.modulos.contabilidad.Models.Cuenta;
import java.util.List;

public class PruebaController {

    public static void main(String[] args) {
        
        // Instanciamos el controlador
        CuentaController controlador = new CuentaController();

        System.out.println("--- INICIANDO PRUEBAS LECTURA (SIN MODIFICAR LA BASE) ---\n");

        // 1. Probar LISTAR (Solo lectura)
        System.out.println("--- LISTADO DE CUENTAS ACTUALES ---");
        List<Cuenta> lista = controlador.obtenerTodasLasCuentas();
        if (lista != null) {
            for (Cuenta c : lista) {
                // Imprimimos usando el toString de tu modelo y mostrando su tipo de cuenta
                System.out.println("ID: " + c.getIdCuenta() + " | " + c + " | Tipo: " + c.getTipoCuenta());
            }
        } else {
            System.out.println("No se pudieron recuperar las cuentas o la lista está vacía.");
        }

        // 2. Probar BUSCAR POR ID (Solo lectura - usando el ID 1 que ya vimos que existe)
        System.out.println("\n--- PRUEBA BUSCAR CUENTA POR ID (ID: 1) ---");
        Cuenta cuentaEspecial = controlador.obtenerCuentaPorId(1);
        if (cuentaEspecial != null) {
            System.out.println("¡Cuenta encontrada con éxito!");
            System.out.println("Código: " + cuentaEspecial.getCodigoCuenta());
            System.out.println("Nombre: " + cuentaEspecial.getNombreCuenta());
            System.out.println("Empresa ID: " + cuentaEspecial.getIdEmpresa());
        } else {
            System.out.println("No se encontró ninguna cuenta con el ID especificado.");
        }

        // 3. Cerrar las conexiones de Hibernate de forma segura
        controlador.finalizarConexion();
        System.out.println("\n--- PRUEBAS FINALIZADAS EXITOSAMENTE ---");
    }
}