package com.garygregg.rebalance.countable;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.Objects;

public class MutablePercent extends MutableCountable
        implements Comparable<MutablePercent>, Factory<Percent> {

    // A class comparable to this one
    private static final Class<Percent> comparableClass = Percent.class;

    // The precision of percent
    private static final int precision = ICountable.getPercentPrecision();

    // Our number formatter
    private static final NumberFormat formatter =
            ICountable.createFormat(precision);

    // A container for immutable currency
    private final Container<Percent> container = new Container<>(this);

    /**
     * Constructs mutable percent with a default truncate flag.
     *
     * @param value The value of the percent
     */
    public MutablePercent(double value) {
        super(value);
    }

    /**
     * Constructs mutable percent from immutable percent.
     *
     * @param percent Immutable percent
     */
    public MutablePercent(@NotNull Percent percent) {
        super(percent.getValue());
    }

    /**
     * Adds percent to mutable percent.
     *
     * @param percent A percent
     * @return The value that was set
     */
    public double add(@NotNull Percent percent) {

        // Add the value, and return the result.
        set(getValue() + percent.getValue());
        return getValue();
    }

    /**
     * Adds mutable percent to mutable percent.
     *
     * @param percent A mutable percent
     * @return The value that was set
     */
    public double add(@NotNull MutablePercent percent) {

        // Add the value, and return the result.
        set(getValue() + percent.getValue());
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
    public int compareTo(@NotNull MutablePercent mutablePercent) {
        return (int) Math.signum(getValue() - mutablePercent.getValue());
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
    public @NotNull Percent getImmutable() {
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
        return ICountable.areEqual(this, object, this.getClass(),
                comparableClass, getPrecision());
    }

    @Override
    public boolean isNotZero() {
        return !isEqual(Percent.getZero());
    }

    /**
     * Returns true if the percent is one hundred, false otherwise
     *
     * @return True if the percent is one hundred, false otherwise
     */
    @SuppressWarnings("unused")
    public boolean isOneHundred() {
        return isEqual(Percent.getOneHundred());
    }

    @Override
    public @NotNull Percent produce() {
        return new Percent(this);
    }

    /**
     * Sets mutable percent with mutable percent.
     *
     * @param percent A mutable percent
     * @return The value that was set
     */
    public double set(@NotNull MutablePercent percent) {

        // Set the value, and return the result.
        set(percent.getValue());
        return getValue();
    }

    /**
     * Subtracts percent from mutable percent.
     *
     * @param percent A percent
     * @return The value that was set
     */
    public double subtract(@NotNull Percent percent) {

        // Subtract the value, and return the result.
        set(getValue() - percent.getValue());
        return getValue();
    }

    /**
     * Subtracts mutable percent from mutable percent.
     *
     * @param percent A mutable percent
     * @return The value that was set
     */
    public double subtract(@NotNull MutablePercent percent) {

        // Subtract the value, and return the result.
        set(getValue() - percent.getValue());
        return getValue();
    }

    @Override
    public String toString() {
        return formatter.format(getValue());
    }
}
