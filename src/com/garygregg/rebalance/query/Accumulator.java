package com.garygregg.rebalance.query;

import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.hierarchy.Queryable;
import org.jetbrains.annotations.NotNull;

interface Accumulator<AccumulationType> {

    /**
     * Accumulates a queryable.
     *
     * @param current  The current accumulation
     * @param queryable A queryable
     */
    void accumulate(@NotNull AccumulationType current,
                    @NotNull Queryable<?, ?> queryable);

    /**
     * Accumulates currency.
     *
     * @param current The current accumulation
     * @param value   A value
     */
    void accumulate(@NotNull AccumulationType current,
                    @NotNull Currency value);

    /**
     * Transforms an accumulation into currency.
     *
     * @param current The current accumulation
     * @return The result of the accumulation as currency
     */
    Currency transform(@NotNull AccumulationType current);
}
