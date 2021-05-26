package com.garygregg.rebalance.query;

import com.garygregg.rebalance.countable.MutableCurrency;
import com.garygregg.rebalance.hierarchy.Queryable;
import org.jetbrains.annotations.NotNull;

class ShouldHaveSummer extends Summer {

    @Override
    public void accumulate(@NotNull MutableCurrency current,
                           @NotNull Queryable<?, ?> queryable) {
        accumulate(current, queryable.getProposed());
    }
}
