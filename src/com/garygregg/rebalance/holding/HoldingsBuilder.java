package com.garygregg.rebalance.holding;

import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.countable.Shares;
import com.garygregg.rebalance.interpreter.CodeInterpreter;
import com.garygregg.rebalance.interpreter.DoubleInterpreter;
import com.garygregg.rebalance.interpreter.NonNegativeInterpreter;
import com.garygregg.rebalance.toolkit.*;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

abstract class HoldingsBuilder extends ElementReader<HoldingDescription> {

    // Our code interpreter
    private final CodeInterpreter codeInterpreter = new CodeInterpreter();

    // The name processor
    private final FieldProcessor<HoldingDescription> nameProcessor =
            new FieldProcessor<>() {

                @Override
                public void processField(@NotNull String field) {
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
                    logMessage(Level.WARNING, String.format("Price '%s' at " +
                                    "line number %d in the holding file " +
                                    "cannot be parsed; using %s.", string,
                            getRow(), Currency.format(defaultValue)));
                }
            };

    // The price processor
    private final FieldProcessorIfNotEmpty<HoldingDescription> priceProcessor =
            new FieldProcessorIfNotEmpty<>() {

                @Override
                public void processNotEmptyField(@NotNull String field) {
                    getTarget().setPrice(priceInterpreter.interpret(field,
                            1.));
                }
            };

    // Our shares interpreter
    private final DoubleInterpreter sharesInterpreter =
            new NonNegativeInterpreter() {

                @Override
                protected void receiveException(@NotNull Exception exception,
                                                @NotNull String string,
                                                Double defaultValue) {
                    logMessage(Level.WARNING, String.format("Shares '%s' " +
                                    "at line number %d in the holding file " +
                                    "cannot be parsed; using %s.", string,
                            getRow(), Shares.format(defaultValue)));
                }
            };

    // The shares processor
    private final FieldProcessorIfNotEmpty<HoldingDescription> sharesProcessor =
            new FieldProcessorIfNotEmpty<>() {

                @Override
                public void processNotEmptyField(@NotNull String field) {
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
                    logMessage(Level.WARNING, String.format("Value '%s' at " +
                                    "line number %d in the holding file " +
                                    "cannot be parsed; using %s.", string,
                            getRow(), Currency.format(defaultValue)));
                }
            };

    // The value processor
    private final FieldProcessorIfNotEmpty<HoldingDescription> valueProcessor =
            new FieldProcessorIfNotEmpty<>() {

                @Override
                public void processNotEmptyField(@NotNull String field) {

                    // Get the holding description and its current value.
                    final HoldingDescription description = getTarget();
                    final Currency current = getTarget().getValue();

                    /*
                     * Use the current value of the description if it is not
                     * null; otherwise use zero.
                     */
                    final double defaultValue = (null == current) ?
                            Currency.getZero().getValue() : current.getValue();

                    // Set the value of the target using the field.
                    description.setValue(valueInterpreter.interpret(field,
                            defaultValue));
                }
            };

    // Our rebalancing weight interpreter
    private final DoubleInterpreter weightInterpreter =
            new DoubleInterpreter() {

                @Override
                protected void receiveException(@NotNull Exception exception,
                                                @NotNull String string,
                                                Double defaultValue) {
                    logMessage(Level.WARNING, String.format("Rebalancing " +
                                    "weight '%s' at line number %d in the " +
                                    "holding file cannot be parsed; using %s.",
                            string, getRow(), defaultValue));
                }
            };

    // The rebalancing weight processor
    private final FieldProcessor<HoldingDescription> weightProcessor =
            new FieldProcessor<>() {

                @Override
                public void processField(@NotNull String field) {
                    getTarget().setWeight(weightInterpreter.interpret(field,
                            0.));
                }
            };

    {

        // Initialize a field index. Add the name and shares processors.
        int fieldIndex = HoldingFields.KEY.getPosition();
        addFieldProcessor(++fieldIndex, nameProcessor);
        addFieldProcessor(++fieldIndex, sharesProcessor);

        /*
         * Add the price processor, the value processor and the weight
         * processor.
         */
        addFieldProcessor(++fieldIndex, priceProcessor);
        addFieldProcessor(++fieldIndex, valueProcessor);
        addFieldProcessor(++fieldIndex, weightProcessor);
    }

    /**
     * Gets the holding type of the builder.
     *
     * @return The holding type of the builder
     */
    protected abstract @NotNull HoldingType getHoldingType();

