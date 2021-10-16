package com.garygregg.rebalance;

import org.jetbrains.annotations.NotNull;

public abstract class AccountKeyLibrary<ContainerType> extends
        Library<AccountKey, ContainerType> {

    // The default account
    private static final Long defaultAccountNumber = 0L;

    // The default key
    private static final AccountKey defaultKey =
            new AccountKey(Library.getDefaultStringKey(),
                    defaultAccountNumber);

    /**
     * Formats an account number in a standardized way.
     *
     * @param number An account number
     * @return The account number formatted in a standardized way
     */
    public static String format(@NotNull Long number) {
        return AccountKey.format(number);
    }

    /**
     * Gets the default account number.
     *
     * @return The default account number
     */
    public static @NotNull Long getDefaultAccountNumber() {
        return defaultAccountNumber;
    }

    @Override
    public boolean areKeyElementsOkay(String @NotNull ... elements) {

        /*
         * There must be exactly two elements. The second element must be
         * parseable as a long integer, and not equal to the default account
         * number. The first element must be acceptable to the superclass.
         */
        Long element;
        int index = 2;
        return (elements.length == (index--)) &&
                (null != (element = AccountKey.parseLong(elements[index--])) &&
                        (!element.equals(getDefaultAccountNumber())) &&
                        super.areKeyElementsOkay(elements[index]));
    }

    @Override
    public @NotNull AccountKey getDefaultKey() {
        return defaultKey;
    }
}
