package de.emesit.easi18n.internal;

public enum MethodOverrideMode {
    /**
     * If a method is declared more than once, an exception is thrown.
     */
    FAILS,
    
    /**
     * If a method is declared multiple times, last occurrence wins (overwrites all previous
     * translations). The order is defined by the I18nClassSupplier.
     */
    LAST_WINS,
    
    /**
     * If a method is declared multiple times, first occurrence wins
     * the order is defined by the I18nClassSupplier.
     */
    FIRST_WINS;
}