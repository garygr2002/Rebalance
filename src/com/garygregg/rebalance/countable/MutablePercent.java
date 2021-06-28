package com.garygregg.rebalance.countable;

import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;
import java.util.Objects;

public class MutablePercent extends MutableCountable
        implements Factory<Percent> {

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
     * Constructs percent.
     *
     * @param value    The value of the percent
     * @param truncate True if the value should be truncated, false if rounded
     */
    @SuppressWarnings("unused")
    MutablePercent(double value, boolean truncate) {
        super(value, truncate);
    }

    /**
     * Constructs percent with a default truncate flag.
     *
     * @param value The value of the percent
     */
    public MutablePercent(double value) {
        super(value);
    }

    /**
     * Constructs percent from other mutable percent.
     *
     * @param percent Other mutable percent
     */
    public MutablePercent(@NotNull MutablePercent percent) {
        super(percent.getValue());
    }

    /**
     * Constructs percent from immutable percent.
     *
     * @param percent Immutable percent
     */
    public MutablePercent(@NotNull Percent percent) {
        super(percent.getValue());
    }

    /**
     * Constructs percent with zero initial value.
     */
    public MutablePercent() {
        this(Percent.getZero());
    }

    /**
     * Adds a percent value.
     *
     * @param percent Another percent value
     * @return The value that was set
     */
    public double add(@NotNull Percent percent) {

        // Add the value, and return the result.
        set(getValue() + percent.getValue());
        return getValue();
    }

    /**
     * Adds a percent value.
     *
     * @param percent Another percent value
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
    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    public boolean isNotZero() {
        return !equals(Percent.getZero());
    }

    /**
     * Returns true if the percent is one hundred, false otherwise
     *
     * @return True if the percent is one hundred, false otherwise
     */
    @SuppressWarnings("EqualsBetweenInconvertibleTypes")
    public boolean isOneHundred() {
        return equals(Percent.getOneHundred());
    }

    @Override
    public @NotNull Percent produce() {
        return new Percent(this);
    }

    /**
     * Sets a percent value.
     *
     * @param percent Another percent value
     * @return The value that was set
     */
    public double set(@NotNull MutablePercent percent) {

        // Set the value, and return the result.
        set(percent.getValue());
        return getValue();
    }

    /**
     * Subtracts a percent value.
     *
     * @param percent Another percent value
     * @return The value that was set
     */
    public double subtract(@NotNull Percent percent) {

        // Subtract the value, and return the result.
        set(getValue() - percent.getValue());
        return getValue();
    }

    /**
     * Subtracts a percent value.
     *
     * @param percent Another percent value
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
