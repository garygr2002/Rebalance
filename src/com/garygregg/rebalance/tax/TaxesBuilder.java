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

    @Override
    protected void processElements(@NotNull String[] elements,
                                   int lineNumber) {

        /*
         * Set the line number, and create a new tax description with the
         * threshold and tax rate.
         */
        setLineNumber(lineNumber);
        final TaxDescription description = new TaxDescription(
                thresholdInterpreter.interpret(
                        elements[TaxFields.THRESHOLD.getPosition()],
                        Currency.getZero().getValue()),
                rateInterpreter.interpret(
                        elements[TaxFields.TAX_RATE.getPosition()],
                        Percent.getZero().getValue()));

        // Get the tax rate, and format a message.
        final Percent taxRate = description.getTaxRate();
        final String taxMessage = (taxRate.compareTo(Percent.getZero()) < 0) ||
                (Percent.getOneHundred().compareTo(taxRate) < 0) ?
                String.format("tax rate is %s", taxRate) : "";

        // Get the threshold, and format a message.
        final Currency threshold = description.getThreshold();
        final String thresholdMessage =
                (threshold.compareTo(Currency.getZero()) < 0) ?
                        String.format("threshold is %s", threshold) : "";

        // Is either message not empty?
        if (!(taxMessage.isEmpty() && thresholdMessage.isEmpty())) {

            /*
             * Either the threshold, the tax rate, or both had a problem. Write
             * the messages.
             */
            logMessage(getExtraordinary(), String.format("Tax description " +
                            "at line number %d has the following problems: %s%s%s.",
                    lineNumber, thresholdMessage,
                    taxMessage.isEmpty() ? "" : "; ", taxMessage));
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
        rateInterpreter.setRow(lineNumber);
        thresholdInterpreter.setRow(lineNumber);
    }
}
