package com.garygregg.rebalance.tax;

import com.garygregg.rebalance.ElementReader;
import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.countable.Percent;
import com.garygregg.rebalance.interpreter.DoubleInterpreter;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

abstract class TaxesBuilder extends ElementReader<TaxDescription> {

    // A blank string
    private final String blank = "";

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
     * Checks threshold and rate messages against blank; outputs warnings if
     * either are not blank, otherwise adds the tax description to the library.
     *
     * @param thresholdMessage A threshold message
     * @param taxRateMessage   A tax rate message
     * @param lineNumber       The line number from which the given tax description
     *                         was read
     * @param description      The tax description to add if both messages are blank
     */
    protected void checkAndAdd(@NotNull String thresholdMessage,
                               @NotNull String taxRateMessage,
                               int lineNumber,
                               @NotNull TaxDescription description) {

        // Is either message not blank?
        if (!(taxRateMessage.isBlank() && thresholdMessage.isBlank())) {

            // Either the threshold, the tax rate, or both are not blank.
            logMessage(Level.WARNING, String.format("Tax rate at line " +
                            "number %d in the %s tax file has the following " +
                            "problem(s): %s%s%s.", lineNumber, getPrefix(),
                    thresholdMessage,
                    (taxRateMessage.isBlank() || thresholdMessage.isBlank()) ?
                            blank : "; ", taxRateMessage));
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
                hadLineProblem() ? " not" : blank));
    }

    /**
     * Checks the threshold in a tax description against zero.
     *
     * @param description The tax description to check
     * @return A non-blank error message if the threshold is negative; a blank
     * string otherwise
     */
    private @NotNull String checkThreshold(
            @NotNull TaxDescription description) {

        // Get the threshold, and format a message.
        final Currency threshold = description.getThreshold();
        return (threshold.compareTo(Currency.getZero()) < 0) ?
                String.format("threshold is negative, %s", threshold) : blank;
    }

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

        // Set the line number and read the threshold.
        setLineNumber(lineNumber);
        final Double threshold = getThresholdInterpreter().interpret(
                elements[TaxFields.THRESHOLD.getPosition()], null);

        // Read the tax rate. Are neither the threshold nor the tax rate null?
        final Double taxRate = getThresholdInterpreter().interpret(
                elements[TaxFields.TAX_RATE.getPosition()], null);
        if (!((null == threshold) || (null == taxRate))) {

            /*
             * Neither the threshold nor the tax rate is null. Create a new
             * tax description with the values.
             */
            final TaxDescription description = new TaxDescription(threshold,
                    taxRate);

            // Process a positive tax rate...
            if (0. < taxRate) {
                processPositiveRate(description, lineNumber);
            }

            // ...or process a non-positive tax rate.
            else {
                processNotPositiveRate(description, lineNumber);
            }
        }
    }

    /**
     * Processes a tax description with a non-positive tax rate.
     *
     * @param description A tax description with a non-positive tax rate
     * @param lineNumber  The line number from which the tax description was
     *                    read
     */
    protected void processNotPositiveRate(@NotNull TaxDescription description,
                                          int lineNumber) {

        // Check the threshold, and get the tax rate.
        final String thresholdMessage = checkThreshold(description);
        final Percent taxRate = description.getTaxRate();

        /*
         * Format an error message if the tax rate is too low, otherwise use a
         * blank message.
         */
        final String taxMessage =
                (taxRate.compareTo(Percent.getZero()) < 0) ?
                        String.format("tax rate is below acceptable range, %s",
                                taxRate) : blank;

        /*
         * Check the error messages and output them if they are not blank. If
         * they are blank, add the tax description.
         */
        checkAndAdd(thresholdMessage, taxMessage, lineNumber, description);
    }

    /**
     * Processes a tax description with a positive tax rate.
     *
     * @param description A tax description with a positive tax rate
     * @param lineNumber  The line number from which the tax description was
     *                    read
     */
    private void processPositiveRate(@NotNull TaxDescription description,
                                     int lineNumber) {

        // Check the threshold, and get the tax rate.
        final String thresholdMessage = checkThreshold(description);
        final Percent taxRate = description.getTaxRate();

        /*
         * Format an error message if the tax rate is too high, otherwise use a
         * blank message.
         */
        final String taxMessage =
                (Percent.getOneHundred().compareTo(taxRate) < 0) ?
                        String.format("tax rate is above acceptable range, %s",
                                taxRate) : blank;

        /*
         * Check the error messages and output them if they are not blank. If
         * they are blank, add the tax description.
         */
        checkAndAdd(thresholdMessage, taxMessage, lineNumber, description);
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
