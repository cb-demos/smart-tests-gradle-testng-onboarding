package com.demo.auth;

public class PasswordValidator {
    public static boolean isStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasUpper = password.chars().anyMatch(Character::isUpperCase);
        return hasDigit && hasUpper;
    }
}
