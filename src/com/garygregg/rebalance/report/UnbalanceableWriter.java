package com.garygregg.rebalance.report;

import com.garygregg.rebalance.Pair;
import com.garygregg.rebalance.account.AccountDescription;
import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.countable.MutableCurrency;
import com.garygregg.rebalance.hierarchy.*;
import com.garygregg.rebalance.ticker.TickerDescription;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

class UnbalanceableWriter {

    // The format for a heading line
    private static final String headingFormat;

    // The length of a long table field
    private static final int longFieldLength = 63;

    // The format for a number line
    private static final String numberFormat;

    // The length of a short table field
    private static final int shortFieldLength = 15;

    static {

        // Construct the formats.
        headingFormat = constructFormat(false);
        numberFormat = constructFormat(true);
    }

    // A name getter for accounts
    private final NameGetter<Account> accountNameGetter = account -> {

        /*
         * Get the description from the account. Return the key of the account
         * as a string if its description is null, otherwise return the name
         * of the account.
         */
        final AccountDescription description = account.getDescription();
        return (null == description) ? account.getKey().toString() :
                description.getName();
    };

    // The list of holdings
    private final List<Pair<Currency, String>> holdingList = new ArrayList<>();

    // A name getter for tickers
    private final NameGetter<Ticker> tickerNameGetter = ticker -> {

        /*
         * Get the description from the ticker. Return the key of the ticker
         * as a string if its description is null, otherwise return the name
         * of the ticker.
         */
        final TickerDescription description = ticker.getDescription();
        return (null == description) ? ticker.getKey() : description.getName();
    };

    // The visitation action for an account
    private final VisitationAction<Account> visitAccount =
            this::addHolding;

    // The visitation action for an institution
    private final VisitationAction<Institution> visitInstitution =
            this::visit;

    // The recipient for writer output
    private final FileWriter writer;

    /**
     * Constructs the unbalance-able writer.
     *
     * @param writer The recipient for writer output
     */
    public UnbalanceableWriter(@NotNull FileWriter writer) {
        this.writer = writer;
    }

    /**
     * Constructs a table format.
     *
     * @param rightJustify True if the last column should be right justified;
     *                     false otherwise
     * @return A table format
     */
    private static String constructFormat(boolean rightJustify) {
        return String.format("%%-%ds ", getLongFieldLength()) +
                String.format("%%%s%ds%n", rightJustify ? "" : "-",
                        getShortFieldLength());
    }

    /**
     * Gets the format for a heading line.
     *
     * @return The format for a heading line
     */
    private static String getHeadingFormat() {
        return headingFormat;
    }

    /**
     * Gets the length of a long table field.
     *
     * @return The length of a long table field
     */
    public static int getLongFieldLength() {
        return longFieldLength;
    }

    /**
     * Gets the format for a number line.
     *
     * @return The format for a number line
     */
    private static String getNumberFormat() {
        return numberFormat;
    }

    /**
     * Gets the length of a short table field.
     *
     * @return The length of a short table field
     */
    public static int getShortFieldLength() {
        return shortFieldLength;
    }

    /**
     * Visits children of an aggregate.
     *
     * @param action      The visitation action to perform
     * @param aggregate   The aggregate
     * @param <ChildType> The type of children of the aggregate
     * @param <T>         The type of the aggregate
     */
    private static <ChildType extends Queryable<?, ?>,
            T extends Queryable<?, ChildType>> void visitAggregate(
            @NotNull VisitationAction<ChildType> action,
            @NotNull T aggregate) {

        // Get the children of the aggregate, and cycle for each.
        final Collection<ChildType> children = aggregate.getChildren();
        for (ChildType child : children) {

            // Visit the first/next child.
            action.visitChild(child);
        }
    }

    /***
     * Adds a holding to the list of holdings.
     *
     * @param value The value of the holding
     * @param hierarchyObject A hierarchy object
     * @param getter A name getter for the type of the hierarchy object
     * @param <HierarchyType> The type of the hierarchy object
     */
    private <HierarchyType> void addHolding(Currency value,
                                            @NotNull HierarchyType hierarchyObject,
                                            @NotNull NameGetter<? super HierarchyType> getter) {

        // Only add the holding if its value is neither null nor zero.
        if ((null != value) && value.isNotZero()) {
            holdingList.add(new Pair<>(value,
                    getter.getName(hierarchyObject)));
        }
    }

