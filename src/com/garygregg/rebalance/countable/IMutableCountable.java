package com.garygregg.rebalance.countable;

public interface IMutableCountable extends ICountable {

    /**
     * Sets a value for the countable.
     *
     * @param value    The value of the countable
     * @param truncate True if the value should be truncated, false if rounded
     * @return The value that was set
     */
    double set(double value, boolean truncate);

    /**
     * Sets a value for the countable with a default truncate flag.
     *
     * @param value The value of the countable
     * @return The value that was set
     */
    double set(double value);
}
