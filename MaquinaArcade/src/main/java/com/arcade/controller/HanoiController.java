package com.arcade.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;

import java.util.List;
import java.util.Stack;

import com.arcade.model.entity.GameRecord;
import com.arcade.model.entity.HanoiRecord;
import com.arcade.model.game.Game;
import com.arcade.model.game.hanoi.Disk;
import com.arcade.model.game.hanoi.HanoiGame;
import com.arcade.model.game.hanoi.HanoiGame.Move;
import com.arcade.service.GameService;
import com.arcade.service.GameFactory.GameType;
import com.arcade.view.components.HanoiBoard;

/**
 * Controlador para el juego de Torres de Hanoi
 * Gestiona la interacción del usuario con el juego
 */
public class HanoiController implements GameController {

    @FXML
    private Pane mainContainer;

    @FXML
    private Label titleLabel;

    @FXML
    private Label disksLabel;

    @FXML
    private Slider disksSlider;

    @FXML
    private Button initButton;

    @FXML
    private Button solveButton;

    @FXML
    private Button stepButton;

    @FXML
    private Button resetButton;

    @FXML
    private Label movesLabel;

    @FXML
    private Label optimalLabel;

    @FXML
    private Label timeLabel;

    private GameService gameService;
    private GameType gameType;
    private HanoiGame game;
    private HanoiBoard hanoiBoard;
    private int selectedTower = -1; // Torre seleccionada para mover

    /**
     * Inicialización del controlador
     */
    @FXML
    public void initialize() {
        // Configurar controles
        disksSlider.setMin(3);
        disksSlider.setMax(10);
        disksSlider.setValue(5);
        disksSlider.setShowTickLabels(true);
        disksSlider.setShowTickMarks(true);
        disksSlider.setMajorTickUnit(1);
        disksSlider.setMinorTickCount(0);
        disksSlider.setSnapToTicks(true);

        // Actualizar etiqueta al cambiar el slider
        disksSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int disks = newVal.intValue();
            disksLabel.setText("Número de discos: " + disks);
            optimalLabel.setText("Movimientos óptimos: " + ((int) Math.pow(2, disks) - 1));
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

        // Actualizar etiqueta de movimientos óptimos
        int disks = (int) disksSlider.getValue();
        optimalLabel.setText("Movimientos óptimos: " + ((int) Math.pow(2, disks) - 1));

        // Inicialmente no hay tablero
        hanoiBoard = null;
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
            game = (HanoiGame) gameService.createGame(gameType);
        }

