package com.example.assetmanagementsystem;

import javafx.application.Application;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * AssetManagementSystem.java
 *
 * A JavaFX-based graphical user interface (GUI) application for managing hardware and software assets.
 * This system allows users to:
 * - Add, edit, and delete assets (both hardware and software)
 * - Display asset details in a dynamic, sortable table
 * - Load and save assets from/to a file
 * - Toggle visibility of asset table columns
 * - Manage assets in memory using both a list (AssetManager) and a binary search tree (AssetBST)
 *
 * Asset IDs are auto-generated to ensure uniqueness.
 * Different input forms are shown for hardware and software asset types.
 * Each asset is stored and displayed with both common and type-specific attributes.
 *
 * Dependencies:
 * - AssetManager: for managing the collection of assets
 * - AssetBST: for managing assets in a binary search tree based on asset ID
 * - FileManager: for saving and loading assets from persistent storage
 * - HardwareAsset and SoftwareAsset: subclasses of the abstract Asset class
 *
 */

public class AssetManagementSystem extends Application {

    // Core asset manager handling storage and sorting
    private final AssetManager assetManager = new AssetManager();

    // JavaFX table for displaying assets
    private final TableView<Asset> assetTable = new TableView<>();

    // Maps column names to their corresponding TableColumn objects
    private final Map<String, TableColumn<Asset, String>> columnMap = new TreeMap<>();

    // Undo/redo history stacks
    private final Deque<List<Asset>> undoStack = new ArrayDeque<>();
    private final Deque<List<Asset>> redoStack = new ArrayDeque<>();

    // Binary search tree for asset lookup by ID
    private final AssetBST assetBST = new AssetBST();

    // Indexes for fast attribute-based search
    private final Map<String, Map<String, List<Asset>>> attributeIndexes = new HashMap<>();

    // Counter for generating unique asset IDs
    private int nextAssetID = 1;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Asset Management System");

        // Dropdown menu for toggling column visibility
        MenuButton columnSelector = new MenuButton("Select Columns");

        // Initialize and configure columns
        createColumns(columnSelector);

        // Buttons to add new assets
        Button addHardwareButton = new Button("Add Hardware Asset");
        addHardwareButton.setOnAction(e -> showAssetInputDialog("hardware", null));

        Button addSoftwareButton = new Button("Add Software Asset");
        addSoftwareButton.setOnAction(e -> showAssetInputDialog("software", null));

        // Load assets from file and populate data structures
        Button loadAssetsButton = new Button("Load Assets");
        loadAssetsButton.setOnAction(e -> {
            try {
                saveStateForUndo();
                List<Asset> loadedAssets = FileManager.loadAssetsFromFile("SavedAssets.txt");
                assetManager.getAssets().clear();
                assetBST.clear();
                clearIndexes();
                assetManager.getAssets().addAll(loadedAssets);
                for (Asset asset : loadedAssets) {
                    assetBST.insert(asset);
                    indexAsset(asset);
                }
                assetTable.setItems(FXCollections.observableArrayList(assetManager.getAssets()));
                updateNextAssetID();
            } catch (IOException ex) {
                showError("Error loading assets from file.");
            }
        });

        // Save current assets to file
        Button saveAssetsButton = new Button("Save Assets");
        saveAssetsButton.setOnAction(e -> {
            try {
                FileManager.saveAssetsToFile(assetManager.getAssets(), "SavedAssets.txt");
            } catch (IOException ex) {
                showError("Error saving assets to file.");
            }
        });

        // Undo/redo buttons
        Button undoButton = new Button("Undo");
        undoButton.setOnAction(e -> undo());

        Button redoButton = new Button("Redo");
        redoButton.setOnAction(e -> redo());

        // Button to delete currently selected asset
        Button deleteButton = new Button("Delete Selected Asset");
        deleteButton.setOnAction(e -> deleteSelectedAsset());

