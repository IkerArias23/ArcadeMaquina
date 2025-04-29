package com.arcade.view.knight;

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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.function.BiConsumer;

/**
 * Panel que dibuja un tablero para el problema del Recorrido del Caballo
 * Permite interacción para mover el caballo
 */
public class KnightBoardPanel extends Pane {

    private int boardSize;
    private int cellSize;
    private int[][] board; // Almacena el orden de visita de las casillas (-1 si no visitada)
    private Canvas canvas;
    private Image knightImage;
    private BiConsumer<Integer, Integer> cellClickHandler;

    // Posición actual del caballo
    private int knightX;
    private int knightY;

    // Colores para el tablero
    private static final Color LIGHT_COLOR = Color.rgb(240, 217, 181);
    private static final Color DARK_COLOR = Color.rgb(181, 136, 99);
    private static final Color HIGHLIGHT_COLOR = Color.rgb(106, 168, 79, 0.7);
    private static final Color VALID_MOVE_COLOR = Color.rgb(106, 168, 79, 0.4);
    private static final Color INVALID_MOVE_COLOR = Color.rgb(217, 83, 79, 0.4);
    private static final Color PATH_COLOR = Color.rgb(66, 133, 244, 0.7);

    /**
     * Constructor con tamaño de tablero y posición inicial
     * @param boardSize tamaño del tablero (NxN)
     * @param startX posición inicial X del caballo
     * @param startY posición inicial Y del caballo
     */
    public KnightBoardPanel(int boardSize, int startX, int startY) {
        this.boardSize = boardSize;
        this.cellSize = 60; // Tamaño por defecto de cada celda
        this.board = new int[boardSize][boardSize];
        this.knightX = startX;
        this.knightY = startY;

        // Inicializar tablero (todas las casillas no visitadas)
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                board[i][j] = -1;
            }
        }

        // Marcar posición inicial
        board[startY][startX] = 0;

        // Cargar imagen del caballo
        try {
            knightImage = new Image(getClass().getResourceAsStream("/images/knight.png"));
        } catch (Exception e) {
            System.err.println("No se pudo cargar la imagen del caballo: " + e.getMessage());
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

        // Dibujar tablero
        drawBoard();

        // Configurar eventos de ratón
        canvas.setOnMouseClicked(this::handleMouseClick);
        canvas.setOnMouseMoved(this::handleMouseMove);
    }

    /**
     * Cambia el tamaño del tablero
     * @param newSize nuevo tamaño del tablero
     */
    public void resizeBoard(int newSize) {
        this.boardSize = newSize;
        this.board = new int[boardSize][boardSize];

        // Reiniciar tablero
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                board[i][j] = -1;
            }
        }

        // Marcar posición inicial
        if (knightX < boardSize && knightY < boardSize) {
            board[knightY][knightX] = 0;
        } else {
            // Ajustar posición del caballo si queda fuera del nuevo tablero
            knightX = 0;
            knightY = 0;
            board[0][0] = 0;
        }

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

                // Dibujar número de movimiento si la casilla ha sido visitada
                if (board[row][col] >= 0) {
                    drawMoveNumber(gc, col, row, board[row][col]);
                }
            }
        }

        // Dibujar el caballo en su posición actual
        drawKnight(gc, knightX, knightY);
    }

    /**
     * Dibuja un caballo en la posición indicada
     * @param gc contexto gráfico
     * @param col columna
     * @param row fila
     */
    private void drawKnight(GraphicsContext gc, int col, int row) {
        if (knightImage != null) {
            // Usar imagen de caballo
            gc.drawImage(
                    knightImage,
                    col * cellSize + (cellSize - 50) / 2,
                    row * cellSize + (cellSize - 50) / 2,
                    50, 50
            );
        } else {
            // Alternativa: dibujar un círculo
            gc.setFill(Color.BLUE);
            gc.fillOval(
                    col * cellSize + cellSize * 0.15,
                    row * cellSize + cellSize * 0.15,
                    cellSize * 0.7,
                    cellSize * 0.7
            );
        }
    }

    /**
     * Dibuja el número de movimiento en una casilla
     * @param gc contexto gráfico
     * @param col columna
     * @param row fila
     * @param moveNumber número de movimiento
     */
    private void drawMoveNumber(GraphicsContext gc, int col, int row, int moveNumber) {
        // Configurar texto
        gc.setFill(Color.BLACK);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        gc.setTextAlign(TextAlignment.CENTER);

        // Dibujar número
        gc.fillText(
                String.valueOf(moveNumber),
                col * cellSize + cellSize / 2,
                row * cellSize + cellSize / 2 + 5 // +5 para centrar verticalmente
        );
    }

    /**
     * Dibuja una línea que muestra la trayectoria del caballo
     */
    public void drawPath() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Dibujar líneas conectando los movimientos
        gc.setStroke(PATH_COLOR);
        gc.setLineWidth(3);

        // Encontrar la secuencia de movimientos
        int maxMove = -1;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (board[i][j] > maxMove) {
                    maxMove = board[i][j];
                }
            }
        }

        // Conectar los puntos en orden
        if (maxMove > 0) {
            for (int move = 0; move < maxMove; move++) {
                // Encontrar las coordenadas de movimiento actual y siguiente
                int x1 = -1, y1 = -1, x2 = -1, y2 = -1;

                for (int i = 0; i < boardSize; i++) {
                    for (int j = 0; j < boardSize; j++) {
                        if (board[i][j] == move) {
                            x1 = j;
                            y1 = i;
                        } else if (board[i][j] == move + 1) {
                            x2 = j;
                            y2 = i;
                        }
                    }
                }

                // Dibujar línea entre los dos puntos
                if (x1 != -1 && y1 != -1 && x2 != -1 && y2 != -1) {
                    gc.beginPath();
                    gc.moveTo(x1 * cellSize + cellSize / 2, y1 * cellSize + cellSize / 2);
                    gc.lineTo(x2 * cellSize + cellSize / 2, y2 * cellSize + cellSize / 2);
                    gc.stroke();
                }
            }
        }

        // Redibujar números y caballo encima de las líneas
        for (int row = 0; row < boardSize; row++) {
            for (int col = 0; col < boardSize; col++) {
                if (board[row][col] >= 0) {
                    drawMoveNumber(gc, col, row, board[row][col]);
                }
            }
        }

        drawKnight(gc, knightX, knightY);
    }

    /**
     * Mueve el caballo a una nueva posición
     * @param col columna destino
     * @param row fila destino
     * @return true si el movimiento es válido
     */
    public boolean moveKnight(int col, int row) {
        if (!isValidKnightMove(knightX, knightY, col, row) || board[row][col] >= 0) {
            return false; // Movimiento inválido o casilla ya visitada
        }

        // Determinar el número de este movimiento
        int moveNumber = 0;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (board[i][j] >= 0 && board[i][j] > moveNumber) {
                    moveNumber = board[i][j];
                }
            }
        }
        moveNumber++;

        // Realizar el movimiento
        board[row][col] = moveNumber;
        knightX = col;
        knightY = row;

        // Redibujar tablero
        drawBoard();
        return true;
    }

    /**
     * Verifica si un movimiento de caballo es válido
     * @param fromX coordenada X origen
     * @param fromY coordenada Y origen
     * @param toX coordenada X destino
     * @param toY coordenada Y destino
     * @return true si el movimiento es válido
     */
    private boolean isValidKnightMove(int fromX, int fromY, int toX, int toY) {
        // Verificar que las coordenadas están dentro del tablero
        if (toX < 0 || toX >= boardSize || toY < 0 || toY >= boardSize) {
            return false;
        }

        // Verificar que es un movimiento de caballo (en forma de L)
        int dx = Math.abs(toX - fromX);
        int dy = Math.abs(toY - fromY);

        return (dx == 1 && dy == 2) || (dx == 2 && dy == 1);
    }

    /**
     * Resalta las casillas a las que el caballo puede moverse
     */
    public void highlightValidMoves() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Redibuja el tablero para quitar resaltados anteriores
        drawBoard();

        // Posibles movimientos del caballo (8 direcciones)
        int[] dx = {2, 1, -1, -2, -2, -1, 1, 2};
        int[] dy = {1, 2, 2, 1, -1, -2, -2, -1};

        // Resaltar casillas válidas
        for (int i = 0; i < 8; i++) {
            int newX = knightX + dx[i];
            int newY = knightY + dy[i];

            if (newX >= 0 && newX < boardSize && newY >= 0 && newY < boardSize && board[newY][newX] == -1) {
                // Casilla válida
                gc.setFill(VALID_MOVE_COLOR);
                gc.fillRect(newX * cellSize, newY * cellSize, cellSize, cellSize);
            }
        }

        // Volver a dibujar el caballo
        drawKnight(gc, knightX, knightY);
    }

    /**
     * Resalta una celda temporalmente (para mostrar movimientos inválidos)
     * @param col columna
     * @param row fila
     */
    public void highlightCell(int col, int row) {
        if (isValidPosition(col, row)) {
            GraphicsContext gc = canvas.getGraphicsContext2D();

            // Guardar color original
            Color originalColor = (col + row) % 2 == 0 ? LIGHT_COLOR : DARK_COLOR;

            // Dibujar celda resaltada
            gc.setFill(INVALID_MOVE_COLOR);
            gc.fillRect(col * cellSize, row * cellSize, cellSize, cellSize);

            // Redibujar número si existe
            if (board[row][col] >= 0) {
                drawMoveNumber(gc, col, row, board[row][col]);
            }

            // Restaurar color original después de un tiempo
            new Thread(() -> {
                try {
                    Thread.sleep(800);

                    // Volver al color original en el hilo de JavaFX
                    javafx.application.Platform.runLater(() -> {
                        gc.setFill(originalColor);
                        gc.fillRect(col * cellSize, row * cellSize, cellSize, cellSize);

                        // Redibujar número si existe
                        if (board[row][col] >= 0) {
                            drawMoveNumber(gc, col, row, board[row][col]);
                        }
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    /**
     * Limpia el tablero volviendo al estado inicial
     * @param startX posición inicial X
     * @param startY posición inicial Y
     */
    public void clearBoard(int startX, int startY) {
        this.knightX = startX;
        this.knightY = startY;

        // Reiniciar tablero
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                board[i][j] = -1;
            }
        }

        // Marcar posición inicial
        board[startY][startX] = 0;

        drawBoard();
    }

    /**
     * Actualiza el tablero con un nuevo estado
     * @param newBoard nueva matriz de estado
     */
    public void updateBoard(int[][] newBoard) {
        if (newBoard.length != boardSize || newBoard[0].length != boardSize) {
            throw new IllegalArgumentException("Dimensiones del tablero incorrectas");
        }

        this.board = new int[boardSize][boardSize];

        // Copiar estado y encontrar posición del caballo (último movimiento)
        int lastMove = -1;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                board[i][j] = newBoard[i][j];

                if (board[i][j] > lastMove) {
                    lastMove = board[i][j];
                    knightX = j;
                    knightY = i;
                }
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
     * Maneja el evento de movimiento del ratón
     * @param event evento de ratón
     */
    private void handleMouseMove(MouseEvent event) {
        if (!isAutoMode()) {
            int col = (int) (event.getX() / cellSize);
            int row = (int) (event.getY() / cellSize);

            // Resaltar casillas válidas cuando el ratón se mueve
            if (isValidPosition(col, row)) {
                highlightValidMoves();
            }
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
     * @return matriz con el estado del tablero
     */
    public int[][] getBoard() {
        int[][] result = new int[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++) {
            System.arraycopy(board[i], 0, result[i], 0, boardSize);
        }
        return result;
    }

    /**
     * Obtiene la posición X actual del caballo
     * @return coordenada X
     */
    public int getKnightX() {
        return knightX;
    }

    /**
     * Obtiene la posición Y actual del caballo
     * @return coordenada Y
     */
    public int getKnightY() {
        return knightY;
    }

    /**
     * Verifica si el recorrido está completo (todas las casillas visitadas)
     * @return true si el recorrido está completo
     */
    public boolean isComplete() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (board[i][j] == -1) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Verifica si está en modo automático
     * @return true si está en modo automático
     */
    private boolean isAutoMode() {
        // Esta implementación siempre está en modo manual
        // Se podría extender para soportar un modo automático
        return false;
    }
}