package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.FundType;
import com.garygregg.rebalance.HoldingLineType;
import com.garygregg.rebalance.TaxType;
import com.garygregg.rebalance.portfolio.PortfolioDescription;
import org.jetbrains.annotations.NotNull;

public class Portfolio extends Aggregate<String, Institution, PortfolioDescription> {

    /**
     * Constructs the portfolio hierarchy object.
     *
     * @param mnemonic The key of the portfolio hierarchy object
     */
    Portfolio(@NotNull String mnemonic) {
        super(mnemonic);
    }

    @Override
    public void breakdown() {

        // Made public here, that is all.
        super.breakdown();
    }

    @Override
    public void clear() {

        // Made public here, that is all.
        super.clear();
    }

    @Override
    public @NotNull HoldingLineType getLineType() {
        return HoldingLineType.PORTFOLIO;
    }

    @Override
    public boolean hasFundType(@NotNull FundType type) {
        return type.equals(FundType.NOT_A_FUND);
    }

    @Override
    public boolean hasTaxType(@NotNull TaxType type) {
        return type.equals(TaxType.NOT_AN_ACCOUNT);
    }

    @Override
    public void setCurrent() {

        // Made public here, that is all.
        super.setCurrent();
    }

    @Override
    public void setProposed() {

        // Made public here, that is all.
        super.setProposed();
    }
}
