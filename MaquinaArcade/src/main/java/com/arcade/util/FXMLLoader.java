package com.arcade.util;


import javafx.scene.Parent;

import java.io.IOException;
import java.net.URL;

/**
 * Utilidad para cargar archivos FXML
 * Implementa el patrón Decorator para extender la funcionalidad del FXMLLoader de JavaFX
 */
public class FXMLLoader {

    /**
     * Carga un archivo FXML
     * @param location URL del archivo FXML
     * @return nodo raíz cargado
     * @throws IOException si hay un error al cargar el archivo
     */
    public static Parent load(URL location) throws IOException {
        // Crear el cargador
        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(location);

        // Añadir funcionalidad adicional al cargador si es necesario
        // Por ejemplo, establecer un controlador de excepciones personalizado
        loader.setControllerFactory(controllerClass -> {
            try {
                return controllerClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Error al crear controlador: " + controllerClass.getName(), e);
            }
        });

        // Cargar el archivo FXML
        return loader.load();
    }

    /**
     * Carga un archivo FXML con un controlador específico
     * @param location URL del archivo FXML
     * @param controller controlador para el FXML
     * @return nodo raíz cargado
     * @throws IOException si hay un error al cargar el archivo
     */
    public static Parent load(URL location, Object controller) throws IOException {
        // Crear el cargador
        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(location);

        // Establecer el controlador
        loader.setController(controller);

        // Cargar el archivo FXML
        return loader.load();
    }

    /**
     * Carga un archivo FXML y devuelve el cargador
     * Útil cuando se necesita acceder al controlador después de cargar
     * @param location URL del archivo FXML
     * @return cargador FXML
     * @throws IOException si hay un error al cargar el archivo
     */
    public static javafx.fxml.FXMLLoader getLoader(URL location) throws IOException {
        // Crear el cargador
        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(location);

        // Configurar el cargador
        loader.setControllerFactory(controllerClass -> {
            try {
                return controllerClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Error al crear controlador: " + controllerClass.getName(), e);
            }
        });

        // Cargar el archivo FXML
        loader.load();

        return loader;
    }

    /**
     * Obtiene el controlador de un cargador FXML
     * @param <T> tipo del controlador
     * @param loader cargador FXML
     * @return controlador
     */
    public static <T> T getController(javafx.fxml.FXMLLoader loader) {
        return loader.getController();
    }
}