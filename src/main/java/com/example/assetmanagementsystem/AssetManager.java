package com.example.assetmanagementsystem;
import java.util.ArrayList;
import java.util.Collections;

class AssetManager {
    private ArrayList<Asset> assets;

    public AssetManager() {
        this.assets = new ArrayList<>();
    }

    // Add a new asset
    public void addAsset(Asset asset) {
        assets.add(asset);
    }

    // Generalized QuickSort method to sort by any attribute.
    public void sortAssets(String attribute) {
        quickSort(assets, 0, assets.size() - 1, attribute);
    }

    // QuickSort implementation used as the ArrayList fits into memory and will generally be efficient for
    // large Asset lists. Since we generally only sort once per attribute type, average case is likely to be
    // encountered and the Time complexity expected is O(n logn).
    private void quickSort(ArrayList<Asset> list, int low, int high, String attribute) {
        if (low < high) {
            // Partitioning index
            int pi = partition(list, low, high, attribute);

            // Recursively sort elements before and after partition
            quickSort(list, low, pi - 1, attribute);
            quickSort(list, pi + 1, high, attribute);
        }
    }

    // Partition function for QuickSort
    private int partition(ArrayList<Asset> list, int low, int high, String attribute) {
        Asset pivot = list.get(high); // Taking the last element as pivot
        String pivotValue = getAttributeValue(pivot, attribute); // Get the pivot value based on the attribute

        int i = (low - 1); // Index of smaller element
        for (int j = low; j < high; j++) {
            String currentValue = getAttributeValue(list.get(j), attribute);

            // If current value is smaller than the pivot value
            if (currentValue.compareTo(pivotValue) < 0) {
                i++;
                // Swap list[i] and list[j]
                Collections.swap(list, i, j);
            }
        }
        // Swap the pivot element with the element at i + 1
        Collections.swap(list, i + 1, high);
        return i + 1;
    }

    // Helper method to get attribute value based on the attribute name
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

    public ArrayList<Asset> getAssets() {
        return assets;
    }
}