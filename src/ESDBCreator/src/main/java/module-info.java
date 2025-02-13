module dbcreator {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    opens dbcreator to javafx.fxml;
    exports dbcreator;
    requires java.sql;
}