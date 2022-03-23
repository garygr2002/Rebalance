package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.hierarchy.Ticker;
import org.jetbrains.annotations.NotNull;

class TickerDelegate extends ReceiverDelegate<Ticker> {

    /**
     * Constructs the ticker delegate with an explicit divisor.
     *
     * @param ticker  The ticker from whom we are delegated
     * @param divisor The divisor
     */
    public TickerDelegate(@NotNull Ticker ticker, double divisor) {
        super(ticker, (0. == divisor) ? ticker.getWeight() :
                ticker.getWeight() / Math.abs(divisor));
    }

    /**
     * Constructs the ticker delegate with an implicit divisor.
     *
     * @param ticker The ticker from whom we are delegated
     */
    public TickerDelegate(@NotNull Ticker ticker) {
        this(ticker, 0.);
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
