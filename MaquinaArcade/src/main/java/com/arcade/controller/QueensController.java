package com.arcade.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;

import com.arcade.model.entity.GameRecord;
import com.arcade.model.entity.QueenRecord;
import com.arcade.model.game.Game;
import com.arcade.model.game.queens.QueensGame;
import com.arcade.service.GameService;
import com.arcade.service.GameFactory.GameType;
import com.arcade.view.components.ChessBoard;

/**
 * Controlador para el juego de N Reinas
 * Gestiona la interacción del usuario con el juego
 */
public class QueensController implements GameController {

    @FXML
    private GridPane mainContainer;

    @FXML
    private Label titleLabel;

    @FXML
    private Label sizeLabel;

    @FXML
    private Slider sizeSlider;

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
    private Label timeLabel;

    private GameService gameService;
    private GameType gameType;
    private QueensGame game;
    private ChessBoard chessBoard;
    private int currentRow = 0; // Para seguir el progreso de la solución paso a paso

    /**
     * Inicialización del controlador
     */
    @FXML
    public void initialize() {
        // Configurar controles
        sizeSlider.setMin(4);
        sizeSlider.setMax(12);
        sizeSlider.setValue(8);
        sizeSlider.setShowTickLabels(true);
        sizeSlider.setShowTickMarks(true);
        sizeSlider.setMajorTickUnit(1);
        sizeSlider.setMinorTickCount(0);
        sizeSlider.setSnapToTicks(true);

        // Actualizar etiqueta al cambiar el slider
        sizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            sizeLabel.setText("Tamaño del tablero: " + newVal.intValue() + "x" + newVal.intValue());
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
            game = (QueensGame) gameService.createGame(gameType);
        }

        // Inicializar con el tamaño seleccionado
        int size = (int) sizeSlider.getValue();
        try {
            gameService.initializeGame(game, size);
            currentRow = 0; // Reiniciar el progreso del paso a paso

            // Crear o actualizar el tablero
            if (chessBoard == null) {
                chessBoard = new ChessBoard(size, size);
                mainContainer.add(chessBoard, 1, 1);
            } else {
                chessBoard.resize(size, size);
                chessBoard.clear();
            }

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

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo inicializar el juego", e.getMessage());
        }
    }

    @Override
    public void stepGame() {
        if (game != null) {
            int boardSize = game.getBoardSize();

            // Si ya llegamos al final del tablero, no hay más pasos que dar
            if (currentRow >= boardSize) {
                showAlert(Alert.AlertType.INFORMATION, "Información",
                        "No se pueden realizar más pasos",
                        "Todas las reinas han sido colocadas.");
                return;
            }

            // Obtener el estado actual de las reinas
            int[] queensPositions = game.getQueensPositions();

            // Determinar la posición de inicio para buscar en la fila actual
            int startCol = -1;
            if (queensPositions[currentRow] >= 0) {
                // Si ya hay una reina en esta fila, empezamos en la siguiente columna
                startCol = queensPositions[currentRow] + 1;
                // Primero quitamos la reina actual
                game.removeQueen(currentRow, queensPositions[currentRow]);
            } else {
                // Si no hay reina, empezamos en la columna 0
                startCol = 0;
            }

            // Intentar colocar una reina en la fila actual
            boolean foundValidPosition = false;

            // Probar cada columna desde startCol hasta el final
            for (int col = startCol; col < boardSize; col++) {
                if (game.placeQueen(currentRow, col)) {
                    foundValidPosition = true;
                    break;
                }
            }

            // Si no encontramos una posición válida, retrocedemos
            if (!foundValidPosition) {
                // Si no se pudo colocar en ninguna posición, retroceder
                if (currentRow > 0) {
                    currentRow--;
                    // El mensaje debe indicar que retrocedemos a la fila anterior
                    showAlert(Alert.AlertType.INFORMATION, "Retroceso",
                            "Retroceso",
                            "No hay posición válida en la fila " + (currentRow + 1) +
                                    ". Retrocediendo a la fila " + (currentRow + 1) + ".");
                } else {
                    // Si estamos en la primera fila y no hay posición válida, no hay solución
                    showAlert(Alert.AlertType.INFORMATION, "Información",
                            "No hay solución",
                            "No se puede encontrar una solución para este tablero.");
                }
            } else {
                // Avanzar a la siguiente fila
                currentRow++;

                // Si hemos llegado al final, hemos encontrado una solución
                if (currentRow == boardSize) {
                    showAlert(Alert.AlertType.INFORMATION, "¡Éxito!",
                            "Solución encontrada",
                            "Se ha encontrado una solución válida para el tablero de " +
                                    boardSize + " reinas.");
                    endGame();
                }
            }

            // Actualizar la interfaz
            updateUI();
        }
    }

    @Override
    public void solveGame() {
        if (game != null) {
            // Deshabilitar controles durante la resolución
            setControlsEnabled(false);

            // Resolver en un hilo separado para no bloquear la UI
            new Thread(() -> {
                boolean success = false;
                try {
                    success = gameService.solveGame(game);
                    // Actualizar contador de filas para el paso a paso
                    if (success) {
                        currentRow = game.getBoardSize();
                    }
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        showAlert(Alert.AlertType.ERROR, "Error",
                                "Error al resolver",
                                "Ocurrió un error al resolver: " + e.getMessage());
                    });
                } finally {
                    final boolean finalSuccess = success;

                    // Actualizar UI en el hilo de JavaFX
                    Platform.runLater(() -> {
                        updateUI();
                        setControlsEnabled(true);

                        if (finalSuccess) {
                            showAlert(Alert.AlertType.INFORMATION, "Éxito",
                                    "Problema resuelto",
                                    "Se ha encontrado una solución válida.");

                            // Guardar el resultado
                            endGame();
                        } else {
                            showAlert(Alert.AlertType.WARNING, "Advertencia",
                                    "No se encontró solución",
                                    "No se pudo resolver el problema con los parámetros actuales.");
                        }
                    });
                }
            }).start();
        }
    }

    @Override
    public void resetGame() {
        if (game != null) {
            game.reset();
            currentRow = 0; // Reiniciar el progreso del paso a paso

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

            // Actualizar UI
            stepsLabel.setText("Pasos: 0");
            timeLabel.setText("Tiempo: 0s");
        }
    }

    @Override
    public void endGame() {
        if (game != null && game.isSolved()) {
            GameRecord record = gameService.saveGameRecord(game, true);

            if (record instanceof QueenRecord) {
                QueenRecord queenRecord = (QueenRecord) record;

                showAlert(Alert.AlertType.INFORMATION, "Partida guardada",
                        "Resultado guardado correctamente",
                        "Tablero " + queenRecord.getBoardSize() + "x" + queenRecord.getBoardSize() +
                                "\nPasos: " + queenRecord.getSteps() +
                                "\nTiempo: " + queenRecord.getElapsedTimeSeconds() + "s");
            }
        }
    }

    /**
     * Maneja los clics en el tablero
     * @param x coordenada X
     * @param y coordenada Y
     */
    private void handleBoardClick(int x, int y) {
        if (game != null && chessBoard != null) {
            // Verificar si ya hay una reina en la casilla
            int[] positions = game.getQueensPositions();
            boolean hasQueen = false;

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
                    if (game.isSolved()) {
                        showAlert(Alert.AlertType.INFORMATION, "¡Felicidades!",
                                "Problema resuelto correctamente",
                                "Has colocado las reinas correctamente.");
                        endGame();
                    }
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