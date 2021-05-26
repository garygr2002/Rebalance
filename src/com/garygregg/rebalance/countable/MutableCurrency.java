package com.garygregg.rebalance.countable;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.Objects;

public class MutableCurrency extends MutableCountable {

    // A class comparable to this one
    private static final Class<Currency> comparableClass = Currency.class;

    // The precision of currency
    private static final int precision = ICountable.getCurrencyPrecision();

    // Our number formatter
    private static final NumberFormat formatter =
            ICountable.createFormat(precision);

    /**
     * Constructs currency.
     *
     * @param value    The value of the currency
     * @param truncate True if the value should be truncated, false if rounded
     */
    @SuppressWarnings("unused")
    public MutableCurrency(double value, boolean truncate) {
        super(value, truncate);
    }

    /**
     * Constructs currency with a default truncate flag.
     *
     * @param value The value of the currency
     */
    public MutableCurrency(double value) {
        super(value);
    }

    /**
     * Constructs currency from other mutable currency.
     *
     * @param currency Other mutable currency
     */
    public MutableCurrency(@NotNull MutableCurrency currency) {
        super(currency.getValue());
    }

    /**
     * Constructs currency from immutable currency.
     *
     * @param currency Immutable currency
     */
    public MutableCurrency(@NotNull Currency currency) {
        super(currency.getValue());
    }

    /**
     * Adds a currency value.
     *
     * @param currency Another currency value
     * @return The value that was set
     */
    public double add(@NotNull Currency currency) {

        // Add the value, and return the result.
        set(getValue() + currency.getValue());
        return getValue();
    }

    /**
     * Adds a currency value.
     *
     * @param currency Another currency value
     * @return The value that was set
     */
    public double add(@NotNull MutableCurrency currency) {

        // Add the value, and return the result.
        set(getValue() + currency.getValue());
        return getValue();
    }

    @Override
    public boolean areNotEqual(double value) {
        return !ICountable.areEqual(value, getValue(), getPrecision());
    }

    /**
     * Divides a currency value.
     *
     * @param currency Another currency value
     * @return The value that was set
     */
    public double divide(@NotNull Currency currency) {

        /*
         * Divide the value, and return the result. Note: No check is made here
         * for divide-by-zero.  If the divisor is zero, the result will be
         * infinity or negative infinity, depending on the sign of the current
         * value.
         */
        set(getValue() / currency.getValue());
        return getValue();
    }

    /**
     * Divides a currency value.
     *
     * @param currency Another currency value
     * @return The value that was set
     */
    public double divide(@NotNull MutableCurrency currency) {

        /*
         * Divide the value, and return the result. Note: No check is made here
         * for divide-by-zero.  If the divisor is zero, the result will be
         * infinity or negative infinity, depending on the sign of the current
         * value.
         */
        set(getValue() / currency.getValue());
        return getValue();
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
     * Returns true the shares are one, false otherwise
     *
     * @return True if the shares are one, false otherwise
     */
    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    public boolean isCent() {
        return equals(Currency.getCent());
    }

    @Override
    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    public boolean isNotZero() {
        return !equals(Currency.getZero());
    }

    /**
     * Multiplies a currency value.
     *
     * @param currency Another currency value
     * @return The value that was set
     */
    public double multiply(@NotNull Currency currency) {

        // Multiply the value, and return the result.
        set(getValue() * currency.getValue());
        return getValue();
    }

    /**
     * Multiplies a currency value.
     *
     * @param currency Another currency value
     * @return The value that was set
     */
    public double multiply(@NotNull MutableCurrency currency) {

        // Multiply the value, and return the result.
        set(getValue() * currency.getValue());
        return getValue();
    }

    /**
     * Sets a currency value.
     *
     * @param currency Another currency value
     * @return The value that was set
     */
    public double set(@NotNull Currency currency) {

        // Set the value, and return the result.
        set(currency.getValue());
        return getValue();
    }

    /**
     * Sets a currency value.
     *
     * @param currency Another currency value
     * @return The value that was set
     */
    public double set(@NotNull MutableCurrency currency) {

        // Set the value, and return the result.
        set(currency.getValue());
        return getValue();
    }

    /**
     * Subtracts a currency value.
     *
     * @param currency Another currency value
     * @return The value that was set
     */
    public double subtract(@NotNull Currency currency) {

        // Subtract the value, and return the result.
        set(getValue() - currency.getValue());
        return getValue();
    }

    /**
     * Subtracts a currency value.
     *
     * @param currency Another currency value
     * @return The value that was set
     */
    public double subtract(@NotNull MutableCurrency currency) {

        // Subtract the value, and return the result.
        set(getValue() - currency.getValue());
        return getValue();
    }

    @Override
    public String toString() {
        return formatter.format(getValue());
    }
}
