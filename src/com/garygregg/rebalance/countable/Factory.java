package com.garygregg.rebalance.countable;

import org.jetbrains.annotations.NotNull;

interface Factory<T> {

    /**
     * Returns a product of the factory.
     *
     * @return A product of the factory
     */
    @NotNull T produce();
}
