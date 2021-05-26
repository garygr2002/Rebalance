package com.garygregg.rebalance.ticker;

enum TickerFields {

    /*
     * The code of the fund: F = Open-end; J = Home equity, loan or pension;
     * Q = Individual stock; X = ETFs
     */
    CODE(0),

    // The minimum investment in the fund
    MINIMUM(4),

    // The name of the fund
    NAME(3),

    // The number of the fund
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

    // The ticker of the fund (and the key)
    TICKER(1);

    // The position of the field in the data file
    private final int position;

    /**
     * Constructs the fund fields enumerator.
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
