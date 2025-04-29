package com.arcade.model.entity;

import java.time.LocalDateTime;

import javax.persistence.*;

/**
 * Entidad para almacenar registros de partidas de Torres de Hanoi
 */
@Entity
@Table(name = "hanoi_records")
@DiscriminatorValue("HANOI")
public class HanoiRecord extends GameRecord {

    @Column(name = "num_disks")
    private int numDisks;

    @Column(name = "movements")
    private int movements;

    /**
     * Constructor por defecto requerido por Hibernate
     */
    public HanoiRecord() {
        super();
    }

    /**
     * Constructor con parámetros específicos del juego
     *
     * @param numDisks número de discos
     * @param movements número de movimientos realizados
     * @param completed indica si se completó el juego
     * @param startTime tiempo de inicio
     * @param endTime tiempo de finalización
     */
    public HanoiRecord(int numDisks, int movements, boolean completed,
                       LocalDateTime startTime, LocalDateTime endTime) {
        super(movements, completed, startTime, endTime);
        this.numDisks = numDisks;
        this.movements = movements;
    }

    /**
     * Obtiene el número de discos
     * @return número de discos
     */
    public int getNumDisks() {
        return numDisks;
    }

    /**
     * Establece el número de discos
     * @param numDisks número de discos
     */
    public void setNumDisks(int numDisks) {
        this.numDisks = numDisks;
    }

    /**
     * Obtiene el número de movimientos realizados
     * @return número de movimientos
     */
    public int getMovements() {
        return movements;
    }

    /**
     * Establece el número de movimientos realizados
     * @param movements número de movimientos
     */
    public void setMovements(int movements) {
        this.movements = movements;
    }

    /**
     * Calcula el número mínimo de movimientos necesarios
     * @return número mínimo de movimientos (2^n - 1)
     */
    public int getMinimumMoves() {
        return (int) Math.pow(2, numDisks) - 1;
    }

    /**
     * Indica si se resolvió de forma óptima
     * @return true si se usaron exactamente los movimientos mínimos
     */
    public boolean isOptimalSolution() {
        return movements == getMinimumMoves();
    }

    @Override
    public String getGameType() {
        return "Torres de Hanoi";
    }

    @Override
    public String toString() {
        return "HanoiRecord [id=" + getId() +
                ", numDisks=" + numDisks +
                ", movements=" + movements +
                " (óptimo: " + getMinimumMoves() + ")" +
                ", completed=" + isCompleted() +
                ", time=" + getElapsedTimeSeconds() + "s]";
    }
}