package com.example.assetmanagementsystem;

/**
 * HardwareAsset represents a physical asset such as a laptop, server, or peripheral.
 * It extends the abstract Asset class and includes additional attributes specific to hardware,
 * such as location and maintenance date.
 */
class HardwareAsset extends Asset {
    private String location;
    private String maintenanceDate;

    /**
     * Constructs a HardwareAsset with all required attributes.
     *
     * @param assetID         Unique identifier for the asset
     * @param name            Name of the asset
     * @param manufacturer    Manufacturer of the hardware
     * @param model           Model number or name
     * @param purchaseDate    Date the asset was purchased
     * @param isActive        Whether the asset is currently active
     * @param location        Physical location of the hardware
     * @param maintenanceDate Last maintenance date
     */
    public HardwareAsset(String assetID, String name, String manufacturer, String model,
                         String purchaseDate, boolean isActive, String location, String maintenanceDate) {
        super(assetID, name, manufacturer, model, purchaseDate, isActive);
        this.location = location;
        this.maintenanceDate = maintenanceDate;
    }

    /**
     * Returns a string containing all asset information for display or logging.
     *
     * @return Full information about the hardware asset
     */
    @Override
    public String displayInfo() {
        return "Hardware Asset ID: " + assetID +
                ", Name: " + name +
                ", Manufacturer: " + manufacturer +
                ", Model: " + model +
                ", Purchase Date: " + purchaseDate +
                ", Active: " + isActive +
                ", Location: " + location +
                ", Maintenance Date: " + maintenanceDate;
    }

    /**
     * Gets the location of the hardware asset.
     *
     * @return Physical location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Gets the last maintenance date of the hardware asset.
     *
     * @return Maintenance date
     */
    public String getMaintenanceDate() {
        return maintenanceDate;
    }
}
