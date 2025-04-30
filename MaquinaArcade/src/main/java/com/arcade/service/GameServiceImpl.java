package com.arcade.service;

import java.util.ArrayList;
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
import com.arcade.model.game.Game;
import com.arcade.service.GameFactory.GameType;

/**
 * Implementación del servicio de juegos
 * Actúa como fachada (patrón Facade) para las operaciones con juegos
 * y la persistencia de datos
 */
public class GameServiceImpl implements GameService {

    private final SessionFactory sessionFactory;

    /**
     * Constructor por defecto
     */
    public GameServiceImpl() {
        this.sessionFactory = HibernateConfig.getInstance().getSessionFactory();
    }

    @Override
    public Game createGame(GameType type) {
        return GameFactory.createGame(type);
    }

    @Override
    public void initializeGame(Game game, Object... params) {
        if (game == null) {
            throw new IllegalArgumentException("El juego no puede ser nulo");
        }

        game.initialize(params);
    }

    @Override
    public boolean solveGame(Game game) {
        if (game == null) {
            throw new IllegalArgumentException("El juego no puede ser nulo");
        }

        return game.solve();
    }

    @Override
    public boolean stepGame(Game game) {
        if (game == null) {
            throw new IllegalArgumentException("El juego no puede ser nulo");
        }

        return game.step();
    }

    @Override
    public GameRecord saveGameRecord(Game game, boolean completed) {
        if (game == null) {
            throw new IllegalArgumentException("El juego no puede ser nulo");
        }

        // Crear el registro con los datos del juego
        GameRecord record = game.createRecord(completed);

        // Guardar en la base de datos
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            session.save(record);

            transaction.commit();
            System.out.println("Registro guardado con ID: " + record.getId());
            return record;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            System.err.println("Error al guardar registro: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<GameRecord> getGameHistory(GameType type) {
        List<GameRecord> records = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            if (type == null) {
                // Obtener todos los registros
                try {
                    Query<QueenRecord> queryQueens = session.createQuery(
                            "FROM QueenRecord ORDER BY endTime DESC",
                            QueenRecord.class
                    );
                    records.addAll(queryQueens.list());
                } catch (Exception e) {
                    System.err.println("Error al cargar registros QueenRecord: " + e.getMessage());
                }

                try {
                    Query<KnightRecord> queryKnight = session.createQuery(
                            "FROM KnightRecord ORDER BY endTime DESC",
                            KnightRecord.class
                    );
                    records.addAll(queryKnight.list());
                } catch (Exception e) {
                    System.err.println("Error al cargar registros KnightRecord: " + e.getMessage());
                }

                try {
                    Query<HanoiRecord> queryHanoi = session.createQuery(
                            "FROM HanoiRecord ORDER BY endTime DESC",
                            HanoiRecord.class
                    );
                    records.addAll(queryHanoi.list());
                } catch (Exception e) {
                    System.err.println("Error al cargar registros HanoiRecord: " + e.getMessage());
                }
            } else {
                // Filtrar por tipo de juego
                switch (type) {
                    case QUEENS:
                        try {
                            Query<QueenRecord> query = session.createQuery(
                                    "FROM QueenRecord ORDER BY endTime DESC",
                                    QueenRecord.class
                            );
                            records.addAll(query.list());
                        } catch (Exception e) {
                            System.err.println("Error al cargar registros QueenRecord: " + e.getMessage());
                        }
                        break;
                    case KNIGHT:
                        try {
                            Query<KnightRecord> query = session.createQuery(
                                    "FROM KnightRecord ORDER BY endTime DESC",
                                    KnightRecord.class
                            );
                            records.addAll(query.list());
                        } catch (Exception e) {
                            System.err.println("Error al cargar registros KnightRecord: " + e.getMessage());
                        }
                        break;
                    case HANOI:
                        try {
                            Query<HanoiRecord> query = session.createQuery(
                                    "FROM HanoiRecord ORDER BY endTime DESC",
                                    HanoiRecord.class
                            );
                            records.addAll(query.list());
                        } catch (Exception e) {
                            System.err.println("Error al cargar registros HanoiRecord: " + e.getMessage());
                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error general al cargar registros: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("Registros cargados: " + records.size());
        return records;
    }

    @Override
    public int getTotalGamesPlayed() {
        int total = 0;
        try (Session session = sessionFactory.openSession()) {
            try {
                Query<Long> query = session.createQuery(
                        "SELECT COUNT(id) FROM GameRecord",
                        Long.class
                );
                Long result = query.uniqueResult();
                total = result != null ? result.intValue() : 0;
            } catch (Exception e) {
                System.err.println("Error al contar registros: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Error general al contar registros: " + e.getMessage());
        }
        return total;
    }

    @Override
    public int getCompletedGamesCount() {
        int total = 0;
        try (Session session = sessionFactory.openSession()) {
            try {
                Query<Long> query = session.createQuery(
                        "SELECT COUNT(id) FROM GameRecord WHERE completed = true",
                        Long.class
                );
                Long result = query.uniqueResult();
                total = result != null ? result.intValue() : 0;
            } catch (Exception e) {
                System.err.println("Error al contar registros completados: " + e.getMessage());
            }
        } catch (Exception e) {
            System.err.println("Error general al contar registros completados: " + e.getMessage());
        }
        return total;
    }
}