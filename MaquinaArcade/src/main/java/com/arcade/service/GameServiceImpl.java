package com.arcade.service;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.arcade.config.HibernateConfig;
import com.arcade.model.entity.GameRecord;
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
            return record;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<GameRecord> getGameHistory(GameType type) {
        String discriminatorValue;
        switch (type) {
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
                throw new IllegalArgumentException("Tipo de juego no soportado: " + type);
        }

        try (Session session = sessionFactory.openSession()) {
            Query<GameRecord> query = session.createQuery(
                    "FROM GameRecord WHERE game_type = :type ORDER BY end_time DESC",
                    GameRecord.class
            );
            query.setParameter("type", discriminatorValue);

            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of(); // Lista vacía en caso de error
        }
    }

    @Override
    public int getTotalGamesPlayed() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(id) FROM GameRecord",
                    Long.class
            );

            Long result = query.uniqueResult();
            return result != null ? result.intValue() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int getCompletedGamesCount() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery(
                    "SELECT COUNT(id) FROM GameRecord WHERE completed = true",
                    Long.class
            );

            Long result = query.uniqueResult();
            return result != null ? result.intValue() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}