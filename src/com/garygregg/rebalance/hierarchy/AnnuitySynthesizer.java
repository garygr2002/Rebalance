package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.InflationCaddy;
import com.garygregg.rebalance.SynthesizerType;
import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.distinguished.DistinguishedAccounts;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.logging.Level;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;

abstract public class AnnuitySynthesizer extends Synthesizer {

    // The daily inflation getter
    private static final Inflation daily = InflationCaddy::getDaily;

    // The monthly inflation getter
    private static final Inflation monthly = InflationCaddy::getMonthly;

    // An inflation caddy instance
    private final InflationCaddy caddy = InflationCaddy.getInstance();

    /**
     * Constructs the annuity synthesizer.
     *
     * @param account The distinguished account associated with this
     *                synthesizer
     */
    public AnnuitySynthesizer(@NotNull DistinguishedAccounts account) {
        super(account);
    }

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
     * Calculates a pension value.
     *
     * @param monthly The monthly payment
     * @param start   The start date of the calculation
     * @param end     The end date of the calculation
     * @param reduce  Reduce the monthly payment for inflation
     * @return The value of the pension
     */
    protected @NotNull Currency calculateValue(@NotNull Currency monthly,
                                               Date start,
                                               @NotNull Date end,
                                               boolean reduce) {

        /*
         * Declare the result, and initialize it to zero. Calculate the local
         * start date.
         */
        Currency result = Currency.getZero();
        final LocalDate localStart = (null == start) ? LocalDate.now() :
                convert(start);

        /*
         * Convert the end date to a local date. Is the start date after the
         * end date?
         */
        final LocalDate localEnd = convert(end);
        if (localStart.isAfter(localEnd)) {

            /*
             * The start date is after the end date. Log an informational
             * message; the result of the calculation is zero.
             */
            getLogger().logMessage(Level.INFO, String.format("Start " +
                            "date of '%s' occurs after end date of '%s'; " +
                            "the value of the pension is zero.",
                    localStart, localEnd));
        }

        // The start date is not after the end date.
        else {

            /*
             * The date of the first payment is the first day of the month
             * after the start date. Does the first payment occur after the end
             * date?
             */
            final LocalDate localFirstPayment =
                    localStart.with(TemporalAdjusters.firstDayOfNextMonth());
            if (localFirstPayment.isAfter(localEnd)) {

                /*
                 * The first payment occurs after the end date. Log an
                 * informational message; the result of the calculation is
                 * zero.
                 */
                getLogger().logMessage(Level.INFO, String.format("First " +
                                "payment date of '%s' occurs after end " +
                                "date of '%s'; value of the pension is zero.",
                        localFirstPayment, localEnd));
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
                        DAYS.between(localStart, localFirstPayment) - 1;

                /*
                 * Adjust the first payment for daily inflation, as requested
                 * and as needed.
                 */
                final double payment = (reduce && (!(0L < daysTillPayment))) ?
                        inflate(monthly, daily, daysTillPayment) :
                        monthly.getValue();

                // Calculate the geometric sum of the payments.
                result = geometricSum(payment, reduce ?
                                getInflation(AnnuitySynthesizer.monthly,
                                        caddy) : getDefaultInflation(),
                        MONTHS.between(localFirstPayment, localEnd));
            }
        }

        // Return the result.
        return result;
    }

    /**
     * Calculates a geometric sum of currency.
     *
     * @param payment The payments
     * @param rate    The rate
     * @param periods The number of periods
     * @return A geometric sum of the payments for the number of periods
     */
    private @NotNull Currency geometricSum(double payment,
                                           double rate,
                                           long periods) {

        /*
         * Convert the geometric sum, and wrap the result in a new
         * currency object before returning it.
         */
        final double forbiddenRate = 1.;
        return new Currency((forbiddenRate == rate) ? payment * periods :
                (payment - payment * Math.pow(rate, periods + 1) /
                        (forbiddenRate - rate)));
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
