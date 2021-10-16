package com.garygregg.rebalance.distinguished;

import com.garygregg.rebalance.Library;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

abstract class DistinguishedLibrary<KeyType, DescriptionType, ValueType>
        extends Library<KeyType, DescriptionType> {

    // A map of values to keys
    private final Map<ValueType, KeyType> keyMap = new HashMap<>();

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
