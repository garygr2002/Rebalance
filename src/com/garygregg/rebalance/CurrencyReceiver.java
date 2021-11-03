package com.garygregg.rebalance;

import com.garygregg.rebalance.countable.Currency;
import org.jetbrains.annotations.NotNull;

public interface CurrencyReceiver {

    /**
     * Sets the proposed value of the receiver.
     *
     * @param currency       The proposed value of the receiver
     * @param okayToTakeMore True if it is okay for this receiver to take more
     *                       than the proposed value; false otherwise
     */
    void setProposed(@NotNull Currency currency, boolean okayToTakeMore);
}
