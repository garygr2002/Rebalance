package com.garygregg.rebalance.tax;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class SingleTaxesBuilder extends IncomeTaxesBuilder {

    @Override
    protected @NotNull TaxLibrary getLibrary() {
        return SingleTaxLibrary.getInstance();
    }

    @Override
    public @NotNull String getPrefix() {
        return "single";
    }

    @Override
    protected @NotNull Logger getReadingLogger() {
        return Logger.getLogger(SingleTaxesBuilder.class.getCanonicalName());
    }
}
