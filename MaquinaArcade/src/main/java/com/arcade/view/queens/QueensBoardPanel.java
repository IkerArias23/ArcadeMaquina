package com.arcade.view.queens;

import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.util.function.BiConsumer;

/**
 * Panel que dibuja un tablero para el problema de las N reinas
 * Permite interacción para colocar y quitar reinas
 */
public class QueensBoardPanel extends Pane {

    private int boardSize;
    private int cellSize;
    private boolean[][] queens; // true si hay una reina en la posición [fila][columna]
    private Canvas canvas;
    private Image queenImage;
    private BiConsumer<Integer, Integer> cellClickHandler;

    // Colores para el tablero
    private static final Color LIGHT_COLOR = Color.rgb(240, 217, 181);
    private static final Color DARK_COLOR = Color.rgb(181, 136, 99);
    private static final Color HIGHLIGHT_COLOR = Color.rgb(106, 168, 79, 0.7);

    /**
     * Constructor con tamaño de tablero
     * @param boardSize tamaño del tablero (NxN)
     */
    public QueensBoardPanel(int boardSize) {
        this.boardSize = boardSize;
        this.cellSize = 60; // Tamaño por defecto de cada celda
        this.queens = new boolean[boardSize][boardSize];

        // Cargar imagen de la reina
        try {
            queenImage = new Image(getClass().getResourceAsStream("/images/queen.png"));
        } catch (Exception e) {
            System.err.println("No se pudo cargar la imagen de la reina: " + e.getMessage());
        }

        initialize();
    }

    /**
     * Inicializa el panel
     */
    private void initialize() {
        int canvasSize = boardSize * cellSize;

        // Crear canvas para dibujar
        canvas = new Canvas(canvasSize, canvasSize);
        getChildren().add(canvas);

        // Configurar tamaño del panel
        setPrefSize(canvasSize, canvasSize);
        setMaxSize(canvasSize, canvasSize);
        setMinSize(canvasSize, canvasSize);

        // Configurar fondo
        setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        // Dibujar tablero vacío
        drawBoard();

        // Configurar eventos de ratón
        canvas.setOnMouseClicked(this::handleMouseClick);
    }

    /**
     * Cambia el tamaño del tablero
     * @param newSize nuevo tamaño del tablero
     */
    public void resizeBoard(int newSize) {
        this.boardSize = newSize;
        this.queens = new boolean[boardSize][boardSize];

        int canvasSize = boardSize * cellSize;
        canvas.setWidth(canvasSize);
        canvas.setHeight(canvasSize);

        setPrefSize(canvasSize, canvasSize);
        setMaxSize(canvasSize, canvasSize);
        setMinSize(canvasSize, canvasSize);

        drawBoard();
    }

    /**
     * Cambia el tamaño de las celdas
     * @param newCellSize nuevo tamaño de celda en píxeles
     */
    public void setCellSize(int newCellSize) {
        this.cellSize = newCellSize;
        resizeBoard(boardSize); // Reutilizar el método de redimensionado
    }

    /**
     * Dibuja el tablero en el canvas
     */
    private void drawBoard() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Limpiar canvas
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Dibujar celdas del tablero
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                // Alternar colores para crear el patrón de ajedrez
                Color cellColor = (row + col) % 2 == 0 ? LIGHT_COLOR : DARK_COLOR;

                // Dibujar celda
                gc.setFill(cellColor);
                gc.fillRect(col * cellSize, row * cellSize, cellSize, cellSize);

                // Dibujar reina si existe en esta posición
                if (queens[row][col]) {
                    drawQueen(gc, col, row);
                }
            }
        }
    }

    /**
     * Dibuja una reina en la posición indicada
     * @param gc contexto gráfico
     * @param col columna
     * @param row fila
     */
    private void drawQueen(GraphicsContext gc, int col, int row) {
        if (queenImage != null) {
            // Usar imagen de reina
            gc.drawImage(
                    queenImage,
                    col * cellSize + (cellSize - 50) / 2,
                    row * cellSize + (cellSize - 50) / 2,
                    50, 50
            );
        } else {
            // Alternativa: dibujar un círculo
            gc.setFill(Color.PURPLE);
            gc.fillOval(
                    col * cellSize + cellSize * 0.15,
                    row * cellSize + cellSize * 0.15,
                    cellSize * 0.7,
                    cellSize * 0.7
            );
        }
    }

    /**
     * Coloca una reina en la posición indicada
     * @param col columna
     * @param row fila
     */
    public void placeQueen(int col, int row) {
        if (isValidPosition(col, row)) {
            queens[row][col] = true;
            drawBoard(); // Redibujar tablero
        }
    }

    /**
     * Quita una reina de la posición indicada
     * @param col columna
     * @param row fila
     */
    public void removeQueen(int col, int row) {
        if (isValidPosition(col, row) && queens[row][col]) {
            queens[row][col] = false;
            drawBoard(); // Redibujar tablero
        }
    }

    /**
     * Resalta una celda temporalmente
     * @param col columna
     * @param row fila
     */
    public void highlightCell(int col, int row) {
        if (isValidPosition(col, row)) {
            GraphicsContext gc = canvas.getGraphicsContext2D();

            // Guardar color original
            Color originalColor = (col + row) % 2 == 0 ? LIGHT_COLOR : DARK_COLOR;

            // Dibujar celda resaltada
            gc.setFill(HIGHLIGHT_COLOR);
            gc.fillRect(col * cellSize, row * cellSize, cellSize, cellSize);

            // Redibujar reina si existe
            if (queens[row][col]) {
                drawQueen(gc, col, row);
            }

            // Restaurar color original después de un tiempo
            new Thread(() -> {
                try {
                    Thread.sleep(800);

                    // Volver al color original en el hilo de JavaFX
                    javafx.application.Platform.runLater(() -> {
                        gc.setFill(originalColor);
                        gc.fillRect(col * cellSize, row * cellSize, cellSize, cellSize);

                        // Redibujar reina si existe
                        if (queens[row][col]) {
                            drawQueen(gc, col, row);
                        }
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    /**
     * Limpia todas las reinas del tablero
     */
    public void clearBoard() {
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                queens[row][col] = false;
            }
        }
        drawBoard();
    }

    /**
     * Maneja el evento de clic en el tablero
     * @param event evento de ratón
     */
    private void handleMouseClick(MouseEvent event) {
        int col = (int) (event.getX() / cellSize);
        int row = (int) (event.getY() / cellSize);

        if (isValidPosition(col, row) && cellClickHandler != null) {
            cellClickHandler.accept(col, row);
        }
    }

    /**
     * Verifica si una posición es válida en el tablero
     * @param col columna
     * @param row fila
     * @return true si la posición es válida
     */
    private boolean isValidPosition(int col, int row) {
        return col >= 0 && col < boardSize && row >= 0 && row < boardSize;
    }

    /**
     * Establece el manejador de clics en celdas
     * @param handler función que recibe coordenadas (col, row)
     */
    public void setOnCellClick(BiConsumer<Integer, Integer> handler) {
        this.cellClickHandler = handler;
    }

    /**
     * Obtiene el estado actual del tablero
     * @return matriz con posiciones de reinas
     */
    public boolean[][] getQueensPositions() {
        boolean[][] result = new boolean[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++) {
            System.arraycopy(queens[i], 0, result[i], 0, boardSize);
        }
        return result;
    }

    /**
     * Verifica si hay una reina en la posición indicada
     * @param col columna
     * @param row fila
     * @return true si hay una reina
     */
    public boolean hasQueen(int col, int row) {
        return isValidPosition(col, row) && queens[row][col];
    }
}