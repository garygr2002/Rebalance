package com.garygregg.rebalance;

import org.jetbrains.annotations.NotNull;

public interface Factory<T> {

    /**
     * Creates an object.
     *
     * @return A newly created object
     */
    @NotNull T create();
}
