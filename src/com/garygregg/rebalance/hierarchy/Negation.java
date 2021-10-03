package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.SynthesizerType;
import org.jetbrains.annotations.NotNull;

class Negation extends Averaging {

    @Override
    public @NotNull SynthesizerType getType() {
        return SynthesizerType.NEGATION;
    }

    @Override
    protected void setValuation(@NotNull Account account,
                                double considered,
                                double notConsidered,
                                int referencedAccountCount) {

        /*
         * Calculate a negation factor before using the superclass to set the
         * considered and non-considered values.
         */
        final double factor = 1. - referencedAccountCount;
        super.setValuation(account, considered * factor,
                notConsidered * factor, referencedAccountCount);
    }
}
