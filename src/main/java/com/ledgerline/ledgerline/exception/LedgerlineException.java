package com.ledgerline.ledgerline.exception;

/**
 * Base unchecked exception for LedgerLine financial errors.
 */
public class LedgerlineException extends RuntimeException {

    public LedgerlineException(String message) {
        super(message);
    }

    public LedgerlineException(String message, Throwable cause) {
        super(message, cause);
    }
}