    /**
     * Adds the holdings in an account.
     *
     * @param account The account for which to add holdings
     */
    private void addHolding(@NotNull Account account) {

        /*
         * Get a well-known 'not considered' valuator instance. Get the
         * children of the account. Are there any children?
         */
        final Valuator valuator = ValueByNotConsidered.getInstance();
        final Collection<Ticker> tickers = account.getChildren();
        if (tickers.isEmpty()) {

            /*
             * There are no children of the account. Add the value of the
             * account itself as a holding.
             */
            addHolding(valuator.getValue(account), account, accountNameGetter);
        }

        // The account has children.
        else {

            // Cycle for each child ticker, and add the value of the holding.
            for (Ticker ticker : tickers) {
                addHolding(valuator.getValue(ticker), ticker,
                        tickerNameGetter);
            }
        }
    }

    /**
     * Clears the list of holdings.
     */
    private void clearHoldings() {
        holdingList.clear();
    }

    /**
     * Gets an iterator for the list of holdings.
     *
     * @return An iterator for the list of holdings
     */
    private @NotNull Iterator<Pair<Currency, String>> getHoldings() {
        return holdingList.listIterator();
    }

    /**
     * Gets the recipient for writer output.
     *
     * @return The recipient for writer output
     */
    private @NotNull FileWriter getWriter() {
        return writer;
    }

    /**
     * Sorts the holdings in reverse order.
     */
    private void sortHoldings() {
        holdingList.sort((first, second) -> (int) Math.signum(
                second.getFirst().getValue() - first.getFirst().getValue()));
    }

    /**
     * Visits a portfolio.
     *
     * @param portfolio The portfolio to visit
     */
    private void visit(@NotNull Portfolio portfolio) {
        visitAggregate(visitInstitution, portfolio);
    }

    /**
     * Visits an institution.
     *
     * @param institution The institution to visit
     */
    private void visit(@NotNull Institution institution) {
        visitAggregate(visitAccount, institution);
    }

    /**
     * Writes a portfolio summary.
     *
     * @param portfolio A portfolio that is the subject of the summary
     * @return True if the summary was successfully written; false otherwise
     * @throws IOException Indicates an I/O exception occurred
     */
    public boolean writeSummary(@NotNull Portfolio portfolio)
            throws IOException {

        /*
         * Get the file writer. Write a descriptive message about the table we
         * are about to write.
         */
        final FileWriter writer = getWriter();
        writer.write("Holdings that cannot be rebalanced:\n\n");

        // Clear the holdings, and visit the portfolio.
        clearHoldings();
        visit(portfolio);

        // Sort the holding, write the summary, and return to caller.
        sortHoldings();
        writeSummary(portfolio.getKey());
        return true;
    }

    /***
     * Writes a portfolio summary.
     * @param name The name of the portfolio
     * @throws IOException Indicates an I/O exception occurred
     */
    private void writeSummary(@NotNull String name) throws IOException {

        // Get the heading and number formats.
        final String headingFormat = getHeadingFormat();
        final String numberFormat = getNumberFormat();

        // Get the file writer, and write the header for the table.
        final FileWriter writer = getWriter();
        writer.write(String.format(headingFormat, "Holding Name", "Value"));

        /*
         * Create a delimiter for use between the table header and table data
         * lines, and between the table data lines and table summary.
         */
        final String delimiterCharacter = "-";
        final String delimiterLine = String.format(headingFormat,
                delimiterCharacter.repeat(getLongFieldLength()),
                delimiterCharacter.repeat(getShortFieldLength()));

        /*
         * Write the delimiter, as we have already written the table header.
         * Declare a currency variable to receive the value of the holding
         * items.
         */
        writer.write(delimiterLine);
        Currency currency;

        /*
         * Declare and initialize a variable to hold the total of the holding
         * items. Declare a variable hold a value/name pair for the holdings.
         */
        final MutableCurrency total = new MutableCurrency();
        Pair<Currency, String> holdingPair;

        // Get an iterator for the holdings, and cycle while holdings exist.
        final Iterator<Pair<Currency, String>> iterator = getHoldings();
        while (iterator.hasNext()) {

            /*
             * Get the first/next holding pair, and the value of the holding
             * from the pair.
             */
            holdingPair = iterator.next();
            currency = holdingPair.getFirst();

            /*
             * Add the value of the holding to the total, and write a data
             * line for the holding.
             */
            total.add(currency);
            writer.write(String.format(numberFormat, holdingPair.getSecond(),
                    currency));
        }

        /*
         * Write another delimiter, followed by a summary line for the whole
         * portfolio.
         */
        writer.write(delimiterLine);
        writer.write(String.format(numberFormat, name, total));
    }

    private interface NameGetter<T> {

        /**
         * Gets the name from an object.
         *
         * @param object The object from which to get a name
         * @return A name for the given object
         */
        @NotNull String getName(@NotNull T object);
    }

    private interface VisitationAction<Aggregate> {

        /**
         * Writes an aggregate.
         *
         * @param aggregate An aggregate
         */
        void visitChild(@NotNull Aggregate aggregate);
    }
}
