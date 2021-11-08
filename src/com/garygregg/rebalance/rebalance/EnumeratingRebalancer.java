package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.hierarchy.Account;
import com.garygregg.rebalance.hierarchy.Ticker;
import org.jetbrains.annotations.NotNull;

abstract class EnumeratingRebalancer extends AccountRebalancer {

    @Override
    public Currency doRebalance(@NotNull Account account) {

        /*
         * Perform the ticker action on the children, receiving a result.
         * Return the zero residual if the result was okay. Otherwise, return
         * null.
         */
        final boolean result = perform(account, getTickerAction());
        return result ? Currency.getZero() : null;
    }

    /**
     * Gets the ticker action for the account.
     *
     * @return The ticker action for the account
     */
    protected abstract @NotNull Action<Account, Ticker> getTickerAction();
}
