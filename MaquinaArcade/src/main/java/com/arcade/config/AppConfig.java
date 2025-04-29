package com.arcade.config;

/**
 * Configuración general de la aplicación
 * Proporciona constantes y configuraciones globales
 *
 * Esta clase implementa el patrón Singleton para garantizar
 * una única instancia de configuración en toda la aplicación
 */
public class AppConfig {

    private static AppConfig instance;

    // Versión de la aplicación
    private final String appVersion = "1.0.0";

    // Directorio de recursos
    private final String resourcesDir = "/resources/";

    // Tamaños por defecto
    private final int defaultQueensSize = 8;
    private final int defaultKnightSize = 8;
    private final int defaultHanoiDisks = 5;

    // Dimensiones UI
    private final int mainWindowWidth = 800;
    private final int mainWindowHeight = 600;

    /**
     * Constructor privado para evitar instanciación directa (Singleton)
     */
    private AppConfig() {
        // Inicialización de configuraciones adicionales
    }

    /**
     * Método para obtener la instancia única de configuración
     * @return instancia de AppConfig
     */
    public static synchronized AppConfig getInstance() {
        if (instance == null) {
            instance = new AppConfig();
        }
        return instance;
    }

    // Getters para los valores de configuración

    public String getAppVersion() {
        return appVersion;
    }

    public String getResourcesDir() {
        return resourcesDir;
    }

    public int getDefaultQueensSize() {
        return defaultQueensSize;
    }

    public int getDefaultKnightSize() {
        return defaultKnightSize;
    }

    public int getDefaultHanoiDisks() {
        return defaultHanoiDisks;
    }

    public int getMainWindowWidth() {
        return mainWindowWidth;
    }

    public int getMainWindowHeight() {
        return mainWindowHeight;
    }

    /**
     * Calcula el número mínimo de movimientos para resolver Torres de Hanoi
     * @param disks número de discos
     * @return número mínimo de movimientos (2^n - 1)
     */
    public static int calculateMinimumHanoiMoves(int disks) {
        return (int) Math.pow(2, disks) - 1;
    }
}