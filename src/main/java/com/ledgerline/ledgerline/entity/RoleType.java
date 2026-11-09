package com.ledgerline.ledgerline.entity;

/**
 * Enumeration representing the system roles in LedgerLine.
 * Prefixed with 'ROLE_' per Spring Security conventions.
 */
public enum RoleType {
    ROLE_ADMIN,
    ROLE_ACCOUNT_HOLDER,
    ROLE_AUDITOR
}

