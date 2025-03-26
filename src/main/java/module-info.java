module com.example.assetmanagementsystem {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.assetmanagementsystem to javafx.fxml;
    exports com.example.assetmanagementsystem;
}