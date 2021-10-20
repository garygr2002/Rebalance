package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.countable.Shares;
import com.garygregg.rebalance.hierarchy.Account;
import com.garygregg.rebalance.hierarchy.Ticker;
import org.jetbrains.annotations.NotNull;

public class PassThroughRebalancer extends EnumeratingRebalancer {

    // A ticker action that passes considered value through to proposed value
    private final TickerAction action = new TickerAction() {

        @Override
        public boolean perform(@NotNull Ticker child, boolean isLast) {

            // Get the value of the considered shares. Is the value not null?
            final Shares considered = child.getConsideredShares();
            final Double value = (null == considered) ? null :
                    considered.getValue();
            if (null != value) {

                /*
                 * The value is not null. Use the value to set the proposed
                 * shares.
                 */
                child.setProposedShares(value);
            }

            // Return success.
            return true;
        }
    };

    @Override
    protected @NotNull Action<Account, Ticker> getTickerAction() {
        return action;
    }
}
