package com.garygregg.rebalance.tax;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class JointTaxesBuilder extends IncomeTaxesBuilder {

    @Override
    protected @NotNull TaxLibrary getLibrary() {
        return JointTaxLibrary.getInstance();
    }

    @Override
    public @NotNull String getPrefix() {
        return "joint";
    }

    @Override
    protected @NotNull Logger getReadingLogger() {
        return Logger.getLogger(JointTaxesBuilder.class.getCanonicalName());
    }
}
