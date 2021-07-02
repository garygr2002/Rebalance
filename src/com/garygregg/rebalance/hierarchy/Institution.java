package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.*;
import org.jetbrains.annotations.NotNull;

public class Institution extends SuperAggregate<
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
    protected @NotNull Account getNewArtificialChild() {
        return new Account(new AccountKey(Library.getDefaultStringKey(),
                AccountKeyLibrary.getDefaultAccountNumber()));
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
    protected void setDescription(Description<String> description) {

        // Right now you cannot set an institution description.
        throw new UnsupportedOperationException("Attempt to set institution " +
                "description; institutions currently have no description.");
    }
}
