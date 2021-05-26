package com.garygregg.rebalance.code;

enum CodeFields {

    // The code (and the key)
    CODE(0),

    // A description of the code based on related codes
    DESCRIPTION(7),

    // The name of the code
    NAME(1),

    // Associated subcode 1
    SUBCODE_1(2),

    // Associated subcode 2
    SUBCODE_2(3),

    // Associated subcode 3
    SUBCODE_3(4),

    // Associated subcode 4
    SUBCODE_4(5),

    // Associated subcode 5
    SUBCODE_5(6);

    // The position of the field in the data file
    private final int position;

    /**
     * Constructs the code fields enumerator.
     *
     * @param position The position of the field in the data file
     */
    CodeFields(int position) {
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
