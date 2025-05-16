package com.example.assetmanagementsystem;

import java.io.*;
import java.util.*;

/**
 * FileManager handles reading and writing asset data to and from a file.
 * It supports saving a list of assets (both hardware and software) and loading them back,
 * preserving their specific subclass attributes via a simple CSV format.
 */
public class FileManager {

    /**
     * Saves a list of Asset objects to a file.
     * Each asset is written as a CSV line with all relevant fields.
     * Format:
     * - Hardware: Hardware,assetID,name,manufacturer,model,purchaseDate,isActive,location,maintenanceDate
     * - Software: Software,assetID,name,manufacturer,model,purchaseDate,isActive,version,licenseKey
     *
     * @param assets   The list of assets to be saved.
     * @param filename The file path where data will be saved.
     * @throws IOException if an I/O error occurs.
     */
    public static void saveAssetsToFile(List<Asset> assets, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Asset asset : assets) {
                if (asset instanceof HardwareAsset ha) {
                    writer.write("Hardware," + ha.getAssetID() + "," + ha.getName() + "," + ha.getManufacturer() + "," +
                            ha.getModel() + "," + ha.getPurchaseDate() + "," + ha.isActive() + "," +
                            ha.getLocation() + "," + ha.getMaintenanceDate());
                } else if (asset instanceof SoftwareAsset sa) {
                    writer.write("Software," + sa.getAssetID() + "," + sa.getName() + "," + sa.getManufacturer() + "," +
                            sa.getModel() + "," + sa.getPurchaseDate() + "," + sa.isActive() + "," +
                            sa.getVersion() + "," + sa.getLicenseKey());
                }
                writer.newLine(); // Write a newline after each asset
            }
        }
    }

    /**
     * Loads a list of Asset objects from a file.
     * Parses each line based on the expected format for hardware and software assets.
     *
     * @param filename The file path to read data from.
     * @return A list of loaded Asset objects.
     * @throws IOException if an I/O error occurs.
     */
    public static List<Asset> loadAssetsFromFile(String filename) throws IOException {
        List<Asset> assets = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 9) continue; // Skip malformed lines

                String type = parts[0];
                String id = parts[1];
                String name = parts[2];
                String manufacturer = parts[3];
                String model = parts[4];
                String purchaseDate = parts[5];
                boolean active = Boolean.parseBoolean(parts[6]);

                if (type.equals("Hardware")) {
                    String location = parts[7];
                    String maintenanceDate = parts[8];
                    assets.add(new HardwareAsset(id, name, manufacturer, model, purchaseDate, active, location, maintenanceDate));
                } else if (type.equals("Software")) {
                    String version = parts[7];
                    String licenseKey = parts[8];
                    assets.add(new SoftwareAsset(id, name, manufacturer, model, purchaseDate, active, version, licenseKey));
                }
            }
        }

        return assets;
    }
}
