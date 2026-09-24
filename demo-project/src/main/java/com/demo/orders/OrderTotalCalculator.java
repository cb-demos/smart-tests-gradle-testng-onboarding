package com.demo.orders;

public class OrderTotalCalculator {
    public static double calculateTotal(double subtotal, double taxRatePercent, double shippingCost) {
        if (subtotal < 0) {
            throw new IllegalArgumentException("Subtotal cannot be negative");
        }
        double tax = subtotal * taxRatePercent / 100.0;
        return subtotal + tax + shippingCost;
    }
}
