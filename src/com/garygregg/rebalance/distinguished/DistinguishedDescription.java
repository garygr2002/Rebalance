package com.garygregg.rebalance.distinguished;

import com.garygregg.rebalance.toolkit.Description;
import org.jetbrains.annotations.NotNull;

class DistinguishedDescription<KeyType, ValueType>
        implements Description<KeyType> {

    // The key
    private final KeyType key;

    // The value
    private final ValueType value;

    /**
     * Constructs the description.
     *
     * @param key   The key of the description
     * @param value The value assigned to the key
     */
    public DistinguishedDescription(@NotNull KeyType key,
                                    @NotNull ValueType value) {

        // Assign the member variables.
        this.key = key;
        this.value = value;
    }

    @Override
    public @NotNull KeyType getKey() {
        return key;
    }

    @Override
    public @NotNull String getName() {
        return getValue().toString();
    }

    /**
     * Gets the value assigned to the key.
     *
     * @return The value assigned to the key
     */
    public @NotNull ValueType getValue() {
        return value;
    }
}
