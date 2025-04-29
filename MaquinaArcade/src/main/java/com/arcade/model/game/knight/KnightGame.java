package com.arcade.model.game.knight;

import com.arcade.model.entity.GameRecord;
import com.arcade.model.entity.KnightRecord;
import com.arcade.model.game.AbstractGame;

/**
 * Implementación del juego del Recorrido del Caballo
 * Resuelve el problema de recorrer todo el tablero de ajedrez
 * con un caballo, pasando una sola vez por cada casilla
 */
public class KnightGame extends AbstractGame {

    // Posibles movimientos del caballo (8 direcciones)
    private static final int[] X_MOVES = {2, 1, -1, -2, -2, -1, 1, 2};
    private static final int[] Y_MOVES = {1, 2, 2, 1, -1, -2, -2, -1};

    private int boardSize;
    private int[][] board; // Almacena el orden de las casillas visitadas
    private int startX;
    private int startY;
    private int totalMoves;

    /**
     * Constructor por defecto
     */
    public KnightGame() {
        super("Recorrido del Caballo", "Recorre todo el tablero con movimientos de caballo");
    }

    @Override
    public void initialize(Object... params) {
        super.initialize(params);

        if (params.length < 3 ||
                !(params[0] instanceof Integer) ||
                !(params[1] instanceof Integer) ||
                !(params[2] instanceof Integer)) {
            throw new IllegalArgumentException("Se requiere el tamaño del tablero y posición inicial (x,y)");
        }

        this.boardSize = (Integer) params[0];
        this.startX = (Integer) params[1];
        this.startY = (Integer) params[2];

        if (boardSize < 5) {
            throw new IllegalArgumentException("El tamaño del tablero debe ser al menos 5");
        }

        if (startX < 0 || startX >= boardSize || startY < 0 || startY >= boardSize) {
            throw new IllegalArgumentException("Posición inicial fuera del tablero");
        }

        this.board = new int[boardSize][boardSize];
        this.totalMoves = 0;

        // Inicializar tablero con -1 (no visitado)
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                board[i][j] = -1;
            }
        }

        // Marcar posición inicial
        board[startY][startX] = 0;
        totalMoves = 1; // Ya hemos visitado la primera casilla
    }

    @Override
    public boolean solve() {
        if (!initialized) {
            throw new IllegalStateException("El juego no ha sido inicializado");
        }

        // Reiniciar el tablero para resolver
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                board[i][j] = -1;
            }
        }

        board[startY][startX] = 0;
        totalMoves = 1;

        // Resolver usando backtracking
        boolean solved = solveBacktracking(startX, startY, 1);

        if (solved) {
            setCompleted();
        }

        return solved;
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
                totalMoves++;

                // Continuar con el siguiente movimiento
                if (solveBacktracking(nextX, nextY, moveCount + 1)) {
                    return true;
                }

                // Si no lleva a una solución, deshacer (backtrack)
                board[nextY][nextX] = -1;
                totalMoves--;
            }
        }

        return false; // No se encontró solución desde esta posición
    }

    /**
     * Verifica si un movimiento es válido
     * @param x posición x
     * @param y posición y
     * @return true si el movimiento es válido
     */
    private boolean isValidMove(int x, int y) {
        return (x >= 0 && x < boardSize &&
                y >= 0 && y < boardSize &&
                board[y][x] == -1);
    }

    @Override
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

    @Override
    public GameRecord createRecord(boolean isCompleted) {
        return new KnightRecord(
                boardSize,
                startX,
                startY,
                totalMoves,
                steps,
                isCompleted,
                startTime,
                endTime
        );
    }

    /**
     * Obtiene el tamaño del tablero
     * @return tamaño del tablero
     */
    public int getBoardSize() {
        return boardSize;
    }

    /**
     * Obtiene el tablero con el orden de visita
     * @return matriz con el orden de visita (-1 si no visitado)
     */
    public int[][] getBoard() {
        int[][] copy = new int[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, boardSize);
        }
        return copy;
    }

    /**
     * Obtiene la posición inicial X
     * @return posición X inicial
     */
    public int getStartX() {
        return startX;
    }

    /**
     * Obtiene la posición inicial Y
     * @return posición Y inicial
     */
    public int getStartY() {
        return startY;
    }

    /**
     * Obtiene el total de movimientos realizados
     * @return número de casillas visitadas
     */
    public int getTotalMoves() {
        return totalMoves;
    }

    /**
     * Intenta realizar un movimiento de caballo
     * @param fromX posición X actual
     * @param fromY posición Y actual
     * @param toX posición X destino
     * @param toY posición Y destino
     * @return true si el movimiento fue válido
     */
    public boolean move(int fromX, int fromY, int toX, int toY) {
        if (!initialized ||
                fromX < 0 || fromX >= boardSize ||
                fromY < 0 || fromY >= boardSize ||
                toX < 0 || toX >= boardSize ||
                toY < 0 || toY >= boardSize) {
            return false;
        }

        // Verificar que la posición origen tiene el último movimiento
        if (board[fromY][fromX] != totalMoves - 1) {
            return false;
        }

        // Verificar que la posición destino no ha sido visitada
        if (board[toY][toX] != -1) {
            return false;
        }

        // Verificar que es un movimiento de caballo válido
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

        // Comprobar si se ha completado el tablero
        if (totalMoves == boardSize * boardSize && isValidSolution()) {
            setCompleted();
        }

        return true;
    }
}