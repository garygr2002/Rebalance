package com.garygregg.rebalance.toolkit;

public enum CategoryType {

    // All types
    ALL,

    // A credit (loan, or credit card balance)
    CREDIT,

    // For containers that are not accounts
    NOT_AN_ACCOUNT,

    // Taxable investments, currently including treasury securities
    TAXABLE,

    // HSA, inherited IRA, real estate, or non-Roth annuity, 401k or IRA
    TAX_DEFERRED,

    // Roth annuity, 401k or IRA
    TAX_PAID
}
