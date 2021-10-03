package com.garygregg.rebalance.tax;

import com.garygregg.rebalance.FilingStatus;
import org.jetbrains.annotations.NotNull;

abstract class IncomeTaxLibrary extends TaxLibrary {

    /**
     * Gets the filing status of the library.
     *
     * @return The filing status of the library
     */
    public abstract @NotNull FilingStatus getFilingStatus();
}
