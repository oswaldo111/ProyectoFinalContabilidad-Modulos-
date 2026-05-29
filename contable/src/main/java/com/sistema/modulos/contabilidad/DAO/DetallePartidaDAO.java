package com.sistema.modulos.contabilidad.DAO;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import com.sistema.modulos.contabilidad.Models.DetallePartida; 
public class DetallePartidaDAO {
     // Se configura y levanta la factoría de sesiones de Hibernate de forma estática
    private static final SessionFactory sessionFactory = new Configuration()
            .configure("hibernate.cfg.xml")
            .buildSessionFactory();

    // Método para crear/instanciar un objeto vacío si lo necesitas
    public DetallePartida crear() {
        return new DetallePartida();
    }

    // Método para INSERTAR un detalle de partida en la base de datos
    public void insertar(DetallePartida detallePartida) {
        Transaction transaction = null;
        
        // El try-with-resources abre y cierra la sesión automáticamente
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            
            session.save(detallePartida);
            
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Método para MOSTRAR todos los detalles de las partidas
    public List<DetallePartida> mostrar() {
        try (Session session = sessionFactory.openSession()) {
            // Revisa que "DetallePartida" coincida exactamente con el nombre de tu clase Entidad
            return session.createQuery("FROM DetallePartida", DetallePartida.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Método para ACTUALIZAR un detalle de partida existente
    public void actualizar(DetallePartida detallePartida) {
        Transaction transaction = null;
        
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            
            session.update(detallePartida);
            
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
    
}
