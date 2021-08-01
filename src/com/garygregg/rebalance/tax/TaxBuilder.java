package com.garygregg.rebalance.tax;

import com.garygregg.rebalance.ElementReader;
import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.countable.Percent;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

abstract class TaxBuilder extends ElementReader {

    // The tax library instance
    private final TaxLibrary library = getLibrary();

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

        // Create a new tax description with the threshold and tax rate.
        final TaxDescription description = new TaxDescription(
                processFloat(elements[TaxFields.THRESHOLD.getPosition()],
                        Currency.getZero().getValue(), lineNumber),
                processFloat(elements[TaxFields.TAX_RATE.getPosition()],
                        Percent.getZero().getValue(), lineNumber));

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
             * Otherwise, parse the allocation as a floating point number.
             */
            result = element.isEmpty() ? defaultValue :
                    Double.parseDouble(element);
        }

        // Catch any number format exception that may occur.
        catch (@NotNull NumberFormatException exception) {

            /*
             * Log a warning message describing the unparseable floating point
             * element.
             */
            logMessage(Level.WARNING, String.format("Unparseable floating " +
                            "point number '%s' at line number %d in holding " +
                            "file; using default %f instead.", element,
                    lineNumber, defaultValue));
        }

        // Return the result.
        return result;
    }
}
