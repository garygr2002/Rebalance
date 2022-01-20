package com.garygregg.rebalance.distinguished;

import org.jetbrains.annotations.NotNull;

public class DistinguishedPortfolioDescription extends
        DistinguishedDescription<DistinguishedPortfolio, String> {

    /**
     * Constructs the description.
     *
     * @param key   The key of the description
     * @param value The value assigned to the key
     */
    public DistinguishedPortfolioDescription(
            @NotNull DistinguishedPortfolio key, @NotNull String value) {
        super(key, value);
    }
}
