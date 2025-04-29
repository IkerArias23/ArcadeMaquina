package com.arcade.model.game.queens;

/**
 * Clase que representa una reina en el problema de las N reinas
 * Proporciona métodos para comprobar si hay conflictos con otras reinas
 */
public class Queen {

    private int row;    // Fila de la reina (0-based)
    private int column; // Columna de la reina (0-based)

    /**
     * Constructor con posición
     * @param row fila
     * @param column columna
     */
    public Queen(int row, int column) {
        this.row = row;
        this.column = column;
    }

    /**
     * Verifica si esta reina amenaza a otra
     * @param other otra reina
     * @return true si hay conflicto (se amenazan)
     */
    public boolean threatens(Queen other) {
        // Misma fila
        if (row == other.row) {
            return true;
        }

        // Misma columna
        if (column == other.column) {
            return true;
        }

        // Misma diagonal (diferencia de filas = diferencia de columnas)
        if (Math.abs(row - other.row) == Math.abs(column - other.column)) {
            return true;
        }

        return false; // No hay conflicto
    }

    /**
     * Verifica si esta reina está en una posición válida respecto a un conjunto de reinas
     * @param queens arreglo de reinas
     * @param count número de reinas en el arreglo
     * @return true si no hay conflictos
     */
    public boolean isValidWith(Queen[] queens, int count) {
        for (int i = 0; i < count; i++) {
            if (threatens(queens[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Obtiene la fila de la reina
     * @return fila
     */
    public int getRow() {
        return row;
    }

    /**
     * Establece la fila de la reina
     * @param row nueva fila
     */
    public void setRow(int row) {
        this.row = row;
    }

    /**
     * Obtiene la columna de la reina
     * @return columna
     */
    public int getColumn() {
        return column;
    }

    /**
     * Establece la columna de la reina
     * @param column nueva columna
     */
    public void setColumn(int column) {
        this.column = column;
    }

    /**
     * Mueve la reina a una nueva posición
     * @param row nueva fila
     * @param column nueva columna
     */
    public void moveTo(int row, int column) {
        this.row = row;
        this.column = column;
    }

    @Override
    public String toString() {
        return "Queen[" + row + "," + column + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Queen queen = (Queen) obj;
        return row == queen.row && column == queen.column;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + row;
        result = 31 * result + column;
        return result;
    }
}