package com.arcade.config;

import java.util.HashMap;
import java.util.Map;

import com.arcade.model.game.Game;
import com.arcade.service.GameFactory;
import com.arcade.service.GameFactory.GameType;

/**
 * Registro central de juegos de la aplicación
 * Implementa el patrón Registry para mantener referencia a los juegos creados
 *
 * El patrón Registry actúa como un registro global que mantiene referencias
 * a objetos que pueden ser necesarios en diferentes partes de la aplicación
 */
public class GameRegistry {

    private static GameRegistry instance;

    private final Map<GameType, Game> activeGames;

    /**
     * Constructor privado para evitar instanciación directa (Singleton)
     */
    private GameRegistry() {
        activeGames = new HashMap<>();
    }

    /**
     * Método para obtener la instancia única del registro
     * @return instancia de GameRegistry
     */
    public static synchronized GameRegistry getInstance() {
        if (instance == null) {
            instance = new GameRegistry();
        }
        return instance;
    }

    /**
     * Obtiene un juego del registro, creándolo si no existe
     * @param type tipo de juego
     * @return instancia del juego
     */
    public Game getGame(GameType type) {
        if (!activeGames.containsKey(type)) {
            Game game = GameFactory.createGame(type);
            activeGames.put(type, game);
        }

        return activeGames.get(type);
    }

    /**
     * Verifica si un juego está registrado
     * @param type tipo de juego
     * @return true si el juego está registrado
     */
    public boolean hasGame(GameType type) {
        return activeGames.containsKey(type);
    }

    /**
     * Elimina un juego del registro
     * @param type tipo de juego
     */
    public void removeGame(GameType type) {
        activeGames.remove(type);
    }

    /**
     * Reinicia todos los juegos registrados
     */
    public void resetAllGames() {
        for (Game game : activeGames.values()) {
            game.reset();
        }
    }

    /**
     * Limpia todo el registro
     */
    public void clearRegistry() {
        activeGames.clear();
    }
}