package com.garygregg.rebalance.tax;

import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.toolkit.FilingStatus;
import com.garygregg.rebalance.toolkit.Library;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

abstract class TaxLibrary extends Library<Currency, TaxDescription> {

    // A map of thresholds to tax description objects
    private final Map<Currency, TaxDescription> brackets = new TreeMap<>();

    /**
     * Checks whether a library map fulfills the contract of there being a
     * non-null library for each filing status.
     *
     * @param libraryMap A library map
     * @param <T>        A tax library type
     * @return True if the map fulfills the contract; false otherwise
     */
    protected static <T extends TaxLibrary> boolean checkContract(
            @NotNull Map<FilingStatus, T> libraryMap) {

        // Get the filing statuses and their number.
        final FilingStatus[] status = FilingStatus.values();
        final int length = status.length;

        // Are there as many entries in the map as there are filing status?
        boolean contractFulfilled = !(libraryMap.size() < length);
        if (contractFulfilled) {

            /*
             * There are as many entries in the map as there are filing status.
             * Now make sure each entry is non-null.
             */
            for (int i = 0; (i < length) && contractFulfilled; ++i) {
                contractFulfilled = (null != libraryMap.get(status[i]));
            }
        }

        // Return whether the contract is fulfilled.
        return contractFulfilled;
    }

    /**
     * Adds a tax description to the library.
     *
     * @param description The tax description to add to the library
     * @return An existing tax description that was displaced in library
     * because it had the same threshold
     */
    TaxDescription addDescription(@NotNull TaxDescription description) {
        return brackets.put(description.getKey(), description);
    }

    @Override
    public boolean areKeysSorted() {
        return brackets instanceof SortedMap;
    }

    @Override
    protected void clearDescriptions() {
        brackets.clear();
    }

    @Override
    public TaxDescription[] getCatalog() {
        return brackets.values().toArray(new TaxDescription[0]);
    }

    @Override
    public @NotNull Currency getDefaultKey() {
        return Currency.getZero();
    }

    @Override
    public TaxDescription getDescription(Currency key) {
        return brackets.get(key);
    }

    @Override
    public int getElementCount() {
        return TaxFields.values().length;
    }

    /**
     * Gets the filing status of the library.
     *
     * @return The filing status of the library
     */
    public abstract @NotNull FilingStatus getFilingStatus();
}
