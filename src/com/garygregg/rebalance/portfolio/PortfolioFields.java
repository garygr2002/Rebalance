package com.garygregg.rebalance.portfolio;

import com.garygregg.rebalance.toolkit.WeightType;

enum PortfolioFields {

    // Mnemonic of the portfolio (and the key)
    MNEMONIC(0, null),

    // Name of the portfolio
    NAME(1, null),

    // Birthdate of the portfolio owner
    BIRTH_DATE(2, null),

    // Projected mortality date of the portfolio owner
    MORTALITY_DATE(3, null),

    // The taxpayer filing status
    FILING_STATUS(4, null),

    // Social Security monthly income starting at age 62
    SS_MONTHLY(5, null),

    // CPI adjusted monthly income
    CPI_MONTHLY(6, null),

    // Non-CPI adjusted monthly income
    NON_CPI_MONTHLY(7, null),

    // Taxable annual income of the portfolio owner
    TAXABLE_ANNUAL(8, null),

    // Desired percentage stocks in the portfolio
    PERCENTAGE_STOCK(9, WeightType.STOCK),

    // Desired percentage bonds in the portfolio
    PERCENTAGE_BOND(10, WeightType.BOND),

    // Desired percentage cash in the portfolio
    PERCENTAGE_CASH(11, WeightType.CASH),

    // Desired percentage real estate in the portfolio
    PERCENTAGE_REAL_ESTATE(12, WeightType.REAL_ESTATE),

    // Percentage adjustment of the equity allocation at market index zero
    INCREASE_AT_ZERO(13, null),

    /*
     * Percentage adjustment of the equity allocation at the bear market
     * threshold
     */
    INCREASE_AT_BEAR(14, null);

    // The position of the field in the data file
    private final int position;

    /*
     * The weight type associated with fields for desired percentages
     * (null for other fields)
     */
    private final WeightType type;

    /**
     * Constructs the portfolio fields enumerator.
     *
     * @param position The position of the field in the data file
     * @param type     The weight type associated with fields for desired
     *                 percentages (null for other fields)
     */
    PortfolioFields(int position, WeightType type) {

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
     * Gets the associated weight type of the field.
     *
     * @return The weight type associated with fields for desired
     * percentages (null for other fields)
     */
    public WeightType getType() {
        return type;
    }
}
