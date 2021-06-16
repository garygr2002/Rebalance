package com.garygregg.rebalance.report;

import com.garygregg.rebalance.ElementProcessor;
import com.garygregg.rebalance.hierarchy.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.Logger;

class ReportsBuilder extends ElementProcessor {

    // The valuator for 'not considered' values
    private final Valuator valuatorForNotConsidered =
            ValueByNotConsidered.getInstance();

    // The valuator for considered values ('considered' or proposed)
    private Valuator valuatorForConsidered;

    {

        // Assign the logger based on class canonical name.
        setLogger(Logger.getLogger(ReportsBuilder.class.getCanonicalName()));
    }

    /**
     * Constructs the reports builder.
     *
     * @param valuatorForConsidered The valuator to be used for writing reports
     */
    ReportsBuilder(@NotNull Valuator valuatorForConsidered) {
        setConsidered(valuatorForConsidered);
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
     * Gets the valuator for 'considered' values ('considered' or proposed)
     *
     * @return The valuator for 'considered' values ('considered' or proposed)
     */
    public @NotNull Valuator getConsidered() {
        return valuatorForConsidered;
    }

    @Override
    protected String getFileType() {
        return "txt";
    }

    /**
     * Gets the valuator for 'not considered' values
     *
     * @return The valuator for 'not considered' values
     */
    public @NotNull Valuator getNotConsidered() {
        return valuatorForNotConsidered;
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
     * Sets the valuator for considered values ('considered' or proposed)
     *
     * @param valuator The new valuator for considered values ('considered' or
     *                 proposed)
     */
    void setConsidered(@NotNull Valuator valuator) {
        this.valuatorForConsidered = valuator;
    }

    /**
     * Writes reports for each portfolio in a hierarchy instance provided by
     * the portfolio class.
     *
     * @param date The date to use for each report
     * @return True if each report was successfully written; false otherwise
     * @throws IOException Indicates an I/O exception occurred
     */
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
         * Create a portfolio writer with the file writer and our hierarchy
         * object valuators. Write a summary of the portfolio using the
         * portfolio writer, receiving a result.
         */
        final PortfolioWriter portfolioWriter = new PortfolioWriter(fileWriter,
                getConsidered(), getNotConsidered());
        final boolean result = portfolioWriter.writeSummary(portfolio, date);

        // Close the file writer and return status from the portfolio writer.
        fileWriter.close();
        return result;
    }
}
