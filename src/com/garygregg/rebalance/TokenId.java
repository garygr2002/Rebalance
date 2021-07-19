package com.garygregg.rebalance;

public enum TokenId {

    /*
     * Note: values are ordered according to how they will be acted upon from
     * the command line.
     */

    // The logging level (valid Level values)
    LEVEL,

    // The annual expected inflation (double, highest precision)
    INFLATION,

    // High S&P 500 value (double, 2 decimal precision)
    HIGH,

    // Current S&P 500 value (double, 2 decimal precision)
    CURRENT,

    // The path for the data directory (string path)
    PATH,

    // Destination for data directory backup (string path)
    DESTINATION,

    // Presumed to be an option argument (string)
    OTHER
}
