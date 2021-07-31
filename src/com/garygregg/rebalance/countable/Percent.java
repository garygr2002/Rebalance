package com.garygregg.rebalance.countable;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.Objects;

public class Percent extends Countable implements Comparable<Percent> {

    // A class comparable to this one
    private static final Class<MutablePercent> comparableClass =
            MutablePercent.class;

    // A known one hundred
    private static final Percent oneHundred = new Percent(100.);

    // The precision of percent
    private static final int precision = ICountable.getPercentPrecision();

    // A known minimum
    private static final Percent minimum =
            new Percent(1. / ICountable.calculateFactor(precision));

    // Our number formatter
    private static final NumberFormat formatter =
            ICountable.createFormat(precision);

    // A known zero
    private static final Percent zero = new Percent(0.);

    /**
     * Constructs percent.
     *
     * @param value    The value of the percent
     * @param truncate True if the value should be truncated, false if rounded
     */
    Percent(double value, boolean truncate) {
        super(value, precision, truncate);
    }

    /**
     * Constructs percent with a default truncate flag.
     *
     * @param value The value of the percent
     */
    public Percent(double value) {
        super(value, precision);
    }

    /**
     * Constructs percent from other percent.
     *
     * @param percent Other percent
     */
    Percent(@NotNull Percent percent) {
        super(percent.getValue(), precision);
    }

    /**
     * Constructs percent from mutable percent.
     *
     * @param percent Mutable percent
     */
    Percent(@NotNull MutablePercent percent) {
        super(percent.getValue(), precision);
    }

    /**
     * Gets a known minimum.
     *
     * @return A known minimum
     */
    public static @NotNull Percent getMinimum() {
        return minimum;
    }

    /**
     * Gets a known one hundred.
     *
     * @return A known one hundred
     */
    public static @NotNull Percent getOneHundred() {
        return oneHundred;
    }

    /**
     * Gets a known zero.
     *
     * @return A known zero
     */
    public static @NotNull Percent getZero() {
        return zero;
    }

    @Override
    public boolean areNotEqual(double value) {
        return !ICountable.areEqual(value, getValue(), getPrecision());
    }

    /**
     * Returns true the percents are one hundred, false otherwise
     *
     * @return True if the percents are one hundred, false otherwise
     */
    public boolean areOneHundred() {
        return equals(getOneHundred());
    }

    @Override
    public int compareTo(@NotNull Percent percent) {
        return (int) Math.signum(getValue() - percent.getValue());
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

    @Override
    public boolean isNotZero() {
        return !equals(getZero());
    }

    /**
     * Returns true if the percent is one hundred, false otherwise
     *
     * @return True if the percent is one hundred, false otherwise
     */
    public boolean isOneHundred() {
        return equals(getOneHundred());
    }

    @Override
    public String toString() {
        return formatter.format(getValue());
    }
}
