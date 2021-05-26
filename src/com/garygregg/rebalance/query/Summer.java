package com.garygregg.rebalance.query;

import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.countable.MutableCurrency;
import org.jetbrains.annotations.NotNull;

abstract class Summer implements Accumulator<MutableCurrency> {

    @Override
    public void accumulate(@NotNull MutableCurrency current,
                           @NotNull Currency value) {
        current.add(value);
    }

    @Override
    public Currency transform(@NotNull MutableCurrency current) {
        return new Currency(current.getValue());
    }
}
