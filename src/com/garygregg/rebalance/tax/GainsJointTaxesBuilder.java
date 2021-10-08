package com.garygregg.rebalance.tax;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class GainsJointTaxesBuilder extends GainsTaxesBuilder {

    @Override
    protected @NotNull TaxLibrary getLibrary() {
        return GainsJointTaxLibrary.getInstance();
    }

    @Override
    public @NotNull String getPrefix() {
        return "gains_joint";
    }

    @Override
    protected @NotNull Logger getReadingLogger() {
        return Logger.getLogger(GainsJointTaxesBuilder.class.getCanonicalName());
    }
}
