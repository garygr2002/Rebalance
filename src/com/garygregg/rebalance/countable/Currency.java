package com.garygregg.rebalance.countable;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.Objects;

public class Currency extends Countable implements Comparable<Currency> {

    // A class comparable to this one
    private static final Class<MutableCurrency> comparableClass =
            MutableCurrency.class;

    // A known minus one
    private static final Currency minusOne = new Currency(-1.);

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
     * Constructs currency with a default truncate flag.
     *
     * @param value The value of the currency
     */
    public Currency(double value) {
        super(value, precision);
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
    public static @NotNull Currency getCent() {
        return cent;
    }

    /**
     * Gets a known minus one.
     *
     * @return A known minus one
     */
    public static @NotNull Currency getMinusOne() {
        return minusOne;
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
    public boolean equals(Object object) {

        // This method has been auto-generated.
        if (this == object) return true;
        if (!(object instanceof Currency)) return false;
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
        return ICountable.areEqual(this, object, this.getClass(),
                comparableClass, getPrecision());
    }

    @Override
    public boolean isNotZero() {
        return !isEqual(getZero());
    }

    @Override
    public String toString() {
        return formatter.format(getValue());
    }
}
