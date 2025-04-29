package com.arcade.view.knight;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import com.arcade.model.entity.GameRecord;
import com.arcade.model.entity.KnightRecord;
import com.arcade.model.game.Game;
import com.arcade.model.game.knight.KnightGame;
import com.arcade.service.GameService;
import com.arcade.view.GameView;
import com.arcade.view.components.ChessBoard;

/**
 * Vista para el juego del Recorrido del Caballo
 * Permite visualizar el tablero y controlar el juego
 */
public class KnightView extends BorderPane implements GameView {

    private KnightGame game;
    private GameService gameService;
    private boolean autoMode;
    private int lastX, lastY; // Última posición del caballo

    // Componentes de la interfaz
    private Label titleLabel;
    private Label infoLabel;
    private Label sizeLabel;
    private Slider sizeSlider;
    private Spinner<Integer> startXSpinner;
    private Spinner<Integer> startYSpinner;
    private Button initButton;
    private Button solveButton;
    private Button stepButton;
    private Button resetButton;
    private Label stepsLabel;
    private Label movesLabel;
    private Label timeLabel;
    private ChessBoard chessBoard;

    /**
     * Constructor por defecto
     */
    public KnightView() {
        super();
        this.autoMode = false;
        createUI();
    }

    /**
     * Crea los elementos de la interfaz
     */
    private void createUI() {
        // Panel superior (título)
        titleLabel = new Label("Recorrido del Caballo");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        infoLabel = new Label("Recorre el tablero con el caballo pasando una sola vez por cada casilla");
        infoLabel.setStyle("-fx-font-size: 14px;");

        VBox topBox = new VBox(10, titleLabel, infoLabel);
        topBox.setAlignment(Pos.CENTER);
        topBox.setPadding(new Insets(10));

        // Panel izquierdo (controles)
        sizeLabel = new Label("Tamaño del tablero: 8x8");

        sizeSlider = new Slider(5, 8, 8);
        sizeSlider.setShowTickLabels(true);
        sizeSlider.setShowTickMarks(true);
        sizeSlider.setMajorTickUnit(1);
        sizeSlider.setMinorTickCount(0);
        sizeSlider.setSnapToTicks(true);

        // Actualizar etiqueta al cambiar el slider
        sizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int size = newVal.intValue();
            sizeLabel.setText("Tamaño del tablero: " + size + "x" + size);
            configureSpinners(size);
        });

        // Spinners para posición inicial
        Label startPosLabel = new Label("Posición inicial:");

        Label startXLabel = new Label("X:");
        startXSpinner = new Spinner<>(0, 7, 0);
        startXSpinner.setMaxWidth(70);

        Label startYLabel = new Label("Y:");
        startYSpinner = new Spinner<>(0, 7, 0);
        startYSpinner.setMaxWidth(70);

        HBox startPosBox = new HBox(10, startXLabel, startXSpinner, startYLabel, startYSpinner);
        startPosBox.setAlignment(Pos.CENTER_LEFT);

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
        movesLabel = new Label("Movimientos: 0 / 64");
        timeLabel = new Label("Tiempo: 0s");

        Label instructionsLabel = new Label("Instrucciones:");
        instructionsLabel.setStyle("-fx-font-weight: bold;");

        Label instructionsTextLabel = new Label("Haz clic en una casilla para mover el caballo. Solo son válidos los movimientos en L.");
        instructionsTextLabel.setWrapText(true);

        VBox leftBox = new VBox(15);
        leftBox.getChildren().addAll(
                new Label("Configuración"),
                sizeLabel,
                sizeSlider,
                startPosLabel,
                startPosBox,
                initButton,
                new javafx.scene.control.Separator(),
                controlsLabel,
                solveButton,
                stepButton,
                resetButton,
                new javafx.scene.control.Separator(),
                statsLabel,
                stepsLabel,
                movesLabel,
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
        chessBoard = null;
        configureSpinners(8); // Configurar spinner con tamaño inicial
    }

    /**
     * Configura los spinners según el tamaño del tablero
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
    public void initialize(Game game, GameService gameService) {
        if (!(game instanceof KnightGame)) {
            throw new IllegalArgumentException("Se esperaba un juego de tipo KnightGame");
        }

        this.game = (KnightGame) game;
        this.gameService = gameService;
    }

    /**
     * Inicializa el juego con los parámetros seleccionados
     */
    private void initializeGame() {
        int size = (int) sizeSlider.getValue();
        int startX = startXSpinner.getValue();
        int startY = startYSpinner.getValue();

        try {
            // Inicializar juego si no existe
            if (game == null) {
                game = new KnightGame();
            }

            game.initialize(size, startX, startY);

            // Guardar posición inicial
            lastX = startX;
            lastY = startY;

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

            // Mostrar caballo y número 0 en posición inicial
            chessBoard.placeKnightAt(startX, startY);
            chessBoard.placeNumberAt(startX, startY, 0);

            // Habilitar/deshabilitar botones
            solveButton.setDisable(false);
            stepButton.setDisable(false);
            resetButton.setDisable(false);
            initButton.setDisable(true);
            sizeSlider.setDisable(true);
            startXSpinner.setDisable(true);
            startYSpinner.setDisable(true);

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
                    showMessage("¡Felicidades! Has completado el recorrido correctamente.");
                    saveGameRecord(true);
                }
            } else {
                // Movimiento inválido, mostrar feedback visual
                chessBoard.highlightCell(x, y);
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
            int boardSize = game.getBoardSize();
            movesLabel.setText("Movimientos: " + game.getTotalMoves() + " / " +
                    (boardSize * boardSize));
            timeLabel.setText("Tiempo: " + game.getElapsedTimeSeconds() + "s");
        }
    }

    @Override
    public void showStep() {
        if (game != null && chessBoard != null) {
            // Desactivar botones durante la operación
            setControlsEnabled(false);

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

            // Buscar el siguiente movimiento posible
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (board[i][j] == -1) { // Casilla no visitada
                        // Verificar si es un movimiento de caballo válido desde la posición actual
                        if (game.move(nextX, nextY, j, i)) {
                            // Actualizar tablero
                            chessBoard.placeKnightAt(j, i);
                            chessBoard.placeNumberAt(j, i, lastMove + 1);

                            // Actualizar última posición
                            lastX = j;
                            lastY = i;

                            success = true;
                            break;
                        }
                    }
                }
                if (success) break;
            }

            // Reactivar botones
            setControlsEnabled(true);

            // Actualizar vista
            updateView();

            if (!success) {
                showMessage("No se pueden realizar más pasos o no hay movimientos válidos.");
            }

            // Verificar si se completó
            if (game.isSolved()) {
                showMessage("Recorrido completado correctamente.");
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
                    // Actualizar tablero con la solución
                    if (success && chessBoard != null) {
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

                    updateView();
                    setControlsEnabled(true);

                    if (success) {
                        showMessage("Se ha encontrado un recorrido válido.");
                        saveGameRecord(true);
                    } else {
                        showMessage("No se pudo encontrar un recorrido completo con los parámetros actuales.");
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
            startXSpinner.setDisable(false);
            startYSpinner.setDisable(false);
            solveButton.setDisable(true);
            stepButton.setDisable(true);
            resetButton.setDisable(true);

            // Actualizar etiquetas
            stepsLabel.setText("Pasos: 0");
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

                if (record instanceof KnightRecord) {
                    KnightRecord knightRecord = (KnightRecord) record;

                    String message = String.format(
                            "Resultado guardado:\nTablero %dx%d\nPosición inicial: (%d,%d)\n" +
                                    "Movimientos: %d\nPasos: %d\nTiempo: %ds",
                            knightRecord.getBoardSize(), knightRecord.getBoardSize(),
                            knightRecord.getStartX(), knightRecord.getStartY(),
                            knightRecord.getTotalMoves(), knightRecord.getSteps(),
                            knightRecord.getElapsedTimeSeconds()
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
        return "Recorrido del Caballo";
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