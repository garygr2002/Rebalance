package com.garygregg.rebalance.tax;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class HeadTaxesBuilder extends IncomeTaxesBuilder {

    @Override
    protected @NotNull TaxLibrary getLibrary() {
        return HeadTaxLibrary.getInstance();
    }

    @Override
    public @NotNull String getPrefix() {
        return "head";
    }

    @Override
    protected @NotNull Logger getReadingLogger() {
        return Logger.getLogger(HeadTaxesBuilder.class.getCanonicalName());
    }
}
