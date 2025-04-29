package com.arcade.view.components;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.arcade.model.entity.GameRecord;
import com.arcade.model.entity.HanoiRecord;
import com.arcade.model.entity.KnightRecord;
import com.arcade.model.entity.QueenRecord;
import com.arcade.service.GameFactory.GameType;
import com.arcade.service.GameService;

/**
 * Panel que muestra el historial de partidas guardadas
 * Permite filtrar por tipo de juego
 */
public class GameHistoryPanel extends BorderPane {

    private final GameService gameService;

    // Componentes de la interfaz
    private Label titleLabel;
    private ComboBox<String> gameTypeComboBox;
    private TableView<GameRecord> recordsTable;
    private Button refreshButton;
    private Label statsLabel;

    /**
     * Constructor con servicio de juegos
     * @param gameService servicio para acceder a los registros
     */
    public GameHistoryPanel(GameService gameService) {
        this.gameService = gameService;
        createUI();
        loadRecords(null); // Cargar todos los registros inicialmente
    }

    /**
     * Crea los elementos de la interfaz
     */
    private void createUI() {
        // Panel superior (título)
        titleLabel = new Label("Historial de Partidas");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));

        // Filtro por tipo de juego
        Label filterLabel = new Label("Filtrar por juego:");

        gameTypeComboBox = new ComboBox<>();
        gameTypeComboBox.setItems(FXCollections.observableArrayList(
                "Todos", "N Reinas", "Recorrido del Caballo", "Torres de Hanoi"
        ));
        gameTypeComboBox.setValue("Todos");

        // Botón de actualizar
        refreshButton = new Button("Actualizar");

        HBox filterBox = new HBox(10, filterLabel, gameTypeComboBox, refreshButton);
        filterBox.setAlignment(Pos.CENTER_LEFT);

        VBox topBox = new VBox(10, titleLabel, filterBox);
        topBox.setPadding(new Insets(10));

        // Tabla de registros
        recordsTable = new TableView<>();
        recordsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Columnas comunes con PropertyValueFactory
        TableColumn<GameRecord, String> gameTypeCol = new TableColumn<>("Juego");
        gameTypeCol.setCellValueFactory(new PropertyValueFactory<>("gameType"));

        TableColumn<GameRecord, Integer> stepsCol = new TableColumn<>("Pasos");
        stepsCol.setCellValueFactory(new PropertyValueFactory<>("steps"));

        TableColumn<GameRecord, Boolean> completedCol = new TableColumn<>("Completado");
        completedCol.setCellValueFactory(new PropertyValueFactory<>("completed"));
        // Personalizar celda para mostrar Sí/No
        completedCol.setCellFactory(new Callback<>() {
            @Override
            public TableCell<GameRecord, Boolean> call(TableColumn<GameRecord, Boolean> param) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(Boolean item, boolean empty) {
                        super.updateItem(item, empty);
                        setText((empty || item == null) ? null : item ? "Sí" : "No");
                    }
                };
            }
        });

        TableColumn<GameRecord, Long> timeCol = new TableColumn<>("Tiempo (s)");
        timeCol.setCellValueFactory(new PropertyValueFactory<>("elapsedTimeSeconds"));

        TableColumn<GameRecord, LocalDateTime> dateCol = new TableColumn<>("Fecha");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        // Personalizar celda para formatear fecha
        dateCol.setCellFactory(col -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            @Override
            protected void updateItem(LocalDateTime date, boolean empty) {
                super.updateItem(date, empty);
                setText((empty || date == null) ? null : formatter.format(date));
            }
        });

        // Columna específica según el tipo de juego (se deja con lambda)
        TableColumn<GameRecord, String> specificCol = new TableColumn<>("Detalle");
        specificCol.setCellValueFactory(cellData -> {
            GameRecord record = cellData.getValue();
            if (record instanceof QueenRecord) {
                QueenRecord qr = (QueenRecord) record;
                return new SimpleStringProperty(
                        "Tablero " + qr.getBoardSize() + "x" + qr.getBoardSize()
                );
            } else if (record instanceof KnightRecord) {
                KnightRecord kr = (KnightRecord) record;
                return new SimpleStringProperty(
                        "Tablero " + kr.getBoardSize() + "x" + kr.getBoardSize() +
                                ", Inicio (" + kr.getStartX() + "," + kr.getStartY() + ")" +
                                ", Movimientos: " + kr.getTotalMoves()
                );
            } else if (record instanceof HanoiRecord) {
                HanoiRecord hr = (HanoiRecord) record;
                return new SimpleStringProperty(
                        "Discos: " + hr.getNumDisks() +
                                ", Movimientos: " + hr.getMovements() +
                                ", Óptimo: " + (hr.isOptimalSolution() ? "Sí" : "No")
                );
            } else {
                return new SimpleStringProperty("");
            }
        });

        // Agregar columnas a la tabla
        recordsTable.getColumns().addAll(
                gameTypeCol, specificCol, stepsCol, timeCol, completedCol, dateCol
        );

        // Estadísticas
        statsLabel = new Label("Total de partidas: 0 | Completadas: 0");

        // Panel principal
        VBox centerBox = new VBox(10, recordsTable, statsLabel);
        centerBox.setPadding(new Insets(10));

        // Configurar eventos
        gameTypeComboBox.setOnAction(e ->
                loadRecords(getGameTypeFromString(gameTypeComboBox.getValue()))
        );
        refreshButton.setOnAction(e ->
                loadRecords(getGameTypeFromString(gameTypeComboBox.getValue()))
        );

        // Asignar paneles
        setTop(topBox);
        setCenter(centerBox);
    }

    /**
     * Carga los registros en la tabla
     * @param gameType tipo de juego a filtrar (null para todos)
     */
    private void loadRecords(GameType gameType) {
        try {
            List<GameRecord> records;
            if (gameType == null) {
                ObservableList<GameRecord> all = FXCollections.observableArrayList();
                for (GameType t : GameType.values()) {
                    all.addAll(gameService.getGameHistory(t));
                }
                records = all;
            } else {
                records = gameService.getGameHistory(gameType);
            }
            recordsTable.setItems(FXCollections.observableArrayList(records));
            updateStats();
        } catch (Exception e) {
            statsLabel.setText("Error al cargar registros: " + e.getMessage());
        }
    }

    /** Actualiza las estadísticas mostradas */
    private void updateStats() {
        int total = gameService.getTotalGamesPlayed();
        int done  = gameService.getCompletedGamesCount();
        statsLabel.setText(String.format(
                "Total de partidas: %d | Completadas: %d (%.1f%%)",
                total, done, total>0 ? (done*100.0/total) : 0.0
        ));
    }

    /**
     * Convierte una cadena con el nombre del juego al enum GameType
     * @param s nombre del juego
     * @return tipo de juego o null para "Todos"
     */
    private GameType getGameTypeFromString(String s) {
        switch (s) {
            case "N Reinas":             return GameType.QUEENS;
            case "Recorrido del Caballo": return GameType.KNIGHT;
            case "Torres de Hanoi":       return GameType.HANOI;
            default:                      return null;
        }
    }
}
