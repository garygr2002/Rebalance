package com.garygregg.rebalance.detailed;

import com.garygregg.rebalance.AccountKeyLibrary;
import com.garygregg.rebalance.DateUtilities;
import com.garygregg.rebalance.ElementReader;
import com.garygregg.rebalance.WeightType;
import com.garygregg.rebalance.interpreter.DoubleInterpreter;
import com.garygregg.rebalance.interpreter.LongInterpreter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DetailedsBuilder extends ElementReader<DetailedDescription> {

    // Our account number interpreter
    private final LongInterpreter accountNumberInterpreter =
            new LongInterpreter() {

                @Override
                protected void receiveException(@NotNull Exception exception,
                                                @NotNull String string,
                                                Long defaultValue) {
                    logMessage(Level.WARNING, String.format("Unparseable " +
                                    "account number '%s' at line number %d in " +
                                    "detailed file; using %d.", string,
                            getRow(), defaultValue));
                }
            };

    // Our allocation interpreter
    private final DoubleInterpreter allocationInterpreter =
            new DoubleInterpreter() {

                @Override
                protected void receiveException(@NotNull Exception exception,
                                                @NotNull String string,
                                                Double defaultValue) {
                    logMessage(Level.WARNING, String.format("Unparseable " +
                                    "allocation '%s' at line number %d and " +
                                    "column %d in detailed file; using %f.",
                            string, getRow(), getColumn(), defaultValue));
                }
            };

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
            final ElementReader<?> processor = new DetailedsBuilder();
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
                    (processor.hadFileProblem() ? "with a" : "without"));
        } catch (@NotNull IOException exception) {
            System.err.println(exception.getMessage());
        }
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

    @Override
    protected @NotNull Logger getReadingLogger() {
        return Logger.getLogger(DetailedsBuilder.class.getCanonicalName());
    }

    @Override
    protected void processElements(@NotNull String[] elements,
                                   int lineNumber) {

        /*
         * Set the line number. Create a new detailed description with the
         * institution mnemonic, account number and name.
         */
        setLineNumber(lineNumber);
        final DetailedDescription description = new DetailedDescription(

                elements[DetailedFields.INSTITUTION.getPosition()],
                accountNumberInterpreter.interpret(
                        elements[DetailedFields.NUMBER.getPosition()],
                        AccountKeyLibrary.getDefaultAccountNumber()),
                elements[DetailedFields.NAME.getPosition()]);

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
             * Set the column in the allocation interpreter. Get the field
             * associated with the position.
             */
            allocationInterpreter.setColumn(i + 1);
            field = positionMap.get(i);

            // Adjust the allocation of the associated fund type.
            description.adjustAllocation(field.getType(),
                    allocationInterpreter.interpret(elements[i]));
        }

        // Log some exit information.
        logMessage(getOrdinary(), String.format("Load of detailed metadata " +
                        "for account with number '%s' (\"%s\") at line %d " +
                        "was%s successful.",
                AccountKeyLibrary.format(description.getNumber()),
                description.getName(), lineNumber,
                hadLineProblem() ? " not" : ""));
    }

    @Override
    protected void setLineNumber(int lineNumber) {

        /*
         * Set the line number as the row in the account number interpreter and
         * allocation interpreter.
         */
        accountNumberInterpreter.setRow(lineNumber);
        allocationInterpreter.setRow(lineNumber);
    }

    @Override
    protected void startProcessing() {

        // Call the superclass method, and set the date in the library.
        super.startProcessing();
        setDate(library);
    }
}
