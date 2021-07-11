package com.garygregg.rebalance.cla;

public enum TokenId {

    // Current S&P 500 value (double, 2 decimal precision)
    CURRENT,

    // Destination for data directory backup (string path)
    DESTINATION,

    // High S&P 500 value (double, 2 decimal precision)
    HIGH,

    // The annual expected inflation (double, highest precision)
    INFLATION,

    // The logging level (valid Level values)
    LEVEL,

    // Presumed to be an option argument (string)
    OTHER,

    // The path for the data directory (string path)
    PATH
}
