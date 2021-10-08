package com.garygregg.rebalance.tax;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class GainsHeadTaxesBuilder extends GainsTaxesBuilder {

    @Override
    protected @NotNull TaxLibrary getLibrary() {
        return GainsHeadTaxLibrary.getInstance();
    }

    @Override
    public @NotNull String getPrefix() {
        return "gains_head";
    }

    @Override
    protected @NotNull Logger getReadingLogger() {
        return Logger.getLogger(GainsHeadTaxesBuilder.class.getCanonicalName());
    }
}
