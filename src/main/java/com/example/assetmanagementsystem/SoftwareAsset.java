package com.example.assetmanagementsystem;
class SoftwareAsset extends Asset {
    private String version;
    private String licenseKey;

    public SoftwareAsset(String assetID, String name, String manufacturer, String model, String purchaseDate, boolean isActive, String version, String licenseKey) {
        super(assetID, name, manufacturer, model, purchaseDate, isActive);
        this.version = version;
    }

    @Override
    public void displayInfo() {
        System.out.println("Software Asset ID: " + assetID + ", Name: " + name + ", Version: " + version + ", License Key: " + licenseKey);
    }

}