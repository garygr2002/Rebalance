package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.countable.MutableCurrency;
import com.garygregg.rebalance.hierarchy.Account;
import com.garygregg.rebalance.hierarchy.Ticker;
import org.jetbrains.annotations.NotNull;

public class PassThroughRebalancer extends EnumeratingRebalancer {

    // A ticker action that passes considered value through to proposed value
    private final TickerAction action = new TickerAction() {

        @Override
        public boolean perform(@NotNull Ticker child, boolean isLast) {

            /*
             * Pass the number of considered shares through to the number of
             * proposed shares, and return success.
             */
            child.passThrough();
            return true;
        }
    };

    @Override
    protected @NotNull Action<Account, Ticker> getTickerAction() {
        return action;
    }
}
