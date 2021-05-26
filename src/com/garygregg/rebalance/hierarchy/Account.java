package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.AccountKey;
import com.garygregg.rebalance.FundType;
import com.garygregg.rebalance.HoldingLineType;
import com.garygregg.rebalance.TaxType;
import com.garygregg.rebalance.account.AccountDescription;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Account extends
        Aggregate<AccountKey, Ticker, AccountDescription> {

    // A map of account synthesizers.
    private static final Map<AccountKey, Synthesizer> synthesizerMap =
            new HashMap<>();

    static {

        // Put the house sales cost synthesizer in the map.
        Synthesizer synthesizer = new HouseSalesCosts();
        synthesizerMap.put(synthesizer.getKey(), synthesizer);

        // Put a my pension synthesizer in the map.
        synthesizer = new MyPension();
        synthesizerMap.put(synthesizer.getKey(), synthesizer);
    }

    // Is this ticker synthesized (or programmatically modified)?
    private boolean synthesized;

    /**
     * Constructs the account hierarchy object.
     *
     * @param key The key of the account hierarchy object
     */
    Account(@NotNull AccountKey key) {
        super(key);
    }

    @Override
    public @NotNull HoldingLineType getLineType() {
        return HoldingLineType.ACCOUNT;
    }

    @Override
    public boolean hasFundType(@NotNull FundType type) {
        return type.equals(FundType.NOT_A_FUND);
    }

    @Override
    public boolean hasTaxType(@NotNull TaxType type) {

        // The account description cannot be null for a 'true' result.
        final AccountDescription description = getDescription();
        boolean result = (null != description);
        if (result) {

            /*
             * The account description is not null. Return true if the tax
             * type matches the given type.
             */
            final TaxType descriptionType = description.getType();
            result = (null != descriptionType) &&
                    descriptionType.equals(type);
        }

        // Return the result.
        return result;
    }

    /**
     * Gets whether this account is synthesized, or programmatically modified.
     *
     * @return True if the ticker is synthesized, or programmatically
     * modified; false otherwise
     */
    public boolean isSynthesized() {
        return synthesized;
    }

    /**
     * Sets the account as synthesized, or programmatically modified.
     */
    void setSynthesized() {
        this.synthesized = true;
    }

    @Override
    boolean synthesizeIf() {

        /*
         * Call the superclass method. Did the superclass method succeed,
         * and not either of: 1) value set in the account, or; 2) the account
         * has children?
         */
        boolean result = super.synthesizeIf();
        if (result && !(hasValueBeenSet() || hasChildren())) {

            /*
             * The superclass method succeeded, and the account neither has
             * existing value nor children. Get a synthesizer from the
             * synthesizer map, if any. Re-initialize the return value if
             * there is no synthesizer, or there is a synthesizer and the
             * account was successful synthesized.
             */
            final Synthesizer synthesizer = synthesizerMap.get(getKey());
            result = (null == synthesizer) || synthesizer.synthesize(this);
        }

        return result;
    }
}
