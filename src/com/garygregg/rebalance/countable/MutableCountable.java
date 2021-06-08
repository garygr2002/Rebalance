package com.garygregg.rebalance.countable;

public abstract class MutableCountable implements IMutableCountable {

    // The value of the countable
    private double value;

    /**
     * Constructs a mutable countable.
     *
     * @param value    The value of the countable
     * @param truncate True if the value should be truncated, false if rounded
     */
    public MutableCountable(double value, boolean truncate) {
        set(value, truncate);
    }

    /**
     * Constructs a mutable countable with a default truncate flag.
     *
     * @param value The value of the countable
     */
    public MutableCountable(double value) {
        set(value);
    }

    /**
     * Clears any immutable value container.
     */
    protected abstract void clear();

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
