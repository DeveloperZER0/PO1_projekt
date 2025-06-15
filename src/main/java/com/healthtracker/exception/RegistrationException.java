package com.healthtracker.exception;

/**
 * Wyjątek rzucany w przypadku problemów z rejestracją użytkownika.
 * Używany gdy wystąpią błędy takie jak zajęty login, nieprawidłowy email, itp.
 */
public class RegistrationException extends Exception {
    
    public RegistrationException(String message) {
        super(message);
    }
    
    public RegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}