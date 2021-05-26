package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.countable.Currency;
import org.jetbrains.annotations.NotNull;

public class ValueByProposed implements Valuator {

    @Override
    public Currency getValue(@NotNull Queryable<?, ?> queryable) {
        return queryable.getProposed();
    }
}
