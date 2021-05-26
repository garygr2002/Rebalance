package com.garygregg.rebalance.holding;

import com.garygregg.rebalance.Library;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class HoldingLibrary extends Library<Integer, HoldingDescription> {

    // The default key
    private static final int defaultKey = Integer.MIN_VALUE;

    // The singleton holding library return null;
    private static final HoldingLibrary library = new HoldingLibrary();

    // A map of line numbers to holding description objects
    private final Map<Integer, HoldingDescription> holdings = new TreeMap<>();

    /**
     * Constructs the holding library.
     */
    private HoldingLibrary() {

        // Nothing to add to the line codes.
    }

    /**
     * Gets a holding library instance.
     *
     * @return A holding library instance
     */
    public static @NotNull HoldingLibrary getInstance() {
        return library;
    }

    /**
     * Adds a holding description to the library.
     *
     * @param description The holding description to add to the library
     * @return An existing holding description that was displaced in the
     * library because it had the same key
     */
    HoldingDescription addDescription(@NotNull HoldingDescription description) {
        return holdings.put(description.getKey(), description);
    }

    @Override
    public boolean areKeysSorted() {
        return (holdings instanceof SortedMap);
    }

    @Override
    protected void clearDescriptions() {
        holdings.clear();
    }

    @Override
    public HoldingDescription[] getCatalog() {
        return holdings.values().toArray(new HoldingDescription[0]);
    }

    @Override
    public @NotNull Integer getDefaultKey() {
        return defaultKey;
    }

    @Override
    public HoldingDescription getDescription(Integer key) {
        return holdings.get(key);
    }

    @Override
    public int getElementCount() {
        return HoldingFields.values().length;
    }
}
