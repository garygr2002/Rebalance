package com.garygregg.rebalance.portfolio;

import com.garygregg.rebalance.Description;
import com.garygregg.rebalance.WeightType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PortfolioDescription implements Description<String> {

    // A map of weight types to their desired allocation weights
    private final Map<WeightType, Double> allocation = new HashMap<>();

    // The mnemonic of the portfolio
    private final String mnemonic;

    // The name associated with the portfolio
    private String name;

    /**
     * Constructs the portfolio description.
     *
     * @param mnemonic The mnemonic of the portfolio
     */
    PortfolioDescription(@NotNull String mnemonic) {
        this.mnemonic = mnemonic;
    }

    /**
     * Adjusts the desired allocation weight of the designated fund type.
     *
     * @param type  The designated weight type
     * @param value The desired allocation weight
     */
    void adjustAllocation(@NotNull WeightType type, double value) {
        allocation.put(type, value);
    }

    /**
     * Gets the desired allocation weight of the designated fund type.
     *
     * @param type The designated fund type
     * @return The desired allocation weight for the designated fund type
     */
    public Double getAllocation(WeightType type) {
        return allocation.get(type);
    }

    @Override
    public @NotNull String getKey() {
        return getMnemonic();
    }

    /**
     * Gets the mnemonic of the account.
     *
     * @return The mnemonic of the account
     */
    public @NotNull String getMnemonic() {
        return mnemonic;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Sets the name associated with the portfolio.
     *
     * @param name The name associated with the portfolio
     */
    void setName(String name) {
        this.name = name;
    }
}
