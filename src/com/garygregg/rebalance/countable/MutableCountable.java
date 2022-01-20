package com.garygregg.rebalance.countable;

public abstract class MutableCountable implements IMutableCountable {

    // The value of the countable
    private double value;

    /**
     * Constructs a mutable countable with a default truncate flag.
     *
     * @param value The value of the countable
     */
    MutableCountable(double value) {
        set(value);
    }

    /**
     * Clears any immutable value container.
     */
    protected abstract void clear();

    /**
     * Gets the factor of the countable.
     *
     * @return The factor of the countable
     */
    public double getFactor() {
        return ICountable.calculateFactor(getPrecision());
    }

    @Override
    public double getValue() {
        return value;
    }

    @Override
    public double set(double value, boolean truncate) {

        /*
         * Clear any immutable value container. Set the value by truncation,
         * or rounding, as specified. Return the value that was set.
         */
        clear();
        this.value = truncate ? ICountable.truncate(value, getPrecision()) :
                ICountable.round(value, getPrecision());
        return getValue();
    }

    @Override
    public double set(double value) {
        return set(value, false);
    }
}
