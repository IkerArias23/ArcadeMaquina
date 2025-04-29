package com.arcade.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

import com.arcade.ArcadeApplication;
import com.arcade.service.GameFactory;
import com.arcade.service.GameService;
import com.arcade.service.GameServiceImpl;
import com.arcade.service.GameFactory.GameType;

/**
 * Controlador para la vista principal de la aplicación
 * Gestiona el menú principal y la navegación a los diferentes juegos
 */
public class MainController {

    @FXML
    private VBox mainContainer;

    @FXML
    private Label titleLabel;

    @FXML
    private Button queensButton;

    @FXML
    private Button knightButton;

    @FXML
    private Button hanoiButton;

    @FXML
    private Button historyButton;

    private GameService gameService;

    /**
     * Inicialización del controlador
     */
    @FXML
    public void initialize() {
        gameService = new GameServiceImpl();

        // Configurar eventos de botones
        queensButton.setOnAction(event -> openGame(GameType.QUEENS));
        knightButton.setOnAction(event -> openGame(GameType.KNIGHT));
        hanoiButton.setOnAction(event -> openGame(GameType.HANOI));
        historyButton.setOnAction(event -> openHistoryView());

        // Actualizar etiquetas con información
        updateStats();
    }

    /**
     * Actualiza las estadísticas mostradas
     */
    private void updateStats() {
        int totalGames = gameService.getTotalGamesPlayed();
        int completedGames = gameService.getCompletedGamesCount();

        // Aquí se podrían actualizar etiquetas con estadísticas
    }

    /**
     * Abre la ventana de un juego específico
     * @param gameType tipo de juego a abrir
     */
    private void openGame(GameType gameType) {
        try {
            // Cargar el FXML correspondiente al juego
            String fxmlPath;
            String title;

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

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Obtener el controlador y pasar el servicio
            GameController controller = loader.getController();
            controller.setGameService(gameService);
            controller.setGameType(gameType);
            controller.initGame();

            // Crear y mostrar la ventana
            Stage gameStage = new Stage();
            gameStage.initModality(Modality.WINDOW_MODAL);
            gameStage.initOwner(ArcadeApplication.getInstance().getPrimaryStage());
            gameStage.setTitle(title);
            gameStage.setScene(new Scene(root, 800, 600));
            gameStage.setMinWidth(600);
            gameStage.setMinHeight(400);

            // Actualizar estadísticas al cerrar la ventana
            gameStage.setOnHidden(e -> updateStats());

            gameStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            // Mostrar un diálogo de error
        }
    }

    /**
     * Abre la vista de historial de partidas
     */
    private void openHistoryView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/history.fxml"));
            Parent root = loader.load();

            // Configurar el controlador de historial
            // HistoryController controller = loader.getController();
            // controller.setGameService(gameService);
            // controller.loadHistory();

            // Crear y mostrar la ventana
            Stage historyStage = new Stage();
            historyStage.initModality(Modality.WINDOW_MODAL);
            historyStage.initOwner(ArcadeApplication.getInstance().getPrimaryStage());
            historyStage.setTitle("Historial de Partidas");
            historyStage.setScene(new Scene(root, 600, 400));

            historyStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            // Mostrar un diálogo de error
        }
    }
}