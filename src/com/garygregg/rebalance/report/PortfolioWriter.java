package com.garygregg.rebalance.report;

import com.garygregg.rebalance.CategoryType;
import com.garygregg.rebalance.DateUtilities;
import com.garygregg.rebalance.Library;
import com.garygregg.rebalance.WeightType;
import com.garygregg.rebalance.account.AccountLibrary;
import com.garygregg.rebalance.code.CodeLibrary;
import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.detailed.DetailedLibrary;
import com.garygregg.rebalance.hierarchy.Institution;
import com.garygregg.rebalance.hierarchy.Portfolio;
import com.garygregg.rebalance.hierarchy.Valuator;
import com.garygregg.rebalance.holding.HoldingLibrary;
import com.garygregg.rebalance.portfolio.PortfolioDescription;
import com.garygregg.rebalance.portfolio.PortfolioLibrary;
import com.garygregg.rebalance.ticker.TickerLibrary;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

class PortfolioWriter {

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

    static {

        // Construct the formats.
        final int valueColumns = getValueColumns();
        headingFormat = constructFormat(false, valueColumns);
        numberFormat = constructFormat(true, valueColumns);
        summaryFormat = constructFormat(true, 1);
    }

    // The valuator for 'considered' values ('considered' or proposed)
    private final Valuator valuatorForConsidered;

    // The valuator for 'not considered' values
    private final Valuator valuatorForNotConsidered;

    // The recipient for writer output
    private final FileWriter writer;

    /**
     * Constructs the portfolio writer.
     *
     * @param writer                   The recipient for writer output
     * @param valuatorForConsidered    The valuator for considered values
     *                                 ('considered' or proposed)
     * @param valuatorForNotConsidered The valuator for 'not considered' values
     */
    public PortfolioWriter(@NotNull FileWriter writer,
                           @NotNull Valuator valuatorForConsidered,
                           @NotNull Valuator valuatorForNotConsidered) {

        // Set all the member variables.
        this.writer = writer;
        this.valuatorForConsidered = valuatorForConsidered;
        this.valuatorForNotConsidered = valuatorForNotConsidered;
    }

