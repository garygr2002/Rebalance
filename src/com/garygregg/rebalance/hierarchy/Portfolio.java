package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.BreakdownType;
import com.garygregg.rebalance.FundType;
import com.garygregg.rebalance.HoldingLineType;
import com.garygregg.rebalance.portfolio.PortfolioDescription;
import org.jetbrains.annotations.NotNull;

public class Portfolio
        extends SuperAggregate<String, Institution, PortfolioDescription> {

    /**
     * Constructs the portfolio hierarchy object.
     *
     * @param mnemonic The key of the portfolio hierarchy object
     */
    Portfolio(@NotNull String mnemonic) {
        super(mnemonic);
    }

    /**
     * Breaks down the portfolio valuation by category type, tax type and
     * weight type, using a given valuation type (current or proposed)
     *
     * @param type Indicates the valuation type (current or proposed)
     */
    public void breakdown(@NotNull BreakdownType type) {

        /*
         * Clear the existing breakdown values. Is the indicated valuation type
         * proposed?
         */
        clear();
        if (BreakdownType.PROPOSED.equals(type)) {

            /*
             * The indicated valuation type is proposed, so set proposed
             * values.
             */
            setProposed();
        }

        /*
         * The indicated valuation type is not proposed, so assume current
         * values are intended. Set current values.
         */
        else {
            setCurrent();
        }

        // Break down the portfolio object.
        breakdown();
    }

    @Override
    public void clear() {

        // Made public here, that is all.
        super.clear();
    }

    @Override
    protected @NotNull Institution getArtificialChild() {
        return Institution.getArtificial();
    }

    @Override
    public @NotNull HoldingLineType getLineType() {
        return HoldingLineType.PORTFOLIO;
    }

    @Override
    public boolean hasFundType(@NotNull FundType type) {
        return type.equals(FundType.NOT_A_FUND);
    }
}
