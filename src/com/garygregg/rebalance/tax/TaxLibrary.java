package com.garygregg.rebalance.tax;

import com.garygregg.rebalance.Library;
import com.garygregg.rebalance.countable.Currency;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

abstract class TaxLibrary extends Library<Currency, TaxDescription> {

    // A map of thresholds to tax description objects
    private final Map<Currency, TaxDescription> brackets = new TreeMap<>();

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
}
