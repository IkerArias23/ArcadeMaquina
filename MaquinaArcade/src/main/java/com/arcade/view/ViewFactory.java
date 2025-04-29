package com.arcade.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

import com.arcade.ArcadeApplication;
import com.arcade.controller.GameController;
import com.arcade.service.GameService;
import com.arcade.service.GameFactory.GameType;

/**
 * Fábrica de vistas para la aplicación
 * Implementa el patrón Factory Method para crear las diferentes vistas de juegos
 */
public class ViewFactory {

    private final GameService gameService;

    /**
     * Constructor con servicio de juegos
     * @param gameService servicio de juegos
     */
    public ViewFactory(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Crea y muestra la ventana para un juego específico
     * @param gameType tipo de juego
     * @return controlador del juego
     * @throws IOException si hay error al cargar la vista
     */
    public GameController createGameView(GameType gameType) throws IOException {
        String fxmlPath;
        String title;

        // Determinar archivo FXML y título según el tipo de juego
        switch (gameType) {
            case QUEENS:
                fxmlPath = "/fxml/queens.fxml";
                title = "N Reinas";
                break;
            case KNIGHT:
                fxmlPath = "/fxml/knight.fxml";
                title = "Recorrido del Caballo";
                break;
            case HANOI:
                fxmlPath = "/fxml/hanoi.fxml";
                title = "Torres de Hanoi";
                break;
            default:
                throw new IllegalArgumentException("Tipo de juego no soportado");
        }

        // Cargar la vista
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        // Configurar controlador
        GameController controller = loader.getController();
        controller.setGameService(gameService);
        controller.setGameType(gameType);

        // Crear y mostrar la ventana
        Stage gameStage = new Stage();
        gameStage.initModality(Modality.WINDOW_MODAL);
        gameStage.initOwner(ArcadeApplication.getInstance().getPrimaryStage());
        gameStage.setTitle(title);
        gameStage.setScene(new Scene(root, 800, 600));
        gameStage.setMinWidth(600);
        gameStage.setMinHeight(400);

        gameStage.show();

        return controller;
    }

    /**
     * Crea y muestra la ventana de historial de partidas
     * @throws IOException si hay error al cargar la vista
     */
    public void createHistoryView() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/history.fxml"));
        Parent root = loader.load();

        // Configurar controlador de historial si es necesario
        // HistoryController controller = loader.getController();
        // controller.setGameService(gameService);

        // Crear y mostrar la ventana
        Stage historyStage = new Stage();
        historyStage.initModality(Modality.WINDOW_MODAL);
        historyStage.initOwner(ArcadeApplication.getInstance().getPrimaryStage());
        historyStage.setTitle("Historial de Partidas");
        historyStage.setScene(new Scene(root, 600, 400));

        historyStage.show();
    }

    /**
     * Crea y muestra un diálogo de configuración para un juego
     * @param gameType tipo de juego
     * @return valores de configuración o null si se cancela
     * @throws IOException si hay error al cargar la vista
     */
    public Object[] createGameConfigDialog(GameType gameType) throws IOException {
        String fxmlPath;
        String title;

        // Determinar archivo FXML y título según el tipo de juego
        switch (gameType) {
            case QUEENS:
                fxmlPath = "/fxml/dialogs/queens_config.fxml";
                title = "Configurar N Reinas";
                break;
            case KNIGHT:
                fxmlPath = "/fxml/dialogs/knight_config.fxml";
                title = "Configurar Recorrido del Caballo";
                break;
            case HANOI:
                fxmlPath = "/fxml/dialogs/hanoi_config.fxml";
                title = "Configurar Torres de Hanoi";
                break;
            default:
                throw new IllegalArgumentException("Tipo de juego no soportado");
        }

        // Cargar la vista
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

        // Configurar controlador si es necesario
        // GameConfigController controller = loader.getController();

        // Crear y mostrar la ventana modal
        Stage configStage = new Stage();
        configStage.initModality(Modality.APPLICATION_MODAL);
        configStage.initOwner(ArcadeApplication.getInstance().getPrimaryStage());
        configStage.setTitle(title);
        configStage.setScene(new Scene(root, 400, 300));

        // Esperar a que se cierre el diálogo
        configStage.showAndWait();

        // Obtener valores de configuración (implementación depende del controlador)
        // return controller.getConfigValues();
        return null;
    }
}