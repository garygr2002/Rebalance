package com.garygregg.rebalance.distinguished;

import com.garygregg.rebalance.AccountKey;
import com.garygregg.rebalance.account.AccountLibrary;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class DistinguishedAccountLibrary extends
        DistinguishedLibrary<DistinguishedAccounts, DistinguishedAccountDescription, AccountKey> {

    // The singleton distinguished account library
    private static final DistinguishedAccountLibrary library =
            new DistinguishedAccountLibrary();

    // A map of distinguished accounts to their descriptions
    private final Map<DistinguishedAccounts, DistinguishedAccountDescription>
            distinguishedAccounts = new TreeMap<>();

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

    /**
     * Adds a distinguished account description to the library.
     *
     * @param description The distinguished account description to add to the
     *                    library
     * @return An existing distinguished account description that was displaced
     * in the library because it had the same key
     */
    @SuppressWarnings("UnusedReturnValue")
    DistinguishedAccountDescription addDescription(
            @NotNull DistinguishedAccountDescription description) {
        return distinguishedAccounts.put(description.getKey(), description);
    }

    @Override
    public boolean areKeyElementsOkay(String... elements) {
        return AccountLibrary.getInstance().areKeyElementsOkay(elements);
    }

    @Override
    public boolean areKeysSorted() {
        return (distinguishedAccounts instanceof SortedMap);
    }

    @Override
    protected void clearDescriptions() {
        distinguishedAccounts.clear();
    }

    @Override
    public DistinguishedAccountDescription[] getCatalog() {
        return distinguishedAccounts.values().toArray(
                new DistinguishedAccountDescription[0]);
    }

    @Override
    public @NotNull DistinguishedAccounts getDefaultKey() {
        return DistinguishedAccounts.DEFAULT;
    }

    @Override
    public DistinguishedAccountDescription getDescription(DistinguishedAccounts key) {
        return distinguishedAccounts.get(key);
    }

    @Override
    public int getElementCount() {
        return DistinguishedFields.values().length;
    }

    @Override
    public AccountKey getValue(@NotNull DistinguishedAccounts key) {

        /*
         * Get the description mapped to the key. Return null if there is no
         * such description, otherwise return the value of the description.
         */
        final DistinguishedAccountDescription description = getDescription(key);
        return (null == description) ? null : description.getValue();
    }
}
