package com.sistema.modulos.contabilidad.DAO;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.sistema.modulos.contabilidad.Models.Cuenta;

public class CuentaDAO {

    // SessionFactory
    private static final SessionFactory sessionFactory =
            new Configuration()
                    .configure("hibernate.cfg.xml")
                    .buildSessionFactory();



    // =========================
    // INSERTAR
    // =========================
    public void insertar(Cuenta cuenta) {

        Transaction tx = null;

        try (Session session = sessionFactory.openSession()) {

            tx = session.beginTransaction();

            session.persist(cuenta);

            tx.commit();

            System.out.println("Cuenta insertada correctamente");

        } catch (Exception e) {

            if (tx != null) {
                tx.rollback();
            }

            System.out.println("Error al insertar: " + e.getMessage());
        }
    }



    // =========================
    // LISTAR
    // =========================
    public List<Cuenta> listar() {

        try (Session session = sessionFactory.openSession()) {

            return session
                    .createQuery("FROM Cuenta", Cuenta.class)
                    .list();

        } catch (Exception e) {

            System.out.println("Error al listar: " + e.getMessage());

            return null;
        }
    }



    // =========================
    // BUSCAR POR ID
    // =========================
    public Cuenta buscarPorId(int id) {

        try (Session session = sessionFactory.openSession()) {

            return session.find(Cuenta.class, id);

        } catch (Exception e) {

            System.out.println("Error al buscar: " + e.getMessage());

            return null;
        }
    }



    // =========================
    // ACTUALIZAR
    // =========================
    public void actualizar(Cuenta cuenta) {

        Transaction tx = null;

        try (Session session = sessionFactory.openSession()) {

            tx = session.beginTransaction();

            session.merge(cuenta);

            tx.commit();

            System.out.println("Cuenta actualizada correctamente");

        } catch (Exception e) {

            if (tx != null) {
                tx.rollback();
            }

            System.out.println("Error al actualizar: " + e.getMessage());
        }
    }



    // =========================
    // ELIMINAR
    // =========================
    public void eliminar(int id) {

        Transaction tx = null;

        try (Session session = sessionFactory.openSession()) {

            tx = session.beginTransaction();

            Cuenta cuenta = session.find(Cuenta.class, id);

            if (cuenta != null) {

                session.remove(cuenta);

                System.out.println("Cuenta eliminada correctamente");

            } else {

                System.out.println("Cuenta no encontrada");
            }

            tx.commit();

        } catch (Exception e) {

            if (tx != null) {
                tx.rollback();
            }

            System.out.println("Error al eliminar: " + e.getMessage());
        }
    }



    // =========================
    // CERRAR SESSION FACTORY
    // =========================
    public static void cerrarFactory() {

        sessionFactory.close();
    }
}