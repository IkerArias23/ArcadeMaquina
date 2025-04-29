package com.arcade.view.queens;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import com.arcade.model.entity.GameRecord;
import com.arcade.model.entity.QueenRecord;
import com.arcade.model.game.Game;
import com.arcade.model.game.queens.QueensGame;
import com.arcade.service.GameService;
import com.arcade.view.GameView;
import com.arcade.view.components.ChessBoard;

/**
 * Vista para el juego de N Reinas
 * Permite visualizar el tablero y controlar el juego
 */
public class QueensView extends BorderPane implements GameView {

    private QueensGame game;
    private GameService gameService;
    private boolean autoMode;

    // Componentes de la interfaz
    private Label titleLabel;
    private Label infoLabel;
    private Label sizeLabel;
    private Slider sizeSlider;
    private Button initButton;
    private Button solveButton;
    private Button stepButton;
    private Button resetButton;
    private Label stepsLabel;
    private Label timeLabel;
    private ChessBoard chessBoard;

    /**
     * Constructor por defecto
     */
    public QueensView() {
        super();
        this.autoMode = false;
        createUI();
    }

    /**
     * Crea los elementos de la interfaz
     */
    private void createUI() {
        // Panel superior (título)
        titleLabel = new Label("Problema de las N Reinas");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        infoLabel = new Label("Coloca N reinas en un tablero de forma que ninguna amenace a otra");
        infoLabel.setStyle("-fx-font-size: 14px;");

        VBox topBox = new VBox(10, titleLabel, infoLabel);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(10));

        // Panel izquierdo (controles)
        sizeLabel = new Label("Tamaño del tablero: 8x8");

        sizeSlider = new Slider(4, 12, 8);
        sizeSlider.setShowTickLabels(true);
        sizeSlider.setShowTickMarks(true);
        sizeSlider.setMajorTickUnit(1);
        sizeSlider.setMinorTickCount(0);
        sizeSlider.setSnapToTicks(true);

