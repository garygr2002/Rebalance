package com.garygregg.rebalance.tax;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

abstract class IncomeTaxesBuilder extends TaxesBuilder {

    @Override
    public @NotNull String getPrefix() {
        return "income";
    }

    @Override
    protected @NotNull Logger getReadingLogger() {
        return Logger.getLogger(IncomeTaxesBuilder.class.getCanonicalName());
    }
}
