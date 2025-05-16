package com.example.assetmanagementsystem;

/**
 * AssetBST is a Binary Search Tree implementation for managing Asset objects by their assetID.
 * It supports insertion, deletion, search, in-order traversal, and clearing the entire tree.
 * The assetID is assumed to be a numeric string, used to determine the order in the BST.
 */
public class AssetBST {

    // Internal class representing a node in the BST
    private static class Node {
        Asset asset;  // Asset object stored in the node
        Node left, right;  // Left and right children

        Node(Asset asset) {
            this.asset = asset;
        }
    }

    private Node root;  // Root node of the BST

    /**
     * Inserts a new Asset into the BST.
     *
     * @param asset The asset to insert
     */
    public void insert(Asset asset) {
        root = insertRec(root, asset);
    }

    // Helper method for recursive insertion
    private Node insertRec(Node root, Asset asset) {
        if (root == null) return new Node(asset);

        int cmp = compareAssetIDs(asset.getAssetID(), root.asset.getAssetID());
        if (cmp < 0) {
            root.left = insertRec(root.left, asset);
        } else if (cmp > 0) {
            root.right = insertRec(root.right, asset);
        }
        // Duplicate IDs are ignored (not inserted again)
        return root;
    }

    /**
     * Searches for an asset by its ID.
     *
     * @param assetID The asset ID to search for
     * @return The matching Asset, or null if not found
     */
    public Asset search(String assetID) {
        return searchRec(root, assetID);
    }

    // Helper method for recursive search
    private Asset searchRec(Node root, String assetID) {
        if (root == null) return null;

        int cmp = compareAssetIDs(assetID, root.asset.getAssetID());
        if (cmp == 0) return root.asset;
        else if (cmp < 0) return searchRec(root.left, assetID);
        else return searchRec(root.right, assetID);
    }

    /**
     * Deletes an asset by its ID.
     *
     * @param assetID The ID of the asset to delete
     */
    public void delete(String assetID) {
        root = deleteRec(root, assetID);
    }

    // Helper method for recursive deletion
    private Node deleteRec(Node root, String assetID) {
        if (root == null) return null;

        int cmp = compareAssetIDs(assetID, root.asset.getAssetID());
        if (cmp < 0) {
            root.left = deleteRec(root.left, assetID);
        } else if (cmp > 0) {
            root.right = deleteRec(root.right, assetID);
        } else {
            // Node with only one child or no child
            if (root.left == null) return root.right;
            if (root.right == null) return root.left;

            // Node with two children: get the inorder successor (smallest in the right subtree)
            Node min = findMin(root.right);
            root.asset = min.asset;
            root.right = deleteRec(root.right, min.asset.getAssetID());
        }
        return root;
    }

    // Finds the node with the minimum assetID in a subtree
    private Node findMin(Node node) {
        while (node.left != null) node = node.left;
        return node;
    }

    /**
     * Clears the entire tree.
     */
    public void clear() {
        root = null;
    }

    /**
     * Performs an in-order traversal and prints asset ID and name.
     * Used primarily for debugging or console-based inspection.
     */
    public void inOrderTraversal() {
        inOrderRec(root);
    }

    // Helper method for in-order traversal
    private void inOrderRec(Node root) {
        if (root != null) {
            inOrderRec(root.left);
            System.out.println(root.asset.getAssetID() + " - " + root.asset.getName());
            inOrderRec(root.right);
        }
    }

    // Compares two asset IDs as integers (assuming asset IDs are numeric strings)
    private int compareAssetIDs(String id1, String id2) {
        return Integer.compare(Integer.parseInt(id1), Integer.parseInt(id2));
    }
}
