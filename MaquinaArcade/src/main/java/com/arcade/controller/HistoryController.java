package com.arcade.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.arcade.model.dao.RecordRepository;
import com.arcade.model.entity.GameRecord;
import com.arcade.model.entity.HanoiRecord;
import com.arcade.model.entity.KnightRecord;
import com.arcade.model.entity.QueenRecord;
import com.arcade.service.GameFactory.GameType;
import com.arcade.service.GameService;

/**
 * Controlador para la vista de historial de partidas
 * Gestiona la visualización y filtrado de registros de juegos
 */
public class HistoryController {

    @FXML
    private Label titleLabel;

    @FXML
    private Label statsLabel;

    @FXML
    private ComboBox<String> gameTypeComboBox;

    @FXML
    private TableView<GameRecord> recordsTable;

    @FXML
    private Button refreshButton;

    @FXML
    private Button closeButton;

    @FXML
    private Button deleteButton;

    private GameService gameService;
    private RecordRepository recordRepository;
    private Stage stage;

    /**
     * Inicialización del controlador
     */
    @FXML
    public void initialize() {
        // Inicializar el combobox con los tipos de juegos
        gameTypeComboBox.setItems(FXCollections.observableArrayList(
                "Todos", "N Reinas", "Recorrido del Caballo", "Torres de Hanoi"
        ));
        gameTypeComboBox.setValue("Todos");

        // Inicializar columnas de la tabla
        initializeTable();

        // Configurar eventos
        gameTypeComboBox.setOnAction(e -> loadRecords());
        refreshButton.setOnAction(e -> loadRecords());
        closeButton.setOnAction(e -> closeWindow());
        deleteButton.setOnAction(e -> deleteSelectedRecord());

        // Inicializar el repositorio
        recordRepository = new RecordRepository();
    }

