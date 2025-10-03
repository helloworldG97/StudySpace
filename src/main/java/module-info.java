module studyspace {
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires javafx.web;
    requires java.desktop;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    // Export main packages
    exports com.studyspace;
    exports com.studyspace.models;
    exports com.studyspace.auth;
    exports com.studyspace.utils;
    exports com.studyspace.components;
    exports com.studyspace.views;

    // Open packages for FXML reflection
    opens com.studyspace to javafx.fxml;
    opens com.studyspace.auth to javafx.fxml;
    opens com.studyspace.models to javafx.fxml;
    opens com.studyspace.components to javafx.fxml;
    opens com.studyspace.views to javafx.fxml;
}