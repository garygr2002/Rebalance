package com.garygregg.rebalance.report;

import com.garygregg.rebalance.*;
import com.garygregg.rebalance.account.AccountDescription;
import com.garygregg.rebalance.countable.*;
import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.hierarchy.Account;
import com.garygregg.rebalance.hierarchy.Institution;
import com.garygregg.rebalance.hierarchy.Portfolio;
import com.garygregg.rebalance.hierarchy.Ticker;
import com.garygregg.rebalance.ticker.*;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class ActionReportWriter extends HierarchyWriter {

    // A string to separate accounts in a report
    private static final String accountSeparator =
            String.format("%s\n", "-".repeat(65));

    // The default rebalance procedure
    private static final RebalanceProcedure defaultProcedure =
            RebalanceProcedure.PERCENT;

    // A string to separate institutions in a report
    private static final String institutionSeparator =
            String.format("%s\n", "*".repeat(80));

    // The value of minus one share
    private static final Shares minusOne = new Shares(-1.);

    // The message used for reporting the name of a description
    private static final String nameMessage = "Name: '%s'\n\n";

    // The message written when there are no tickers matching given criteria
    private static final String noTickers = "There are no tickers that " +
            "require rebalance by %s.\n";

    // The message used for tickers not considered for rebalance
    private static final String notConsideredMessage = "Problem in ticker " +
            "%-5s ('%s'; Number %s)!\n";

    // The message used for unknown ticker description
    private static final String nullMessage = "Ticker %-5s does not have a " +
            "description.\n";

    // The message used for percentage reallocation
    private static final String percentageMessage = "Allocate %7s%% to %-5s " +
            "('%s'; Number: %s).\n";

    // The common portfolio/institution/account declaration string
    private static final String requiredRebalance = "Required rebalance " +
            "actions for %s key '%s'.\n";

    // The message used for buying and selling shares
    private static final String sharesMessage = "%-4s %14s shares of %-5s " +
            "('%s'; Number: %s).\n";

    // A temporary report string
    private static final String temporaryReport = "There is/are %d tickers " +
            "that need rebalance %s.\n";

    // The message used for unknown ticker description
    private static final String unknownMessage = "Ticker %-5s ('%s'; Number " +
            "%s) has a description of an unknown type.\n";

    // The value of zero currency
    private static final Currency zeroCurrency = Currency.getZero();

    // The value of zero percent
    private static final Percent zeroPercent = Percent.getZero();

    // The value of zero shares
    private static final Shares zeroShares = Shares.getZero();

    // A list of tickers to be rebalanced by currency transfer
    private final List<Ticker> byCurrency = new ArrayList<>();

    // A list of tickers to be rebalanced by shares purchases or sales
    private final List<Ticker> byShares = new ArrayList<>();

    // A reusable list of currency/ticker pairs
    private final List<Pair<MutableCurrency, Ticker>> differencePairs =
            new ArrayList<>();

    // A map of ticker description classes to actions
    private final Map<Class<? extends TickerDescription>,
            List<Ticker>> listMap = new HashMap<>();

    // A list of tickers that is not considered for rebalance
    private final List<Ticker> notConsidered = new ArrayList<>();

    // A list of tickers that have null ticker descriptions
    private final List<Ticker> nullDescription = new ArrayList<>();

    // A replacement action for building the currency/ticker pair list
    private final Action<Ticker> tickerAction = new ListAdder();

    // A list of tickers that have descriptions of unknown types
    private final List<Ticker> unknownDescription = new ArrayList<>();

    // The rebalance procedure for the current account
    private RebalanceProcedure procedure = defaultProcedure;

    {

        /*
         * Build list map with the various classes of ticker description
         * classes mapped to the lists that receive the corresponding ticker
         * symbols.
         */
        listMap.put(ETFDescription.class, byShares);
        listMap.put(FundDescription.class, byCurrency);
        listMap.put(NotConsideredDescription.class, notConsidered);
        listMap.put(null, nullDescription);
        listMap.put(StockDescription.class, byShares);
    }

    /**
     * Calculates the difference between proposed and considered share values
     * in a ticker.
     *
     * @param shares Receives the difference (will be an absolute value)
     * @param ticker A ticker containing proposed and considered share values
     * @return True if the value placed in the shares object was positive
     */
    private static boolean calculateDifference(@NotNull MutableShares shares,
                                               @NotNull Ticker ticker) {


        // Get the considered and proposed values from the ticker.
        final Shares considered = ticker.getConsideredShares();
        final Shares proposed = ticker.getProposedShares();

        /*
         * Set the proposed value in the receiving variable. Is the considered
         * value not null?
         */
        shares.set((null == proposed) ? zeroShares : proposed);
        if (null != considered) {

            /*
             * The considered value is not null. Subtract it from the proposed
             * value.
             */
            shares.subtract(considered);
        }

        // Determine if the result is positive. Is the result negative?
        final boolean isPositive =
                zeroShares.compareTo(shares.getImmutable()) <= 0;
        if (!isPositive) {

            /*
             * The result is negative. Multiply by minus one to get its
             * absolute value.
             */
            shares.multiply(minusOne);
        }

        // Return whether the result was positive.
        return isPositive;
    }

    /**
     * Gets an initial percentage list.
     *
     * @param size The size of the desired list
     * @return An initial percentage list
     */
    private static @NotNull List<MutablePercent> getInitialList(int size) {

        // Create the list. Is the size greater than zero?
        final List<MutablePercent> list = new ArrayList<>();
        if (0 < size) {

            /*
             * The size is greater than zero. Add the first element to the
             * list using one hundred percent.
             */
            int i = 0;
            list.add(i, new MutablePercent(Math.abs(100.)));

            // Cycle for any remaining positions, and add zero percent.
            for (++i; i < size; ++i) {
                list.add(new MutablePercent(zeroPercent));
            }
        }

        // Return the list.
        return list;
    }

    /**
     * Gets the name from a description.
     *
     * @param description The description from which to get a name
     * @return The name of the description, or a default if the name is null
     */
    private static @NotNull String getName(Description<?> description) {

        /*
         * Get the name from the description. Return the name, or default if
         * the name is null.
         */
        final String name = (null == description) ? null :
                description.getName();
        return (null == name) ? "<name unavailable" : name;
    }

    /**
     * Gets the number from a ticker description.
     *
     * @param description The description from which to get a number
     * @return The number of the ticker description, or a default if the
     * number is null
     */
    private static Integer getNumber(TickerDescription description) {
        return (null == description) ? null : description.getNumber();
    }

    /**
     * Writes a name string.
     *
     * @param writer The file writer to receive the report lines
     * @param name   The name to write
     * @throws IOException Indicates an I/O exception occurred
     */
    private static void writeName(@NotNull FileWriter writer,
                                  @NotNull String name)
            throws IOException {
        writer.write(String.format(nameMessage, name));
    }

    /**
     * Writes a name string.
     *
     * @param writer      The file writer to receive the report lines
     * @param description A description containing the name to write
     * @throws IOException Indicates an I/O exception occurred
     */
    private static void writeName(@NotNull FileWriter writer,
                                  Description<?> description)
            throws IOException {
        writeName(writer, getName(description));
    }

    /**
     * Adds a difference pair.
     *
     * @param ticker The ticker for which to add a difference pair
     */
    private void addDifferencePair(@NotNull Ticker ticker) {

        /*
         * Get the difference between the proposed and considered values of the
         * ticker. Is the difference not zero?
         */
        final MutableCurrency currency = getDifference(ticker);
        if (currency.isNotZero()) {

            /*
             * The difference is not zero. Add it to the difference list paired
             * with the ticker.
             */
            differencePairs.add(new Pair<>(currency, ticker));
        }
    }

    /**
     * Clears the ticker lists.
     */
    private void clearLists() {

        // Clear each ticker list.
        byCurrency.clear();
        byShares.clear();
        notConsidered.clear();
        nullDescription.clear();
    }

    @Override
    protected void doPreCycle(@NotNull FileWriter writer,
                              @NotNull Portfolio portfolio)
            throws IOException {

        // Write the portfolio key and the portfolio name.
        writer.write(String.format(requiredRebalance, "portfolio", portfolio.getKey()));
        writeName(writer, portfolio.getDescription());
    }

    @Override
    protected void doPreCycle(@NotNull FileWriter writer,
                              @NotNull Institution institution)
            throws IOException {

        // Write an institution separator before describing the institution.
        writer.write(institutionSeparator);
        writer.write(String.format(requiredRebalance, "institution",
                institution.getKey()));
        writeName(writer, institution.getName());
    }

    @Override
    protected void doPreCycle(@NotNull FileWriter writer,
                              @NotNull Account account)
            throws IOException {

        // Write the account separator followed by the account key.
        writer.write(accountSeparator);
        writer.write(String.format(requiredRebalance, "account",
                AccountKey.format(account.getKey().getSecond())));

        /*
         * Get the account description and use it to set the rebalance
         * procedure.
         */
        final AccountDescription description = account.getDescription();
        setProcedure((null == description) ? null :
                description.getRebalanceProcedure());

        // Write the name of the account using its description.
        writeName(writer, description);
    }

    /**
     * Gets the difference between proposed and considered values in a ticker,
     * and returns it as mutable currency.
     *
     * @param ticker The ticker for which to get a difference
     * @return The difference between proposed and considered values in the
     * ticker
     */
    private @NotNull MutableCurrency getDifference(@NotNull Ticker ticker) {

        // Get the considered and proposed values of the ticker.
        final Currency considered = ticker.getConsidered();
        final Currency proposed = ticker.getProposed();

        /*
         * Create a new mutable currency with the proposed value of the ticker
         * if the proposed value is not null. If it is null, use zero. Is the
         * considered value of the ticker not null?
         */
        final MutableCurrency currency =
                new MutableCurrency((null == proposed) ? zeroCurrency : proposed);
        if (null != considered) {

            /*
             * The considered value of the ticker is not null. Subtract it from
             * the proposed value.
             */
            currency.subtract(considered);
        }

        // Return the result.
        return currency;
    }

    /**
     * Gets a ticker list based on a ticker type.
     *
     * @param ticker A ticker
     * @return A ticker list based on the type of the ticker
     */
    private @NotNull List<Ticker> getList(@NotNull Ticker ticker) {

        /*
         * Get the description of the ticker. Get an appropriate list from the
         * list map based on the class of the ticker description. Return an
         * unknown description list if the class of the ticker description is
         * not recognized.
         */
        final TickerDescription description = ticker.getDescription();
        final List<Ticker> list = listMap.get((null == description) ? null :
                description.getClass());
        return (null == list) ? unknownDescription : list;
    }

    @Override
    protected @NotNull String getPrefix() {
        return "action";
    }

    /**
     * Gets the rebalance procedure for the current account.
     *
     * @return The rebalance procedure for the current account
     */
    private @NotNull RebalanceProcedure getProcedure() {
        return procedure;
    }

    /**
     * Reports rebalance actions by currency transfer.
     *
     * @param writer The file writer to receive the report lines
     * @throws IOException Indicates an I/O exception occurred
     */
    private void reportByCurrency(@NotNull FileWriter writer)
            throws IOException {

        /*
         * Write the no-tickers message if there are no tickers needing
         * rebalance by currency transfer.
         */
        if (byCurrency.isEmpty()) {
            writer.write(String.format(noTickers,
                    "redistribution of balances"));
        }

        /*
         * Otherwise, do one thing if the rebalance procedure is
         * redistribution...
         */
        else if (RebalanceProcedure.REDISTRIBUTE.equals(getProcedure())) {
            reportByRedistribution(writer);
        }

        // ...otherwise, do another thing.
        else {
            reportByPercentage(writer);
        }

        // Finish up by writing a newline.
        writer.write("\n");
    }

    /**
     * Reports currency transfer rebalance by percentages.
     *
     * @param writer The file writer to receive the report lines
     * @throws IOException Indicates an I/O exception occurred
     */
    private void reportByPercentage(@NotNull FileWriter writer)
            throws IOException {

        /*
         * Step one: 1. Collect the proposed values of the tickers to be used
         * as reallocation weights. Create a weight list, and cycle for each
         * ticker.
         */
        Currency proposed;
        final List<Double> weights = new ArrayList<>();
        for (Ticker ticker : byCurrency) {

            /*
             * Get the proposed value of the first/next ticker, and add it as a
             * new element of the weight list.
             */
            proposed = ticker.getProposed();
            weights.add((null == proposed) ? zeroCurrency.getValue() :
                    proposed.getValue());
        }

        // Create a percentage list that is the same size as the weight list.
        final List<MutablePercent> percentages =
                getInitialList(weights.size());

        /*
         * Create a new reallocator with the weight list. Can the reallocator
         * reallocate using this weight list?
         */
        final Reallocator reallocator = new Reallocator(weights);
        if (reallocator.canReallocate()) {

            /*
             * The reallocator can reallocate using the given weight list.
             * Reallocate the percentage list. Declare a local variable to
             * receive a ticker description.
             */
            reallocator.reallocate(percentages);
            TickerDescription description;

            // Cycle for each ticker.
            int i = 0;
            for (Ticker ticker : byCurrency) {

                /*
                 * Write about the percentage reallocated to each ticker
                 * symbol.
                 */
                description = ticker.getDescription();
                writer.write(String.format(percentageMessage,
                        percentages.get(i++), ticker.getKey(),
                        getName(description), getNumber(description)));
            }
        }

        /*
         * Reallocation cannot occur. At this time the only reason that would
         * cause this problem is that the sum of the values assigned to tickers
         * is not positive (i.e., it is zero).
         */
        else {
            writer.write("Rebalance impossible; there is no value to " +
                    "rebalance!\n");
        }
    }

    /**
     * Reports currency transfer rebalance by redistribution.
     *
     * @param writer The file writer to receive the report lines
     * @throws IOException Indicates an I/O exception occurred
     */
    private void reportByRedistribution(@NotNull FileWriter writer)
            throws IOException {

        // TODO: Fill this in.
        writer.write(String.format(temporaryReport, byCurrency.size(),
                "by reallocation"));
    }

    /**
     * Reports rebalance actions by buying or selling shares.
     *
     * @param writer The file writer to receive the report lines
     * @throws IOException Indicates an I/O exception occurred
     */
    private void reportByShares(@NotNull FileWriter writer)
            throws IOException {

        /*
         * Write the no-tickers message if there are no tickers needing
         * rebalance by buying or selling shares.
         */
        if (byShares.isEmpty()) {
            writer.write(String.format(noTickers, "buying or selling shares"));
        }

        /*
         * There are one or more tickers needing rebalance by buying or selling
         * shares.
         */
        else {

            // Declare local variables. Initialize them as required.
            TickerDescription description;
            boolean result;
            final MutableShares shares = new MutableShares();

            /*
             * Cycle for each ticker needing rebalance by buying or selling
             * shares.
             */
            for (Ticker ticker : byShares) {

                /*
                 * Get the description from the ticker, and recalculate the
                 * difference between proposed and considered shares.
                 */
                description = ticker.getDescription();
                result = calculateDifference(shares, ticker);

                /*
                 * Write about the buying and/or selling required to achieve the
                 * rebalance.
                 */
                writer.write(String.format(sharesMessage,
                        result ? "Buy" : "Sell", shares, ticker.getKey(),
                        description.getName(), description.getNumber()));
            }
        }

        // Finish up by writing a newline.
        writer.write("\n");
    }

    /**
     * Reports tickers that should not be considered for rebalance but
     * nevertheless have rebalance actions.
     *
     * @param writer The file writer to receive the report lines
     * @throws IOException Indicates an I/O exception occurred
     */
    private void reportNotConsidered(@NotNull FileWriter writer)
            throws IOException {

        // Are there any tickers that are not considered for rebalance?
        boolean problems = false;
        if (0 < notConsidered.size()) {

            /*
             * There are tickers that are not considered for rebalance. Declare
             * a variable to receive a ticker description. Cycle for each
             * ticker.
             */
            TickerDescription description;
            for (Ticker ticker : notConsidered) {

                /*
                 * Determine if the ticker has any difference between proposed
                 * and considered value. Is there a difference?
                 */
                if (getDifference(ticker).isNotZero()) {

                    /*
                     * A ticker not considered for rebalance nevertheless has a
                     * difference between proposed and considered values. This
                     * is a problem. Set the 'problem' flag, and get the
                     * description from the ticker.
                     */
                    problems = true;
                    description = ticker.getDescription();

                    // Write about the problem.
                    writer.write(String.format(notConsideredMessage,
                            ticker.getKey(), getName(description),
                            getNumber(description)));
                }
            }

            /*
             * Finish up by writing a newline if there were one or more
             * problems.
             */
            if (problems) {
                writer.write("\n");
            }
        }
    }

    /**
     * Reports tickers with null ticker descriptions but nevertheless have
     * rebalance actions.
     *
     * @param writer The file writer to receive the report lines
     * @throws IOException Indicates an I/O exception occurred
     */
    private void reportNullDescription(@NotNull FileWriter writer)
            throws IOException {

        // Are there any tickers with null descriptions?
        if (0 < nullDescription.size()) {

            /*
             * There are tickers with null descriptions. Cycle for each ticker,
             * and write about it.
             */
            for (Ticker ticker : nullDescription) {
                writer.write(String.format(nullMessage, ticker.getKey()));
            }

            // Finish up by writing a newline.
            writer.write("\n");
        }
    }

    /**
     * Reports tickers with descriptions of unknown types but nevertheless have
     * rebalance actions.
     *
     * @param writer The file writer to receive the report lines
     * @throws IOException Indicates an I/O exception occurred
     */
    private void reportUnknownDescription(@NotNull FileWriter writer)
            throws IOException {

        // Are there any tickers with descriptions of an unknown type?
        if (0 < unknownDescription.size()) {

            /*
             * There are tickers with descriptions of an unknown type. Declare
             * a variable to receive the ticker description. Cycle for each
             * ticker.
             */
            TickerDescription description;
            for (Ticker ticker : unknownDescription) {

                /*
                 * Get the description from the first/next ticker, and describe
                 * the ticker.
                 */
                description = ticker.getDescription();
                writer.write(String.format(unknownMessage, ticker.getKey(),
                        getName(description), getNumber(description)));
            }

            // Finish up by writing a newline.
            writer.write("\n");
        }
    }

    /**
     * Sets the rebalance procedure for the current account.
     *
     * @param procedure The rebalance procedure for the current account
     */
    private void setProcedure(RebalanceProcedure procedure) {
        this.procedure = (null == procedure) ? defaultProcedure : procedure;
    }

    @Override
    protected void writeLines(@NotNull FileWriter writer,
                              @NotNull Account account)
            throws IOException {

        // Write a newline before performing the pre-cycle action.
        writer.write("\n");
        doPreCycle(writer, account);

        /*
         * Iterate over the children of the account using the ticker action.
         * Report share buy/sell actions that are required to achieve the
         * rebalance.
         */
        iterate(writer, account.getChildren(), tickerAction);
        reportByShares(writer);

        /*
         * Report currency transfer actions that are required to achieve the
         * rebalance. Report tickers that should not be considered for
         * rebalance but nevertheless have rebalance actions.
         */
        reportByCurrency(writer);
        reportNotConsidered(writer);

        /*
         * Report tickers that have null, or unknown ticker descriptions but
         * nevertheless have rebalance actions. Clear the ticker lists.
         */
        reportNullDescription(writer);
        reportUnknownDescription(writer);
        clearLists();
    }

    @Override
    protected void writeLines(@NotNull FileWriter writer,
                              @NotNull Ticker ticker) {
        throw new RuntimeException("The call to this method has been " +
                "intercepted, and should not happen!");
    }

    @Override
    protected void writeLines(@NotNull FileWriter writer,
                              @NotNull Institution institution)
            throws IOException {

        // Write a newline, then call the superclass method.
        writer.write("\n");
        super.writeLines(writer, institution);
    }

    // An action that adds tickers to lists
    private class ListAdder implements Action<Ticker> {

        // A reusable mutable shares object
        private final MutableShares shares = new MutableShares();

        @Override
        public void doAction(@NotNull FileWriter writer,
                             @NotNull Ticker ticker) {

            /*
             * Get the reusable mutable shares object. Calculate the difference
             * between the proposed and considered shares of the ticker. Is the
             * result not zero?
             */
            final MutableShares shares = getShares();
            calculateDifference(shares, ticker);
            if (shares.isNotZero()) {

                /*
                 * The difference between the proposed and considered shares of
                 * the ticker is not zero. Get an appropriate list for the
                 * ticker, and add the ticker to that list.
                 */
                getList(ticker).add(ticker);
            }
        }

        /**
         * Gets the reusable mutable shares object.
         *
         * @return The reusable mutable shares object
         */
        public @NotNull MutableShares getShares() {
            return shares;
        }
    }
}