    /**
     * Checks that the date of a hierarchy - if provided - matches that in the
     * holdings library.
     *
     * @param writer        A write to receive message
     * @param hierarchyDate The date of a hierarchy
     * @throws IOException Indicates an I/O exception occurred
     */
    private static void checkDate(@NotNull FileWriter writer,
                                  Date hierarchyDate) throws IOException {

        // Is the provided hierarchy date not null?
        if (null != hierarchyDate) {

            /*
             * The hierarchy date is not null. Get the date from the holdings
             * library. Is the date from the holdings library not null, and is
             * this non-null date not equal to the provided hierarchy date?
             */
            final Date holdingDate = HoldingLibrary.getInstance().getDate();
            if (!((null == holdingDate) ||
                    holdingDate.equals(hierarchyDate))) {

                /*
                 * The dates are both not null, and do not match. Write a
                 * message to the writer.
                 */
                writer.write(String.format("\nWarning: The date %s of the " +
                                "holdings does not equal that of the " +
                                "hierarchy, %s!\n",
                        DateUtilities.format(holdingDate),
                        DateUtilities.format(hierarchyDate)));
            }
        }
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
        return String.format("%%-%ds", getFieldLength()) +
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
    public static String getSummaryFormat() {
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
     * Writes a date to a file writer.
     *
     * @param writer      The file writer to receive a message
     * @param date        The date to write to the writer
     * @param description A description of the date
     * @throws IOException Indicates an I/O exception occurred
     */
    private static void writeDate(@NotNull FileWriter writer,
                                  @NotNull Date date,
                                  @NotNull String description) throws IOException {

        /*
         * Format the description in a tag of fixed length, then write the
         * tag to the writer along with the date.
         */
        final String tag = String.format("The date of the %s is:", description);
        writer.write(String.format("%-37s %s.\n", tag,
                DateUtilities.format(date)));
    }

    /**
     * Write the date of a library to a file writer.
     *
     * @param writer      The file writer to receive a message
     * @param library     The library from which to extract a date
     * @param description A description of the library
     * @throws IOException Indicates an I/O exception occurred
     */
    private static void writeDate(@NotNull FileWriter writer,
                                  @NotNull Library<?, ?> library,
                                  @NotNull String description) throws IOException {
        writeDate(writer, library.getDate(), String.format("%s %s",
                description, "library"));
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
     * Gets the format for a number line.
     *
     * @return The format for a number line
     */
    private String getNumberFormat() {
        return numberFormat;
    }

    /**
     * Gets the valuator for 'considered' values ('considered' or proposed)
     *
     * @return The valuator for 'considered' values ('considered' or proposed)
     */
    private @NotNull Valuator getValuatorForConsidered() {
        return valuatorForConsidered;
    }

    /**
     * Gets the valuator for 'not considered' values
     *
     * @return The valuator for 'not considered'
     */
    private @NotNull Valuator getValuatorForNotConsidered() {
        return valuatorForNotConsidered;
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
     * Writes dates for each known element reader.
     *
     * @param writer The file writer to receive the dates
     * @throws IOException Indicates an I/O exception occurred
     */
    private void writeDates(@NotNull FileWriter writer) throws IOException {

        // Write the dates of each element reader.
        writeDate(writer, HoldingLibrary.getInstance(), "holding");
        writeDate(writer, AccountLibrary.getInstance(), "account");
        writeDate(writer, CodeLibrary.getInstance(), "code");
        writeDate(writer, DetailedLibrary.getInstance(), "detailed");
        writeDate(writer, PortfolioLibrary.getInstance(), "portfolio");
        writeDate(writer, TickerLibrary.getInstance(), "ticker");
    }

    /**
     * Writes a portfolio summary.
     *
     * @param portfolio A portfolio that is the subject of the summary
     * @param date      The date to use for the summary
     * @return True if the summary was successfully written; false otherwise
     * @throws IOException Indicates an I/O exception occurred
     */
    public boolean writeSummary(@NotNull Portfolio portfolio,
                                Date date) throws IOException {

        final PortfolioDescription description = portfolio.getDescription();
        final String name = (null == description) ? "<name not available>" :
                description.getName();

        final FileWriter writer = getWriter();
        writer.write(String.format("Portfolio summary for: %s%n", name));

        final String newLine = "\n";
        writer.write(newLine);

        writeDate(writer, date, "hierarchy");
        writeDates(writer);

        checkDate(writer, date);
        writer.write(newLine);

        final String headingFormat = getHeadingFormat();
        final String numberFormat = getNumberFormat();
        final String summaryFormat = getSummaryFormat();

        writer.write(String.format(headingFormat, "Institution", "Taxable",
                "Tax Deferred", "Tax Paid", "Total"));

        final String delimiter = "-".repeat(getFieldLength());
        final String delimiterLine = String.format(headingFormat, delimiter,
                delimiter, delimiter, delimiter, delimiter);

        writer.write(delimiterLine);
        final List<Institution> institutions = new ArrayList<>(portfolio.getChildren());
        institutions.sort(new Comparator<>() {

            // We use 'considered' values for this sort.
            final Valuator valuator = getValuatorForConsidered();

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

        Institution institution;
        final Valuator valuator = getValuatorForConsidered();
        final double zero = Currency.getZero().getValue();

        final Iterator<Institution> iterator = institutions.iterator();
        while (iterator.hasNext() && (zero < valuator.getValue(
                institution = iterator.next()).getValue())) {

            writer.write(String.format(numberFormat, institution.getKey(),
                    valuator.getValue(institution, CategoryType.TAXABLE),
                    valuator.getValue(institution, CategoryType.TAX_DEFERRED),
                    valuator.getValue(institution, CategoryType.TAX_PAID),
                    valuator.getValue(institution)));
        }

        writer.write(delimiterLine);
        writer.write(String.format(numberFormat, portfolio.getKey(),
                valuator.getValue(portfolio, CategoryType.TAXABLE),
                valuator.getValue(portfolio, CategoryType.TAX_DEFERRED),
                valuator.getValue(portfolio, CategoryType.TAX_PAID),
                valuator.getValue(portfolio)));

        writer.write(newLine);
        WeightType type;

        final String separator = ":";
        final String format = "%s%s";

        writer.write(String.format(summaryFormat,
                String.format(format, type = WeightType.STOCK, separator),
                valuator.getValue(portfolio, type)));

        writer.write(String.format(summaryFormat,
                String.format(format, type = WeightType.BOND, separator),
                valuator.getValue(portfolio, type)));

        writer.write(String.format(summaryFormat,
                String.format(format, type = WeightType.CASH, separator),
                valuator.getValue(portfolio, type)));

        writer.write(String.format(summaryFormat,
                String.format(format, type = WeightType.REAL_ESTATE,
                        separator), valuator.getValue(portfolio, type)));

        // TODO:
        return true;
    }
}
