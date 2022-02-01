package com.garygregg.rebalance.ticker;

enum TickerFields {

    /*
     * The code of the ticker: F = mutual fund or sub-account that can be
     * considered for rebalance; 'J' = sub-account that cannot be considered
     * for rebalance; 'Q' = single stock; 'X' = exchange-traded ticker
     */
    CODE(0),

    // The minimum investment in the ticker
    MINIMUM(4),

    // The name of the ticker
    NAME(3),

    // The number of the ticker
    NUMBER(2),

    // The preferred round number of shares to hold
    PREFERRED_ROUNDING(5),

    // Subcode #1 for contained securities
    SUBCODE_1(6),

    // Subcode #2 for contained securities
    SUBCODE_2(7),

    // Subcode #3 for contained securities
    SUBCODE_3(8),

    // Subcode #4 for contained securities
    SUBCODE_4(9),

    // The unique ticker ID (and the key)
    TICKER(1);

    // The position of the field in the data file
    private final int position;

    /**
     * Constructs the ticker fields enumerator.
     *
     * @param position The position of the field in the data file
     */
    TickerFields(int position) {
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
}
