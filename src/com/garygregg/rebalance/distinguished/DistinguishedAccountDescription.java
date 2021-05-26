package com.garygregg.rebalance.distinguished;

import com.garygregg.rebalance.AccountKey;
import org.jetbrains.annotations.NotNull;

public class DistinguishedAccountDescription extends
        DistinguishedDescription<DistinguishedAccounts, AccountKey> {

    /**
     * Constructs the description.
     *
     * @param key   The key of the description
     * @param value The value assigned to the key
     */
    public DistinguishedAccountDescription(@NotNull DistinguishedAccounts key,
                                           @NotNull AccountKey value) {
        super(key, value);
    }
}
