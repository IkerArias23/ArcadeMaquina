package com.arcade.model.game.queens;

/**
 * Clase que representa un tablero para el problema de las N reinas
 * Proporciona métodos para manipular y verificar el estado del tablero
 */
public class QueensBoard {

    private final int size;
    private boolean[][] board; // true si hay una reina, false si no
    private int[] queensInRows; // Posición de las reinas (índice = fila, valor = columna)

    /**
     * Constructor con tamaño del tablero
     * @param size tamaño del tablero (N)
     */
    public QueensBoard(int size) {
        if (size < 4) {
            throw new IllegalArgumentException("El tamaño del tablero debe ser al menos 4");
        }

        this.size = size;
        this.board = new boolean[size][size];
        this.queensInRows = new int[size];

        // Inicializar el tablero vacío
        clear();
    }

    /**
     * Limpia el tablero (quita todas las reinas)
     */
    public void clear() {
        for (int i = 0; i < size; i++) {
            queensInRows[i] = -1; // No hay reina en esta fila
            for (int j = 0; j < size; j++) {
                board[i][j] = false;
            }
        }
    }

    /**
     * Intenta colocar una reina en la posición especificada
     * @param row fila (0-based)
     * @param col columna (0-based)
     * @return true si la reina se colocó correctamente
     */
    public boolean placeQueen(int row, int col) {
        if (!isValidPosition(row, col)) {
            return false;
        }

        // Verificar si la posición es segura
        if (!isSafePosition(row, col)) {
            return false;
        }

        // Si hay una reina en la misma fila, quitarla primero
        if (queensInRows[row] != -1) {
            board[row][queensInRows[row]] = false;
        }

        // Colocar la reina
        board[row][col] = true;
        queensInRows[row] = col;

        return true;
    }

    /**
     * Quita una reina de la posición especificada
     * @param row fila
     * @param col columna
     * @return true si había una reina para quitar
     */
    public boolean removeQueen(int row, int col) {
        if (!isValidPosition(row, col) || !board[row][col]) {
            return false;
        }

        board[row][col] = false;
        queensInRows[row] = -1;

        return true;
    }

    /**
     * Verifica si la posición dada es segura para colocar una reina
     * @param row fila
     * @param col columna
     * @return true si es seguro colocar una reina
     */
    public boolean isSafePosition(int row, int col) {
        // Comprobar columna
        for (int i = 0; i < size; i++) {
            if (i != row && board[i][col]) {
                return false; // Hay una reina en la misma columna
            }
        }

        // Comprobar diagonales
        return checkDiagonals(row, col);
    }

    /**
     * Verifica si hay conflictos en las diagonales
     * @param row fila
     * @param col columna
     * @return true si no hay reinas en las diagonales
     */
    private boolean checkDiagonals(int row, int col) {
        // Diagonal superior izquierda
        for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
            if (board[i][j]) {
                return false;
            }
        }

        // Diagonal superior derecha
        for (int i = row - 1, j = col + 1; i >= 0 && j < size; i--, j++) {
            if (board[i][j]) {
                return false;
            }
        }

        // Diagonal inferior izquierda
        for (int i = row + 1, j = col - 1; i < size && j >= 0; i++, j--) {
            if (board[i][j]) {
                return false;
            }
        }

        // Diagonal inferior derecha
        for (int i = row + 1, j = col + 1; i < size && j < size; i++, j++) {
            if (board[i][j]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Verifica si la posición está dentro del tablero
     * @param row fila
     * @param col columna
     * @return true si la posición es válida
     */
    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < size && col >= 0 && col < size;
    }

    /**
     * Verifica si hay una reina en la posición dada
     * @param row fila
     * @param col columna
     * @return true si hay una reina
     */
    public boolean hasQueen(int row, int col) {
        return isValidPosition(row, col) && board[row][col];
    }

    /**
     * Verifica si todas las reinas están colocadas correctamente
     * @return true si el tablero tiene una solución válida
     */
    public boolean isSolved() {
        // Verificar que hay N reinas
        int queensCount = 0;
        for (int r : queensInRows) {
            if (r != -1) {
                queensCount++;
            }
        }

        if (queensCount != size) {
            return false;
        }

        // Verificar que no hay amenazas
        for (int i = 0; i < size; i++) {
            int col = queensInRows[i];

            // Verificar columna (otras filas)
            for (int j = 0; j < size; j++) {
                if (j != i && queensInRows[j] == col) {
                    return false; // Dos reinas en la misma columna
                }
            }

            // Verificar diagonales
            for (int j = 0; j < size; j++) {
                if (j != i && queensInRows[j] != -1) {
                    // Diagonal: diferencia de filas = diferencia de columnas
                    if (Math.abs(i - j) == Math.abs(queensInRows[i] - queensInRows[j])) {
                        return false; // Reinas en la misma diagonal
                    }
                }
            }
        }

        return true;
    }

    /**
     * Obtiene el tamaño del tablero
     * @return tamaño N
     */
    public int getSize() {
        return size;
    }

    /**
     * Obtiene las posiciones de las reinas
     * @return array donde el índice es la fila y el valor es la columna (-1 si no hay reina)
     */
    public int[] getQueensPositions() {
        return queensInRows.clone();
    }

    /**
     * Obtiene una copia del estado del tablero
     * @return matriz booleana (true donde hay reina)
     */
    public boolean[][] getBoard() {
        boolean[][] copy = new boolean[size][size];
        for (int i = 0; i < size; i++) {
            System.arraycopy(board[i], 0, copy[i], 0, size);
        }
        return copy;
    }

    /**
     * Establece las posiciones de las reinas según un arreglo dado
     * @param positions arreglo donde el índice es la fila y el valor es la columna
     * @return true si todas las posiciones son válidas
     */
    public boolean setQueensPositions(int[] positions) {
        if (positions.length != size) {
            return false;
        }

        // Limpiar el tablero primero
        clear();

        // Colocar las reinas
        for (int row = 0; row < size; row++) {
            int col = positions[row];
            if (col != -1) {
                if (!placeQueen(row, col)) {
                    clear(); // Restaurar estado inicial en caso de error
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                sb.append(board[i][j] ? "Q " : ". ");
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}