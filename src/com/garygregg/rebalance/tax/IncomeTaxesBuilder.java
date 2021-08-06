package com.garygregg.rebalance.tax;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class IncomeTaxesBuilder extends TaxesBuilder {

    @Override
    protected @NotNull TaxLibrary getLibrary() {
        return IncomeTaxLibrary.getInstance();
    }

    @Override
    public @NotNull String getPrefix() {
        return "income";
    }

    @Override
    protected @NotNull Logger getReadingLogger() {
        return Logger.getLogger(IncomeTaxesBuilder.class.getCanonicalName());
    }
}
