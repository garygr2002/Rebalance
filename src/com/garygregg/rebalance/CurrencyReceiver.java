package com.garygregg.rebalance;

import com.garygregg.rebalance.countable.Currency;
import org.jetbrains.annotations.NotNull;

public interface CurrencyReceiver {

    /**
     * Sets the proposed value of the receiver.
     *
     * @param currency The proposed value of the receiver
     * @return The residual that could not be set
     */
    @NotNull Currency setProposed(@NotNull Currency currency);
}
