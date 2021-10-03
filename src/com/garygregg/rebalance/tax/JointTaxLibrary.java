package com.garygregg.rebalance.tax;

import com.garygregg.rebalance.FilingStatus;
import org.jetbrains.annotations.NotNull;

public class JointTaxLibrary extends IncomeTaxLibrary {

    // The singleton income tax library, married filing jointly
    private static final JointTaxLibrary library = new JointTaxLibrary();

    /**
     * Constructs the income tax library, married filing jointly.
     */
    private JointTaxLibrary() {

        // Nothing to do here not. Added only to make the constructor private.
    }

    /**
     * Gets a married filing jointly income tax library instance.
     *
     * @return A married filing jointly income tax library instance
     */
    public static @NotNull JointTaxLibrary getInstance() {
        return library;
    }

    @Override
    public @NotNull FilingStatus getFilingStatus() {
        return FilingStatus.JOINT;
    }
}
