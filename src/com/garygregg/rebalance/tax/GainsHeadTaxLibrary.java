package com.garygregg.rebalance.tax;

import com.garygregg.rebalance.FilingStatus;
import org.jetbrains.annotations.NotNull;

public class GainsHeadTaxLibrary extends GainsTaxLibrary {

    // The singleton capital gains tax library, file head-of-household
    private static final GainsHeadTaxLibrary library =
            new GainsHeadTaxLibrary();

    static {

        // Add an instance of this class to the capital gains tax library.
        GainsTaxLibrary.addLibrary(getInstance());
    }

    /**
     * Constructs the capital gains tax library, filing head-of-household.
     */
    private GainsHeadTaxLibrary() {

        // Nothing to do here not. Added only to make the constructor private.
    }

    /**
     * Gets a filing head-of-household capital gains tax library instance.
     *
     * @return A filing head-of-household capital gains tax library instance
     */
    static @NotNull GainsHeadTaxLibrary getInstance() {
        return library;
    }

    @Override
    public @NotNull FilingStatus getFilingStatus() {
        return FilingStatus.HEAD;
    }
}
