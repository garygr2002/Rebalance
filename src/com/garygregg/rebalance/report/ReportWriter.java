package com.garygregg.rebalance.report;

import com.garygregg.rebalance.*;
import com.garygregg.rebalance.account.AccountLibrary;
import com.garygregg.rebalance.code.CodeLibrary;
import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.countable.MutableCurrency;
import com.garygregg.rebalance.countable.Percent;
import com.garygregg.rebalance.detailed.DetailedLibrary;
import com.garygregg.rebalance.hierarchy.*;
import com.garygregg.rebalance.holding.HoldingLibrary;
import com.garygregg.rebalance.portfolio.PortfolioDescription;
import com.garygregg.rebalance.portfolio.PortfolioLibrary;
import com.garygregg.rebalance.ticker.TickerLibrary;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.Logger;

abstract class ReportWriter extends ElementProcessor {

    // The valuator for not balanceable assets
    private final Valuator notBalanceable = ValueByNotConsidered.getInstance();

    // The valuator for balanceable assets ('considered' or proposed)
    private Valuator balanceable;

    // The current holdings
    private HoldingLibrary currentHoldings;

    {

        // Assign the logger based on class canonical name.
        setLogger(Logger.getLogger(ReportWriter.class.getCanonicalName()));
    }

    /**
     * Constructs the report writer with an explicit valuator for balanceable
     * assets.
     *
     * @param balanceable The valuator to for balanceable assets
     */
    ReportWriter(@NotNull Valuator balanceable) {
        setBalanceable(balanceable);
    }

    /**
     * Constructs the report writer with a default valuator for balanceable
     * assets.
     */
    ReportWriter() {
        this(ValueByConsidered.getInstance());
    }

