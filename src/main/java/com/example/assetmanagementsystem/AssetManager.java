package com.example.assetmanagementsystem;

import java.util.ArrayList;
import java.util.Collections;

/**
 * AssetManager handles the management of a collection of Asset objects.
 * It allows adding assets, retrieving the asset list, and sorting the list
 * based on a given attribute using the QuickSort algorithm.
 */
class AssetManager {
    private ArrayList<Asset> assets;

    /**
     * Constructs an AssetManager with an empty list of assets.
     */
    public AssetManager() {
        this.assets = new ArrayList<>();
    }

    /**
     * Adds a new asset to the internal list.
     *
     * @param asset The asset to add.
     */
    public void addAsset(Asset asset) {
        assets.add(asset);
    }

    /**
     * Sorts the asset list by a specified attribute.
     * Valid attributes include: assetID, name, manufacturer, model, purchaseDate.
     *
     * @param attribute The attribute to sort by.
     */
    public void sortAssets(String attribute) {
        quickSort(assets, 0, assets.size() - 1, attribute);
    }

    /**
     * Implements QuickSort to sort the asset list based on a given attribute.
     *
     * @param list      The list of assets to sort.
     * @param low       The starting index.
     * @param high      The ending index.
     * @param attribute The attribute to sort by.
     */
    private void quickSort(ArrayList<Asset> list, int low, int high, String attribute) {
        if (low < high) {
            int pi = partition(list, low, high, attribute);
            quickSort(list, low, pi - 1, attribute);
            quickSort(list, pi + 1, high, attribute);
        }
    }

    /**
     * Partitions the list for QuickSort based on the given attribute.
     *
     * @param list      The list to partition.
     * @param low       The starting index.
     * @param high      The ending index (pivot).
     * @param attribute The attribute to compare.
     * @return The partition index.
     */
    private int partition(ArrayList<Asset> list, int low, int high, String attribute) {
        Asset pivot = list.get(high);
        String pivotValue = getAttributeValue(pivot, attribute);

        int i = low - 1;
        for (int j = low; j < high; j++) {
            String currentValue = getAttributeValue(list.get(j), attribute);
            if (currentValue.compareTo(pivotValue) < 0) {
                i++;
                Collections.swap(list, i, j);
            }
        }
        Collections.swap(list, i + 1, high);
        return i + 1;
    }

    /**
     * Returns the value of the specified attribute from an Asset.
     *
     * @param asset     The asset to extract from.
     * @param attribute The attribute name.
     * @return The value as a string.
     */
    private String getAttributeValue(Asset asset, String attribute) {
        switch (attribute.toLowerCase()) {
            case "assetid":
                return asset.getAssetID();
            case "name":
                return asset.getName();
            case "manufacturer":
                return asset.getManufacturer();
            case "model":
                return asset.getModel();
            case "purchasedate":
                return asset.getPurchaseDate();
            default:
                throw new IllegalArgumentException("Invalid attribute for sorting: " + attribute);
        }
    }

    /**
     * Returns the list of all managed assets.
     *
     * @return The asset list.
     */
    public ArrayList<Asset> getAssets() {
        return assets;
    }
}
