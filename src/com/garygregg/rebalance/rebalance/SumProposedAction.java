package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.countable.Currency;
import org.jetbrains.annotations.NotNull;

class SumProposedAction extends SumAction {

    @Override
    public void doAction(@NotNull ReceiverDelegate<?> delegate) {

        /*
         * Get the proposed value of the receiver. Is the proposed value not
         * null?
         */
        final Currency currency = delegate.getProposed();
        if (null != currency) {

            // The proposed value is not null. Add it to the mutable sum.
            getMutableSum().add(currency);
        }
    }
}
