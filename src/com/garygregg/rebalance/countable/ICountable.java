package com.garygregg.rebalance.countable;

import org.jetbrains.annotations.NotNull;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public interface ICountable {

    /**
     * Determines if two values are equal to a given precision.
     *
     * @param first     The first value
     * @param second    The second value
     * @param precision The precision in decimal places that is under
     *                  consideration
     * @return True if the two values are equal to the given precision, false
     * otherwise
     */
    static boolean areEqual(double first, double second,
                            int precision) {

        /*
         * Calculate the equality threshold. Return true if the absolute value
         * of the difference between the first and second values is less than
         * this threshold, false otherwise.
         */
        final double threshold = 0.5 / calculateFactor(precision);
        return Math.abs(first - second) < threshold;
    }

    /**
     * Determines whether two objects are both one of two subclasses of
     * ICountable, and numerically equal to a precision.
     *
     * @param objectOne The first object
     * @param objectTwo The second object
     * @param classOne  The first class derivable from ICountable
     * @param classTwo  The second class derivable from ICountable
     * @param precision The precision
     * @param <S>       Any type derived from ICountable
     * @param <T>       Any type derived from ICountable
     * @return True if the two objects are of the correct ICountable types,
     * and equal to the given precision
     */
    static <S extends ICountable,
            T extends ICountable> boolean areEqual(
            final Object objectOne, final Object objectTwo,
            @NotNull final Class<? extends S> classOne,
            @NotNull final Class<? extends T> classTwo,
            int precision) {

        // Initialize the result. Are the two objects the same reference?
        Boolean result = null;
        if (objectOne == objectTwo) {

            // The two objects are the same reference. The answer is 'true'.
            result = true;
        }

        // The answer is 'false' if now either object is null.
        if ((null == objectOne) || (null == objectTwo)) {
            result = false;
        }

        // No definitive answer yet...
        if (null == result) {

            /*
             * Set a tentative result if the classes of the two objects are
             * either of the given ICountable types.
             */
            result = doesClassMatch(objectOne, classOne, classTwo) &&
                    doesClassMatch(objectTwo, classOne, classTwo);
        }

        // Are the two objects of the given ICountable types?
        if (result) {

            /*
             * The two objects are of the given ICountable types. Cast them
             * both to ICountable.
             */
            final ICountable countableOne = (ICountable) objectOne;
            final ICountable countableTwo = (ICountable) objectTwo;

            // Reset the result based on a value comparison.
            result = ICountable.areEqual(countableOne.getValue(),
                    countableTwo.getValue(), precision);
        }

        // Return the result.
        return result;
    }

    /**
     * Calculates a factor of ten based on a precision.
     *
     * @param precision The given precision
     * @return A factor of ten based on the given precision
     */
    static double calculateFactor(int precision) {
        return Math.pow(10., precision);
    }

    /**
     * Creates a number format.
     *
     * @param precision The maximum and minimum digits of the format precision
     * @return A number format
     */
    static NumberFormat createFormat(int precision) {

        /*
         * Declare and initialize the format. Get the precision from the
         * countable. Set the maximum and minimum fraction digits to the
         * precision of the countable.
         */
        final NumberFormat format = new DecimalFormat();
        format.setMaximumFractionDigits(precision);
        format.setMinimumFractionDigits(precision);

        // Set the rounding mode of the format. Return the format.
        format.setRoundingMode(RoundingMode.HALF_UP);
        return format;
    }

    /**
     * Determines if the class of an object matches either one of two
     * alternatives.
     *
     * @param object Any object
     * @param first  The first class
     * @param second The second class
     * @return True if the class matches one or both of the given
     * alternatives, false if neither
     */
    static boolean doesClassMatch(Object object, Class<?> first,
                                  Class<?> second) {

        /*
         * Get the class of the object, and return whether it is assignable to
         * either of the given classes.
         */
        final Class<?> objectClass = object.getClass();
        return first.isAssignableFrom(objectClass) ||
                second.isAssignableFrom(objectClass);
    }

    /**
     * Returns the common currency precision.
     *
     * @return The common currency precision
     */
    static int getCurrencyPrecision() {
        return 2;
    }

    /**
     * Returns the common percent precision.
     *
     * @return The common percent precision
     */
    static int getPercentPrecision() {
        return 3;
    }

    /**
     * Returns the common price precision.
     *
     * @return The common price precision
     */
    static int getPricePrecision() {
        return 3;
    }

    /**
     * Returns the precision of the common shares.
     *
     * @return The precision of the common shares
     */
    static int getSharesPrecision() {
        return 3;
    }

    /**
     * Rounds a value given a precision.
     *
     * @param value     The given value
     * @param precision The given precision
     * @return The given value, rounded
     */
    static double round(double value, int precision) {

        /*
         * Calculate the factor with the given precision, round the value
         * and return it.
         */
        final double factor = calculateFactor(precision);
        return Math.round(value * factor) / factor;
    }

    /**
     * Truncates a value given a precision.
     *
     * @param value     The given value
     * @param precision The given precision
     * @return The given value, truncated
     */
    static double truncate(double value, int precision) {

        /*
         * Calculate the factor with the given precision, truncate the value
         * and return it.
         */
        final double factor = calculateFactor(precision);
        return ((long) (value * factor)) / factor;
    }

    /**
     * Returns true if the contained countable does not equal the given value,
     * false if it does.
     *
     * @param value The given value
     * @return True if the contained countable does not equal the given value,
     * false if it does
     */
    boolean areNotEqual(double value);

    /**
     * Gets the precision of this countable.
     *
     * @return The precision of this countable
     */
    int getPrecision();

    /**
     * Gets the value of the countable.
     *
     * @return The value of the countable
     */
    double getValue();

    /**
     * Returns true if the countable is not zero, false otherwise.
     *
     * @return True if the countable is not zero, false otherwise
     */
    boolean isNotZero();
}
