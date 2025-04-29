package com.arcade.model.game.hanoi;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.arcade.model.entity.GameRecord;
import com.arcade.model.entity.HanoiRecord;
import com.arcade.model.game.AbstractGame;

/**
 * Implementación del juego de las Torres de Hanói
 * Permite resolver el problema de mover una torre de discos
 * de un poste a otro siguiendo reglas específicas
 */
public class HanoiGame extends AbstractGame {

    private int numDisks;
    private List<Stack<Disk>> towers;
    private List<Move> moves;
    private List<Move> optimalSolution;
    private int currentMoveIndex;

    /**
     * Representa un movimiento de disco entre torres
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
     * Constructor por defecto
     */
    public HanoiGame() {
        super("Torres de Hanói", "Mueve la torre de discos de un poste a otro");
        this.towers = new ArrayList<>(3);
        this.moves = new ArrayList<>();
        this.optimalSolution = new ArrayList<>();
    }

    @Override
    public void initialize(Object... params) {
        super.initialize(params);

        if (params.length < 1 || !(params[0] instanceof Integer)) {
            throw new IllegalArgumentException("Se requiere el número de discos");
        }

        this.numDisks = (Integer) params[0];

        if (numDisks < 3 || numDisks > 10) {
            throw new IllegalArgumentException("El número de discos debe estar entre 3 y 10");
        }

        // Inicializar torres
        this.towers.clear();
        for (int i = 0; i < 3; i++) {
            this.towers.add(new Stack<>());
        }

        // Colocar discos en la primera torre
        for (int i = numDisks; i > 0; i--) {
            this.towers.get(0).push(new Disk(i));
        }

        this.moves.clear();
        this.optimalSolution.clear();
        this.currentMoveIndex = 0;

        // Calcular la solución óptima
        calculateOptimalSolution(numDisks, 0, 2, 1);
    }

    /**
     * Calcula recursivamente la solución óptima (secuencia de movimientos)
     * @param n número de discos a mover
     * @param from torre origen
     * @param to torre destino
     * @param aux torre auxiliar
     */
    private void calculateOptimalSolution(int n, int from, int to, int aux) {
        if (n == 1) {
            optimalSolution.add(new Move(from, to, 1));
        } else {
            calculateOptimalSolution(n - 1, from, aux, to);
            optimalSolution.add(new Move(from, to, n));
            calculateOptimalSolution(n - 1, aux, to, from);
        }
    }

    @Override
    public boolean solve() {
        if (!initialized) {
            throw new IllegalStateException("El juego no ha sido inicializado");
        }

        // Reiniciar el juego
        initialize(numDisks);

        // Ejecutar todos los movimientos de la solución óptima
        for (Move move : optimalSolution) {
            moveDisk(move.getFromTower(), move.getToTower());
        }

        setCompleted();
        return true;
    }

    @Override
    public boolean step() {
        if (!initialized) {
            throw new IllegalStateException("El juego no ha sido inicializado");
        }

        if (currentMoveIndex >= optimalSolution.size()) {
            return false; // No hay más pasos que mostrar
        }

        // Ejecutar el siguiente movimiento de la solución óptima
        Move move = optimalSolution.get(currentMoveIndex);
        moveDisk(move.getFromTower(), move.getToTower());
        currentMoveIndex++;

        // Comprobar si se ha completado el puzzle
        if (isGameComplete()) {
            setCompleted();
        }

        return true;
    }

    @Override
    public boolean isValidSolution() {
        // Comprobar que la torre destino tiene todos los discos correctamente apilados
        if (towers.get(2).size() != numDisks) {
            return false;
        }

        // Comprobar que los discos están en orden correcto (mayor a menor desde la base)
        Stack<Disk> destTower = towers.get(2);
        for (int i = 0; i < numDisks; i++) {
            if (destTower.get(i).getSize() != numDisks - i) {
                return false;
            }
        }

        return true;
    }

    @Override
    public GameRecord createRecord(boolean isCompleted) {
        return new HanoiRecord(
                numDisks,
                moves.size(),
                isCompleted,
                startTime,
                endTime
        );
    }

    /**
     * Obtiene el número de discos
     * @return número de discos
     */
    public int getNumDisks() {
        return numDisks;
    }

    /**
     * Obtiene el estado actual de las torres
     * @return lista con las tres torres
     */
    public List<Stack<Disk>> getTowers() {
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
     * Obtiene la solución óptima (lista de movimientos)
     * @return lista de movimientos de la solución óptima
     */
    public List<Move> getOptimalSolution() {
        return new ArrayList<>(optimalSolution);
    }

    /**
     * Intenta mover un disco de una torre a otra
     * @param fromTower torre origen (0, 1 o 2)
     * @param toTower torre destino (0, 1 o 2)
     * @return true si el movimiento fue válido
     */
    public boolean moveDisk(int fromTower, int toTower) {
        if (!initialized ||
                fromTower < 0 || fromTower > 2 ||
                toTower < 0 || toTower > 2 ||
                fromTower == toTower) {
            return false;
        }

        Stack<Disk> fromStack = towers.get(fromTower);
        Stack<Disk> toStack = towers.get(toTower);

        // No hay disco para mover
        if (fromStack.isEmpty()) {
            return false;
        }

        // No se puede colocar un disco más grande sobre uno más pequeño
        if (!toStack.isEmpty() && fromStack.peek().getSize() > toStack.peek().getSize()) {
            return false;
        }

        // Realizar el movimiento
        Disk disk = fromStack.pop();
        toStack.push(disk);

        // Registrar el movimiento
        moves.add(new Move(fromTower, toTower, disk.getSize()));
        steps++;

        // Comprobar si se ha completado el puzzle
        if (isGameComplete()) {
            setCompleted();
        }

        return true;
    }

    /**
     * Comprueba si el juego está completo (todos los discos en la tercera torre)
     * @return true si el juego está completo
     */
    private boolean isGameComplete() {
        return towers.get(2).size() == numDisks;
    }

    /**
     * Obtiene el número mínimo de movimientos necesarios para resolver el puzzle
     * @return número mínimo de movimientos (2^n - 1)
     */
    public int getMinimumMoves() {
        return (int) Math.pow(2, numDisks) - 1;
    }
}