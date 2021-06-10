package com.garygregg.rebalance.report;

import com.garygregg.rebalance.ElementProcessor;
import com.garygregg.rebalance.hierarchy.Hierarchy;
import com.garygregg.rebalance.hierarchy.Portfolio;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.Logger;

public class ReportsBuilder extends ElementProcessor {

    {

        // Assign the logger based on class canonical name.
        setLogger(Logger.getLogger(ReportsBuilder.class.getCanonicalName()));
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

    @Override
    protected String getFileType() {
        return "txt";
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
         * Delete the file if it is a directory, and create a writer to a new
         * file.
         */
        deleteDirectory(file);
        return new FileWriter(file);
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
        final FileWriter writer = getWriter(
                getDateUtilities().getTypeDirectory(),
                portfolio.getKey(), date);

        // TODO: Write the report.
        return false;
    }
}
