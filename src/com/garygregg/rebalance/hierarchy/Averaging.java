package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.AccountKey;
import com.garygregg.rebalance.MessageLogger;
import com.garygregg.rebalance.SynthesizerType;
import com.garygregg.rebalance.account.AccountDescription;
import com.garygregg.rebalance.countable.MutableCurrency;
import com.garygregg.rebalance.distinguished.DistinguishedAccounts;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;

class Averaging extends Synthesizer {

    // Value by 'considered'
    private final static Valuator byConsidered =
            ValueByConsidered.getInstance();

    // Value by 'not considered'
    private final static Valuator byNotConsidered =
            ValueByNotConsidered.getInstance();

    /**
     * Constructs the averaging synthesizer.
     *
     * @param account The distinguished account associated with this
     *                synthesizer
     */
    public Averaging(@NotNull DistinguishedAccounts account) {
        super(account);
    }

    /**
     * Divides a value.
     *
     * @param value       The value to divide
     * @param denominator The denominator of the division
     * @return The divided value
     */
    private static double divide(double value, int denominator) {
        return (0 == denominator) ? 0. : value / denominator;
    }

    /**
     * Gets the keys of referenced accounts.
     *
     * @param account An account
     * @return The keys of referenced accounts
     */
    private static @NotNull AccountKey @NotNull [] getReferencedAccounts(
            @NotNull Account account) {

        /*
         * Declare and initialize a sorted set of account keys. Get the
         * description from the account. Is the description not null?
         */
        final Set<AccountKey> keys = new TreeSet<>();
        final AccountDescription description = account.getDescription();
        if (null != description) {

            /*
             * The description is not null. Get the institution mnemonic from
             * the account key.
             */
            final String institutionMnemonic = account.getKey().getFirst();

            /*
             * Get the referenced account numbers from the account description.
             * Cycle for each account number.
             */
            final Long[] referencedAccounts =
                    description.getReferencedAccounts();
            for (Long referencedAccount : referencedAccounts) {

                /*
                 * Add a new account key to the set consisting of the
                 * institution mnemonic and the referenced account number.
                 */
                keys.add(new AccountKey(institutionMnemonic, referencedAccount));
            }
        }

        // Return the account keys as an array.
        return keys.toArray(new AccountKey[0]);
    }

    /**
     * Sums the value of a collection of hierarchy objects.
     *
     * @param hierarchyObjects A collection of hierarchy objects
     * @param valuator         A valuator for hierarchy objects
     * @return The sum of the values in the collection
     */
    private static double sum(@NotNull Collection<Common<?, ?, ?>> hierarchyObjects,
                              @NotNull Valuator valuator) {

        // Declare and initialize the sum. Are there any hierarchy objects?
        double sum = 0.;
        if (!hierarchyObjects.isEmpty()) {

            /*
             * There are hierarchy objects. Declare and initialize mutable
             * currency initialized with the empty sum. Cycle for each
             * hierarchy object.
             */
            final MutableCurrency currency = new MutableCurrency(sum);
            for (Common<?, ?, ?> hierarchyObject : hierarchyObjects) {

                // Add the value of the first/next hierarchy object to the sum.
                currency.add(valuator.getValue(hierarchyObject));
            }

            // Get the summed value.
            sum = currency.getValue();
        }

        // Return the sum.
        return sum;
    }

    @Override
    public @NotNull SynthesizerType getType() {
        return SynthesizerType.AVERAGING;
    }

    /**
     * Sets the valuation of an account.
     *
     * @param account                The account to set
     * @param considered             The considered valuation of the account
     * @param notConsidered          The not-considered valuation of the account
     * @param referencedAccountCount The number of referenced accounts
     */
    protected void setValuation(@NotNull Account account,
                                double considered,
                                double notConsidered,
                                int referencedAccountCount) {

        // Set the considered and not-considered valuation in the account.
        account.setConsidered(divide(considered, referencedAccountCount));
        account.setNotConsidered(divide(notConsidered,
                referencedAccountCount));
    }

    @Override
    public boolean synthesize(@NotNull Account account) {

        // Call the superclass method. Was this successful?
        boolean result = super.synthesize(account);
        if (result) {

            /*
             * The superclass method succeeded. The referenced accounts are the
             * estimates used by this synthesizer. Get their keys. Declare a
             * variable to receive an estimate.
             */
            final AccountKey[] estimateKeys = getReferencedAccounts(account);
            Account estimate;

            // Get a hierarchy object and the message logger.
            final Hierarchy hierarchy = Hierarchy.getInstance();
            final MessageLogger logger = getLogger();

            /*
             * Declare a list to receive house price estimates. Cycle for each
             * estimate account key.
             */
            final List<Common<?, ?, ?>> estimateList = new ArrayList<>();
            for (AccountKey estimateKey : estimateKeys) {

                /*
                 * Get the estimate for the first/next key. Is the estimate
                 * null?
                 */
                estimate = hierarchy.getAccount(estimateKey);
                if (null == estimate) {

                    // The estimate is null. Log a warning.
                    logger.logMessage(Level.WARNING, String.format("Unable " +
                            "to retrieve house estimate with key '%s'; its " +
                            "account is missing.", estimateKey));
                }

                // The estimate is not null. Add it to the account list.
                else {
                    estimateList.add(estimate);
                }
            }

            /*
             * Set the 'considered' and 'not considered' values in the target
             * account by averaging the estimates. Re-initialize the result.
             */
            setValuation(account, sum(estimateList, byConsidered),
                    sum(estimateList, byNotConsidered), estimateList.size());
            result = !logger.hadProblem1();
        }

        // Return the result.
        return result;
    }
}
