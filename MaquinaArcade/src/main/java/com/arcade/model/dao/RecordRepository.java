package com.arcade.model.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.arcade.config.HibernateConfig;
import com.arcade.model.entity.GameRecord;
import com.arcade.model.entity.HanoiRecord;
import com.arcade.model.entity.KnightRecord;
import com.arcade.model.entity.QueenRecord;

/**
 * Repositorio para gestionar los registros de partidas
 * Implementa el patrón Repository para proporcionar acceso a datos
 * Extiende la funcionalidad del GameDAO con operaciones específicas
 */
public class RecordRepository {

    private final SessionFactory sessionFactory;
    private final GameDAO gameDAO;

    /**
     * Constructor por defecto
     */
    public RecordRepository() {
        this.sessionFactory = HibernateConfig.getInstance().getSessionFactory();
        this.gameDAO = new GameDAOImpl(sessionFactory);
    }

    /**
     * Constructor con SessionFactory
     * @param sessionFactory factory para crear sesiones
     */
    public RecordRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.gameDAO = new GameDAOImpl(sessionFactory);
    }

    /**
     * Guarda un nuevo registro de partida
     * @param record registro a guardar
     * @return registro guardado con ID
     */
    public GameRecord saveRecord(GameRecord record) {
        return gameDAO.save(record);
    }

    /**
     * Obtiene todos los registros de N Reinas
     * @return lista de registros
     */
    public List<QueenRecord> findAllQueenRecords() {
        try (Session session = sessionFactory.openSession()) {
            Query<QueenRecord> query = session.createQuery(
                    "FROM QueenRecord ORDER BY endTime DESC",
                    QueenRecord.class
            );
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar registros de N Reinas: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene todos los registros del Recorrido del Caballo
     * @return lista de registros
     */
    public List<KnightRecord> findAllKnightRecords() {
        try (Session session = sessionFactory.openSession()) {
            Query<KnightRecord> query = session.createQuery(
                    "FROM KnightRecord ORDER BY endTime DESC",
                    KnightRecord.class
            );
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar registros del Recorrido del Caballo: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene todos los registros de Torres de Hanoi
     * @return lista de registros
     */
    public List<HanoiRecord> findAllHanoiRecords() {
        try (Session session = sessionFactory.openSession()) {
            Query<HanoiRecord> query = session.createQuery(
                    "FROM HanoiRecord ORDER BY endTime DESC",
                    HanoiRecord.class
            );
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar registros de Torres de Hanoi: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene los registros de N Reinas para un tamaño específico
     * @param boardSize tamaño del tablero
     * @return lista de registros
     */
    public List<QueenRecord> findQueenRecordsByBoardSize(int boardSize) {
        try (Session session = sessionFactory.openSession()) {
            Query<QueenRecord> query = session.createQuery(
                    "FROM QueenRecord WHERE boardSize = :size ORDER BY endTime DESC",
                    QueenRecord.class
            );
            query.setParameter("size", boardSize);
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar registros por tamaño: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene los registros del Recorrido del Caballo para un tamaño específico
     * @param boardSize tamaño del tablero
     * @return lista de registros
     */
    public List<KnightRecord> findKnightRecordsByBoardSize(int boardSize) {
        try (Session session = sessionFactory.openSession()) {
            Query<KnightRecord> query = session.createQuery(
                    "FROM KnightRecord WHERE boardSize = :size ORDER BY endTime DESC",
                    KnightRecord.class
            );
            query.setParameter("size", boardSize);
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar registros por tamaño: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene los registros de Torres de Hanoi para un número específico de discos
     * @param numDisks número de discos
     * @return lista de registros
     */
    public List<HanoiRecord> findHanoiRecordsByNumDisks(int numDisks) {
        try (Session session = sessionFactory.openSession()) {
            Query<HanoiRecord> query = session.createQuery(
                    "FROM HanoiRecord WHERE numDisks = :disks ORDER BY endTime DESC",
                    HanoiRecord.class
            );
            query.setParameter("disks", numDisks);
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar registros por discos: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene los registros en un rango de fechas
     * @param startDate fecha inicial
     * @param endDate fecha final
     * @return lista de registros
     */
    public List<GameRecord> findRecordsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        try (Session session = sessionFactory.openSession()) {
            Query<GameRecord> query = session.createQuery(
                    "FROM GameRecord WHERE endTime BETWEEN :start AND :end ORDER BY endTime DESC",
                    GameRecord.class
            );
            query.setParameter("start", startDate);
            query.setParameter("end", endDate);
            return query.list();
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar registros por fecha: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene las estadísticas de partidas para cada tipo de juego
     * @return array con [total partidas, completadas, porcentaje] para cada juego
     */
    public Object[][] getGameStats() {
        Object[][] stats = new Object[3][3];

        try (Session session = sessionFactory.openSession()) {
            // Estadísticas para N Reinas
            Query<Long> queryQueensTotal = session.createQuery(
                    "SELECT COUNT(id) FROM QueenRecord", Long.class);
            Query<Long> queryQueensCompleted = session.createQuery(
                    "SELECT COUNT(id) FROM QueenRecord WHERE completed = true", Long.class);

            Long queensTotal = queryQueensTotal.uniqueResult();
            Long queensCompleted = queryQueensCompleted.uniqueResult();
            double queensPercentage = queensTotal > 0 ?
                    (queensCompleted * 100.0 / queensTotal) : 0.0;

            stats[0][0] = queensTotal;
            stats[0][1] = queensCompleted;
            stats[0][2] = queensPercentage;

            // Estadísticas para Recorrido del Caballo
            Query<Long> queryKnightTotal = session.createQuery(
                    "SELECT COUNT(id) FROM KnightRecord", Long.class);
            Query<Long> queryKnightCompleted = session.createQuery(
                    "SELECT COUNT(id) FROM KnightRecord WHERE completed = true", Long.class);

            Long knightTotal = queryKnightTotal.uniqueResult();
            Long knightCompleted = queryKnightCompleted.uniqueResult();
            double knightPercentage = knightTotal > 0 ?
                    (knightCompleted * 100.0 / knightTotal) : 0.0;

            stats[1][0] = knightTotal;
            stats[1][1] = knightCompleted;
            stats[1][2] = knightPercentage;

            // Estadísticas para Torres de Hanoi
            Query<Long> queryHanoiTotal = session.createQuery(
                    "SELECT COUNT(id) FROM HanoiRecord", Long.class);
            Query<Long> queryHanoiCompleted = session.createQuery(
                    "SELECT COUNT(id) FROM HanoiRecord WHERE completed = true", Long.class);

            Long hanoiTotal = queryHanoiTotal.uniqueResult();
            Long hanoiCompleted = queryHanoiCompleted.uniqueResult();
            double hanoiPercentage = hanoiTotal > 0 ?
                    (hanoiCompleted * 100.0 / hanoiTotal) : 0.0;

            stats[2][0] = hanoiTotal;
            stats[2][1] = hanoiCompleted;
            stats[2][2] = hanoiPercentage;

            return stats;

        } catch (Exception e) {
            throw new RuntimeException("Error al obtener estadísticas: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina todos los registros de una tabla específica
     * @param entityClass clase de entidad a eliminar
     */
    public void deleteAllRecords(Class<?> entityClass) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            String entityName = entityClass.getSimpleName();
            session.createQuery("DELETE FROM " + entityName).executeUpdate();

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error al eliminar registros: " + e.getMessage(), e);
        }
    }

    /**
     * Elimina un registro por su ID y clase
     * @param id identificador del registro
     * @param entityClass clase de entidad
     */
    public void deleteRecord(Long id, Class<?> entityClass) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Object entity = session.get(entityClass, id);
            if (entity != null) {
                session.delete(entity);
            }

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Error al eliminar registro: " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene el DAO subyacente
     * @return instancia de GameDAO
     */
    public GameDAO getGameDAO() {
        return gameDAO;
    }
}