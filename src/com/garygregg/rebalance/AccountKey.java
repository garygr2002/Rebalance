package com.garygregg.rebalance;

import com.garygregg.rebalance.interpreter.LongInterpreter;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AccountKey extends Pair<String, Long> implements
        Comparable<AccountKey> {

    // Our logger
    private static final Logger logger =
            Logger.getLogger(AccountKey.class.getCanonicalName());

    // Our account number interpreter
    private static final LongInterpreter accountNumberInterpreter =
            new LongInterpreter() {

                @Override
                protected void receiveException(@NotNull Exception exception,
                                                @NotNull String string) {
                    logger.log(Level.WARNING, String.format("Received an " +
                                    "exception of type '%s' with message " +
                                    "'%s' when trying to parse '%s' as an " +
                                    "account number; using null.",
                            exception.getClass().getSimpleName(),
                            exception.getMessage(), string));
                }
            };

    /*
     * The separator between institution and account number that produces a
     * unique key
     */
    private static final String unitSeparator = "\u001f";

    /**
     * Constructs an account key from its constituents.
     *
     * @param mnemonic The institution mnemonic
     * @param number   The account number
     */
    public AccountKey(String mnemonic, Long number) {
        super(mnemonic, number);
    }

    /**
     * Creates an account key from a unique string representation.
     *
     * @param uniqueKey A unique string representation of the account key
     * @return A new account key object
     */
    public static @NotNull AccountKey createKey(@NotNull String uniqueKey) {

        /*
         * Declare the result, and variables for account number and
         * institution.
         */
        AccountKey result;
        Long accountNumber;
        String institution = null;

        /*
         * Split the unique key string into elements around the unit
         * separator. Are there two or more elements in the result?
         */
        final String[] elements = uniqueKey.split(unitSeparator);
        int elementsLength = elements.length;
        if (1 < elementsLength) {

            /*
             * There are two or more elements from splitting the unique key
             * string around the unit separator. Account number and institution
             * are both specified. Get them both. Reinitialize the result
             * around the institution and account number.
             */
            accountNumber = parseLong(elements[--elementsLength]);
            institution = elements[--elementsLength];
            result = new AccountKey(institution, accountNumber);
        }

        // Only one element was specified.
        else {

            /*
             * Assume the single element is an account number unless it cannot
             * be parsed as a long integer. Can the element not be parsed as a
             * long integer?
             */
            accountNumber = parseLong(elements[--elementsLength]);
            if (null == accountNumber) {

                /*
                 * The single element cannot be parsed as a long integer.
                 * Assume instead that the single element is an institution
                 * name.
                 */
                institution = elements[elementsLength];
            }

            // Re-initialize the result.
            result = new AccountKey(institution, accountNumber);
        }

        // Return the result.
        return result;
    }

    /**
     * Creates a unique key from an institution and account number.
     *
     * @param institution   An institution
     * @param accountNumber An account number
     * @return A unique key created from an institution and account number
     */
    public static String createUniqueKey(String institution,
                                         Long accountNumber) {

        // Declare and initialize the result. Is the institution null?
        String result = null;
        if (null == institution) {

            /*
             * The institution is null. Reinitialize the result to the string
             * representation of the account number without either a leading
             * or trailing field separator.
             */
            if (null != accountNumber) {
                result = accountNumber.toString();
            }
        }

        /*
         * The institution is not null. Format the result with the institution
         * followed by a trailing separator. If the account number is null, add
         * a blank suffix. If the account number is not null, add the string
         * representation of the account number as a suffix.
         */
        else {
            result = String.format("%s%s%s", institution, unitSeparator,
                    (null == accountNumber) ? "" : accountNumber.toString());
        }

        // Return the result.
        return result;
    }

    /**
     * Formats an account number in a standard format.
     *
     * @param number The account number to format
     * @return The account number formatted in a standardized way
     */
    public static @NotNull String format(@NotNull Long number) {
        return String.format("%016d", number);
    }

    /**
     * Parses a long integer.
     *
     * @param value A value to parse
     * @return A valid long if the value had been parseable, otherwise null.
     */
    public static Long parseLong(@NotNull String value) {
        return accountNumberInterpreter.interpret(value);
    }

    /**
     * Combines an institution mnemonic and an account number into a
     * standardized string representation.
     *
     * @param institution   An institution mnemonic
     * @param accountNumber An account number
     * @return A standardized string representation of the institution
     * mnemonic and the account number
     */
    public static @NotNull String toString(String institution,
                                           Long accountNumber) {
        return combine(institution, (null == accountNumber) ? "" :
                format(accountNumber));
    }

    /**
     * Parses a unique key into a standard format.
     *
     * @param string A unique key
     * @return An standard format for the unique key
     */
    public static @NotNull String toString(@NotNull String string) {

        /*
         * Separate the unique key around the unit separator. Format the
         * elements as an institution and account number.
         */
        final String[] elements = string.split(unitSeparator);
        return toString((1 < elements.length) ? elements[0] : null,
                parseLong(elements[elements.length - 1]));
    }

    @Override
    public int compareTo(@NotNull AccountKey accountKey) {
        return compare((Pair<String, Long>) this, accountKey);
    }

    /**
     * Creates a unique key from the institution mnemonic and account number.
     *
     * @return A unique key created from the institution mnemonic and account
     * number
     */
    public String createUniqueKey() {
        return createUniqueKey(getFirst(), getSecond());
    }

    @Override
    public String toString() {
        return toString(getFirst(), getSecond());
    }
}
