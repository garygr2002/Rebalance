package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.hierarchy.Ticker;
import org.jetbrains.annotations.NotNull;

class TickerDelegate extends ReceiverDelegate<Ticker> {

    /**
     * Constructs the ticker delegate.
     *
     * @param ticker The ticker from whom we are delegated
     */
    public TickerDelegate(@NotNull Ticker ticker) {
        super(ticker, 1.);
    }

    @Override
    public Currency getProposed() {
        return getReceiver().getProposed();
    }
}
