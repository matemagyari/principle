package org.tindalos.principle.app.service;

public record ValidationResult(boolean success, String message) {

    public static ValidationResult successful() {
        return new ValidationResult(true, "");
    }

    public static ValidationResult failure(String message) {
        return new ValidationResult(false, message);
    }
}

