package com.sistema.modulos.contabilidad;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.sistema.modulos.contabilidad.DAO.CuentaDAO;
import com.sistema.modulos.contabilidad.DAO.PartidaDAO;
import com.sistema.modulos.contabilidad.Models.Cuenta;
import com.sistema.modulos.contabilidad.Models.DetallePartida;
import com.sistema.modulos.contabilidad.Models.Partida;

public class TestPartidaDAO {

    public static void main(String[] args) {

        PartidaDAO partidaDAO = new PartidaDAO();
        CuentaDAO cuentaDAO = new CuentaDAO();

        System.out.println("=======================================================");
        System.out.println("  INICIANDO PRUEBAS CRUD (CON LIMPIEZA AUTOMÁTICA)     ");
        System.out.println("=======================================================\n");

        // Obtenemos cuentas reales para poder insertar
        // ¡OJO! Asegúrate de que las cuentas con ID 1 y 2 existan en tu BD
        Cuenta cuentaCaja = cuentaDAO.buscarPorId(1); 
        Cuenta cuentaIngreso = cuentaDAO.buscarPorId(2); 

        if (cuentaCaja == null || cuentaIngreso == null) {
            System.out.println("   [-] ADVERTENCIA: No se encontraron las cuentas. La prueba fallará.");
            return; // Detenemos la prueba si no hay cuentas
        }

        int idGenerado = 0;

        // ==========================================
        // 1. CREAR E INSERTAR
        // ==========================================
        System.out.println("▶ [PRUEBA 1] INSERCIÓN DE NUEVA PARTIDA");
        System.out.println("-------------------------------------------------------");
        
        Partida nuevaPartida = new Partida();
        nuevaPartida.setIdEmpresa(1); // ID de empresa válido
        nuevaPartida.setNumeroPartida(999);
        nuevaPartida.setFecha(new Date());
        nuevaPartida.setDescripcionGeneral("Partida Temporal de Prueba CRUD");
        nuevaPartida.setEstado("BORRADOR");

        // Detalle DEBE
        DetallePartida detalleDebe = new DetallePartida();
        detalleDebe.setIdCuenta(cuentaCaja);
        detalleDebe.setDebe(new BigDecimal("500.00"));
        
        // Detalle HABER
        DetallePartida detalleHaber = new DetallePartida();
        detalleHaber.setIdCuenta(cuentaIngreso);
        detalleHaber.setHaber(new BigDecimal("500.00"));

        nuevaPartida.addDetalle(detalleDebe);
        nuevaPartida.addDetalle(detalleHaber);

        partidaDAO.insertar(nuevaPartida);
        idGenerado = nuevaPartida.getIdPartida(); // Capturamos el ID que la BD le asignó

        System.out.println("   [+] Se insertó exitosamente. ID asignado: " + idGenerado + "\n");


        // Verificamos que se haya generado un ID antes de continuar
        if (idGenerado > 0) {

            // ==========================================
            // 2. BUSCAR LA PARTIDA RECIÉN CREADA
            // ==========================================
            System.out.println("▶ [PRUEBA 2] BÚSQUEDA (ID: " + idGenerado + ")");
            System.out.println("-------------------------------------------------------");
            Partida partidaBuscada = partidaDAO.buscarPorId(idGenerado);
            
            if (partidaBuscada != null) {
                System.out.println("   [+] Partida encontrada.");
                System.out.println("       - Descripción: " + partidaBuscada.getDescripcionGeneral());
                System.out.println("       - Detalles cargados: " + partidaBuscada.getDetalles().size());
            }
            System.out.println();


            // ==========================================
            // 3. ACTUALIZAR
            // ==========================================
            System.out.println("▶ [PRUEBA 3] ACTUALIZACIÓN");
            System.out.println("-------------------------------------------------------");
            partidaBuscada.setEstado("MAYORIZADA");
            partidaBuscada.setDescripcionGeneral("Partida Temporal - ACTUALIZADA");
            partidaDAO.actualizar(partidaBuscada);
            
            // Verificamos recargando
            Partida partidaActualizada = partidaDAO.buscarPorId(idGenerado);
            System.out.println("   [+] Cambio exitoso. Nuevo estado: " + partidaActualizada.getEstado() + "\n");


            // ==========================================
            // 4. LISTAR
            // ==========================================
            System.out.println("▶ [PRUEBA 4] LISTADO GENERAL");
            System.out.println("-------------------------------------------------------");
            List<Partida> listaPartidas = partidaDAO.listar();
            System.out.println("   [+] Total de partidas en la base de datos: " + (listaPartidas != null ? listaPartidas.size() : 0) + "\n");


            // ==========================================
            // 5. ELIMINAR (LIMPIEZA)
            // ==========================================
            System.out.println("▶ [PRUEBA 5] ELIMINACIÓN Y LIMPIEZA");
            System.out.println("-------------------------------------------------------");
            partidaDAO.eliminar(idGenerado);

            // Verificación final
            Partida partidaEliminada = partidaDAO.buscarPorId(idGenerado);
            if (partidaEliminada == null) {
                System.out.println("   [+] LIMPIEZA EXITOSA: La partida " + idGenerado + " y sus detalles han sido borrados.");
            } else {
                System.out.println("   [-] ERROR: La partida sigue existiendo en la base de datos.");
            }
            System.out.println();

        }

        // ==========================================
        // FIN DE PRUEBAS
        // ==========================================
        System.out.println("=======================================================");
        System.out.println("                 CERRANDO CONEXIONES                   ");
        System.out.println("=======================================================");
        PartidaDAO.cerrarFactory();
        CuentaDAO.cerrarFactory(); // Cerramos también el de cuentas por buena práctica
    }
}