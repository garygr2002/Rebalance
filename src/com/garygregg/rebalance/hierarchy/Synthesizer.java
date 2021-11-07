package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.AccountKey;
import com.garygregg.rebalance.MessageLogger;
import com.garygregg.rebalance.SynthesizerType;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

abstract class Synthesizer {

    // Our local message logger
    private final MessageLogger messageLogger = new MessageLogger();

    /**
     * Gets the message logger for the synthesizer.
     *
     * @return The message logger for the synthesizer
     */
    protected MessageLogger getLogger() {
        return messageLogger;
    }

    /**
     * Gets the type associated with this synthesizer class.
     *
     * @return The type associated with this synthesizer class
     */
    public abstract @NotNull SynthesizerType getType();

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
         * for problems. Get the key of given account.
         */
        getLogger().resetProblem1();
        final AccountKey givenKey = account.getKey();

        /*
         * Value must not have been set in the account for it to be
         * synthesized. Has value already been set in the account?
         */
        boolean result = !account.hasValueBeenSet();
        if (!result) {

            // Value has already been set in the account. Log a warning.
            getLogger().log(Level.WARNING, String.format("Rejecting " +
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
            getLogger().log(Level.WARNING, String.format("Rejecting " +
                    "attempt to synthesize account with key '%s' that " +
                    "has children.", givenKey));
        }

        // The account does not have children.
        else {

            /*
             * Log some information about beginning synthesis. Set the account
             * as synthesized.
             */
            getLogger().log(Level.FINEST, String.format("Beginning " +
                    "synthesis for account with key '%s'.", givenKey));
            account.setSynthesized();
        }

        // Return the result.
        return result;
    }
}
