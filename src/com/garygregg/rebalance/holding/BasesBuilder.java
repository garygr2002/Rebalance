package com.garygregg.rebalance.holding;

import com.garygregg.rebalance.toolkit.HoldingType;
import org.jetbrains.annotations.NotNull;

public class BasesBuilder extends HoldingsBuilder {

    @Override
    protected @NotNull HoldingType getHoldingType() {
        return HoldingType.BASIS;
    }
}
