package com.garygregg.rebalance.tax;

import org.jetbrains.annotations.NotNull;

public class IncomeTaxLibrary extends TaxLibrary {

    // The singleton income tax library
    private static final IncomeTaxLibrary library = new IncomeTaxLibrary();

    /**
     * Constructs the income tax library.
     */
    private IncomeTaxLibrary() {

        // Nothing to do here not. Added only to make the constructor private.
    }

    /**
     * Gets an income tax library instance.
     *
     * @return An income tax library instance
     */
    public static @NotNull IncomeTaxLibrary getInstance() {
        return library;
    }
}
