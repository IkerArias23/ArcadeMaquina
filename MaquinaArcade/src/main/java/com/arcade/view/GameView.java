package com.arcade.view;

import com.arcade.model.game.Game;
import com.arcade.service.GameService;

/**
 * Interfaz que define las operaciones comunes para todas las vistas de juegos
 * Las implementaciones específicas para cada juego deben implementar estos métodos
 */
public interface GameView {

    /**
     * Inicializa la vista con su juego y servicio correspondiente
     * @param game juego asociado a la vista
     * @param gameService servicio de juego
     */
    void initialize(Game game, GameService gameService);

    /**
     * Actualiza la interfaz con el estado actual del juego
     */
    void updateView();

    /**
     * Visualiza un paso en la resolución del juego
     */
    void showStep();

    /**
     * Visualiza la solución completa del juego
     */
    void showSolution();

    /**
     * Reinicia la vista al estado inicial
     */
    void reset();

    /**
     * Muestra un mensaje informativo en la vista
     * @param message mensaje a mostrar
     */
    void showMessage(String message);

    /**
     * Muestra un error en la vista
     * @param error mensaje de error
     */
    void showError(String error);

    /**
     * Obtiene el juego asociado a esta vista
     * @return instancia del juego
     */
    Game getGame();

    /**
     * Obtiene el título de la vista
     * @return título para ventana/panel
     */
    String getTitle();

    /**
     * Verifica si el juego asociado ha sido completado
     * @return true si el juego está completo
     */
    boolean isGameCompleted();

    /**
     * Configura el modo de la vista (manual o automático)
     * @param autoMode true para modo automático, false para manual
     */
    void setAutoMode(boolean autoMode);

    /**
     * Obtiene el modo actual de la vista
     * @return true si está en modo automático
     */
    boolean isAutoMode();
}