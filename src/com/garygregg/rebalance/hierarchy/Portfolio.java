package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.*;
import com.garygregg.rebalance.portfolio.PortfolioDescription;
import org.jetbrains.annotations.NotNull;

public class Portfolio
        extends SuperAggregate<String, Institution, PortfolioDescription> {

    // A factory for producing the last account
    private final Factory<Account> factory = () -> {

        /*
         * Declare a variable to receive a candidate account, and a
         * variable to receive the last account. Cycle for each
         * institution.
         */
        Account candidate, last = null;
        for (Institution institution : getChildren()) {

            /*
             * Get the last account of the first/next institution as a
             * candidate. Compare the candidate to the last account. Does
             * the current last account compare less than the candidate?
             */
            candidate = institution.getLastToBeRebalanced();
            if ((null == last) || (0 < candidate.compareTo(last))) {

                /*
                 * The current last account compares less than the
                 * candidate. Set the candidate as the new last.
                 */
                last = candidate;
            }
        }

        /*
         * Return the last account, or an artificial account if the last
         * account is null.
         */
        return (null == last) ? Account.getArtificial() : last;
    };

    // A lazy boy for producing the last account
    private final LazyBoy<Account> lazyBoy = new LazyBoy<>(factory);

    /**
     * Constructs the portfolio hierarchy object.
     *
     * @param mnemonic The key of the portfolio hierarchy object
     */
    Portfolio(@NotNull String mnemonic) {
        super(mnemonic);
    }

    @Override
    Institution addChild(@NotNull Common<?, ?, ?> hierarchyObject)
            throws ClassCastException {

        // Clear the lazy boy, and add the hierarchy object.
        lazyBoy.clear();
        return super.addChild(hierarchyObject);
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
    public @NotNull Account getLastToBeRebalanced() {
        return lazyBoy.getLazily();
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
