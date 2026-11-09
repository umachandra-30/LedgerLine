package com.ledgerline.ledgerline.exception;

/**
 * Exception thrown when attempting to create a resource that already exists (e.g. duplicate email).
 */
public class DuplicateResourceException extends LedgerlineException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}

