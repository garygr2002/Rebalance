package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.countable.MutableCurrency;
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
    public boolean doRebalance(@NotNull Account account,
                               @NotNull MutableCurrency residual) {
        return perform(account, getTickerAction());
    }
}
