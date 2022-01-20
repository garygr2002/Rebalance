package com.garygregg.rebalance.tax;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

abstract class GainsTaxesBuilder extends TaxesBuilder {

    @Override
    public @NotNull String getPrefix() {
        return "gains";
    }

    @Override
    protected @NotNull Logger getReadingLogger() {
        return Logger.getLogger(GainsTaxesBuilder.class.getCanonicalName());
    }
}
