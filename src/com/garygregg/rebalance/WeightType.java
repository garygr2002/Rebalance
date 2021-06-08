package com.garygregg.rebalance;

public enum WeightType {

    // No parent for 'all'.
    ALL(),

    // Tickers must contain fund type CASH.
    CASH(ALL),

    // Tickers must contain fund type CASH and fund type TREASURY.
    CASH_GOVERNMENT(CASH),

    // Tickers must contain fund type CASH and not fund type TREASURY.
    CASH_UNCATEGORIZED(CASH),

    // Tickers must contain fund type BOND.
    BOND(ALL),

    // Tickers must contain fund type CORPORATE.
    BOND_CORPORATE(BOND),

    // Tickers must contain fund type BOND and fund type FOREIGN.
    BOND_FOREIGN(BOND),

    // Tickers must contain fund type BOND and fund type TREASURY.
    BOND_GOVERNMENT(BOND),

    // Tickers must contain fund type HIGH. Necessarily these are 'corporate'.
    BOND_HIGH(BOND),

    // Tickers must contain fund type INFLATION.
    BOND_INFLATION(BOND),

    // Tickers must contain fund type MORTGAGE.
    BOND_MORTGAGE(BOND),

    /*
     * Tickers must contain fund type BOND and fund type DOMESTIC, but no
     * other.
     */
    BOND_UNCATEGORIZED(BOND),

    /*
     * Tickers must contain fund type SHORT. Suggest letting 'short' prevail
     * for bonds that are both 'government' AND 'short'.
     */
    BOND_SHORT(BOND),

    // Tickers must contain fund type REAL_ESTATE.
    REAL_ESTATE(ALL),

    // Tickers must contain fund type STOCK.
    STOCK(ALL),

    // Tickers must contain fund type STOCK and fund type DOMESTIC.
    STOCK_DOMESTIC(STOCK),

    // Tickers must contain fund type STOCK and fund type FOREIGN.
    STOCK_FOREIGN(STOCK),

    // Tickers must contain fund type LARGE.
    STOCK_LARGE(STOCK_DOMESTIC, STOCK_FOREIGN),

    // Tickers must contains fund type NOT_LARGE.
    STOCK_NOT_LARGE(STOCK_DOMESTIC, STOCK_FOREIGN),

    // Tickers must contains fund type MEDIUM.
    STOCK_MEDIUM(STOCK_NOT_LARGE),

    // Tickers must contains fund type SMALL.
    STOCK_SMALL(STOCK_NOT_LARGE),

    // Tickers must contains fund type GROWTH.
    STOCK_GROWTH(STOCK_LARGE, STOCK_NOT_LARGE, STOCK_MEDIUM, STOCK_SMALL),

    // Tickers must contains fund type VALUE.
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
