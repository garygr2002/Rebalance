package com.garygregg.rebalance.holding;

import com.garygregg.rebalance.*;
import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.interpreter.CodeInterpreter;
import com.garygregg.rebalance.interpreter.DoubleInterpreter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HoldingsBuilder extends ElementReader {

    // Our code interpreter
    private final CodeInterpreter codeInterpreter = new CodeInterpreter();

    // The holding library instance
    private final HoldingLibrary library = HoldingLibrary.getInstance();

    // The name processor
    private final FieldProcessor<HoldingDescription> nameProcessor =
            new FieldProcessor<>() {

                @Override
                public void processField(@NotNull String field, int lineNumber) {
                    getTarget().setName(field);
                }
            };

    // Our price interpreter
    private final DoubleInterpreter priceInterpreter =
            new DoubleInterpreter() {

                @Override
                protected void receiveException(@NotNull Exception exception,
                                                @NotNull String string,
                                                Double defaultValue) {
                    logMessage(Level.WARNING, String.format("Unparseable " +
                                    "price '%s' at line number %d in " +
                                    "holding file; using default %f instead.",
                            string, getMarker(), defaultValue));
                }
            };

    // The price processor
    private final FieldProcessorIfNotEmpty<HoldingDescription> priceProcessor =
            new FieldProcessorIfNotEmpty<>() {

                @Override
                public void processNotEmptyField(@NotNull String field,
                                                 int lineNumber) {
                    getTarget().setPrice(priceInterpreter.interpret(field,
                            1.));
                }
            };

    // Our shares interpreter
    private final DoubleInterpreter sharesInterpreter =
            new DoubleInterpreter() {

                @Override
                protected void receiveException(@NotNull Exception exception,
                                                @NotNull String string,
                                                Double defaultValue) {
                    logMessage(Level.WARNING, String.format("Unparseable " +
                                    "shares '%s' at line number %d in " +
                                    "holding file; using default %f instead.",
                            string, getMarker(), defaultValue));
                }
            };

    // The shares processor
    private final FieldProcessorIfNotEmpty<HoldingDescription> sharesProcessor =
            new FieldProcessorIfNotEmpty<>() {

                @Override
                public void processNotEmptyField(@NotNull String field,
                                                 int lineNumber) {
                    getTarget().setShares(sharesInterpreter.interpret(field,
                            0.));
                }
            };

    // The parent tracker
    private final ParentTracker tracker = ParentTracker.getInstance();

    // Our value interpreter
    private final DoubleInterpreter valueInterpreter =
            new DoubleInterpreter() {

                @Override
                protected void receiveException(@NotNull Exception exception,
                                                @NotNull String string,
                                                Double defaultValue) {
                    logMessage(Level.WARNING, String.format("Unparseable " +
                                    "value '%s' at line number %d in " +
                                    "holding file; using default %f instead.",
                            string, getMarker(), defaultValue));
                }
            };

    // The value processor
    private final FieldProcessorIfNotEmpty<HoldingDescription> valueProcessor =
            new FieldProcessorIfNotEmpty<>() {

                @Override
                public void processNotEmptyField(@NotNull String field,
                                                 int lineNumber) {

                    // Get the holding description and its current value.
                    final HoldingDescription description = getTarget();
                    final Currency current = getTarget().getValue();

                    /*
                     * Use the current value of the description of the
                     * description if it is not null; otherwise use zero.
                     */
                    final double defaultValue = (null == current) ?
                            Currency.getZero().getValue() : current.getValue();

                    // Set the value of the target using the field.
                    description.setValue(valueInterpreter.interpret(field,
                            defaultValue));
                }
            };

    {

        // Initialize a field index. Add the name and shares processors.
        int fieldIndex = HoldingFields.KEY.getPosition();
        addFieldProcessor(++fieldIndex, nameProcessor);
        addFieldProcessor(++fieldIndex, sharesProcessor);

        // Add the price and value processors.
        addFieldProcessor(++fieldIndex, priceProcessor);
        addFieldProcessor(++fieldIndex, valueProcessor);
    }

    /**
     * Gets a string representation of the parent of a holding description.
     *
     * @param description A holding description
     * @return A string representation of the holding description
     */
    private static @NotNull String getParent(@NotNull HoldingDescription
                                                     description) {

        /*
         * TODO: Delete this whole method.
         *
         * Get the key and line type of the holding description.
         */
        final HoldingKey key = description.getHoldingParentChild();
        final HoldingLineType lineType = description.getLineType();

        /*
         * Format the parent of holding key as an account key if the line type
         * is 'ticker'. Otherwise, just output the parent itself.
         */
        return ((null != lineType) &&
                (lineType.equals(HoldingLineType.TICKER))) ?
                AccountKey.toString(key.getFirst()) : key.getFirst();
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
            final ElementReader processor = new HoldingsBuilder();
            processor.readLines();

            // The holding library should now be populated. Print its date.
            final HoldingLibrary library = HoldingLibrary.getInstance();
            System.out.printf("The date of the library is: %s.%n",
                    DateUtilities.format(library.getDate()));

            // Cycle for each holding description in the library.
            HoldingKey key;
            for (HoldingDescription description : library.getCatalog()) {

                // Display statistics for the first/next holding description.
                key = description.getHoldingParentChild();
                System.out.printf("Line Code: %-12s; " +
                                "Parent: %-32s; " +
                                "Child: %-32s; " +
                                "Name: %-45s; " +
                                "Shares: %-15s; " +
                                "Price: %-15s; " +
                                "Value: %-15s%n",
                        description.getLineType(),
                        getParent(description), key.getSecond(),
                        description.getName(),
                        description.getShares(),
                        description.getPrice(),
                        description.getValue());
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
        return 2;
    }

    @Override
    @NotNull
    public String getPrefix() {
        return "holding";
    }

    @Override
    protected @NotNull Logger getReadingLogger() {
        return Logger.getLogger(HoldingsBuilder.class.getCanonicalName());
    }

    @Override
    protected void processElements(@NotNull String[] elements,
                                   int lineNumber) {

        /*
         * Set the line number as the marker in the code interpreter and the
         * price interpreter.
         */
        codeInterpreter.setMarker(lineNumber);
        priceInterpreter.setMarker(lineNumber);

        /*
         * Set the line number as the marker in the shares interpreter and the
         * value interpreter.
         */
        sharesInterpreter.setMarker(lineNumber);
        valueInterpreter.setMarker(lineNumber);

        // Get the line code.
        final Character lineCode = codeInterpreter.interpret(
                elements[HoldingFields.LINE_TYPE.getPosition()]);

        // Determine the line type from the code. Is the line type known?
        final HoldingLineType lineType =
                tracker.getAssociation(lineCode);
        if (null == lineType) {

            // The line type is not know. Log a warning.
            logMessage(Level.WARNING, String.format("Line code '%s' is not " +
                            "recognized at line number %d in holding file.",
                    lineCode, lineNumber));
        }

        // Create a new holding description key.
        final HoldingKey key = new HoldingKey(tracker.constructKey(lineCode,
                elements[HoldingFields.KEY.getPosition()]));

        /*
         * Create a new holding description with the key. Set the line type
         * of the description.
         */
        final HoldingDescription description = new HoldingDescription(lineNumber,
                key);
        description.setLineType(lineType);

        /*
         * Check the key of the description against the default key in the
         * library. Try to add the new holding description, receiving any
         * existing description with the same key.
         */
        checkKey(library, description, lineNumber);
        if (null != library.addDescription(description)) {

            /*
             * Log a message describing where the duplicate portfolio
             * description occurs.
             */
            logMessage(getExtraordinary(), String.format("Replacing holding " +
                            "with key '%s' at line number %d in holding file.",
                    description.getKey(), lineNumber));
        }

        /*
         * Set the target in the field processors. Get the number of line
         * elements and the number of holding fields.
         */
        setTarget(description);
        final int elementsLength = elements.length;
        final int numberOfPortfolioFields = HoldingFields.values().length;

        /*
         * The fields-to-process is the minimum of the number of line elements
         * and the number of holding fields.
         */
        final int fieldsToProcess = Math.min(elementsLength,
                numberOfPortfolioFields);

        /*
         * Log a warning if the fields-to-process is less than the number of
         * line elements.
         */
        if (fieldsToProcess < elementsLength) {
            logMessage(Level.WARNING, String.format("There are %d holding " +
                            "line elements but only %d holding fields at " +
                            "line number %d; you might want to check that.",
                    elementsLength, numberOfPortfolioFields, lineNumber));
        }

        /*
         * Or log a different warning if the fields-to-process is less than one
         * less than the number of portfolio fields. We allow the final field
         * (holding value) to be missing, assuming it is the product of shares
         * times price.
         */
        else if (fieldsToProcess < numberOfPortfolioFields - 1) {
            logMessage(Level.WARNING, String.format("There are %d holding " +
                            "fields but only %d holding line elements at " +
                            "line number %d; you might want to check that.",
                    numberOfPortfolioFields, elementsLength, lineNumber));
        }

        // Cycle for each remaining field-to-process, and process it.
        for (int i = getMinimumFields(); i < fieldsToProcess; ++i) {
            processField(i, elements[i], lineNumber);
        }

        // Log some exit information.
        logMessage(getOrdinary(), String.format("Load of metadata for " +
                        "holding with key '%s' at line %d was%s successful.",
                description.getHoldingParentChild().getSecond(), lineNumber,
                hadLineProblem() ? " not" : ""));
    }

    /**
     * Sets the target in the field processors.
     *
     * @param description The target to set in the field processors
     */
    private void setTarget(@NotNull HoldingDescription description) {

        // Set the description in all the processors.
        nameProcessor.setTarget(description);
        sharesProcessor.setTarget(description);
        priceProcessor.setTarget(description);
        valueProcessor.setTarget(description);
    }

    @Override
    protected void startProcessing() {

        /*
         * Call the superclass method and reset the parent tracker. Set the
         * date in the library.
         */
        super.startProcessing();
        tracker.reset();
        setDate(library);
    }
}
