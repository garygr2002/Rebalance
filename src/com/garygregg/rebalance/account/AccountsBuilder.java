package com.garygregg.rebalance.account;

import com.garygregg.rebalance.*;
import com.garygregg.rebalance.interpreter.DoubleInterpreter;
import com.garygregg.rebalance.interpreter.LongInterpreter;
import com.garygregg.rebalance.interpreter.RebalanceProcedureInterpreter;
import com.garygregg.rebalance.interpreter.TaxTypeInterpreter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AccountsBuilder extends ElementReader<AccountDescription> {

    // Our account number interpreter
    private final LongInterpreter accountNumberInterpreter =
            new LongInterpreter() {

                @Override
                protected void receiveException(@NotNull Exception exception,
                                                @NotNull String string,
                                                Long defaultValue) {
                    logMessage(Level.WARNING, String.format("Unparseable account " +
                            "number '%s' at line number %d in account file; " +
                            "using %d.", string, getRow(), defaultValue));
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
                                    "column %d in account file; using %f.",
                            string, getRow(), getColumn(), defaultValue));
                }
            };

    // The account library instance
    private final AccountLibrary library = AccountLibrary.getInstance();

    // A map of element positions to account fields
    private final Map<Integer, AccountFields> positionMap = new HashMap<>();

    // Our re-balance order interpreter
    private final LongInterpreter rebalanceOrderInterpreter =
            new LongInterpreter() {

                @Override
                protected @NotNull Long doInterpret(@NotNull String string) {

                    /*
                     * Parse the re-balance order using the superclass. Make
                     * sure the parsed value is unique (to an integer value) in
                     * the account file by: 1) removing the high-end bits
                     * greater than an ordinary integer; 2) shifting the value
                     * to the left by the size of an ordinary integer, and 3)
                     * adding the row. This will mean that accounts with the
                     * same, integer re-balance order will be rebalanced by
                     * that which occurred first in the accounts file.
                     */
                    final long parsedValue = super.doInterpret(string);
                    return ((parsedValue & Integer.MAX_VALUE) <<
                            Integer.SIZE) + getRow();
                }

                @Override
                protected void receiveException(@NotNull Exception exception,
                                                @NotNull String string,
                                                Long defaultValue) {
                    logMessage(Level.WARNING, String.format("Unparseable " +
                                    "re-balance order '%s' at line number " +
                                    "%d in account file; using %d.",
                            string, getRow(), defaultValue));
                }
            };

    // Our re-balance procedure interpreter
    private final RebalanceProcedureInterpreter rebalanceProcedureInterpreter =
            new RebalanceProcedureInterpreter() {

                @Override
                protected void receiveException(@NotNull Exception exception,
                                                @NotNull String string,
                                                RebalanceProcedure defaultValue) {
                    logMessage(Level.WARNING, String.format("Unparseable " +
                            "account re-balance procedure '%s' at " +
                            "line number %d in account file; using " +
                            "%s.", string, getRow(), defaultValue));
                }
            };

    // Our tax type interpreter
    private final TaxTypeInterpreter taxTypeInterpreter =
            new TaxTypeInterpreter() {

                @Override
                protected void receiveException(@NotNull Exception exception,
                                                @NotNull String string,
                                                TaxType defaultValue) {
                    logMessage(Level.WARNING, String.format("Unparseable " +
                                    "account tax type '%s' at line number %d in " +
                                    "account file; using %s.", string, getRow(),
                            defaultValue));
                }
            };

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
            final ElementReader<?> processor = new AccountsBuilder();
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
         * Set the line number. Create a new account description with the
         * interpreted account number, re-balancer order, name, tax type and
         * re-balance procedure.
         */
        setLineNumber(lineNumber);
        final AccountDescription description = new AccountDescription(

                // Institution and account number
                elements[AccountFields.INSTITUTION.getPosition()],
                accountNumberInterpreter.interpret(
                        elements[AccountFields.NUMBER.getPosition()],
                        AccountKeyLibrary.getDefaultAccountNumber()),

                // Re-balancer order
                rebalanceOrderInterpreter.interpret(
                        elements[AccountFields.REBALANCE_ORDER.getPosition()]),

                // Name and tax type
                elements[AccountFields.NAME.getPosition()],
                taxTypeInterpreter.interpret(
                        elements[AccountFields.TYPE.getPosition()]),

                // Re-balance procedure
                rebalanceProcedureInterpreter.interpret(elements[
                        AccountFields.REBALANCE_PROCEDURE.getPosition()]));

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
        logMessage(getOrdinary(), String.format("Load of metadata for " +
                        "account with number '%s' (\"%s\") at line %d " +
                        "was%s successful.",
                AccountKeyLibrary.format(description.getNumber()),
                description.getName(), lineNumber,
                hadLineProblem() ? " not" : ""));
    }

    @Override
    protected void setLineNumber(int lineNumber) {

        /*
         * Set the line number as the row in the account number interpreter
         * and allocation interpreter.
         */
        accountNumberInterpreter.setRow(lineNumber);
        allocationInterpreter.setRow(lineNumber);

        /*
         * Set the line number as the row in the re-balancer order
         * interpreter, the re-balance procedure interpreter, and the tax type
         * interpreter.
         */
        rebalanceOrderInterpreter.setRow(lineNumber);
        rebalanceProcedureInterpreter.setRow(lineNumber);
        taxTypeInterpreter.setRow(lineNumber);
    }

    @Override
    protected void startProcessing() {

        // Call the superclass method, and set the date in the library.
        super.startProcessing();
        setDate(library);
    }
}
