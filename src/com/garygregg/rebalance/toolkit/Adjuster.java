package com.garygregg.rebalance.toolkit;

public interface Adjuster {

    /**
     * Performs the function, returning a 'y' for the given 'x'.
     *
     * @param x The argument
     * @return The value of the function at the argument
     */
    double f(double x);

    /**
     * Gets the slope of the function for the given 'x'.
     *
     * @param x The argument
     * @return The slope of the function at the argument
     */
    @SuppressWarnings("unused")
    double fSlope(double x);

    /**
     * Gets the horizontal asymptote.
     *
     * @return The horizontal asymptote, or null if there is no horizontal
     * asymptote
     */
    Double getHAsymptote();

    /**
     * Gets the scale.
     *
     * @return The scale
     */
    double getScale();

    /**
     * Gets the vertical asymptote.
     *
     * @return The vertical asymptote, or null if there is no vertical
     * asymptote
     */
    @SuppressWarnings("unused")
    Double getVAsymptote();

    /**
     * Gets the x-value at the bear market threshold.
     *
     * @return The x-value at the bear market threshold
     */
    double getXBear();

    /**
     * Gets the x-value at market high.
     *
     * @return The x-value at market high
     */
    double getXHigh();

    /**
     * Gets the x-value at market zero.
     *
     * @return The x-value at market zero
     */
    double getXZero();

    /**
     * Gets the y-value at the bear market threshold.
     *
     * @return The y-value at the bear market threshold
     */
    double getYBear();

    /**
     * Gets the y-value at market high.
     *
     * @return The y-value at market high
     */
    double getYHigh();

    /**
     * Gets the y-value at market zero.
     *
     * @return The y-value at market zero
     */
    double getYZero();

    /**
     * Sets the y-values to those that approximate the v1.3 adjustment used in
     * the software.
     *
     * @param yHigh The y-value at market high
     */
    @SuppressWarnings("unused")
    void setNearlyALine(double yHigh);

    /**
     * Sets the y-values.
     *
     * @param high The y-value at market high
     * @param bear The y-value at the bear market threshold
     * @param zero The y-value at market zero
     */
    void setY(double high, double bear, double zero);
}
