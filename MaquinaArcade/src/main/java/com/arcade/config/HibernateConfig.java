package com.arcade.config;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import com.arcade.model.entity.GameRecord;
import com.arcade.model.entity.QueenRecord;
import com.arcade.model.entity.KnightRecord;
import com.arcade.model.entity.HanoiRecord;

/**
 * Configuración de Hibernate para la aplicación
 * Implementa el patrón Singleton para asegurar una única instancia
 * de la SessionFactory en toda la aplicación.
 */
public class HibernateConfig {

    private static HibernateConfig instance;
    private SessionFactory sessionFactory;

    /**
     * Constructor privado para evitar instanciación directa (Singleton)
     */
    private HibernateConfig() {
        // Inicializar la SessionFactory
        try {
            StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .configure() // configura los ajustes desde hibernate.cfg.xml
                    .build();

            // Construir la SessionFactory
            sessionFactory = new MetadataSources(registry)
                    .addAnnotatedClass(GameRecord.class)
                    .addAnnotatedClass(QueenRecord.class)
                    .addAnnotatedClass(KnightRecord.class)
                    .addAnnotatedClass(HanoiRecord.class)
                    .buildMetadata()
                    .buildSessionFactory();

        } catch (Exception e) {
            System.err.println("Error al inicializar Hibernate: " + e);
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Método para obtener la instancia única de la configuración
     * @return instancia de HibernateConfig
     */
    public static synchronized HibernateConfig getInstance() {
        if (instance == null) {
            instance = new HibernateConfig();
        }
        return instance;
    }

    /**
     * Devuelve la fábrica de sesiones para operaciones con la BD
     * @return SessionFactory
     */
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Cierra los recursos de Hibernate
     */
    public void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}