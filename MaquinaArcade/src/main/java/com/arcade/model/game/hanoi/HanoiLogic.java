package com.arcade.model.game.hanoi;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que implementa la lógica del problema de las Torres de Hanoi
 * Proporciona algoritmos para resolver y validar soluciones
 */
public class HanoiLogic {

    private final int numDisks;
    private final Tower[] towers;
    private final List<Move> moves;
    private final List<Move> optimalSolution;
    private int steps; // Contador de pasos/intentos

    /**
     * Representa un movimiento entre torres
     */
    public static class Move {
        private final int fromTower;
        private final int toTower;
        private final int diskSize;

        public Move(int fromTower, int toTower, int diskSize) {
            this.fromTower = fromTower;
            this.toTower = toTower;
            this.diskSize = diskSize;
        }

        public int getFromTower() {
            return fromTower;
        }

        public int getToTower() {
            return toTower;
        }

        public int getDiskSize() {
            return diskSize;
        }

        @Override
        public String toString() {
            return "Mover disco " + diskSize + " de torre " + (fromTower + 1) + " a torre " + (toTower + 1);
        }
    }

    /**
     * Constructor con número de discos
     * @param numDisks número de discos
     */
    public HanoiLogic(int numDisks) {
        if (numDisks < 3 || numDisks > 10) {
            throw new IllegalArgumentException("El número de discos debe estar entre 3 y 10");
        }

        this.numDisks = numDisks;
        this.towers = new Tower[3];
        this.moves = new ArrayList<>();
        this.optimalSolution = new ArrayList<>();
        this.steps = 0;

        initializeTowers();
        calculateOptimalSolution();
    }

    /**
     * Inicializa las torres con los discos en la primera torre
     */
    public void initializeTowers() {
        // Crear las torres
        for (int i = 0; i < 3; i++) {
            towers[i] = new Tower(i, numDisks);
        }

        // Colocar discos en la primera torre (de mayor a menor)
        for (int i = numDisks; i > 0; i--) {
            towers[0].push(new Disk(i));
        }

        // Limpiar lista de movimientos
        moves.clear();
    }

    /**
     * Calcula la solución óptima (secuencia de movimientos)
     */
    private void calculateOptimalSolution() {
        optimalSolution.clear();
        solveRecursive(numDisks, 0, 2, 1);
    }

    /**
     * Método recursivo para calcular la solución óptima
     * @param n número de discos a mover
     * @param from torre origen
     * @param to torre destino
     * @param aux torre auxiliar
     */
    private void solveRecursive(int n, int from, int to, int aux) {
        if (n == 1) {
            optimalSolution.add(new Move(from, to, 1));
        } else {
            solveRecursive(n - 1, from, aux, to);
            optimalSolution.add(new Move(from, to, n));
            solveRecursive(n - 1, aux, to, from);
        }
    }

    /**
     * Resuelve el problema automáticamente
     * @return true si se resolvió correctamente
     */
    public boolean solve() {
        // Reiniciar torres
        initializeTowers();
        moves.clear();

        // Ejecutar todos los movimientos de la solución óptima
        for (Move move : optimalSolution) {
            moveDisk(move.getFromTower(), move.getToTower());
            steps++;
        }

        return isComplete();
    }

    /**
     * Realiza un paso de la solución automática
     * @param currentStep paso actual (índice en la solución óptima)
     * @return true si se realizó el paso correctamente
     */
    public boolean step(int currentStep) {
        if (currentStep >= 0 && currentStep < optimalSolution.size()) {
            Move move = optimalSolution.get(currentStep);
            boolean result = moveDisk(move.getFromTower(), move.getToTower());
            if (result) {
                steps++;
            }
            return result;
        }
        return false;
    }

    /**
     * Mueve un disco de una torre a otra
     * @param fromTower torre origen (0, 1 o 2)
     * @param toTower torre destino (0, 1 o 2)
     * @return true si el movimiento fue válido
     */
    public boolean moveDisk(int fromTower, int toTower) {
        if (fromTower < 0 || fromTower > 2 || toTower < 0 || toTower > 2 || fromTower == toTower) {
            return false;
        }

        Tower from = towers[fromTower];
        Tower to = towers[toTower];

        // Verificar si hay disco para mover
        if (from.isEmpty()) {
            return false;
        }

        // Verificar si se puede colocar en la torre destino
        Disk disk = from.peek();
        if (!to.canPlaceDisk(disk)) {
            return false;
        }

        // Realizar el movimiento
        disk = from.pop();
        to.push(disk);

        // Registrar el movimiento
        moves.add(new Move(fromTower, toTower, disk.getSize()));

        return true;
    }

    /**
     * Verifica si el puzzle está completo (todos los discos en la tercera torre)
     * @return true si está completo
     */
    public boolean isComplete() {
        return towers[2].getSize() == numDisks;
    }

    /**
     * Verifica si la solución actual es óptima
     * @return true si se usó el mínimo número de movimientos
     */
    public boolean isOptimalSolution() {
        return moves.size() == getMinimumMoves();
    }

    /**
     * Verifica si la configuración actual es válida
     * @return true si la configuración es válida
     */
    public boolean isValidConfiguration() {
        // Contar el total de discos en todas las torres
        int totalDisks = 0;
        for (Tower tower : towers) {
            totalDisks += tower.getSize();
        }

        // Verificar que hay el número correcto de discos
        if (totalDisks != numDisks) {
            return false;
        }

        // Verificar que las torres tienen discos en orden correcto
        for (Tower tower : towers) {
            Disk[] disks = tower.getDisks();
            for (int i = 0; i < disks.length - 1; i++) {
                if (disks[i].getSize() < disks[i + 1].getSize()) {
                    return false; // Disco más pequeño debajo de uno más grande
                }
            }
        }

        return true;
    }

    /**
     * Obtiene el número de discos
     * @return número de discos
     */
    public int getNumDisks() {
        return numDisks;
    }

    /**
     * Obtiene las torres
     * @return array con las tres torres
     */
    public Tower[] getTowers() {
        return towers;
    }

    /**
     * Obtiene la lista de movimientos realizados
     * @return lista de movimientos
     */
    public List<Move> getMoves() {
        return new ArrayList<>(moves);
    }

    /**
     * Obtiene la solución óptima
     * @return lista con los movimientos de la solución óptima
     */
    public List<Move> getOptimalSolution() {
        return new ArrayList<>(optimalSolution);
    }

    /**
     * Obtiene el número mínimo de movimientos necesarios
     * @return número mínimo de movimientos (2^n - 1)
     */
    public int getMinimumMoves() {
        return (int) Math.pow(2, numDisks) - 1;
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
}