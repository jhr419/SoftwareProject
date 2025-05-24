module com.shelton.ebu6403.ledgerapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires org.json;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    opens com.shelton.ebu6403 to javafx.fxml, javafx.graphics;
    exports com.shelton.ebu6403;
    exports com.shelton.ebu6403.controller;
    exports com.shelton.ebu6403.models;
    opens com.shelton.ebu6403.models to org.junit.platform.commons;

    opens com.shelton.ebu6403.controller to javafx.fxml, javafx.graphics;
    opens com.shelton.ebu6403.images to javafx.graphics;
    opens com.shelton.ebu6403.styles to javafx.graphics;
    opens com.shelton.ebu6403.views to javafx.fxml;

}

