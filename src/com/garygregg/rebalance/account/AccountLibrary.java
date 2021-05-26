package com.garygregg.rebalance.account;

import com.garygregg.rebalance.AccountKey;
import com.garygregg.rebalance.AccountKeyLibrary;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class AccountLibrary extends AccountKeyLibrary<AccountDescription> {

    // The singleton account library
    private static final AccountLibrary library = new AccountLibrary();

    // A map of account numbers to account description objects
    private final Map<AccountKey, AccountDescription> accounts =
            new TreeMap<>();

    /**
     * Constructs the account library.
     */
    private AccountLibrary() {
        addLineCode('A');
    }

    /**
     * Gets an account library instance.
     *
     * @return An account library instance
     */
    public static @NotNull AccountLibrary getInstance() {
        return library;
    }

    /**
     * Adds an account description to the library.
     *
     * @param description The account description to add to the library
     * @return An existing account description that was displaced in the
     * library because it had the same number
     */
    AccountDescription addDescription(@NotNull AccountDescription description) {
        return accounts.put(description.getKey(), description);
    }

    @Override
    public boolean areKeysSorted() {
        return (accounts instanceof SortedMap);
    }

    @Override
    protected void clearDescriptions() {
        accounts.clear();
    }

    @Override
    public AccountDescription[] getCatalog() {
        return accounts.values().toArray(new AccountDescription[0]);
    }

    @Override
    public AccountDescription getDescription(AccountKey key) {
        return accounts.get(key);
    }

    @Override
    public int getElementCount() {
        return AccountFields.values().length;
    }
}
