package com.arcade.model.game.knight;

/**
 * Clase que representa un caballo en el problema del Recorrido del Caballo
 * Proporciona métodos para calcular movimientos y verificar su validez
 */
public class Knight {

    // Posibles movimientos del caballo (8 direcciones)
    private static final int[] X_MOVES = {2, 1, -1, -2, -2, -1, 1, 2};
    private static final int[] Y_MOVES = {1, 2, 2, 1, -1, -2, -2, -1};

    private int x;       // Posición X actual
    private int y;       // Posición Y actual
    private int boardSize; // Tamaño del tablero

    /**
     * Constructor con posición inicial y tamaño del tablero
     * @param x posición X inicial
     * @param y posición Y inicial
     * @param boardSize tamaño del tablero
     */
    public Knight(int x, int y, int boardSize) {
        if (x < 0 || x >= boardSize || y < 0 || y >= boardSize) {
            throw new IllegalArgumentException("Posición inicial fuera del tablero");
        }

        this.x = x;
        this.y = y;
        this.boardSize = boardSize;
    }

    /**
     * Verifica si un movimiento es válido
     * @param newX nueva posición X
     * @param newY nueva posición Y
     * @param visited matriz que indica si una casilla ha sido visitada
     * @return true si el movimiento es válido
     */
    public boolean isValidMove(int newX, int newY, boolean[][] visited) {
        // Verificar que las coordenadas están dentro del tablero
        if (newX < 0 || newX >= boardSize || newY < 0 || newY >= boardSize) {
            return false;
        }

        // Verificar que la casilla no ha sido visitada
        if (visited[newY][newX]) {
            return false;
        }

        // Verificar que es un movimiento de caballo (en forma de L)
        int dx = Math.abs(newX - x);
        int dy = Math.abs(newY - y);

        return (dx == 1 && dy == 2) || (dx == 2 && dy == 1);
    }

    /**
     * Mueve el caballo a una nueva posición
     * @param newX nueva posición X
     * @param newY nueva posición Y
     */
    public void moveTo(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }

    /**
     * Calcula los posibles movimientos desde la posición actual
     * @param visited matriz que indica si una casilla ha sido visitada
     * @return array con las posiciones [x,y] de los movimientos válidos
     */
    public int[][] getPossibleMoves(boolean[][] visited) {
        // Contar movimientos válidos
        int count = 0;
        for (int i = 0; i < 8; i++) {
            int newX = x + X_MOVES[i];
            int newY = y + Y_MOVES[i];

            if (isValidMove(newX, newY, visited)) {
                count++;
            }
        }

        // Crear array con los movimientos válidos
        int[][] validMoves = new int[count][2];
        int index = 0;

        for (int i = 0; i < 8; i++) {
            int newX = x + X_MOVES[i];
            int newY = y + Y_MOVES[i];

            if (isValidMove(newX, newY, visited)) {
                validMoves[index][0] = newX;
                validMoves[index][1] = newY;
                index++;
            }
        }

        return validMoves;
    }

    /**
     * Implementación de la heurística de Warnsdorff
     * Ordena los movimientos según el número de opciones futuras
     * (preferencia a casillas con menos opciones)
     * @param visited matriz que indica si una casilla ha sido visitada
     * @return array con las posiciones [x,y] ordenadas por la heurística
     */
    public int[][] getWarnsdorffMoves(boolean[][] visited) {
        // Obtener movimientos válidos
        int[][] validMoves = getPossibleMoves(visited);

        // Si no hay movimientos, devolver array vacío
        if (validMoves.length == 0) {
            return new int[0][0];
        }

        // Calcular número de opciones futuras para cada movimiento
        int[] options = new int[validMoves.length];

        for (int i = 0; i < validMoves.length; i++) {
            int moveX = validMoves[i][0];
            int moveY = validMoves[i][1];

            // Marcar casilla como visitada temporalmente
            boolean[][] tempVisited = new boolean[boardSize][boardSize];
            for (int r = 0; r < boardSize; r++) {
                for (int c = 0; c < boardSize; c++) {
                    tempVisited[r][c] = visited[r][c];
                }
            }
            tempVisited[moveY][moveX] = true;

            // Contar opciones futuras
            options[i] = 0;
            for (int j = 0; j < 8; j++) {
                int nextX = moveX + X_MOVES[j];
                int nextY = moveY + Y_MOVES[j];

                if (nextX >= 0 && nextX < boardSize && nextY >= 0 && nextY < boardSize && !tempVisited[nextY][nextX]) {
                    options[i]++;
                }
            }
        }

        // Ordenar movimientos por número de opciones (menor a mayor)
        for (int i = 0; i < validMoves.length - 1; i++) {
            for (int j = 0; j < validMoves.length - i - 1; j++) {
                if (options[j] > options[j + 1]) {
                    // Intercambiar movimientos
                    int[] tempMove = validMoves[j];
                    validMoves[j] = validMoves[j + 1];
                    validMoves[j + 1] = tempMove;

                    // Intercambiar opciones
                    int tempOption = options[j];
                    options[j] = options[j + 1];
                    options[j + 1] = tempOption;
                }
            }
        }

        return validMoves;
    }

    /**
     * Obtiene la posición X actual
     * @return posición X
     */
    public int getX() {
        return x;
    }

    /**
     * Obtiene la posición Y actual
     * @return posición Y
     */
    public int getY() {
        return y;
    }

    /**
     * Verifica si la posición actual coincide con unas coordenadas dadas
     * @param checkX coordenada X a verificar
     * @param checkY coordenada Y a verificar
     * @return true si coincide
     */
    public boolean isAt(int checkX, int checkY) {
        return x == checkX && y == checkY;
    }

    @Override
    public String toString() {
        return "Knight[" + x + "," + y + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Knight knight = (Knight) obj;
        return x == knight.x && y == knight.y && boardSize == knight.boardSize;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + x;
        result = 31 * result + y;
        result = 31 * result + boardSize;
        return result;
    }
}