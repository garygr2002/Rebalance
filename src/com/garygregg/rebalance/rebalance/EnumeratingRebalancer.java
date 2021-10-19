package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.hierarchy.Account;
import com.garygregg.rebalance.hierarchy.Ticker;
import org.jetbrains.annotations.NotNull;

abstract class EnumeratingRebalancer extends AccountRebalancer {

    /**
     * Gets the ticker action for the account.
     *
     * @return The ticker action for the account
     */
    protected abstract @NotNull Action<Account, Ticker> getTickerAction();

    @Override
    public boolean rebalance(@NotNull Account account) {
        return super.rebalance(account) && perform(account, getTickerAction());
    }
}
