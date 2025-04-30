package com.arcade;

import com.arcade.config.HibernateConfig;
import com.arcade.util.FXMLLoader;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Clase principal de la aplicación Arcade
 * Implementa el patrón Singleton para asegurar una única instancia
 * de la aplicación en ejecución.
 */
public class ArcadeApplication extends Application {

    private static ArcadeApplication instance;
    private Stage primaryStage;

    /**
     * Constructor público sin argumentos,
     * necesario para que JavaFX pueda instanciar esta clase.
     */
    public ArcadeApplication() {
        // nada aquí, JavaFX lo instancia por reflexión
    }

    /**
     * Método para obtener la instancia única de la aplicación
     * @return instancia de ArcadeApplication
     */
    public static ArcadeApplication getInstance() {
        return instance;
    }

    /**
     * Punto de entrada principal de la aplicación
     * @param args argumentos de línea de comandos
     */
    public static void main(String[] args) {
        // Inicializar Hibernate antes de iniciar la aplicación
        HibernateConfig.getInstance().getSessionFactory();
        launch(args);
    }

    /**
     * Método de inicialización de JavaFX
     * @param primaryStage escenario principal
     */
    @Override
    public void start(Stage primaryStage) {
        // Guardamos la referencia y fijamos la instancia singleton:
        this.primaryStage = primaryStage;
        instance = this;

        this.primaryStage.setTitle("Máquina Arcade de Lógica");

        try {
            // Cargar la vista principal
            BorderPane root = (BorderPane) FXMLLoader.load(
                    getClass().getResource("/fxml/main.fxml")
            );
            Scene scene = new Scene(root, 800, 600);
            scene.getStylesheets().add(
                    getClass().getResource("/css/main.css").toExternalForm()
            );

            primaryStage.setScene(scene);
            primaryStage.setMinWidth(800);
            primaryStage.setMinHeight(600);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que devuelve el escenario principal
     * @return primaryStage
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Método de cierre de la aplicación
     */
    @Override
    public void stop() {
        // Cerrar la conexión con la base de datos
        HibernateConfig.getInstance().shutdown();
    }
    
}
