package com.garygregg.rebalance.countable;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.Objects;

public class MutableCurrency extends MutableCountable
        implements Comparable<MutableCurrency>, Factory<Currency> {

    // A class comparable to this one
    private static final Class<Currency> comparableClass = Currency.class;

    // The precision of currency
    private static final int precision = ICountable.getCurrencyPrecision();

    // Our number formatter
    private static final NumberFormat formatter =
            ICountable.createFormat(precision);

    // A container for immutable currency
    private final Container<Currency> container = new Container<>(this);

    /**
     * Constructs mutable currency with a default truncate flag.
     *
     * @param value The value of the currency
     */
    public MutableCurrency(double value) {
        super(value);
    }

    /**
     * Constructs mutable currency from other mutable currency.
     *
     * @param currency Other mutable currency
     */
    public MutableCurrency(@NotNull MutableCurrency currency) {
        super(currency.getValue());
    }

    /**
     * Constructs mutable currency from immutable currency.
     *
     * @param currency Immutable currency
     */
    public MutableCurrency(@NotNull Currency currency) {
        super(currency.getValue());
    }

    /**
     * Constructs mutable currency with zero initial value.
     */
    public MutableCurrency() {
        this(Currency.getZero());
    }

    /**
     * Adds currency to mutable currency.
     *
     * @param currency A currency
     * @return The value that was set
     */
    public double add(@NotNull Currency currency) {

        // Add the value, and return the result.
        set(getValue() + currency.getValue());
        return getValue();
    }

    /**
     * Adds mutable currency to mutable currency.
     *
     * @param currency A mutable currency
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

    @Override
    protected void clear() {

        /*
         * There is a null check here because initialization of the container
         * might not have occurred when this method is called.
         */
        if (null != container) {
            container.clear();
        }
    }

    @Override
    public int compareTo(@NotNull MutableCurrency mutableCurrency) {
        return (int) Math.signum(getValue() - mutableCurrency.getValue());
    }

    /**
     * Divides mutable currency with currency.
     *
     * @param currency A currency
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
     * Divides mutable currency with mutable currency.
     *
     * @param currency A mutable currency
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
    public boolean equals(Object object) {

        // This method has been auto-generated.
        if (this == object) return true;
        if (!(object instanceof MutableCurrency)) return false;
        return isEqual(object);
    }

    /**
     * Gets an immutable equivalent of this object.
     *
     * @return An immutable equivalent of this object
     */
    public @NotNull Currency getImmutable() {
        return container.get();
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
    @SuppressWarnings("unused")
    public boolean isCent() {
        return isEqual(Currency.getCent());
    }

    @Override
    public boolean isEqual(Object object) {
        return ICountable.areEqual(this, object, getClass(), comparableClass,
                getPrecision());
    }

    @Override
    public boolean isNotZero() {
        return !isEqual(Currency.getZero());
    }

    /**
     * Multiplies mutable currency with currency.
     *
     * @param currency A currency
     * @return The value that was set
     */
    @SuppressWarnings("UnusedReturnValue")
    public double multiply(@NotNull Currency currency) {

        // Multiply the value, and return the result.
        set(getValue() * currency.getValue());
        return getValue();
    }

    /**
     * Multiplies mutable currency with mutable currency.
     *
     * @param currency A currency
     * @return The value that was set
     */
    @SuppressWarnings("unused")
    public double multiply(@NotNull MutableCurrency currency) {

        // Multiply the value, and return the result.
        set(getValue() * currency.getValue());
        return getValue();
    }

    @Override
    public @NotNull Currency produce() {
        return new Currency(this);
    }

    /**
     * Sets mutable currency with currency.
     *
     * @param currency A currency
     * @return The value that was set
     */
    public double set(@NotNull Currency currency) {

        // Set the value, and return the result.
        set(currency.getValue());
        return getValue();
    }

    /**
     * Sets mutable currency with mutable currency.
     *
     * @param currency A mutable currency
     * @return The value that was set
     */
    public double set(@NotNull MutableCurrency currency) {

        // Set the value, and return the result.
        set(currency.getValue());
        return getValue();
    }

    /**
     * Subtracts currency from mutable currency.
     *
     * @param currency A currency
     * @return The value that was set
     */
    public double subtract(@NotNull Currency currency) {

        // Subtract the value, and return the result.
        set(getValue() - currency.getValue());
        return getValue();
    }

    /**
     * Subtracts mutable currency from mutable currency.
     *
     * @param currency A mutable currency
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
