package com.garygregg.rebalance.tax;

import com.garygregg.rebalance.FilingStatus;
import com.garygregg.rebalance.countable.Currency;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public abstract class IncomeTaxLibrary extends TaxLibrary {

    // The income tax library vending map
    private static final Map<FilingStatus, IncomeTaxLibrary> libraryMap
            = new HashMap<>();

    // The standard deduction for the library
    private Currency standardDeduction;

    /**
     * Constructs the income tax library.
     */
    IncomeTaxLibrary() {
        clearStandardDeduction();
    }

    /**
     * Adds an income tax library.
     *
     * @param library The income tax library to add
     * @return Any income tax library previously associated with the filing
     * status
     */
    @SuppressWarnings("UnusedReturnValue")
    static IncomeTaxLibrary addLibrary(@NotNull IncomeTaxLibrary library) {
        return libraryMap.put(library.getFilingStatus(), library);
    }

    /**
     * Checks whether there is an income tax library for each filing status.
     *
     * @return True if there is an income tax library for each filing status;
     * false otherwise
     */
    public static boolean checkContract() {
        return TaxLibrary.checkContract(libraryMap);
    }

    /**
     * Gets an income tax library based on a filing status.
     *
     * @param filingStatus The filing status
     * @return The income tax library most recently associated with the filing
     * status, or null if there is no associated library
     */
    public static IncomeTaxLibrary getLibrary(
            @NotNull FilingStatus filingStatus) {
        return libraryMap.get(filingStatus);
    }

    /**
     * Clears the standard deduction.
     */
    void clearStandardDeduction() {
        standardDeduction = null;
    }

    /**
     * Gets the standard deduction for the library.
     *
     * @return The standard deduction for the library
     */
    public Currency getStandardDeduction() {
        return standardDeduction;
    }

    /**
     * Sets the standard deduction for the library.
     *
     * @param standardDeduction The standard deduction for the library
     */
    void setStandardDeduction(@NotNull Currency standardDeduction) {
        this.standardDeduction = standardDeduction;
    }
}
