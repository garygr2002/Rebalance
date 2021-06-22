package com.garygregg.rebalance;

import org.jetbrains.annotations.NotNull;

public enum WeightType {

    // No parent for 'all'.
    ALL("All"),

    // Tickers must contain fund type CASH.
    CASH( "Cash general", ALL),

    // Tickers must contain fund type CASH and fund type TREASURY.
    CASH_GOVERNMENT("Cash government", CASH),

    // Tickers must contain fund type CASH and not fund type TREASURY.
    CASH_UNCATEGORIZED("Cash commercial", CASH),

    // Tickers must contain fund type BOND.
    BOND("Bond general", ALL),

    // Tickers must contain fund type CORPORATE.
    BOND_CORPORATE("Bond corporate", BOND),

    // Tickers must contain fund type BOND and fund type FOREIGN.
    BOND_FOREIGN("Bond foreign", BOND),

    // Tickers must contain fund type BOND and fund type TREASURY.
    BOND_GOVERNMENT("Bond government", BOND),

    // Tickers must contain fund type HIGH. Necessarily these are 'corporate'.
    BOND_HIGH("Bond high-yield", BOND),

    // Tickers must contain fund type INFLATION.
    BOND_INFLATION("Bond inflation", BOND),

    // Tickers must contain fund type MORTGAGE.
    BOND_MORTGAGE("Bond mortgage", BOND),

    /*
     * Tickers must contain fund type BOND and fund type DOMESTIC, but no
     * other.
     */
    BOND_UNCATEGORIZED("Bond unspecific", BOND),

    /*
     * Tickers must contain fund type SHORT. Suggest letting 'short' prevail
     * for bonds that are both 'government' AND 'short'.
     */
    BOND_SHORT("Bond short-term", BOND),

    // Tickers must contain fund type REAL_ESTATE.
    REAL_ESTATE("Real-estate", ALL),

    // Tickers must contain fund type STOCK.
    STOCK("Stock general", ALL),

    // Tickers must contain fund type STOCK and fund type DOMESTIC.
    STOCK_DOMESTIC("Stock domestic", STOCK),

    // Tickers must contain fund type STOCK and fund type FOREIGN.
    STOCK_FOREIGN("Stock foreign", STOCK),

    // Tickers must contain fund type LARGE.
    STOCK_LARGE("Stock large-cap", STOCK_DOMESTIC, STOCK_FOREIGN),

    // Tickers must contains fund type NOT_LARGE.
    STOCK_NOT_LARGE("Stock not large", STOCK_DOMESTIC,
            STOCK_FOREIGN),

    // Tickers must contains fund type MEDIUM.
    STOCK_MEDIUM("Stock mid-cap", STOCK_NOT_LARGE),

    // Tickers must contains fund type SMALL.
    STOCK_SMALL("Stock small-cap", STOCK_NOT_LARGE),

    // Tickers must contains fund type GROWTH.
    STOCK_GROWTH("Stock growth", STOCK_LARGE, STOCK_NOT_LARGE, STOCK_MEDIUM,
            STOCK_SMALL),

    // Tickers must contains fund type VALUE.
    STOCK_VALUE("Stock value", STOCK_LARGE, STOCK_NOT_LARGE, STOCK_MEDIUM,
            STOCK_SMALL);

    // The parents for the weight type
    private final WeightType[] parents;

    // The 'soft' name of the type
    private final String softName;

    /**
     * Constructs the weight type.
     *
     * @param parents The parents for the weight type
     */
    WeightType(@NotNull String softName,
               WeightType... parents) {

        // Assign the member variables.
        this.parents = parents;
        this.softName = softName;
    }

    /**
     * Gets the parents for the weight type.
     *
     * @return The parents for the weight type
     */
    public WeightType[] getParents() {
        return parents;
    }

    /**
     * Gets the 'soft' name of the type.
     *
     * @return The 'soft' name of the type
     */
    public String getSoftName() {
        return softName;
    }
}
