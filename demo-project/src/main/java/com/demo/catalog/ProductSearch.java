package com.demo.catalog;

public class ProductSearch {
    public static boolean matches(String query, String productName) {
        if (query == null) {
            throw new IllegalArgumentException("query cannot be null");
        }
        if (productName == null) {
            return false;
        }
        return productName.toLowerCase().contains(query.toLowerCase());
    }
}
