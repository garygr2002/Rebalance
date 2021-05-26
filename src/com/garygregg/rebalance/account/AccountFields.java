package com.garygregg.rebalance.account;

import com.garygregg.rebalance.FundType;

enum AccountFields {

    // The institution where the account is held
    INSTITUTION(0, null),

    // Name of the account
    NAME(3, null),

    // Number of the account (and the key)
    NUMBER(1, null),

    // Desired percentage bonds in the account
    PERCENTAGE_BONDS(7, FundType.BOND),

    // Desired percentage cash in the account
    PERCENTAGE_CASH(8, FundType.CASH),

    // Desired percentage stocks in the account
    PERCENTAGE_EQUITIES(6, FundType.STOCK),

    // Desired percentage real estate in the account
    PERCENTAGE_REAL_ESTATE(9, FundType.REAL_ESTATE),

    /*
     * The rebalance order of the account; accounts with the same declared
     * order are rebalance in the order they occur in the data file
     */
    REBALANCE_ORDER(2, null),

    // The rebalance procedure for the account
    REBALANCE_PROCEDURE(5, null),

    // The tax type of the account
    TYPE(4, null);

    // The position of the field in the data file
    final int position;

    /*
     * The fund type associated with fields for desired percentages
     * (null for other fields)
     */
    final FundType type;

    /**
     * Constructs the account fields enumerator.
     *
     * @param position The position of the field in the data file
     * @param type     The fund type associated with fields for desired
     *                 percentages (null for other fields)
     */
    AccountFields(int position, FundType type) {

        // Initialize the member variables.
        this.type = type;
        this.position = position;
    }

    /**
     * Gets the position of the field.
     *
     * @return The position of the field
     */
    public int getPosition() {
        return position;
    }

    /**
     * Gets the associated fund type of the field.
     *
     * @return The fund type associated with fields for desired
     * percentages (null for other fields)
     */
    public FundType getType() {
        return type;
    }
}
