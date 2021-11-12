package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.Pair;
import com.garygregg.rebalance.countable.Currency;
import org.jetbrains.annotations.NotNull;

class ReallocationScore extends Pair<Currency, Double> implements
        Comparable<ReallocationScore> {

    /**
     * Constructs the reallocation score.
     *
     * @param residual        The residual
     * @param averageAbsolute The average absolute residual
     */
    public ReallocationScore(@NotNull Currency residual,
                             @NotNull Double averageAbsolute) {
        super(residual, averageAbsolute);
    }

    @Override
    public int compareTo(@NotNull ReallocationScore reallocationScore) {
        return compare((Pair<Currency, Double>) this, reallocationScore);
    }
}
