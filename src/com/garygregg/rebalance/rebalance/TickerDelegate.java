package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.hierarchy.Ticker;
import org.jetbrains.annotations.NotNull;

class TickerDelegate extends ReceiverDelegate<Ticker> {

    /**
     * Constructs the ticker delegate with an explicit multiplier.
     *
     * @param ticker     The ticker from whom we are delegated
     * @param multiplier The multiplier
     */
    public TickerDelegate(@NotNull Ticker ticker, double multiplier) {
        super(ticker, (0. == multiplier) ? ticker.getWeight() :
                ticker.getWeight() * Math.abs(multiplier));
    }

    /**
     * Constructs the ticker delegate with a default multiplier.
     *
     * @param ticker The ticker from whom we are delegated
     */
    public TickerDelegate(@NotNull Ticker ticker) {
        this(ticker, 1.);
    }

    @Override
    public Currency getProposed() {
        return getReceiver().getProposed();
    }

    @Override
    public void onCannotSet() {

        /*
         * Call the superclass method, then pass through proposed value in
         * the ticker.
         */
        super.onCannotSet();
        getReceiver().passThrough();
    }
}
