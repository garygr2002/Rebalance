package com.garygregg.rebalance.toolkit;

import org.jetbrains.annotations.NotNull;

class HyperbolicAdjuster implements Adjuster {

    // The horizontal asymptote
    private double hAsymptote;

    // The scale
    private double scale;

    // The vertical asymptote
    private double vAsymptote;

    // The x-value at the bear market threshold
    private double xBear;

    // The x-value at market high
    private double xHigh;

    // The x-value at market zero
    private double xZero;

    // The y-value at the bear market threshold
    private double yBear;

    // The y-value at market high
    private double yHigh;

    // The y-value at market zero
    private double yZero;

    @Override
    public double f(double x) {

        /*
         * Get the horizontal asymptote. Calculate and return the value of the
         * function.
         */
        final double hAsymptote = getHAsymptote();
        return (hAsymptote - getYHigh()) / getDenominator(x) + hAsymptote;
    }

    @Override
    public double fSlope(double x) {

        // Get the scale. Calculate and return the value of the slope.
        final double scale = getScale();
        return -scale * (getHAsymptote() - getYHigh()) /
                Math.pow(getDenominator(x), 2.);
    }

    /**
     * The formula that allows calculation of the scale.
     *
     * @param x A value from the domain
     * @param y A value from the range
     * @return The result of the formula
     */
    private @NotNull Pair<Double, Double> formula(double x, double y) {
        return new Pair<>(y, (y - getYHigh()) / (x - getXHigh()));
    }

    /**
     * Gets the denominator calculation needed for the value of the function,
     * and its slope.
     *
     * @param x The argument
     * @return The value of the denominator needed for both the value of the
     * function, and its slope
     */
    private double getDenominator(double x) {
        return getScale() * (x - getXHigh()) - 1.;
    }

    @Override
    public Double getHAsymptote() {
        return hAsymptote;
    }

    @Override
    public double getScale() {
        return scale;
    }

    @Override
    public Double getVAsymptote() {
        return vAsymptote;
    }

    @Override
    public double getXBear() {
        return xBear;
    }

    @Override
    public double getXHigh() {
        return xHigh;
    }

    @Override
    public double getXZero() {
        return xZero;
    }

    @Override
    public double getYBear() {
        return yBear;
    }

    @Override
    public double getYHigh() {
        return yHigh;
    }

    @Override
    public double getYZero() {
        return yZero;
    }

    @Override
    public void setNearlyALine(double yHigh) {

        /*
         * Calculate the addition used with the old adjustment when the market
         * value reaches zero. Use this to calculate the adjustment used for
         * the y-value at a bear market drop. Deduct a small amount for the
         * adjustment for a complete market drop so that we can get a hyperbole
         * instead of a straight line.
         */
        final double completeDrop = 100. * 5. / 8.;
        setY(yHigh, completeDrop * PreferenceManager.getBearFraction() +
                yHigh, completeDrop - 0.01 + yHigh);
    }

    /**
     * Sets scale, horizontal asymptote, and vertical asymptote from the
     * existing 'x' and 'y' values; assumes 'x' and 'y' values have been
     * set and are consistent.
     */
    private void setScaleAndAsymptotes() {

        /*
         * Get the y-value at market zero. Use the formula to calculate
         * temporary values at market zero and the bear market threshold.
         */
        final double yZero = getYZero();
        final Pair<Double, Double> atZero = formula(getXZero(), yZero);
        final Pair<Double, Double> atBear = formula(getXBear(), getYBear());

        // Calculate the scale from the temporaries.
        scale = (atZero.getSecond() - atBear.getSecond()) /
                (atZero.getFirst() - atBear.getFirst());

        // Calculate the horizontal and vertical asymptotes.
        hAsymptote = (yZero * scale - atZero.getSecond()) / scale;
        vAsymptote = getXHigh() + Math.pow(scale, -1.);
    }

    /**
     * Sets the x-values.
     *
     * @param high The x-value at market high
     * @param bear The x-value at the bear market threshold
     * @param zero The x-value at market zero
     */
    public void setX(double high, double bear, double zero) {

        // Set the x-values for market high and market zero.
        xHigh = high;
        xZero = zero;

        /*
         * Set the x-value at the bear market threshold, and set (or reset) the
         * scale and asymptotes.
         */
        xBear = bear;
        setScaleAndAsymptotes();
    }

    @Override
    public void setY(double high, double bear, double zero) {

        // Set the y-values for market high and market zero.
        yHigh = high;
        yZero = zero;

        /*
         * Set the y-value at the bear market threshold, and set (or reset) the
         * scale and asymptotes.
         */
        yBear = bear;
        setScaleAndAsymptotes();
    }
}
