package com.garygregg.rebalance.tax;

import com.garygregg.rebalance.FilingStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public abstract class GainsTaxLibrary extends TaxLibrary {

    // The capital gains tax library vending map
    private static final Map<FilingStatus, GainsTaxLibrary> map
            = new HashMap<>();

    /**
     * Adds a capital tax library.
     *
     * @param library The capital gains tax library to add
     * @return Any capital gains tax library previously associated with the
     * filing status
     */
    @SuppressWarnings("UnusedReturnValue")
    static GainsTaxLibrary addLibrary(@NotNull GainsTaxLibrary library) {
        return map.put(library.getFilingStatus(), library);
    }

    /**
     * Gets a capital tax library based on a filing status.
     *
     * @param filingStatus The filing status
     * @return The capital tax library most recently associated with the filing
     * status, or null if there is no associated library
     */
    public static GainsTaxLibrary getLibrary(FilingStatus filingStatus) {
        return map.get(filingStatus);
    }

    /**
     * Gets the filing status of the library.
     *
     * @return The filing status of the library
     */
    public abstract @NotNull FilingStatus getFilingStatus();
}
