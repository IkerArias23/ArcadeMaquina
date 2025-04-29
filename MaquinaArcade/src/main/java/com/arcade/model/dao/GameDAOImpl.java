package com.arcade.model.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.arcade.config.HibernateConfig;
import com.arcade.model.entity.GameRecord;
import com.arcade.service.GameFactory.GameType;

/**
 * Implementación de la interfaz GameDAO
 * Proporciona acceso a la base de datos mediante Hibernate
 */
public class GameDAOImpl implements GameDAO {

    private final SessionFactory sessionFactory;

    /**
     * Constructor por defecto
     * Obtiene la SessionFactory del singleton HibernateConfig
     */
    public GameDAOImpl() {
        this.sessionFactory = HibernateConfig.getInstance().getSessionFactory();
    }

    /**
     * Constructor con SessionFactory
     * @param sessionFactory factory para crear sesiones
     */
    public GameDAOImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public GameRecord save(GameRecord record) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            session.save(record);

            transaction.commit();
            return record;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error al guardar registro: " + e.getMessage(), e);
        }
    }

    @Override
    public GameRecord update(GameRecord record) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            session.update(record);

            transaction.commit();
            return record;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error al actualizar registro: " + e.getMessage(), e);
        }
    }

    @Override
    public GameRecord findById(Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(GameRecord.class, id);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar registro por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<GameRecord> findAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<GameRecord> query = session.createQuery(
                    "FROM GameRecord ORDER BY endTime DESC",
                    GameRecord.class
            );

            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar todos los registros: " + e.getMessage(), e);
        }
    }

    @Override
    public List<GameRecord> findByGameType(GameType gameType) {
        String discriminatorValue;
        switch (gameType) {
            case QUEENS:
                discriminatorValue = "QUEENS";
                break;
            case KNIGHT:
                discriminatorValue = "KNIGHT";
                break;
            case HANOI:
                discriminatorValue = "HANOI";
                break;
            default:
                throw new IllegalArgumentException("Tipo de juego no soportado: " + gameType);
        }

        try (Session session = sessionFactory.openSession()) {
            Query<GameRecord> query = session.createQuery(
                    "FROM GameRecord WHERE game_type = :type ORDER BY endTime DESC",
                    GameRecord.class
            );
            query.setParameter("type", discriminatorValue);

            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar registros por tipo: " + e.getMessage(), e);
        }
    }

    @Override
    public List<GameRecord> findCompleted() {
        try (Session session = sessionFactory.openSession()) {
            Query<GameRecord> query = session.createQuery(
                    "FROM GameRecord WHERE completed = true ORDER BY endTime DESC",
                    GameRecord.class
            );

            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar registros completados: " + e.getMessage(), e);
        }
    }

    @Override
    public List<GameRecord> findIncomplete() {
        try (Session session = sessionFactory.openSession()) {
            Query<GameRecord> query = session.createQuery(
                    "FROM GameRecord WHERE completed = false ORDER BY endTime DESC",
                    GameRecord.class
            );

            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar registros incompletos: " + e.getMessage(), e);
        }
    }

    @Override
    public void delete(GameRecord record) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            session.delete(record);

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error al eliminar registro: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteById(Long id) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            GameRecord record = session.get(GameRecord.class, id);
            if (record != null) {
                session.delete(record);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error al eliminar registro por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteAll() {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            session.createQuery("DELETE FROM GameRecord").executeUpdate();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error al eliminar todos los registros: " + e.getMessage(), e);
        }
    }

    @Override
    public long count() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(id) FROM GameRecord",
                    Long.class
            );

            Long result = query.uniqueResult();
            return result != null ? result : 0;
        } catch (Exception e) {
            throw new RuntimeException("Error al contar registros: " + e.getMessage(), e);
        }
    }

    @Override
    public long countByGameType(GameType gameType) {
        String discriminatorValue;
        switch (gameType) {
            case QUEENS:
                discriminatorValue = "QUEENS";
                break;
            case KNIGHT:
                discriminatorValue = "KNIGHT";
                break;
            case HANOI:
                discriminatorValue = "HANOI";
                break;
            default:
                throw new IllegalArgumentException("Tipo de juego no soportado: " + gameType);
        }

        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(id) FROM GameRecord WHERE game_type = :type",
                    Long.class
            );
            query.setParameter("type", discriminatorValue);

            Long result = query.uniqueResult();
            return result != null ? result : 0;
        } catch (Exception e) {
            throw new RuntimeException("Error al contar registros por tipo: " + e.getMessage(), e);
        }
    }

    @Override
    public long countCompleted() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(id) FROM GameRecord WHERE completed = true",
                    Long.class
            );

            Long result = query.uniqueResult();
            return result != null ? result : 0;
        } catch (Exception e) {
            throw new RuntimeException("Error al contar registros completados: " + e.getMessage(), e);
        }
    }

    @Override
    public void close() {
        // No cerramos la SessionFactory aquí para permitir su uso por otros componentes
        // Se cierra desde el método shutdown de HibernateConfig al finalizar la aplicación
    }
}