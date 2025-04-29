package com.arcade.view;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import com.arcade.controller.GameController;            // ← Import correcto
import com.arcade.model.game.Game;
import com.arcade.service.GameFactory.GameType;
import com.arcade.service.GameService;
import com.arcade.service.GameServiceImpl;
import com.arcade.view.components.GameHistoryPanel;

/**
 * Clase que implementa la vista principal de la aplicación
 * Muestra el menú con opciones para elegir juegos
 *
 * Este componente sigue el patrón Facade al proporcionar una interfaz unificada
 * para acceder a las diferentes vistas de juegos.
 */
public class MainView {

    private final Stage stage;
    private final BorderPane root;
    private final GameService gameService;
    private final ViewFactory viewFactory;

    private Label titleLabel;
    private Label subtitleLabel;
    private Button queensButton;
    private Button knightButton;
    private Button hanoiButton;
    private Button historyButton;
    private Button exitButton;

    /**
     * Constructor con escenario principal
     * @param stage escenario principal
     */
    public MainView(Stage stage) {
        this.stage = stage;
        this.root = new BorderPane();
        this.gameService = new GameServiceImpl();
        this.viewFactory = new ViewFactory(gameService);

        initialize();
    }

    /**
     * Inicializa la vista principal
     */
    private void initialize() {
        // Configurar escenario
        stage.setTitle("Máquina Arcade de Lógica");
        stage.setMinWidth(800);
        stage.setMinHeight(600);

        // Crear elementos de UI
        createUIElements();

        // Configurar eventos
        setupEventHandlers();

        // Crear y asignar escena
        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
        stage.setScene(scene);
    }

    /**
     * Crea los elementos de la interfaz de usuario
     */
    private void createUIElements() {
        // Crear encabezado
        titleLabel = new Label("Máquina Arcade de Lógica");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 28));

        subtitleLabel = new Label("Selecciona un juego para comenzar");
        subtitleLabel.setFont(Font.font("System", FontWeight.NORMAL, 16));

        VBox headerBox = new VBox(10, titleLabel, subtitleLabel);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setPadding(new Insets(20));

        // Crear tarjetas de juegos
        VBox queensCard = createGameCard(
                "N Reinas",
                "Coloca N reinas en un tablero sin que se amenacen entre sí",
                "queensButton"
        );

        VBox knightCard = createGameCard(
                "Recorrido del Caballo",
                "Recorre todo el tablero con el caballo de ajedrez sin repetir casilla",
                "knightButton"
        );

        VBox hanoiCard = createGameCard(
                "Torres de Hanoi",
                "Mueve todos los discos de una torre a otra siguiendo las reglas",
                "hanoiButton"
        );

        // Crear contenedor para tarjetas
        HBox cardsBox = new HBox(50, queensCard, knightCard, hanoiCard);
        cardsBox.setAlignment(Pos.CENTER);

        // Crear botones adicionales
        historyButton = new Button("Ver Historial de Partidas");
        historyButton.setPrefWidth(200);

        exitButton = new Button("Salir");
        exitButton.setPrefWidth(100);

        HBox bottomBox = new HBox(20, historyButton, exitButton);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setPadding(new Insets(20));

        // Organizar elementos en el panel principal
        VBox centerBox = new VBox(20, cardsBox, new javafx.scene.control.Separator(), bottomBox);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setPadding(new Insets(20));

        // Crear pie de página
        Label footerLabel = new Label("Proyecto de Técnicas de Programación - Versión 1.0");
        footerLabel.setFont(Font.font("System", 12));

        HBox footerBox = new HBox(footerLabel);
        footerBox.setAlignment(Pos.CENTER);
        footerBox.setPadding(new Insets(10));

        // Asignar elementos al panel principal
        root.setTop(headerBox);
        root.setCenter(centerBox);
        root.setBottom(footerBox);
    }

    /**
     * Crea una tarjeta para un juego
     * @param title título del juego
     * @param description descripción del juego
     * @param buttonId identificador para el botón
     * @return panel con la tarjeta
     */
    private VBox createGameCard(String title, String description, String buttonId) {
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 20));

        Label descLabel = new Label(description);
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(250);
        descLabel.setAlignment(Pos.CENTER);

        Button playButton = new Button("Jugar");
        playButton.setPrefWidth(150);

        // Guardar referencia al botón
        switch (buttonId) {
            case "queensButton":
                queensButton = playButton;
                break;
            case "knightButton":
                knightButton = playButton;
                break;
            case "hanoiButton":
                hanoiButton = playButton;
                break;
        }

        VBox card = new VBox(10, titleLabel, descLabel, playButton);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.getStyleClass().add("game-card");

        return card;
    }

    /**
     * Configura los manejadores de eventos
     */
    private void setupEventHandlers() {
        // Eventos de los botones de juegos
        queensButton.setOnAction(event -> openGame(GameType.QUEENS));
        knightButton.setOnAction(event -> openGame(GameType.KNIGHT));
        hanoiButton.setOnAction(event -> openGame(GameType.HANOI));

        // Evento para el historial
        historyButton.setOnAction(event -> openHistoryView());

        // Evento para salir
        exitButton.setOnAction(event -> Platform.exit());
    }

    /**
     * Abre una ventana con el juego seleccionado
     * @param gameType tipo de juego a abrir
     */
    private void openGame(GameType gameType) {
        try {
            // Ahora usa el GameController de com.arcade.controller
            GameController controller = viewFactory.createGameView(gameType);
            // La ventana ya se muestra dentro de createGameView(...)
        } catch (Exception e) {
            showError("Error al abrir el juego", e.getMessage());
        }
    }

    /**
     * Abre la ventana de historial de partidas
     */
    private void openHistoryView() {
        try {
            Stage historyStage = new Stage();
            historyStage.setTitle("Historial de Partidas");

            GameHistoryPanel historyPanel = new GameHistoryPanel(gameService);

            Scene scene = new Scene(historyPanel, 600, 400);
            scene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());

            historyStage.setScene(scene);
            historyStage.show();

        } catch (Exception e) {
            showError("Error al abrir el historial", e.getMessage());
        }
    }

    /**
     * Muestra la vista principal
     */
    public void show() {
        stage.show();
    }

    /**
     * Muestra un error en un diálogo
     * @param header encabezado del error
     * @param message mensaje de error
     */
    private void showError(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
