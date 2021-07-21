package com.garygregg.rebalance.detailed;

import com.garygregg.rebalance.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DetailedsBuilder extends ElementReader {

    // The detailed library instance
    private final DetailedLibrary library = DetailedLibrary.getInstance();

    // A map of element positions to detailed fields
    private final Map<Integer, DetailedFields> positionMap = new HashMap<>();

    {

        // Cycle for each detailed field.
        for (DetailedFields field : DetailedFields.values()) {

            // Map the position of the field to the field itself.
            positionMap.put(field.getPosition(), field);
        }
    }

    /**
     * Displays weights for a detailed description.
     *
     * @param description A detailed description
     */
    private static void displayWeights(@NotNull DetailedDescription description) {

        /*
         * TODO: Delete this method.
         *
         * Print a header for the table that follows, and cycle for each weight
         * type.
         */
        System.out.printf("Weights for detailed account description: %s follow:%n",
                description.getKey());
        for (WeightType type : WeightType.values()) {

            // Print the weight assigned to each weight type.
            System.out.printf("%-30s: %3f%n", type, description.getAllocation(type));
        }
    }

    /**
     * Tests this class.
     *
     * @param arguments Command line arguments
     */
    public static void main(String[] arguments) {

        /*
         * TODO: Delete this method.
         */
        try {

            // Create an element processor. Read lines from the file object.
            final ElementReader processor = new DetailedsBuilder();
            processor.readLines();

            // The detailed library should now be populated. Print its date.
            final DetailedLibrary library = DetailedLibrary.getInstance();
            System.out.printf("The date of the library is: %s.%n",
                    DateUtilities.format(library.getDate()));

            // Cycle for each detailed description in the library.
            for (DetailedDescription description : library.getCatalog()) {

                // Display statistics for the first/next detailed description.
                System.out.printf("Account institution for detail: %-12s; " +
                                "Account number for detail: %20s; " +
                                "Account name for detail: %-40s%n",
                        description.getInstitution(),
                        AccountKeyLibrary.format(description.getNumber()),
                        description.getName());
                displayWeights(description);
            }

            // Say whether the element processor had warning or error.
            System.out.printf("The element processor " +
                            "completed %s warning or error.%n",
                    (processor.hadProblem() ? "with a" : "without"));
        } catch (@NotNull IOException exception) {
            System.err.println(exception.getMessage());
        }
    }

    /**
     * Processes an institution.
     *
     * @param institution The institution
     * @return A processed institution
     */
    private static String processInstitution(@NotNull String institution) {
        return institution;
    }

    /**
     * Processes an account name.
     *
     * @param name The account name
     * @return A processed account name
     */
    private static String processName(@NotNull String name) {
        return name;
    }

    @Override
    protected @NotNull Logger getReadingLogger() {
        return Logger.getLogger(DetailedsBuilder.class.getCanonicalName());
    }

    @Override
    public int getMinimumFields() {
        return 3;
    }

    @Override
    @NotNull
    public String getPrefix() {
        return "detailed";
    }

    /**
     * Processes an allocation element.
     *
     * @param allocation The allocation element
     * @param lineNumber The line number where the allocation occurs
     * @return A processed allocation
     */
    private Double processAllocation(@NotNull String allocation,
                                     int lineNumber) {

        // Declare and initialize the result to a default value.
        Double result = null;
        try {

            /*
             * Parse the allocation as a floating point number. Catch any
             * number format exception that may occur.
             */
            result = Double.parseDouble(allocation);
        } catch (@NotNull NumberFormatException exception) {

            // Log a warning message describing the unparseable allocation.
            logMessage(Level.WARNING, String.format("Unparseable allocation " +
                            "'%s' at line number %d in detailed file; using null",
                    allocation, lineNumber));
        }

        // Return the result.
        return result;
    }

    @Override
    protected boolean processElements(@NotNull String[] elements,
                                      int lineNumber) {

        // Create a new detailed description with the detailed number and name.
        final DetailedDescription description = new DetailedDescription(

                processInstitution(preprocessField(
                        elements[DetailedFields.INSTITUTION.getPosition()])),

                processNumber(preprocessField(
                        elements[DetailedFields.NUMBER.getPosition()]),
                        lineNumber),

                processName(preprocessField(
                        elements[DetailedFields.NAME.getPosition()])));

        /*
         * Check the key of the description against the default key in the
         * library. Try to add the new detailed description, receiving any
         * existing description with the same key.
         */
        checkKey(library, description, lineNumber);
        if (null != library.addDescription(description)) {

            /*
             * Log a message describing where the duplicate detailed description
             * occurs.
             */
            logMessage(getExtraordinary(), String.format("Replacing details " +
                            "with account number '%s' at line number %d in " +
                            "detailed file.",
                    AccountKeyLibrary.format(description.getNumber()),
                    lineNumber));
        }

        // Get the number of line elements and the number of detailed fields.
        final int elementsLength = elements.length;
        final int numberOfDetailedFields = DetailedFields.values().length;

        /*
         * The fields-to-process is the minimum of the number of line elements
         * and the number of detailed fields.
         */
        final int fieldsToProcess = Math.min(elementsLength,
                numberOfDetailedFields);

        /*
         * Log a warning if the fields-to-process is less than the number of
         * line elements.
         */
        if (fieldsToProcess < elementsLength) {
            logMessage(Level.WARNING, String.format("There are %d detailed " +
                            "line elements but only %d detailed fields at " +
                            "line number %d; you might want to check that.",
                    elementsLength, numberOfDetailedFields, lineNumber));
        }

        /*
         * Or log a different warning if the fields-to-process is less than the
         * number of detailed fields.
         */
        else if (fieldsToProcess < numberOfDetailedFields) {
            logMessage(Level.WARNING, String.format("There are %d detailed " +
                            "fields but only %d detailed line elements at " +
                            "line number %d; you might want to check that.",
                    numberOfDetailedFields, elementsLength, lineNumber));
        }

        // Cycle for each remaining field-to-process.
        DetailedFields field;
        for (int i = getMinimumFields(); i < fieldsToProcess; ++i) {

            /*
             * Get the field associated with the position, Adjust the
             * allocation of the associated fund type.
             */
            field = positionMap.get(i);
            description.adjustAllocation(field.getType(),
                    processAllocation(elements[i], lineNumber));
        }

        // Log some information and return the result.
        logMessage(getOrdinary(), String.format("Successfully loaded " +
                        "detailed metadata for account number '%s' (\"%s\") " +
                        "at line %d.",
                AccountKeyLibrary.format(description.getNumber()),
                description.getName(), lineNumber));
        return true;
    }

    /**
     * Processes an account number.
     *
     * @param number     The account number
     * @param lineNumber The line number where the account number occurs
     * @return A processed account number
     */
    private Long processNumber(@NotNull String number, int lineNumber) {

        // Try to parse an account number. Is the number not parseable?
        final Long result = AccountKey.parseLong(number);
        if (null == result) {

            /*
             * The number is not parseable. Log a warning message describing
             * the unparseable account number.
             */
            logMessage(Level.WARNING, String.format("Unparseable account " +
                    "number '%s' at line number %d in detailed file; " +
                    "using null.", number, lineNumber));
        }

        // Return the result.
        return result;
    }

    @Override
    protected void startProcessing() {

        // Call the superclass method, and set the date in the library.
        super.startProcessing();
        setDate(library);
    }
}
