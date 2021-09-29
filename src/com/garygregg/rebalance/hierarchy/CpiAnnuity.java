package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.distinguished.DistinguishedAccounts;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.logging.Level;

class CpiAnnuity extends Annuity {

    /**
     * Constructs the CPI adjustment synthesizer.
     *
     * @param account The distinguished account associated with this
     *                synthesizer
     */
    public CpiAnnuity(@NotNull DistinguishedAccounts account) {
        super(account);
    }

    /**
     * Calculates an annuity value.
     *
     * @param monthly The monthly payment
     * @param start   The start date of the calculation
     * @param end     The end date of the calculation
     * @return The value of the annuity
     */
    protected @NotNull Currency calculateValue(@NotNull Currency monthly,
                                               Date start,
                                               @NotNull Date end) {
        return super.calculateValue(monthly, start, end, false);
    }

    @Override
    public boolean synthesize(@NotNull Account account) {

        // TODO: Fill in this method.
        getLogger().logMessage(Level.INFO, String.format("The synthesizer " +
                        "for class '%s' intended for the distinguished account '%s' " +
                        "needs to be completed.",
                CpiAnnuity.class.getSimpleName(), getAccount().name()));
        return super.synthesize(account);
    }
}
