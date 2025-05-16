package com.example.assetmanagementsystem;

/**
 * The Asset class is an abstract base class that represents a general asset in the asset management system.
 * It contains common attributes shared by all types of assets such as asset ID, name, manufacturer, model,
 * purchase date, and active status. Subclasses such as HardwareAsset and SoftwareAsset extend this class
 * and provide specific implementations for the abstract displayInfo() method.
 */
abstract class Asset {
    // Unique identifier for the asset
    protected String assetID;

    // Name or title of the asset
    protected String name;

    // Manufacturer of the asset
    protected String manufacturer;

    // Model number or description of the asset
    protected String model;

    // Date the asset was purchased
    protected String purchaseDate;

    // Indicates whether the asset is currently in use or retired
    protected boolean isActive;

    /**
     * Constructs a new Asset with the specified attributes.
     *
     * @param assetID      Unique ID for the asset
     * @param name         Name of the asset
     * @param manufacturer Manufacturer of the asset
     * @param model        Model information
     * @param purchaseDate Date of purchase
     * @param isActive     Status of the asset (active or not)
     */
    public Asset(String assetID, String name, String manufacturer, String model, String purchaseDate, boolean isActive) {
        this.assetID = assetID;
        this.name = name;
        this.manufacturer = manufacturer;
        this.model = model;
        this.purchaseDate = purchaseDate;
        this.isActive = isActive;
    }

    /**
     * Abstract method to be implemented by subclasses to return a string representation of the asset details.
     *
     * @return Descriptive string containing asset information
     */
    public abstract String displayInfo();

    // Getter for asset ID
    public String getAssetID() {
        return assetID;
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Getter for active status
    public boolean isActive() {
        return isActive;
    }

    // Getter for model
    public String getModel() {
        return model;
    }

    // Getter for manufacturer
    public String getManufacturer() {
        return manufacturer;
    }

    // Getter for purchase date
    public String getPurchaseDate() {
        return purchaseDate;
    }
}
