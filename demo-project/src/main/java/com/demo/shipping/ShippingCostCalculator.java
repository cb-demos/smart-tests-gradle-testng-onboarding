package com.demo.shipping;

public class ShippingCostCalculator {
    public static double calculate(double weightKg, String method) {
        if (weightKg < 0) {
            throw new IllegalArgumentException("Weight cannot be negative");
        }
        double base;
        switch (method) {
            case "STANDARD":
                base = 5.0;
                break;
            case "EXPRESS":
                base = 15.0;
                break;
            default:
                throw new IllegalArgumentException("Unknown shipping method: " + method);
        }
        return base + weightKg * 1.2;
    }
}
