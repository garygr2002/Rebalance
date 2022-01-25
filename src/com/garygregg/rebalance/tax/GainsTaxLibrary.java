package com.garygregg.rebalance.tax;

import com.garygregg.rebalance.FilingStatus;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

public abstract class GainsTaxLibrary extends TaxLibrary {

    // The capital gains tax library vending map
    private static final Map<FilingStatus, GainsTaxLibrary> libraryMap
            = new EnumMap<>(FilingStatus.class);

    /**
     * Adds a capital tax library.
     *
     * @param library The capital gains tax library to add
     * @return Any capital gains tax library previously associated with the
     * filing status
     */
    @SuppressWarnings("UnusedReturnValue")
    static GainsTaxLibrary addLibrary(@NotNull GainsTaxLibrary library) {
        return libraryMap.put(library.getFilingStatus(), library);
    }

    /**
     * Checks whether there is a capital gains tax library for each filing
     * status.
     *
     * @return True if there is a capital gains tax library for each filing
     * status; false otherwise
     */
    public static boolean checkContract() {
        return TaxLibrary.checkContract(libraryMap);
    }

    /**
     * Gets a capital tax library based on a filing status.
     *
     * @param filingStatus The filing status
     * @return The capital tax library most recently associated with the filing
     * status, or null if there is no associated library
     */
    public static GainsTaxLibrary getLibrary(FilingStatus filingStatus) {
        return libraryMap.get(filingStatus);
    }
}
