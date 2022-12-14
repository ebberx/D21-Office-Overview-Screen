/**
 * Module definition of the application
 * @author Esben Christensen
 */
module Application {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.media;
    requires javafx.base;
    requires java.sql;

    requires junit;

    exports Application;
    exports Test;
    exports Domain;
    exports Foundation;
}