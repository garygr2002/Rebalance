package com.garygregg.rebalance.tax;

import com.garygregg.rebalance.FilingStatus;
import org.jetbrains.annotations.NotNull;

public class GainsJointTaxLibrary extends GainsTaxLibrary {

    // The singleton capital gains tax library, married-filing-jointly
    private static final GainsJointTaxLibrary library =
            new GainsJointTaxLibrary();

    static {

        // Add an instance of this class to the capital gains tax library.
        GainsTaxLibrary.addLibrary(getInstance());
    }

    /**
     * Constructs the capital gains tax library, married-filing-jointly.
     */
    private GainsJointTaxLibrary() {

        // Nothing to do here not. Added only to make the constructor private.
    }

    /**
     * Gets a married-filing-jointly capital gains tax library instance.
     *
     * @return A married-filing-jointly capital gains tax library instance
     */
    static @NotNull GainsJointTaxLibrary getInstance() {
        return library;
    }

    @Override
    public @NotNull FilingStatus getFilingStatus() {
        return FilingStatus.JOINT;
    }
}
