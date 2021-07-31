package com.garygregg.rebalance.tax;

import com.garygregg.rebalance.Description;
import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.countable.Percent;
import org.jetbrains.annotations.NotNull;

public class TaxDescription implements Description<Currency> {

    // The threshold of the tax rate
    private final Currency threshold;

    // The tax rate for the threshold
    private final Percent taxRate;

    /**
     * Constructs the tax description.
     *
     * @param threshold The threshold for the tax rate
     * @param taxRate   The tax rate for the threshold
     */
    public TaxDescription(double threshold, double taxRate) {

        // Set the member variables.
        this.threshold = new Currency(threshold);
        this.taxRate = new Percent(taxRate);
    }

    @Override
    public @NotNull Currency getKey() {
        return getThreshold();
    }

    @Override
    public String getName() {
        return String.format("The tax rate with threshold of %s",
                getThreshold());
    }

    /**
     * Gets the tax rate for the threshold.
     *
     * @return The tax rate for the threshold
     */
    public @NotNull Percent getTaxRate() {
        return taxRate;
    }

    /**
     * Gets the threshold for the tax rate.
     *
     * @return The threshold for the tax rate
     */
    public @NotNull Currency getThreshold() {
        return threshold;
    }
}
