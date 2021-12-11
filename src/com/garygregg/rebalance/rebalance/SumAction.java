package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.countable.MutableCurrency;
import org.jetbrains.annotations.NotNull;

abstract class SumAction
        extends ContainerNodeAction<ReceiverDelegate<?>, MutableCurrency> {

    // The initial value for the container in the action
    private static final double initialValue = 0.;

    // The sum of the values of the delegates
    private final MutableCurrency sum = getContained();

    @Override
    protected @NotNull MutableCurrency getInitialValue() {
        return new MutableCurrency(initialValue);
    }

    /**
     * Gets the mutable sum.
     *
     * @return The mutable sum
     */
    protected @NotNull MutableCurrency getMutableSum() {
        return sum;
    }

    /**
     * Gets the sum.
     *
     * @return The sum
     */
    public @NotNull Currency getSum() {
        return getMutableSum().getImmutable();
    }

    @Override
    public void reset() {
        sum.set(initialValue);
    }
}
