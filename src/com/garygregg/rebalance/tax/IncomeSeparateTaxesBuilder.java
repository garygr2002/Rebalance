package com.garygregg.rebalance.tax;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class IncomeSeparateTaxesBuilder extends IncomeTaxesBuilder {

    @Override
    protected @NotNull TaxLibrary getLibrary() {
        return IncomeSeparateTaxLibrary.getInstance();
    }

    @Override
    public @NotNull String getPrefix() {
        return "income_separate";
    }

    @Override
    protected @NotNull Logger getReadingLogger() {
        return Logger.getLogger(IncomeSeparateTaxesBuilder.class.getCanonicalName());
    }
}
