package com.arcade.view.components;

import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Componente que implementa un tablero para el juego de Torres de Hanói
 * Visualiza las torres y discos, y permite la interacción
 */
public class HanoiBoard extends Pane {

    private int numDisks;
    private List<Rectangle> towers;
    private List<List<Rectangle>> disks;
    private Consumer<Integer> towerClickHandler;

    // Constantes de dimensiones
    private static final int TOWER_WIDTH = 20;
    private static final int TOWER_HEIGHT = 200;
    private static final int TOWER_SPACING = 200;
    private static final int BASE_WIDTH = 600;
    private static final int BASE_HEIGHT = 20;
    private static final int DISK_HEIGHT = 20;
    private static final int MAX_DISK_WIDTH = 180;

    // Colores
    private static final Color TOWER_COLOR = Color.BROWN;
    private static final Color BASE_COLOR = Color.BROWN;
    private static final Color HIGHLIGHT_COLOR = Color.YELLOW;

    /**
     * Constructor con número de discos
     * @param numDisks número de discos
     */
    public HanoiBoard(int numDisks) {
        this.numDisks = numDisks;
        this.towers = new ArrayList<>(3);
        this.disks = new ArrayList<>(3);

        for (int i = 0; i < 3; i++) {
            disks.add(new ArrayList<>());
        }

        initialize();
    }

    /**
     * Inicializa el tablero
     */
    private void initialize() {
        // Configurar panel
        this.setPrefSize(BASE_WIDTH + 100, TOWER_HEIGHT + BASE_HEIGHT + 50);
        this.setMinSize(BASE_WIDTH + 100, TOWER_HEIGHT + BASE_HEIGHT + 50);

        // Crear base
        Rectangle base = new Rectangle(0, TOWER_HEIGHT, BASE_WIDTH, BASE_HEIGHT);
        base.setFill(BASE_COLOR);
        this.getChildren().add(base);

        // Crear torres
        for (int i = 0; i < 3; i++) {
            int xPos = i * TOWER_SPACING + (TOWER_SPACING / 2) - (TOWER_WIDTH / 2);

            Rectangle tower = new Rectangle(xPos, 0, TOWER_WIDTH, TOWER_HEIGHT);
            tower.setFill(TOWER_COLOR);

            // Agregar evento de clic
            final int towerIndex = i;
            tower.setOnMouseClicked(event -> {
                if (towerClickHandler != null) {
                    towerClickHandler.accept(towerIndex);
                }
            });

            this.getChildren().add(tower);
            towers.add(tower);
        }

        // Crear discos iniciales en la primera torre
        createInitialDisks();
    }

    /**
     * Crea los discos iniciales en la primera torre
     */
    private void createInitialDisks() {
        // Limpiar discos existentes
        for (List<Rectangle> towerDisks : disks) {
            for (Rectangle disk : towerDisks) {
                this.getChildren().remove(disk);
            }
            towerDisks.clear();
        }

        // Crear nuevos discos en la primera torre
        for (int i = numDisks; i > 0; i--) {
            createDisk(0, i);
        }
    }

    /**
     * Crea un disco en una torre
     * @param tower índice de la torre (0, 1 o 2)
     * @param size tamaño del disco (1 es el más pequeño)
     */
    private Rectangle createDisk(int tower, int size) {
        int diskWidth = calculateDiskWidth(size);
        int xPos = tower * TOWER_SPACING + (TOWER_SPACING / 2) - (diskWidth / 2);
        int yPos = TOWER_HEIGHT - BASE_HEIGHT - (disks.get(tower).size() + 1) * DISK_HEIGHT;

        Rectangle disk = new Rectangle(xPos, yPos, diskWidth, DISK_HEIGHT);
        disk.setFill(getDiskColor(size));
        disk.setArcWidth(10);
        disk.setArcHeight(10);

        // Agregar disco a la torre
        disks.get(tower).add(disk);
        this.getChildren().add(disk);

        return disk;
    }

    /**
     * Actualiza el estado de las torres
     * @param towerDisks arreglo con los discos en cada torre
     */
    public void updateTowers(int[][] towerDisks) {
        // Limpiar discos existentes
        for (List<Rectangle> towerDisks1 : disks) {
            for (Rectangle disk : towerDisks1) {
                this.getChildren().remove(disk);
            }
            towerDisks1.clear();
        }

        // Crear discos según el estado actual
        for (int i = 0; i < 3; i++) {
            if (towerDisks[i] != null) {
                for (int j = towerDisks[i].length - 1; j >= 0; j--) {
                    createDisk(i, towerDisks[i][j]);
                }
            }
        }
    }

    /**
     * Cambia el número de discos
     * @param numDisks nuevo número de discos
     */
    public void setNumDisks(int numDisks) {
        this.numDisks = numDisks;
        createInitialDisks();
    }

    /**
     * Resalta una torre
     * @param tower índice de la torre (0, 1 o 2)
     */
    public void highlightTower(int tower) {
        // Restaurar colores originales
        for (Rectangle t : towers) {
            t.setFill(TOWER_COLOR);
        }

        // Resaltar torre seleccionada
        if (tower >= 0 && tower < 3) {
            towers.get(tower).setFill(HIGHLIGHT_COLOR);
        }
    }

    /**
     * Calcula el ancho de un disco según su tamaño
     * @param size tamaño del disco (1 es el más pequeño)
     * @return ancho del disco
     */
    private int calculateDiskWidth(int size) {
        // El disco más grande ocupa casi todo el ancho disponible
        // Los demás discos se escalan según su tamaño relativo
        return (int) (MAX_DISK_WIDTH * (0.3 + (0.7 * size / numDisks)));
    }

    /**
     * Obtiene el color para un disco según su tamaño
     * @param size tamaño del disco
     * @return color
     */
    private Color getDiskColor(int size) {
        // Usar una escala de colores según el tamaño del disco
        double hue = (360.0 * size / numDisks) % 360;
        return Color.hsb(hue, 0.7, 0.9);
    }

    /**
     * Establece el manejador de clics en torres
     * @param handler función que recibe el índice de la torre
     */
    public void setOnTowerClick(Consumer<Integer> handler) {
        this.towerClickHandler = handler;
    }
}