package com.arcade.model.game.hanoi;

import java.util.Stack;

/**
 * Clase que representa una torre en el juego de Torres de Hanoi
 * Utiliza una pila (Stack) para almacenar los discos
 */
public class Tower {

    private final int id; // Identificador de la torre (0, 1, 2)
    private final Stack<Disk> disks; // Pila de discos
    private final int maxDisks; // Número máximo de discos

    /**
     * Constructor con identificador y máximo número de discos
     * @param id identificador de la torre
     * @param maxDisks número máximo de discos
     */
    public Tower(int id, int maxDisks) {
        this.id = id;
        this.maxDisks = maxDisks;
        this.disks = new Stack<>();
    }

    /**
     * Obtiene el identificador de la torre
     * @return id de la torre
     */
    public int getId() {
        return id;
    }

    /**
     * Comprueba si la torre está vacía
     * @return true si no tiene discos
     */
    public boolean isEmpty() {
        return disks.isEmpty();
    }

    /**
     * Comprueba si la torre está llena
     * @return true si tiene el máximo de discos
     */
    public boolean isFull() {
        return disks.size() >= maxDisks;
    }

    /**
     * Obtiene el número de discos en la torre
     * @return cantidad de discos
     */
    public int getSize() {
        return disks.size();
    }

    /**
     * Obtiene el disco de la parte superior sin quitarlo
     * @return disco superior o null si está vacía
     */
    public Disk peek() {
        return disks.isEmpty() ? null : disks.peek();
    }

    /**
     * Quita y devuelve el disco de la parte superior
     * @return disco superior
     * @throws IllegalStateException si la torre está vacía
     */
    public Disk pop() {
        if (disks.isEmpty()) {
            throw new IllegalStateException("No hay discos en la torre " + id);
        }
        return disks.pop();
    }

    /**
     * Coloca un disco en la parte superior
     * @param disk disco a colocar
     * @throws IllegalStateException si el movimiento no es válido
     */
    public void push(Disk disk) {
        if (disk == null) {
            throw new IllegalArgumentException("No se puede colocar un disco nulo");
        }

        if (!disks.isEmpty() && disk.getSize() > disks.peek().getSize()) {
            throw new IllegalStateException(
                    "No se puede colocar un disco más grande sobre uno más pequeño");
        }

        disks.push(disk);
    }

    /**
     * Comprueba si se puede colocar un disco en esta torre
     * @param disk disco a colocar
     * @return true si el movimiento es válido
     */
    public boolean canPlaceDisk(Disk disk) {
        return disk != null &&
                (disks.isEmpty() || disk.getSize() < disks.peek().getSize());
    }

    /**
     * Obtiene una copia de los discos en la torre
     * @return array con los discos (el índice 0 es la base)
     */
    public Disk[] getDisks() {
        Disk[] result = new Disk[disks.size()];
        for (int i = 0; i < disks.size(); i++) {
            result[i] = disks.get(i);
        }
        return result;
    }

    /**
     * Crea una copia de esta torre
     * @return copia de la torre
     */
    public Tower copy() {
        Tower copy = new Tower(id, maxDisks);
        for (Disk disk : disks) {
            copy.disks.push(new Disk(disk.getSize()));
        }
        return copy;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Torre ").append(id + 1).append(": ");

        if (disks.isEmpty()) {
            sb.append("vacía");
        } else {
            sb.append("[");
            for (Disk disk : disks) {
                sb.append(disk.getSize()).append(" ");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("]");
        }

        return sb.toString();
    }
}