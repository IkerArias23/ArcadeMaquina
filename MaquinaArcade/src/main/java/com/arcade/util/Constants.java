package com.arcade.util;

/**
 * Clase de constantes para la aplicación
 * Proporciona valores comunes utilizados en diferentes partes del programa
 */
public final class Constants {

    // Evitar instanciación
    private Constants() {
        throw new AssertionError("No se deben crear instancias de esta clase");
    }

    // Información de aplicación
    public static final String APP_NAME = "Máquina Arcade de Lógica";
    public static final String APP_VERSION = "1.0.0";
    public static final String APP_AUTHOR = "Técnicas de Programación";

    // Dimensiones UI
    public static final int MAIN_WINDOW_WIDTH = 800;
    public static final int MAIN_WINDOW_HEIGHT = 600;
    public static final int GAME_WINDOW_WIDTH = 800;
    public static final int GAME_WINDOW_HEIGHT = 600;
    public static final int DIALOG_WIDTH = 400;
    public static final int DIALOG_HEIGHT = 300;

    // Dimensiones tableros
    public static final int CELL_SIZE = 60;
    public static final int CHESS_BOARD_DEFAULT_SIZE = 8;

    // Valores por defecto de juegos
    public static final int DEFAULT_QUEENS_SIZE = 8;
    public static final int DEFAULT_KNIGHT_SIZE = 8;
    public static final int DEFAULT_HANOI_DISKS = 5;
    public static final int MIN_QUEENS_SIZE = 4;
    public static final int MAX_QUEENS_SIZE = 12;
    public static final int MIN_KNIGHT_SIZE = 5;
    public static final int MAX_KNIGHT_SIZE = 8;
    public static final int MIN_HANOI_DISKS = 3;
    public static final int MAX_HANOI_DISKS = 10;

    // Parámetros de animación
    public static final int ANIMATION_DURATION_MS = 500;
    public static final int SOLUTION_STEP_DELAY_MS = 500;

    // Nombres de archivos de recursos
    public static final String CSS_MAIN = "/css/main.css";
    public static final String IMAGE_QUEEN = "/images/queen.png";
    public static final String IMAGE_KNIGHT = "/images/knight.png";
    public static final String IMAGE_DISK = "/images/disk.png";

    // Nombres de archivos FXML
    public static final String FXML_MAIN = "/fxml/main.fxml";
    public static final String FXML_QUEENS = "/fxml/queens.fxml";
    public static final String FXML_KNIGHT = "/fxml/knight.fxml";
    public static final String FXML_HANOI = "/fxml/hanoi.fxml";
    public static final String FXML_HISTORY = "/fxml/history.fxml";

    // Mensajes de error comunes
    public static final String ERROR_BOARD_SIZE = "Tamaño del tablero no válido";
    public static final String ERROR_INVALID_POSITION = "Posición fuera del tablero";
    public static final String ERROR_INVALID_MOVE = "Movimiento no válido";
    public static final String ERROR_HANOI_INVALID_MOVE = "No se puede colocar un disco más grande sobre uno más pequeño";
    public static final String ERROR_DATABASE = "Error en la base de datos";
    public static final String ERROR_LOAD_FXML = "Error al cargar interfaz";
    public static final String ERROR_LOAD_RESOURCE = "Error al cargar recurso";

    // Textos informativos
    public static final String INFO_GAME_SAVED = "Partida guardada correctamente";
    public static final String INFO_GAME_COMPLETED = "¡Felicidades! Has completado el juego";
    public static final String INFO_NO_MORE_STEPS = "No hay más pasos disponibles";

    // Parámetros de base de datos
    public static final String DB_DRIVER = "org.h2.Driver";
    public static final String DB_URL = "jdbc:h2:./arcadedb;DB_CLOSE_DELAY=-1";
    public static final String DB_USERNAME = "sa";
    public static final String DB_PASSWORD = "";

    // Hibernate
    public static final String HIBERNATE_DIALECT = "org.hibernate.dialect.H2Dialect";
    public static final String HIBERNATE_SHOW_SQL = "true";
    public static final String HIBERNATE_FORMAT_SQL = "true";
    public static final String HIBERNATE_HBM2DDL_AUTO = "update";

    /**
     * Calcula el número mínimo de movimientos para resolver Torres de Hanoi
     * @param disks número de discos
     * @return número mínimo de movimientos (2^n - 1)
     */
    public static int calculateMinimumHanoiMoves(int disks) {
        return (int) Math.pow(2, disks) - 1;
    }
}