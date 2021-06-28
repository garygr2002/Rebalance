package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.countable.MutableCurrency;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

abstract class Breakdown<EnumType extends Enum<EnumType>,
        HierarchyType extends Common<?, ?, ?>>
        implements IBreakdown<EnumType, HierarchyType> {

    // A breakdown of value by type
    private final Map<EnumType, MutableCurrency> breakdown = new HashMap<>();

    @Override
    public void clear() {
        breakdown.clear();
    }

    /**
     * Gets a value from the breakdown map for a type.
     *
     * @param type The given type
     * @return The existing value for the type
     */
    public @NotNull Currency get(EnumType type) {
        return getMutable(type).getImmutable();
    }

    /**
     * Gets a value from the breakdown map for a type.
     *
     * @param type The given type
     * @return A value from the breakdown map for the type
     */
    protected @NotNull MutableCurrency getMutable(EnumType type) {

        /*
         * Get the current mutable value from the breakdown map for the type.
         * Is the current value null?
         */
        MutableCurrency currency = breakdown.get(type);
        if (null == currency) {

            /*
             * The current value is null. Add a new, zero value to the map for
             * the type.
             */
            put(type, currency = new MutableCurrency());
        }

        // Return the value.
        return currency;
    }

    /**
     * Puts a value in the breakdown map.
     *
     * @param type     The key value
     * @param currency The value to insert into the map
     * @return Any previous value that was previously mapped using the same key
     */
    protected MutableCurrency put(EnumType type,
                                  @NotNull MutableCurrency currency) {
        return breakdown.put(type, currency);
    }
}
