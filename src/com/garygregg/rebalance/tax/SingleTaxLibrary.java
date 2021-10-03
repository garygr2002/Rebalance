package com.garygregg.rebalance.tax;

import com.garygregg.rebalance.FilingStatus;
import org.jetbrains.annotations.NotNull;

public class SingleTaxLibrary extends IncomeTaxLibrary {

    // The singleton income tax library, filing single
    private static final SingleTaxLibrary library = new SingleTaxLibrary();

    static {

        // Add an instance of this class to the income tax library.
        IncomeTaxLibrary.addLibrary(getInstance());
    }

    /**
     * Constructs the income tax library, filing single.
     */
    private SingleTaxLibrary() {

        // Nothing to do here not. Added only to make the constructor private.
    }

    /**
     * Gets a filing single income tax library instance.
     *
     * @return A filing single income tax library instance
     */
    static @NotNull SingleTaxLibrary getInstance() {
        return library;
    }

    @Override
    public @NotNull FilingStatus getFilingStatus() {
        return FilingStatus.SINGLE;
    }
}
