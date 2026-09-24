package com.demo.coupons;

public class CouponValidator {
    public static boolean isValid(String code, double orderAmount) {
        if (orderAmount <= 0) {
            throw new IllegalArgumentException("Order amount must be positive");
        }
        if (code == null || code.length() < 6) {
            return false;
        }
        return code.startsWith("SAVE");
    }
}
