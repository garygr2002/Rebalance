package com.garygregg.rebalance.portfolio;

import com.garygregg.rebalance.WeightType;

enum PortfolioFields {

    // Mnemonic of the portfolio (and the key)
    MNEMONIC(0, null),

    // Name of the portfolio
    NAME(1, null),

    // Desired percentage stocks in the portfolio
    PERCENTAGE_STOCK(2, WeightType.STOCK),

    // Desired percentage bonds in the portfolio
    PERCENTAGE_BOND(3, WeightType.BOND),

    // Desired percentage cash in the portfolio
    PERCENTAGE_CASH(4, WeightType.CASH),

    // Desired percentage real estate in the portfolio
    PERCENTAGE_REAL_ESTATE(5, WeightType.REAL_ESTATE);

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
