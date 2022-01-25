package com.garygregg.rebalance;

import com.garygregg.rebalance.countable.Percent;
import org.jetbrains.annotations.NotNull;

import java.io.PrintStream;

public final class InflationCaddy {

    // The singleton inflation caddy instance
    private static final InflationCaddy instance = new InflationCaddy();

    // The output stream
    private static final PrintStream stream = MessageLogger.getOutputStream();

    // Annual inflation
    private double annual;

    // Daily inflation
    private double daily;

    // Monthly inflation
    private double monthly;

    /**
     * Constructs the inflation caddy.
     */
    private InflationCaddy() {
        setPercent(null);
    }

    /**
     * Calculates an inflation value.
     *
     * @param annual   The annual inflation
     * @param exponent The exponent (years per desired time period)
     * @return An inflation value
     */
    private static double calculate(double annual, double exponent) {
        return Math.exp(Math.log(annual) / exponent);
    }

    /**
     * Gets an inflation caddy instance.
     *
     * @return An inflation caddy instance
     */
    public static @NotNull InflationCaddy getInstance() {
        return instance;
    }

    /**
     * Gets the annual inflation.
     *
     * @return The annual inflation
     */
    public Double getAnnual() {
        return annual;
    }

    /**
     * Gets the daily inflation.
     *
     * @return The daily inflation
     */
    public Double getDaily() {
        return daily;
    }

    /**
     * Gets the monthly inflation.
     *
     * @return The monthly inflation
     */
    public Double getMonthly() {
        return monthly;
    }

    /**
     * Sets inflation as a percentage.
     *
     * @param percent Inflation as a percentage
     */
    private void set(double percent) {

        annual = 1. + (percent / Percent.getOneHundred().getValue());
        daily = calculate(annual, 365.2425);
        monthly = calculate(annual, 12.);
    }

    /**
     * Sets inflation as a percentage.
     *
     * @param percent Inflation as a percentage
     */
    public void setPercent(Double percent) {
        set((null == percent) ? 0. : percent);
    }
}
