package com.arcade.model.entity;

import java.time.LocalDateTime;

import javax.persistence.*;

/**
 * Entidad para almacenar registros de partidas del Recorrido del Caballo
 */
@Entity
@Table(name = "knight_records")
@DiscriminatorValue("KNIGHT")
public class KnightRecord extends GameRecord {

    @Column(name = "board_size")
    private int boardSize;

    @Column(name = "start_x")
    private int startX;

    @Column(name = "start_y")
    private int startY;

    @Column(name = "total_moves")
    private int totalMoves;

    /**
     * Constructor por defecto requerido por Hibernate
     */
    public KnightRecord() {
        super();
    }

    /**
     * Constructor con parámetros específicos del juego
     *
     * @param boardSize tamaño del tablero
     * @param startX posición X inicial
     * @param startY posición Y inicial
     * @param totalMoves total de movimientos realizados
     * @param steps número de intentos/pasos realizados
     * @param completed indica si se completó el juego
     * @param startTime tiempo de inicio
     * @param endTime tiempo de finalización
     */
    public KnightRecord(int boardSize, int startX, int startY, int totalMoves,
                        int steps, boolean completed,
                        LocalDateTime startTime, LocalDateTime endTime) {
        super(steps, completed, startTime, endTime);
        this.boardSize = boardSize;
        this.startX = startX;
        this.startY = startY;
        this.totalMoves = totalMoves;
    }

    /**
     * Obtiene el tamaño del tablero
     * @return tamaño del tablero
     */
    public int getBoardSize() {
        return boardSize;
    }

    /**
     * Establece el tamaño del tablero
     * @param boardSize tamaño del tablero
     */
    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
    }

    /**
     * Obtiene la posición X inicial
     * @return posición X inicial
     */
    public int getStartX() {
        return startX;
    }

    /**
     * Establece la posición X inicial
     * @param startX posición X inicial
     */
    public void setStartX(int startX) {
        this.startX = startX;
    }

    /**
     * Obtiene la posición Y inicial
     * @return posición Y inicial
     */
    public int getStartY() {
        return startY;
    }

    /**
     * Establece la posición Y inicial
     * @param startY posición Y inicial
     */
    public void setStartY(int startY) {
        this.startY = startY;
    }

    /**
     * Obtiene el total de movimientos realizados
     * @return número de casillas visitadas
     */
    public int getTotalMoves() {
        return totalMoves;
    }

    /**
     * Establece el total de movimientos realizados
     * @param totalMoves número de casillas visitadas
     */
    public void setTotalMoves(int totalMoves) {
        this.totalMoves = totalMoves;
    }

    @Override
    public String getGameType() {
        return "Recorrido del Caballo";
    }

    @Override
    public String toString() {
        return "KnightRecord [id=" + getId() +
                ", boardSize=" + boardSize +
                ", startPos=(" + startX + "," + startY + ")" +
                ", totalMoves=" + totalMoves +
                ", steps=" + getSteps() +
                ", completed=" + isCompleted() +
                ", time=" + getElapsedTimeSeconds() + "s]";
    }
}