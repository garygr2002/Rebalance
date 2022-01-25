package com.garygregg.rebalance.tax;

public enum TaxFields {

    // The tax rate for the threshold
    TAX_RATE(1),

    // The threshold for the tax rate
    THRESHOLD(0);

    // The position of the field in the data file
    private final int position;

    /**
     * Constructs the tax fields enumerator.
     *
     * @param position The position of the field in the data file
     */
    TaxFields(int position) {
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
