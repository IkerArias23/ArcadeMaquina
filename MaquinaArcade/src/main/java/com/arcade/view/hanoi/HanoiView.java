package com.arcade.view.hanoi;

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

import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

import com.arcade.model.entity.GameRecord;
import com.arcade.model.entity.HanoiRecord;
import com.arcade.model.game.Game;
import com.arcade.model.game.hanoi.Disk;
import com.arcade.model.game.hanoi.HanoiGame;
import com.arcade.model.game.hanoi.HanoiGame.Move;
import com.arcade.service.GameService;
import com.arcade.view.GameView;
import com.arcade.view.components.HanoiBoard;

/**
 * Vista para el juego de Torres de Hanoi
 * Permite visualizar e interactuar con las torres y discos
 */
public class HanoiView extends BorderPane implements GameView {

    private HanoiGame game;
    private GameService gameService;
    private boolean autoMode;
    private int selectedTower = -1; // Torre seleccionada para mover

    // Componentes de la interfaz
    private Label titleLabel;
    private Label infoLabel;
    private Label disksLabel;
    private Slider disksSlider;
    private Button initButton;
    private Button solveButton;
    private Button stepButton;
    private Button resetButton;
    private Label movesLabel;
    private Label optimalLabel;
    private Label timeLabel;
    private HanoiBoard hanoiBoard;

    /**
     * Constructor por defecto
     */
    public HanoiView() {
        super();
        this.autoMode = false;
        createUI();
    }

    /**
     * Crea los elementos de la interfaz
     */
    private void createUI() {
        // Panel superior (título)
        titleLabel = new Label("Torres de Hanoi");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        infoLabel = new Label("Mueve todos los discos de la primera torre a la tercera, un disco a la vez");
        infoLabel.setStyle("-fx-font-size: 14px;");

        VBox topBox = new VBox(10, titleLabel, infoLabel);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(10));

        // Panel izquierdo (controles)
        disksLabel = new Label("Número de discos: 5");

        disksSlider = new Slider(3, 10, 5);
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

        movesLabel = new Label("Movimientos: 0 / 31");
        optimalLabel = new Label("Movimientos óptimos: 31");
        timeLabel = new Label("Tiempo: 0s");

        Label instructionsLabel = new Label("Instrucciones:");
        instructionsLabel.setStyle("-fx-font-weight: bold;");

        Label instructionsTextLabel = new Label(
                "1. Haz clic en una torre para seleccionarla\n" +
                        "2. Haz clic en otra torre para mover el disco\n" +
                        "3. No puedes colocar un disco más grande sobre uno más pequeño"
        );
        instructionsTextLabel.setWrapText(true);

