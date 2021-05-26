package com.garygregg.rebalance.distinguished;

public enum DistinguishedFields {

    // The key of the holding
    KEY(1),

    // The holding type
    LINE_TYPE(0),

    // The name of the holding
    VALUE(2);

    // The position of the field in the data file
    private final int position;

    /**
     * Constructs the balance-in fields enumerator.
     *
     * @param position The position of the field in the data file
     */
    DistinguishedFields(int position) {
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
