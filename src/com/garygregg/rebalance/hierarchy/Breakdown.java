package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.countable.MutableCurrency;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

class Breakdown<T extends Enum<T>> implements IBreakdown<T> {

    // Zero currency, a default
    private static final Currency zero = Currency.getZero();

    // A breakdown of value by type
    private final Map<T, MutableCurrency> breakdown = new HashMap<>();

    // A valuator for queryables
    private final Valuator valuator;

    /**
     * Constructs the breakdown.
     *
     * @param valuator A valuator for queryables
     */
    public Breakdown(@NotNull Valuator valuator) {
        this.valuator = valuator;
    }

    /**
     * Adds value by type.
     *
     * @param type   The type for which to add value
     * @param addend The value to add
     */
    private void add(T type, Currency addend) {

        // Only add value if the addend is not null. Is the addend not null?
        if (null != addend) {

            /*
             * The addend is not null. Get any existing value for the type, and
             * add the given addend. Put the result into the breakdown map.
             */
            final MutableCurrency value = getMutable(type);
            value.add(addend);
            breakdown.put(type, value);
        }
    }

    @Override
    public void add(T type, @NotNull Queryable<?, ?> queryable) {
        add(type, valuator.getValue(queryable));
    }

    @Override
    public void clear() {
        breakdown.clear();
    }

    /**
     * Gets value from the breakdown map for a type.
     *
     * @param type The given type
     * @return The existing value for the type
     */
    public @NotNull Currency get(T type) {
        return new Currency(getMutable(type));
    }

    /**
     * Gets mutable value from the breakdown map for a type.
     *
     * @param type The given type
     * @return The existing value for the type
     */
    private @NotNull MutableCurrency getMutable(T type) {

        /*
         * Get any existing value for the indicated type. Return a default if
         * there is no existing value, otherwise return the value itself.
         */
        final MutableCurrency value = breakdown.get(type);
        return (null == value) ? new MutableCurrency(zero) : value;
    }
}