    /**
     * Gets the holding library.
     *
     * @return The holding library
     */
    protected @NotNull HoldingLibrary getLibrary() {
        return HoldingLibrary.getInstance(getHoldingType());
    }

    @Override
    public int getMinimumFields() {
        return 2;
    }

    @Override
    @NotNull
    public String getPrefix() {
        return getHoldingType().toString().toLowerCase();
    }

    @Override
    protected @NotNull Logger getReadingLogger() {
        return Logger.getLogger(HoldingsBuilder.class.getCanonicalName());
    }

    @Override
    protected void processElements(@NotNull String @NotNull [] elements,
                                   int lineNumber) {

        // Set the line number and get the line code.
        setLineNumber(lineNumber);
        final Character lineCode = codeInterpreter.interpret(
                elements[HoldingFields.LINE_TYPE.getPosition()]);

        // Determine the line type from the code. Is the line type known?
        final HoldingLineType lineType =
                tracker.getAssociation(lineCode);
        if (null == lineType) {

            // The line type is not known. Log a warning and return.
            logMessage(Level.WARNING, String.format("Line code '%s' is not " +
                            "recognized at line number %d in the %s file.",
                    lineCode, lineNumber, getPrefix()));
            return;
        }

        // Create a new holding description key.
        final HoldingKey key = new HoldingKey(tracker.constructKey(lineCode,
                elements[HoldingFields.KEY.getPosition()]));

        /*
         * Create a new holding description with the key. Set the line type
         * of the description.
         */
        final HoldingDescription description =
                new HoldingDescription(lineNumber, key);
        description.setLineType(lineType);

        /*
         * Get the holding library. Check the key of the description against
         * the default key in the library. Try to add the new holding
         * description, receiving any existing description with the same key.
         */
        final HoldingLibrary library = getLibrary();
        checkKey(library, description, lineNumber);
        if (null != library.addDescription(description)) {

            /*
             * Log a message describing where the duplicate portfolio
             * description occurs.
             */
            logMessage(getInformationLevel(), String.format("Replacing " +
                            "holding with key '%s' at line number %d in the " +
                            "%s file.", description.getKey(), lineNumber,
                    getPrefix()));
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
         * Or log a different warning if the fields-to-process is less than two
         * less than the number of portfolio fields. We allow the final two
         * fields (holding value and rebalancing weight) to be missing. We
         * assume that the holding value is the product of shares times price,
         * and that the rebalancing weight is null.
         */
        else if (fieldsToProcess < numberOfPortfolioFields - 2) {
            logMessage(Level.WARNING, String.format("There are %d holding " +
                            "fields but only %d holding line elements at " +
                            "line number %d; you might want to check that.",
                    numberOfPortfolioFields, elementsLength, lineNumber));
        }

        // Cycle for each remaining field-to-process, and process it.
        for (int i = getMinimumFields(); i < fieldsToProcess; ++i) {
            processField(i, elements[i]);
        }

        // Log some exit information.
        logMessage(getOrdinary(), String.format("Load of metadata for " +
                        "holding with key '%s' at line %d was%s successful.",
                description.getHoldingParentChild().getSecond(), lineNumber,
                hadLineProblem() ? " not" : ""));
    }

    @SuppressWarnings("GrazieInspection")
    @Override
    protected void setLineNumber(int lineNumber) {

        /*
         * Set the line number as the row in the code interpreter and the price
         * interpreter.
         */
        codeInterpreter.setRow(lineNumber);
        priceInterpreter.setRow(lineNumber);

        /*
         * Set the line number as the row in the shares interpreter, the value
         * interpreter, and the weight interpreter.
         */
        sharesInterpreter.setRow(lineNumber);
        valueInterpreter.setRow(lineNumber);
        weightInterpreter.setRow(lineNumber);
    }

    @Override
    protected void setTarget(@NotNull HoldingDescription description) {

        // Set the description in all the processors.
        nameProcessor.setTarget(description);
        sharesProcessor.setTarget(description);
        priceProcessor.setTarget(description);
        valueProcessor.setTarget(description);
        weightProcessor.setTarget(description);
    }

    @Override
    protected void startProcessing() {

        /*
         * Call the superclass method and reset the parent tracker. Set the
         * date in the library.
         */
        super.startProcessing();
        tracker.reset();
        setDate(getLibrary());
    }
}
