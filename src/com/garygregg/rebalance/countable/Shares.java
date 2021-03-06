package com.garygregg.rebalance.countable;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.Objects;

public class Shares extends Countable implements Comparable<Shares> {

    // A class comparable to this one
    private static final Class<MutableShares> comparableClass =
            MutableShares.class;

    // A known minus one
    private static final Shares minusOne = new Shares(-1.);

    // A known one
    private static final Shares one = new Shares(1.);

    // The precision of shares
    private static final int precision = ICountable.getSharesPrecision();

    // A known minimum
    private static final Shares minimum =
            new Shares(1. / ICountable.calculateFactor(precision));

    // Our number formatter
    private static final NumberFormat formatter =
            ICountable.createFormat(precision);

    // A known zero
    private static final Shares zero = new Shares(0.);

    /**
     * Constructs shares with a default truncate flag.
     *
     * @param value The value of the shares
     */
    public Shares(double value) {
        super(value, precision);
    }

    /**
     * Constructs shares from mutable shares.
     *
     * @param shares Mutable shares
     */
    Shares(@NotNull MutableShares shares) {
        super(shares.getValue(), precision);
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
     * Gets a known minimum.
     *
     * @return A known minimum
     */
    public static @NotNull Shares getMinimum() {
        return minimum;
    }

    /**
     * Gets a known minus one.
     *
     * @return A known minus one
     */
    public static @NotNull Shares getMinusOne() {
        return minusOne;
    }

    /**
     * Gets a known one.
     *
     * @return A known one
     */
    public static @NotNull Shares getOne() {
        return one;
    }

    /**
     * Gets a known zero.
     *
     * @return A known zero
     */
    public static @NotNull Shares getZero() {
        return zero;
    }

    @Override
    public boolean areNotEqual(double value) {
        return !ICountable.areEqual(value, getValue(), getPrecision());
    }

    /**
     * Returns true the shares are one, false otherwise
     *
     * @return True if the shares are one, false otherwise
     */
    @SuppressWarnings("unused")
    public boolean areOne() {
        return equals(getOne());
    }

    @Override
    public int compareTo(@NotNull Shares shares) {
        return (int) Math.signum(getValue() - shares.getValue());
    }

    @Override
    public boolean equals(Object object) {

        // This method has been auto-generated.
        if (this == object) return true;
        if (!(object instanceof Shares)) return false;
        return isEqual(object);
    }

    @Override
    public int getPrecision() {
        return precision;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValue());
    }

    @Override
    public boolean isEqual(Object object) {
        return ICountable.areEqual(this, object, getClass(), comparableClass,
                getPrecision());
    }

    @Override
    public boolean isNotZero() {
        return !equals(getZero());
    }

    /**
     * Returns true if the shares are one, false otherwise
     *
     * @return True if the shares are one, false otherwise
     */
    public boolean isOne() {
        return equals(getOne());
    }

    @Override
    public String toString() {
        return formatter.format(getValue());
    }
}
