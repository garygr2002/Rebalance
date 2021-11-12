package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.countable.Currency;
import org.jetbrains.annotations.NotNull;

class ReallocationScore implements Comparable<ReallocationScore> {

    // The ideal reallocation score
    private static final ReallocationScore idealScore =
            new ReallocationScore(Currency.getZero(), 0.);

    // The average absolute residual;
    private final double averageAbsolute;

    // The residual
    private final double residual;

    // The residual as currency
    private final Currency residualCurrency;

    /**
     * Constructs the reallocation score.
     *
     * @param residual        The residual
     * @param averageAbsolute The average absolute residual
     */
    public ReallocationScore(@NotNull Currency residual,
                             double averageAbsolute) {

        // Set the member variables.
        this.averageAbsolute = Math.abs(averageAbsolute);
        this.residual = Math.abs(residual.getValue());
        this.residualCurrency = new Currency(getResidual());
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

        // Compare first by residual.
        final int byResidual = Double.compare(getResidual(),
                score.getResidual());

        /*
         * If residuals compare the same, compare by average absolutes and
         * return the result. Otherwise, return the comparison of the
         * residuals.
         */
        return (0 == byResidual) ? Double.compare(getAverageAbsolute(),
                score.getAverageAbsolute()) : byResidual;
    }

    /**
     * Gets the average absolute residual.
     *
     * @return The average absolute residual.
     */
    public double getAverageAbsolute() {
        return averageAbsolute;
    }

    /**
     * Gets the residual.
     *
     * @return The residual
     */
    private double getResidual() {
        return residual;
    }

    /**
     * Gets the residual currency.
     *
     * @return The residual currency
     */
    public Currency getResidualCurrency() {
        return residualCurrency;
    }
}
