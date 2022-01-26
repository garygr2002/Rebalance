package com.garygregg.rebalance.distinguished;

import com.garygregg.rebalance.toolkit.AccountKey;
import org.jetbrains.annotations.NotNull;

public class DistinguishedAccountDescription extends
        DistinguishedDescription<DistinguishedAccount, AccountKey> {

    // The corresponding portfolio key
    private final String portfolioKey;

    /**
     * Constructs the description.
     *
     * @param key          The key of the description
     * @param value        The value assigned to the key
     * @param portfolioKey The corresponding portfolio key
     */
    public DistinguishedAccountDescription(@NotNull DistinguishedAccount key,
                                           @NotNull AccountKey value,
                                           @NotNull String portfolioKey) {

        /*
         * Call the superclass method, then set the corresponding portfolio
         * key.
         */
        super(key, value);
        this.portfolioKey = portfolioKey;
    }

    /**
     * Gets the corresponding portfolio key.
     *
     * @return The corresponding portfolio key
     */
    @SuppressWarnings("unused")
    public String getPortfolioKey() {
        return portfolioKey;
    }
}
