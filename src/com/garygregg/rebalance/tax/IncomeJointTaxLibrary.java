package com.garygregg.rebalance.tax;

import com.garygregg.rebalance.FilingStatus;
import org.jetbrains.annotations.NotNull;

public class IncomeJointTaxLibrary extends IncomeTaxLibrary {

    // The singleton income tax library, married-filing-jointly
    private static final IncomeJointTaxLibrary library =
            new IncomeJointTaxLibrary();

    static {

        // Add an instance of this class to the income tax library.
        IncomeTaxLibrary.addLibrary(getInstance());
    }

    /**
     * Constructs the income tax library, married-filing-jointly.
     */
    private IncomeJointTaxLibrary() {

        // Nothing to do here not. Added only to make the constructor private.
    }

    /**
     * Gets a married-filing-jointly income tax library instance.
     *
     * @return A married-filing-jointly income tax library instance
     */
    static @NotNull IncomeJointTaxLibrary getInstance() {
        return library;
    }

    @Override
    public @NotNull FilingStatus getFilingStatus() {
        return FilingStatus.JOINT;
    }
}
