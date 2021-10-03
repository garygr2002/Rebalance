package com.garygregg.rebalance.tax;

import com.garygregg.rebalance.FilingStatus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public abstract class IncomeTaxLibrary extends TaxLibrary {

    // The tax library vending map
    private static final Map<FilingStatus, IncomeTaxLibrary> map
            = new HashMap<>();

    /**
     * Adds an income tax library.
     *
     * @param library The income tax library to add
     * @return Any income tax library previously associated with the filing
     * status
     */
    @SuppressWarnings("UnusedReturnValue")
    static IncomeTaxLibrary addLibrary(@NotNull IncomeTaxLibrary library) {
        return map.put(library.getFilingStatus(), library);
    }

    /**
     * Gets an income tax library based on a filing status.
     *
     * @param filingStatus The filing status
     * @return The income tax library most recently associated with the filing
     * status, or null if there is no associated library
     */
    public static IncomeTaxLibrary getLibrary(FilingStatus filingStatus) {
        return map.get(filingStatus);
    }

    /**
     * Gets the filing status of the library.
     *
     * @return The filing status of the library
     */
    public abstract @NotNull FilingStatus getFilingStatus();
}
