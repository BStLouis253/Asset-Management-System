package com.example.assetmanagementsystem;

import java.io.*;
import java.util.*;

public class FileManager {

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
                writer.newLine();
            }
        }
    }

    public static List<Asset> loadAssetsFromFile(String filename) throws IOException {
        List<Asset> assets = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 9) continue;
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
