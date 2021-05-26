package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.countable.Currency;
import org.jetbrains.annotations.NotNull;

public interface Valuator {

    /**
     * Gets the value of a queryable.
     *
     * @param queryable A queryable
     * @return The value of a queryable
     */
    Currency getValue(@NotNull Queryable<?, ?> queryable);
}