    /**
     * Checks that the date of a hierarchy - if provided - matches that in the
     * given holdings.
     *
     * @param writer        A writer to receive messages
     * @param holdings      Holdings
     * @param hierarchyDate The date of a hierarchy
     * @throws IOException Indicates an I/O exception occurred
     */
    private static void checkDate(@NotNull FileWriter writer,
                                  HoldingLibrary holdings,
                                  Date hierarchyDate) throws IOException {

        // Is the provided hierarchy date not null?
        if (null != hierarchyDate) {

            // The hierarchy date is not null. Get the date from the holdings.
            final Date holdingDate = (null == holdings) ? null :
                    holdings.getDate();

            /*
             * Is the date from the holdings not null, and is this non-null
             * date not equal to the provided hierarchy date?
             */
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

    /***
     * Creates a path to a file.
     * @param file The file for which to create a path.
     * @throws IOException Indicates the path could not be created
     */
    private static void createPath(@NotNull File file)
            throws IOException {

        /*
         * Get the parent directory. Try to create the parent and all paths to
         * the parent if the parent does not exist.
         */
        final File parent = file.getParentFile();
        if (!(parent.exists() || parent.mkdirs())) {

            // Throw an I/O exception if any of this could not be accomplished.
            throw new IOException(String.format("Could not create required " +
                    "directory '%s'", parent));
        }
    }

    /**
     * Recursively deletes a directory.
     *
     * @param directory The directory to delete
     * @return True if the directory was successfully deleted; false otherwise
     */
    @SuppressWarnings("UnusedReturnValue")
    private static boolean deleteDirectory(@NotNull File directory) {

        /*
         * Get the contents of the directory, if any. The result will be null
         * if the given file object is not a directory. Is the file object a
         * directory?
         */
        final File[] files = directory.listFiles();
        if (null != files) {

            /*
             * The file object is a directory. Cycle for each file in the
             * directory, assume the file itself is a directory, and delete it.
             */
            for (File file : files) {
                deleteDirectory(file);
            }
        }

        // Delete the directory.
        return directory.delete();
    }

    /**
     * Gets the name from a portfolio.
     *
     * @param portfolio A portfolio from which to get a name
     * @return The name of the portfolio, or a default if none is available
     */
    private static @NotNull String getName(@NotNull Portfolio portfolio) {

        /*
         * Get the description of the portfolio, and the name of the portfolio
         * from its description. Use the key of the portfolio if its
         * description is null.
         */
        final PortfolioDescription description = portfolio.getDescription();
        return (null == description) ? portfolio.getKey() :
                description.getName();
    }

    /**
     * Returns a non-null date, either the argument to the method or a default
     * if the argument is null.
     *
     * @param date        Any date
     * @param defaultDate A non-null default date to use
     * @return The argument, or a default if the date is null
     */
    private static @NotNull Date getNonNullDate(Date date,
                                                @NotNull Date defaultDate) {
        return (null == date) ? defaultDate : date;
    }

    /**
     * Returns a non-null date, either the argument to the method or a default
     * if the argument is null.
     *
     * @param date Any date
     * @return The argument, or a default if the date is null
     */
    private static @NotNull Date getNonNullDate(Date date) {
        return getNonNullDate(date, new Date());
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
                                  @NotNull String description)
            throws IOException {

        // Format the description in a tag of fixed length.
        final String tag = String.format("The date of the %s is:",
                description);

        // Write the tag to the writer along with the date.
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
                                  @NotNull String description)
            throws IOException {
        writeDate(writer, library.getDate(), String.format("%s %s",
                description, "library"));
    }

    /**
     * Writes dates for each known element reader.
     *
     * @param writer The file writer to receive the dates
     * @throws IOException Indicates an I/O exception occurred
     */
    private static void writeDates(@NotNull FileWriter writer)
            throws IOException {

        // Write the date of the valuation library.
        writeDate(writer, HoldingLibrary.getInstance(HoldingType.VALUATION),
                "holding");

        // Write the date of the basis library.
        writeDate(writer, HoldingLibrary.getInstance(HoldingType.BASIS),
                "basis");

        // Write the dates of the code library, the portfolio library.
        writeDate(writer, CodeLibrary.getInstance(), "code");
        writeDate(writer, PortfolioLibrary.getInstance(), "portfolio");

        /*
         * Write the dates of the account library, the detailed library, and
         * the ticker library.
         */
        writeDate(writer, AccountLibrary.getInstance(), "account");
        writeDate(writer, DetailedLibrary.getInstance(), "detailed");
        writeDate(writer, TickerLibrary.getInstance(), "ticker");
    }

    /**
     * Writes a portfolio-specific description to a file writer.
     *
     * @param writer    The file writer to receive the description
     * @param portfolio The portfolio to use
     * @throws IOException Indicates an I/O exception occurred
     */
    private static void writeDescription(@NotNull Writer writer,
                                         @NotNull Portfolio portfolio)
            throws IOException {

        /*
         * Declare and initialize investor birthdate and projected mortality
         * date.
         */
        Date birthdate = null;
        Date mortalityDate = null;

        /*
         * Declare and initialize CPI-adjusted, and non-CPI-adjusted monthly
         * income.
         */
        Currency cpiMonthly = null;
        Currency nonCpiMonthly = null;

        /*
         * Declare and initialize Social Security monthly and taxable annual
         * income.
         */
        Currency socialSecurityMonthly = null;
        Currency taxableAnnual = null;

        // Get the expected inflation rate.
        final Double inflation =
                PreferenceManager.getInstance().getInflation();

        // Get the description from the portfolio. Is the description not null?
        final PortfolioDescription description = portfolio.getDescription();
        if (null != description) {

            /*
             * The portfolio description is not null. Get the birthdate and
             * projected mortality date.
             */
            birthdate = description.getBirthdate();
            mortalityDate = description.getMortalityDate();

            // Get the CPI-adjusted and non-CPI-adjusted monthly income.
            cpiMonthly = description.getCpiMonthly();
            nonCpiMonthly = description.getNonCpiMonthly();

            // Get the Social Security monthly and taxable annual income.
            socialSecurityMonthly = description.getSocialSecurityMonthly();
            taxableAnnual = description.getTaxableAnnual();
        }

        // Declare needed string.
        final String format = "%-37s %s.\n";
        final String unavailable = "unavailable";

        // Format and write the birthdate message.
        writer.write(String.format(format, "Investor birthday is:",
                (null == birthdate) ? unavailable :
                        DateUtilities.format(birthdate)));

        // Format and write the projected mortality date message.
        writer.write(String.format(format, "Investor projected mortality " +
                "date is:", (null == mortalityDate) ?
                unavailable : DateUtilities.format(mortalityDate)));

        // Format and write the CPI-adjusted monthly income.
        writer.write(String.format(format, "CPI adjusted monthly income is:",
                (null == cpiMonthly) ? unavailable : cpiMonthly));

        // Format and write the non-CPI adjusted monthly income.
        writer.write(String.format(format, "Non-CPI adjusted monthly " +
                "income is:", (null == nonCpiMonthly) ?
                unavailable : nonCpiMonthly));

        // Format and write the Social Security monthly income.
        writer.write(String.format(format, "Social Security monthly income " +
                "is:", (null == socialSecurityMonthly) ? unavailable :
                socialSecurityMonthly));

        // Format and write the taxable annual income.
        writer.write(String.format(format, "Taxable annual income is:",
                (null == taxableAnnual) ? unavailable : taxableAnnual));

        /*
         * Format and write the expected annual rate of inflation. Finish by
         * writing a newline.
         */
        writer.write(String.format(format, "Expected annual rate of " +
                "inflation is:", (null == inflation) ?
                unavailable :
                String.format("%s%%", Percent.format(inflation))));
        writer.write("\n");
    }

    /**
     * Gets the valuator for balanceable assets ('considered' or proposed)
     *
     * @return The valuator for balanceable assets ('considered' or proposed)
     */
    public @NotNull Valuator getBalanceable() {
        return balanceable;
    }

    /**
     * Gets the current holdings.
     *
     * @return The current holdings
     */
    private HoldingLibrary getCurrentHoldings() {
        return currentHoldings;
    }

    @Override
    protected String getFileType() {
        return "txt";
    }

    /**
     * Gets the valuator for not balanceable assets
     *
     * @return The valuator for not balanceable assets
     */
    public @NotNull Valuator getNotBalanceable() {
        return notBalanceable;
    }

    /**
     * Gets a file writer given the directory, a portfolio key, and a date;
     * destroys any directory with the same name!
     *
     * @param directory The directory to contain the file writer
     * @param key       The portfolio key (used to create a subdirectory)
     * @param date      A date (used to create a file name)
     * @return A file writer conforming to the parameters
     * @throws IOException If the file cannot be opened for any reason
     */
    protected @NotNull FileWriter getWriter(@NotNull File directory,
                                            @NotNull String key,
                                            Date date) throws IOException {

        // Create a file representative of the given parameters.
        final File file = new File(Paths.get(directory.getPath(), key,
                getDateUtilities().constructFilename(
                        getNonNullDate(date))).toString());

        /*
         * Create a path to the file. Delete the file if it is a directory,
         * and create a writer to a new file.
         */
        createPath(file);
        deleteDirectory(file);
        return new FileWriter(file);
    }

    /**
     * Sets the valuator for balanceable assets ('considered' or proposed)
     *
     * @param valuator The new valuator for balanceable assets ('considered' or
     *                 proposed)
     */
    void setBalanceable(@NotNull Valuator valuator) {
        balanceable = valuator;
    }

    /**
     * Sets the current holdings.
     *
     * @param currentHoldings The current holdings
     */
    private void setCurrentHoldings(HoldingLibrary currentHoldings) {
        this.currentHoldings = currentHoldings;
    }

    /**
     * Writes portfolio information between the balanceable and unbalanceable
     * sections of the report.
     *
     * @param writer    The recipient for writer output
     * @param portfolio A portfolio
     * @return True if the information was successfully written; false
     * otherwise
     * @throws IOException Indicates an I/O exception occurred
     */
    protected boolean writeBetween(@NotNull FileWriter writer,
                                   @NotNull Portfolio portfolio)
            throws IOException {

        // The default is to do nothing.
        return true;
    }

    /**
     * Writes a portfolio-specific header to a file writer.
     *
     * @param writer    The file writer to receive the header
     * @param portfolio The portfolio to use
     * @param date      The date to use
     * @throws IOException Indicates an I/O exception occurred
     */
    private void writeHeader(@NotNull FileWriter writer,
                             @NotNull Portfolio portfolio,
                             Date date) throws IOException {

        // Describe the portfolio by name.
        writer.write(String.format("Portfolio summary for: %s%n%n",
                getName(portfolio)));

        /*
         * Describe the date of the hierarchy, followed by the dates of the
         * libraries.
         */
        writeDate(writer, date, "hierarchy");
        writeDates(writer);

        /*
         * Finally, check that the date of the hierarchy matches the date of
         * the holdings.
         */
        checkDate(writer, getCurrentHoldings(), date);
        writer.write("\n");
    }

    /**
     * Writes reports for each portfolio in a hierarchy instance provided by
     * the portfolio class.
     *
     * @param date The date to use for each report
     * @return True if each report was successfully written; false otherwise
     * @throws IOException Indicates an I/O exception occurred
     */
    @SuppressWarnings("unused")
    public boolean writeLines(Date date) throws IOException {
        return writeLines(Hierarchy.getInstance(), date);
    }

    /**
     * Writes a report about each portfolio in a hierarchy.
     *
     * @param hierarchy The hierarchy to use as a source of portfolios
     * @param date      The date to use for each report
     * @return True if each report was successfully written; false otherwise
     * @throws IOException Indicates an I/O exception occurred
     */
    public boolean writeLines(@NotNull Hierarchy hierarchy, Date date)
            throws IOException {

        // Set the current holdings from the given hierarchy.
        setCurrentHoldings(HoldingLibrary.getInstance(
                hierarchy.getHoldingType()));

        /*
         * Get a non-null date to use, preferring the argument first, the
         * date in the hierarchy second.
         */
        final Date dateToUse = getNonNullDate(date,
                getNonNullDate(hierarchy.getDate()));

        /*
         * Declare and initialize the result, and cycle for each portfolio in
         * the hierarchy.
         */
        boolean result = true;
        for (Portfolio portfolio : hierarchy.getPortfolios()) {

            /*
             * Write a report for the first/next hierarchy. All portfolios must
             * be written successfully in order to return 'true'.
             */
            result = writeLines(portfolio, dateToUse) && result;
        }

        // Clear the current holdings. Return the result.
        setCurrentHoldings(null);
        return result;
    }

    /**
     * Writes a report about a portfolio.
     *
     * @param portfolio The portfolio about which to write a report
     * @param date      The date to use for the report
     * @return True if the report was successfully written; false otherwise
     * @throws IOException Indicates an I/O exception occurred
     */
    public boolean writeLines(@NotNull Portfolio portfolio,
                              Date date)
            throws IOException {

        /*
         * Create a file writer tailored to the portfolio key and the given
         * date.
         */
        final FileWriter fileWriter = getWriter(
                getDateUtilities().getTypeDirectory(),
                portfolio.getKey(), date);

        /*
         * Write a portfolio header followed by a portfolio description. Use
         * the file writer for this purpose.
         */
        writeHeader(fileWriter, portfolio, date);
        writeDescription(fileWriter, portfolio);

        /*
         * Create a balance-able writer with the file writer, and the valuator
         * for balance-able assets.
         */
        final BalanceableWriter balanceableWriter =
                new BalanceableWriter(fileWriter, getBalanceable());

        /*
         * Write a summary of the portfolio using the balance-able writer,
         * receiving a result. Write any information that should appear between
         * the balance-able and un-balanceable portions of the report. Receive
         * a result, and-ing it with the previous result.
         */
        boolean result = balanceableWriter.writeSummary(portfolio);
        result = writeBetween(fileWriter, portfolio) && result;

        /*
         * Create an unbalance-able writer with the file writer. For this
         * writer, the valuator is well known and does not need to be supplied.
         * Write a newline.
         */
        final UnbalanceableWriter unbalanceableWriter =
                new UnbalanceableWriter(fileWriter);
        fileWriter.write("\n");

        /*
         * Write a summary of the portfolio using the un-balanceable writer.
         * Receive a result, and-ing it with the previous result.
         */
        result = unbalanceableWriter.writeSummary(portfolio) && result;
        writeTrailer(fileWriter, portfolio);

        // Close the file writer and return the result to our caller.
        fileWriter.close();
        return result;
    }

    /**
     * Writes a portfolio-specific trailer to a file writer.
     *
     * @param writer    The file writer to receive the header
     * @param portfolio The portfolio to use
     * @throws IOException Indicates an I/O exception occurred
     */
    private void writeTrailer(@NotNull FileWriter writer,
                              @NotNull Portfolio portfolio)
            throws IOException {

        /*
         * Calculate the total value of the portfolio. Use all weight types
         * for the balanceable portion.
         */
        final MutableCurrency total = new MutableCurrency();
        total.add(getBalanceable().getValue(portfolio, WeightType.ALL));
        total.add(getNotBalanceable().getValue(portfolio));

        // Report the value just calculated.
        writer.write(String.format("%nTotal portfolio value for %s is: %s.%n",
                getName(portfolio), total));
    }
}
