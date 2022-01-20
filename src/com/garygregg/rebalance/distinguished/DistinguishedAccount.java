package com.garygregg.rebalance.distinguished;

public enum DistinguishedAccount {

    // The brokerage account
    BROKERAGE,

    // Capital gains tax on house sale
    CAPITAL_GAINS_TAX,

    // The default (unused)
    DEFAULT,

    // House averaging estimate
    ESTIMATE_AVERAGING,

    // The 1st 401k
    FIRST_401K,

    // The Health Savings Account
    HEALTH_SAVINGS_ACCOUNT,

    // The 1st house estimate
    HOUSE_ESTIMATE_1,

    // The 2nd house estimate
    HOUSE_ESTIMATE_2,

    // Costs required to sell the home at best price
    HOUSE_SALES_COSTS,

    // The large beneficiary IRA
    LARGE_BENEFICIARY,

    // Pension
    PENSION,

    // The 2020 rollover IRA
    ROLLOVER_2020,

    // The Roth IRA mature by 2020
    ROTH_IRA_2020,

    // The Roth IRA mature by 2024
    ROTH_IRA_2024,

    // The Roth IRA mature by 2025
    ROTH_IRA_2025,

    // The 2nd 401k
    SECOND_401K,

    // The commission a sales agent will charge for selling the house
    SELLER_AGENT_COMMISSION,

    // The small beneficiary IRA
    SMALL_BENEFICIARY,

    // Social Security
    SOCIAL_SECURITY,

    // Washington Real Estate Excise Tax
    @SuppressWarnings("SpellCheckingInspection") WASHINGTON_REET
}
