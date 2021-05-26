package com.garygregg.rebalance.query;

import com.garygregg.rebalance.countable.MutableCurrency;
import org.jetbrains.annotations.NotNull;

public class CurrencyQuery extends Query<MutableCurrency> {

    /**
     * Constructs the query.
     *
     * @param accumulator The accumulator for this query
     * @param initial     The initial value for the accumulator
     */
    public CurrencyQuery(@NotNull Accumulator<MutableCurrency> accumulator,
                         @NotNull MutableCurrency initial) {
        super(accumulator, initial);
    }
}
