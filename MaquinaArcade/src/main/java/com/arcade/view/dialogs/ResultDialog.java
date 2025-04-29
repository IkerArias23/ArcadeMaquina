package com.arcade.view.dialogs;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import com.arcade.model.entity.GameRecord;
import com.arcade.model.entity.HanoiRecord;
import com.arcade.model.entity.KnightRecord;
import com.arcade.model.entity.QueenRecord;
import com.arcade.service.GameFactory.GameType;

/**
 * Diálogo para mostrar los resultados detallados de una partida
 * Presenta información y estadísticas sobre el juego completado
 */
public class ResultDialog {

    private Stage dialogStage;
    private GameRecord record;
    private GameType gameType;

    /**
     * Constructor con registro y tipo de juego
     * @param record registro de la partida
     * @param gameType tipo de juego
     * @param owner ventana propietaria
     */
    public ResultDialog(GameRecord record, GameType gameType, Window owner) {
        this.record = record;
        this.gameType = gameType;
        this.dialogStage = new Stage();

        // Configurar el diálogo
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.initOwner(owner);
        dialogStage.initStyle(StageStyle.DECORATED);
        dialogStage.setResizable(true);
        dialogStage.setTitle("Resultados - " + getGameName(gameType));
        dialogStage.setMinWidth(500);
        dialogStage.setMinHeight(400);

        // Crear la interfaz del diálogo
        createDialogContent();
    }

