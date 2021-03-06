package com.garygregg.rebalance.countable;

public abstract class Countable implements ICountable {

    // The value of the countable
    private final double value;

    /**
     * Constructs a countable object.
     *
     * @param value     The value of the countable
     * @param precision The precision of the countable
     * @param truncate  True if the value should be truncated, false if rounded
     */
    Countable(double value, int precision,
              @SuppressWarnings("SameParameterValue") boolean truncate) {
        this.value = truncate ? ICountable.truncate(value, precision) :
                ICountable.round(value, precision);
    }

    /**
     * Constructs a countable with a default truncate flag.
     *
     * @param value     The value of the countable
     * @param precision The precision of the countable
     */
    Countable(double value, int precision) {
        this(value, precision, false);
    }

    @Override
    public double getValue() {
        return value;
    }
}
