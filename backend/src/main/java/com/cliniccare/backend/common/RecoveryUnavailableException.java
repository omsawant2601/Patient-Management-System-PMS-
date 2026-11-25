package com.cliniccare.backend.common;

public class RecoveryUnavailableException extends RuntimeException {
    public RecoveryUnavailableException() {
        super("Password recovery email delivery is not configured");
    }
}
