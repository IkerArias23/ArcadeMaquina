package com.arcade.model.game.queens;

/**
 * Clase que implementa la lógica del problema de las N reinas
 * Proporciona algoritmos para resolver y validar soluciones
 */
public class QueensLogic {

    private final int boardSize;
    private int[] queensPositions; // Posición de las reinas (índice = fila, valor = columna)
    private boolean[][] board; // true si hay una reina, false si no
    private int steps; // Contador de pasos/intentos

    /**
     * Constructor con tamaño del tablero
     * @param boardSize tamaño del tablero (N)
     */
    public QueensLogic(int boardSize) {
        if (boardSize < 4) {
            throw new IllegalArgumentException("El tamaño del tablero debe ser al menos 4");
        }

        this.boardSize = boardSize;
        this.steps = 0;
        initializeBoard();
    }

    /**
     * Inicializa el tablero vacío
     */
    public void initializeBoard() {
        this.queensPositions = new int[boardSize];
        this.board = new boolean[boardSize][boardSize];

        // Inicializar tablero vacío
        for (int i = 0; i < boardSize; i++) {
            queensPositions[i] = -1; // No hay reina colocada
            for (int j = 0; j < boardSize; j++) {
                board[i][j] = false;
            }
        }
    }

    /**
     * Resuelve el problema de las N reinas utilizando backtracking
     * @return true si se encontró una solución
     */
    public boolean solve() {
        // Reiniciar el tablero para resolver
        initializeBoard();

        // Resolver usando backtracking
        return solveBacktracking(0);
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
                queensPositions[row] = col;
                board[row][col] = true;

                // Pasar a la siguiente fila
                if (solveBacktracking(row + 1)) {
                    return true;
                }

                // Si no lleva a una solución, retirar la reina (backtrack)
                queensPositions[row] = -1;
                board[row][col] = false;
            }
        }

        return false; // No se pudo colocar una reina en esta fila
    }

    /**
     * Intenta colocar una reina en una posición específica
     * @param row fila
     * @param col columna
     * @return true si se pudo colocar
     */
    public boolean placeQueen(int row, int col) {
        if (row < 0 || row >= boardSize || col < 0 || col >= boardSize) {
            return false;
        }

        // Verificar si es una posición válida
        if (isSafePosition(row, col)) {
            // Colocar la reina
            queensPositions[row] = col;
            board[row][col] = true;

            steps++;
            return true;
        }

        return false;
    }

    /**
     * Retira una reina de una posición
     * @param row fila
     * @param col columna
     * @return true si se pudo retirar
     */
    public boolean removeQueen(int row, int col) {
        if (row < 0 || row >= boardSize || col < 0 || col >= boardSize) {
            return false;
        }

        if (board[row][col]) {
            queensPositions[row] = -1;
            board[row][col] = false;
            steps++;
            return true;
        }

        return false;
    }

    /**
     * Verifica si es seguro colocar una reina en la posición dada
     * @param row fila
     * @param col columna
     * @return true si es seguro colocar una reina
     */
    public boolean isSafePosition(int row, int col) {
        // Comprobar columna
        for (int i = 0; i < boardSize; i++) {
            if (board[i][col] && i != row) {
                return false; // Hay una reina en la misma columna
            }
        }

        // Comprobar fila
        for (int j = 0; j < boardSize; j++) {
            if (board[row][j] && j != col) {
                return false; // Hay una reina en la misma fila
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

        // Comprobar diagonal inferior izquierda
        for (int i = row + 1, j = col - 1; i < boardSize && j >= 0; i++, j--) {
            if (board[i][j]) {
                return false; // Hay una reina en la diagonal
            }
        }

        // Comprobar diagonal inferior derecha
        for (int i = row + 1, j = col + 1; i < boardSize && j < boardSize; i++, j++) {
            if (board[i][j]) {
                return false; // Hay una reina en la diagonal
            }
        }

        return true; // Es seguro colocar una reina
    }

    /**
     * Verifica si la configuración actual es una solución válida
     * @return true si es una solución válida
     */
    public boolean isValidSolution() {
        // Verificar que todas las reinas estén colocadas
        for (int i = 0; i < boardSize; i++) {
            if (queensPositions[i] == -1) {
                return false;
            }
        }

        // Verificar que ninguna reina amenace a otra
        for (int i = 0; i < boardSize; i++) {
            for (int j = i + 1; j < boardSize; j++) {
                // Misma columna
                if (queensPositions[i] == queensPositions[j]) {
                    return false;
                }

                // Diagonales (diferencia de índices)
                if (Math.abs(queensPositions[i] - queensPositions[j]) == Math.abs(i - j)) {
                    return false;
                }
            }
        }

        return true;
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
        return queensPositions.clone();
    }

    /**
     * Obtiene la matriz del tablero
     * @return matriz con las posiciones de las reinas
     */
    public boolean[][] getBoard() {
        boolean[][] result = new boolean[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++) {
            System.arraycopy(board[i], 0, result[i], 0, boardSize);
        }
        return result;
    }

    /**
     * Obtiene el número de pasos/intentos realizados
     * @return contador de pasos
     */
    public int getSteps() {
        return steps;
    }

    /**
     * Reinicia el contador de pasos
     */
    public void resetSteps() {
        this.steps = 0;
    }
}