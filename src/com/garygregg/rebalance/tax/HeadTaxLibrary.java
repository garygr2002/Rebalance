package com.garygregg.rebalance.tax;

import com.garygregg.rebalance.FilingStatus;
import org.jetbrains.annotations.NotNull;

public class HeadTaxLibrary extends IncomeTaxLibrary {

    // The singleton income tax library, file head-of-household
    private static final HeadTaxLibrary library = new HeadTaxLibrary();

    /**
     * Constructs the income tax library, filing head-of-household.
     */
    private HeadTaxLibrary() {

        // Nothing to do here not. Added only to make the constructor private.
    }

    /**
     * Gets a filing head-of-household income tax library instance.
     *
     * @return A filing head-of-household income tax library instance
     */
    public static @NotNull HeadTaxLibrary getInstance() {
        return library;
    }

    @Override
    public @NotNull FilingStatus getFilingStatus() {
        return FilingStatus.HEAD;
    }
}
