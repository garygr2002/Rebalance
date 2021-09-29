package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.SynthesizerType;
import com.garygregg.rebalance.distinguished.DistinguishedAccounts;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

class AveragingSynthesizer extends Synthesizer {

    // The set of referenced accounts
    private final Set<Long> referencedAccounts = new TreeSet<>();

    /**
     * Constructs the averaging synthesizer.
     *
     * @param account The distinguished account associated with this
     *                synthesizer
     */
    public AveragingSynthesizer(@NotNull DistinguishedAccounts account) {
        super(account);
    }

    /**
     * Gets the referenced accounts.
     *
     * @return The referenced accounts
     */
    public @NotNull Long @NotNull [] getReferencedAccounts() {
        return referencedAccounts.toArray(new Long[0]);
    }

    @Override
    public @NotNull SynthesizerType getType() {
        return SynthesizerType.AVERAGER;
    }

    /**
     * Sets the referenced accounts from an account.
     *
     * @param account An account
     */
    public void setReferencedAccounts(@NotNull Account account) {

        // Clear existing references, then add the new references.
        referencedAccounts.clear();
        Collections.addAll(referencedAccounts, getReferencedAccounts());
    }

    @Override
    public boolean synthesize(@NotNull Account account) {

        // TODO: Fill this out.
        return super.synthesize(account);
    }
}
