package com.garygregg.rebalance.tax;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class GainsSingleTaxesBuilder extends GainsTaxesBuilder {

    @Override
    protected @NotNull TaxLibrary getLibrary() {
        return GainsSingleTaxLibrary.getInstance();
    }

    @Override
    public @NotNull String getPrefix() {
        return "gains_single";
    }

    @Override
    protected @NotNull Logger getReadingLogger() {
        return Logger.getLogger(GainsSingleTaxesBuilder.class.getCanonicalName());
    }
}
