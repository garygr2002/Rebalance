package com.garygregg.rebalance;

import com.garygregg.rebalance.countable.Currency;
import org.jetbrains.annotations.NotNull;

public interface CurrencyReceiver {

    /**
     * Clears the last snapshot.
     */
    void clearSnapshot();

    /**
     * Recovers the last snapshot.
     */
    void recoverSnapshot();

    /**
     * Sets the proposed value of the receiver.
     *
     * @param currency The proposed value of the receiver
     * @return The difference between what was desired and what was actually
     * set (the 'residual'); this will be positive if a receiver took too
     * little, negative if it took too much
     */
    @NotNull Currency setProposed(@NotNull Currency currency);

    /**
     * Takes a snapshot.
     */
    void takeSnapshot();
}
