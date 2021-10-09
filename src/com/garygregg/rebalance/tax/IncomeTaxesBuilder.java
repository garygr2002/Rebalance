package com.garygregg.rebalance.tax;

import com.garygregg.rebalance.countable.Currency;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;
import java.util.logging.Logger;

abstract class IncomeTaxesBuilder extends TaxesBuilder {

    /**
     * Gets the income tax library.
     *
     * @return The income tax library
     */
    private @Nullable IncomeTaxLibrary getIncomeTaxLibrary() {

        /*
         * Get the tax library. Cast the tax library to an income tax library
         * if it is an instance of such. Otherwise, use null. Is the income tax
         * library null?
         */
        final TaxLibrary library = getLibrary();
        final IncomeTaxLibrary incomeTaxLibrary =
                (library instanceof IncomeTaxLibrary) ?
                        ((IncomeTaxLibrary) library) : null;
        if (null == incomeTaxLibrary) {

            /*
             * The income tax library is null. This is an error. Log it as
             * such.
             */
            logMessage(Level.SEVERE, String.format("The builder for " +
                    "the '%s' income tax library is not associated " +
                    "with an income tax library!", getPrefix()));
        }

        // Return the income tax library.
        return incomeTaxLibrary;
    }

    @Override
    public @NotNull String getPrefix() {
        return "income";
    }

    @Override
    protected @NotNull Logger getReadingLogger() {
        return Logger.getLogger(IncomeTaxesBuilder.class.getCanonicalName());
    }

    @Override
    protected void processNotPositiveRate(@NotNull TaxDescription description,
                                          int lineNumber) {

        // Get the income tax library. Is the income tax library not null?
        final IncomeTaxLibrary incomeTaxLibrary = getIncomeTaxLibrary();
        if (null != incomeTaxLibrary) {

            /*
             * The income tax library is not null. Set its standard deduction
             * using the threshold in the tax description.
             */
            setStandardDeduction(description.getThreshold(), lineNumber,
                    incomeTaxLibrary);
        }
    }

    /**
     * Sets the standard deduction.
     *
     * @param standardDeduction The standard deduction
     * @param lineNumber        The line number where the elements occur
     */
    private void setStandardDeduction(@NotNull Currency standardDeduction,
                                      int lineNumber,
                                      @NotNull IncomeTaxLibrary library) {

        /*
         * Get any existing standard deduction from library. Is the deduction
         * not null?
         */
        final Currency existingDeduction = library.getStandardDeduction();
        if (null == existingDeduction) {

            /*
             * The existing standard deduction in the tax library is null. So
             * set the deduction with the interpreted value, and log a message.
             */
            library.setStandardDeduction(standardDeduction);
            logMessage(Level.INFO, String.format("The standard deduction " +
                            "for the '%s' income tax library has been set " +
                            "to %s at line %d.",
                    getPrefix(), standardDeduction, lineNumber));
        }

        /*
         * A standard deduction has already been set in the library. Log a
         * warning.
         */
        else {
            logMessage(Level.WARNING, String.format("The standard deduction " +
                            "for the '%s' income tax library cannot be set " +
                            "to %s at line %d because it has already been " +
                            "set to %s.", getPrefix(), standardDeduction,
                    lineNumber, existingDeduction));
        }
    }

    @Override
    protected void startProcessing() {

        /*
         * Call the superclass method. Get the income tax library. Is the
         * income tax library not null?
         */
        super.startProcessing();
        final IncomeTaxLibrary library = getIncomeTaxLibrary();
        if (null != library) {

            /*
             * The income tax library is not null. Clear its standard
             * deduction.
             */
            library.clearStandardDeduction();
        }
    }
}
