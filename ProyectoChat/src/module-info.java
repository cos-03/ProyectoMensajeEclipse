module ProyectoChat {

    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;
    requires java.sql;
    requires javafx.graphics;

    opens application to javafx.graphics, javafx.fxml;
}
