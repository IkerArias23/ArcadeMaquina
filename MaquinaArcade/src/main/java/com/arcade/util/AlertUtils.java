package com.arcade.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

/**
 * Utilidades para mostrar alertas y diálogos
 * Proporciona métodos para mostrar distintos tipos de mensajes al usuario
 */
public final class AlertUtils {

    // Evitar instanciación
    private AlertUtils() {
        throw new AssertionError("No se deben crear instancias de esta clase");
    }

    /**
     * Muestra un mensaje informativo
     * @param title título de la ventana
     * @param header encabezado (null para omitir)
     * @param message mensaje a mostrar
     */
    public static void showInfo(String title, String header, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Muestra un mensaje de advertencia
     * @param title título de la ventana
     * @param header encabezado (null para omitir)
     * @param message mensaje a mostrar
     */
    public static void showWarning(String title, String header, String message) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Muestra un mensaje de error
     * @param title título de la ventana
     * @param header encabezado (null para omitir)
     * @param message mensaje a mostrar
     */
    public static void showError(String title, String header, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Muestra un mensaje de error con detalles de excepción
     * @param title título de la ventana
     * @param header encabezado (null para omitir)
     * @param message mensaje a mostrar
     * @param e excepción que ha ocurrido
     */
    public static void showException(String title, String header, String message, Exception e) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);

        // Crear área de texto para el stack trace
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String exceptionText = sw.toString();

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(textArea, 0, 0);

        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    /**
     * Muestra un diálogo de confirmación
     * @param title título de la ventana
     * @param header encabezado (null para omitir)
     * @param message mensaje a mostrar
     * @return true si el usuario confirma, false si cancela
     */
    public static boolean showConfirmation(String title, String header, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Muestra un diálogo de decisión con botones personalizados
     * @param title título de la ventana
     * @param header encabezado (null para omitir)
     * @param message mensaje a mostrar
     * @param options botones a mostrar
     * @return botón seleccionado o null si se cerró la ventana
     */
    public static ButtonType showChoice(String title, String header, String message, ButtonType... options) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.getButtonTypes().setAll(options);

        Optional<ButtonType> result = alert.showAndWait();
        return result.orElse(null);
    }

    /**
     * Centra una alerta en la ventana padre
     * @param alert alerta a centrar
     * @param owner ventana padre
     */
    public static void centerAlert(Alert alert, Stage owner) {
        if (owner != null) {
            // Posiciona la alerta después de que se haya mostrado
            alert.setOnShown(e -> {
                Stage dialogStage = (Stage) alert.getDialogPane().getScene().getWindow();
                dialogStage.setX(owner.getX() + owner.getWidth() / 2 - dialogStage.getWidth() / 2);
                dialogStage.setY(owner.getY() + owner.getHeight() / 2 - dialogStage.getHeight() / 2);
            });
        }
    }

    /**
     * Configura un estilo CSS para una alerta
     * @param alert alerta a configurar
     * @param cssFile archivo CSS a usar
     */
    public static void setAlertStyle(Alert alert, String cssFile) {
        alert.getDialogPane().getStylesheets().add(
                AlertUtils.class.getResource(cssFile).toExternalForm()
        );
    }
}