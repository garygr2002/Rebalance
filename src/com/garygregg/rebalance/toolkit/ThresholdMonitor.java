package com.garygregg.rebalance.toolkit;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class ThresholdMonitor {

    // The threshold
    private final Level threshold;

    // The maximum seen
    private Level maximum;

    /**
     * Constructs the threshold monitor with an explicit threshold.
     *
     * @param threshold The threshold
     */
    public ThresholdMonitor(@NotNull Level threshold) {

        // Set the threshold and reset the monitor.
        this.threshold = threshold;
        reset();
    }

    /**
     * Constructs the threshold monitor with an implied threshold.
     */
    public ThresholdMonitor() {
        this(Level.WARNING);
    }

    /**
     * Gets the maximum observation.
     *
     * @return The maximum observation
     */
    public Level getMaximum() {
        return maximum;
    }

    /**
     * Gets the threshold.
     *
     * @return The threshold
     */
    public @NotNull Level getThreshold() {
        return threshold;
    }

    /**
     * Determines if the threshold has been reached.
     *
     * @return True if the threshold has been reached; false otherwise
     */
    public boolean isThresholdReached() {
        return isThresholdReached(getMaximum());
    }

    /**
     * Determines if an observation reaches the threshold.
     *
     * @param observation The observation
     * @return True if the observation reaches the threshold; false otherwise
     */
    public boolean isThresholdReached(Level observation) {
        return (null != observation) &&
                (getThreshold().intValue() <= observation.intValue());
    }

    /**
     * Makes an observation.
     *
     * @param observation The observation to make
     * @return The argument (for chaining)
     */
    public Level observe(@NotNull Level observation) {

        /*
         * Get the current maximum observation. Is the maximum observation
         * null, or is it less than the new observation?
         */
        final Level maximum = getMaximum();
        if ((null == maximum) ||
                (maximum.intValue() < observation.intValue())) {

            /*
             * The maximum observation is null, or less than the new
             * observation. Set the new observation as maximum.
             */
            setMaximum(observation);
        }

        // Return the argument.
        return observation;
    }

    /**
     * Resets the threshold monitor.
     */
    public void reset() {
        setMaximum(null);
    }

    /**
     * Sets the maximum observation.
     *
     * @param maximum The maximum observation
     */
    private void setMaximum(Level maximum) {
        this.maximum = maximum;
    }
}
