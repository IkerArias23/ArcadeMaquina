package com.arcade.view.dialogs;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import com.arcade.service.GameFactory.GameType;
import com.arcade.util.Constants;

/**
 * Diálogo para configurar parámetros de juego
 * Permite al usuario establecer configuraciones antes de iniciar un juego
 */
public class GameConfigDialog {

    private Stage dialogStage;
    private GameType gameType;
    private Object[] configValues;
    private boolean configApplied = false;

    // Controles específicos para cada tipo de juego
    private Slider sizeSlider; // Para tableros (N Reinas, Recorrido del Caballo)
    private Spinner<Integer> startXSpinner; // Para Recorrido del Caballo
    private Spinner<Integer> startYSpinner; // Para Recorrido del Caballo
    private Slider disksSlider; // Para Torres de Hanoi

    /**
     * Constructor con tipo de juego
     * @param gameType tipo de juego a configurar
     * @param owner ventana propietaria
     */
    public GameConfigDialog(GameType gameType, Window owner) {
        this.gameType = gameType;
        this.dialogStage = new Stage();

        // Configurar el diálogo
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(owner);
        dialogStage.initStyle(StageStyle.UTILITY);
        dialogStage.setResizable(false);
        dialogStage.setTitle("Configuración - " + getGameName(gameType));

        // Crear la interfaz del diálogo
        createDialogContent();
    }

    /**
     * Crea el contenido del diálogo según el tipo de juego
     */
    private void createDialogContent() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        // Título
        Label titleLabel = new Label("Configuración para " + getGameName(gameType));
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Contenido específico según el tipo de juego
        VBox content = createGameSpecificContent();

        // Botones
        Button applyButton = new Button("Aplicar");
        Button cancelButton = new Button("Cancelar");

        HBox buttonBox = new HBox(10, applyButton, cancelButton);
        buttonBox.setAlignment(Pos.CENTER);

        // Eventos de botones
        applyButton.setOnAction(e -> {
            saveConfiguration();
            configApplied = true;
            dialogStage.close();
        });

        cancelButton.setOnAction(e -> {
            configApplied = false;
            dialogStage.close();
        });

        // Agregar todo al contenedor principal
        root.getChildren().addAll(titleLabel, content, buttonBox);

