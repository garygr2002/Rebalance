package com.garygregg.rebalance.tax;

import com.garygregg.rebalance.ElementReader;
import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.countable.Percent;
import com.garygregg.rebalance.interpreter.DoubleInterpreter;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

abstract class TaxesBuilder extends ElementReader<TaxDescription> {

    // The tax library instance
    private final TaxLibrary library = getLibrary();
    // Our rate interpreter
    private final DoubleInterpreter rateInterpreter =
            new DoubleInterpreter() {
                @Override
                protected void receiveException(@NotNull Exception exception,
                                                @NotNull String string,
                                                Double defaultValue) {
                    logMessage(Level.WARNING, String.format("Tax rate '%s' " +
                                    "at line number %d in the %s tax file " +
                                    "cannot be parsed; using %s.", string,
                            getRow(), getPrefix(),
                            Percent.format(defaultValue)));
                }
            };
    // Our threshold interpreter
    private final DoubleInterpreter thresholdInterpreter =
            new DoubleInterpreter() {
                @Override
                protected void receiveException(@NotNull Exception exception,
                                                @NotNull String string,
                                                Double defaultValue) {
                    logMessage(Level.WARNING, String.format("Income " +
                                    "threshold '%s' at line number %d in " +
                                    "the %s tax file cannot be parsed; " +
                                    "using %s.", string, getRow(), getPrefix(),
                            Currency.format(defaultValue)));
                }
            };

    /**
     * Gets the tax library to be used by this reader.
     *
     * @return The tax library to be used by this reader
     */
    protected abstract @NotNull TaxLibrary getLibrary();

    @Override
    public int getMinimumFields() {
        return 2;
    }

    /**
     * Gets the rate interpreter.
     *
     * @return The rate interpreter
     */
    protected @NotNull DoubleInterpreter getRateInterpreter() {
        return rateInterpreter;
    }

    /**
     * Gets the threshold interpreter.
     *
     * @return The threshold interpreter
     */
    protected @NotNull DoubleInterpreter getThresholdInterpreter() {
        return thresholdInterpreter;
    }

    @Override
    protected void processElements(@NotNull String @NotNull [] elements,
                                   int lineNumber) {

        // Get the default threshold and tax rate value. Set the line number.
        final double defaultValue = Percent.getZero().getValue();
        setLineNumber(lineNumber);

        // Create a new tax description with the threshold and tax rate.
        final TaxDescription description = new TaxDescription(
                getThresholdInterpreter().interpret(
                        elements[TaxFields.THRESHOLD.getPosition()],
                        defaultValue),
                getRateInterpreter().interpret(
                        elements[TaxFields.TAX_RATE.getPosition()],
                        defaultValue));

        // Get the threshold, and format a message.
        final Currency threshold = description.getThreshold();
        final String thresholdMessage =
                (threshold.compareTo(Currency.getZero()) < 0) ?
                        String.format("threshold is negative, %s",
                                threshold) : "";

        // Get the tax rate, and format a message.
        final Percent taxRate = description.getTaxRate();
        final String taxMessage = (taxRate.compareTo(Percent.getZero()) < 0) ||
                (Percent.getOneHundred().compareTo(taxRate) < 0) ?
                String.format("tax rate is outside acceptable range, %s",
                        taxRate) : "";

        // Is either message not empty?
        if (!(taxMessage.isEmpty() && thresholdMessage.isEmpty())) {

            // Either the threshold, the tax rate, or both had a problem.
            logMessage(Level.WARNING, String.format("Tax rate at line " +
                            "number %d in the %s tax file has the following " +
                            "problem(s): %s%s%s.", lineNumber, getPrefix(),
                    thresholdMessage,
                    (taxMessage.isEmpty() || thresholdMessage.isEmpty()) ?
                            "" : "; ", taxMessage));
        }

        /*
         * Try to add the new tax description, receiving any existing
         * description with the same threshold.
         */
        else if (null != library.addDescription(description)) {

            /*
             * Log a message describing where the duplicate tax description
             * occurs.
             */
            logMessage(getExtraordinary(), String.format("Replacing tax " +
                            "bracket with threshold '%s' at line number %d in " +
                            "%s file.", description.getKey(), lineNumber,
                    getPrefix()));
        }

        // Log some exit information.
        logMessage(getOrdinary(), String.format("Load of metadata for " +
                        "tax bracket with threshold '%s' at line %d was%s " +
                        "successful.", description.getKey(), lineNumber,
                hadLineProblem() ? " not" : ""));
    }

    @Override
    protected void setLineNumber(int lineNumber) {

        /*
         * Set the line number as the row in both the rate interpreter and
         * the threshold interpreter.
         */
        getRateInterpreter().setRow(lineNumber);
        getThresholdInterpreter().setRow(lineNumber);
    }

    @Override
    protected void startProcessing() {

        // Call the superclass method, and set the date in the library.
        super.startProcessing();
        setDate(getLibrary());
    }
}
