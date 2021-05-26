package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.AccountKey;
import com.garygregg.rebalance.MessageLogger;
import com.garygregg.rebalance.distinguished.DistinguishedAccountLibrary;
import com.garygregg.rebalance.distinguished.DistinguishedAccounts;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

abstract class Synthesizer {

    // Our local message logger
    private final MessageLogger messageLogger = new MessageLogger();

    /**
     * Gets the distinguished account for which this synthesizer is intended.
     *
     * @return The distinguished account for which this synthesizer is intended
     */
    public abstract @NotNull DistinguishedAccounts getAccount();

    /**
     * Gets the account key for which this synthesizer is intended.
     *
     * @return The account key for which this synthesizer is intended
     */
    public AccountKey getKey() {
        return DistinguishedAccountLibrary.getInstance().
                getValue(getAccount());
    }

    /**
     * Gets the message logger for the synthesizer.
     *
     * @return The message logger for the synthesizer
     */
    protected MessageLogger getLogger() {
        return messageLogger;
    }

    /**
     * Synthesizes an account.
     *
     * @param account The account to synthesize
     * @return True if the account was successfully synthesized, false
     * otherwise
     */
    public boolean synthesize(@NotNull Account account) {

        /*
         * Note: This method should be first called by any class overriding
         * it, and cancel its work if the result is false. Reset the logger
         * for problems. Get the key of given account, and the intended key.
         */
        getLogger().resetProblem();
        final AccountKey givenKey = account.getKey();
        final AccountKey intendedKey = getKey();

        // The intended key should equal the given key. Is this not so?
        boolean result = (null != intendedKey) &&
                (0 == intendedKey.compareTo(givenKey));
        if (!result) {

            // The intended key does not equal the given key. Log a warning.
            getLogger().logMessage(Level.WARNING, String.format("Rejecting " +
                            "attempt to synthesize account with key '%s' using a " +
                            "synthesizer intended for key '%s'.", givenKey,
                    intendedKey));
        }

        /*
         * The intended key equals the given key. Value must not have been set
         * in the account for it to be synthesized. Has value already been set
         * in the account?
         */
        else if (!(result = !account.hasValueBeenSet())) {

            // Value has already been set in the account. Log a warning.
            getLogger().logMessage(Level.WARNING, String.format("Rejecting " +
                    "attempt to synthesize account with key '%s' that " +
                    "already has value set.", givenKey));
        }

        /*
         * The account does not already have value set. The account must not
         * have children in order to be synthesized. Does the account have
         * children?
         */
        else if (!(result = !account.hasChildren())) {

            // The account has children. Log a warning.
            getLogger().logMessage(Level.WARNING, String.format("Rejecting " +
                    "attempt to synthesize account with key '%s' that " +
                    "has children.", givenKey));
        }

        // The account does not have children.
        else {

            /*
             * Log some information about beginning synthesis. Set the account
             * as synthesized.
             */
            getLogger().logMessage(Level.FINEST, String.format("Beginning " +
                    "synthesis for account with key '%s'.", givenKey));
            account.setSynthesized();
        }

        // Return the result.
        return result;
    }
}
