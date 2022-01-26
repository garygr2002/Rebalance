package com.garygregg.rebalance.holding;

import com.garygregg.rebalance.toolkit.HoldingType;
import org.jetbrains.annotations.NotNull;

public class ValuationsBuilder extends HoldingsBuilder {

    @Override
    protected @NotNull HoldingType getHoldingType() {
        return HoldingType.VALUATION;
    }

    @Override
    public @NotNull String getPrefix() {
        return "holding";
    }
}
