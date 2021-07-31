package com.garygregg.rebalance.countable;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.Objects;

public class Currency extends Countable implements Comparable<Currency> {

    // A class comparable to this one
    private static final Class<MutableCurrency> comparableClass =
            MutableCurrency.class;

    // A known one
    private static final Currency one = new Currency(1.);

    // The precision of currency
    private static final int precision = ICountable.getCurrencyPrecision();

    // Our number formatter
    private static final NumberFormat formatter =
            ICountable.createFormat(precision);

    // A known cent
    private static final Currency cent =
            new Currency(1. / ICountable.calculateFactor(precision));

    // A known zero
    private static final Currency zero = new Currency(0.);

    /**
     * Constructs currency.
     *
     * @param value    The value of the currency
     * @param truncate True if the value should be truncated, false if rounded
     */
    Currency(double value, boolean truncate) {
        super(value, precision, truncate);
    }

    /**
     * Constructs currency with a default truncate flag.
     *
     * @param value The value of the currency
     */
    public Currency(double value) {
        super(value, precision);
    }

    /**
     * Constructs currency from other currency.
     *
     * @param currency Other currency
     */
    Currency(@NotNull Currency currency) {
        super(currency.getValue(), precision);
    }

    /**
     * Constructs currency from mutable currency.
     *
     * @param currency Mutable currency
     */
    Currency(@NotNull MutableCurrency currency) {
        super(currency.getValue(), precision);
    }

    /**
     * Gets a known cent.
     *
     * @return A known cent
     */
    public static @NotNull Currency getCent() {
        return cent;
    }

    /**
     * Gets a known one.
     *
     * @return A known one
     */
    public static @NotNull Currency getOne() {
        return one;
    }

    /**
     * Gets a known zero.
     *
     * @return A known zero
     */
    public static @NotNull Currency getZero() {
        return zero;
    }

    @Override
    public boolean areNotEqual(double value) {
        return !ICountable.areEqual(value, getValue(), getPrecision());
    }

    @Override
    public int compareTo(@NotNull Currency currency) {
        return (int) Math.signum(getValue() - currency.getValue());
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
     * Returns true if the currency is a cent, false otherwise
     *
     * @return True if the currency is a cent, false otherwise
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
