package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.AccountKey;
import com.garygregg.rebalance.SynthesizerType;
import com.garygregg.rebalance.account.AccountDescription;
import com.garygregg.rebalance.distinguished.DistinguishedAccounts;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.TreeSet;

class Averaging extends Synthesizer {

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

    @Override
    public @NotNull SynthesizerType getType() {
        return SynthesizerType.AVERAGING;
    }

    @Override
    public boolean synthesize(@NotNull Account account) {

        // Call the superclass method. Was this successful?
        boolean result = super.synthesize(account);
        if (result) {

            // TODO: Fill this out.
        }

        // Return the result.
        return result;
    }
}
