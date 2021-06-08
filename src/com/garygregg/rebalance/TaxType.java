package com.garygregg.rebalance;

public enum TaxType {

    // All types
    ALL,

    // A credit (loan, or credit card balance)
    CREDIT,

    // Health Savings Account
    HSA,

    // An inherited IRA
    INHERITED_IRA,

    // A regular 401k
    NON_ROTH_401K,

    // A regular annuity
    NON_ROTH_ANNUITY,

    // A regular contributory IRA
    NON_ROTH_IRA,

    // For containers that are not accounts
    NOT_AN_ACCOUNT,

    // A pension plan
    PENSION,

    // A real estate holding
    REAL_ESTATE,

    // A Roth annuity
    ROTH_ANNUITY,

    // A Roth 401k
    ROTH_401K,

    // A Roth contributory IRA
    ROTH_IRA,

    // A taxable account
    TAXABLE,

    // A treasury account
    TREASURY
}
