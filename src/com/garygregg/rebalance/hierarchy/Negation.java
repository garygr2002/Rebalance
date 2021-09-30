package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.SynthesizerType;
import com.garygregg.rebalance.distinguished.DistinguishedAccounts;
import org.jetbrains.annotations.NotNull;

class Negation extends Averaging {

    /**
     * Constructs the negation synthesizer.
     *
     * @param account The distinguished account associated with this
     *                synthesizer
     */
    public Negation(@NotNull DistinguishedAccounts account) {
        super(account);
    }

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
