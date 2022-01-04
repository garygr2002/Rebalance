package com.garygregg.rebalance.report;

import com.garygregg.rebalance.AccountKey;
import com.garygregg.rebalance.Pair;
import com.garygregg.rebalance.account.AccountDescription;
import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.countable.MutableCurrency;
import com.garygregg.rebalance.hierarchy.Account;
import com.garygregg.rebalance.hierarchy.Institution;
import com.garygregg.rebalance.hierarchy.Portfolio;
import com.garygregg.rebalance.hierarchy.Ticker;
import com.garygregg.rebalance.portfolio.PortfolioDescription;
import com.garygregg.rebalance.ticker.TickerDescription;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ActionReportWriter extends HierarchyWriter {

    // A string to separate accounts in a report
    private static final String accountSeparator =
            String.format("%s\n", "-".repeat(65));

    // The common portfolio/institution/account declaration string
    private static final String format = "Required rebalance actions for " +
            "%s key '%s'.\n";

    // A string to separate institutions in a report
    private static final String institutionSeparator =
            String.format("%s\n", "*".repeat(80));

    // The value of zero currency
    private static final Currency zero = Currency.getZero();

    // A reusable list of currency/ticker pairs
    final List<Pair<MutableCurrency, Ticker>> differencePairs =
            new ArrayList<>();

    // A replacement action for building the currency/ticker pair list
    private final Action<Ticker> tickerAction = (writer, ticker) -> {

        /*
         * Get the description from the ticker. Is the description not null,
         * and does the non-null description indicate that the ticker is
         * considered for rebalance?
         */
        final TickerDescription description = ticker.getDescription();
        if ((null != description) && description.isConsidered()) {

            /*
             * The ticker description is not null, and the non-null description
             * indicates that the ticker is considered for rebalance. Add a
             * difference pair for the ticker.
             */
            addDifferencePair(ticker);
        }
    };

    /**
     * Writes a name string.
     *
     * @param writer The file writer to receive the report lines
     * @param name   The name to write
     * @throws IOException Indicates an I/O exception occurred
     */
    private static void writeName(@NotNull FileWriter writer, String name)
            throws IOException {
        writer.write(String.format("Name: '%s'\n", (null == name) ?
                "<name unavailable>" : name));
    }

    /**
     * Adds a difference pair.
     *
     * @param ticker The ticker for which to add a difference pair
     */
    private void addDifferencePair(@NotNull Ticker ticker) {

        // Get the considered and proposed values of the ticker.
        final Currency considered = ticker.getConsidered();
        final Currency proposed = ticker.getProposed();

        /*
         * Create a new mutable currency with the proposed value of the ticker
         * if the proposed value is not null. If it is null, use zero. Is the
         * considered value of the ticker not null?
         */
        final MutableCurrency currency =
                new MutableCurrency((null == proposed) ? zero : proposed);
        if (null != considered) {

            /*
             * The considered value of the ticker is not null. Subtract it from
             * the proposed value.
             */
            currency.subtract(considered);
        }

        /*
         * Add a new difference pair with the value just calculated and the
         * ticker, but only if the difference value is not zero.
         */
        if (currency.isNotZero()) {
            differencePairs.add(new Pair<>(currency, ticker));
        }
    }

    @Override
    protected void doPreCycle(@NotNull FileWriter writer,
                              @NotNull Portfolio portfolio)
            throws IOException {

        // Write the portfolio key. Get the portfolio description.
        writer.write(String.format(format, "portfolio", portfolio.getKey()));
        final PortfolioDescription description = portfolio.getDescription();

        // Write the portfolio name.
        writeName(writer, (null == description) ? "<name unavailable>" :
                description.getName());
    }

    @Override
    protected void doPreCycle(@NotNull FileWriter writer,
                              @NotNull Institution institution)
            throws IOException {

        // Write an institution separator before describing the institution.
        writer.write(institutionSeparator);
        writer.write(String.format(format, "institution",
                institution.getKey()));
        writeName(writer, institution.getName());
    }

    @Override
    protected void doPreCycle(@NotNull FileWriter writer,
                              @NotNull Account account)
            throws IOException {

        // Write the account separator followed by the account key.
        writer.write(accountSeparator);
        writer.write(String.format(format, "account",
                AccountKey.format(account.getKey().getSecond())));

        // Write the name of the account.
        final AccountDescription description = account.getDescription();
        writeName(writer, (null == description) ? "<name unavailable>" :
                description.getName());
    }

    @Override
    protected @NotNull String getPrefix() {
        return "action";
    }

    /**
     * Publishes rebalances actions contained in the differences list.
     *
     * @param writer The file writer to receive the report lines
     * @throws IOException Indicates an I/O exception occurred
     */
    private void publishActions(@NotNull FileWriter writer) throws IOException {

        // TODO: Fill this out.
        writer.write("Rebalance actions are required for this " +
                "account!\n");
    }

    @Override
    protected void writeLines(@NotNull FileWriter writer,
                              @NotNull Account account)
            throws IOException {

        /*
         * Write a newline before performing the pre-cycle action. Clear the
         * difference pairs list.
         */
        writer.write("\n");
        doPreCycle(writer, account);
        differencePairs.clear();

        /*
         * Iterate over the children of the account using the ticker action.
         * Sort the difference pairs by their difference components.
         */
        iterate(writer, account.getChildren(), tickerAction);
        differencePairs.sort(Comparator.comparing(Pair::getFirst));

        /*
         * Publish the required rebalance actions if there are one or more
         * difference pairs for the account.
         */
        if (0 < differencePairs.size()) {
            publishActions(writer);
        }

        /*
         * Otherwise, write a message describing that no rebalance actions are
         * required.
         */
        else {
            writer.write("No rebalance actions are required for this " +
                    "account.\n");
        }
    }

    @Override
    protected void writeLines(@NotNull FileWriter writer,
                              @NotNull Institution institution)
            throws IOException {

        // Write a newline, then call the superclass method.
        writer.write("\n");
        super.writeLines(writer, institution);
    }

    @Override
    protected void writeLines(@NotNull FileWriter writer,
                              @NotNull Ticker ticker)
            throws IOException {

        // TODO: Delete this.
        writer.write(String.format(format, "ticker", ticker.getKey()));
    }
}
