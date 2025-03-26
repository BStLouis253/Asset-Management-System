package com.example.assetmanagementsystem;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class AssetManagementSystem extends Application {

    private AssetManager assetManager = new AssetManager();

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Asset Management System");

        // Create a TableView to display assets
        TableView<Asset> assetTable = new TableView<>();
        TableColumn<Asset, String> nameColumn = new TableColumn<>("Asset Name");
        nameColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getName()));
        assetTable.getColumns().add(nameColumn);

        // Button to add a new asset
        Button addButton = new Button("Add Asset");
        addButton.setOnAction(e -> {
            // Sample: Add a new hardware asset with random number in the name at the end. Sampe for prototype purposes.
            Random random = new Random();
            assetManager.addAsset(new HardwareAsset("001", "Laptop" + random.nextInt(1000), "Asus", "M160", "2024-05-22", true, "Office", "2027-05-22"));
            assetTable.getItems().clear();
            assetTable.getItems().addAll(assetManager.getAssets());
        });

        // Button to load assets from file
        Button loadAssetsButton = new Button("Load Assets");
        loadAssetsButton.setOnAction(e -> {
            try {
                ArrayList<Asset> loadedAssets = FileManager.loadAssetsFromFile("SavedAssets.txt");
                assetManager.getAssets().clear();
                assetManager.getAssets().addAll(loadedAssets);
                assetTable.getItems().clear();
                assetTable.getItems().addAll(assetManager.getAssets());
            } catch (IOException ex) {
                ex.printStackTrace();
                showErrorMessage("Error loading assets from file.");
            }
        });

        // Button to save assets to file
        Button saveAssetsButton = new Button("Save Assets");
        saveAssetsButton.setOnAction(e -> {
            try {
                FileManager.saveAssetsToFile(assetManager.getAssets(), "SavedAssets.txt");
            } catch (IOException ex) {
                ex.printStackTrace();
                showErrorMessage("Error saving assets to file.");
            }
        });

        // Sort By Name Button
        Button sortByNameButton = new Button("Sort By Name");
        sortByNameButton.setOnAction(e -> {
            assetManager.sortAssets("name");  // Sorts assets by name
            assetTable.getItems().clear();
            assetTable.getItems().addAll(assetManager.getAssets());
        });

        // Layout
        VBox vbox = new VBox();
        vbox.getChildren().addAll(addButton, loadAssetsButton, saveAssetsButton, sortByNameButton, assetTable);

        Scene scene = new Scene(vbox);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}