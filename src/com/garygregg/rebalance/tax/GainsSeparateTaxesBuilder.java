package com.garygregg.rebalance.tax;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class GainsSeparateTaxesBuilder extends GainsTaxesBuilder {

    @Override
    protected @NotNull TaxLibrary getLibrary() {
        return GainsSeparateTaxLibrary.getInstance();
    }

    @Override
    public @NotNull String getPrefix() {
        return "gains_separate";
    }

    @Override
    protected @NotNull Logger getReadingLogger() {
        return Logger.getLogger(GainsSeparateTaxesBuilder.class.getCanonicalName());
    }
}
