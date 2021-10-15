package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.hierarchy.Account;
import org.jetbrains.annotations.NotNull;

abstract class AccountRebalancer extends Rebalancer {

    /**
     * Rebalances an account.
     *
     * @param account The account to rebalance
     * @return True if the account was successfully rebalanced; false
     * otherwise
     */
    public abstract boolean rebalance(@NotNull Account account);
}
