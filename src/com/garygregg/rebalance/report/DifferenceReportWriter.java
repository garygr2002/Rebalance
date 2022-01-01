package com.garygregg.rebalance.report;

import com.garygregg.rebalance.AccountKey;
import com.garygregg.rebalance.Description;
import com.garygregg.rebalance.Library;
import com.garygregg.rebalance.account.AccountLibrary;
import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.countable.MutableCurrency;
import com.garygregg.rebalance.hierarchy.*;
import com.garygregg.rebalance.portfolio.PortfolioLibrary;
import com.garygregg.rebalance.ticker.*;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DifferenceReportWriter extends ReportWriter {

    // A map of ticker description classes to their corresponding line codes
    private static final Map<Class<? extends TickerDescription>,
            Character> classMap = new HashMap<>();

    // The default line code
    private static final Character defaultLineCode = ' ';

    // The field separator
    private static final String fieldSeparator = ",";

    // The first account line code
    private static final Character firstAccountLineCode;

    // The first institution line code
    private static final Character firstInstitutionLineCode;

    // The first portfolio line code
    private static final Character firstPortfolioLineCode;

    // The format for each report line
    private static final String format =
            "%-1s%s%-17s%-43s%18s%s%18s%s%18s\n";

    // The value of zero currency
    private static final Currency zero = Currency.getZero();

    static {

        // Add ETF and fund codes to the ticker description class map.
        classMap.put(ETFDescription.class, TickerLibrary.getETFCode());
        classMap.put(FundDescription.class, TickerLibrary.getFundCode());

        /*
         * Add not-considered and stock fund codes to the ticker description
         * class map.
         */
        classMap.put(NotConsideredDescription.class,
                TickerLibrary.getNotConsideredCode());
        classMap.put(StockDescription.class, TickerLibrary.getStockCode());

        // Get the first line codes for accounts and institutions.
        firstAccountLineCode = getFirstLineCode(AccountLibrary.getInstance());
        firstInstitutionLineCode = 'I';

        // Get the first line code for portfolios.
        firstPortfolioLineCode = getFirstLineCode(
                PortfolioLibrary.getInstance());
    }

    /*
     * A mutable currency for calculating the difference between proposed and
     * considered values
     */
    private final MutableCurrency difference = new MutableCurrency();

    /**
     * Constructs the difference report writer.
     */
    public DifferenceReportWriter() {
        super(ValueByConsidered.getInstance());
    }

    /**
     * Formats a name.
     *
     * @param name The given name
     * @return The formatted name
     */
    private static @NotNull String formatName(@NotNull String name) {
        return formatWithSeparator(name);
    }

    /**
     * Formats a non-ticker key.
     *
     * @param key The given non-ticker key
     * @return The formatted non-ticker key
     */
    private static @NotNull String formatNonTickerKey(@NotNull String key) {
        return formatWithSeparator(key);
    }

    /**
     * Formats a ticker key.
     *
     * @param key The given ticker key
     * @return The formatted ticker key
     */
    private static @NotNull String formatTickerKey(@NotNull String key) {
        return String.format("%-5s%s", key, fieldSeparator);
    }

    /**
     * Formats a field with a separator immediately appended.
     *
     * @param field The given field
     * @return The formatted field with a separator immediately appended
     */
    private static @NotNull String formatWithSeparator(@NotNull String field) {
        return String.format("%s%s", field, fieldSeparator);
    }

    /**
     * Gets the first line code for a library.
     *
     * @param library The given library
     * @return The first line code for the given library
     */
    private static @NotNull Character getFirstLineCode(
            @NotNull Library<?, ?> library) {

        /*
         * Get the line codes from the library. Declare and initialize the
         * index of the first line code.
         */
        final Character[] lineCodes = library.getLineCodes();
        final int firstLineCode = 0;

        /*
         * Return the first line code if the line codes array has one or more
         * codes. Otherwise, return a default line code.
         */
        return (firstLineCode < lineCodes.length) ? lineCodes[firstLineCode] :
                defaultLineCode;
    }

    /**
     * Gets the line code for a ticker.
     *
     * @param ticker A ticker
     * @return The line code for the ticker
     */
    private static @NotNull Character getFirstLineCode(
            @NotNull Ticker ticker) {

        /*
         * Get the description from the ticker and the class of the
         * description. Return the line code corresponding to the description.
         */
        final TickerDescription description = ticker.getDescription();
        final Class<? extends TickerDescription> descriptionClass =
                (null == description) ? null : description.getClass();
        return classMap.get(descriptionClass);
    }

    /**
     * Gets the name from a description.
     *
     * @param description A description
     * @return The name from the description, or a default if the description
     * is null
     */
    private static String getName(Description<?> description) {
        return (null == description) ? null : description.getName();
    }

    /**
     * Interprets currency.
     *
     * @param currency A given currency value
     * @return A non-null interpretation of the currency value
     */
    private static @NotNull Currency interpretCurrency(Currency currency) {
        return (null == currency) ? zero : currency;
    }

    @Override
    protected @NotNull String getPrefix() {
        return "difference";
    }

    /**
     * Writes a report line.
     *
     * @param writer     The file writer to receive the line
     * @param lineCode   The line code
     * @param key        The key for the line
     * @param name       The name of the line
     * @param proposed   The proposed value of the corresponding holding
     * @param considered The considered value of the corresponding holding
     * @throws IOException Indicates an I/O exception occurred
     */
    private void writeLine(@NotNull FileWriter writer,
                           @NotNull Character lineCode,
                           @NotNull String key, String name,
                           Currency proposed, Currency considered)
            throws IOException {

        /*
         * Reinterpret the given considered and proposed values. Is either the
         * considered value or the proposed value something other than zero?
         */
        considered = interpretCurrency(considered);
        proposed = interpretCurrency(proposed);
        if (considered.isNotZero() || proposed.isNotZero()) {

            /*
             * The considered value or the proposed value is something other
             * than zero. Determine the difference between the considered and
             * proposed values.
             */
            difference.set(proposed);
            difference.subtract(considered);

            // Write the line.
            writer.write(String.format(format, lineCode, fieldSeparator, key,
                    (name == null) ? "<name missing>" : name, proposed,
                    fieldSeparator, considered, fieldSeparator, difference));
        }
    }

    /**
     * Writes lines for an account.
     *
     * @param writer  The file writer to receive the lines
     * @param account The account for which to write lines
     * @throws IOException Indicates an I/O exception occurred
     */
    private void writeLines(@NotNull FileWriter writer,
                            @NotNull Account account)
            throws IOException {

        /*
         * Get the account number from the account key. Write about the given
         * account.
         */
        final Long accountNumber = account.getKey().getSecond();
        writeLine(writer, firstAccountLineCode,
                formatNonTickerKey((null == accountNumber) ? "" :
                        AccountKey.format(accountNumber)),
                formatName(getName(account.getDescription())),
                account.getProposed(), account.getConsidered());

        // Cycle for each ticker.
        for (Ticker ticker : account.getChildren()) {

            // Write lines for the first/next ticker.
            writeLine(writer, getFirstLineCode(ticker),
                    formatTickerKey(ticker.getKey()),
                    formatName(getName(ticker.getDescription())),
                    ticker.getProposed(), ticker.getConsidered());
        }
    }

    /**
     * Writes lines for an institution.
     *
     * @param writer      The file writer to receive the lines
     * @param institution The institution for which to write lines
     * @throws IOException Indicates an I/O exception occurred
     */
    private void writeLines(@NotNull FileWriter writer,
                            @NotNull Institution institution)
            throws IOException {

        // Write about the given institution.
        writeLine(writer, firstInstitutionLineCode,
                formatNonTickerKey(institution.getKey()),
                formatName(institution.getName()), institution.getProposed(),
                institution.getConsidered());

        // Cycle for each account.
        for (Account account : institution.getChildren()) {

            // Write lines for the first/next account.
            writeLines(writer, account);
        }
    }

    @Override
    public boolean writeLines(@NotNull Portfolio portfolio, Date date)
            throws IOException {

        /*
         * Create a file writer tailored to the portfolio key and the given
         * date.
         */
        final String key = portfolio.getKey();
        final FileWriter fileWriter = getWriter(
                getDateUtilities().getTypeDirectory(), key, date);

        // Write about the given portfolio.
        writeLine(fileWriter, firstPortfolioLineCode,
                formatNonTickerKey(key),
                formatName(getName(portfolio.getDescription())),
                portfolio.getProposed(), portfolio.getConsidered());

        // Cycle for each institution.
        for (Institution institution : portfolio.getChildren()) {

            // Write lines for the first/next institution.
            writeLines(fileWriter, institution);
        }

        // Close the file writer and return success to our caller.
        fileWriter.close();
        return true;
    }
}
