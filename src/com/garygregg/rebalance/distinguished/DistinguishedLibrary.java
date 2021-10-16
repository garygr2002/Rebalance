package com.garygregg.rebalance.distinguished;

import com.garygregg.rebalance.Library;
import org.jetbrains.annotations.NotNull;

import java.util.*;

abstract class DistinguishedLibrary<KeyType extends Comparable<KeyType>,
        DescriptionType extends DistinguishedDescription<KeyType, ValueType>,
        ValueType> extends Library<KeyType, DescriptionType> {

    // A map of keys to descriptions
    private final Map<KeyType, DescriptionType> descriptionMap =
            new TreeMap<>();

    // A map of values to keys
    private final Map<ValueType, KeyType> keyMap = new HashMap<>();

    /**
     * Adds a distinguished description to the library.
     *
     * @param description The distinguished description to add to the library
     * @return An existing distinguished description that was displaced in the
     * library because it had the same key
     */
    @SuppressWarnings("UnusedReturnValue")
    DescriptionType addDescription(@NotNull DescriptionType description) {

        /*
         * Get the key from the description. Map the value to the key, and add
         * the description to the description map.
         */
        final KeyType account = description.getKey();
        addValue(account, description.getValue());
        return descriptionMap.put(account, description);
    }

    /**
     * Adds a key to be mapped to a value.
     *
     * @param key   The key
     * @param value The value
     * @return Any key previously mapped to the value
     */
    @SuppressWarnings("UnusedReturnValue")
    protected KeyType addValue(@NotNull KeyType key,
                               @NotNull ValueType value) {
        return keyMap.put(value, key);
    }

    @Override
    public boolean areKeysSorted() {
        return (descriptionMap instanceof SortedMap);
    }

    @Override
    protected void clearDescriptions() {

        // Clear both the key map and the description map.
        keyMap.clear();
        descriptionMap.clear();
    }

    @Override
    public DescriptionType getDescription(KeyType key) {
        return descriptionMap.get(key);
    }

    /**
     * Gets a collection of descriptions.
     *
     * @return A collection of descriptions
     */
    protected @NotNull Collection<DescriptionType> getDescriptions() {
        return List.copyOf(descriptionMap.values());
    }

    /**
     * Gets a key for a value.
     *
     * @param value The value for which to get a key
     * @return The key, if any, mapped to the value
     */
    public KeyType getKey(@NotNull ValueType value) {
        return keyMap.get(value);
    }

    /**
     * Gets the value for a key.
     *
     * @return The key for which to get a value
     */
    public abstract ValueType getValue(@NotNull KeyType key);
}
