package com.arcade.model.game.queens;

import com.arcade.model.entity.GameRecord;
import com.arcade.model.entity.QueenRecord;
import com.arcade.model.game.AbstractGame;

/**
 * Implementación del juego de las N Reinas
 * Resuelve el problema de colocar N reinas en un tablero de ajedrez
 * sin que ninguna amenace a otra
 */
public class QueensGame extends AbstractGame {

    private int boardSize;
    private int[] queens; // Posición de las reinas (índice = fila, valor = columna)
    private boolean[][] board; // true si hay una reina, false si no

    /**
     * Constructor por defecto
     */
    public QueensGame() {
        super("N Reinas", "Coloca N reinas en un tablero sin que se amenacen");
    }

    @Override
    public void initialize(Object... params) {
        super.initialize(params);

        if (params.length < 1 || !(params[0] instanceof Integer)) {
            throw new IllegalArgumentException("Se requiere el tamaño del tablero (N)");
        }

        this.boardSize = (Integer) params[0];

        if (boardSize < 4) {
            throw new IllegalArgumentException("El tamaño del tablero debe ser al menos 4");
        }

        this.queens = new int[boardSize];
        this.board = new boolean[boardSize][boardSize];

        // Inicializar tablero vacío
        for (int i = 0; i < boardSize; i++) {
            queens[i] = -1; // No hay reina colocada
            for (int j = 0; j < boardSize; j++) {
                board[i][j] = false;
            }
        }
    }

    @Override
    public boolean solve() {
        if (!initialized) {
            throw new IllegalStateException("El juego no ha sido inicializado");
        }

        // Reiniciar el tablero para resolver
        for (int i = 0; i < boardSize; i++) {
            queens[i] = -1;
            for (int j = 0; j < boardSize; j++) {
                board[i][j] = false;
            }
        }

        // Resolver usando backtracking
        boolean solved = solveBacktracking(0);

        if (solved) {
            setCompleted();
        }

        return solved;
    }

    /**
     * Método recursivo para resolver el problema mediante backtracking
     * @param row fila actual
     * @return true si se encontró una solución
     */
    private boolean solveBacktracking(int row) {
        if (row >= boardSize) {
            return true; // Todas las reinas colocadas correctamente
        }

        // Intentar colocar una reina en cada columna de la fila actual
        for (int col = 0; col < boardSize; col++) {
            steps++; // Contar cada intento como un paso

            if (isSafePosition(row, col)) {
                // Colocar reina
                queens[row] = col;
                board[row][col] = true;

                // Pasar a la siguiente fila
                if (solveBacktracking(row + 1)) {
                    return true;
                }

                // Si no lleva a una solución, retirar la reina (backtrack)
                queens[row] = -1;
                board[row][col] = false;
            }
        }

        return false; // No se pudo colocar una reina en esta fila
    }

    /**
     * Verifica si es seguro colocar una reina en la posición dada
     * @param row fila
     * @param col columna
     * @return true si es seguro colocar una reina
     */
    private boolean isSafePosition(int row, int col) {
        // Comprobar columna
        for (int i = 0; i < row; i++) {
            if (queens[i] == col) {
                return false; // Hay una reina en la misma columna
            }
        }

        // Comprobar diagonal superior izquierda
        for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
            if (board[i][j]) {
                return false; // Hay una reina en la diagonal
            }
        }

        // Comprobar diagonal superior derecha
        for (int i = row - 1, j = col + 1; i >= 0 && j < boardSize; i--, j++) {
            if (board[i][j]) {
                return false; // Hay una reina en la diagonal
            }
        }

        return true; // Es seguro colocar una reina
    }

    @Override
    public boolean isValidSolution() {
        // Verificar que todas las reinas estén colocadas
        for (int i = 0; i < boardSize; i++) {
            if (queens[i] == -1) {
                return false;
            }
        }

        // Verificar que ninguna reina amenace a otra
        for (int i = 0; i < boardSize; i++) {
            for (int j = i + 1; j < boardSize; j++) {
                // Misma columna
                if (queens[i] == queens[j]) {
                    return false;
                }

                // Diagonales (diferencia de índices)
                if (Math.abs(queens[i] - queens[j]) == Math.abs(i - j)) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public GameRecord createRecord(boolean isCompleted) {
        return new QueenRecord(
                boardSize,
                steps,
                isCompleted,
                startTime,
                endTime
        );
    }

    /**
     * Obtiene el tamaño del tablero
     * @return tamaño N del tablero
     */
    public int getBoardSize() {
        return boardSize;
    }

    /**
     * Obtiene la posición de las reinas
     * @return array con posiciones (índice = fila, valor = columna)
     */
    public int[] getQueensPositions() {
        return queens.clone();
    }

    /**
     * Intenta colocar una reina en una posición específica
     * @param row fila
     * @param col columna
     * @return true si se pudo colocar
     */
    public boolean placeQueen(int row, int col) {
        if (!initialized || row < 0 || row >= boardSize || col < 0 || col >= boardSize) {
            return false;
        }

        // Verificar si es una posición válida
        boolean safe = true;

        // Comprobar si hay reinas en la misma fila
        for (int c = 0; c < boardSize; c++) {
            if (board[row][c] && c != col) {
                safe = false;
                break;
            }
        }

        // Comprobar si hay reinas en la misma columna
        if (safe) {
            for (int r = 0; r < boardSize; r++) {
                if (board[r][col] && r != row) {
                    safe = false;
                    break;
                }
            }
        }

        // Comprobar diagonales
        if (safe) {
            // Diagonal superior izquierda
            for (int r = row - 1, c = col - 1; r >= 0 && c >= 0; r--, c--) {
                if (board[r][c]) {
                    safe = false;
                    break;
                }
            }
        }

        if (safe) {
            // Diagonal superior derecha
            for (int r = row - 1, c = col + 1; r >= 0 && c < boardSize; r--, c++) {
                if (board[r][c]) {
                    safe = false;
                    break;
                }
            }
        }

        if (safe) {
            // Diagonal inferior izquierda
            for (int r = row + 1, c = col - 1; r < boardSize && c >= 0; r++, c--) {
                if (board[r][c]) {
                    safe = false;
                    break;
                }
            }
        }

        if (safe) {
            // Diagonal inferior derecha
            for (int r = row + 1, c = col + 1; r < boardSize && c < boardSize; r++, c++) {
                if (board[r][c]) {
                    safe = false;
                    break;
                }
            }
        }

        if (safe) {
            // Si la posición es segura, colocar la reina
            queens[row] = col;
            board[row][col] = true;

            steps++;

            // Comprobar si se ha completado el tablero
            boolean complete = true;
            for (int i = 0; i < boardSize; i++) {
                if (queens[i] == -1) {
                    complete = false;
                    break;
                }
            }

            if (complete && isValidSolution()) {
                setCompleted();
            }

            return true;
        }

        return false;
    }

    /**
     * Retira una reina de una posición
     * @param row fila
     * @param col columna
     */
    public void removeQueen(int row, int col) {
        if (initialized && row >= 0 && row < boardSize && col >= 0 && col < boardSize) {
            if (board[row][col]) {
                queens[row] = -1;
                board[row][col] = false;
                steps++;
            }
        }
    }
}