package com.garygregg.rebalance.distinguished;

import org.jetbrains.annotations.NotNull;

public class DistinguishedInstitutionDescription extends
        DistinguishedDescription<DistinguishedInstitutions, String> {

    /**
     * Constructs the description.
     *
     * @param key   The key of the description
     * @param value The value assigned to the key
     */
    public DistinguishedInstitutionDescription(@NotNull DistinguishedInstitutions key,
                                               @NotNull String value) {
        super(key, value);
    }
}
