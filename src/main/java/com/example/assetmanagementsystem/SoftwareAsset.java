package com.example.assetmanagementsystem;

/**
 * SoftwareAsset represents a software-type asset in the asset management system.
 * It extends the abstract Asset class and adds software-specific attributes
 * such as version and license key.
 */
class SoftwareAsset extends Asset {
    // Version of the software (e.g., "v2.1.4")
    private String version;

    // License key associated with the software
    private String licenseKey;

    /**
     * Constructs a SoftwareAsset with the specified properties.
     *
     * @param assetID      Unique identifier for the asset
     * @param name         Name of the software
     * @param manufacturer Manufacturer of the software
     * @param model        Model or edition of the software
     * @param purchaseDate Purchase date of the asset
     * @param isActive     Whether the asset is currently active
     * @param version      Version of the software
     * @param licenseKey   License key for the software
     */
    public SoftwareAsset(String assetID, String name, String manufacturer, String model, String purchaseDate,
                         boolean isActive, String version, String licenseKey) {
        super(assetID, name, manufacturer, model, purchaseDate, isActive);
        this.version = version;
        this.licenseKey = licenseKey;
    }

    /**
     * Returns a formatted string containing key software asset details.
     *
     * @return A string with asset ID, name, version, and license key
     */
    @Override
    public String displayInfo() {
        return "Software Asset ID: " + assetID +
                ", Name: " + name +
                ", Manufacturer: " + manufacturer +
                ", Model: " + model +
                ", Purchase Date: " + purchaseDate +
                ", Active: " + isActive +
                ", Version: " + version +
                ", License Key: " + licenseKey;
    }

    /**
     * Returns the version of the software.
     *
     * @return Software version
     */
    public String getVersion() {
        return version;
    }

    /**
     * Returns the license key of the software.
     *
     * @return Software license key
     */
    public String getLicenseKey() {
        return licenseKey;
    }
}
