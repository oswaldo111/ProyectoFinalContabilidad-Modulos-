package com.sistema.core.security;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.sistema.modulos.contabilidad.Models.DetallePartida;
import com.sistema.modulos.contabilidad.Models.Partida;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class testORM {

   public static void main(String[] args) {
        System.out.println("Iniciando Hibernate...");
        
        // 1. Inicializar la SessionFactory leyendo el hibernate.cfg.xml
        SessionFactory sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();

        // 2. Abrir una sesión
        try (Session session = sessionFactory.openSession()) {
            
            // =========================================================
            // PRUEBA 3: Consultar datos con HQL y Lazy Loading
            // =========================================================
            System.out.println("\n--- LEYENDO DATOS CON HQL ---");
            
            // Usamos HQL para buscar la partida 999. Fíjate que usamos el nombre de la CLASE (Partida)
            List<Partida> partidas = session.createQuery("FROM Partida p WHERE p.numeroPartida = 999", Partida.class).list();

            for (Partida p : partidas) {
                System.out.println("Partida Encontrada: #" + p.getNumeroPartida() + " | Fecha: " + p.getFecha());
                System.out.println("Concepto: " + p.getDescripcionGeneral());
                System.out.println("Detalles:");
                
                // Gracias a las relaciones en las entidades, podemos navegar por los objetos
                for (DetallePartida d : p.getDetalles()) {
                    System.out.println("  -> Cuenta: " + d.getIdCuenta().getNombreCuenta() + 
                                       " | Debe: $" + d.getDebe() + 
                                       " | Haber: $" + d.getHaber());
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Siempre cerrar el SessionFactory al apagar la aplicación
            sessionFactory.close();
            System.out.println("Conexión finalizada.");
        }
    }
}
