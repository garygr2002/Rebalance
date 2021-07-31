package com.garygregg.rebalance.tax;

import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public class CapitalGainsTaxBuilder extends TaxBuilder {

    @Override
    protected @NotNull TaxLibrary getLibrary() {
        return CapitalGainsTaxLibrary.getInstance();
    }

    @Override
    public @NotNull String getPrefix() {
        return "gains";
    }

    @Override
    protected @NotNull Logger getReadingLogger() {
        return Logger.getLogger(CapitalGainsTaxBuilder.class.getCanonicalName());
    }
}
