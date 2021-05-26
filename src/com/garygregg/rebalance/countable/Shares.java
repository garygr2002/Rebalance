package com.garygregg.rebalance.countable;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.Objects;

public class Shares extends Countable {

    // A class comparable to this one
    private static final Class<MutableShares> comparableClass =
            MutableShares.class;

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
     * Constructs shares.
     *
     * @param value    The value of the shares
     * @param truncate True if the value should be truncated, false if rounded
     */
    public Shares(double value, boolean truncate) {
        super(value, precision, truncate);
    }

    /**
     * Constructs shares with a default truncate flag.
     *
     * @param value The value of the shares
     */
    public Shares(double value) {
        super(value, precision);
    }

    /**
     * Constructs shares from other shares.
     *
     * @param shares Other shares
     */
    public Shares(@NotNull Shares shares) {
        super(shares.getValue(), precision);
    }

    /**
     * Constructs shares from mutable shares.
     *
     * @param shares Mutable shares
     */
    public Shares(@NotNull MutableShares shares) {
        super(shares.getValue(), precision);
    }

    /**
     * Gets a known minimum.
     *
     * @return A known minimum
     */
    public static Shares getMinimum() {
        return minimum;
    }

    /**
     * Gets a known one.
     *
     * @return A known one
     */
    public static Shares getOne() {
        return one;
    }

    /**
     * Gets a known zero.
     *
     * @return A known zero
     */
    public static Shares getZero() {
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
    public boolean areOne() {
        return equals(getOne());
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
     * Returns true if the shares are one, false otherwise
     *
     * @return True if the shares are one, false otherwise
     */
    public boolean isOne() {
        return equals(getOne());
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
