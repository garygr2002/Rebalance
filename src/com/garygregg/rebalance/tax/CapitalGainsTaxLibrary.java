package com.garygregg.rebalance.tax;

import org.jetbrains.annotations.NotNull;

public class CapitalGainsTaxLibrary extends TaxLibrary {

    // The singleton capital gains tax library
    private static final CapitalGainsTaxLibrary library =
            new CapitalGainsTaxLibrary();

    /**
     * Constructs the capital gains tax library.
     */
    private CapitalGainsTaxLibrary() {

        // Nothing to do here not. Added only to make the constructor private.
    }

    /**
     * Gets a capital gains tax library instance.
     *
     * @return A capital gains tax library instance
     */
    public static @NotNull CapitalGainsTaxLibrary getInstance() {
        return library;
    }
}
