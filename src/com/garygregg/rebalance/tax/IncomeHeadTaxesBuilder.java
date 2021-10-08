package com.garygregg.rebalance.tax;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class IncomeHeadTaxesBuilder extends IncomeTaxesBuilder {

    @Override
    protected @NotNull TaxLibrary getLibrary() {
        return IncomeHeadTaxLibrary.getInstance();
    }

    @Override
    public @NotNull String getPrefix() {
        return "income_head";
    }

    @Override
    protected @NotNull Logger getReadingLogger() {
        return Logger.getLogger(IncomeHeadTaxesBuilder.class.getCanonicalName());
    }
}
