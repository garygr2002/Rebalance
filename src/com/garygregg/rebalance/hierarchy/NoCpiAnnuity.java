package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.portfolio.PortfolioDescription;
import org.jetbrains.annotations.NotNull;

class NoCpiAnnuity extends Annuity {

    @Override
    protected @NotNull Currency getMonthlyIncome(@NotNull PortfolioDescription
                                                description) {
        return description.getNonCpiMonthly();
    }

    @Override
    protected boolean isReduced() {
        return true;
    }
}
