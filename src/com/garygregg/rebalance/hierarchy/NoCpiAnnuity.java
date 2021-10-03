package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.distinguished.DistinguishedAccounts;
import com.garygregg.rebalance.portfolio.PortfolioDescription;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

class NoCpiAnnuity extends Annuity {

    /**
     * Constructs the No-CPI adjustment synthesizer.
     *
     * @param account The distinguished account associated with this
     *                synthesizer
     */
    public NoCpiAnnuity(@NotNull DistinguishedAccounts account) {
        super(account);
    }

    @Override
    protected Currency getMonthlyIncome(@NotNull PortfolioDescription
                                                description) {
        return description.getNonCpiMonthly();
    }

    @Override
    protected Date getStartDate(Date referenceDate) {
        return null;
    }

    @Override
    protected boolean isReduced() {
        return true;
    }
}
