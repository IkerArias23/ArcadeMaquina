package com.arcade.model.entity;

import java.time.LocalDateTime;

import javax.persistence.*;

/**
 * Entidad para almacenar registros de partidas del juego de N Reinas
 */
@Entity
@Table(name = "queen_records")
@DiscriminatorValue("QUEENS")
public class QueenRecord extends GameRecord {

    @Column(name = "board_size")
    private int boardSize;

    /**
     * Constructor por defecto requerido por Hibernate
     */
    public QueenRecord() {
        super();
    }

    /**
     * Constructor con parámetros específicos del juego
     *
     * @param boardSize tamaño del tablero
     * @param steps número de pasos realizados
     * @param completed indica si se completó el juego
     * @param startTime tiempo de inicio
     * @param endTime tiempo de finalización
     */
    public QueenRecord(int boardSize, int steps, boolean completed,
                       LocalDateTime startTime, LocalDateTime endTime) {
        super(steps, completed, startTime, endTime);
        this.boardSize = boardSize;
    }

    /**
     * Obtiene el tamaño del tablero
     * @return tamaño del tablero (N)
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

    @Override
    public String getGameType() {
        return "N Reinas";
    }

    @Override
    public String toString() {
        return "QueenRecord [id=" + getId() +
                ", boardSize=" + boardSize +
                ", steps=" + getSteps() +
                ", completed=" + isCompleted() +
                ", time=" + getElapsedTimeSeconds() + "s]";
    }
}