        // Actualizar etiqueta al cambiar el slider
        sizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            sizeLabel.setText("Tamaño del tablero: " + newVal.intValue() + "x" + newVal.intValue());
        });

        initButton = new Button("Iniciar Juego");
        initButton.setPrefWidth(150);
        initButton.setStyle("-fx-font-weight: bold;");

        Label controlsLabel = new Label("Controles");
        controlsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        solveButton = new Button("Resolver Automáticamente");
        solveButton.setPrefWidth(180);
        solveButton.setDisable(true);

        stepButton = new Button("Paso a Paso");
        stepButton.setPrefWidth(180);
        stepButton.setDisable(true);

        resetButton = new Button("Reiniciar");
        resetButton.setPrefWidth(180);
        resetButton.setDisable(true);

        Label statsLabel = new Label("Estadísticas");
        statsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        stepsLabel = new Label("Pasos: 0");
        timeLabel = new Label("Tiempo: 0s");

        Label instructionsLabel = new Label("Instrucciones:");
        instructionsLabel.setStyle("-fx-font-weight: bold;");

        Label instructionsTextLabel = new Label("Haz clic en una casilla para colocar o quitar una reina");
        instructionsTextLabel.setWrapText(true);

        VBox leftBox = new VBox(15);
        leftBox.getChildren().addAll(
                new Label("Configuración"),
                sizeLabel,
                sizeSlider,
                initButton,
                new javafx.scene.control.Separator(),
                controlsLabel,
                solveButton,
                stepButton,
                resetButton,
                new javafx.scene.control.Separator(),
                statsLabel,
                stepsLabel,
                timeLabel,
                new javafx.scene.control.Separator(),
                instructionsLabel,
                instructionsTextLabel
        );
        leftBox.setPrefWidth(200);
        leftBox.setPadding(new Insets(20));

        // Configurar eventos
        initButton.setOnAction(e -> initializeGame());
        solveButton.setOnAction(e -> showSolution());
        stepButton.setOnAction(e -> showStep());
        resetButton.setOnAction(e -> reset());

        // Asignar paneles
        setTop(topBox);
        setLeft(leftBox);

        // Inicialmente no hay tablero
        chessBoard = null;
    }

    @Override
    public void initialize(Game game, GameService gameService) {
        if (!(game instanceof QueensGame)) {
            throw new IllegalArgumentException("Se esperaba un juego de tipo QueensGame");
        }

        this.game = (QueensGame) game;
        this.gameService = gameService;
    }

    /**
     * Inicializa el juego con el tamaño seleccionado
     */
    private void initializeGame() {
        int size = (int) sizeSlider.getValue();

        try {
            // Inicializar juego si no existe
            if (game == null) {
                game = new QueensGame();
            }

            game.initialize(size);

            // Crear o actualizar el tablero
            if (chessBoard == null) {
                chessBoard = new ChessBoard(size, size);

                // Configurar el evento de clic
                chessBoard.setOnCellClick((x, y) -> handleBoardClick(x, y));

                // Agregar al centro
                setCenter(new GridPane()); // Limpiar el centro
                GridPane centerPane = new GridPane();
                centerPane.setAlignment(Pos.CENTER);
                centerPane.setPadding(new Insets(20));
                centerPane.add(chessBoard, 0, 0);
                setCenter(centerPane);

            } else {
                chessBoard.resize(size, size);
                chessBoard.clear();
            }

            // Habilitar/deshabilitar botones
            solveButton.setDisable(false);
            stepButton.setDisable(false);
            resetButton.setDisable(false);
            initButton.setDisable(true);
            sizeSlider.setDisable(true);

            // Actualizar etiquetas
            updateView();

        } catch (Exception e) {
            showError("Error: " + e.getMessage());
        }
    }

    /**
     * Maneja el clic en una celda del tablero
     * @param x coordenada X
     * @param y coordenada Y
     */
    private void handleBoardClick(int x, int y) {
        if (game != null && chessBoard != null) {
            // Obtener posiciones actuales
            int[] positions = game.getQueensPositions();
            boolean hasQueen = false;

            // Verificar si ya hay una reina en la casilla
            for (int i = 0; i < positions.length; i++) {
                if (positions[i] == x && i == y) {
                    hasQueen = true;
                    break;
                }
            }

            if (hasQueen) {
                // Quitar reina
                game.removeQueen(y, x);
                chessBoard.removeQueenAt(x, y);
            } else {
                // Intentar colocar reina
                boolean success = game.placeQueen(y, x);

                if (success) {
                    chessBoard.placeQueenAt(x, y);

                    // Verificar si se completó el tablero
                    if (game.isValidSolution()) {
                        showMessage("¡Felicidades! Has resuelto el problema correctamente.");
                        saveGameRecord(true);
                    }
                }
            }

            // Actualizar interfaz
            updateView();
        }
    }

    @Override
    public void updateView() {
        if (game != null) {
            // Actualizar etiquetas
            stepsLabel.setText("Pasos: " + game.getSteps());
            timeLabel.setText("Tiempo: " + game.getElapsedTimeSeconds() + "s");

            // Actualizar tablero con las posiciones de las reinas
            if (chessBoard != null) {
                chessBoard.clear();

                int[] positions = game.getQueensPositions();
                for (int row = 0; row < positions.length; row++) {
                    int col = positions[row];
                    if (col >= 0) {
                        chessBoard.placeQueenAt(col, row);
                    }
                }
            }
        }
    }

    @Override
    public void showStep() {
        if (game != null) {
            // Desactivar botones durante la operación
            setControlsEnabled(false);

            // Hacer un paso en el algoritmo
            boolean success = game.step();

            // Actualizar vista
            updateView();

            // Reactivar botones
            setControlsEnabled(true);

            if (!success) {
                showMessage("No se pueden realizar más pasos.");
            }

            // Verificar si se completó
            if (game.isSolved()) {
                showMessage("Problema resuelto correctamente.");
                saveGameRecord(true);
            }
        }
    }

    @Override
    public void showSolution() {
        if (game != null) {
            // Desactivar botones durante la resolución
            setControlsEnabled(false);

            // Resolver en un hilo separado para no bloquear la UI
            new Thread(() -> {
                boolean success = game.solve();

                // Actualizar UI en el hilo de JavaFX
                Platform.runLater(() -> {
                    updateView();
                    setControlsEnabled(true);

                    if (success) {
                        showMessage("Se ha encontrado una solución válida.");
                        saveGameRecord(true);
                    } else {
                        showMessage("No se pudo resolver el problema con los parámetros actuales.");
                    }
                });
            }).start();
        }
    }

    @Override
    public void reset() {
        if (game != null) {
            game.reset();

            // Limpiar tablero
            if (chessBoard != null) {
                chessBoard.clear();
            }

            // Habilitar controles
            initButton.setDisable(false);
            sizeSlider.setDisable(false);
            solveButton.setDisable(true);
            stepButton.setDisable(true);
            resetButton.setDisable(true);

            // Actualizar etiquetas
            stepsLabel.setText("Pasos: 0");
            timeLabel.setText("Tiempo: 0s");
        }
    }

    /**
     * Guarda un registro del juego en la base de datos
     * @param completed indica si el juego se completó
     */
    private void saveGameRecord(boolean completed) {
        if (game != null && gameService != null) {
            try {
                GameRecord record = gameService.saveGameRecord(game, completed);

                if (record instanceof QueenRecord) {
                    QueenRecord queenRecord = (QueenRecord) record;

                    String message = String.format(
                            "Resultado guardado:\nTablero %dx%d\nPasos: %d\nTiempo: %ds",
                            queenRecord.getBoardSize(), queenRecord.getBoardSize(),
                            queenRecord.getSteps(), queenRecord.getElapsedTimeSeconds()
                    );

                    showMessage(message);
                }
            } catch (Exception e) {
                showError("Error al guardar: " + e.getMessage());
            }
        }
    }

    /**
     * Habilita o deshabilita los controles
     * @param enabled true para habilitar, false para deshabilitar
     */
    private void setControlsEnabled(boolean enabled) {
        solveButton.setDisable(!enabled);
        stepButton.setDisable(!enabled);
        resetButton.setDisable(!enabled);
    }

    @Override
    public void showMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void showError(String error) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(error);
        alert.showAndWait();
    }

    @Override
    public Game getGame() {
        return game;
    }

    @Override
    public String getTitle() {
        return "Problema de las N Reinas";
    }

    @Override
    public boolean isGameCompleted() {
        return game != null && game.isSolved();
    }

    @Override
    public void setAutoMode(boolean autoMode) {
        this.autoMode = autoMode;
    }

    @Override
    public boolean isAutoMode() {
        return autoMode;
    }
}