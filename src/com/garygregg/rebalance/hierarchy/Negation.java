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
                                double notConsidered) {

        /*
         * Negate both the considered and not-considered values before setting
         * them.
         */
        final double negation = -1.;
        super.setValuation(account, considered * negation,
                notConsidered * negation);
    }
}
