package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.Description;
import com.garygregg.rebalance.FundType;
import com.garygregg.rebalance.HoldingLineType;
import com.garygregg.rebalance.TaxType;
import org.jetbrains.annotations.NotNull;

public class Institution extends Aggregate<
        String,
        Account,
        Description<String>> {

    /**
     * Constructs the institution hierarchy object.
     *
     * @param mnemonic The key of the institution hierarchy object
     */
    Institution(@NotNull String mnemonic) {
        super(mnemonic);
    }

    @Override
    public @NotNull HoldingLineType getLineType() {
        return HoldingLineType.INSTITUTION;
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
    protected void setDescription(Description<String> description) {

        // Right now you cannot set an institution description.
        throw new UnsupportedOperationException("Attempt to set institution " +
                "description; institutions currently have no description.");
    }
}
