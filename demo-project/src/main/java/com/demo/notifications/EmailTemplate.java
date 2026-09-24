package com.demo.notifications;

public class EmailTemplate {
    public static String render(String templateName, String recipientName) {
        if (recipientName == null) {
            throw new IllegalArgumentException("recipientName cannot be null");
        }
        if ("welcome".equals(templateName)) {
            return "Hello " + recipientName + ", welcome!";
        }
        if ("reset-password".equals(templateName)) {
            return "Hello " + recipientName + ", reset your password.";
        }
        throw new IllegalArgumentException("Unknown template: " + templateName);
    }
}
