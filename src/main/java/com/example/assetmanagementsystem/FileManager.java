package com.example.assetmanagementsystem;

import java.io.*;
import java.util.*;

class FileManager {

    public static void saveAssetsToFile(ArrayList<Asset> assets, String filename) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        for (Asset asset : assets) {
            //To do: add more attributes to output file when Asset class attributes are better defined.
            writer.write(asset.getAssetID() + "," + asset.getName() + "," + asset.isActive() + "\n");
        }
        writer.close();
    }

    public static ArrayList<Asset> loadAssetsFromFile(String filename) throws IOException {
        ArrayList<Asset> assets = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(",");
            // For simplicity in this prototype version, we are assuming all assets are Hardware
            assets.add(new HardwareAsset(data[0], data[1], "Unknown", "Unknown", "Unknown", Boolean.parseBoolean(data[2]), "Unknown", "Unknown"));
        }
        reader.close();
        return assets;
    }
}