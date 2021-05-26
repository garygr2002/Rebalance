package com.garygregg.rebalance.account;

import com.garygregg.rebalance.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class AccountDescription implements Description<AccountKey> {

    // The number of the account
    private final Long accountNumber;

    // A map of fund types to their desired allocation weights
    private final Map<FundType, Double> allocation = new HashMap<>();

    // The description key
    private final AccountKey key;

    // The name of the account
    private final String name;

    // The rebalance order of the account
    private final Long rebalanceOrder;

    // The rebalance procedure of the account
    private final RebalanceProcedure rebalanceProcedure;

    // The tax type of the account
    private final TaxType type;

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
     * Adjusts the desired allocation weight of the designated fund type.
     *
     * @param type  The designated fund type
     * @param value The desired allocation weight
     */
    void adjustAllocation(@NotNull FundType type, double value) {
        allocation.put(type, value);
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
     * Gets the tax type of the account.
     *
     * @return The tax type of the account
     */
    public TaxType getType() {
        return type;
    }
}