        VBox leftBox = new VBox(15);
        leftBox.getChildren().addAll(
                new Label("Configuración"),
                disksLabel,
                disksSlider,
                initButton,
                new javafx.scene.control.Separator(),
                controlsLabel,
                solveButton,
                stepButton,
                resetButton,
                new javafx.scene.control.Separator(),
                statsLabel,
                movesLabel,
                optimalLabel,
                timeLabel,
                new javafx.scene.control.Separator(),
                instructionsLabel,
                instructionsTextLabel
        );
        leftBox.setPrefWidth(220);
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
        hanoiBoard = null;
    }

    @Override
    public void initialize(Game game, GameService gameService) {
        if (!(game instanceof HanoiGame)) {
            throw new IllegalArgumentException("Se esperaba un juego de tipo HanoiGame");
        }

        this.game = (HanoiGame) game;
        this.gameService = gameService;
    }

    /**
     * Inicializa el juego con el número de discos seleccionado
     */
    private void initializeGame() {
        int disks = (int) disksSlider.getValue();

        try {
            // Inicializar juego si no existe
            if (game == null) {
                game = new HanoiGame();
            }

            game.initialize(disks);

            // Crear o actualizar el tablero de Hanoi
            if (hanoiBoard == null) {
                hanoiBoard = new HanoiBoard(disks);

                // Configurar el evento de clic
                hanoiBoard.setOnTowerClick(this::handleTowerClick);

                // Agregar al centro
                setCenter(new GridPane()); // Limpiar el centro
                GridPane centerPane = new GridPane();
                centerPane.setAlignment(Pos.CENTER);
                centerPane.setPadding(new Insets(20));
                centerPane.add(hanoiBoard, 0, 0);
                setCenter(centerPane);

            } else {
                hanoiBoard.setNumDisks(disks);
            }

            // Actualizar tablero con estado inicial
            updateHanoiBoard();

            // Habilitar/deshabilitar botones
            solveButton.setDisable(false);
            stepButton.setDisable(false);
            resetButton.setDisable(false);
            initButton.setDisable(true);
            disksSlider.setDisable(true);

            // Actualizar etiquetas
            int minMoves = (int) Math.pow(2, disks) - 1;
            movesLabel.setText("Movimientos: 0 / " + minMoves);
            optimalLabel.setText("Movimientos óptimos: " + minMoves);
            timeLabel.setText("Tiempo: 0s");

        } catch (Exception e) {
            showError("Error: " + e.getMessage());
        }
    }

    /**
     * Maneja el clic en una torre
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
                            showMessage("¡Felicidades! Has completado el puzzle con " +
                                    game.getMoves().size() + " movimientos.\n" +
                                    "Movimientos óptimos: " + game.getMinimumMoves());
                            saveGameRecord(true);
                        }
                    } else {
                        showError("Movimiento inválido. No se puede colocar un disco más grande sobre uno más pequeño.");
                    }
                }

                // Limpiar selección
                selectedTower = -1;
                hanoiBoard.highlightTower(-1);
            }

            // Actualizar interfaz
            updateView();
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

    @Override
    public void updateView() {
        if (game != null) {
            // Actualizar etiquetas
            movesLabel.setText("Movimientos: " + game.getMoves().size() +
                    " / " + game.getMinimumMoves());
            timeLabel.setText("Tiempo: " + game.getElapsedTimeSeconds() + "s");
        }
    }

    @Override
    public void showStep() {
        if (game != null && hanoiBoard != null) {
            // Ejecutar un paso de la solución automática
            if (game.getOptimalSolution().size() <= game.getMoves().size()) {
                showMessage("No hay más pasos disponibles.");
                return;
            }

            int currentStep = game.getMoves().size();
            boolean success = game.step();

            // Actualizar tablero
            updateHanoiBoard();

            // Actualizar interfaz
            updateView();

            if (!success) {
                showMessage("No se pueden realizar más pasos.");
            }

            // Verificar si se completó el puzzle
            if (game.isSolved()) {
                showMessage("Puzzle completado con " + game.getMoves().size() +
                        " movimientos (óptimo: " + game.getMinimumMoves() + ").");
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
                // Resolver puzzle
                boolean success = false;

                // Opcionalmente, mostrar una animación paso a paso
                if (autoMode) {
                    // Reiniciar juego
                    game.reset();
                    game.initialize(game.getNumDisks());

                    // Mostrar solución paso a paso
                    AtomicInteger currentStep = new AtomicInteger(0);
                    List<Move> solution = game.getOptimalSolution();

                    Platform.runLater(() -> updateHanoiBoard());

                    for (Move move : solution) {
                        try {
                            Thread.sleep(500); // Pausa entre movimientos

                            // Ejecutar movimiento en el hilo de JavaFX
                            Platform.runLater(() -> {
                                game.moveDisk(move.getFromTower(), move.getToTower());
                                updateHanoiBoard();
                                updateView();
                                currentStep.incrementAndGet();
                            });

                            Thread.sleep(500); // Pausa para ver el resultado

                        } catch (InterruptedException e) {
                            break;
                        }
                    }

                    success = true;

                } else {
                    // Resolver de una vez
                    success = game.solve();

                    // Actualizar interfaz en el hilo de JavaFX
                    Platform.runLater(() -> {
                        updateHanoiBoard();
                        updateView();
                    });
                }

                final boolean finalSuccess = success;

                // Actualizar UI en el hilo de JavaFX
                Platform.runLater(() -> {
                    setControlsEnabled(true);

                    if (finalSuccess) {
                        showMessage("Se ha resuelto el puzzle con " + game.getMoves().size() +
                                " movimientos (óptimo: " + game.getMinimumMoves() + ").");
                        saveGameRecord(true);
                    } else {
                        showError("Ocurrió un error al resolver el puzzle.");
                    }
                });
            }).start();
        }
    }

    @Override
    public void reset() {
        if (game != null) {
            game.reset();

            // Limpiar selección
            selectedTower = -1;

            // Quitar el tablero
            if (hanoiBoard != null) {
                // Alternativa: podiamos reiniciar el tablero en vez de eliminarlo
                getChildren().remove(hanoiBoard);
                hanoiBoard = null;
            }

            // Habilitar controles
            initButton.setDisable(false);
            disksSlider.setDisable(false);
            solveButton.setDisable(true);
            stepButton.setDisable(true);
            resetButton.setDisable(true);

            // Actualizar etiquetas
            movesLabel.setText("Movimientos: 0 / ?");
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

                if (record instanceof HanoiRecord) {
                    HanoiRecord hanoiRecord = (HanoiRecord) record;

                    String message = String.format(
                            "Resultado guardado:\nDiscos: %d\nMovimientos: %d (óptimo: %d)\n" +
                                    "Solución óptima: %s\nTiempo: %ds",
                            hanoiRecord.getNumDisks(),
                            hanoiRecord.getMovements(),
                            hanoiRecord.getMinimumMoves(),
                            hanoiRecord.isOptimalSolution() ? "Sí" : "No",
                            hanoiRecord.getElapsedTimeSeconds()
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
        return "Torres de Hanoi";
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