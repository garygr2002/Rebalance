package com.garygregg.rebalance.countable;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.Objects;

public class MutableShares extends MutableCountable
        implements Factory<Shares> {

    // A class comparable to this one
    private static final Class<Shares> comparableClass = Shares.class;

    // The precision of shares
    private static final int precision = ICountable.getSharesPrecision();

    // Our number formatter
    private static final NumberFormat formatter =
            ICountable.createFormat(precision);

    // A container for immutable currency
    private final Container<Shares> container = new Container<>(this);

    /**
     * Constructs shares.
     *
     * @param value    The value of the shares
     * @param truncate True if the value should be truncated, false if rounded
     */
    @SuppressWarnings("unused")
    MutableShares(double value, boolean truncate) {
        super(value, truncate);
    }

    /**
     * Constructs shares with a default truncate flag.
     *
     * @param value The value of the shares
     */
    MutableShares(double value) {
        super(value);
    }

    /**
     * Constructs shares from other mutable shares.
     *
     * @param shares Other mutable shares
     */
    MutableShares(@NotNull MutableShares shares) {
        super(shares.getValue());
    }

    /**
     * Constructs shares from immutable shares.
     *
     * @param shares Immutable shares
     */
    MutableShares(@NotNull Shares shares) {
        super(shares.getValue());
    }

    /**
     * Adds a shares value.
     *
     * @param shares Another shares value
     * @return The value that was set
     */
    public double add(@NotNull Shares shares) {

        // Add the value, and return the result.
        set(getValue() + shares.getValue());
        return getValue();
    }

    /**
     * Adds a shares value.
     *
     * @param shares Another shares value
     * @return The value that was set
     */
    public double add(@NotNull MutableShares shares) {

        // Add the value, and return the result.
        set(getValue() + shares.getValue());
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
    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    public boolean equals(Object object) {
        return ICountable.areEqual(this, object, this.getClass(),
                comparableClass, getPrecision());
    }

    /**
     * Gets an immutable equivalent of this object.
     *
     * @return An immutable equivalent of this object
     */
    public @NotNull Shares getImmutable() {
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
    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    public boolean isNotZero() {
        return !equals(Shares.getZero());
    }

    /**
     * Returns true if the shares are one, false otherwise
     *
     * @return True if the shares are one, false otherwise
     */
    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    public boolean isOne() {
        return equals(Shares.getOne());
    }

    @Override
    public @NotNull Shares produce() {
        return new Shares(this);
    }

    /**
     * Sets a shares value.
     *
     * @param shares Another shares value
     * @return The value that was set
     */
    public double set(@NotNull MutableShares shares) {

        // Set the value, and return the result.
        set(shares.getValue());
        return getValue();
    }

    /**
     * Subtracts a shares value.
     *
     * @param shares Another shares value
     * @return The value that was set
     */
    public double subtract(@NotNull Shares shares) {

        // Subtract the value, and return the result.
        set(getValue() - shares.getValue());
        return getValue();
    }

    /**
     * Subtracts a shares value.
     *
     * @param shares Another shares value
     * @return The value that was set
     */
    public double subtract(@NotNull MutableShares shares) {

        // Subtract the value, and return the result.
        set(getValue() - shares.getValue());
        return getValue();
    }

    @Override
    public String toString() {
        return formatter.format(getValue());
    }
}
