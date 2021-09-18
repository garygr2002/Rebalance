package com.garygregg.rebalance.report;

import com.garygregg.rebalance.CategoryType;
import com.garygregg.rebalance.Reallocator;
import com.garygregg.rebalance.WeightType;
import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.countable.MutablePercent;
import com.garygregg.rebalance.countable.Percent;
import com.garygregg.rebalance.hierarchy.Institution;
import com.garygregg.rebalance.hierarchy.Portfolio;
import com.garygregg.rebalance.hierarchy.Valuator;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

class BalanceableWriter {

    // The length of a table field
    private static final int fieldLength = 15;

    // The format for a heading line
    private static final String headingFormat;

    // The format for a number line
    private static final String numberFormat;

    // The format for a summary line
    private static final String summaryFormat;

    // The number of value columns in a table
    private static final int valueColumns = 4;

    // The highest level weight types
    private static final WeightType[] weightTypes = {WeightType.STOCK,
            WeightType.BOND, WeightType.CASH, WeightType.REAL_ESTATE};

    static {

        // Construct the formats.
        final int valueColumns = getValueColumns();
        headingFormat = constructFormat(false, valueColumns);
        numberFormat = constructFormat(true, valueColumns);
        summaryFormat = constructFormat(true, 2);
    }

    // The valuator for balance-able values ('considered' or proposed)
    private final Valuator valuator;

    // The recipient for writer output
    private final FileWriter writer;

    /**
     * Constructs the balance-able writer.
     *
     * @param writer   The recipient for writer output
     * @param valuator The valuator for balance-able values('considered' or
     *                 proposed)
     */
    public BalanceableWriter(@NotNull FileWriter writer,
                             @NotNull Valuator valuator) {

        // Set the member variables.
        this.writer = writer;
        this.valuator = valuator;
    }

    /**
     * Constructs a table format.
     *
     * @param rightJustify True if the columns after the first should be right
     *                     justified; false otherwise
     * @param valueColumns The number of value columns
     * @return A table format
     */
    private static String constructFormat(boolean rightJustify,
                                          int valueColumns) {

        // Get the field length and construct the format string.
        final int fieldLength = getFieldLength();
        return String.format("%%-%ds", fieldLength) +
                String.valueOf(String.format(" %%%s%ds",
                        rightJustify ? "" : "-",
                        fieldLength)).repeat(Math.max(0, valueColumns)) +
                "%n";
    }

    /**
     * Gets the length of a table field.
     *
     * @return The length of a table field
     */
    private static int getFieldLength() {
        return fieldLength;
    }

    /**
     * Gets the format for a summary line.
     *
     * @return The format for a summary line
     */
    private static String getSummaryFormat() {
        return summaryFormat;
    }

    /**
     * Gets the number of value columns in a table.
     *
     * @return The number of value columns in a table
     */
    private static int getValueColumns() {
        return valueColumns;
    }

    /**
     * Gets the highest level weight types.
     *
     * @return The highest level weight types
     */
    private static @NotNull WeightType[] getWeightTypes() {
        return weightTypes;
    }

    /**
     * Gets the format for a heading line.
     *
     * @return The format for a heading line
     */
    private String getHeadingFormat() {
        return headingFormat;
    }

    /**
     * Creates a list of institutions included in a portfolio, sorts them by
     * descending balanceable value, and returns the list.
     */
    private @NotNull List<Institution> getInstitutions(@NotNull Portfolio portfolio) {

        /*
         * Create a list of the children of the portfolio. These will be
         * institutions. Sort them in order of their value using the
         * balanceable valuator.
         */
        final List<Institution> institutions =
                new ArrayList<>(portfolio.getChildren());
        institutions.sort(new Comparator<>() {

            // We use the balanceable valuator for this sort
            final Valuator valuator = getValuator();

            @Override
            public int compare(@NotNull Institution first,
                               @NotNull Institution second) {

                /*
                 * Get the result of subtracting the value of the first
                 * institution from the second, then return the sign of the
                 * result.
                 */
                final double difference = getValue(second) - getValue(first);
                return (int) Math.signum(difference);
            }

            /**
             * Gets the 'considered' value of an institution.
             *
             * @param institution The institution for which to get a
             *                    'considered' value
             * @return The 'considered' value of an institution
             */
            private double getValue(@NotNull Institution institution) {
                return valuator.getValue(institution).getValue();
            }
        });

        // Return the list of sorted institutions.
        return institutions;
    }

