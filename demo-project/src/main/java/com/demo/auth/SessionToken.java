package com.demo.auth;

public class SessionToken {
    public static String generate(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("userId cannot be empty");
        }
        return userId + "-token";
    }

    public static boolean isValid(String token) {
        return token != null && token.contains("-token");
    }
}
