package com.garygregg.rebalance.tax;

import com.garygregg.rebalance.FilingStatus;
import org.jetbrains.annotations.NotNull;

public class SeparateTaxLibrary extends IncomeTaxLibrary {

    // The singleton income tax library, married filing separately
    private static final SeparateTaxLibrary library = new SeparateTaxLibrary();

    /**
     * Constructs the income tax library, married filing separately.
     */
    private SeparateTaxLibrary() {

        // Nothing to do here not. Added only to make the constructor private.
    }

    /**
     * Gets a married filing separately income tax library instance.
     *
     * @return A married filing separately income tax library instance
     */
    public static @NotNull SeparateTaxLibrary getInstance() {
        return library;
    }

    @Override
    public @NotNull FilingStatus getFilingStatus() {
        return FilingStatus.SEPARATE;
    }
}
