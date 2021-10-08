package com.garygregg.rebalance.tax;

import com.garygregg.rebalance.FilingStatus;
import org.jetbrains.annotations.NotNull;

public class IncomeHeadTaxLibrary extends IncomeTaxLibrary {

    // The singleton income tax library, file head-of-household
    private static final IncomeHeadTaxLibrary library =
            new IncomeHeadTaxLibrary();

    static {

        // Add an instance of this class to the income tax library.
        IncomeTaxLibrary.addLibrary(getInstance());
    }

    /**
     * Constructs the income tax library, filing head-of-household.
     */
    private IncomeHeadTaxLibrary() {

        // Nothing to do here not. Added only to make the constructor private.
    }

    /**
     * Gets a filing head-of-household income tax library instance.
     *
     * @return A filing head-of-household income tax library instance
     */
    static @NotNull IncomeHeadTaxLibrary getInstance() {
        return library;
    }

    @Override
    public @NotNull FilingStatus getFilingStatus() {
        return FilingStatus.HEAD;
    }
}
