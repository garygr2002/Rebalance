package com.garygregg.rebalance.portfolio;

import com.garygregg.rebalance.DateInterpreter;
import com.garygregg.rebalance.DateUtilities;
import com.garygregg.rebalance.ElementReader;
import com.garygregg.rebalance.WeightType;
import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.interpreter.BooleanInterpreter;
import com.garygregg.rebalance.interpreter.DoubleInterpreter;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PortfoliosBuilder extends ElementReader<PortfolioDescription> {

    // The allocation processors
    private final PortfoliosBuilder.MyAllocationProcessor[]
            allocationProcessors = {
            new PortfoliosBuilder.MyAllocationProcessor(),
            new PortfoliosBuilder.MyAllocationProcessor(),
            new PortfoliosBuilder.MyAllocationProcessor(),
            new PortfoliosBuilder.MyAllocationProcessor()
    };

    // Our annual income interpreter
    private final DoubleInterpreter annualIncomeInterpreter =
            new DoubleInterpreter() {
                @Override
                protected void receiveException(@NotNull Exception exception,
                                                @NotNull String string,
                                                Double defaultValue) {
                    logMessage(Level.WARNING, String.format("Unparseable " +
                                    "annual income '%s' at line number %d " +
                                    "in portfolio file; using %s.",
                            string, getRow(), Currency.format(defaultValue)));
                }
            };

    // Our birthdate interpreter
    private final DateInterpreter birthdateInterpreter =
            new DateInterpreter() {

                @Override
                protected void receiveException(@NotNull Exception exception,
                                                @NotNull String string,
                                                Date defaultValue) {
                    logMessage(Level.WARNING, String.format("Unparseable " +
                                    "birthdate '%s' at line number %d in " +
                                    "portfolio file; using %s.",
                            string, getRow(), defaultValue));
                }
            };

    // The birthdate processor
    private final FieldProcessor<PortfolioDescription> birthDateProcessor =
            new FieldProcessor<>() {
                @Override
                public void processField(@NotNull String field) {
                    getTarget().setBirthDate(
                            birthdateInterpreter.interpret(field));
                }
            };

    // Our CPI adjusted flag interpreter
    private final BooleanInterpreter cpiAdjustedInterpreter =
            new BooleanInterpreter() {

                @Override
                protected void receiveException(@NotNull Exception exception,
                                                @NotNull String string,
                                                Boolean defaultValue) {

                    logMessage(Level.WARNING, String.format("Unparseable " +
                                    "CPI adjusted flag '%s' at line number " +
                                    "%d in portfolio file; using %s.",
                            string, getRow(), defaultValue));
                }
            };

    // The CPI adjusted flag processor
    private final FieldProcessor<PortfolioDescription> cpiAdjustedProcessor =
            new FieldProcessor<>() {
                @Override
                public void processField(@NotNull String field) {
                    getTarget().setCpiAdjusted(
                            cpiAdjustedInterpreter.interpret(field, false));
                }
            };

    // The portfolio library instance
    private final PortfolioLibrary library = PortfolioLibrary.getInstance();

    // Our monthly social security income interpreter
    private final DoubleInterpreter monthlySsiInterpreter =
            new DoubleInterpreter() {

                @Override
                protected void receiveException(@NotNull Exception exception,
                                                @NotNull String string,
                                                Double defaultValue) {
                    logMessage(Level.WARNING, String.format("Unparseable " +
                                    "monthly social security income '%s' at " +
                                    "line number %d in portfolio file; " +
                                    "using %s.",
                            string, getRow(), Currency.format(defaultValue)));
                }
            };

    // Our mortality date interpreter
    private final DateInterpreter mortalityDateInterpreter =
            new DateInterpreter() {

                @Override
                protected void receiveException(@NotNull Exception exception,
                                                @NotNull String string,
                                                Date defaultValue) {
                    logMessage(Level.WARNING, String.format("Unparseable " +
                                    "mortality date '%s' at line number %d " +
                                    "in portfolio file; using %s.",
                            string, getRow(), defaultValue));
                }
            };

    // The projected mortality date processor
    private final FieldProcessor<PortfolioDescription> mortalityDateProcessor =
            new FieldProcessor<>() {
                @Override
                public void processField(@NotNull String field) {
                    getTarget().setMortalityDate(
                            mortalityDateInterpreter.interpret(field));
                }
            };

    // The name processor
    private final FieldProcessor<PortfolioDescription> nameProcessor =
            new FieldProcessor<>() {

                @Override
                public void processField(@NotNull String field) {
                    getTarget().setName(field);
                }
            };

    // Our other monthly income interpreter
    private final DoubleInterpreter otherMonthlyInterpreter =
            new DoubleInterpreter() {

                @Override
                protected void receiveException(@NotNull Exception exception,
                                                @NotNull String string,
                                                Double defaultValue) {
                    logMessage(Level.WARNING, String.format("Unparseable " +
                                    "other monthly income '%s' at line " +
                                    "number %d in portfolio file; using " +
                                    "%s.",
                            string, getRow(), Currency.format(defaultValue)));
                }
            };

    // The other monthly annuity income processor
    private final FieldProcessor<PortfolioDescription> otherMonthlyProcessor =
            new FieldProcessor<>() {
                @Override
                public void processField(@NotNull String field) {
                    getTarget().setOtherMonthly(new Currency(
                            otherMonthlyInterpreter.interpret(field, 0.)));
                }
            };

    // A map of element positions to portfolio fields
    private final Map<Integer, PortfolioFields> positionMap = new HashMap<>();

    // The monthly Social Security monthly income processor
    private final FieldProcessor<PortfolioDescription>
            socialSecurityMonthlyProcessor = new FieldProcessor<>() {

        @Override
        public void processField(@NotNull String field) {
            getTarget().setSocialSecurityMonthly(new Currency(
                    monthlySsiInterpreter.interpret(field, 0.)));
        }
    };

    // The taxable annual income processor
    private final FieldProcessor<PortfolioDescription> taxableAnnualProcessor =
            new FieldProcessor<>() {
                @Override
                public void processField(@NotNull String field) {
                    getTarget().setTaxableAnnual(
                            new Currency(annualIncomeInterpreter.interpret(field,
                                    0.)));
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

        // Add the birthdate and mortality date processors.
        addFieldProcessor(++fieldIndex, birthDateProcessor);
        addFieldProcessor(++fieldIndex, mortalityDateProcessor);

        // Add the Social Security and other monthly annuity income processors.
        addFieldProcessor(++fieldIndex, socialSecurityMonthlyProcessor);
        addFieldProcessor(++fieldIndex, otherMonthlyProcessor);

        // Add the CPI adjusted flag process and the taxable income processor.
        addFieldProcessor(++fieldIndex, cpiAdjustedProcessor);
        addFieldProcessor(++fieldIndex, taxableAnnualProcessor);

        // Cycle for each allocation processor.
        for (MyAllocationProcessor processor : allocationProcessors) {

            /*
             * Increment and set the first/next column in the field processor.
             * Add the processor.
             */
            processor.setColumn(++fieldIndex);
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
            final ElementReader<?> processor = new PortfoliosBuilder();
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
                                "Mortality date: %s;%nMonthly Social " +
                                "Security: %s; Other monthly annuity: %s; " +
                                "Annuity CPI adjusted: %b;%nTaxable annual " +
                                "income: %s;%nStock: %f, Bond: %f, " +
                                "Cash: %f, Real estate: %f.%n%n",
                        ++portfolio,
                        description.getKey(),
                        description.getName(),
                        DateUtilities.format(description.getBirthDate()),
                        DateUtilities.format(description.getMortalityDate()),
                        description.getSocialSecurityMonthly(),
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
                    (processor.hadFileProblem() ? "with a" : "without"));
        } catch (@NotNull IOException exception) {
            System.err.println(exception.getMessage());
        }
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

    @Override
    protected void processElements(@NotNull String[] elements,
                                   int lineNumber) {

        /*
         * Set the line number, and create a new portfolio description with
         * the index.
         */
        setLineNumber(lineNumber);
        final PortfolioDescription description = new PortfolioDescription(
                elements[PortfolioFields.MNEMONIC.getPosition()]);

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
            processField(i, elements[i]);
        }

        // Log some exit information.
        logMessage(getOrdinary(), String.format("Load of metadata for " +
                        "portfolio with mnemonic '%s' at line %d was%s successful.",
                description.getKey(), lineNumber,
                hadLineProblem() ? " not" : ""));
    }

    @Override
    protected void setLineNumber(int lineNumber) {

        /*
         * Set the line number as the row in the birthdate interpreter and the
         * projected mortality date interpreter.
         */
        birthdateInterpreter.setRow(lineNumber);
        mortalityDateInterpreter.setRow(lineNumber);

        /*
         * Set the line number as the row in the monthly social security income
         * interpreter, the other monthly income interpreter, and the CPI
         * adjusted interpreter.
         */
        monthlySsiInterpreter.setRow(lineNumber);
        otherMonthlyInterpreter.setRow(lineNumber);
        cpiAdjustedInterpreter.setRow(lineNumber);

        /*
         * Set the line number as the row in the annual income interpreter.
         * Cycle for each allocation processor.
         */
        annualIncomeInterpreter.setRow(lineNumber);
        for (MyAllocationProcessor processor : allocationProcessors) {

            /*
             * Set the line number as the row in the first/next allocation
             * processor.
             */
            processor.setRow(lineNumber);
        }
    }

    @Override
    protected void setTarget(@NotNull PortfolioDescription description) {

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
         * the monthly Social Security income processor.
         */
        otherMonthlyProcessor.setTarget(description);
        socialSecurityMonthlyProcessor.setTarget(description);

        /*
         * Set the target for the mortality date processor, the birthdate
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

        // Our allocation interpreter
        private final DoubleInterpreter interpreter = new DoubleInterpreter() {

            @Override
            protected void receiveException(@NotNull Exception exception,
                                            @NotNull String string,
                                            Double defaultValue) {
                logMessage(Level.WARNING, String.format("Unparseable " +
                                "allocation '%s' at line number %d, " +
                                "column number %d in portfolio file; using " +
                                "%s.",
                        string, getRow(), getColumn(), defaultValue));
            }
        };

        // The column for allocation fields
        private int column;

        /**
         * Gets the column.
         *
         * @return The column
         */
        protected int getColumn() {
            return column;
        }

        @Override
        public void processField(@NotNull String field) {
            getTarget().adjustAllocation(positionMap.get(
                    getColumn()).getType(), interpreter.interpret(field, 0.));
        }

        /**
         * Sets the column.
         *
         * @param column The column
         */
        public void setColumn(int column) {
            interpreter.setColumn(this.column = column);
        }

        /**
         * Sets the row.
         *
         * @param row The row
         */
        public void setRow(int row) {
            interpreter.setRow(row);
        }
    }
}
