package com.arcade.controller;

import com.arcade.model.game.Game;
import com.arcade.service.GameService;
import com.arcade.service.GameFactory.GameType;

/**
 * Interfaz base para controladores de juegos
 * Define el comportamiento común para todos los controladores
 */
public interface GameController {

    /**
     * Establece el servicio de juegos
     * @param gameService servicio de juegos
     */
    void setGameService(GameService gameService);

    /**
     * Establece el tipo de juego
     * @param gameType tipo de juego
     */
    void setGameType(GameType gameType);

    /**
     * Inicializa el juego con sus valores por defecto
     */
    void initGame();

    /**
     * Ejecuta el juego paso a paso
     */
    void stepGame();

    /**
     * Resuelve el juego automáticamente
     */
    void solveGame();

    /**
     * Reinicia el juego
     */
    void resetGame();

    /**
     * Finaliza el juego y guarda el resultado
     */
    void endGame();
}