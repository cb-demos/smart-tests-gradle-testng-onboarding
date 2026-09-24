package com.demo.tax;

public class TaxCalculator {
    public static double calculateTax(double amount, String region) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative");
        }
        double rate;
        switch (region) {
            case "US":
                rate = 7.5;
                break;
            case "EU":
            case "UK":
                rate = 20.0;
                break;
            default:
                throw new IllegalArgumentException("Unknown region: " + region);
        }
        return amount * rate / 100.0;
    }
}
