package com.arcade.model.game.knight;

/**
 * Clase que implementa la lógica del problema del Recorrido del Caballo
 * Proporciona algoritmos para resolver y validar soluciones
 */
public class KnightLogic {

    // Posibles movimientos del caballo (8 direcciones)
    private static final int[] X_MOVES = {2, 1, -1, -2, -2, -1, 1, 2};
    private static final int[] Y_MOVES = {1, 2, 2, 1, -1, -2, -2, -1};

    private final int boardSize;
    private int[][] board; // Almacena el orden de las casillas visitadas (-1 si no visitada)
    private int startX; // Posición inicial X
    private int startY; // Posición inicial Y
    private int totalMoves; // Número total de movimientos realizados
    private int steps; // Contador de pasos/intentos

    /**
     * Constructor con tamaño del tablero y posición inicial
     * @param boardSize tamaño del tablero
     * @param startX posición inicial X
     * @param startY posición inicial Y
     */
    public KnightLogic(int boardSize, int startX, int startY) {
        if (boardSize < 5) {
            throw new IllegalArgumentException("El tamaño del tablero debe ser al menos 5");
        }

        if (startX < 0 || startX >= boardSize || startY < 0 || startY >= boardSize) {
            throw new IllegalArgumentException("Posición inicial fuera del tablero");
        }

        this.boardSize = boardSize;
        this.startX = startX;
        this.startY = startY;
        this.steps = 0;

        initializeBoard();
    }

    /**
     * Inicializa el tablero
     */
    public void initializeBoard() {
        this.board = new int[boardSize][boardSize];

        // Inicializar tablero con -1 (no visitado)
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                board[i][j] = -1;
            }
        }

        // Marcar posición inicial
        board[startY][startX] = 0;
        this.totalMoves = 1; // Ya hemos visitado la primera casilla
    }

    /**
     * Resuelve el problema del recorrido del caballo mediante backtracking
     * @return true si se encontró una solución
     */
    public boolean solve() {
        // Reiniciar el tablero para resolver
        initializeBoard();

        // Resolver usando backtracking
        return solveBacktracking(startX, startY, 1);
    }

    /**
     * Método recursivo para resolver el problema mediante backtracking
     * @param x posición x actual
     * @param y posición y actual
     * @param moveCount número del movimiento actual
     * @return true si se encontró una solución
     */
    private boolean solveBacktracking(int x, int y, int moveCount) {
        if (moveCount == boardSize * boardSize) {
            return true; // Se han visitado todas las casillas
        }

        // Probar los 8 posibles movimientos
        for (int i = 0; i < 8; i++) {
            steps++; // Contar cada intento como un paso

            int nextX = x + X_MOVES[i];
            int nextY = y + Y_MOVES[i];

            // Verificar si el movimiento es válido
            if (isValidMove(nextX, nextY)) {
                // Realizar el movimiento
                board[nextY][nextX] = moveCount;

                // Continuar con el siguiente movimiento
                if (solveBacktracking(nextX, nextY, moveCount + 1)) {
                    return true;
                }

                // Si no lleva a una solución, deshacer (backtrack)
                board[nextY][nextX] = -1;
            }
        }

        return false; // No se encontró solución desde esta posición
    }

    /**
     * Intenta realizar un movimiento del caballo
     * @param fromX coordenada X origen
     * @param fromY coordenada Y origen
     * @param toX coordenada X destino
     * @param toY coordenada Y destino
     * @return true si el movimiento es válido y se realizó
     */
    public boolean move(int fromX, int fromY, int toX, int toY) {
        // Verificar coordenadas
        if (fromX < 0 || fromX >= boardSize || fromY < 0 || fromY >= boardSize ||
                toX < 0 || toX >= boardSize || toY < 0 || toY >= boardSize) {
            return false;
        }

        // Verificar que el origen tenga el último movimiento
        if (board[fromY][fromX] != totalMoves - 1) {
            return false;
        }

        // Verificar que el destino no haya sido visitado
        if (board[toY][toX] != -1) {
            return false;
        }

        // Verificar que sea un movimiento de caballo válido
        boolean validKnightMove = false;
        for (int i = 0; i < 8; i++) {
            if (fromX + X_MOVES[i] == toX && fromY + Y_MOVES[i] == toY) {
                validKnightMove = true;
                break;
            }
        }

        if (!validKnightMove) {
            return false;
        }

        // Realizar el movimiento
        board[toY][toX] = totalMoves;
        totalMoves++;
        steps++;

        return true;
    }

    /**
     * Verifica si un movimiento es válido
     * @param x coordenada X
     * @param y coordenada Y
     * @return true si el movimiento es válido
     */
    public boolean isValidMove(int x, int y) {
        return (x >= 0 && x < boardSize &&
                y >= 0 && y < boardSize &&
                board[y][x] == -1);
    }

    /**
     * Verifica si la configuración actual es una solución válida
     * @return true si es una solución válida
     */
    public boolean isValidSolution() {
        // Verificar que todas las casillas han sido visitadas
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (board[i][j] == -1) {
                    return false;
                }
            }
        }

        // Verificar que los movimientos son válidos (cada par de movimientos consecutivos
        // debe corresponder a un movimiento de caballo)
        for (int move = 0; move < boardSize * boardSize - 1; move++) {
            int x1 = -1, y1 = -1, x2 = -1, y2 = -1;

            // Encontrar las posiciones de los movimientos consecutivos
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
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
            boolean validKnightMove = false;
            for (int i = 0; i < 8; i++) {
                if (x1 + X_MOVES[i] == x2 && y1 + Y_MOVES[i] == y2) {
                    validKnightMove = true;
                    break;
                }
            }

            if (!validKnightMove) {
                return false;
            }
        }

        return true;
    }

    /**
     * Obtiene el tamaño del tablero
     * @return tamaño del tablero
     */
    public int getBoardSize() {
        return boardSize;
    }

    /**
     * Obtiene la posición inicial X
     * @return coordenada X inicial
     */
    public int getStartX() {
        return startX;
    }

    /**
     * Obtiene la posición inicial Y
     * @return coordenada Y inicial
     */
    public int getStartY() {
        return startY;
    }

    /**
     * Obtiene el tablero actual
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
     * Obtiene el total de movimientos realizados
     * @return número de casillas visitadas
     */
    public int getTotalMoves() {
        return totalMoves;
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

    /**
     * Verifica si el recorrido está completo
     * @return true si se han visitado todas las casillas
     */
    public boolean isComplete() {
        return totalMoves == boardSize * boardSize;
    }
}