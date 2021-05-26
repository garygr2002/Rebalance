package com.garygregg.rebalance;

public enum WeightType {

    // No parent for 'all'.
    ALL(),

    CASH(ALL),

    CASH_GOVERNMENT(CASH),

    CASH_UNCATEGORIZED(CASH),

    BOND(ALL),

    BOND_CORPORATE(BOND),

    BOND_FOREIGN(BOND),

    BOND_GOVERNMENT(BOND),

    // Necessarily these are 'corporate'.
    BOND_HIGH(BOND),

    BOND_INFLATION(BOND),

    BOND_MORTGAGE(BOND),

    BOND_UNCATEGORIZED(BOND),

    /*
     * Suggest letting 'short' prevail for bonds that are both 'government' AND
     * 'short'.
     */
    BOND_SHORT(BOND),

    REAL_ESTATE(ALL),

    STOCK(ALL),

    STOCK_DOMESTIC(STOCK),

    STOCK_FOREIGN(STOCK),

    STOCK_LARGE(STOCK_DOMESTIC, STOCK_FOREIGN),

    STOCK_NOT_LARGE(STOCK_DOMESTIC, STOCK_FOREIGN),

    STOCK_MEDIUM(STOCK_NOT_LARGE),

    STOCK_SMALL(STOCK_NOT_LARGE),

    STOCK_GROWTH(STOCK_LARGE, STOCK_NOT_LARGE, STOCK_MEDIUM, STOCK_SMALL),

    STOCK_VALUE(STOCK_LARGE, STOCK_NOT_LARGE, STOCK_MEDIUM, STOCK_SMALL);

    // The parents for the weight type
    private final WeightType[] parents;

    /**
     * Constructs the weight type.
     *
     * @param parents The parents for the weight type
     */
    WeightType(WeightType... parents) {
        this.parents = parents;
    }

    /**
     * Gets the parents for the weight type.
     *
     * @return The parents for the weight type
     */
    public WeightType[] getParents() {
        return parents;
    }
}
