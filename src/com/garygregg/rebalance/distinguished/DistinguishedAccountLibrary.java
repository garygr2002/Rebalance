package com.garygregg.rebalance.distinguished;

import com.garygregg.rebalance.AccountKey;
import com.garygregg.rebalance.account.AccountLibrary;
import org.jetbrains.annotations.NotNull;

public class DistinguishedAccountLibrary extends
        DistinguishedLibrary<DistinguishedAccount,
                DistinguishedAccountDescription, AccountKey> {

    // The singleton distinguished account library
    private static final DistinguishedAccountLibrary library =
            new DistinguishedAccountLibrary();

    /**
     * Constructs the distinguished account library.
     */
    private DistinguishedAccountLibrary() {

        // Nothing to add to the line codes.
    }

    /**
     * Gets a distinguished account library instance.
     *
     * @return A distinguished account library instance
     */
    public static @NotNull DistinguishedAccountLibrary getInstance() {
        return library;
    }

    @Override
    public boolean areKeyElementsOkay(String @NotNull ... elements) {
        return AccountLibrary.getInstance().areKeyElementsOkay(elements);
    }

    @Override
    public DistinguishedAccountDescription[] getCatalog() {
        return getDescriptions().toArray(
                new DistinguishedAccountDescription[0]);
    }

    @Override
    public @NotNull DistinguishedAccount getDefaultKey() {
        return DistinguishedAccount.DEFAULT;
    }

    @Override
    public int getElementCount() {
        return DistinguishedFields.values().length;
    }

    @Override
    public AccountKey getValue(@NotNull DistinguishedAccount key) {

        /*
         * Get the description mapped to the key. Return null if there is no
         * such description, otherwise return the value of the description.
         */
        final DistinguishedAccountDescription description =
                getDescription(key);
        return (null == description) ? null : description.getValue();
    }
}
