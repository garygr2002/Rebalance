package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.portfolio.PortfolioDescription;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

class NoCpiAnnuity extends Annuity {

    @Override
    protected @NotNull Currency getMonthlyIncome(@NotNull PortfolioDescription
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
