package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.portfolio.PortfolioDescription;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

class CpiAnnuity extends Annuity {

    @Override
    protected @NotNull Currency getMonthlyIncome(@NotNull PortfolioDescription
                                                             description) {
        return description.getCpiMonthly();
    }

    @Override
    protected Date getStartDate(Date referenceDate) {
        return null;
    }

    @Override
    protected boolean isReduced() {
        return false;
    }
}
