package com.garygregg.rebalance;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class ElementReader<DescriptionType extends Description<?>>
        extends ElementProcessor {

    // A map of element indices to field processors
    private final Map<Integer, FieldProcessor<?>> processorMap =
            new HashMap<>();

    // The date(s) parsed from the name of the processed file
    private Collection<Pair<String, Date>> datesParsed;

    {

        // Assign the logger based on the class canonical name.
        setLogger(Logger.getLogger(ElementReader.class.getCanonicalName()));
    }

    /**
     * Preprocesses elements for element processors.
     *
     * @param elements The elements to preprocess.
     * @return The preprocessed elements
     */
    @Contract("_ -> param1")
    private static @NotNull String @NotNull [] preprocessElements(
            @NotNull String @NotNull [] elements) {

        // Get the length of the element array, and cycle for each element.
        final int length = elements.length;
        for (int i = 0; i < length; ++i) {

            /*
             * Preprocess the first/next element by removing leading and
             * trailing whitespace.
             */
            elements[i] = elements[i].trim();
        }

        // Return the preprocessed elements.
        return elements;
    }

    /**
     * Adds a field processor.
     *
     * @param index     The index for the field processor
     * @param processor The field processor to add
     * @return Any field processor that was replaced at the given index
     */
    @SuppressWarnings("UnusedReturnValue")
    protected FieldProcessor<?> addFieldProcessor(int index,
                                                  FieldProcessor<?> processor) {
        return processorMap.put(index, processor);
    }

    /**
     * Checks the key of a description against the default key for a library.
     *
     * @param library     The library to check
     * @param description The description to check
     * @param lineNumber  The line number in a data file where the description
     *                    occurs
     * @param <T>         The key type
     */
    protected <T> void checkKey(@NotNull Library<T, ?> library,
                                @NotNull Description<T> description,
                                int lineNumber) {

        /*
         * Get the default key from the library, and check it against the key
         * used in the description. Are they equal?
         */
        final T defaultKey = library.getDefaultKey();
        if (defaultKey.equals(description.getKey())) {

            // The keys are equal. Log a message.
            logMessage(Level.WARNING, String.format("Description of type " +
                            "'%s' uses default key '%s' at line number %d; " +
                            "you might want to check that.",
                    getPrefix(), defaultKey, lineNumber));
        }
    }

    /**
     * Reads lines from a field reader.
     *
     * @param fileReader      The reader from which to read lines
     * @param continueOnFalse True if lines should continue to be read if at
     *                        least one had an error, false otherwise
     * @return True if one or more lines had an error, false otherwise
     * @throws IOException Indicates an I/O exception occurred
     */
    private boolean doReadLines(@NotNull FileReader fileReader,
                                boolean continueOnFalse) throws IOException {

        /*
         * Declare variables to receive line elements, the line itself, and a
         * variable to track the line number.
         */
        String[] elements;
        String line;
        int lineNumber = 1;

        /*
         * Declare a variable to receive a field count. Get the minimum number
         * of fields. Declare and initialize flags.
         */
        int fieldCount;
        final int minimumFields = getMinimumFields();
        boolean noFailures = true, processResult = true;

        /*
         * Create a new buffered reader. Cycle while the process result is
         * true, and we have not reached the end of file.
         */
        final BufferedReader bufferedReader = new BufferedReader(fileReader);
        while (processResult && (null != (line = bufferedReader.readLine()))) {

            /*
             * We have read a new line. Split the line by commas, and get the
             * field count. Is the field count at least as long as the minimum
             * number of fields?
             */
            elements = line.split(",");
            fieldCount = elements.length;
            //noinspection AssignmentUsedAsCondition
            if (processResult = (minimumFields <= fieldCount)) {

                /*
                 * The field count is at least as long as the minimum number of
                 * fields. Reset the line problem flag. Preprocess, then
                 * process the elements. Determine if there has been no problem
                 * in doing so by checking the line problem flag.
                 */
                resetLineProblem();
                processElements(preprocessElements(line.split(",")),
                        lineNumber++);
                processResult = !hadLineProblem();
            }

            // There are an insufficient number of fields.
            else {

                // Log a descriptive message.
                logMessage(continueOnFalse ? Level.WARNING : Level.SEVERE,
                        String.format("File at line " +
                                        "%d contains insufficient elements, " +
                                        "needs at least %d, only %d given; %s.",
                                lineNumber, minimumFields, fieldCount,
                                continueOnFalse ?
                                        "skipping line" : "stopping"));
            }

            /*
             * Update the no-failures flag to track whether the processing of
             * any line failed. Update the process-result flag to continue if
             * so indicate by the caller.
             */
            noFailures &= processResult;
            processResult |= continueOnFalse;
        }

        /*
         * Done processing lines by end-of-file, or error. Close buffered
         * reader, and return the no-failures flag.
         */
        bufferedReader.close();
        return noFailures;
    }

    /**
     * Gets the date(s) parsed from the name of the processed file
     *
     * @return The date(s) parsed from the name of the processed file
     */
    private Collection<Pair<String, Date>> getDatesParsed() {
        return datesParsed;
    }

    /**
     * Gets the file type.
     *
     * @return The file type
     */
    protected String getFileType() {
        return "csv";
    }

    /**
     * Gets the minimum number of fields a line must contain.
     *
     * @return The minimum number of fields a line must contain
     */
    public abstract int getMinimumFields();

    /**
     * Returns a file reader, if any, for the most recent file with a name that
     * is flagged as occurring before the given date (date may be null).
     *
     * @param directory The directory from which to return a reader
     * @param date      The given date (maybe null)
     * @return A file reader for the thus described file, or null if none exists
     */
    private FileReader getMostRecentReader(@NotNull File directory, Date date) {

        // Get the date utilities object. Declare and initialize the result.
        final DateUtilities utilities = getDateUtilities();
        FileReader result = null;

        /*
         * List files in the given directory that match a regular expression
         * of named files that this element processor can use. Are there any
         * extant files like this?
         */
        final File[] files = directory.listFiles((dir1, name) ->
                name.matches(utilities.constructFilename(
                        DateUtilities.getDateRegex())));
        if ((null != files) && (0 < files.length)) {

            /*
             * There are files that match the regular expression. Declare a
             * comparator for files using their names.
             */
            final Comparator<File> comparator =
                    Comparator.comparing(File::getName);

            /*
             * Sort the files by name. Initialize an array index to the last
             * file that qualified. Is date qualifier not null?
             */
            Arrays.sort(files, comparator);
            int index = files.length - 1;
            if (null != date) {

                /*
                 * The date qualifier is not null. Construct the path for a
                 * file usable by this element processor that has the given
                 * date.
                 */
                final Path path = Paths.get(directory.getPath(),
                        utilities.constructFilename(date));

                /*
                 * Do a binary search for the qualifying file. If we do not
                 * find the exact file, the result of the search will be
                 * negative. Is the result negative?
                 */
                index = Arrays.binarySearch(files, new File(path.toString()),
                        comparator);
                if (0 > index) {

                    /*
                     * The search result is negative, so we did not find the
                     * exact file. Try to adjust the index to that of the file
                     * that is most recent, but prior to the indicated date.
                     * If the index is still negative than there is no
                     * qualifying file.
                     */
                    index = Math.abs(index) - 2;
                }
            }

            /*
             * If the index not negative then we have found a qualifying file.
             * We found either the exact specified file, or the most recent
             * file flagged as occurring before it. Is the index not negative?
             */
            if (0 <= index) {

                // The index is not negative.
                try {

                    /*
                     * Try to create a file reader with the indicated file.
                     * Find and parse any dates contained in the name of the
                     * file.
                     */
                    final File file = files[index];
                    result = new FileReader(file);
                    setDatesParsed(DateUtilities.findDates(file.getName()));
                }

                /*
                 * We have checked for an extant file, so we should never
                 * receive a file-not-found exception. This being the case,
                 * do not allow our caller to have to check for the
                 * file-not-found exception. Wrap the thrown exception in a
                 * runtime exception.
                 */
                catch (@NotNull FileNotFoundException exception) {
                    throw new RuntimeException(exception);
                }
            }
        }

        // Return the result.
        return result;
    }

    /**
     * Gets the logging level for ordinary, non-warning activity.
     *
     * @return The logging level for ordinary, non-warning activity
     */
    protected @NotNull Level getOrdinary() {
        return Level.FINE;
    }

    /**
     * Gets the prefix for configuration files of a subclass.
     *
     * @return The prefix for configuration files of a subclass
     */
    public abstract @NotNull String getPrefix();

    /**
     * Gets the specific class/subclass reading logger.
     *
     * @return The specific class/subclass reading logger
     */
    protected abstract @NotNull Logger getReadingLogger();

    /**
     * Processes line elements.
     *
     * @param elements   The line elements
     * @param lineNumber The line number where the elements occur
     */
    protected abstract void processElements(@NotNull String[] elements,
                                            int lineNumber);

    /**
     * Calls a field processor for a given element index.
     *
     * @param index      The given element index
     * @param field      The field to processor
     */
    protected void processField(int index, @NotNull String field) {

        // Get the indexed field processor. Is the processor not null?
        final FieldProcessor<?> processor = processorMap.get(index);
        if (null != processor) {

            // The processor is not null. Call it to process the field.
            processor.processField(field);
        }
    }

    /**
     * Reads lines from a configuration file with a date flag that occurs on,
     * or before a given date.
     *
     * @param date            The most recent file may not be later than this
     *                        date
     * @param continueOnFalse True if lines should continue to be read if at
     *                        least one had an error, false otherwise
     * @return True if one or more lines had an error, false otherwise
     * @throws IOException Indicates an I/O exception occurred
     */
    public boolean readLines(Date date, boolean continueOnFalse)
            throws IOException {

        /*
         * Reset the problem flag. Get the most recent reader for the type
         * directory and date.
         */
        resetFileProblem();
        final FileReader reader = getMostRecentReader(
                getDateUtilities().getTypeDirectory(), date);

        // Is the reader not null?
        boolean result = (null != reader);
        if (result) {

            // The reader is not null. Go ahead and read lines.
            result = readLines(reader, continueOnFalse);

            // Bad news. We could not find a reader. Was the date null?
        } else if (null == date) {

            /*
             * The date was null, indicating there was no file with the known
             * name format.
             */
            logMessage(Level.SEVERE, String.format("No identifiable file of " +
                            "type '%s' could be located; check the directory.",
                    getPrefix()));
        } else {

            /*
             * The date was not null, indicating there may be a file with the
             * known name format, but its date occurs after the given date.
             */
            logMessage(Level.SEVERE, String.format("No file of type '%s' " +
                            "predates '%s'; check the directory.",
                    getPrefix(), DateUtilities.format(date)));
        }

        // Return the result.
        return result;
    }

    /**
     * Reads lines from a configuration file with a date flag that occurs on,
     * or before the current date.
     *
     * @param continueOnFalse True if lines should continue to be read if at
     *                        least one had an error, false otherwise
     * @return True if one or more lines had an error, false otherwise
     * @throws IOException Indicates an I/O exception occurred
     */
    @SuppressWarnings("unused")
    public boolean readLines(boolean continueOnFalse)
            throws IOException {
        return readLines((Date) (null), continueOnFalse);
    }

    /**
     * Reads lines from a configuration file with a date flag that occurs on,
     * or before the current date.  Uses a default <code>continueOnFalse</code>
     * flag.
     *
     * @return True if one or more lines had an error, false otherwise
     * @throws IOException Indicates an I/O exception occurred
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean readLines()
            throws IOException {
        return readLines((Date) null, true);
    }

    /**
     * Reads lines from a configuration file with a date flag that occurs on,
     * or before a given date. Uses a default <code>continueOnFalse</code>
     * flag.
     *
     * @param date The most recent file may not be later than this date
     * @return True if one or more lines had an error, false otherwise
     * @throws IOException Indicates an I/O exception occurred
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean readLines(@NotNull Date date)
            throws IOException {
        return readLines(date, true);
    }

    /**
     * Reads lines from a field reader.
     *
     * @param fileReader      The reader from which to read lines
     * @param continueOnFalse True if lines should continue to be read if at
     *                        least one had an error, false otherwise
     * @return True if one or more lines had an error, false otherwise
     * @throws IOException Indicates an I/O exception occurred
     */
    private boolean readLines(@NotNull FileReader fileReader,
                              boolean continueOnFalse) throws IOException {

        // Declare the result, and get the processor's prefix.
        boolean result;
        final String prefix = getPrefix();

        // Log a 'start processing' message...
        logMessage(getOrdinary(), String.format("Starting processing " +
                "for element processor of type '%s'.", prefix));

        // ...and start processing.
        startProcessing();
        try {

            // Log a message, and perform the unguarded line read.
            logMessage(getOrdinary(), String.format("Reading lines for " +
                    "element processor of type '%s'.", prefix));
            result = doReadLines(fileReader, continueOnFalse);
        }

        // Do this block even if an exception occurred.
        finally {

            // Log 'stop processing' message.
            logMessage(getOrdinary(), String.format("Stopping " +
                            "processing for element processor of type '%s'.",
                    prefix));
            stopProcessing();
        }

        // Log an exit message.
        logMessage(getInformationLevel(), String.format("Element reader of " +
                        "type '%s' is exiting with%s error(s)%s.", prefix,
                result ? " no" : "", (result || continueOnFalse) ? "" :
                        "; unread lines after the first error may exist."));

        // Return the result.
        return result;
    }

    /**
     * Sets a date in the library.
     *
     * @param library The library in which to set a date.
     * @return True if the date in the library was successfully set, false
     * otherwise
     */
    @SuppressWarnings("UnusedReturnValue")
    protected boolean setDate(@NotNull Library<?, ?> library) {

        /*
         * Declare and initialize an iterator for date format match and date
         * pairs.
         */
        final Iterator<Pair<String, Date>> iterator =
                getDatesParsed().iterator();

        /*
         * Initialize the date in the library. Cycle while a date has not been
         * set, and date pairs exist.
         */
        library.setDate(null);
        while ((null == library.getDate()) && iterator.hasNext()) {

            // Set a candidate date in the library.
            library.setDate(iterator.next().getSecond());
        }

        // Return whether the library has a non-null date.
        return (null != library.getDate());
    }

    /**
     * Sets the dates parsed from the name of the processed file
     *
     * @param datesParsed The date(s) parsed from the name of the processed
     *                    file
     */
    private void setDatesParsed(Collection<Pair<String, Date>> datesParsed) {
        this.datesParsed = datesParsed;
    }

    /**
     * Sets the line number in the interpreters.
     *
     * @param lineNumber The line number to set in the interpreters
     */
    protected void setLineNumber(int lineNumber) {
        // The default is to do nothing.
    }

    /**
     * Sets the target in the field processors.
     *
     * @param description The target to set in the field processors
     */
    protected void setTarget(@NotNull DescriptionType description) {
        // The default is to do nothing.
    }

    /**
     * Notifies a derived class that element processing is about to start.
     */
    protected void startProcessing() {
        setLogger(getReadingLogger());
    }

    /**
     * Notifies a derived class that element processing has finished.
     */
    protected void stopProcessing() {

        // Clear the dates parsed and restore the last logger, if any.
        setDatesParsed(null);
        restoreLogger();
    }

    /**
     * A processor for fields that can be parameterized for target.
     *
     * @param <TargetType> The type of the target
     */
    protected abstract static class FieldProcessor<TargetType> {

        // The target
        private TargetType target;

        /**
         * Gets the target.
         *
         * @return The target
         */
        public TargetType getTarget() {
            return target;
        }

        /**
         * Processes the field.
         */
        public abstract void processField(@NotNull String field);

        /**
         * Sets the target.
         *
         * @param target The target to set
         */
        public void setTarget(TargetType target) {
            this.target = target;
        }
    }

    /**
     * A parameterizable field processor that only accepts non-blank fields.
     *
     * @param <TargetType> The type of the target
     */
    protected abstract static class FieldProcessorIfNotEmpty<TargetType>
            extends FieldProcessor<TargetType> {

        @Override
        public final void processField(@NotNull String field) {

            // Process the field if it is not blank.
            if (!field.trim().isBlank()) {
                processNotEmptyField(field);
            }
        }

        /**
         * Processes the non-blank field.
         *
         * @param field      The field
         */
        public abstract void processNotEmptyField(@NotNull String field);
    }
}
