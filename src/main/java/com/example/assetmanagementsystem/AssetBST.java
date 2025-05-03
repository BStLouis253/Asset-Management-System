package com.example.assetmanagementsystem;

public class AssetBST {
    private static class Node {
        Asset asset;
        Node left, right;

        Node(Asset asset) {
            this.asset = asset;
        }
    }

    private Node root;

    public void insert(Asset asset) {
        root = insertRec(root, asset);
    }

    private Node insertRec(Node root, Asset asset) {
        if (root == null) return new Node(asset);

        int cmp = compareAssetIDs(asset.getAssetID(), root.asset.getAssetID());
        if (cmp < 0) {
            root.left = insertRec(root.left, asset);
        } else if (cmp > 0) {
            root.right = insertRec(root.right, asset);
        }

        return root;
    }

    public Asset search(String assetID) {
        return searchRec(root, assetID);
    }

    private Asset searchRec(Node root, String assetID) {
        if (root == null) return null;

        int cmp = compareAssetIDs(assetID, root.asset.getAssetID());
        if (cmp == 0) return root.asset;
        else if (cmp < 0) return searchRec(root.left, assetID);
        else return searchRec(root.right, assetID);
    }

    public void delete(String assetID) {
        root = deleteRec(root, assetID);
    }

    private Node deleteRec(Node root, String assetID) {
        if (root == null) return null;

        int cmp = compareAssetIDs(assetID, root.asset.getAssetID());
        if (cmp < 0) {
            root.left = deleteRec(root.left, assetID);
        } else if (cmp > 0) {
            root.right = deleteRec(root.right, assetID);
        } else {
            if (root.left == null) return root.right;
            if (root.right == null) return root.left;

            Node min = findMin(root.right);
            root.asset = min.asset;
            root.right = deleteRec(root.right, min.asset.getAssetID());
        }
        return root;
    }

    private Node findMin(Node node) {
        while (node.left != null) node = node.left;
        return node;
    }

    public void clear() {
        root = null;
    }

    public void inOrderTraversal() {
        inOrderRec(root);
    }

    private void inOrderRec(Node root) {
        if (root != null) {
            inOrderRec(root.left);
            System.out.println(root.asset.getAssetID() + " - " + root.asset.getName());
            inOrderRec(root.right);
        }
    }

    private int compareAssetIDs(String id1, String id2) {
        return Integer.compare(Integer.parseInt(id1), Integer.parseInt(id2));
    }
}
