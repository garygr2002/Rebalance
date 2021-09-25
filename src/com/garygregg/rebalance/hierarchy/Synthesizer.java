package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.AccountKey;
import com.garygregg.rebalance.InflationCaddy;
import com.garygregg.rebalance.MessageLogger;
import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.distinguished.DistinguishedAccountLibrary;
import com.garygregg.rebalance.distinguished.DistinguishedAccounts;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.logging.Level;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;

abstract class Synthesizer {

    // The daily inflation getter
    private static final Inflation daily = InflationCaddy::getDaily;

    // The monthly inflation getter
    private static final Inflation monthly = InflationCaddy::getMonthly;

    // An inflation caddy instance
    private final InflationCaddy caddy = InflationCaddy.getInstance();

    // Our local message logger
    private final MessageLogger messageLogger = new MessageLogger();

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
         * Use the getter to access inflation from the caddy. Return a default
         * if the result is null, otherwise return the result.
         */
        final Double result = inflation.getInflation(caddy);
        return (null == result) ? getDefaultInflation() : result;
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

            // The start date is after the end date. Log a warning.
            getLogger().logMessage(Level.WARNING, String.format("Start " +
                            "date of '%s' occurs before end date of '%s'; cannot " +
                            "calculate value of monthly payments.",
                    localStart, localEnd));
        }

        // The start date is not after the end date.
        else {

            /*
             * The date of the first payment is the first day of the month
             * after the start date.
             */
            final LocalDate localFirstPayment =
                    localStart.with(TemporalAdjusters.firstDayOfNextMonth());

            // Adjust the first payment for daily inflation, as requested.
            final Currency firstPayment = reduce ? inflate(monthly, daily,
                    DAYS.between(localStart, localFirstPayment)) : monthly;

            result = geometricSum(firstPayment, reduce ? getInflation(Synthesizer.monthly, caddy) : getDefaultInflation(),
                    MONTHS.between(localFirstPayment, localEnd));
        }

        return result;
    }

    /**
     * Calculates a geometric sum of currency.
     *
     * @param currency The currency
     * @param rate     The rate
     * @param periods  The number of periods
     * @return A geometric sum of the given currency
     */
    private @NotNull Currency geometricSum(@NotNull Currency currency,
                                           double rate,
                                           long periods) {

        /*
         * Declare and initialize the forbidden rate. Get the payment from the
         * currency.
         */
        final double forbiddenRate = 1.;
        final double payment = currency.getValue();

        /*
         * Convert the geometric sum, and wrap the result in a new
         * currency object before returning it.
         */
        return new Currency((forbiddenRate == rate) ? payment * periods :
                (payment - payment * Math.pow(rate, periods + 1) /
                        (forbiddenRate - rate)));
    }

    /**
     * Gets the distinguished account for which this synthesizer is intended.
     *
     * @return The distinguished account for which this synthesizer is intended
     */
    public abstract @NotNull DistinguishedAccounts getAccount();

    /**
     * Gets the account key for which this synthesizer is intended.
     *
     * @return The account key for which this synthesizer is intended
     */
    public AccountKey getKey() {
        return DistinguishedAccountLibrary.getInstance().
                getValue(getAccount());
    }

    /**
     * Gets the message logger for the synthesizer.
     *
     * @return The message logger for the synthesizer
     */
    protected MessageLogger getLogger() {
        return messageLogger;
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
    private @NotNull Currency inflate(@NotNull Currency currency,
                                      @NotNull Inflation inflation,
                                      long periods) {
        return new Currency(currency.getValue() /
                Math.pow(getInflation(inflation, caddy), periods));
    }

    /**
     * Synthesizes an account.
     *
     * @param account The account to synthesize
     * @return True if the account was successfully synthesized, false
     * otherwise
     */
    public boolean synthesize(@NotNull Account account) {

        /*
         * Note: This method should be first called by any class overriding
         * it, and cancel its work if the result is false. Reset the logger
         * for problems. Get the key of given account, and the intended key.
         */
        getLogger().resetProblem1();
        final AccountKey givenKey = account.getKey();
        final AccountKey intendedKey = getKey();

        // The intended key should equal the given key. Is this not so?
        boolean result = (null != intendedKey) &&
                (0 == intendedKey.compareTo(givenKey));
        if (!result) {

            // The intended key does not equal the given key. Log a warning.
            getLogger().logMessage(Level.WARNING, String.format("Rejecting " +
                            "attempt to synthesize account with key '%s' using a " +
                            "synthesizer intended for key '%s'.", givenKey,
                    intendedKey));
        }

        /*
         * The intended key equals the given key. Value must not have been set
         * in the account for it to be synthesized. Has value already been set
         * in the account?
         */
        else if (!(result = !account.hasValueBeenSet())) {

            // Value has already been set in the account. Log a warning.
            getLogger().logMessage(Level.WARNING, String.format("Rejecting " +
                    "attempt to synthesize account with key '%s' that " +
                    "already has value set.", givenKey));
        }

        /*
         * The account does not already have value set. The account must not
         * have children in order to be synthesized. Does the account have
         * children?
         */
        else if (!(result = !account.hasChildren())) {

            // The account has children. Log a warning.
            getLogger().logMessage(Level.WARNING, String.format("Rejecting " +
                    "attempt to synthesize account with key '%s' that " +
                    "has children.", givenKey));
        }

        // The account does not have children.
        else {

            /*
             * Log some information about beginning synthesis. Set the account
             * as synthesized.
             */
            getLogger().logMessage(Level.FINEST, String.format("Beginning " +
                    "synthesis for account with key '%s'.", givenKey));
            account.setSynthesized();
        }

        // Return the result.
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
