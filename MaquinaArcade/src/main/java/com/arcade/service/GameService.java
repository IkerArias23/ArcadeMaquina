package com.arcade.service;

import java.util.List;

import com.arcade.model.entity.GameRecord;
import com.arcade.model.game.Game;
import com.arcade.service.GameFactory.GameType;

/**
 * Interfaz para el servicio de juegos
 * Define las operaciones disponibles para gestionar juegos
 */
public interface GameService {

    /**
     * Crea un nuevo juego del tipo especificado
     * @param type tipo de juego
     * @return instancia del juego creado
     */
    Game createGame(GameType type);

    /**
     * Inicializa un juego con los parámetros indicados
     * @param game juego a inicializar
     * @param params parámetros de inicialización
     */
    void initializeGame(Game game, Object... params);

    /**
     * Resuelve automáticamente un juego
     * @param game juego a resolver
     * @return true si se resolvió correctamente
     */
    boolean solveGame(Game game);

    /**
     * Realiza un paso en la resolución de un juego
     * @param game juego a avanzar
     * @return true si se realizó el paso correctamente
     */
    boolean stepGame(Game game);

    /**
     * Guarda un registro de una partida completada
     * @param game juego a registrar
     * @param completed indica si se completó correctamente
     * @return registro creado
     */
    GameRecord saveGameRecord(Game game, boolean completed);

    /**
     * Obtiene el historial de registros para un tipo de juego
     * @param type tipo de juego
     * @return lista de registros
     */
    List<GameRecord> getGameHistory(GameType type);

    /**
     * Obtiene el número total de partidas jugadas
     * @return número de partidas
     */
    int getTotalGamesPlayed();

    /**
     * Obtiene el número de partidas completadas con éxito
     * @return número de partidas completadas
     */
    int getCompletedGamesCount();
}