        // Inicializar con el número de discos seleccionado
        int disks = (int) disksSlider.getValue();
        try {
            gameService.initializeGame(game, disks);

            // Crear o actualizar el tablero
            if (hanoiBoard == null) {
                hanoiBoard = new HanoiBoard(disks);
                mainContainer.getChildren().add(hanoiBoard);
                hanoiBoard.relocate(50, 100);
            } else {
                hanoiBoard.setNumDisks(disks);
            }

            // Configurar interacción con el tablero
            hanoiBoard.setOnTowerClick(tower -> handleTowerClick(tower));

            // Actualizar tablero con estado inicial
            updateHanoiBoard();

            // Actualizar UI
            updateUI();

            // Habilitar botones
            solveButton.setDisable(false);
            stepButton.setDisable(false);
            resetButton.setDisable(false);
            initButton.setDisable(true);
            disksSlider.setDisable(true);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "No se pudo inicializar el juego", e.getMessage());
        }
    }

    @Override
    public void stepGame() {
        if (game != null) {
            boolean success = gameService.stepGame(game);

            // Actualizar tablero
            updateHanoiBoard();

            // Actualizar UI
            updateUI();

            if (!success) {
                showAlert(Alert.AlertType.INFORMATION, "Información",
                        "No se pueden realizar más pasos",
                        "El juego ha llegado a su estado final.");
            }

            // Verificar si se completó el puzzle
            if (game.isSolved()) {
                showAlert(Alert.AlertType.INFORMATION, "¡Felicidades!",
                        "Puzzle completado",
                        "Se ha completado el puzzle con " + game.getMoves().size() +
                                " movimientos. Movimientos óptimos: " + game.getMinimumMoves());
                endGame();
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
                    // Actualizar tablero
                    updateHanoiBoard();

                    // Actualizar UI
                    updateUI();

                    setControlsEnabled(true);

                    if (success) {
                        showAlert(Alert.AlertType.INFORMATION, "Éxito",
                                "Puzzle resuelto",
                                "Se ha resuelto el puzzle con " + game.getMoves().size() +
                                        " movimientos (óptimo: " + game.getMinimumMoves() + ").");

                        // Guardar el resultado
                        endGame();
                    }
                });
            }).start();
        }
    }

    @Override
    public void resetGame() {
        if (game != null) {
            game.reset();

            // Habilitar controles
            initButton.setDisable(false);
            disksSlider.setDisable(false);
            solveButton.setDisable(true);
            stepButton.setDisable(true);
            resetButton.setDisable(true);

            // Limpiar selección
            selectedTower = -1;

            // Quitar el tablero
            if (hanoiBoard != null) {
                mainContainer.getChildren().remove(hanoiBoard);
                hanoiBoard = null;
            }

            // Actualizar UI
            movesLabel.setText("Movimientos: 0");
            timeLabel.setText("Tiempo: 0s");
        }
    }

    @Override
    public void endGame() {
        if (game != null && game.isSolved()) {
            GameRecord record = gameService.saveGameRecord(game, true);

            if (record instanceof HanoiRecord) {
                HanoiRecord hanoiRecord = (HanoiRecord) record;

                showAlert(Alert.AlertType.INFORMATION, "Partida guardada",
                        "Resultado guardado correctamente",
                        "Discos: " + hanoiRecord.getNumDisks() +
                                "\nMovimientos: " + hanoiRecord.getMovements() +
                                " (óptimo: " + hanoiRecord.getMinimumMoves() + ")" +
                                "\nSolución óptima: " + (hanoiRecord.isOptimalSolution() ? "Sí" : "No") +
                                "\nTiempo: " + hanoiRecord.getElapsedTimeSeconds() + "s");
            }
        }
    }

    /**
     * Maneja los clics en las torres
     * @param tower índice de la torre (0, 1 o 2)
     */
    private void handleTowerClick(int tower) {
        if (game != null && hanoiBoard != null) {
            if (selectedTower == -1) {
                // Primera selección (torre origen)
                List<Stack<Disk>> towers = game.getTowers();

                // Verificar que la torre tenga discos
                if (towers.get(tower).isEmpty()) {
                    return;
                }

                // Seleccionar torre
                selectedTower = tower;
                hanoiBoard.highlightTower(tower);

            } else {
                // Segunda selección (torre destino)
                if (selectedTower != tower) {
                    // Intentar mover disco
                    boolean success = game.moveDisk(selectedTower, tower);

                    if (success) {
                        // Actualizar tablero
                        updateHanoiBoard();

                        // Verificar si se completó el puzzle
                        if (game.isSolved()) {
                            showAlert(Alert.AlertType.INFORMATION, "¡Felicidades!",
                                    "Puzzle completado",
                                    "Has completado el puzzle con " + game.getMoves().size() +
                                            " movimientos. Movimientos óptimos: " + game.getMinimumMoves());
                            endGame();
                        }
                    } else {
                        showAlert(Alert.AlertType.WARNING, "Movimiento inválido",
                                "No se puede mover el disco",
                                "No se puede colocar un disco más grande sobre uno más pequeño.");
                    }
                }

                // Limpiar selección
                selectedTower = -1;
            }

            // Actualizar UI
            updateUI();
        }
    }

    /**
     * Actualiza el tablero de Hanoi con el estado actual del juego
     */
    private void updateHanoiBoard() {
        if (game != null && hanoiBoard != null) {
            List<Stack<Disk>> towers = game.getTowers();

            // Convertir las pilas de discos a arreglos para el tablero
            int[][] towerDisks = new int[3][];

            for (int i = 0; i < 3; i++) {
                Stack<Disk> tower = towers.get(i);
                towerDisks[i] = new int[tower.size()];

                for (int j = 0; j < tower.size(); j++) {
                    towerDisks[i][j] = tower.get(j).getSize();
                }
            }

            // Actualizar el tablero
            hanoiBoard.updateTowers(towerDisks);
        }
    }

    /**
     * Actualiza la interfaz con el estado actual del juego
     */
    private void updateUI() {
        if (game != null) {
            // Actualizar etiquetas
            movesLabel.setText("Movimientos: " + game.getMoves().size() +
                    " / " + game.getMinimumMoves());
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