        // Crear la escena
        Scene scene = new Scene(root);
        dialogStage.setScene(scene);
    }

    /**
     * Crea el contenido específico según el tipo de juego
     * @return panel con los controles específicos
     */
    private VBox createGameSpecificContent() {
        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER);

        switch (gameType) {
            case QUEENS:
                createQueensContent(content);
                break;
            case KNIGHT:
                createKnightContent(content);
                break;
            case HANOI:
                createHanoiContent(content);
                break;
        }

        return content;
    }

    /**
     * Crea el contenido para configurar N Reinas
     * @param content panel donde agregar los controles
     */
    private void createQueensContent(VBox content) {
        Label sizeLabel = new Label("Tamaño del tablero: " + Constants.DEFAULT_QUEENS_SIZE);

        sizeSlider = new Slider(Constants.MIN_QUEENS_SIZE, Constants.MAX_QUEENS_SIZE, Constants.DEFAULT_QUEENS_SIZE);
        sizeSlider.setShowTickLabels(true);
        sizeSlider.setShowTickMarks(true);
        sizeSlider.setMajorTickUnit(1);
        sizeSlider.setMinorTickCount(0);
        sizeSlider.setSnapToTicks(true);

        // Actualizar etiqueta al cambiar el slider
        sizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            sizeLabel.setText("Tamaño del tablero: " + newVal.intValue());
        });

        content.getChildren().addAll(
                new Label("Configuración para N Reinas:"),
                sizeLabel,
                sizeSlider
        );
    }

    /**
     * Crea el contenido para configurar Recorrido del Caballo
     * @param content panel donde agregar los controles
     */
    private void createKnightContent(VBox content) {
        Label sizeLabel = new Label("Tamaño del tablero: " + Constants.DEFAULT_KNIGHT_SIZE);

        sizeSlider = new Slider(Constants.MIN_KNIGHT_SIZE, Constants.MAX_KNIGHT_SIZE, Constants.DEFAULT_KNIGHT_SIZE);
        sizeSlider.setShowTickLabels(true);
        sizeSlider.setShowTickMarks(true);
        sizeSlider.setMajorTickUnit(1);
        sizeSlider.setMinorTickCount(0);
        sizeSlider.setSnapToTicks(true);

        // Actualizar etiqueta al cambiar el slider
        sizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int size = newVal.intValue();
            sizeLabel.setText("Tamaño del tablero: " + size);
            configureSpinners(size);
        });

        // Spinners para posición inicial
        Label posLabel = new Label("Posición inicial:");

        startXSpinner = new Spinner<>(0, Constants.DEFAULT_KNIGHT_SIZE - 1, 0);
        startYSpinner = new Spinner<>(0, Constants.DEFAULT_KNIGHT_SIZE - 1, 0);

        startXSpinner.setMaxWidth(70);
        startYSpinner.setMaxWidth(70);

        GridPane posGrid = new GridPane();
        posGrid.setHgap(10);
        posGrid.add(new Label("X:"), 0, 0);
        posGrid.add(startXSpinner, 1, 0);
        posGrid.add(new Label("Y:"), 2, 0);
        posGrid.add(startYSpinner, 3, 0);
        posGrid.setAlignment(Pos.CENTER);

        content.getChildren().addAll(
                new Label("Configuración para Recorrido del Caballo:"),
                sizeLabel,
                sizeSlider,
                posLabel,
                posGrid
        );
    }

    /**
     * Configura los spinners según el tamaño del tablero
     * @param size tamaño del tablero
     */
    private void configureSpinners(int size) {
        if (startXSpinner != null && startYSpinner != null) {
            SpinnerValueFactory<Integer> xValueFactory =
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(0, size - 1, 0);
            SpinnerValueFactory<Integer> yValueFactory =
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(0, size - 1, 0);

            startXSpinner.setValueFactory(xValueFactory);
            startYSpinner.setValueFactory(yValueFactory);
        }
    }

    /**
     * Crea el contenido para configurar Torres de Hanoi
     * @param content panel donde agregar los controles
     */
    private void createHanoiContent(VBox content) {
        Label disksLabel = new Label("Número de discos: " + Constants.DEFAULT_HANOI_DISKS);

        disksSlider = new Slider(Constants.MIN_HANOI_DISKS, Constants.MAX_HANOI_DISKS, Constants.DEFAULT_HANOI_DISKS);
        disksSlider.setShowTickLabels(true);
        disksSlider.setShowTickMarks(true);
        disksSlider.setMajorTickUnit(1);
        disksSlider.setMinorTickCount(0);
        disksSlider.setSnapToTicks(true);

        // Actualizar etiqueta al cambiar el slider
        disksSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            disksLabel.setText("Número de discos: " + newVal.intValue());
        });

        // Mostrar movimientos óptimos
        Label optimalLabel = new Label("Movimientos óptimos: " + Constants.calculateMinimumHanoiMoves(Constants.DEFAULT_HANOI_DISKS));

        // Actualizar movimientos óptimos al cambiar el slider
        disksSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int disks = newVal.intValue();
            disksLabel.setText("Número de discos: " + disks);
            optimalLabel.setText("Movimientos óptimos: " + Constants.calculateMinimumHanoiMoves(disks));
        });

        content.getChildren().addAll(
                new Label("Configuración para Torres de Hanoi:"),
                disksLabel,
                disksSlider,
                optimalLabel
        );
    }

    /**
     * Guarda la configuración actual
     */
    private void saveConfiguration() {
        switch (gameType) {
            case QUEENS:
                configValues = new Object[] { (int) sizeSlider.getValue() };
                break;
            case KNIGHT:
                configValues = new Object[] {
                        (int) sizeSlider.getValue(),
                        startXSpinner.getValue(),
                        startYSpinner.getValue()
                };
                break;
            case HANOI:
                configValues = new Object[] { (int) disksSlider.getValue() };
                break;
        }
    }

    /**
     * Obtiene el nombre del juego según su tipo
     * @param type tipo de juego
     * @return nombre del juego
     */
    private String getGameName(GameType type) {
        switch (type) {
            case QUEENS: return "N Reinas";
            case KNIGHT: return "Recorrido del Caballo";
            case HANOI: return "Torres de Hanoi";
            default: return "Juego";
        }
    }

    /**
     * Muestra el diálogo y espera a que se cierre
     * @return true si se aplicó la configuración
     */
    public boolean showAndWait() {
        dialogStage.showAndWait();
        return configApplied;
    }

    /**
     * Obtiene los valores de configuración
     * @return array con los valores de configuración
     */
    public Object[] getConfigValues() {
        return configValues;
    }
}