    /**
     * Inicializa las columnas de la tabla
     */
    private void initializeTable() {
        // Columna de tipo de juego
        TableColumn<GameRecord, String> gameTypeCol = new TableColumn<>("Juego");
        gameTypeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGameType()));

        // Columna de fecha/hora
        TableColumn<GameRecord, String> dateTimeCol = new TableColumn<>("Fecha/Hora");
        dateTimeCol.setCellValueFactory(data -> {
            LocalDateTime dateTime = data.getValue().getEndTime();
            if (dateTime == null) {
                return new SimpleStringProperty("(sin fecha)");
            }
            return new SimpleStringProperty(dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
        });

        // Columna de pasos
        TableColumn<GameRecord, Integer> stepsCol = new TableColumn<>("Pasos");
        stepsCol.setCellValueFactory(data ->
                new SimpleObjectProperty<>(data.getValue().getSteps()));

        // Columna de tiempo
        TableColumn<GameRecord, String> timeCol = new TableColumn<>("Tiempo");
        timeCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getElapsedTimeSeconds() + " seg"));

        // Columna de completado
        TableColumn<GameRecord, String> completedCol = new TableColumn<>("Completado");
        completedCol.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().isCompleted() ? "Sí" : "No"));

        // Columna de detalles (específicos de cada juego)
        TableColumn<GameRecord, String> detailsCol = new TableColumn<>("Detalles");
        detailsCol.setCellValueFactory(data -> {
            GameRecord record = data.getValue();
            if (record instanceof QueenRecord) {
                QueenRecord queenRecord = (QueenRecord) record;
                return new SimpleStringProperty(
                        "Tablero: " + queenRecord.getBoardSize() + "x" + queenRecord.getBoardSize()
                );
            } else if (record instanceof KnightRecord) {
                KnightRecord knightRecord = (KnightRecord) record;
                return new SimpleStringProperty(
                        "Tablero: " + knightRecord.getBoardSize() + "x" + knightRecord.getBoardSize() +
                                ", Inicio: (" + knightRecord.getStartX() + "," + knightRecord.getStartY() + ")" +
                                ", Movimientos: " + knightRecord.getTotalMoves()
                );
            } else if (record instanceof HanoiRecord) {
                HanoiRecord hanoiRecord = (HanoiRecord) record;
                return new SimpleStringProperty(
                        "Discos: " + hanoiRecord.getNumDisks() +
                                ", Movimientos: " + hanoiRecord.getMovements() + "/" + hanoiRecord.getMinimumMoves() +
                                ", Óptimo: " + (hanoiRecord.isOptimalSolution() ? "Sí" : "No")
                );
            } else {
                return new SimpleStringProperty("");
            }
        });

        // Ajustar el tamaño de las columnas proporcionalmente
        gameTypeCol.prefWidthProperty().bind(recordsTable.widthProperty().multiply(0.15));
        dateTimeCol.prefWidthProperty().bind(recordsTable.widthProperty().multiply(0.2));
        stepsCol.prefWidthProperty().bind(recordsTable.widthProperty().multiply(0.1));
        timeCol.prefWidthProperty().bind(recordsTable.widthProperty().multiply(0.1));
        completedCol.prefWidthProperty().bind(recordsTable.widthProperty().multiply(0.1));
        detailsCol.prefWidthProperty().bind(recordsTable.widthProperty().multiply(0.35));

        // Añadir columnas a la tabla
        recordsTable.getColumns().setAll(
                gameTypeCol, dateTimeCol, stepsCol, timeCol, completedCol, detailsCol
        );
    }

    /**
     * Carga los registros según el filtro seleccionado
     */
    private void loadRecords() {
        try {
            String gameTypeString = gameTypeComboBox.getValue();
            List<GameRecord> records = new ArrayList<>();

            if ("Todos".equals(gameTypeString)) {
                // Cargar registros de todos los tipos
                if (recordRepository != null) {
                    try {
                        records.addAll(recordRepository.findAllQueenRecords());
                        records.addAll(recordRepository.findAllKnightRecords());
                        records.addAll(recordRepository.findAllHanoiRecords());
                    } catch (Exception e) {
                        System.err.println("Error al cargar registros desde repositorio: " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                // Si el repositorio falla o no tiene datos, intentar con el servicio
                if (records.isEmpty() && gameService != null) {
                    for (GameType type : GameType.values()) {
                        List<GameRecord> typeRecords = gameService.getGameHistory(type);
                        if (typeRecords != null && !typeRecords.isEmpty()) {
                            records.addAll(typeRecords);
                        }
                    }
                }
            } else {
                // Cargar registros de un tipo específico
                GameType gameType = getGameTypeFromString(gameTypeString);

                if (gameType != null) {
                    if (recordRepository != null) {
                        switch (gameType) {
                            case QUEENS:
                                records.addAll(recordRepository.findAllQueenRecords());
                                break;
                            case KNIGHT:
                                records.addAll(recordRepository.findAllKnightRecords());
                                break;
                            case HANOI:
                                records.addAll(recordRepository.findAllHanoiRecords());
                                break;
                        }
                    }

                    // Si el repositorio falla o no tiene datos, intentar con el servicio
                    if (records.isEmpty() && gameService != null) {
                        List<GameRecord> typeRecords = gameService.getGameHistory(gameType);
                        if (typeRecords != null) {
                            records.addAll(typeRecords);
                        }
                    }
                }
            }

            // Ordenar por fecha (más reciente primero)
            if (!records.isEmpty()) {
                Collections.sort(records, Comparator.comparing(GameRecord::getEndTime,
                        Comparator.nullsLast(Comparator.reverseOrder())));
            }

            // Actualizar la tabla
            recordsTable.setItems(FXCollections.observableArrayList(records));
            System.out.println("Registros cargados en la tabla: " + records.size());

            // Actualizar estadísticas
            updateStats(records);

        } catch (Exception e) {
            System.err.println("Error al cargar registros: " + e.getMessage());
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Error", "Error al cargar registros", e.getMessage());
        }
    }

    /**
     * Actualiza las estadísticas mostradas
     */
    private void updateStats(List<GameRecord> records) {
        int total = records.size();
        int completed = 0;

        for (GameRecord record : records) {
            if (record.isCompleted()) {
                completed++;
            }
        }

        double percentage = total > 0 ? (completed * 100.0 / total) : 0;

        statsLabel.setText(String.format(
                "Total de partidas: %d | Completadas: %d (%.1f%%)",
                total, completed, percentage
        ));
    }

    /**
     * Elimina el registro seleccionado
     */
    private void deleteSelectedRecord() {
        GameRecord selectedRecord = recordsTable.getSelectionModel().getSelectedItem();

        if (selectedRecord == null) {
            showAlert(AlertType.WARNING, "Aviso", "Ningún registro seleccionado",
                    "Por favor, selecciona un registro para eliminar.");
            return;
        }

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Estás seguro de eliminar este registro?");
        alert.setContentText("Esta acción no se puede deshacer.");

        // Si el usuario confirma la eliminación
        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try {
                    if (recordRepository == null) {
                        recordRepository = new RecordRepository();
                    }
                    recordRepository.deleteRecord(selectedRecord.getId(), selectedRecord.getClass());
                    loadRecords(); // Recargar la lista
                    showAlert(AlertType.INFORMATION, "Información", "Registro eliminado",
                            "El registro ha sido eliminado correctamente.");
                } catch (Exception e) {
                    showAlert(AlertType.ERROR, "Error", "Error al eliminar", e.getMessage());
                }
            }
        });
    }

    /**
     * Cierra la ventana
     */
    private void closeWindow() {
        if (stage != null) {
            stage.close();
        }
    }

    /**
     * Establece el servicio de juegos
     * @param gameService servicio de juegos
     */
    public void setGameService(GameService gameService) {
        this.gameService = gameService;
        loadRecords();
    }

    /**
     * Establece el escenario para poder cerrarlo
     * @param stage escenario
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Convierte una cadena de texto al tipo de juego correspondiente
     * @param gameTypeString texto que representa el tipo de juego
     * @return el tipo de juego (enum) o null para "Todos"
     */
    private GameType getGameTypeFromString(String gameTypeString) {
        switch (gameTypeString) {
            case "N Reinas":
                return GameType.QUEENS;
            case "Recorrido del Caballo":
                return GameType.KNIGHT;
            case "Torres de Hanoi":
                return GameType.HANOI;
            default:
                return null;
        }
    }

    /**
     * Muestra una alerta
     * @param type tipo de alerta
     * @param title título
     * @param header encabezado
     * @param content contenido
     */
    private void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}