    /**
     * Gets the format for a number line.
     *
     * @return The format for a number line
     */
    private String getNumberFormat() {
        return numberFormat;
    }

    /**
     * Breaks down a portfolio into percentages based on the highest level
     * weight type values.
     *
     * @param portfolio A portfolio
     * @return A map of the highest level weight type values mapped to their
     * percentages in the portfolio
     */
    private @NotNull Map<WeightType, MutablePercent> getPercentages(
            @NotNull Portfolio portfolio) {

        /*
         * Declare and initialize an array of the highest level weight types,
         * and an array index.
         */
        final WeightType[] types = getWeightTypes();
        int i = 0;

        /*
         * Get our valuator. Use it to create a list of portfolio weights using
         * the highest level weight types.
         */
        final Valuator valuator = getValuator();
        final List<Double> weights = List.of(
                valuator.getValue(portfolio, types[i++]).getValue(),
                valuator.getValue(portfolio, types[i++]).getValue(),
                valuator.getValue(portfolio, types[i++]).getValue(),
                valuator.getValue(portfolio, types[i]).getValue()
        );

        /*
         * Declare and initialize a list of percentages. Get the size of the
         * weights list.
         */
        final List<MutablePercent> percentages = new ArrayList<>();
        final int weightSize = weights.size();

        /*
         * Declare and initialize a default percent. Cycle for each element in
         * the weight list.
         */
        final double defaultPercent = Percent.getZero().getValue();
        for (i = 0; i < weightSize; ++i) {

            /*
             * Insert a percentage element corresponding to the first/next
             * weight element using the default percentage.
             */
            percentages.add(new MutablePercent(defaultPercent));
        }

        /*
         * Reinitialize the first percentage to one hundred. Declare and
         * initialize a reallocator.
         */
        percentages.get(0).set(Percent.getOneHundred().getValue());
        final Reallocator reallocator = new Reallocator(weights);

        // Try to reallocate the percentages.
        try {
            reallocator.reallocate(percentages);
        }

        /*
         * Catch any illegal argument exception that may occur, and
         * reinitialize the first percentage to the default.
         */
        catch (@NotNull IllegalArgumentException exception) {
            percentages.get(0).set(defaultPercent);
        }

        /*
         * Create a map of weights to percentages. Cycle for each highest level
         * weight type.
         */
        final Map<WeightType, MutablePercent> map = new HashMap<>();
        final int typesLength = types.length;
        for (i = 0; i < typesLength; ++i) {

            /*
             * Put the first/next weight type and its corresponding percentage
             * in the map.
             */
            map.put(types[i], percentages.get(i));
        }

        // Return the map.
        return map;
    }

    /**
     * Gets the valuator.
     *
     * @return The valuator
     */
    private @NotNull Valuator getValuator() {
        return valuator;
    }

    /**
     * Gets the recipient for writer output.
     *
     * @return The recipient for writer output
     */
    private FileWriter getWriter() {
        return writer;
    }

    /**
     * Writes a portfolio description line by a highest-level weight type.
     *
     * @param portfolio The portfolio for which to write a description line
     * @param map       A map of weight types to percentages
     * @param type      The specific weight type to describe
     * @throws IOException Indicates an I/O exception occurred
     */
    private void writePercentage(@NotNull Portfolio portfolio,
                                 @NotNull Map<WeightType, MutablePercent> map,
                                 @NotNull WeightType type) throws IOException {

        // Declare and initialize the format, and write the description line.
        final String format = "%s%s";
        getWriter().write(String.format(getSummaryFormat(),
                String.format(format, type.getSoftName(), ":"),
                getValuator().getValue(portfolio, type),
                String.format(format, map.get(type), "%")));
    }

