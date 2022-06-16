package com.garygregg.rebalance.countable;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.Objects;

public class MutableShares extends MutableCountable
        implements Comparable<MutableShares>, Factory<Shares> {

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
     * Constructs mutable shares with a default truncate flag.
     *
     * @param value The value of the shares
     */
    public MutableShares(double value) {
        super(value);
    }

    /**
     * Constructs mutable shares from immutable shares.
     *
     * @param shares Immutable shares
     */
    public MutableShares(@NotNull Shares shares) {
        super(shares.getValue());
    }

    /**
     * Constructs shares with zero initial value.
     */
    public MutableShares() {
        this(Shares.getZero());
    }

    /**
     * Adds shares to mutable shares.
     *
     * @param shares A shares
     * @return The value that was set
     */
    public double add(@NotNull Shares shares) {

        // Add the value, and return the result.
        set(getValue() + shares.getValue());
        return getValue();
    }

    /**
     * Adds mutable shares to mutable shares.
     *
     * @param shares A mutable shares
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
    public int compareTo(@NotNull MutableShares mutableShares) {
        return (int) Math.signum(getValue() - mutableShares.getValue());
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
    public boolean isEqual(Object object) {
        return ICountable.areEqual(this, object, getClass(), comparableClass,
                getPrecision());
    }

    @Override
    public boolean isNotZero() {
        return !isEqual(Shares.getZero());
    }

    /**
     * Returns true if the shares are one, false otherwise
     *
     * @return True if the shares are one, false otherwise
     */
    public boolean isOne() {
        return isEqual(Shares.getOne());
    }

    /**
     * Multiplies mutable shares with shares.
     *
     * @param shares A shares
     * @return The value that was set
     */
    @SuppressWarnings("UnusedReturnValue")
    public double multiply(@NotNull Shares shares) {

        // Multiply the value, and return the result.
        set(getValue() * shares.getValue());
        return getValue();
    }

    @Override
    public @NotNull Shares produce() {
        return new Shares(this);
    }

    /**
     * Sets mutable shares with shares.
     *
     * @param shares A shares
     * @return The value that was set
     */
    public double set(@NotNull MutableShares shares) {

        // Set the value, and return the result.
        set(shares.getValue());
        return getValue();
    }

    /**
     * Sets mutable shares with shares.
     *
     * @param shares A shares
     * @return The value that was set
     */
    @SuppressWarnings("UnusedReturnValue")
    public double set(@NotNull Shares shares) {

        // Set the value, and return the result.
        set(shares.getValue());
        return getValue();
    }

    /**
     * Subtracts shares from mutable shares.
     *
     * @param shares A shares
     * @return The value that was set
     */
    @SuppressWarnings("UnusedReturnValue")
    public double subtract(@NotNull Shares shares) {

        // Subtract the value, and return the result.
        set(getValue() - shares.getValue());
        return getValue();
    }

    /**
     * Subtracts mutable shares from mutable shares.
     *
     * @param shares A mutable shares
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
