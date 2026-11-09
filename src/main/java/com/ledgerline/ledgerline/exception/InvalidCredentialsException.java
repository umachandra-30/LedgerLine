package com.ledgerline.ledgerline.exception;

/**
 * Exception thrown when authentication fails due to incorrect credentials.
 * Provides a generic message to prevent account enumeration attacks.
 */
public class InvalidCredentialsException extends LedgerlineException {

    public InvalidCredentialsException(String message) {
        super(message);
    }
}

