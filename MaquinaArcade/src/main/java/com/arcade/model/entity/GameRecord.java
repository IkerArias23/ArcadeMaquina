package com.arcade.model.entity;

import java.time.LocalDateTime;

import javax.persistence.*;

/**
 * Clase base para registros de partidas
 * Implementa la estructura común para todos los registros de juegos
 *
 * Utilizada como parte del patrón de herencia para Hibernate (InheritanceType.JOINED)
 */
@Entity
@Table(name = "game_records")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "game_type")
public abstract class GameRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "steps")
    private int steps;

    @Column(name = "completed")
    private boolean completed;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "elapsed_time_seconds")
    private long elapsedTimeSeconds;

    /**
     * Constructor por defecto requerido por Hibernate
     */
    public GameRecord() {
    }

    /**
     * Constructor con parámetros básicos
     * @param steps número de pasos realizados
     * @param completed indica si se completó el juego
     * @param startTime tiempo de inicio
     * @param endTime tiempo de finalización
     */
    public GameRecord(int steps, boolean completed, LocalDateTime startTime, LocalDateTime endTime) {
        this.steps = steps;
        this.completed = completed;
        this.startTime = startTime;
        this.endTime = endTime;

        if (startTime != null && endTime != null) {
            this.elapsedTimeSeconds = java.time.Duration.between(startTime, endTime).getSeconds();
        }
    }

    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public long getElapsedTimeSeconds() {
        return elapsedTimeSeconds;
    }

    public void setElapsedTimeSeconds(long elapsedTimeSeconds) {
        this.elapsedTimeSeconds = elapsedTimeSeconds;
    }

    /**
     * Obtiene el tipo de juego
     * @return tipo de juego
     */
    public abstract String getGameType();
}