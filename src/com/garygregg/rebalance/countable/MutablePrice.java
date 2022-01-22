package com.garygregg.rebalance.countable;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.Objects;

public class MutablePrice extends MutableCountable
        implements Comparable<MutablePrice>, Factory<Price> {

    // A class comparable to this one
    private static final Class<Price> comparableClass = Price.class;

    // The precision of price
    private static final int precision = ICountable.getPricePrecision();

    // Our number formatter
    private static final NumberFormat formatter =
            ICountable.createFormat(precision);

    // A container for immutable price
    private final Container<Price> container = new Container<>(this);

    /**
     * Constructs mutable price with a default truncate flag.
     *
     * @param value The value of the price
     */
    public MutablePrice(double value) {
        super(value);
    }

    /**
     * Adds price to mutable price.
     *
     * @param price A price
     * @return The value that was set
     */
    public double add(@NotNull Price price) {

        // Add the value, and return the result.
        set(getValue() + price.getValue());
        return getValue();
    }

    /**
     * Adds mutable price to mutable price.
     *
     * @param price A mutable price
     * @return The value that was set
     */
    public double add(@NotNull MutablePrice price) {

        // Add the value, and return the result.
        set(getValue() + price.getValue());
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
    public int compareTo(@NotNull MutablePrice mutablePrice) {
        return (int) Math.signum(getValue() - mutablePrice.getValue());
    }

    /**
     * Divides mutable price with price.
     *
     * @param price A price
     * @return The value that was set
     */
    public double divide(@NotNull Price price) {

        /*
         * Divide the value, and return the result. Note: No check is made here
         * for divide-by-zero.  If the divisor is zero, the result will be
         * infinity or negative infinity, depending on the sign of the current
         * value.
         */
        set(getValue() / price.getValue());
        return getValue();
    }

    /**
     * Divides mutable price with mutable price.
     *
     * @param price A mutable price
     * @return The value that was set
     */
    public double divide(@NotNull MutablePrice price) {

        /*
         * Divide the value, and return the result. Note: No check is made here
         * for divide-by-zero.  If the divisor is zero, the result will be
         * infinity or negative infinity, depending on the sign of the current
         * value.
         */
        set(getValue() / price.getValue());
        return getValue();
    }

    @Override
    public boolean equals(Object object) {

        // This method has been auto-generated.
        if (this == object) return true;
        if (!(object instanceof MutablePercent)) return false;
        return isEqual(object);
    }

    /**
     * Gets an immutable equivalent of this object.
     *
     * @return An immutable equivalent of this object
     */
    public @NotNull Price getImmutable() {
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

    @Override
    public boolean isEqual(Object object) {
        return ICountable.areEqual(this, object, getClass(), comparableClass,
                getPrecision());
    }

    @Override
    public boolean isNotZero() {
        return !isEqual(Price.getZero());
    }

    /**
     * Multiplies mutable price with price.
     *
     * @param price A price
     * @return The value that was set
     */
    @SuppressWarnings("unused")
    public double multiply(@NotNull Price price) {

        // Multiply the value, and return the result.
        set(getValue() * price.getValue());
        return getValue();
    }

    /**
     * Multiplies mutable price with mutable price.
     *
     * @param price A mutable price
     * @return The value that was set
     */
    @SuppressWarnings("unused")
    public double multiply(@NotNull MutablePrice price) {

        // Multiply the value, and return the result.
        set(getValue() * price.getValue());
        return getValue();
    }

    @Override
    public @NotNull Price produce() {
        return new Price(this);
    }

    /**
     * Sets mutable price with price.
     *
     * @param price A price
     * @return The value that was set
     */
    public double set(@NotNull Price price) {

        // Set the value, and return the result.
        set(price.getValue());
        return getValue();
    }

    /**
     * Sets mutable price with mutable price.
     *
     * @param price A mutable price
     * @return The value that was set
     */
    public double set(@NotNull MutablePrice price) {

        // Set the value, and return the result.
        set(price.getValue());
        return getValue();
    }

    /**
     * Subtracts price from mutable price.
     *
     * @param price A price
     * @return The value that was set
     */
    public double subtract(@NotNull Price price) {

        // Subtract the value, and return the result.
        set(getValue() - price.getValue());
        return getValue();
    }

    /**
     * Subtracts mutable price from mutable price.
     *
     * @param price A mutable price
     * @return The value that was set
     */
    public double subtract(@NotNull MutablePrice price) {

        // Subtract the value, and return the result.
        set(getValue() - price.getValue());
        return getValue();
    }

    @Override
    public String toString() {
        return formatter.format(getValue());
    }
}
