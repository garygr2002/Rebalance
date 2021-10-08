package com.garygregg.rebalance.tax;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class IncomeSingleTaxesBuilder extends IncomeTaxesBuilder {

    @Override
    protected @NotNull TaxLibrary getLibrary() {
        return IncomeSingleTaxLibrary.getInstance();
    }

    @Override
    public @NotNull String getPrefix() {
        return "income_single";
    }

    @Override
    protected @NotNull Logger getReadingLogger() {
        return Logger.getLogger(IncomeSingleTaxesBuilder.class.getCanonicalName());
    }
}
