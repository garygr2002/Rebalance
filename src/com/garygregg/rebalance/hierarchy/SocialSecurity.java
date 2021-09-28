package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.distinguished.DistinguishedAccounts;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

class SocialSecurity extends CpiAnnuity {

    /**
     * Constructs the Social Security synthesizer.
     *
     * @param account The distinguished account associated with this
     *                synthesizer
     */
    public SocialSecurity(@NotNull DistinguishedAccounts account) {
        super(account);
    }

    @Override
    public boolean synthesize(@NotNull Account account) {

        // TODO: Fill in this method.
        getLogger().logMessage(Level.INFO, String.format("The synthesizer " +
                        "for class '%s' intended for the distinguished account '%s' " +
                        "needs to be completed.",
                SocialSecurity.class.getSimpleName(), getAccount().name()));
        return super.synthesize(account);
    }
}
