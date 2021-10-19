package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.hierarchy.Account;
import com.garygregg.rebalance.hierarchy.Ticker;
import org.jetbrains.annotations.NotNull;

public class PassThroughRebalancer extends EnumeratingRebalancer {

    // A ticker action that passes considered value through to proposed value
    private final TickerAction action = new TickerAction() {

        @Override
        public boolean perform(@NotNull Ticker child) {

            /*
             * Set the proposed shares the same as the considered shares, and
             * return success.
             */
            child.setProposedShares(child.getConsideredShares().getValue());
            return true;
        }
    };

    @Override
    protected @NotNull Action<Account, Ticker> getTickerAction() {
        return action;
    }
}
