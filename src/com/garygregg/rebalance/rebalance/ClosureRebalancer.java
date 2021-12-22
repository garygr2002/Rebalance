package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.WeightType;
import com.garygregg.rebalance.hierarchy.Account;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

class ClosureRebalancer extends WeightRebalancer {

    @Override
    protected @NotNull Map<WeightType, Double> getWeights(@NotNull Account account,
                                                          boolean adjust) {

        // TODO: Change this to use getWeightsForClosure(Account, boolean).
        return super.getWeights(account, adjust);
    }
}
