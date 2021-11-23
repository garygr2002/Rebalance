package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.countable.Currency;
import org.jetbrains.annotations.NotNull;

class ReallocationScore implements Comparable<ReallocationScore> {

    // The ideal deviation
    private static final double idealDeviation = 0.;

    // The ideal reallocation score
    private static final ReallocationScore idealScore =
            new ReallocationScore(Currency.getZero(),
                    getIdealDeviation());

    // The deviation component
    private final double deviation;

    // The residual component
    private final Currency residual;

    /**
     * Constructs the reallocation score. How the residual and deviation are
     * interpreted are application dependent, except with the certain
     * knowledge that ideal definition is known.
     *
     * @param residual  The residual component
     * @param deviation The deviation component
     */
    public ReallocationScore(@NotNull Currency residual,
                             double deviation) {

        // Set the member variables.
        this.deviation = deviation;
        this.residual = residual;
    }

    /**
     * Gets the ideal deviation.
     *
     * @return The ideal deviation
     */
    public static double getIdealDeviation() {
        return idealDeviation;
    }

    /**
     * Gets the ideal score.
     *
     * @return The ideal score
     */
    public static @NotNull ReallocationScore getIdealScore() {
        return idealScore;
    }

    @Override
    public int compareTo(@NotNull ReallocationScore score) {

        // Compare first by the absolute values of the residual component.
        final int byResidual = Double.compare(
                Math.abs(getResidual().getValue()),
                Math.abs(score.getResidual().getValue()));

        /*
         * If absolute values of the residuals compare the same, compare by
         * absolute values of the deviation component, and return the result.
         * Otherwise, return the comparison of the absolute values of the
         * residual component. Note: It is not strictly necessary to take the
         * absolute values of the deviations if they are calculated in the
         * expected way, as the sum of squares divided by the number of
         * samples. The deviation squares are already always positive.
         */
        return (0 == byResidual) ? Double.compare(
                Math.abs(getDeviation()),
                Math.abs(score.getDeviation())) : byResidual;
    }

    /**
     * Gets the deviation component.
     *
     * @return The deviation component
     */
    public double getDeviation() {
        return deviation;
    }

    /**
     * Gets the residual component.
     *
     * @return The residual component
     */
    public @NotNull Currency getResidual() {
        return residual;
    }
}
