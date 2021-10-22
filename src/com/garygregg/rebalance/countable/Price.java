package com.garygregg.rebalance.countable;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.Objects;

public class Price extends Countable implements Comparable<Price> {

    // A class comparable to this one
    private static final Class<MutablePrice> comparableClass =
            MutablePrice.class;

    // A known one
    private static final Price one = new Price(1.);

    // The precision of price
    private static final int precision = ICountable.getPricePrecision();

    // Our number formatter
    private static final NumberFormat formatter =
            ICountable.createFormat(precision);

    // A known cent
    private static final Price cent =
            new Price(1. / ICountable.calculateFactor(precision));

    // A known zero
    private static final Price zero = new Price(0.);

    /**
     * Constructs price.
     *
     * @param value    The value of the price
     * @param truncate True if the value should be truncated, false if rounded
     */
    Price(double value, boolean truncate) {
        super(value, precision, truncate);
    }

    /**
     * Constructs price with a default truncate flag.
     *
     * @param value The value of the price
     */
    public Price(double value) {
        super(value, precision);
    }

    /**
     * Constructs price from other price.
     *
     * @param price Other price
     */
    Price(@NotNull Price price) {
        super(price.getValue(), precision);
    }

    /**
     * Constructs price from mutable price.
     *
     * @param price Mutable price
     */
    Price(@NotNull MutablePrice price) {
        super(price.getValue(), precision);
    }

    /**
     * Formats a value.
     *
     * @param value The value to format
     * @return The formatted value
     */
    public static String format(Double value) {
        return (null == value) ? null : formatter.format(value);
    }

    /**
     * Gets a known cent.
     *
     * @return A known cent
     */
    public static @NotNull Price getCent() {
        return cent;
    }

    /**
     * Gets a known one.
     *
     * @return A known one
     */
    public static @NotNull Price getOne() {
        return one;
    }

    /**
     * Gets a known zero.
     *
     * @return A known zero
     */
    public static @NotNull Price getZero() {
        return zero;
    }

    @Override
    public boolean areNotEqual(double value) {
        return !ICountable.areEqual(value, getValue(), getPrecision());
    }

    @Override
    public int compareTo(@NotNull Price price) {
        return (int) Math.signum(getValue() - price.getValue());
    }

    @Override
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object object) {
        return ICountable.areEqual(this, object, this.getClass(),
                comparableClass, getPrecision());
    }

    @Override
    public int getPrecision() {
        return precision;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }

    /**
     * Returns true if the price is a cent, false otherwise
     *
     * @return True if the price is a cent, false otherwise
     */
    public boolean isCent() {
        return equals(getCent());
    }

    @Override
    public boolean isNotZero() {
        return !equals(getZero());
    }

    @Override
    public String toString() {
        return formatter.format(getValue());
    }
}
