package com.example.assetmanagementsystem;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class AssetManagementSystem extends Application {

    private final AssetManager assetManager = new AssetManager();
    private final TableView<Asset> assetTable = new TableView<>();
    private final Map<String, TableColumn<Asset, String>> columnMap = new TreeMap<>();
    private final Deque<List<Asset>> undoStack = new ArrayDeque<>();
    private final Deque<List<Asset>> redoStack = new ArrayDeque<>();
    private int nextAssetID = 1;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Asset Management System");

        MenuButton columnSelector = new MenuButton("Select Columns");

        createColumns(columnSelector);

        Button addHardwareButton = new Button("Add Hardware Asset");
        addHardwareButton.setOnAction(e -> showAssetInputDialog("hardware"));

        Button addSoftwareButton = new Button("Add Software Asset");
        addSoftwareButton.setOnAction(e -> showAssetInputDialog("software"));

        Button loadAssetsButton = new Button("Load Assets");
        loadAssetsButton.setOnAction(e -> {
            try {
                saveStateForUndo();
                List<Asset> loadedAssets = FileManager.loadAssetsFromFile("SavedAssets.txt");
                assetManager.getAssets().clear();
                assetManager.getAssets().addAll(loadedAssets);
                assetTable.setItems(FXCollections.observableArrayList(assetManager.getAssets()));
                updateNextAssetID();
            } catch (IOException ex) {
                showError("Error loading assets from file.");
            }
        });

        Button saveAssetsButton = new Button("Save Assets");
        saveAssetsButton.setOnAction(e -> {
            try {
                FileManager.saveAssetsToFile(assetManager.getAssets(), "SavedAssets.txt");
            } catch (IOException ex) {
                showError("Error saving assets to file.");
            }
        });

        Button undoButton = new Button("Undo");
        undoButton.setOnAction(e -> undo());

        Button redoButton = new Button("Redo");
        redoButton.setOnAction(e -> redo());

        Button deleteButton = new Button("Delete Selected Asset");
        deleteButton.setOnAction(e -> deleteSelectedAsset());

        HBox topBar = new HBox(10, addHardwareButton, addSoftwareButton, loadAssetsButton, saveAssetsButton,
                columnSelector, undoButton, redoButton, deleteButton);
        topBar.setPadding(new Insets(10));

        VBox root = new VBox(10, topBar, assetTable);
        root.setPadding(new Insets(10));
        VBox.setVgrow(assetTable, Priority.ALWAYS);

        primaryStage.setScene(new Scene(root, 1000, 600));
        primaryStage.show();
    }

    private void createColumns(MenuButton columnSelector) {
        columnMap.clear();
        assetTable.getColumns().clear();
        columnSelector.getItems().clear();

        addColumn("Active", asset -> String.valueOf(asset.isActive()), columnSelector);
        addColumn("Asset ID", Asset::getAssetID, columnSelector);
        addColumn("License Key", asset -> asset instanceof SoftwareAsset sa ? sa.getLicenseKey() : "", columnSelector);
        addColumn("Location", asset -> asset instanceof HardwareAsset ha ? ha.getLocation() : "", columnSelector);
        addColumn("Maintenance Date", asset -> asset instanceof HardwareAsset ha ? ha.getMaintenanceDate() : "", columnSelector);
        addColumn("Manufacturer", Asset::getManufacturer, columnSelector);
        addColumn("Model", Asset::getModel, columnSelector);
        addColumn("Name", Asset::getName, columnSelector);
        addColumn("Purchase Date", Asset::getPurchaseDate, columnSelector);
        addColumn("Version", asset -> asset instanceof SoftwareAsset sa ? sa.getVersion() : "", columnSelector);
    }

    private void addColumn(String name, Function<Asset, String> extractor, MenuButton selector) {
        TableColumn<Asset, String> col = new TableColumn<>(name);
        col.setCellValueFactory(cell -> new ReadOnlyStringWrapper(extractor.apply(cell.getValue())));
        columnMap.put(name, col);
        addColumnsAlphabetically();

        CheckMenuItem menuItem = new CheckMenuItem(name);
        menuItem.setSelected(true);
        menuItem.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected) {
                columnMap.put(name, col);
                addColumnsAlphabetically();
            } else {
                assetTable.getColumns().remove(col);
                columnMap.remove(name);
            }
        });
        selector.getItems().add(menuItem);
    }

    private void addColumnsAlphabetically() {
        List<String> sortedNames = new ArrayList<>(columnMap.keySet());
        Collections.sort(sortedNames);
        assetTable.getColumns().setAll(sortedNames.stream().map(columnMap::get).toList());
    }

    private void showAssetInputDialog(String type) {
        Dialog<Asset> dialog = new Dialog<>();
        dialog.setTitle("Add " + (type.equals("hardware") ? "Hardware" : "Software") + " Asset");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        String newID = String.format("%03d", nextAssetID++);

        TextField nameField = new TextField();
        TextField manufacturerField = new TextField();
        TextField modelField = new TextField();
        TextField purchaseDateField = new TextField();
        CheckBox activeCheckbox = new CheckBox();

        grid.add(new Label("Asset ID:"), 0, 0);
        grid.add(new Label(newID), 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Manufacturer:"), 0, 2);
        grid.add(manufacturerField, 1, 2);
        grid.add(new Label("Model:"), 0, 3);
        grid.add(modelField, 1, 3);
        grid.add(new Label("Purchase Date:"), 0, 4);
        grid.add(purchaseDateField, 1, 4);
        grid.add(new Label("Active:"), 0, 5);
        grid.add(activeCheckbox, 1, 5);

        TextField extraField1 = new TextField();
        TextField extraField2 = new TextField();

        if (type.equals("hardware")) {
            grid.add(new Label("Location:"), 0, 6);
            grid.add(extraField1, 1, 6);
            grid.add(new Label("Maintenance Date:"), 0, 7);
            grid.add(extraField2, 1, 7);
        } else {
            grid.add(new Label("Version:"), 0, 6);
            grid.add(extraField1, 1, 6);
            grid.add(new Label("License Key:"), 0, 7);
            grid.add(extraField2, 1, 7);
        }

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                saveStateForUndo();
                if (type.equals("hardware")) {
                    return new HardwareAsset(newID, nameField.getText(), manufacturerField.getText(), modelField.getText(), purchaseDateField.getText(),
                            activeCheckbox.isSelected(), extraField1.getText(), extraField2.getText());
                } else {
                    return new SoftwareAsset(newID, nameField.getText(), manufacturerField.getText(), modelField.getText(), purchaseDateField.getText(),
                            activeCheckbox.isSelected(), extraField1.getText(), extraField2.getText());
                }
            }
            return null;
        });

        Optional<Asset> result = dialog.showAndWait();
        result.ifPresent(asset -> {
            assetManager.addAsset(asset);
            assetTable.getItems().add(asset);
        });
    }

    private void updateNextAssetID() {
        int maxID = assetManager.getAssets().stream()
                .mapToInt(asset -> Integer.parseInt(asset.getAssetID()))
                .max().orElse(0);
        nextAssetID = maxID + 1;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }

    private void saveStateForUndo() {
        undoStack.push(deepCopy(assetManager.getAssets()));
        redoStack.clear();
    }

    private void undo() {
        if (!undoStack.isEmpty()) {
            redoStack.push(deepCopy(assetManager.getAssets()));
            assetManager.getAssets().clear();
            assetManager.getAssets().addAll(undoStack.pop());
            assetTable.setItems(FXCollections.observableArrayList(assetManager.getAssets()));
        }
    }

    private void redo() {
        if (!redoStack.isEmpty()) {
            undoStack.push(deepCopy(assetManager.getAssets()));
            assetManager.getAssets().clear();
            assetManager.getAssets().addAll(redoStack.pop());
            assetTable.setItems(FXCollections.observableArrayList(assetManager.getAssets()));
        }
    }

    private void deleteSelectedAsset() {
        Asset selected = assetTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            saveStateForUndo();
            assetManager.getAssets().remove(selected);
            assetTable.getItems().remove(selected);
        } else {
            showError("Please select an asset to delete.");
        }
    }

    private List<Asset> deepCopy(List<Asset> assets) {
        List<Asset> copy = new ArrayList<>();
        for (Asset asset : assets) {
            if (asset instanceof HardwareAsset ha) {
                copy.add(new HardwareAsset(ha.getAssetID(), ha.getName(), ha.getManufacturer(), ha.getModel(), ha.getPurchaseDate(),
                        ha.isActive(), ha.getLocation(), ha.getMaintenanceDate()));
            } else if (asset instanceof SoftwareAsset sa) {
                copy.add(new SoftwareAsset(sa.getAssetID(), sa.getName(), sa.getManufacturer(), sa.getModel(), sa.getPurchaseDate(),
                        sa.isActive(), sa.getVersion(), sa.getLicenseKey()));
            }
        }
        return copy;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
