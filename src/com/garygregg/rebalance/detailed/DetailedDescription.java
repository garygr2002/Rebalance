package com.garygregg.rebalance.detailed;

import com.garygregg.rebalance.AccountKey;
import com.garygregg.rebalance.Description;
import com.garygregg.rebalance.WeightType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class DetailedDescription implements Description<AccountKey> {

    // A map of weight types to their desired allocation weights
    private final Map<WeightType, Double> allocation = new HashMap<>();

    // The description key
    private final AccountKey key;

    // The name of the associated account
    private final String name;

    /**
     * Constructs the detail description.
     *
     * @param institution The institution where the account is held
     * @param number      The number of the associated account
     * @param name        The name of the associated account
     */
    DetailedDescription(@NotNull String institution,
                        @NotNull Long number,
                        String name) {

        /*
         * Preset the weight for the 'all' weight type to 100. Set all the
         * other member fields.
         */
        this.allocation.put(WeightType.ALL, 100.);
        this.key = new AccountKey(institution, number);
        this.name = name;
    }

    /**
     * Adjusts the desired allocation weight of the designated weight type.
     *
     * @param type  The designated weight type
     * @param value The desired allocation weight
     */
    void adjustAllocation(@NotNull WeightType type, double value) {
        allocation.put(type, value);
    }

    /**
     * Gets the desired allocation weight of the designated weight type.
     *
     * @param type The designated weight type
     * @return The desired allocation weight for the designated weight type
     */
    public Double getAllocation(WeightType type) {
        return allocation.get(type);
    }

    /**
     * Gets the institution where the account is held.
     *
     * @return The institution where the account is held
     */
    public String getInstitution() {
        return key.getFirst();
    }

    @Override
    public @NotNull AccountKey getKey() {
        return key;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Gets the number of the associated account.
     *
     * @return The number of the associated account
     */
    public @NotNull Long getNumber() {
        return key.getSecond();
    }
}
