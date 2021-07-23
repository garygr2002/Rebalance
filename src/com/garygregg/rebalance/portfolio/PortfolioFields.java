package com.garygregg.rebalance.portfolio;

import com.garygregg.rebalance.WeightType;

enum PortfolioFields {

    // Mnemonic of the portfolio (and the key)
    MNEMONIC(0, null),

    // Name of the portfolio
    NAME(1, null),

    // Birth date of the portfolio owner
    BIRTH_DATE(2, null),

    // Projected mortality date of the portfolio owner
    MORTALITY_DATE(3, null),

    // Monthly SSN income starting at age 62
    SSN_MONTHLY(4, null),

    // Other current monthly annuity income
    OTHER_MONTHLY(5, null),

    // Flag to indicate if OTHER_MONTHLY is CPI adjusted
    CPI_ADJUSTED(6, null),

    // Taxable annual income of the portfolio owner
    TAXABLE_ANNUAL(7, null),

    // Desired percentage stocks in the portfolio
    PERCENTAGE_STOCK(8, WeightType.STOCK),

    // Desired percentage bonds in the portfolio
    PERCENTAGE_BOND(9, WeightType.BOND),

    // Desired percentage cash in the portfolio
    PERCENTAGE_CASH(10, WeightType.CASH),

    // Desired percentage real estate in the portfolio
    PERCENTAGE_REAL_ESTATE(11, WeightType.REAL_ESTATE);

    // The position of the field in the data file
    final int position;

    /*
     * The weight type associated with fields for desired percentages
     * (null for other fields)
     */
    final WeightType type;

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
