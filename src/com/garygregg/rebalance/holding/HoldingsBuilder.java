package com.garygregg.rebalance.holding;

import com.garygregg.rebalance.*;
import com.garygregg.rebalance.HoldingKey;
import com.garygregg.rebalance.countable.Currency;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HoldingsBuilder extends ElementReader {

    // The holding library instance
    private final HoldingLibrary library = HoldingLibrary.getInstance();

    // The name processor
    private final FieldProcessor<HoldingDescription> nameProcessor =
            new FieldProcessor<>() {

                @Override
                public void processField(@NotNull String field, int lineNumber) {
                    getTarget().setName(processName(preprocessField(field)));
                }
            };

    // The price processor
    private final FieldProcessorIfNotEmpty<HoldingDescription> priceProcessor =
            new FieldProcessorIfNotEmpty<>() {

                @Override
                public void processNotEmptyField(@NotNull String field,
                                                 int lineNumber) {
                    getTarget().setPrice(processFloat(preprocessField(field),
                            1., lineNumber));
                }
            };

    // The shares processor
    private final FieldProcessorIfNotEmpty<HoldingDescription> sharesProcessor =
            new FieldProcessorIfNotEmpty<>() {

                @Override
                public void processNotEmptyField(@NotNull String field,
                                                 int lineNumber) {
                    getTarget().setShares(processFloat(preprocessField(field),
                            0., lineNumber));
                }
            };

    // The parent tracker
    private final ParentTracker tracker = new ParentTracker();

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
                     * Use the current value of the description if the
                     * description if it is not null; otherwise use zero.
                     */
                    final double defaultValue = (null == current) ?
                            Currency.getZero().getValue() : current.getValue();

                    // Set the value of the target using the field.
                    description.setValue(processFloat(preprocessField(field),
                            defaultValue, lineNumber));
                }
            };

    {

        // Assign the logger based on class canonical name.
        setLogger(Logger.getLogger(
                HoldingsBuilder.class.getCanonicalName()));

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
         * is 'ticker'. Otherwise just output the parent itself.
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
                    (processor.hadProblem() ? "with a" : "without"));
        } catch (@NotNull IOException exception) {
            System.err.println(exception.getMessage());
        }
    }

    /**
     * Processes a code.
     *
     * @param code The code
     * @return A processed code
     */
    private static @NotNull Character processCode(@NotNull String code) {
        return code.charAt(0);
    }

    /**
     * Processes a key element.
     *
     * @param key The key element
     * @return A processed key element
     */
    private static @NotNull String processKey(@NotNull String key) {

        // Currently we just return the argument.
        return key;
    }

    /**
     * Processes a name element.
     *
     * @param name The name element
     * @return A processed name element
     */
    private static @NotNull String processName(@NotNull String name) {

        // Currently we just return the argument.
        return name;
    }

    @Override
    public int getMinimumFields() {
        return 2;
    }

    @Override
    protected @NotNull String getPrefix() {
        return "holding";
    }

    @Override
    protected boolean processElements(@NotNull String[] elements,
                                      int lineNumber) {

        // Get the line code.
        final Character lineCode = processCode(preprocessField(
                elements[HoldingFields.LINE_TYPE.getPosition()]));

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
                processKey(preprocessField(
                        elements[HoldingFields.KEY.getPosition()]))));

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

        // Log some information and return the result.
        logMessage(getOrdinary(), String.format("Successfully loaded " +
                        "metadata for holding with key '%s' at line %d.",
                description.getHoldingParentChild().getSecond(), lineNumber));
        return true;
    }

    /**
     * Processes a floating point element.
     *
     * @param element      The floating point element
     * @param defaultValue The value to return if the element is an empty string
     * @param lineNumber   The line number where the floating point element
     *                     occurs
     * @return A processed floating point element
     */
    private double processFloat(@NotNull String element, double defaultValue,
                                int lineNumber) {

        // Declare and initialize the result to a default value.
        double result = defaultValue;
        try {

            /*
             * Use the default value if the element is the empty string.
             * Otherwise parse the allocation as a floating point number. Catch
             * any number format exception that may occur.
             */
            result = element.isEmpty() ? defaultValue :
                    Double.parseDouble(element);
        } catch (@NotNull NumberFormatException exception) {

            /*
             * Log a warning message describing the unparseable floating point
             * element.
             */
            logMessage(Level.WARNING, String.format("Unparseable floating " +
                            "point number '%s' at line number %d in holding file; " +
                            "using default %f instead.", element, lineNumber,
                    defaultValue));
        }

        // Return the result.
        return result;
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

        // Call the superclass method, and set the date in the library.
        super.startProcessing();
        setDate(library);
    }
}
