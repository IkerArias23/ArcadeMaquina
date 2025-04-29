package com.arcade.model.game;

import com.arcade.model.entity.GameRecord;

/**
 * Interfaz que define el comportamiento común de todos los juegos
 * Esta interfaz es parte de la implementación del patrón Factory Method
 */
public interface Game {

    /**
     * Nombre del juego
     * @return nombre del juego
     */
    String getName();

    /**
     * Descripción del juego
     * @return descripción del juego
     */
    String getDescription();

    /**
     * Inicializa el juego con los parámetros proporcionados
     * @param params parámetros de inicialización
     */
    void initialize(Object... params);

    /**
     * Resuelve el juego de forma automática
     * @return true si se pudo resolver, false en caso contrario
     */
    boolean solve();

    /**
     * Realiza un paso en la resolución del juego
     * @return true si se realizó el paso correctamente, false si no es posible
     */
    boolean step();

    /**
     * Valida si el estado actual es una solución correcta
     * @return true si el estado actual es una solución válida
     */
    boolean isValidSolution();

    /**
     * Reinicia el juego al estado inicial
     */
    void reset();

    /**
     * Crea un registro de la partida para almacenar en la base de datos
     * @param isCompleted indica si se completó el juego
     * @return registro de juego
     */
    GameRecord createRecord(boolean isCompleted);
}