package com.arcade.service;

import com.arcade.model.game.Game;
import com.arcade.model.game.queens.QueensGame;
import com.arcade.model.game.knight.KnightGame;
import com.arcade.model.game.hanoi.HanoiGame;

/**
 * Fábrica de juegos que implementa el patrón Factory Method
 * Permite crear instancias de juegos concretos a través de una interfaz común
 */
public class GameFactory {

    // Tipos de juegos disponibles
    public enum GameType {
        QUEENS("N Reinas", "Coloca N reinas en un tablero sin que se amenacen"),
        KNIGHT("Recorrido del Caballo", "Recorre todo el tablero de ajedrez con el caballo"),
        HANOI("Torres de Hanoi", "Mueve todos los discos de una torre a otra")
        ;

        private final String name;
        private final String description;

        GameType(String name, String description) {
            this.name = name;
            this.description = description;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * Crea una instancia de juego según el tipo solicitado
     * @param type tipo de juego a crear
     * @return instancia del juego solicitado
     */
    public static Game createGame(GameType type) {
        switch (type) {
            case QUEENS:
                return new QueensGame();
            case KNIGHT:
                return new KnightGame();
            case HANOI:
                return new HanoiGame();
            default:
                throw new IllegalArgumentException("Tipo de juego no soportado: " + type);
        }
    }

    /**
     * Método para obtener todos los tipos de juegos disponibles
     * @return array con los tipos de juegos
     */
    public static GameType[] getAvailableGames() {
        return GameType.values();
    }
}