package com.arcade.model.game;

import java.time.LocalDateTime;

/**
 * Clase abstracta que implementa funcionalidad común para todos los juegos
 * Implementa la interfaz Game y proporciona implementación base
 */
public abstract class AbstractGame implements Game {

    protected String name;
    protected String description;
    protected boolean initialized;
    protected boolean solved;
    protected int steps;
    protected LocalDateTime startTime;
    protected LocalDateTime endTime;

    /**
     * Constructor con nombre y descripción
     * @param name nombre del juego
     * @param description descripción del juego
     */
    public AbstractGame(String name, String description) {
        this.name = name;
        this.description = description;
        this.initialized = false;
        this.solved = false;
        this.steps = 0;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void initialize(Object... params) {
        this.startTime = LocalDateTime.now();
        this.steps = 0;
        this.solved = false;
        this.initialized = true;
    }

    @Override
    public boolean step() {
        if (!initialized) {
            throw new IllegalStateException("El juego no ha sido inicializado");
        }

        if (solved) {
            return false; // No se pueden realizar más pasos si ya está resuelto
        }

        steps++;
        return true;
    }

    @Override
    public void reset() {
        this.initialized = false;
        this.solved = false;
        this.steps = 0;
        this.startTime = null;
        this.endTime = null;
    }

    /**
     * Marca el juego como completado
     */
    protected void setCompleted() {
        this.solved = true;
        this.endTime = LocalDateTime.now();
    }

    /**
     * Obtiene el número de pasos realizados
     * @return número de pasos
     */
    public int getSteps() {
        return steps;
    }

    /**
     * Obtiene el tiempo transcurrido en segundos
     * @return tiempo en segundos o -1 si no ha finalizado
     */
    public long getElapsedTimeSeconds() {
        if (startTime == null) {
            return 0;
        }

        LocalDateTime end = (endTime != null) ? endTime : LocalDateTime.now();
        return java.time.Duration.between(startTime, end).getSeconds();
    }

    /**
     * Indica si el juego ha sido resuelto
     * @return true si está resuelto
     */
    public boolean isSolved() {
        return solved;
    }
}