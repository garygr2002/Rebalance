package com.garygregg.rebalance.report;

import com.garygregg.rebalance.DateUtilities;
import com.garygregg.rebalance.ElementProcessor;
import com.garygregg.rebalance.Library;
import com.garygregg.rebalance.account.AccountLibrary;
import com.garygregg.rebalance.code.CodeLibrary;
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
import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.Logger;

class ReportsBuilder extends ElementProcessor {

    // The valuator for not balanceable assets
    private final Valuator notBalanceable = ValueByNotConsidered.getInstance();

    // The valuator for balanceable assets ('considered' or proposed)
    private Valuator balanceable;

    {

        // Assign the logger based on class canonical name.
        setLogger(Logger.getLogger(ReportsBuilder.class.getCanonicalName()));
    }

    /**
     * Constructs the reports builder.
     *
     * @param balanceable The valuator to for balanceable assets
     */
    ReportsBuilder(@NotNull Valuator balanceable) {
        setBalanceable(balanceable);
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
     * Gets the valuator for balanceable assets ('considered' or proposed)
     *
     * @return The valuator for balanceable assets ('considered' or proposed)
     */
    public @NotNull Valuator getBalanceable() {
        return balanceable;
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

    @Override
    protected @NotNull String getPrefix() {
        return "report";
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
    private @NotNull FileWriter getWriter(@NotNull File directory,
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
        this.balanceable = valuator;
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

        /*
         * Get the description of the portfolio, and the name of the portfolio
         * from its description.
         */
        final PortfolioDescription description = portfolio.getDescription();
        final String name = (null == description) ? "<name not available>" :
                description.getName();

        /*
         * Describe the portfolio by name. Describe the date of the hierarchy,
         * followed by the dates of the libraries.
         */
        writer.write(String.format("Portfolio summary for: %s%n%n", name));
        writeDate(writer, date, "hierarchy");
        writeDates(writer);

        /*
         * Finally, check that the date of the hierarchy matches the date of
         * the holdings.
         */
        checkDate(writer, date);
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

        // Return the result.
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
         * Write a portfolio header using the file writer. Create a
         * balanceable writer with the file writer and the valuator for
         * balanceable assets.
         */
        writeHeader(fileWriter, portfolio, date);
        final BalanceableWriter balanceableWriter =
                new BalanceableWriter(fileWriter, getBalanceable());

        /*
         * Write a summary of the portfolio using the balanceable writer,
         * receiving a result. Close the file writer and return status received
         * from the balanceable writer.
         *
         * TODO:
         *
         * Create a not-balanceable writer, and write its own summary.
         */
        final boolean result = balanceableWriter.writeSummary(portfolio);
        fileWriter.close();
        return result;
    }
}
