package com.arcade.view.components;

import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.function.BiConsumer;

/**
 * Componente que implementa un tablero de ajedrez interactivo
 * Utiliza el patrón Composite para organizar las celdas del tablero
 */
public class ChessBoard extends GridPane {

    private int rows;
    private int cols;
    private StackPane[][] cells;
    private BiConsumer<Integer, Integer> cellClickHandler;

    // Colores para las celdas
    private static final Color LIGHT_COLOR = Color.rgb(240, 217, 181);
    private static final Color DARK_COLOR = Color.rgb(181, 136, 99);
    private static final Color HIGHLIGHT_COLOR = Color.rgb(106, 168, 79, 0.7);

    // Imágenes para las piezas (se podrían cargar de recursos)
    private Image queenImage;
    private Image knightImage;

    /**
     * Constructor con dimensiones
     * @param cols número de columnas
     * @param rows número de filas
     */
    public ChessBoard(int cols, int rows) {
        this.rows = rows;
        this.cols = cols;

        // Intentar cargar imágenes
        try {
            queenImage = new Image(getClass().getResourceAsStream("/images/queen.png"));
            knightImage = new Image(getClass().getResourceAsStream("/images/knight.png"));
        } catch (Exception e) {
            // Si no se pueden cargar, se usarán formas geométricas
            System.err.println("No se pudieron cargar las imágenes: " + e.getMessage());
        }

        initialize();
    }

    /**
     * Inicializa el tablero
     */
    private void initialize() {
        this.setHgap(1);
        this.setVgap(1);
        this.setPadding(new Insets(10));

        // Crear celdas
        cells = new StackPane[rows][cols];

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                StackPane cell = createCell(col, row);
                cells[row][col] = cell;
                this.add(cell, col, row);
            }
        }
    }

    /**
     * Crea una celda del tablero
     * @param col columna
     * @param row fila
     * @return celda creada
     */
    private StackPane createCell(int col, int row) {
        StackPane cell = new StackPane();

        // Determinar color según posición (patrón de ajedrez)
        Color cellColor = (col + row) % 2 == 0 ? LIGHT_COLOR : DARK_COLOR;
        cell.setBackground(new Background(new BackgroundFill(cellColor, CornerRadii.EMPTY, Insets.EMPTY)));

        // Tamaño de celda
        cell.setPrefSize(60, 60);
        cell.setMinSize(40, 40);

        // Agregar evento de clic
        final int finalCol = col;
        final int finalRow = row;
        cell.setOnMouseClicked(event -> {
            if (cellClickHandler != null) {
                cellClickHandler.accept(finalCol, finalRow);
            }
        });

        return cell;
    }

    /**
     * Cambia el tamaño del tablero
     * @param newCols nuevas columnas
     * @param newRows nuevas filas
     */
    public void resize(int newCols, int newRows) {
        this.getChildren().clear();
        this.rows = newRows;
        this.cols = newCols;
        initialize();
    }

    /**
     * Limpia todas las piezas del tablero
     */
    public void clear() {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                cells[row][col].getChildren().clear();
            }
        }
    }

    /**
     * Coloca una reina en una posición
     * @param col columna
     * @param row fila
     */
    public void placeQueenAt(int col, int row) {
        if (isValidPosition(col, row)) {
            cells[row][col].getChildren().clear();

            if (queenImage != null) {
                ImageView queenView = new ImageView(queenImage);
                queenView.setFitHeight(50);
                queenView.setFitWidth(50);
                cells[row][col].getChildren().add(queenView);
            } else {
                // Alternativa si no hay imagen
                Circle queen = new Circle(20);
                queen.setFill(Color.PURPLE);
                cells[row][col].getChildren().add(queen);
            }
        }
    }

    /**
     * Coloca un caballo en una posición
     * @param col columna
     * @param row fila
     */
    public void placeKnightAt(int col, int row) {
        if (isValidPosition(col, row)) {
            cells[row][col].getChildren().clear();

            if (knightImage != null) {
                ImageView knightView = new ImageView(knightImage);
                knightView.setFitHeight(50);
                knightView.setFitWidth(50);
                cells[row][col].getChildren().add(knightView);
            } else {
                // Alternativa si no hay imagen
                Circle knight = new Circle(20);
                knight.setFill(Color.BLUE);
                cells[row][col].getChildren().add(knight);
            }
        }
    }

    /**
     * Coloca un número en una celda (para mostrar secuencia de movimientos)
     * @param col columna
     * @param row fila
     * @param number número a mostrar
     */
    public void placeNumberAt(int col, int row, int number) {
        if (isValidPosition(col, row)) {
            cells[row][col].getChildren().clear();

            javafx.scene.text.Text text = new javafx.scene.text.Text(String.valueOf(number));
            text.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
            cells[row][col].getChildren().add(text);
        }
    }

    /**
     * Resalta una celda
     * @param col columna
     * @param row fila
     */
    public void highlightCell(int col, int row) {
        if (isValidPosition(col, row)) {
            // Guardar color original
            Color originalColor = (col + row) % 2 == 0 ? LIGHT_COLOR : DARK_COLOR;

            // Cambiar a color de resaltado
            cells[row][col].setBackground(
                    new Background(new BackgroundFill(HIGHLIGHT_COLOR, CornerRadii.EMPTY, Insets.EMPTY))
            );

            // Restaurar color original después de un tiempo
            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(
                    javafx.util.Duration.seconds(0.8)
            );
            pause.setOnFinished(event ->
                    cells[row][col].setBackground(
                            new Background(new BackgroundFill(originalColor, CornerRadii.EMPTY, Insets.EMPTY))
                    )
            );
            pause.play();
        }
    }

    /**
     * Quita una pieza de una posición
     * @param col columna
     * @param row fila
     */
    public void removeQueenAt(int col, int row) {
        if (isValidPosition(col, row)) {
            cells[row][col].getChildren().clear();
        }
    }

    /**
     * Verifica si una posición es válida
     * @param col columna
     * @param row fila
     * @return true si la posición es válida
     */
    private boolean isValidPosition(int col, int row) {
        return col >= 0 && col < cols && row >= 0 && row < rows;
    }

    /**
     * Establece el manejador de clics en celdas
     * @param handler función que recibe coordenadas (col, row)
     */
    public void setOnCellClick(BiConsumer<Integer, Integer> handler) {
        this.cellClickHandler = handler;
    }
}