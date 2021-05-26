package com.garygregg.rebalance.holding;

enum HoldingFields {

    // The key of the holding
    KEY(1),

    // The holding type
    LINE_TYPE(0),

    // The name of the holding
    NAME(2),

    // Share price
    PRICE(4),

    // Number of shares
    SHARES(3),

    // The value of the holding: shares times price
    VALUE(5);

    // The position of the field in the data file
    private final int position;

    /**
     * Constructs the balance-in fields enumerator.
     *
     * @param position The position of the field in the data file
     */
    HoldingFields(int position) {
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
