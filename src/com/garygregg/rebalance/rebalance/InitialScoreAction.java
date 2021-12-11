package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.countable.MutableCurrency;
import org.jetbrains.annotations.NotNull;

class InitialScoreAction extends ResidualProducerAction {

    /**
     * Constructs the initial score action.
     */
    public InitialScoreAction() {
        setRelative(false);
    }

    @Override
    protected boolean isConsidered(@NotNull ReceiverDelegate<?> delegate) {
        return true;
    }

    @Override
    protected @NotNull Currency produceResidual(
            @NotNull ReceiverDelegate<?> delegate,
            @NotNull Currency currency, boolean isRelative) {

        /*
         * Declare and initialize mutable currency to hold the incoming value.
         * Is the incoming value not relative?
         */
        final MutableCurrency incoming = new MutableCurrency(currency);
        if (!isRelative) {

            /*
             * The incoming value is not relative (a.k.a., it is absolute).
             * Subtract the current value of the delegate from the incoming
             * value.
             */
            incoming.subtract(delegate.getCurrent());
        }

        // Return the incoming value as immutable currency.
        return incoming.getImmutable();
    }
}
