package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.distinguished.DistinguishedAccounts;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class Pension extends AnnuitySynthesizer {

    @Override
    public @NotNull DistinguishedAccounts getAccount() {
        return DistinguishedAccounts.PENSION;
    }

    @Override
    public boolean synthesize(@NotNull Account account) {

        // TODO: Fill in this method.
        getLogger().logMessage(Level.INFO, String.format("The synthesizer " +
                        "for class '%s' intended for the distinguished account '%s' " +
                        "needs to be completed.",
                Pension.class.getSimpleName(), getAccount().name()));
        return super.synthesize(account);
    }
}