    /**
     * Crea el contenido del diálogo
     */
    private void createDialogContent() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        // Título
        Label titleLabel = new Label("Resultados de la partida");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));

        // Contenido
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

        // Pestaña de resumen
        Tab summaryTab = new Tab("Resumen", createSummaryContent());

        // Pestaña de estadísticas
        Tab statsTab = new Tab("Estadísticas", createStatsContent());

        // Agregar pestañas
        tabPane.getTabs().addAll(summaryTab, statsTab);

        // Botón de cerrar
        Button closeButton = new Button("Cerrar");
        closeButton.setPrefWidth(100);
        closeButton.setOnAction(e -> dialogStage.close());

        HBox buttonBox = new HBox(closeButton);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        // Organizar componentes
        VBox topBox = new VBox(10, titleLabel, new Separator());

        root.setTop(topBox);
        root.setCenter(tabPane);
        root.setBottom(buttonBox);

        // Asignar escena
        Scene scene = new Scene(root, 500, 400);
        dialogStage.setScene(scene);
    }

    /**
     * Crea el contenido de la pestaña de resumen
     * @return contenido de resumen
     */
    private VBox createSummaryContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        // Información común
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);

        // Encabezado
        Label headerLabel = new Label(getGameName(gameType));
        headerLabel.setFont(Font.font("System", FontWeight.BOLD, 16));

        // Estado
        Label stateLabel = new Label("Estado:");
        Label stateValueLabel = new Label(record.isCompleted() ? "Completado" : "No completado");
        stateValueLabel.setStyle(record.isCompleted() ?
                "-fx-text-fill: green; -fx-font-weight: bold;" :
                "-fx-text-fill: red; -fx-font-weight: bold;");

        // Tiempo
        Label timeLabel = new Label("Tiempo:");
        Label timeValueLabel = new Label(record.getElapsedTimeSeconds() + " segundos");

        // Pasos
        Label stepsLabel = new Label("Pasos/Intentos:");
        Label stepsValueLabel = new Label(String.valueOf(record.getSteps()));

        // Fecha
        Label dateLabel = new Label("Fecha:");
        Label dateValueLabel = new Label(record.getEndTime().toString()
                .replace("T", " ")
                .substring(0, 19));

        // Agregar información común
        grid.add(stateLabel, 0, 0);
        grid.add(stateValueLabel, 1, 0);
        grid.add(timeLabel, 0, 1);
        grid.add(timeValueLabel, 1, 1);
        grid.add(stepsLabel, 0, 2);
        grid.add(stepsValueLabel, 1, 2);
        grid.add(dateLabel, 0, 3);
        grid.add(dateValueLabel, 1, 3);

        // Agregar información específica según el tipo de juego
        addGameSpecificInfo(grid, 4);

        content.getChildren().addAll(headerLabel, grid);

        return content;
    }

    /**
     * Añade información específica según el tipo de juego
     * @param grid cuadrícula donde añadir la información
     * @param startRow fila inicial
     */
    private void addGameSpecificInfo(GridPane grid, int startRow) {
        if (record instanceof QueenRecord) {
            QueenRecord queenRecord = (QueenRecord) record;

            Label boardSizeLabel = new Label("Tamaño del tablero:");
            Label boardSizeValueLabel = new Label(
                    queenRecord.getBoardSize() + "x" + queenRecord.getBoardSize()
            );

            grid.add(boardSizeLabel, 0, startRow);
            grid.add(boardSizeValueLabel, 1, startRow);

        } else if (record instanceof KnightRecord) {
            KnightRecord knightRecord = (KnightRecord) record;

            Label boardSizeLabel = new Label("Tamaño del tablero:");
            Label boardSizeValueLabel = new Label(
                    knightRecord.getBoardSize() + "x" + knightRecord.getBoardSize()
            );

            Label startPosLabel = new Label("Posición inicial:");
            Label startPosValueLabel = new Label(
                    "(" + knightRecord.getStartX() + "," + knightRecord.getStartY() + ")"
            );

            Label movesLabel = new Label("Movimientos:");
            Label movesValueLabel = new Label(
                    knightRecord.getTotalMoves() + " de " +
                            (knightRecord.getBoardSize() * knightRecord.getBoardSize())
            );

            grid.add(boardSizeLabel, 0, startRow);
            grid.add(boardSizeValueLabel, 1, startRow);
            grid.add(startPosLabel, 0, startRow + 1);
            grid.add(startPosValueLabel, 1, startRow + 1);
            grid.add(movesLabel, 0, startRow + 2);
            grid.add(movesValueLabel, 1, startRow + 2);

        } else if (record instanceof HanoiRecord) {
            HanoiRecord hanoiRecord = (HanoiRecord) record;

            Label disksLabel = new Label("Número de discos:");
            Label disksValueLabel = new Label(
                    String.valueOf(hanoiRecord.getNumDisks())
            );

            Label movesLabel = new Label("Movimientos:");
            Label movesValueLabel = new Label(
                    hanoiRecord.getMovements() + " de " + hanoiRecord.getMinimumMoves() +
                            " (óptimo: " + (hanoiRecord.isOptimalSolution() ? "Sí" : "No") + ")"
            );

            grid.add(disksLabel, 0, startRow);
            grid.add(disksValueLabel, 1, startRow);
            grid.add(movesLabel, 0, startRow + 1);
            grid.add(movesValueLabel, 1, startRow + 1);
        }
    }

    /**
     * Crea el contenido de la pestaña de estadísticas
     * @return contenido de estadísticas
     */
    private VBox createStatsContent() {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        // Título
        Label titleLabel = new Label("Estadísticas");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));

        // Gráfico de barras
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);

        barChart.setTitle("Comparativa");
        xAxis.setLabel("Métrica");
        yAxis.setLabel("Valor");

        // Serie de datos
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Tu partida");

        // Agregar datos según el tipo de juego
        if (record instanceof QueenRecord) {
            QueenRecord queenRecord = (QueenRecord) record;

            series.getData().add(new XYChart.Data<>("Tamaño", queenRecord.getBoardSize()));
            series.getData().add(new XYChart.Data<>("Pasos", queenRecord.getSteps()));
            series.getData().add(new XYChart.Data<>("Tiempo (s)", queenRecord.getElapsedTimeSeconds()));

        } else if (record instanceof KnightRecord) {
            KnightRecord knightRecord = (KnightRecord) record;

            series.getData().add(new XYChart.Data<>("Tamaño", knightRecord.getBoardSize()));
            series.getData().add(new XYChart.Data<>("Movimientos", knightRecord.getTotalMoves()));
            series.getData().add(new XYChart.Data<>("Pasos", knightRecord.getSteps()));
            series.getData().add(new XYChart.Data<>("Tiempo (s)", knightRecord.getElapsedTimeSeconds()));

        } else if (record instanceof HanoiRecord) {
            HanoiRecord hanoiRecord = (HanoiRecord) record;

            series.getData().add(new XYChart.Data<>("Discos", hanoiRecord.getNumDisks()));
            series.getData().add(new XYChart.Data<>("Movimientos", hanoiRecord.getMovements()));
            series.getData().add(new XYChart.Data<>("Óptimo", hanoiRecord.getMinimumMoves()));
            series.getData().add(new XYChart.Data<>("Tiempo (s)", hanoiRecord.getElapsedTimeSeconds()));
        }

        barChart.getData().add(series);

        // Texto adicional
        Label notesLabel = new Label("Notas:");
        Label notesTextLabel = new Label(getGameSpecificNotes());
        notesTextLabel.setWrapText(true);

        content.getChildren().addAll(titleLabel, barChart, notesLabel, notesTextLabel);

        return content;
    }

    /**
     * Obtiene notas específicas según el tipo de juego
     * @return texto con notas específicas
     */
    private String getGameSpecificNotes() {
        switch (gameType) {
            case QUEENS:
                return "El problema de las N Reinas consiste en colocar N reinas en un tablero de ajedrez " +
                        "de tamaño NxN de forma que ninguna reina amenace a otra. La solución óptima " +
                        "minimiza el número de pasos necesarios para encontrar una configuración válida.";

            case KNIGHT:
                return "El problema del Recorrido del Caballo consiste en encontrar una secuencia de movimientos " +
                        "de un caballo de ajedrez tal que visite cada casilla del tablero exactamente una vez. " +
                        "La dificultad aumenta con el tamaño del tablero y varía según la posición inicial.";

            case HANOI:
                return "El problema de las Torres de Hanoi consiste en mover una pila de discos desde una torre " +
                        "a otra, usando una tercera torre como auxiliar, moviendo un solo disco cada vez y sin " +
                        "colocar nunca un disco más grande sobre uno más pequeño. El número mínimo de movimientos " +
                        "necesarios es 2^n - 1, donde n es el número de discos.";

            default:
                return "";
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
     * Muestra el diálogo
     */
    public void show() {
        dialogStage.show();
    }

    /**
     * Muestra el diálogo y espera a que se cierre
     */
    public void showAndWait() {
        dialogStage.showAndWait();
    }
}