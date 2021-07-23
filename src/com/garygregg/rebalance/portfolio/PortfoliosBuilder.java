package com.garygregg.rebalance.portfolio;

import com.garygregg.rebalance.DateUtilities;
import com.garygregg.rebalance.ElementReader;
import com.garygregg.rebalance.WeightType;
import com.garygregg.rebalance.countable.Currency;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PortfoliosBuilder extends ElementReader {

    // The allocation processors
    private final PortfoliosBuilder.MyAllocationProcessor[]
            allocationProcessors = {
            new PortfoliosBuilder.MyAllocationProcessor(),
            new PortfoliosBuilder.MyAllocationProcessor(),
            new PortfoliosBuilder.MyAllocationProcessor(),
            new PortfoliosBuilder.MyAllocationProcessor()
    };

    // The birth date processor
    private final FieldProcessor<PortfolioDescription> birthDateProcessor =
            new FieldProcessor<>() {
                @Override
                public void processField(@NotNull String field,
                                         int lineNumber) {
                    getTarget().setBirthDate(processDate(
                            preprocessField(field), lineNumber));
                }
            };

    // The CPI adjusted flag processor
    private final FieldProcessor<PortfolioDescription> cpiAdjustedProcessor =
            new FieldProcessor<PortfolioDescription>() {
                @Override
                public void processField(@NotNull String field, int lineNumber) {
                    getTarget().setCpiAdjusted(processBoolean(
                            preprocessField(field), lineNumber));
                }
            };

    // The portfolio library instance
    private final PortfolioLibrary library = PortfolioLibrary.getInstance();

    // The projected mortality date processor
    private final FieldProcessor<PortfolioDescription> mortalityDateProcessor =
            new FieldProcessor<>() {
                @Override
                public void processField(@NotNull String field, int lineNumber) {
                    getTarget().setMortalityDate(processDate(
                            preprocessField(field), lineNumber));
                }
            };

    // The name processor
    private final FieldProcessor<PortfolioDescription> nameProcessor =
            new FieldProcessor<>() {

                @Override
                public void processField(@NotNull String field,
                                         int lineNumber) {
                    getTarget().setName(processName(preprocessField(field)));
                }
            };

    // The other monthly annuity income processor
    private final FieldProcessor<PortfolioDescription> otherMonthlyProcessor =
            new FieldProcessor<PortfolioDescription>() {
                @Override
                public void processField(@NotNull String field, int lineNumber) {
                    getTarget().setOtherMonthly(new Currency(processFloat(
                            preprocessField(field), 0., lineNumber)));
                }
            };

    // A map of element positions to portfolio fields
    private final Map<Integer, PortfolioFields> positionMap = new HashMap<>();

    // The monthly SSN income processor
    private final FieldProcessor<PortfolioDescription> ssnMonthlyProcessor =
            new FieldProcessor<PortfolioDescription>() {
                @Override
                public void processField(@NotNull String field,
                                         int lineNumber) {
                    getTarget().setSsnMonthly(new Currency(processFloat(
                            preprocessField(field), 0., lineNumber)));
                }
            };

    // The taxable annual income processor
    private final FieldProcessor<PortfolioDescription> taxableAnnualProcessor =
            new FieldProcessor<PortfolioDescription>() {
                @Override
                public void processField(@NotNull String field,
                                         int lineNumber) {
                    getTarget().setTaxableAnnual(new Currency(processFloat(
                            preprocessField(field), 0., lineNumber)));
                }
            };

    {

        // Cycle for each portfolio field.
        for (PortfolioFields field : PortfolioFields.values()) {

            // Map the position of the field to the field itself.
            positionMap.put(field.getPosition(), field);
        }

        // Initialize a field index, and add the portfolio name processor.
        int fieldIndex = 0;
        addFieldProcessor(++fieldIndex, nameProcessor);

        // Add the birth date and mortality date processors.
        addFieldProcessor(++fieldIndex, birthDateProcessor);
        addFieldProcessor(++fieldIndex, mortalityDateProcessor);

        // Add the SSN and other monthly annuity income processors.
        addFieldProcessor(++fieldIndex, ssnMonthlyProcessor);
        addFieldProcessor(++fieldIndex, otherMonthlyProcessor);

        // Add the CPI adjusted flag process and the taxable income processor.
        addFieldProcessor(++fieldIndex, cpiAdjustedProcessor);
        addFieldProcessor(++fieldIndex, taxableAnnualProcessor);

        // Cycle for each allocation processor.
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
            final ElementReader processor = new PortfoliosBuilder();
            processor.readLines(new Date());

            // The holding library should now be populated. Print its date.
            final PortfolioLibrary library = PortfolioLibrary.getInstance();
            System.out.printf("The date of the library is: %s.%n",
                    DateUtilities.format(library.getDate()));

            // Cycle for each portfolio description in the library.
            int portfolio = 0;
            for (PortfolioDescription description : library.getCatalog()) {

                // Display statistics for the first/next portfolio description.
                System.out.printf("Portfolio (%d) '%s' with name '%s' now " +
                                "loaded;%nBirth date: %s; " +
                                "Mortality date: %s;%nMonthly SSN: %s; Other " +
                                "monthly annuity: %s; " +
                                "Annuity CPI adjusted: %b;%nTaxable annual " +
                                "income: %s;%nStock: %f, Bond: %f, " +
                                "Cash: %f, Real estate: %f.%n%n",
                        ++portfolio,
                        description.getKey(),
                        description.getName(),
                        DateUtilities.format(description.getBirthDate()),
                        DateUtilities.format(description.getMortalityDate()),
                        description.getSsnMonthly(),
                        description.getOtherMonthly(),
                        description.getCpiAdjusted(),
                        description.getTaxableAnnual(),
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
    private static @NotNull String processMnemonic(@NotNull String mnemonic) {
        return mnemonic;
    }

    /**
     * Processes a name.
     *
     * @param name The name to process
     * @return A processed name
     */
    private static @NotNull String processName(@NotNull String name) {
        return name;
    }

    @Override
    public int getMinimumFields() {
        return 1;
    }

    @Override
    public @NotNull String getPrefix() {
        return "portfolio";
    }

    @Override
    protected @NotNull Logger getReadingLogger() {
        return Logger.getLogger(PortfoliosBuilder.class.getCanonicalName());
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

            // Parse the allocation as a floating point number.
            result = Double.parseDouble(allocation);
        }

        // Catch any number format exception that may occur.
        catch (@NotNull NumberFormatException exception) {

            // Log a warning message describing the unparseable allocation.
            logMessage(Level.WARNING, String.format("Unparseable allocation " +
                            "'%s' at line number %d in portfolio file; using null",
                    allocation, lineNumber));
        }

        // Return the result.
        return result;
    }

    /**
     * Process a boolean.
     *
     * @param aBoolean   The boolean to process
     * @param lineNumber The line number where the boolean element occurs
     * @return A processed boolean
     */
    private boolean processBoolean(@NotNull String aBoolean, int lineNumber) {

        /*
         * Parse the argument as a boolean. Is the string representation of
         * the result not equal to the lowercase translation of the argument?
         */
        final boolean result = Boolean.parseBoolean(aBoolean);
        if (!Boolean.toString(result).equals(aBoolean.toLowerCase())) {

            /*
             * The string representation of the result is not equal to the
             * lowercase translation of the argument. This means the argument
             * was not a proper boolean representation. Log a warning.
             */
            logMessage(Level.WARNING, String.format("Unparseable boolean " +
                            "'%s' at line number %d in portfolio file; using %s.",
                    aBoolean, lineNumber, result));
        }

        // Return the result.
        return result;
    }

    /**
     * Processes a date.
     *
     * @param date       The date to process
     * @param lineNumber The line number where the date element occurs
     * @return A processed date
     */
    private Date processDate(@NotNull String date, int lineNumber) {

        // Declare and initialize the result to a default value.
        Date parsedDate = null;
        try {

            // Try to parse the date.
            parsedDate = DateUtilities.parse(date);
        }

        // Catch any parse exception that may occur.
        catch (@NotNull ParseException exception) {
            logMessage(Level.WARNING, String.format("Unparseable date '%s' at " +
                            "line number %d in portfolio file; using null.", date,
                    lineNumber));
        }

        // Return the result.
        return parsedDate;
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
                            "point number '%s' at line number %d in " +
                            "portfolio file; using default %f instead.",
                    element, lineNumber, defaultValue));
        }

        // Return the result.
        return result;
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

        /*
         * Set the target for the taxable annual income processor and the CPI
         * adjusted flag processor.
         */
        taxableAnnualProcessor.setTarget(description);
        cpiAdjustedProcessor.setTarget(description);

        /*
         * Set the target for the other monthly annuity income processor and
         * the monthly SSN income processor.
         */
        otherMonthlyProcessor.setTarget(description);
        ssnMonthlyProcessor.setTarget(description);

        /*
         * Set the target for the mortality date processor, the birth date
         * processor and the name processor.
         */
        mortalityDateProcessor.setTarget(description);
        birthDateProcessor.setTarget(description);
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
