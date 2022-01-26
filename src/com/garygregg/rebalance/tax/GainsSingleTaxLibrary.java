package com.garygregg.rebalance.tax;

import com.garygregg.rebalance.toolkit.FilingStatus;
import org.jetbrains.annotations.NotNull;

public final class GainsSingleTaxLibrary extends GainsTaxLibrary {

    // The singleton capital gains tax library, filing single
    private static final GainsSingleTaxLibrary library =
            new GainsSingleTaxLibrary();

    static {

        // Add an instance of this class to the capital gains tax library.
        GainsTaxLibrary.addLibrary(getInstance());
    }

    /**
     * Constructs the capital gains tax library, filing single.
     */
    private GainsSingleTaxLibrary() {

        // Nothing to do here not. Added only to make the constructor private.
    }

    /**
     * Gets a filing single capital gains tax library instance.
     *
     * @return A filing single capital gains tax library instance
     */
    static @NotNull GainsSingleTaxLibrary getInstance() {
        return library;
    }

    @Override
    public @NotNull FilingStatus getFilingStatus() {
        return FilingStatus.SINGLE;
    }
}
