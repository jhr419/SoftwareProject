module com.shelton.ebu6403.ledgerapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens com.shelton.ebu6403 to javafx.fxml;
    exports com.shelton.ebu6403;
}