package com.lubiekakao1212.kboom.util;

public class Validation {

    public static <T> void validateNotNull(T value, String message) {
        if(value == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static <T> void requiredProperty(T value, String propertyName) {
        validateNotNull(value, "Missing required property: " + propertyName);
    }


}
