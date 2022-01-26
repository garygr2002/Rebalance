package com.garygregg.rebalance.tax;

import com.garygregg.rebalance.toolkit.FilingStatus;
import org.jetbrains.annotations.NotNull;

public final class IncomeSingleTaxLibrary extends IncomeTaxLibrary {

    // The singleton income tax library, filing single
    private static final IncomeSingleTaxLibrary library =
            new IncomeSingleTaxLibrary();

    static {

        // Add an instance of this class to the income tax library.
        IncomeTaxLibrary.addLibrary(getInstance());
    }

    /**
     * Constructs the income tax library, filing single.
     */
    private IncomeSingleTaxLibrary() {

        // Nothing to do here not. Added only to make the constructor private.
    }

    /**
     * Gets a filing single income tax library instance.
     *
     * @return A filing single income tax library instance
     */
    static @NotNull IncomeSingleTaxLibrary getInstance() {
        return library;
    }

    @Override
    public @NotNull FilingStatus getFilingStatus() {
        return FilingStatus.SINGLE;
    }
}