    /**
     * Writes the portfolio breakdown percentages.
     *
     * @param portfolio The portfolio for which to breakdown percentages
     * @throws IOException Indicates an I/O exception occurred
     */
    private void writePercentages(@NotNull Portfolio portfolio) throws IOException {

        /*
         * Get the percentage breakdown of the portfolio by highest-level
         * weight type.
         */
        final Map<WeightType, MutablePercent> percentages =
                getPercentages(portfolio);

        // Write the percentages for each of the highest-level weight types.
        writePercentage(portfolio, percentages, WeightType.STOCK);
        writePercentage(portfolio, percentages, WeightType.BOND);
        writePercentage(portfolio, percentages, WeightType.CASH);
        writePercentage(portfolio, percentages, WeightType.REAL_ESTATE);
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
        writer.write("Holdings that can be rebalanced:\n\n");

        // Write the institution table followed by a newline.
        writeTable(portfolio);
        writer.write("\n");

        // Write the portfolio breakdown percentages, and return to caller.
        writePercentages(portfolio);
        return true;
    }

    /**
     * Writes a table breakdown of institutions in a portfolio to include
     * taxable holdings, tax deferred holdings, tax paid holdings, and totals.
     *
     * @param portfolio The portfolio for which to describe contained
     *                  institutions
     * @throws IOException Indicates an I/O exception occurred
     */
    private void writeTable(@NotNull Portfolio portfolio) throws IOException {

        // Get the heading and number formats.
        final String headingFormat = getHeadingFormat();
        final String numberFormat = getNumberFormat();

        // Get the file writer, and write the header for the table.
        final FileWriter writer = getWriter();
        writer.write(String.format(headingFormat, "Institution", "Taxable",
                "Tax Deferred", "Tax Paid", "Total"));

        /*
         * Create a delimiter for use between the table header and table data
         * lines, and between the table data lines and table summary.
         */
        final String delimiter = "-".repeat(getFieldLength());
        final String delimiterLine = String.format(headingFormat, delimiter,
                delimiter, delimiter, delimiter, delimiter);

        /*
         * Write the delimiter, as we have already written the table header.
         * Get a sorted list of institutions from the portfolio.
         */
        writer.write(delimiterLine);
        final List<Institution> institutions = getInstitutions(portfolio);

        /*
         * Declare and initialize more local variables used to write the table
         * data lines.
         */
        Institution institution;
        final Valuator valuator = getValuator();
        final double zero = Currency.getZero().getValue();

        /*
         * Get an iterator for the institutions list. Cycle while institutions
         * exist, and while their balanceable value is greater than zero.
         */
        final Iterator<Institution> iterator = institutions.iterator();
        while (iterator.hasNext() && (zero < valuator.getValue(
                institution = iterator.next()).getValue())) {

            // Write a data line for the first/next institution.
            writer.write(String.format(numberFormat, institution.getKey(),
                    valuator.getValue(institution, CategoryType.TAXABLE),
                    valuator.getValue(institution, CategoryType.TAX_DEFERRED),
                    valuator.getValue(institution, CategoryType.TAX_PAID),
                    valuator.getValue(institution)));
        }

        /*
         * Write another delimiter, followed by a summary line for the whole
         * portfolio.
         */
        writer.write(delimiterLine);
        writer.write(String.format(numberFormat, portfolio.getKey(),
                valuator.getValue(portfolio, CategoryType.TAXABLE),
                valuator.getValue(portfolio, CategoryType.TAX_DEFERRED),
                valuator.getValue(portfolio, CategoryType.TAX_PAID),
                valuator.getValue(portfolio)));

        /*
         * Continue iterating and doing nothing with institutions that have
         * zero balanceable value.
         */
        //noinspection StatementWithEmptyBody
        while (iterator.hasNext() && (zero <= valuator.
                getValue(iterator.next()).getValue())) ;

        /*
         * The remainder of the institutions, if any, have negative balances.
         * This seems wrong. Only debts are negative, and debts are not
         * balanceable.
         */
        if (iterator.hasNext()) {

            // Write a newline for starters.
            writer.write("\n");
            do {

                /*
                 * Get the first/next institution (we know there is at least
                 * one at this point), and warn about its negative balance.
                 */
                institution = iterator.next();
                writer.write(String.format("Warning! Institution '%s' has " +
                                "balance of %s!%n", institution.getKey(),
                        valuator.getValue(institution).getValue()));
            } while (iterator.hasNext());
        }
    }
}
