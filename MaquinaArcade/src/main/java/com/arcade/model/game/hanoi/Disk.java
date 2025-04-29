package com.arcade.model.game.hanoi;

/**
 * Clase que representa un disco en el juego de Torres de Hanoi
 * Los discos tienen un tamaño y solo pueden apilarse sobre discos más grandes
 */
public class Disk {

    private final int size;

    /**
     * Constructor con tamaño
     * @param size tamaño del disco (1 es el más pequeño)
     */
    public Disk(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("El tamaño del disco debe ser positivo");
        }
        this.size = size;
    }

    /**
     * Obtiene el tamaño del disco
     * @return tamaño del disco
     */
    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "Disco[" + size + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Disk disk = (Disk) obj;
        return size == disk.size;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(size);
    }
}