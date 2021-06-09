package com.garygregg.rebalance.portfolio;

import com.garygregg.rebalance.DateUtilities;
import com.garygregg.rebalance.ElementProcessor;
import com.garygregg.rebalance.WeightType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PortfoliosBuilder extends ElementProcessor {

    // The allocation processors
    private final PortfoliosBuilder.MyAllocationProcessor[]
            allocationProcessors = {
            new PortfoliosBuilder.MyAllocationProcessor(),
            new PortfoliosBuilder.MyAllocationProcessor(),
            new PortfoliosBuilder.MyAllocationProcessor(),
            new PortfoliosBuilder.MyAllocationProcessor()
    };

    // The portfolio library instance
    private final PortfolioLibrary library = PortfolioLibrary.getInstance();

    // The name processor
    private final FieldProcessor<PortfolioDescription> nameProcessor =
            new FieldProcessor<>() {

                @Override
                public void processField(@NotNull String field, int lineNumber) {
                    getTarget().setName(processName(preprocessField(field)));
                }
            };

    // A map of element positions to portfolio fields
    private final Map<Integer, PortfolioFields> positionMap = new HashMap<>();

    {

        // Assign the logger based on class canonical name.
        setLogger(Logger.getLogger(
                PortfoliosBuilder.class.getCanonicalName()));

        // Cycle for each portfolio field.
        for (PortfolioFields field : PortfolioFields.values()) {

            // Map the position of the field to the field itself.
            positionMap.put(field.getPosition(), field);
        }

        /*
         * Initialize a field index, and add the portfolio name processor.
         * Cycle for each allocation processor.
         */
        int fieldIndex = 0;
        addFieldProcessor(++fieldIndex, nameProcessor);
        for (MyAllocationProcessor processor : allocationProcessors) {

            /*
             * Increment and set the first/next index in the field processor.
             * Add the processor.
             */
            processor.setIndex(++fieldIndex);
            addFieldProcessor(fieldIndex, processor);
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
            final ElementProcessor processor = new PortfoliosBuilder();
            processor.readLines(new Date());

            // The holding library should now be populated. Print its date.
            final PortfolioLibrary library = PortfolioLibrary.getInstance();
            System.out.printf("The date of the library is: %s.%n",
                    DateUtilities.format(library.getDate()));

            // Cycle for each portfolio description in the library.
            for (PortfolioDescription description : library.getCatalog()) {

                // Display statistics for the first/next portfolio description.
                System.out.printf("Portfolio '%s' with name '%s' now " +
                                "loaded; Stock: %f, Bond: %f, Cash: %f, " +
                                "Real estate: %f.%n", description.getKey(),
                        description.getName(),
                        description.getAllocation(WeightType.STOCK),
                        description.getAllocation(WeightType.BOND),
                        description.getAllocation(WeightType.CASH),
                        description.getAllocation(WeightType.REAL_ESTATE));
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
     * Processes a mnemonic.
     *
     * @param mnemonic The mnemonic to process
     * @return The processed mnemonic
     */
    private static String processMnemonic(@NotNull String mnemonic) {
        return mnemonic;
    }

    /**
     * Processes a name.
     *
     * @param name The name to process
     * @return A processed name
     */
    private static String processName(@NotNull String name) {
        return name;
    }

    @Override
    public int getMinimumFields() {
        return 1;
    }

    @Override
    protected @NotNull String getPrefix() {
        return "portfolio";
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
                            "'%s' at line number %d in portfolio file; using null",
                    allocation, lineNumber));
        }

        // Return the result.
        return result;
    }

    @Override
    protected boolean processElements(@NotNull String[] elements,
                                      int lineNumber) {

        // Create a new portfolio description with the index.
        final PortfolioDescription description = new PortfolioDescription(
                processMnemonic(preprocessField(
                        elements[PortfolioFields.MNEMONIC.getPosition()])));

        /*
         * Check the key of the description against the default key in the
         * library. Try to add the new portfolio description, receiving any
         * existing description with the same key.
         */
        checkKey(library, description, lineNumber);
        if (null != library.addDescription(description)) {

            /*
             * Log a message describing where the duplicate portfolio
             * description occurs.
             */
            logMessage(getExtraordinary(), String.format("Replacing " +
                    "portfolio with mnemonic '%s' at line number %d in " +
                    "portfolio file.", description.getKey(), lineNumber));
        }

        /*
         * Set the target in the field processors. Get the number of line
         * elements and the number of portfolio fields.
         */
        setTarget(description);
        final int elementsLength = elements.length;
        final int numberOfPortfolioFields = PortfolioFields.values().length;

        /*
         * The fields-to-process is the minimum of the number of line elements
         * and the number of portfolio fields.
         */
        final int fieldsToProcess = Math.min(elementsLength,
                numberOfPortfolioFields);

        /*
         * Log a warning if the fields-to-process is less than the number of
         * line elements.
         */
        if (fieldsToProcess < elementsLength) {
            logMessage(Level.WARNING, String.format("There are %d portfolio " +
                            "line elements but only %d portfolio fields at " +
                            "line number %d; you might want to check that.",
                    elementsLength, numberOfPortfolioFields, lineNumber));
        }

        /*
         * Or log a different warning if the fields-to-process is less than the
         * number of portfolio fields.
         */
        else if (fieldsToProcess < numberOfPortfolioFields) {
            logMessage(Level.WARNING, String.format("There are %d portfolio " +
                            "fields but only %d portfolio line elements at " +
                            "line number %d; you might want to check that.",
                    numberOfPortfolioFields, elementsLength, lineNumber));
        }

        // Cycle for each remaining field-to-process, and process it.
        for (int i = getMinimumFields(); i < fieldsToProcess; ++i) {
            processField(i, elements[i], lineNumber);
        }

        // Log some information and return the result.
        logMessage(getOrdinary(), String.format("Successfully loaded " +
                        "metadata for portfolio with mnemonic '%s' at line " +
                        "%d.", description.getKey(), lineNumber));
        return true;
    }

    /**
     * Sets the target in the field processors.
     *
     * @param description The target to set in the field processors
     */
    private void setTarget(@NotNull PortfolioDescription description) {

        // Cycle for each allocation processor and set its target.
        for (MyAllocationProcessor processor : allocationProcessors) {
            processor.setTarget(description);
        }

        // Set the target in the name processor.
        nameProcessor.setTarget(description);
    }

    @Override
    protected void startProcessing() {

        // Call the superclass method, and set the date in the library.
        super.startProcessing();
        setDate(library);
    }

    /**
     * An allocation processor for PortfolioDescription targets.
     */
    private class MyAllocationProcessor extends
            FieldProcessor<PortfolioDescription> {

        // The index for allocation fields
        private Integer index;

        /**
         * Builds the processor.
         *
         * @param index An index for the allocation field
         */
        public MyAllocationProcessor(Integer index) {
            setIndex(index);
        }

        /**
         * Builds the processor with a default allocation field index.
         */
        public MyAllocationProcessor() {
            this(null);
        }

        /**
         * Gets the index for allocation fields.
         *
         * @return The index for allocation fields
         */
        public Integer getIndex() {
            return index;
        }

        @Override
        public void processField(@NotNull String field, int lineNumber) {
            getTarget().adjustAllocation(positionMap.get(getIndex()).getType(),
                    processAllocation(preprocessField(field), lineNumber));
        }

        /**
         * Sets the index for subcode fields.
         *
         * @param index The index for subcode fields
         */
        public void setIndex(Integer index) {
            this.index = index;
        }
    }
}
