package com.garygregg.rebalance.distinguished;

import com.garygregg.rebalance.Library;
import org.jetbrains.annotations.NotNull;

abstract class DistinguishedLibrary<KeyType, DescriptionType, ValueType>
        extends Library<KeyType, DescriptionType> {

    /**
     * Gets the value for a key.
     *
     * @return The key for which to get a value
     */
    public abstract ValueType getValue(@NotNull KeyType key);
}
