package com.garygregg.rebalance.hierarchy;

import org.jetbrains.annotations.NotNull;

interface IBreakdown<T extends Enum<T>> {

    /**
     * Adds value from a queryable.
     *
     * @param type      The type for which to add value
     * @param queryable A queryable; its value will be determined by the the
     *                  valuator
     */
    void add(T type, @NotNull Queryable<?, ?> queryable);

    /**
     * Clears the breakdown map.
     */
    void clear();
}
