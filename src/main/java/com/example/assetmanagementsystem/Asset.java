package com.example.assetmanagementsystem;
abstract class Asset {
    protected String assetID;
    protected String name;
    protected String manufacturer;
    protected String model;
    protected String purchaseDate;
    protected boolean isActive;

    public Asset(String assetID, String name, String manufacturer, String model, String purchaseDate, boolean isActive) {
        this.assetID = assetID;
        this.name = name;
        this.manufacturer = manufacturer;
        this.model = model;
        this.purchaseDate = purchaseDate;
        this.isActive = isActive;
    }

    public abstract void displayInfo();

    public String getAssetID() {
        return assetID;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getModel() {
        return model;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }


}