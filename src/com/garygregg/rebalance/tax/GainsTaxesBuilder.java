package com.garygregg.rebalance.tax;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;
import java.util.logging.Logger;

abstract class GainsTaxesBuilder extends TaxesBuilder {

    /**
     * Gets the capital gains tax library.
     *
     * @return The capital gains tax library
     */
    private @Nullable GainsTaxLibrary getGainsTaxLibrary() {

        /*
         * Get the tax library. Cast the tax library to a capital-gains tax
         * library if it is an instance of such. Otherwise, use null. Is the
         * capital-gains library null?
         */
        final TaxLibrary library = getLibrary();
        final GainsTaxLibrary gainsTaxLibrary =
                (library instanceof GainsTaxLibrary) ?
                        ((GainsTaxLibrary) library) : null;
        if (null == gainsTaxLibrary) {

            /*
             * The capital gains tax library is null. This is an error. Log it
             * as such.
             */
            logMessage(Level.SEVERE, String.format("The builder for " +
                    "the '%s' capital gains tax library is not associated " +
                    "with a capital gains tax library!", getPrefix()));
        }

        // Return the capital gains tax library.
        return gainsTaxLibrary;
    }

    @Override
    public @NotNull String getPrefix() {
        return "gains";
    }

    @Override
    protected @NotNull Logger getReadingLogger() {
        return Logger.getLogger(GainsTaxesBuilder.class.getCanonicalName());
    }

}
