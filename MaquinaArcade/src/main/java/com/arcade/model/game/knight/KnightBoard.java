package com.arcade.model.game.knight;

/**
 * Clase que representa un tablero para el problema del Recorrido del Caballo
 * Proporciona métodos para manipular y verificar el estado del tablero
 */
public class KnightBoard {

    private final int size;
    private int[][] board; // Almacena el orden de visita de las casillas (-1 si no visitada)
    private int knightX; // Posición actual X del caballo
    private int knightY; // Posición actual Y del caballo
    private int moveCount; // Contador de movimientos

    // Posibles movimientos del caballo (8 direcciones)
    private static final int[] X_MOVES = {2, 1, -1, -2, -2, -1, 1, 2};
    private static final int[] Y_MOVES = {1, 2, 2, 1, -1, -2, -2, -1};

    /**
     * Constructor con tamaño y posición inicial
     * @param size tamaño del tablero
     * @param startX posición inicial X
     * @param startY posición inicial Y
     */
    public KnightBoard(int size, int startX, int startY) {
        if (size < 5) {
            throw new IllegalArgumentException("El tamaño del tablero debe ser al menos 5");
        }

        if (!isValidPosition(startX, startY, size)) {
            throw new IllegalArgumentException("Posición inicial fuera del tablero");
        }

        this.size = size;
        this.board = new int[size][size];
        this.knightX = startX;
        this.knightY = startY;
        this.moveCount = 0;

        // Inicializar tablero (casillas no visitadas)
        clear();

        // Marcar posición inicial
        board[startY][startX] = moveCount;
        moveCount++;
    }

    /**
     * Limpia el tablero y reinicia el caballo a la posición inicial
     */
    public void clear() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                board[i][j] = -1;
            }
        }

        moveCount = 0;
        board[knightY][knightX] = moveCount;
        moveCount++;
    }

    /**
     * Verifica si una posición está dentro del tablero
     * @param x coordenada X
     * @param y coordenada Y
     * @param boardSize tamaño del tablero
     * @return true si la posición es válida
     */
    public static boolean isValidPosition(int x, int y, int boardSize) {
        return x >= 0 && x < boardSize && y >= 0 && y < boardSize;
    }

    /**
     * Verifica si una posición está dentro del tablero
     * @param x coordenada X
     * @param y coordenada Y
     * @return true si la posición es válida
     */
    public boolean isValidPosition(int x, int y) {
        return isValidPosition(x, y, size);
    }

    /**
     * Verifica si una casilla ha sido visitada
     * @param x coordenada X
     * @param y coordenada Y
     * @return true si la casilla ha sido visitada
     */
    public boolean isVisited(int x, int y) {
        return isValidPosition(x, y) && board[y][x] != -1;
    }

    /**
     * Obtiene el valor de la casilla (orden de visita)
     * @param x coordenada X
     * @param y coordenada Y
     * @return orden de visita o -1 si no ha sido visitada
     */
    public int getCellValue(int x, int y) {
        return isValidPosition(x, y) ? board[y][x] : -1;
    }

    /**
     * Verifica si un movimiento de caballo es válido
     * @param fromX coordenada X origen
     * @param fromY coordenada Y origen
     * @param toX coordenada X destino
     * @param toY coordenada Y destino
     * @return true si el movimiento es válido
     */
    public boolean isValidMove(int fromX, int fromY, int toX, int toY) {
        // Verificar que las coordenadas están dentro del tablero
        if (!isValidPosition(fromX, fromY) || !isValidPosition(toX, toY)) {
            return false;
        }

        // Verificar que la casilla destino no ha sido visitada
        if (isVisited(toX, toY)) {
            return false;
        }

        // Verificar que es un movimiento de caballo (en forma de L)
        int dx = Math.abs(toX - fromX);
        int dy = Math.abs(toY - fromY);

        return (dx == 1 && dy == 2) || (dx == 2 && dy == 1);
    }

    /**
     * Mueve el caballo a una nueva posición
     * @param toX coordenada X destino
     * @param toY coordenada Y destino
     * @return true si el movimiento se realizó correctamente
     */
    public boolean moveKnight(int toX, int toY) {
        // Verificar si el movimiento es válido
        if (!isValidMove(knightX, knightY, toX, toY)) {
            return false;
        }

        // Realizar el movimiento
        knightX = toX;
        knightY = toY;
        board[toY][toX] = moveCount;
        moveCount++;

        return true;
    }

    /**
     * Obtiene una lista de movimientos posibles desde la posición actual
     * @return array con las coordenadas [x,y] de los movimientos válidos
     */
    public int[][] getPossibleMoves() {
        int validCount = 0;

        // Contar movimientos válidos
        for (int i = 0; i < 8; i++) {
            int nextX = knightX + X_MOVES[i];
            int nextY = knightY + Y_MOVES[i];

            if (isValidPosition(nextX, nextY) && !isVisited(nextX, nextY)) {
                validCount++;
            }
        }

        // Crear array con los movimientos válidos
        int[][] validMoves = new int[validCount][2];
        int index = 0;

        for (int i = 0; i < 8; i++) {
            int nextX = knightX + X_MOVES[i];
            int nextY = knightY + Y_MOVES[i];

            if (isValidPosition(nextX, nextY) && !isVisited(nextX, nextY)) {
                validMoves[index][0] = nextX;
                validMoves[index][1] = nextY;
                index++;
            }
        }

        return validMoves;
    }

    /**
     * Verifica si el recorrido está completo (todas las casillas visitadas)
     * @return true si el recorrido está completo
     */
    public boolean isComplete() {
        return moveCount == size * size;
    }

    /**
     * Verifica si el recorrido es válido (cada par de movimientos consecutivos
     * corresponde a un movimiento de caballo)
     * @return true si el recorrido es válido
     */
    public boolean isValidTour() {
        if (moveCount <= 1) {
            return true; // No hay movimientos suficientes para verificar
        }

        // Verificar cada par de movimientos consecutivos
        for (int move = 0; move < moveCount - 1; move++) {
            int x1 = -1, y1 = -1, x2 = -1, y2 = -1;

            // Encontrar las posiciones de los movimientos consecutivos
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < size; j++) {
                    if (board[i][j] == move) {
                        y1 = i;
                        x1 = j;
                    } else if (board[i][j] == move + 1) {
                        y2 = i;
                        x2 = j;
                    }
                }
            }

            // Verificar que es un movimiento de caballo válido
            if (x1 != -1 && y1 != -1 && x2 != -1 && y2 != -1) {
                int dx = Math.abs(x2 - x1);
                int dy = Math.abs(y2 - y1);

                if ((dx != 1 || dy != 2) && (dx != 2 || dy != 1)) {
                    return false; // No es un movimiento de caballo válido
                }
            } else {
                return false; // No se encontraron los movimientos consecutivos
            }
        }

        return true;
    }

    /**
     * Obtiene el tamaño del tablero
     * @return tamaño del tablero
     */
    public int getSize() {
        return size;
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
     * Obtiene el número de movimientos realizados
     * @return contador de movimientos
     */
    public int getMoveCount() {
        return moveCount;
    }

    /**
     * Obtiene una copia del estado del tablero
     * @return matriz con el estado (-1 si no visitada, 0+ orden de visita)
     */
    public int[][] getBoard() {
        int[][] copy = new int[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, size);
        }
        return copy;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (board[i][j] == -1) {
                    sb.append(".. ");
                } else {
                    sb.append(String.format("%2d ", board[i][j]));
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}