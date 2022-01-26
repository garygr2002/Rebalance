package com.garygregg.rebalance.detailed;

import com.garygregg.rebalance.toolkit.WeightType;

enum DetailedFields {

    /*
     * The mnemonic of the institution where the account is held (and 1st half
     * of the key)
     */
    INSTITUTION(0, null),

    // Number of the associated account (and 2nd half of the key)
    NUMBER(1, null),

    // Name of the associated account
    NAME(2, null),

    // Desired percentage stocks
    PERCENTAGE_STOCK(3, WeightType.STOCK),

    // Desired percentage of domestic stocks
    PERCENTAGE_STOCK_DOMESTIC(4, WeightType.STOCK_DOMESTIC),

    // Desired percentage of foreign stocks
    PERCENTAGE_STOCK_FOREIGN(5, WeightType.STOCK_FOREIGN),

    // Desired percentage of large stocks
    PERCENTAGE_STOCK_LARGE(6, WeightType.STOCK_LARGE),

    // Desired percentage of not-large stocks
    PERCENTAGE_STOCK_NOT_LARGE(7, WeightType.STOCK_NOT_LARGE),

    // Desired percentage of medium stocks
    PERCENTAGE_STOCK_MEDIUM(8, WeightType.STOCK_MEDIUM),

    // Desired percentage of small stocks
    PERCENTAGE_STOCK_SMALL(9, WeightType.STOCK_SMALL),

    // Desired percentage of growth and value stocks
    PERCENTAGE_STOCK_GROWTH_AND_VALUE(10,
            WeightType.STOCK_GROWTH_AND_VALUE),

    // Desired percentage of growth or value stocks
    PERCENTAGE_STOCK_GROWTH_OR_VALUE(11,
            WeightType.STOCK_GROWTH_OR_VALUE),

    // Desired percentage of growth stocks
    PERCENTAGE_STOCK_GROWTH(12, WeightType.STOCK_GROWTH),

    // Desired percentage of value stocks
    PERCENTAGE_STOCK_VALUE(13, WeightType.STOCK_VALUE),

    // Desired percentage of bonds
    PERCENTAGE_BOND(14, WeightType.BOND),

    // Desired percentage of corporate bonds
    PERCENTAGE_BOND_CORPORATE(15, WeightType.BOND_CORPORATE),

    // Desired percentage of foreign bonds
    PERCENTAGE_BOND_FOREIGN(16, WeightType.BOND_FOREIGN),

    // Desired percentage of government bonds
    PERCENTAGE_BOND_GOVERNMENT(17, WeightType.BOND_GOVERNMENT),

    // Desired percentage of high-yield bonds
    PERCENTAGE_BOND_HIGH(18, WeightType.BOND_HIGH),

    // Desired percentage of inflation-protected bonds
    PERCENTAGE_BOND_INFLATION(19, WeightType.BOND_INFLATION),

    // Desired percentage of mortgage-backed bonds
    PERCENTAGE_BOND_MORTGAGE(20, WeightType.BOND_MORTGAGE),

    // Desired percentage of short-term bonds
    PERCENTAGE_BOND_SHORT(21, WeightType.BOND_SHORT),

    // Desired percentage of uncategorized bonds
    PERCENTAGE_BOND_UNCATEGORIZED(22, WeightType.BOND_UNCATEGORIZED),

    // Desired percentage of cash
    PERCENTAGE_CASH(23, WeightType.CASH),

    // Desired percentage of cash in government securities
    PERCENTAGE_CASH_GOVERNMENT(24, WeightType.CASH_GOVERNMENT),

    // Desired percentage of cash in uncategorized securities
    PERCENTAGE_CASH_UNCATEGORIZED(25, WeightType.CASH_UNCATEGORIZED),

    // Desired percentage of real estate
    PERCENTAGE_REAL_ESTATE(26, WeightType.REAL_ESTATE);

    // The position of the field in the data file
    private final int position;

    /*
     * The weight type associated with fields for desired percentages
     * (null for other fields)
     */
    private final WeightType type;

    /**
     * Constructs the enumerator of the detailed fields.
     *
     * @param position The position of the field in the data file
     * @param type     The weight type associated with fields for desired
     *                 percentages (null for other fields)
     */
    DetailedFields(int position, WeightType type) {

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
