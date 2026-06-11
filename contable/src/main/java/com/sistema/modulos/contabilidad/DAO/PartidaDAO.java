package com.sistema.modulos.contabilidad.DAO;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.sistema.modulos.contabilidad.Models.Partida;

public class PartidaDAO {

    // SessionFactory
    private static final SessionFactory sessionFactory =
            new Configuration()
                    .configure("hibernate.cfg.xml")
                    .buildSessionFactory();



    // =========================
    // INSERTAR
    // =========================
    public void insertar(Partida partida) {

        // El try-with-resources asegura que la sesión siempre se cierre al final
        try (Session session = sessionFactory.openSession()) {
            
            Transaction tx = session.beginTransaction();
            
            try {
                session.persist(partida);
                tx.commit();
                System.out.println("Partida insertada correctamente.");
                
            } catch (Exception e) {
                // Hacemos el rollback ANTES de que la sesión se cierre
                if (tx != null) {
                    tx.rollback();
                }
                System.out.println("Error al insertar partida: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("Error de sesión al insertar: " + e.getMessage());
        }
    }



    // =========================
    // LISTAR
    // =========================
    public List<Partida> listar() {

        try (Session session = sessionFactory.openSession()) {

            return session
                    .createQuery("FROM Partida", Partida.class)
                    .list();

        } catch (Exception e) {

            System.out.println("Error al listar partidas: " + e.getMessage());
            return null;
        }
    }



    // =========================
    // BUSCAR POR ID
    // =========================
    public Partida buscarPorId(int id) {

        try (Session session = sessionFactory.openSession()) {

            Partida partida = session.find(Partida.class, id);

            if (partida != null) {
                // SOLUCIÓN: Forzamos a Hibernate a traer los detalles 
                // ANTES de que el try-with-resources cierre la sesión
                org.hibernate.Hibernate.initialize(partida.getDetalles());
            }

            return partida;

        } catch (Exception e) {

            System.out.println("Error al buscar partida: " + e.getMessage());
            return null;
        }
    }



    // =========================
    // ACTUALIZAR
    // =========================
    public void actualizar(Partida partida) {

        try (Session session = sessionFactory.openSession()) {

            Transaction tx = session.beginTransaction();

            try {
                session.merge(partida);
                tx.commit();
                System.out.println("Partida actualizada correctamente.");

            } catch (Exception e) {
                if (tx != null) {
                    tx.rollback();
                }
                System.out.println("Error al actualizar partida: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("Error de sesión al actualizar: " + e.getMessage());
        }
    }



    // =========================
    // ELIMINAR
    // =========================
    public void eliminar(int id) {

        try (Session session = sessionFactory.openSession()) {

            Transaction tx = session.beginTransaction();

            try {
                Partida partida = session.find(Partida.class, id);

                if (partida != null) {
                    session.remove(partida);
                    System.out.println("Partida eliminada correctamente.");
                } else {
                    System.out.println("Partida no encontrada.");
                }

                tx.commit();

            } catch (Exception e) {
                if (tx != null) {
                    tx.rollback();
                }
                System.out.println("Error al eliminar partida: " + e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("Error de sesión al eliminar: " + e.getMessage());
        }
    }



    // =========================
    // CERRAR SESSION FACTORY
    // =========================
    public static void cerrarFactory() {
        sessionFactory.close();
    }
}