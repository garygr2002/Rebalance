package com.garygregg.rebalance.query;

import com.garygregg.rebalance.countable.MutableCurrency;
import org.jetbrains.annotations.NotNull;

public class StartAtZeroQuery extends CurrencyQuery {

    /**
     * Constructs the query.
     *
     * @param accumulator The accumulator for this query
     */
    public StartAtZeroQuery(@NotNull Accumulator<MutableCurrency>
                                    accumulator) {
        super(accumulator, new MutableCurrency());
    }
}
