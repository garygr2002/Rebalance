package com.garygregg.rebalance.tax;

import com.garygregg.rebalance.toolkit.FilingStatus;
import org.jetbrains.annotations.NotNull;

public final class GainsSeparateTaxLibrary extends GainsTaxLibrary {

    // The singleton capital gains tax library, married-filing-separately
    private static final GainsSeparateTaxLibrary library =
            new GainsSeparateTaxLibrary();

    static {

        // Add an instance of this class to the capital gains tax library.
        GainsTaxLibrary.addLibrary(getInstance());
    }

    /**
     * Constructs the capital gains tax library, married-filing-separately.
     */
    private GainsSeparateTaxLibrary() {

        // Nothing to do here not. Added only to make the constructor private.
    }

    /**
     * Gets a married-filing-separately capital gains tax library instance.
     *
     * @return A married-filing-separately capital gains tax library instance
     */
    static @NotNull GainsSeparateTaxLibrary getInstance() {
        return library;
    }

    @Override
    public @NotNull FilingStatus getFilingStatus() {
        return FilingStatus.SEPARATE;
    }
}
