package com.garygregg.rebalance.detailed;

import com.garygregg.rebalance.AccountKey;
import com.garygregg.rebalance.AccountKeyLibrary;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class DetailedLibrary extends AccountKeyLibrary<DetailedDescription> {

    // The singleton detailed library
    private static final DetailedLibrary library = new DetailedLibrary();

    // A map of account numbers to detailed description objects
    private final Map<AccountKey, DetailedDescription> detaileds =
            new TreeMap<>();

    /**
     * Constructs the detailed library.
     */
    private DetailedLibrary() {

        // Nothing to do here yet.
    }

    /**
     * Gets a detailed library instance.
     *
     * @return A detailed library instance
     */
    public static @NotNull DetailedLibrary getInstance() {
        return library;
    }

    /**
     * Adds a detailed description to the library.
     *
     * @param description The detailed description to add to the library
     * @return An existing detailed description that was displaced in the
     * library because it had the same number
     */
    DetailedDescription addDescription(@NotNull DetailedDescription
                                               description) {
        return detaileds.put(description.getKey(), description);
    }

    @Override
    public boolean areKeysSorted() {
        return (detaileds instanceof SortedMap);
    }

    @Override
    protected void clearDescriptions() {
        detaileds.clear();
    }

    @Override
    public DetailedDescription[] getCatalog() {
        return detaileds.values().toArray(new DetailedDescription[0]);
    }

    @Override
    public DetailedDescription getDescription(@NotNull AccountKey key) {
        return detaileds.get(key);
    }

    @Override
    public int getElementCount() {
        return DetailedFields.values().length;
    }
}
