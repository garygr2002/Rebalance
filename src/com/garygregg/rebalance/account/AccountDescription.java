package com.garygregg.rebalance.account;

import com.garygregg.rebalance.toolkit.*;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class AccountDescription implements Comparable<AccountDescription>,
        Description<AccountKey> {

    // The number of the account
    private final Long accountNumber;

    // A map of fund types to their desired allocation weights
    private final Map<FundType, Double> allocation =
            new EnumMap<>(FundType.class);

    // The description key
    private final AccountKey key;

    // The name of the account
    private final String name;

    // The rebalance order of the account
    private final Long rebalanceOrder;

    // The rebalance procedure of the account
    private final RebalanceProcedure rebalanceProcedure;

    // The set of referenced accounts
    private final Set<Long> referencedAccounts = new TreeSet<>();

    // The tax type of the account
    private final TaxType type;

    // The synthesizer type to be used for the account value (if needed)
    private SynthesizerType synthesizerType;

    /**
     * Constructs the account description.
     *
     * @param institution        The institution where the account is held
     * @param accountNumber      The number of the account
     * @param rebalanceOrder     The rebalance order of the account
     * @param name               The name of the account
     * @param type               The tax type of the account
     * @param rebalanceProcedure The rebalance procedure of the account
     */
    AccountDescription(@NotNull String institution,
                       @NotNull Long accountNumber, Long rebalanceOrder,
                       String name,
                       TaxType type,
                       RebalanceProcedure rebalanceProcedure) {

        // Set all the member fields.
        this.accountNumber = accountNumber;
        this.key = new AccountKey(institution, accountNumber);
        this.name = name;
        this.rebalanceOrder = rebalanceOrder;
        this.rebalanceProcedure = rebalanceProcedure;
        this.type = type;
    }

    /**
     * Adds a referenced account.
     *
     * @param accountNumber The number of the referenced account
     * @return True if the account number had not already been added
     */
    @SuppressWarnings("UnusedReturnValue")
    boolean addReferencedAccount(@NotNull Long accountNumber) {
        return referencedAccounts.add(accountNumber);
    }

    /**
     * Adjusts the desired allocation weight of the designated fund type.
     *
     * @param type  The designated fund type
     * @param value The desired allocation weight
     */
    void adjustAllocation(@NotNull FundType type, Double value) {
        allocation.put(type, value);
    }

    @Override
    public int compareTo(@NotNull AccountDescription accountDescription) {

        // Get his rebalance order and my rebalance order.
        final Long his = accountDescription.getRebalanceOrder();
        final Long mine = getRebalanceOrder();

        // Calculate the return value.
        return (null == mine) ? ((null == his) ? 0 : -1) :
                ((null == his) ? 1 : mine.compareTo(his));
    }

    /**
     * Gets the desired allocation weight of the designated fund type.
     *
     * @param type The designated fund type
     * @return The desired allocation weight for the designated fund type
     */
    public Double getAllocation(FundType type) {
        return allocation.get(type);
    }

    /**
     * Gets the institution where the account is held.
     *
     * @return The institution where the account is held
     */
    public String getInstitution() {
        return key.getFirst();
    }

    @Override
    public @NotNull AccountKey getKey() {
        return key;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Gets the number of the account.
     *
     * @return The number of the account
     */
    public @NotNull Long getNumber() {
        return accountNumber;
    }

    /**
     * Gets the rebalance order of the account.
     *
     * @return The rebalance order of the account
     */
    public Long getRebalanceOrder() {
        return rebalanceOrder;
    }

    /**
     * Gets the rebalance procedure of the account.
     *
     * @return The rebalance procedure of the account
     */
    public RebalanceProcedure getRebalanceProcedure() {
        return rebalanceProcedure;
    }

    /**
     * Gets the referenced accounts.
     *
     * @return The referenced accounts
     */
    public @NotNull Long @NotNull [] getReferencedAccounts() {
        return referencedAccounts.toArray(new Long[0]);
    }

    /**
     * Gets the synthesizer type to be used for the account value.
     *
     * @return The synthesizer type to be used for the account value
     */
    public SynthesizerType getSynthesizerType() {
        return synthesizerType;
    }

    /**
     * Gets the tax type of the account.
     *
     * @return The tax type of the account
     */
    public TaxType getType() {
        return type;
    }

    /**
     * Sets the synthesizer type to be used for the account value.
     *
     * @param synthesizerType The synthesizer type to be used for the account
     *                        value
     */
    void setSynthesizerType(SynthesizerType synthesizerType) {
        this.synthesizerType = synthesizerType;
    }
}
