package com.example.assetmanagementsystem;
class HardwareAsset extends Asset {
    private String location;
    private String maintenanceDate;

    public HardwareAsset(String assetID, String name, String manufacturer, String model, String purchaseDate, boolean isActive, String location, String maintenanceDate) {
        super(assetID, name, manufacturer, model, purchaseDate, isActive);
        this.location = location;
        this.maintenanceDate = maintenanceDate;
    }

    @Override
    public void displayInfo() {
        System.out.println("Hardware Asset ID: " + assetID + ", Name: " + name + ", Location: " + location + ", Maintenance Date: " + maintenanceDate);
    }

}