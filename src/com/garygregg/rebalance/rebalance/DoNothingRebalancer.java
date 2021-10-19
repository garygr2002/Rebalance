package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.hierarchy.Account;
import com.garygregg.rebalance.hierarchy.Ticker;
import org.jetbrains.annotations.NotNull;

class DoNothingRebalancer extends EnumeratingRebalancer {

    // A ticker action that does nothing
    private final TickerAction action = new TickerAction() {

        @Override
        public boolean perform(@NotNull Ticker child) {
            return true;
        }
    };

    @Override
    protected @NotNull Action<Account, Ticker> getTickerAction() {
        return action;
    }
}
