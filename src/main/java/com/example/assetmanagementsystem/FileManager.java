package com.example.assetmanagementsystem;

import java.io.*;
import java.util.*;

class FileManager {

    public static void saveAssetsToFile(ArrayList<Asset> assets, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Asset asset : assets) {
                if (asset instanceof HardwareAsset hw) {
                    writer.write("Hardware," + hw.getAssetID() + "," + hw.getName() + "," + hw.getManufacturer() + "," +
                            hw.getModel() + "," + hw.getPurchaseDate() + "," + hw.isActive() + "," +
                            hw.getLocation() + "," + hw.getMaintenanceDate() + "\n");
                } else if (asset instanceof SoftwareAsset sw) {
                    writer.write("Software," + sw.getAssetID() + "," + sw.getName() + "," + sw.getManufacturer() + "," +
                            sw.getModel() + "," + sw.getPurchaseDate() + "," + sw.isActive() + "," +
                            sw.getVersion() + "," + sw.getLicenseKey() + "\n");
                }
            }
        }
    }


    public static ArrayList<Asset> loadAssetsFromFile(String filename) throws IOException {
        ArrayList<Asset> assets = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                String type = data[0];
                if (type.equals("Hardware")) {
                    assets.add(new HardwareAsset(data[1], data[2], data[3], data[4], data[5],
                            Boolean.parseBoolean(data[6]), data[7], data[8]));
                } else if (type.equals("Software")) {
                    assets.add(new SoftwareAsset(data[1], data[2], data[3], data[4], data[5],
                            Boolean.parseBoolean(data[6]), data[7], data[8]));
                }
            }
        }
        return assets;
    }
}