package com.garygregg.rebalance;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateUtilities {

    // The data directory
    private static final File dataDirectory =
            PreferenceManager.getInstance().getSource().toFile();

    // The template for the parse format
    private static final String formatTemplate = "yyyyMMdd";

    // The regular expression for dates
    private static final String dateRegex = String.format("\\d{%d}",
            formatTemplate.length());

    // The date format used for parsing
    private static final DateFormat dateFormat =
            new SimpleDateFormat(formatTemplate);

    // The date format used for messages
    private static final DateFormat messageFormat =
            new SimpleDateFormat("yyyy-MM-dd");

    // A pattern for the date regular expression
    private static final Pattern pattern = Pattern.compile(getDateRegex());

    // The file type to be used
    private final String fileType;

    // The prefix of to be used
    private final String prefix;

    // The subdirectory of the data directory specific to this processor type
    private final File typeDirectory;

    /**
     * Constructs a date utilities object.
     *
     * @param prefix   The prefix to be used
     * @param fileType The file type to be used
     */
    public DateUtilities(@NotNull String prefix, @NotNull String fileType) {

        // Set the member variables.
        this.fileType = fileType;
        this.prefix = prefix;
        this.typeDirectory = new File(getDataDirectory(), getPrefix());
    }

    /**
     * Constructs a file name.
     *
     * @param prefix   The prefix of the file name
     * @param suffix   The suffix of the file name
     * @param fileType The file type
     * @return A file name
     */
    private static @NotNull String constructFilename(@NotNull String prefix,
                                                     @NotNull String suffix,
                                                     @NotNull String fileType) {
        return prefix + getSeparator() + suffix +
                getExtensionSeparator() + fileType;
    }

    /**
     * Constructs a file name.
     *
     * @param prefix   The prefix of the file name
     * @param date     The date of the file
     * @param fileType The file type
     * @return A file name
     */
    private static @NotNull String constructFilename(@NotNull String prefix,
                                                     @NotNull Date date,
                                                     @NotNull String fileType) {
        return constructFilename(prefix,
                DateUtilities.getDateFormat().format(date), fileType);
    }

    /**
     * Returns a collection of pairs parsed from an argument: A subsequence
     * that matched the date regular expression, and a date parsed from the
     * match.
     *
     * @param string Any string
     * @return A collection of pairs parsed from an argument: A subsequence
     * that matched the date regular expression, and a date parsed from the
     * match
     */
    public static @NotNull Collection<Pair<String, Date>>
    findDates(@NotNull String string) {

        /*
         * Declare and initialize the result. Find matches for the date regular
         * expression.
         */
        final List<Pair<String, Date>> dates = new ArrayList<>();
        final Matcher matcher = pattern.matcher(string);

        /*
         * Declare a variable to receive the matches. Cycle while matches
         * exist.
         */
        String nextMatch;
        while (matcher.find()) {

            // Get the first/next match.
            nextMatch = string.substring(matcher.start(), matcher.end());
            try {

                /*
                 * Try to add a date match pair that contains the subsequence
                 * and the subsequence parsed as a date.
                 */
                dates.add(new Pair<>(nextMatch, dateFormat.parse(nextMatch)));
            }

            // Catch any parse exception that may occur.
            catch (@NotNull ParseException exception) {

                /*
                 * For whatever reason the date subsequence cannot be parsed.
                 * Add a date match pair that contains the subsequence together
                 * with null.
                 */
                dates.add(new Pair<>(nextMatch, null));
            }
        }

        // Return the collection. It belongs to the caller now.
        return dates;
    }

    /**
     * Formats a date in a standardized way.
     *
     * @param date Any date
     * @return A standardized, formatted date
     */
    public static String format(Date date) {
        return (null == date) ? null : getMessageFormat().format(date);
    }

    /**
     * Gets the data directory.
     *
     * @return The data directory
     */
    private static @NotNull File getDataDirectory() {
        return dataDirectory;
    }

    /**
     * Gets the date format used for parsing.
     *
     * @return The date format used for parsing
     */
    private static @NotNull DateFormat getDateFormat() {
        return dateFormat;
    }

    /**
     * Gets the regular expression used for dates.
     *
     * @return The regular expression used for dates
     */
    public static @NotNull String getDateRegex() {
        return dateRegex;
    }

    /**
     * Gets the extension separator.
     *
     * @return The extension separator
     */
    private static @NotNull String getExtensionSeparator() {

        // Note: This is not the same as the file separator!
        return ".";
    }

    /**
     * Gets the date format used for messages.
     *
     * @return The date format used for messages
     */
    private static @NotNull DateFormat getMessageFormat() {
        return messageFormat;
    }

    /**
     * Gets the configuration file name separator.
     *
     * @return The configuration file separator
     */
    private static @NotNull String getSeparator() {

        /*
         * Note: This is different from either the file, or extension
         * separators!
         */
        return "_";
    }

    /**
     * Parses a string expected to be in the standardized date format.
     *
     * @param date A string expected to be in standardized date format
     * @return The parsed date
     * @throws ParseException Indicates that the argument is not in the
     *                        standardized date format
     */
    public static @NotNull Date parse(@NotNull String date)
            throws ParseException {
        return getMessageFormat().parse(date);
    }

    /**
     * Constructs a file name.
     *
     * @param suffix The suffix of the file name
     * @return A file name
     */
    public @NotNull String constructFilename(@NotNull String suffix) {
        return constructFilename(getPrefix(), suffix, getFileType());
    }

    /**
     * Constructs a file name.
     *
     * @param date The date of the file
     * @return A file name
     */
    public @NotNull String constructFilename(@NotNull Date date) {
        return constructFilename(getPrefix(), date, getFileType());
    }

    /**
     * Gets the file type to be used.
     *
     * @return The file type to be used
     */
    public @NotNull String getFileType() {
        return fileType;
    }

    /**
     * Gets the prefix to be used.
     *
     * @return The prefix to be used
     */
    public @NotNull String getPrefix() {
        return prefix;
    }

    /**
     * Gets the type directory, the concatenation of the data directory
     * and a subdirectory with the same name as the type prefix.
     *
     * @return The type directory, the concatenation of the data directory
     * and a subdirectory with the same name as the type prefix
     */
    public @NotNull File getTypeDirectory() {
        return typeDirectory;
    }
}