        // Button to open attribute-based search dialog
        Button searchButton = new Button("Search By Attribute");
        searchButton.setOnAction(e -> showSearchDialog());

        // Top toolbar with all controls
        HBox topBar = new HBox(10, addHardwareButton, addSoftwareButton, loadAssetsButton, saveAssetsButton,
                columnSelector, undoButton, redoButton, deleteButton, searchButton);
        topBar.setPadding(new Insets(10));

        // Layout configuration
        VBox root = new VBox(10, topBar, assetTable);
        root.setPadding(new Insets(10));
        VBox.setVgrow(assetTable, Priority.ALWAYS);

        // Enable double-clicking a row to edit the corresponding asset
        assetTable.setRowFactory(tv -> {
            TableRow<Asset> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    Asset selectedAsset = row.getItem();
                    String type = (selectedAsset instanceof HardwareAsset) ? "hardware" : "software";
                    showAssetInputDialog(type, selectedAsset);
                }
            });
            return row;
        });

        // Load existing assets on startup
        loadAssetsButton.fire();

        primaryStage.setScene(new Scene(root, 1000, 600));
        primaryStage.show();
    }

    // Initializes and adds all asset-related columns to the table and column selector
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

    // Adds a column to the table with a corresponding checkbox in the selector
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

    // Sorts and re-adds columns to the table in alphabetical order
    private void addColumnsAlphabetically() {
        List<String> sortedNames = new ArrayList<>(columnMap.keySet());
        Collections.sort(sortedNames);
        assetTable.getColumns().setAll(sortedNames.stream().map(columnMap::get).toList());
    }

    // Displays a dialog for adding or editing a hardware or software asset
    private void showAssetInputDialog(String type, Asset existingAsset) {
        // Create the dialog and set its title based on context (add/edit and type)
        Dialog<Asset> dialog = new Dialog<>();
        dialog.setTitle((existingAsset == null ? "Add " : "Edit ") +
                (type.equals("hardware") ? "Hardware" : "Software") + " Asset");

        // Setup the input layout using GridPane
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Use the existing asset ID if editing, otherwise generate the next ID
        String assetID = existingAsset == null ? String.format("%03d", nextAssetID++) : existingAsset.getAssetID();

        // Initialize input fields with existing values if editing, or leave blank if adding
        TextField nameField = new TextField(existingAsset != null ? existingAsset.getName() : "");
        TextField manufacturerField = new TextField(existingAsset != null ? existingAsset.getManufacturer() : "");
        TextField modelField = new TextField(existingAsset != null ? existingAsset.getModel() : "");
        TextField purchaseDateField = new TextField(existingAsset != null ? existingAsset.getPurchaseDate() : "");
        CheckBox activeCheckbox = new CheckBox();
        if (existingAsset != null) activeCheckbox.setSelected(existingAsset.isActive());

        // Add general fields to the grid
        grid.add(new Label("Asset ID:"), 0, 0);
        grid.add(new Label(assetID), 1, 0);
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

        // Fields for asset-type-specific attributes
        TextField extraField1 = new TextField();
        TextField extraField2 = new TextField();

        // If the asset is hardware, set and display location and maintenance date fields
        if (type.equals("hardware")) {
            if (existingAsset instanceof HardwareAsset ha) {
                extraField1.setText(ha.getLocation());
                extraField2.setText(ha.getMaintenanceDate());
            }
            grid.add(new Label("Location:"), 0, 6);
            grid.add(extraField1, 1, 6);
            grid.add(new Label("Maintenance Date:"), 0, 7);
            grid.add(extraField2, 1, 7);
        } else { // Otherwise it's software; show version and license key
            if (existingAsset instanceof SoftwareAsset sa) {
                extraField1.setText(sa.getVersion());
                extraField2.setText(sa.getLicenseKey());
            }
            grid.add(new Label("Version:"), 0, 6);
            grid.add(extraField1, 1, 6);
            grid.add(new Label("License Key:"), 0, 7);
            grid.add(extraField2, 1, 7);
        }

        // Add grid to dialog content and define buttons
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Define behavior when user confirms (OK button)
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                saveStateForUndo(); // Save current state for undo support
                if (type.equals("hardware")) {
                    return new HardwareAsset(assetID, nameField.getText(), manufacturerField.getText(), modelField.getText(),
                            purchaseDateField.getText(), activeCheckbox.isSelected(),
                            extraField1.getText(), extraField2.getText());
                } else {
                    return new SoftwareAsset(assetID, nameField.getText(), manufacturerField.getText(), modelField.getText(),
                            purchaseDateField.getText(), activeCheckbox.isSelected(),
                            extraField1.getText(), extraField2.getText());
                }
            }
            return null; // Cancel was clicked
        });

        // Process the result of the dialog when the user confirms input
        Optional<Asset> result = dialog.showAndWait();
        result.ifPresent(asset -> {
            // If editing, remove old asset from all places before adding the updated one
            if (existingAsset != null) {
                assetManager.getAssets().remove(existingAsset);
                assetTable.getItems().remove(existingAsset);
                assetBST.delete(existingAsset.getAssetID());
            }
            // Add the new or updated asset to data structures and UI
            assetManager.addAsset(asset);
            assetTable.getItems().add(asset);
            assetBST.insert(asset);
        });
    }

    // Clears the attribute index map used for fast searching
    private void clearIndexes() {
        attributeIndexes.clear();
    }

    // Adds an asset to the attribute index map for quick lookups by various fields
    private void indexAsset(Asset asset) {
        indexAttribute("Name", asset.getName(), asset);
        indexAttribute("Asset ID", asset.getAssetID(), asset);
        indexAttribute("Manufacturer", asset.getManufacturer(), asset);
        indexAttribute("Model", asset.getModel(), asset);
        indexAttribute("Purchase Date", asset.getPurchaseDate(), asset);
        if (asset instanceof HardwareAsset ha) {
            indexAttribute("Location", ha.getLocation(), asset);
            indexAttribute("Maintenance Date", ha.getMaintenanceDate(), asset);
        } else if (asset instanceof SoftwareAsset sa) {
            indexAttribute("Version", sa.getVersion(), asset);
            indexAttribute("License Key", sa.getLicenseKey(), asset);
        }
    }

    // Adds a single attribute-value pair to the attribute index map for the given asset
    private void indexAttribute(String attribute, String value, Asset asset) {
        attributeIndexes.putIfAbsent(attribute, new HashMap<>());
        attributeIndexes.get(attribute).computeIfAbsent(value.toLowerCase(), k -> new ArrayList<>()).add(asset);
    }

    // Displays a dialog to allow the user to search for assets by a selected attribute
    private void showSearchDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Search By Attribute");

        // Input controls for selecting an attribute and entering a search query
        ComboBox<String> attributeBox = new ComboBox<>(FXCollections.observableArrayList(attributeIndexes.keySet()));
        attributeBox.getSelectionModel().selectFirst();
        TextField queryField = new TextField();
        Button searchButton = new Button("Search");

        TableView<Asset> resultTable = new TableView<>();
        ListView<String> resultList = new ListView<>();

        // Layout setup
        VBox content = new VBox(10,
                new Label("Select Attribute:"), attributeBox,
                new Label("Enter search term:"), queryField,
                searchButton,
                new Label("Results:"), resultList
        );
        content.setPadding(new Insets(10));
        content.setPrefWidth(900);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.CLOSE);

        dialog.setResultConverter(buttonType -> null);

        // Action for performing the search when the button is clicked
        searchButton.setOnAction(e -> {
            String attr = attributeBox.getValue();
            String query = queryField.getText().toLowerCase();
            List<Asset> results = new ArrayList<>();

            // Background task to avoid freezing the UI
            Task<List<Asset>> searchTask = new Task<>() {
                @Override
                protected List<Asset> call() {
                    if (attr.equals("Asset ID")) {
                        // Use BST for fast asset ID lookup
                        Asset match = assetBST.search(query);
                        if (match != null) {
                            results.add(match);
                        }
                    } else {
                        // Search indexed attributes for exact and partial matches
                        Map<String, List<Asset>> map = attributeIndexes.getOrDefault(attr, Map.of());

                        List<Asset> exactMatches = map.getOrDefault(query, new ArrayList<>());
                        List<Asset> partialMatches = map.entrySet().stream()
                                .filter(entry -> entry.getKey().contains(query) && !entry.getKey().equals(query))
                                .flatMap(entry -> entry.getValue().stream())
                                .collect(Collectors.toList());

                        results.addAll(exactMatches);
                        results.addAll(partialMatches);
                    }
                    return results;
                }
            };

            // Display search results when task completes
            searchTask.setOnSucceeded(ev -> {
                List<String> displayResults = searchTask.getValue().stream()
                        .map(asset -> asset.getAssetID() + " - " + asset.displayInfo())
                        .collect(Collectors.toList());

                resultList.setItems(FXCollections.observableArrayList(displayResults));
            });

            // Handle search errors
            searchTask.setOnFailed(ev -> {
                Throwable error = searchTask.getException();
                System.out.println("Search failed: " + error.getMessage());
            });

            new Thread(searchTask).start();
        });

        dialog.showAndWait();
    }

    // Updates the nextAssetID counter to one greater than the current highest ID
    private void updateNextAssetID() {
        int maxID = assetManager.getAssets().stream()
                .mapToInt(asset -> Integer.parseInt(asset.getAssetID()))
                .max().orElse(0);
        nextAssetID = maxID + 1;
    }

    // Displays an error message in an alert dialog
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }

    // Saves the current asset list state for undo functionality
    private void saveStateForUndo() {
        undoStack.push(deepCopy(assetManager.getAssets()));
        redoStack.clear();
    }

    // Undoes the last change by restoring the previous state
    private void undo() {
        if (!undoStack.isEmpty()) {
            redoStack.push(deepCopy(assetManager.getAssets()));
            assetManager.getAssets().clear();
            assetManager.getAssets().addAll(undoStack.pop());
            assetTable.setItems(FXCollections.observableArrayList(assetManager.getAssets()));
        }
    }

    // Redoes the last undone change
    private void redo() {
        if (!redoStack.isEmpty()) {
            undoStack.push(deepCopy(assetManager.getAssets()));
            assetManager.getAssets().clear();
            assetManager.getAssets().addAll(redoStack.pop());
            assetTable.setItems(FXCollections.observableArrayList(assetManager.getAssets()));
        }
    }

    // Deletes the selected asset from the table, list, and BST
    private void deleteSelectedAsset() {
        Asset selected = assetTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            saveStateForUndo();
            assetManager.getAssets().remove(selected);
            assetTable.getItems().remove(selected);
            assetBST.delete(selected.getAssetID());
        } else {
            showError("Please select an asset to delete.");
        }
    }

    // Creates a deep copy of the list of assets to preserve state for undo/redo
    private List<Asset> deepCopy(List<Asset> assets) {
        List<Asset> copy = new ArrayList<>();
        for (Asset asset : assets) {
            if (asset instanceof HardwareAsset ha) {
                copy.add(new HardwareAsset(
                        ha.getAssetID(), ha.getName(), ha.getManufacturer(), ha.getModel(), ha.getPurchaseDate(),
                        ha.isActive(), ha.getLocation(), ha.getMaintenanceDate()));
            } else if (asset instanceof SoftwareAsset sa) {
                copy.add(new SoftwareAsset(
                        sa.getAssetID(), sa.getName(), sa.getManufacturer(), sa.getModel(), sa.getPurchaseDate(),
                        sa.isActive(), sa.getVersion(), sa.getLicenseKey()));
            }
        }
        return copy;
    }

    // Launches the JavaFX application
    public static void main(String[] args) {
        launch(args);
    }
}
