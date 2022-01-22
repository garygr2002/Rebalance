package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.InflationCaddy;
import com.garygregg.rebalance.SynthesizerType;
import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.portfolio.PortfolioDescription;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.logging.Level;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;

abstract class Annuity extends Synthesizer {

    // The daily inflation getter
    private static final Inflation daily = InflationCaddy::getDaily;

    // The monthly inflation getter
    private static final Inflation monthly = InflationCaddy::getMonthly;

    // An inflation caddy instance
    private final InflationCaddy caddy = InflationCaddy.getInstance();

    /**
     * Converts a date.
     *
     * @param date The date to convert
     * @return The converted date
     */
    private static @NotNull LocalDate convert(@NotNull Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Calculates a geometric sum of currency.
     *
     * @param payment The payments
     * @param rate    The rate
     * @param periods The number of periods
     * @return A geometric sum of the payments for the number of periods
     */
    private static double geometricSum(double payment, double rate,
                                       long periods) {

        /*
         * Declare and initialize the 'forbidden' rate, and calculate the
         * geometric sum.
         */
        final double forbiddenRate = 1.;
        return (forbiddenRate == rate) ? payment * periods :
                ((payment - payment * Math.pow(rate, periods + 1)) /
                        (forbiddenRate - rate));
    }

    /**
     * Gets the default inflation.
     *
     * @return The default inflation
     */
    private static double getDefaultInflation() {
        return 1.;
    }

    /**
     * Gets an inflation figure from an inflation caddy.
     *
     * @param inflation The figure getter
     * @param caddy     The inflation caddy
     * @return An inflation figure
     */
    private static double getInflation(@NotNull Inflation inflation,
                                       @NotNull InflationCaddy caddy) {

        /*
         * Use the getter to access inflation from the caddy. Use a default
         * value if the result is null, otherwise use the value from the caddy.
         * Return the reciprocal of the result.
         */
        final Double result = inflation.getInflation(caddy);
        return 1. / ((null == result) ? getDefaultInflation() : result);
    }

    /**
     * Gets monthly income.
     *
     * @param monthlyIncome A monthly income value
     * @return The argument itself, or a default if the argument is null
     */
    private static @NotNull Currency getMonthlyIncome(Currency monthlyIncome) {
        return (null == monthlyIncome) ? Currency.getZero() : monthlyIncome;
    }

    /**
     * Calculates an annuity value.
     *
     * @param monthly   The monthly payment
     * @param start     The start date of the annuity
     * @param end       The end date of the calculation (estimate participant
     *                  mortality)
     * @param valuation The valuation date of the annuity
     * @param reduce    Reduce the monthly payment for inflation
     * @return The value of the annuity
     */
    private double calculateValue(@NotNull Currency monthly,
                                  Date start, Date end, Date valuation,
                                  boolean reduce) {

        /*
         * Declare the result, and initialize it to zero. Calculate the local
         * valuation date, using 'now' if the given valuation date is null.
         */
        double result = 0.;
        final LocalDate localValuation = (null == valuation) ? LocalDate.now() :
                convert(valuation);

        /*
         * Calculate the local annuity start date, using the valuation date if
         * the given start date is null.
         */
        final LocalDate annuityStart = (null == start) ? localValuation :
                convert(start);

        /*
         * Declare and initialize the calculation start date, using the
         * valuation date if the annuity has already started. Otherwise, use
         * the future annuity start date.
         */
        final LocalDate calculationStart =
                annuityStart.isBefore(localValuation) ? localValuation :
                        annuityStart;

        /*
         * Declare and initialize the calculation end date, using the
         * calculation start date if the end date is null. Is the start date
         * after the end date?
         */
        final LocalDate calculationEnd = (null == end) ? calculationStart :
                convert(end);
        if (calculationStart.isAfter(calculationEnd)) {

            /*
             * The start date is after the end date. Log an informational
             * message; the result of the calculation is zero.
             */
            getLogger().log(Level.INFO, String.format("Start " +
                            "date of '%s' occurs after end date of '%s'; " +
                            "the value of the pension is zero.",
                    calculationStart, calculationEnd));
        }

        // The start date is not after the end date.
        else {

            /*
             * The date of the first payment is the first day of the month
             * after the start date. Does the first payment occur after the end
             * date?
             */
            final LocalDate firstPayment =
                    calculationStart.with(TemporalAdjusters.firstDayOfNextMonth());
            if (firstPayment.isAfter(calculationEnd)) {

                /*
                 * The first payment occurs after the end date. Log an
                 * informational message; the result of the calculation is
                 * zero.
                 */
                getLogger().log(Level.INFO, String.format("First " +
                                "payment date of '%s' occurs after end " +
                                "date of '%s'; value of the pension is zero.",
                        firstPayment, calculationEnd));
            }

            // The first payment date is not after the end date.
            else {

                /*
                 * Calculate the number of days till the next payment. We
                 * assume that there will always be one day between the start
                 * date and the first date of the next month. We would like to
                 * reduce it by one so that there are no days till the next
                 * payment if the payment occurs the next day.
                 */
                final long daysTillPayment =
                        DAYS.between(calculationStart, firstPayment) - 1;

                /*
                 * Adjust the first payment for daily inflation, as requested
                 * and as needed.
                 */
                final double payment = (reduce && (!(0L < daysTillPayment))) ?
                        inflate(monthly, daily, daysTillPayment) :
                        monthly.getValue();

                // Calculate the geometric sum of the payments.
                result = geometricSum(payment, reduce ?
                                getInflation(Annuity.monthly, caddy) :
                                getDefaultInflation(),
                        MONTHS.between(firstPayment, calculationEnd));
            }
        }

        // Return the result.
        return result;
    }

    /**
     * Gets the monthly income of the annuity.
     *
     * @param description A portfolio description containing the monthly income
     *                    data
     * @return The monthly income of the annuity
     */
    protected abstract @NotNull Currency getMonthlyIncome(@NotNull PortfolioDescription
                                                                  description);

    /**
     * Gets the start date of the annuity.
     *
     * @param referenceDate The reference date for calculating the annuity
     *                      start date
     * @return The start date of the annuity (null means that the annuity has
     * already unconditionally started)
     */
    protected Date getStartDate(Date referenceDate) {
        return null;
    }

    @Override
    public @NotNull SynthesizerType getType() {
        return SynthesizerType.ANNUITY;
    }

    /**
     * Reduces the value of currency due to inflation.
     *
     * @param currency  The currency to reduce
     * @param inflation The inflation figure getter
     * @param periods   The number of periods of compounding (duration of period
     *                  is defined by the figure getter)
     * @return The value of the currency reduced due to inflation
     */
    @SuppressWarnings("SameParameterValue")
    private double inflate(@NotNull Currency currency,
                           @NotNull Inflation inflation,
                           long periods) {
        return currency.getValue() /
                Math.pow(getInflation(inflation, caddy), periods);
    }

    /**
     * Determines whether the value of the annuity is reduced for inflation.
     *
     * @return True if the annuity is reduced for inflation; false otherwise
     */
    protected abstract boolean isReduced();

    @Override
    public boolean synthesize(@NotNull Account account) {

        // Call the superclass method. Was this successful?
        boolean result = super.synthesize(account);
        if (result) {

            /*
             * The superclass method was successful. Get the portfolio
             * description from the account. Is the portfolio description null?
             */
            final PortfolioDescription description =
                    account.getPortfolioDescription();
            if (null == description) {

                // The portfolio description is null. Log a warning.
                getLogger().log(Level.WARNING,
                        String.format("Synthesis of annuity value for " +
                                "account '%s' requires a non-null " +
                                "portfolio description.", account.getKey()));
            }

            // The portfolio description is not null.
            else {

                /*
                 * The annuity value cannot be rebalanced, so set its
                 * considered value to zero. Calculate the not-considered value
                 * using the monthly income from the annuity, its start date,
                 * the projected participant mortality date, the account
                 * valuation date, and whether the annuity is reduced for
                 * inflation.
                 */
                account.setConsidered(0.);
                account.setNotConsidered(calculateValue(
                        getMonthlyIncome(getMonthlyIncome(description)),
                        getStartDate(description.getBirthdate()),
                        description.getMortalityDate(), account.getDate(),
                        isReduced()));
            }
        }

        // Return the result of the synthesis.
        return result;
    }

    private interface Inflation {

        /**
         * Gets an inflation figure from an inflation caddy.
         *
         * @param caddy The caddy from which to get an inflation figure
         * @return An inflation figure
         */
        Double getInflation(@NotNull InflationCaddy caddy);
    }
}
