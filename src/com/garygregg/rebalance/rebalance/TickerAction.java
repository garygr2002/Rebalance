package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.hierarchy.Account;
import com.garygregg.rebalance.hierarchy.Ticker;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

abstract class TickerAction extends Action<Account, Ticker> {

    @Override
    public @NotNull Collection<Ticker> doGetChildren(
            @NotNull Account account) {
        return account.getChildren();
    }
}
