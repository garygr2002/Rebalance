package com.garygregg.rebalance;

import com.garygregg.rebalance.countable.MutableCurrency;
import org.jetbrains.annotations.NotNull;

public interface Balanceable {

    /**
     * Sets the residual of a rebalance operation.
     *
     * @param residual The residual of a rebalance operation
     */
    void setResidual(@NotNull MutableCurrency residual);
}
