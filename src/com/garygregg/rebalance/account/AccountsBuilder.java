package com.garygregg.rebalance.account;

import com.garygregg.rebalance.*;
import com.garygregg.rebalance.interpreter.DoubleInterpreter;
import com.garygregg.rebalance.interpreter.LongInterpreter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AccountsBuilder extends ElementReader {

    // Our account number interpreter
    private final LongInterpreter accountNumberInterpreter =
            new LongInterpreter() {

                @Override
                protected void receiveException(@NotNull Exception exception,
                                                @NotNull String string) {
                    logMessage(Level.WARNING, String.format("Unparseable account " +
                            "number '%s' at line number %d in account file; " +
                            "using null.", string, getMarker()));
                }
            };

    // Our allocation interpreter
    private final DoubleInterpreter allocationInterpreter = new DoubleInterpreter() {

        @Override
        protected void receiveException(@NotNull Exception exception,
                                        @NotNull String string) {
            logMessage(Level.WARNING, String.format("Unparseable allocation " +
                            "'%s' at line number %d in account file; using null.",
                    string, getMarker()));
        }
    };

    // The account library instance
    private final AccountLibrary library = AccountLibrary.getInstance();

    // A map of element positions to account fields
    private final Map<Integer, AccountFields> positionMap = new HashMap<>();

    {

        // Cycle for each account field.
        for (AccountFields field : AccountFields.values()) {

            // Map the position of the field to the field itself.
            positionMap.put(field.getPosition(), field);
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
            final ElementReader processor = new AccountsBuilder();
            processor.readLines();

            // The account library should now be populated. Print its date.
            final AccountLibrary library = AccountLibrary.getInstance();
            System.out.printf("The date of the library is: %s.%n",
                    DateUtilities.format(library.getDate()));

            // Cycle for each account description in the library.
            for (AccountDescription description : library.getCatalog()) {

                // Display statistics for the first/next account description.
                System.out.printf("Institution: %-12s; " +
                                "Account number: %20s; " +
                                "Rebalance order: %20d; " +
                                "Account name: %-40s; " +
                                "Account type: %-20s; " +
                                "Rebalance procedure: %-20s%n",
                        description.getInstitution(),
                        AccountKeyLibrary.format(description.getNumber()),
                        description.getRebalanceOrder(),
                        description.getName(),
                        description.getType(),
                        description.getRebalanceProcedure().toString());
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
        return 6;
    }

    @Override
    @NotNull
    public String getPrefix() {
        return "account";
    }

    @Override
    protected @NotNull Logger getReadingLogger() {
        return Logger.getLogger(AccountsBuilder.class.getCanonicalName());
    }

    @Override
    protected void processElements(@NotNull String[] elements,
                                   int lineNumber) {

        /*
         * Set the line number as the marker in the account number interpreter.
         * Create a new account description with the interpreted account
         * number, re-balance order, name, tax type and re-balance procedure.
         */
        accountNumberInterpreter.setMarker(lineNumber);
        final AccountDescription description = new AccountDescription(

                elements[AccountFields.INSTITUTION.getPosition()],
                accountNumberInterpreter.interpret(
                        elements[AccountFields.NUMBER.getPosition()]),

                processRebalanceOrder(
                        elements[AccountFields.REBALANCE_ORDER.
                                getPosition()], lineNumber),

                elements[AccountFields.NAME.getPosition()],
                processType(
                        elements[AccountFields.TYPE.getPosition()],
                        lineNumber),

                processRebalanceProcedure(
                        elements[AccountFields.REBALANCE_PROCEDURE.
                                getPosition()], lineNumber));

        /*
         * Check the key of the description against the default key in the
         * library. Try to add the new account description, receiving any
         * existing description with the same key.
         */
        checkKey(library, description, lineNumber);
        if (null != library.addDescription(description)) {

            /*
             * Log a message describing where the duplicate account description
             * occurs.
             */
            logMessage(getExtraordinary(), String.format("Replacing account " +
                            "with number '%s' at line number %d in account " +
                            "file.",
                    AccountKeyLibrary.format(description.getNumber()),
                    lineNumber));
        }

        // Get the number of line elements and the number of account fields.
        final int elementsLength = elements.length;
        final int numberOfAccountFields = AccountFields.values().length;

        /*
         * The fields-to-process is the minimum of the number of line elements
         * and the number of account fields.
         */
        final int fieldsToProcess = Math.min(elementsLength,
                numberOfAccountFields);

        /*
         * Log a warning if the fields-to-process is less than the number of
         * line elements.
         */
        if (fieldsToProcess < elementsLength) {
            logMessage(Level.WARNING, String.format("There are %d account " +
                            "line elements but only %d account fields at " +
                            "line number %d; you might want to check that.",
                    elementsLength, numberOfAccountFields, lineNumber));
        }

        /*
         * Or log a different warning if the fields-to-process is less than the
         * number of account fields.
         */
        else if (fieldsToProcess < numberOfAccountFields) {
            logMessage(Level.WARNING, String.format("There are %d account " +
                            "fields but only %d account line elements at " +
                            "line number %d; you might want to check that.",
                    numberOfAccountFields, elementsLength, lineNumber));
        }

        // Cycle for each remaining fields-to-process.
        AccountFields field;
        for (int i = getMinimumFields(); i < fieldsToProcess; ++i) {

            /*
             * Get the field associated with the position. Set the line number
             * as the marker in the allocation interpreter.
             */
            field = positionMap.get(i);
            allocationInterpreter.setMarker(lineNumber);

            // Adjust the allocation of the associated fund type.
            description.adjustAllocation(field.getType(),
                    allocationInterpreter.interpret(elements[i]));
        }

        // Log some exit information.
        logMessage(getOrdinary(), String.format("Load of metadata for " +
                        "account with number '%s' (\"%s\") at line %d " +
                        "was%s successful.",
                AccountKeyLibrary.format(description.getNumber()),
                description.getName(), lineNumber,
                hadLineProblem() ? " not" : ""));
    }

    /**
     * Processes a rebalance order.
     *
     * @param rebalanceOrder The rebalance order
     * @param lineNumber     The line number where the rebalance order occurs
     * @return A processed rebalance order
     */
    private Long processRebalanceOrder(@NotNull String rebalanceOrder,
                                       int lineNumber) {

        // Declare and initialize the result with a default value.
        Long result = null;
        try {

            /*
             * Parse the rebalance order as an integer. Make sure the parsed
             * value is unique in the account file by: 1) removing the sign
             * bit; 2) casting the value to a long integer; 3) shifting the
             * value to the left by the size of an ordinary integer, and 4)
             * adding the line number. This will mean that accounts with the
             * same rebalance order will be rebalanced by which occurred first
             * in the accounts file.
             */
            final int parsedValue = Integer.parseInt(rebalanceOrder);
            result = (((long) (parsedValue & Integer.MAX_VALUE)) <<
                    Integer.SIZE) + lineNumber;
        } catch (@NotNull NumberFormatException exception) {

            /*
             * Log a warning message describing the unparseable rebalance
             * order.
             */
            logMessage(Level.WARNING, String.format("Unparseable re-balance " +
                    "order '%s' at line number %d in account file; using " +
                    "null.", rebalanceOrder, lineNumber));
        }

        // Return the result.
        return result;
    }

    /**
     * Processes a rebalance procedure.
     *
     * @param rebalanceProcedure The rebalance procedure to process
     * @param lineNumber         The line number where the rebalance procedure
     *                           occurs
     * @return A processed rebalance procedure
     */
    private RebalanceProcedure processRebalanceProcedure(
            @NotNull String rebalanceProcedure, int lineNumber) {

        // Declare and initialize the result with a default value.
        RebalanceProcedure result = null;
        try {

            // Parse the rebalance procedure.
            result = RebalanceProcedure.valueOf(
                    rebalanceProcedure.toUpperCase());
        } catch (@NotNull IllegalArgumentException exception) {

            /*
             * Log a warning message describing the unparseable rebalance
             * procedure.
             */
            logMessage(Level.WARNING, String.format("Unparseable account " +
                            "rebalance procedure '%s' at line number %d in " +
                            "account file; using null.",
                    rebalanceProcedure, lineNumber));
        }

        // Return the result.
        return result;
    }

    /**
     * Processes a tax type.
     *
     * @param type       The tax type to process
     * @param lineNumber The line number where the tax type occurs
     * @return A processed tax type
     */
    private TaxType processType(@NotNull String type, int lineNumber) {

        // Declare and initialize the result with a default value.
        TaxType result = null;
        try {

            // Parse the tax type.
            result = TaxType.valueOf(type.toUpperCase());
        } catch (@NotNull IllegalArgumentException exception) {

            // Log a warning message describing the unparseable tax type.
            logMessage(Level.WARNING, String.format("Unparseable account " +
                    "tax type '%s' at line number %d in account file; using " +
                    "null.", type, lineNumber));
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
