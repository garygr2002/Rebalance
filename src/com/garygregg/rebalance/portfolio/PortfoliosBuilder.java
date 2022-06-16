package com.garygregg.rebalance.portfolio;

import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.interpreter.DoubleInterpreter;
import com.garygregg.rebalance.interpreter.NonNegativeInterpreter;
import com.garygregg.rebalance.interpreter.PositiveInterpreter;
import com.garygregg.rebalance.toolkit.DateInterpreter;
import com.garygregg.rebalance.toolkit.ElementReader;
import com.garygregg.rebalance.toolkit.FilingStatus;
import com.garygregg.rebalance.toolkit.FilingStatusInterpreter;
import org.jetbrains.annotations.NotNull;

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

    // Our birthdate interpreter
    private final DateInterpreter birthdateInterpreter =
            new DateInterpreter() {

                @Override
                protected void receiveException(@NotNull Exception exception,
                                                @NotNull String string,
                                                Date defaultValue) {
                    logMessage(Level.WARNING, String.format("Birthdate " +
                                    "'%s' at line number %d in portfolio " +
                                    "file cannot be parsed; using %s.", string,
                            getRow(), defaultValue));
                }
            };

    // The birthdate processor
    private final FieldProcessor<PortfolioDescription> birthdateProcessor =
            new FieldProcessor<>() {
                @Override
                public void processField(@NotNull String field) {
                    getTarget().setBirthdate(
                            birthdateInterpreter.interpret(field));
                }
            };

    // Our CPI adjusted monthly income interpreter
    private final DoubleInterpreter cpiInterpreter =
            new DoubleInterpreter() {

                @Override
                protected void receiveException(@NotNull Exception exception,
                                                @NotNull String string,
                                                Double defaultValue) {

                    logMessage(Level.WARNING, String.format("Monthly CPI " +
                                    "adjusted income '%s' at line number " +
                                    "%d in the portfolio file cannot be " +
                                    "parsed; using %s.", string, getRow(),
                            Currency.format(defaultValue)));
                }
            };

    // The CPI adjusted monthly income processor
    private final FieldProcessor<PortfolioDescription> cpiProcessor =
            new FieldProcessor<>() {
                @Override
                public void processField(@NotNull String field) {
                    getTarget().setCpiMonthly(new Currency(
                            cpiInterpreter.interpret(field, 0.)));
                }
            };

    // Our filing status interpreter
    private final FilingStatusInterpreter filingStatusInterpreter =
            new FilingStatusInterpreter() {

                @Override
                protected void receiveException(@NotNull Exception exception,
                                                @NotNull String string,
                                                FilingStatus defaultValue) {
                    logMessage(Level.WARNING, String.format("Filing status " +
                                    "'%s' at line number %d in portfolio file " +
                                    "cannot be parsed; using %s.", string, getRow(),
                            defaultValue));
                }
            };

    // The filing status processor
    private final FieldProcessor<PortfolioDescription> filingStatusProcessor =
            new FieldProcessor<>() {

                @Override
                public void processField(@NotNull String field) {
                    getTarget().setFilingStatus(
                            filingStatusInterpreter.interpret(field));
                }
            };

    // Our increase-at-bear interpreter
    private final DoubleInterpreter increaseAtBearInterpreter =
            new PositiveInterpreter() {

                @Override
                protected void receiveException(@NotNull Exception exception,
                                                @NotNull String string,
                                                Double defaultValue) {
                    logMessage(Level.WARNING, String.format("Equity " +
                                    "increase percentage for bear market " +
                                    "'%s' at line number %d in portfolio " +
                                    "file cannot be parsed; using %s.", string,
                            getRow(), defaultValue));
                }
            };

    // The increase-at-bear processor
    private final FieldProcessor<PortfolioDescription>
            increaseAtBearProcessor = new FieldProcessor<>() {

        @Override
        public void processField(@NotNull String field) {
            getTarget().setIncreaseAtBear(
                    increaseAtBearInterpreter.interpret(field));
        }
    };

    // Our increase-at-zero interpreter
    private final DoubleInterpreter increaseAtZeroInterpreter =
            new PositiveInterpreter() {

                @Override
                protected void receiveException(@NotNull Exception exception,
                                                @NotNull String string,
                                                Double defaultValue) {
                    logMessage(Level.WARNING, String.format("Equity " +
                                    "increase percentage for zero market " +
                                    "'%s' at line number %d in portfolio " +
                                    "file cannot be parsed; using %s.", string,
                            getRow(), defaultValue));
                }
            };

    // The increase-at-zero processor
    private final FieldProcessor<PortfolioDescription>
            increaseAtZeroProcessor = new FieldProcessor<>() {

        @Override
        public void processField(@NotNull String field) {
            getTarget().setIncreaseAtZero(
                    increaseAtZeroInterpreter.interpret(field));
        }
    };

    // The portfolio library instance
    private final PortfolioLibrary library = PortfolioLibrary.getInstance();

    // Our projected mortality date interpreter
    private final DateInterpreter mortalityDateInterpreter =
            new DateInterpreter() {

                @Override
                protected void receiveException(@NotNull Exception exception,
                                                @NotNull String string,
                                                Date defaultValue) {
                    logMessage(Level.WARNING, String.format("Mortality " +
                                    "date '%s' at line number %d in " +
                                    "portfolio file cannot be parsed; " +
                                    "using %s.", string, getRow(),
                            defaultValue));
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

    // Our non-CPI adjusted monthly income interpreter
    private final DoubleInterpreter nonCpiInterpreter =
            new DoubleInterpreter() {

                @Override
                protected void receiveException(@NotNull Exception exception,
                                                @NotNull String string,
                                                Double defaultValue) {

                    logMessage(Level.WARNING, String.format("Monthly " +
                                    "non-CPI adjusted income '%s' at line " +
                                    "number %d in the portfolio file cannot " +
                                    "be parsed; using %s.", string, getRow(),
                            Currency.format(defaultValue)));
                }
            };

    // The non-CPI adjusted monthly income processor
    private final FieldProcessor<PortfolioDescription> nonCpiProcessor =
            new FieldProcessor<>() {
                @Override
                public void processField(@NotNull String field) {
                    getTarget().setNonCpiMonthly(new Currency(
                            nonCpiInterpreter.interpret(field, 0.)));
                }
            };

    // A map of element positions to portfolio fields
    private final Map<Integer, PortfolioFields> positionMap = new HashMap<>();

    // Our Social Security monthly income interpreter
    private final DoubleInterpreter socialSecurityInterpreter =
            new DoubleInterpreter() {

                @Override
                protected void receiveException(@NotNull Exception exception,
                                                @NotNull String string,
                                                Double defaultValue) {
                    logMessage(Level.WARNING, String.format("Monthly " +
                                    "Social Security income '%s' at line " +
                                    "number %d in the portfolio file cannot " +
                                    "be parsed; using %s.", string, getRow(),
                            Currency.format(defaultValue)));
                }
            };

    // The Social Security monthly income processor
    private final FieldProcessor<PortfolioDescription>
            socialSecurityProcessor = new FieldProcessor<>() {

        @Override
        public void processField(@NotNull String field) {
            getTarget().setSocialSecurityMonthly(new Currency(
                    socialSecurityInterpreter.interpret(field, 0.)));
        }
    };

    // Our taxable annual income interpreter
    private final DoubleInterpreter taxableInterpreter =
            new DoubleInterpreter() {
                @Override
                protected void receiveException(@NotNull Exception exception,
                                                @NotNull String string,
                                                Double defaultValue) {
                    logMessage(Level.WARNING, String.format("Taxable income " +
                                    "'%s' at line number %d in portfolio " +
                                    "file cannot be parsed; using %s.", string,
                            getRow(), Currency.format(defaultValue)));
                }
            };

    // The taxable annual income processor
    private final FieldProcessor<PortfolioDescription> taxableProcessor =
            new FieldProcessor<>() {
                @Override
                public void processField(@NotNull String field) {
                    getTarget().setTaxableAnnual(
                            new Currency(taxableInterpreter.interpret(field,
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

        // Add the birthdate, mortality date, and filing status processors.
        addFieldProcessor(++fieldIndex, birthdateProcessor);
        addFieldProcessor(++fieldIndex, mortalityDateProcessor);
        addFieldProcessor(++fieldIndex, filingStatusProcessor);

        // Add the Social Security and CPI adjusted income processors.
        addFieldProcessor(++fieldIndex, socialSecurityProcessor);
        addFieldProcessor(++fieldIndex, cpiProcessor);

        /*
         * Add the non-CPI adjusted income processor and the taxable income
         * processor.
         */
        addFieldProcessor(++fieldIndex, nonCpiProcessor);
        addFieldProcessor(++fieldIndex, taxableProcessor);

        // Cycle for each allocation processor.
        for (MyAllocationProcessor processor : allocationProcessors) {

            /*
             * Increment and set the first/next column in the field processor.
             * Add the processor.
             */
            processor.setColumn(++fieldIndex);
            addFieldProcessor(fieldIndex, processor);
        }

        // Add the adjustment processors.
        addFieldProcessor(++fieldIndex, increaseAtZeroProcessor);
        addFieldProcessor(++fieldIndex, increaseAtBearProcessor);
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
    protected void processElements(@NotNull String @NotNull [] elements,
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
            logMessage(getInformationLevel(), String.format("Replacing " +
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
         * number of portfolio fields (minus the optional equity increase
         * fields).
         */
        else if (fieldsToProcess < numberOfPortfolioFields - 2) {
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
                        "portfolio with mnemonic '%s' at line %d was%s " +
                        "successful.", description.getKey(), lineNumber,
                hadLineProblem() ? " not" : ""));
    }

    @Override
    protected void setLineNumber(int lineNumber) {

        /*
         * Set the line number as the row in the birthdate interpreter, the
         * projected mortality date interpreter, and the filing status
         * interpreter.
         */
        birthdateInterpreter.setRow(lineNumber);
        mortalityDateInterpreter.setRow(lineNumber);
        filingStatusInterpreter.setRow(lineNumber);

        /*
         * Set the line number as the row in the Social Security income
         * interpreter, the CPI income interpreter, and the non-CPI income
         * interpreter.
         */
        socialSecurityInterpreter.setRow(lineNumber);
        cpiInterpreter.setRow(lineNumber);
        nonCpiInterpreter.setRow(lineNumber);

        /*
         * Set the line number as the row in the taxable income interpreter.
         * Cycle for each allocation processor.
         */
        taxableInterpreter.setRow(lineNumber);
        for (MyAllocationProcessor processor : allocationProcessors) {

            /*
             * Set the line number as the row in the first/next allocation
             * processor.
             */
            processor.setRow(lineNumber);
        }

        // Set the line number as the row in the increase interpreters.
        increaseAtZeroInterpreter.setRow(lineNumber);
        increaseAtBearInterpreter.setRow(lineNumber);
    }

    @Override
    protected void setTarget(@NotNull PortfolioDescription description) {

        // Set the target for the increase processors.
        increaseAtBearProcessor.setTarget(description);
        increaseAtZeroProcessor.setTarget(description);

        /*
         * Cycle for each allocation processor, and set the target for the
         * first/next allocation processor.
         */
        for (MyAllocationProcessor processor : allocationProcessors) {
            processor.setTarget(description);
        }

        /*
         * Set the target for the taxable income processor and the non-CPI
         * income processor.
         */
        taxableProcessor.setTarget(description);
        nonCpiProcessor.setTarget(description);

        /*
         * Set the target for the CPI income processor and the Social Security
         * income processor.
         */
        cpiProcessor.setTarget(description);
        socialSecurityProcessor.setTarget(description);

        /*
         * Set the target for the filing status processor, and the mortality
         * date processor.
         */
        filingStatusProcessor.setTarget(description);
        mortalityDateProcessor.setTarget(description);

        // Set the target for the birthday processor and the name processor.
        birthdateProcessor.setTarget(description);
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
        private final DoubleInterpreter interpreter =
                new NonNegativeInterpreter() {

                    @Override
                    protected void receiveException(@NotNull Exception exception,
                                                    @NotNull String string,
                                                    Double defaultValue) {
                        logMessage(Level.WARNING, String.format("Allocation " +
                                        "'%s' at line number %d, column " +
                                        "number %d in the portfolio file " +
                                        "cannot be parsed; using %s.", string,
                                getRow(), getColumn(), defaultValue));
                    }
                };

        /**
         * Gets the column.
         *
         * @return The column
         */
        protected int getColumn() {
            return interpreter.getColumn();
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
            interpreter.setColumn(column);
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
