module org.example.marblecity {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;

    opens org.example.marblecity to javafx.fxml;
    exports org.example.marblecity;
}