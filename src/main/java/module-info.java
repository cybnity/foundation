module com.cybnity.ui {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.cybnity.ui to javafx.fxml;
    exports com.cybnity.ui;
}
