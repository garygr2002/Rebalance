package com.garygregg.rebalance.toolkit;

import com.garygregg.rebalance.countable.Currency;
import org.jetbrains.annotations.NotNull;

@FunctionalInterface
public interface Balanceable {

    /**
     * Sets the residual of a rebalance operation.
     *
     * @param residual The residual of a rebalance operation
     */
    void setResidual(@NotNull Currency residual);
}
