package com.arcade.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.GridPane;

import com.arcade.model.entity.GameRecord;
import com.arcade.model.entity.KnightRecord;
import com.arcade.model.game.Game;
import com.arcade.model.game.knight.KnightGame;
import com.arcade.service.GameService;
import com.arcade.service.GameFactory.GameType;
import com.arcade.view.components.ChessBoard;

/**
 * Controlador para el juego del Recorrido del Caballo
 * Gestiona la interacción del usuario con el juego
 */
public class KnightController implements GameController {

    @FXML
    private GridPane mainContainer;

    @FXML
    private Label titleLabel;

    @FXML
    private Label sizeLabel;

    @FXML
    private Slider sizeSlider;

    @FXML
    private Spinner<Integer> startXSpinner;

    @FXML
    private Spinner<Integer> startYSpinner;

    @FXML
    private Button initButton;

    @FXML
    private Button solveButton;

    @FXML
    private Button stepButton;

    @FXML
    private Button resetButton;

    @FXML
    private Label stepsLabel;

    @FXML
    private Label movesLabel;

    @FXML
    private Label timeLabel;

    private GameService gameService;
    private GameType gameType;
    private KnightGame game;
    private ChessBoard chessBoard;
    private int lastX, lastY; // Última posición del caballo

    /**
     * Inicialización del controlador
     */
    @FXML
    public void initialize() {
        // Configurar controles
        sizeSlider.setMin(5);
        sizeSlider.setMax(8);
        sizeSlider.setValue(8);
        sizeSlider.setShowTickLabels(true);
        sizeSlider.setShowTickMarks(true);
        sizeSlider.setMajorTickUnit(1);
        sizeSlider.setMinorTickCount(0);
        sizeSlider.setSnapToTicks(true);

        // Configurar spinners
        configureSpinners((int) sizeSlider.getValue());

        // Actualizar etiqueta y spinners al cambiar el slider
        sizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int size = newVal.intValue();
            sizeLabel.setText("Tamaño del tablero: " + size + "x" + size);
            configureSpinners(size);
        });

        // Configurar eventos de botones
        initButton.setOnAction(event -> initGame());
        solveButton.setOnAction(event -> solveGame());
        stepButton.setOnAction(event -> stepGame());
        resetButton.setOnAction(event -> resetGame());

        // Deshabilitar botones hasta inicializar
        solveButton.setDisable(true);
        stepButton.setDisable(true);
        resetButton.setDisable(true);

        // Inicialmente no hay tablero
        chessBoard = null;
    }

    /**
     * Configura los spinners para seleccionar posición inicial
     * @param size tamaño del tablero
     */
    private void configureSpinners(int size) {
        SpinnerValueFactory<Integer> xValueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, size - 1, 0);
        SpinnerValueFactory<Integer> yValueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, size - 1, 0);

        startXSpinner.setValueFactory(xValueFactory);
        startYSpinner.setValueFactory(yValueFactory);
    }

    @Override
    public void setGameService(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    @Override
    public void initGame() {
        // Crear el juego si no existe
        if (game == null) {
            game = (KnightGame) gameService.createGame(gameType);
        }

        // Inicializar con los parámetros seleccionados
        int size = (int) sizeSlider.getValue();
        int startX = startXSpinner.getValue();
        int startY = startYSpinner.getValue();

        try {
            gameService.initializeGame(game, size, startX, startY);

            // Crear o actualizar el tablero
            if (chessBoard == null) {
                chessBoard = new ChessBoard(size, size);
                mainContainer.add(chessBoard, 1, 1);
            } else {
                chessBoard.resize(size, size);
                chessBoard.clear();
            }

            // Guardar posición inicial
            lastX = startX;
            lastY = startY;

            // Mostrar caballo en posición inicial
            chessBoard.placeKnightAt(startX, startY);
            chessBoard.placeNumberAt(startX, startY, 0); // Primer movimiento

            // Configurar interacción con el tablero
            chessBoard.setOnCellClick((x, y) -> handleBoardClick(x, y));

            // Actualizar UI
            updateUI();

            // Habilitar botones
            solveButton.setDisable(false);
            stepButton.setDisable(false);
            resetButton.setDisable(false);
            initButton.setDisable(true);
            sizeSlider.setDisable(true);
            startXSpinner.setDisable(true);
            startYSpinner.setDisable(true);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo inicializar el juego", e.getMessage());
        }
    }

    @Override
    public void stepGame() {
        if (game != null) {
            // Ejecutar un paso de la solución automática
            boolean success = false;

            // Obtener estado actual del tablero
            int[][] board = game.getBoard();
            int size = game.getBoardSize();

            // Buscar el último movimiento
            int lastMove = -1;
            int nextX = -1, nextY = -1;

            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (board[i][j] > lastMove) {
                        lastMove = board[i][j];
                        nextX = j;
                        nextY = i;
                    }
                }
            }

            // Realizar el siguiente movimiento automático
            if (nextX >= 0 && nextY >= 0) {
                success = game.move(lastX, lastY, nextX, nextY);

                if (success) {
                    // Actualizar tablero
                    chessBoard.placeKnightAt(nextX, nextY);
                    chessBoard.placeNumberAt(nextX, nextY, lastMove + 1);

                    // Actualizar última posición
                    lastX = nextX;
                    lastY = nextY;
                }
            }

            updateUI();

            if (!success) {
                showAlert(Alert.AlertType.INFORMATION, "Información",
                        "No se pueden realizar más pasos",
                        "El juego ha llegado a su estado final.");
            }
        }
    }

    @Override
    public void solveGame() {
        if (game != null) {
            // Deshabilitar controles durante la resolución
            setControlsEnabled(false);

            // Resolver en un hilo separado para no bloquear la UI
            new Thread(() -> {
                boolean success = gameService.solveGame(game);

                // Actualizar UI en el hilo de JavaFX
                Platform.runLater(() -> {
                    // Actualizar tablero con la solución
                    if (success) {
                        int[][] board = game.getBoard();
                        int size = game.getBoardSize();

                        chessBoard.clear();

                        // Mostrar secuencia de movimientos
                        for (int i = 0; i < size; i++) {
                            for (int j = 0; j < size; j++) {
                                if (board[i][j] >= 0) {
                                    chessBoard.placeNumberAt(j, i, board[i][j]);
                                }
                            }
                        }

                        // Colocar caballo en posición final
                        int lastMove = size * size - 1;
                        for (int i = 0; i < size; i++) {
                            for (int j = 0; j < size; j++) {
                                if (board[i][j] == lastMove) {
                                    chessBoard.placeKnightAt(j, i);
                                    lastX = j;
                                    lastY = i;
                                }
                            }
                        }
                    }

                    updateUI();
                    setControlsEnabled(true);

                    if (success) {
                        showAlert(Alert.AlertType.INFORMATION, "Éxito",
                                "Problema resuelto",
                                "Se ha encontrado un recorrido válido.");

                        // Guardar el resultado
                        endGame();
                    } else {
                        showAlert(Alert.AlertType.WARNING, "Advertencia",
                                "No se encontró solución",
                                "No se pudo encontrar un recorrido completo con los parámetros actuales.");
                    }
                });
            }).start();
        }
    }

    @Override
    public void resetGame() {
        if (game != null) {
            game.reset();

            // Limpiar tablero
            if (chessBoard != null) {
                chessBoard.clear();
            }

            // Habilitar controles
            initButton.setDisable(false);
            sizeSlider.setDisable(false);
            startXSpinner.setDisable(false);
            startYSpinner.setDisable(false);
            solveButton.setDisable(true);
            stepButton.setDisable(true);
            resetButton.setDisable(true);

            // Actualizar UI
            stepsLabel.setText("Pasos: 0");
            movesLabel.setText("Movimientos: 0");
            timeLabel.setText("Tiempo: 0s");
        }
    }

    @Override
    public void endGame() {
        if (game != null && game.isSolved()) {
            GameRecord record = gameService.saveGameRecord(game, true);

            if (record instanceof KnightRecord) {
                KnightRecord knightRecord = (KnightRecord) record;

                showAlert(Alert.AlertType.INFORMATION, "Partida guardada",
                        "Resultado guardado correctamente",
                        "Tablero " + knightRecord.getBoardSize() + "x" + knightRecord.getBoardSize() +
                                "\nPosición inicial: (" + knightRecord.getStartX() + "," + knightRecord.getStartY() + ")" +
                                "\nMovimientos: " + knightRecord.getTotalMoves() +
                                "\nPasos: " + knightRecord.getSteps() +
                                "\nTiempo: " + knightRecord.getElapsedTimeSeconds() + "s");
            }
        }
    }

    /**
     * Maneja los clics en el tablero
     * @param x coordenada x
     * @param y coordenada y
     */
    private void handleBoardClick(int x, int y) {
        if (game != null && chessBoard != null) {
            // Intentar mover el caballo
            boolean success = game.move(lastX, lastY, x, y);

            if (success) {
                // Mostrar movimiento en el tablero
                int[][] board = game.getBoard();
                chessBoard.placeKnightAt(x, y);
                chessBoard.placeNumberAt(x, y, board[y][x]);

                // Actualizar última posición
                lastX = x;
                lastY = y;

                // Verificar si se completó el recorrido
                if (game.isSolved()) {
                    showAlert(Alert.AlertType.INFORMATION, "¡Felicidades!",
                            "Recorrido completado",
                            "Has completado el recorrido del caballo correctamente.");
                    endGame();
                }
            }

            // Actualizar UI
            updateUI();
        }
    }

    /**
     * Actualiza la interfaz con el estado actual del juego
     */
    private void updateUI() {
        if (game != null) {
            // Actualizar etiquetas
            stepsLabel.setText("Pasos: " + game.getSteps());
            movesLabel.setText("Movimientos: " + game.getTotalMoves() + " / " +
                    (game.getBoardSize() * game.getBoardSize()));
            timeLabel.setText("Tiempo: " + game.getElapsedTimeSeconds() + "s");
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

    /**
     * Muestra una alerta
     * @param type tipo de alerta
     * @param title título
     * @param header encabezado
     * @param content contenido
     */
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}