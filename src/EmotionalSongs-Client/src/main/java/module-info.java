/**
 * Modulo basato su JavaFX che fornisce le dipendenze e i package necessari
 * per l'avvio dell'applicazione.
 */
module emotionalsongs {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    opens emotionalsongs to javafx.fxml,javafx.graphics;
    exports emotionalsongs;
    requires transitive javafx.graphics;
    requires javafx.base;
    opens util to javafx.graphics, javafx.fxml,javafx.scene;
    exports util;
    exports client;
    exports interfacce;
    opens client to javafx.fxml, javafx.graphics;
    requires java.sql;
    requires java.rmi;
}