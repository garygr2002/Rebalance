package com.garygregg.rebalance.tax;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class SeparateTaxesBuilder extends TaxesBuilder {

    @Override
    protected @NotNull TaxLibrary getLibrary() {
        return SeparateTaxLibrary.getInstance();
    }

    @Override
    public @NotNull String getPrefix() {
        return "separate";
    }

    @Override
    protected @NotNull Logger getReadingLogger() {
        return Logger.getLogger(SeparateTaxesBuilder.class.getCanonicalName());
    }
}
