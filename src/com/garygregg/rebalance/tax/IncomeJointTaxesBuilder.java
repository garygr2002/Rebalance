package com.garygregg.rebalance.tax;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class IncomeJointTaxesBuilder extends IncomeTaxesBuilder {

    @Override
    protected @NotNull TaxLibrary getLibrary() {
        return IncomeJointTaxLibrary.getInstance();
    }

    @Override
    public @NotNull String getPrefix() {
        return "income_joint";
    }

    @Override
    protected @NotNull Logger getReadingLogger() {
        return Logger.getLogger(IncomeJointTaxesBuilder.class.getCanonicalName());
    }
}
