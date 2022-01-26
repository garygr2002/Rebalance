package com.garygregg.rebalance.toolkit;

import org.jetbrains.annotations.NotNull;

public interface Description<KeyType> {

    /**
     * Gets the key of the description.
     *
     * @return The key of the description
     */
    @NotNull KeyType getKey();

    /**
     * Gets the name of the description.
     *
     * @return The name of the description
     */
    String getName();
}
