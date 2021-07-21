package com.garygregg.rebalance.cla;

import org.jetbrains.annotations.NotNull;

public enum CommandLineId {

    /*
     * Note: values are ordered according to how they will be acted upon from
     * the command line.
     */

    // The logging level (valid Level values)
    LEVEL("ALL, CONFIG, FINE, FINER, FINEST, INFO, OFF, SEVERE or WARNING",
            "lglv", true),

    // The annual expected inflation (double, highest precision)
    INFLATION("annual inflation rate", "fltn", true),

    // High S&P 500 value (double, 2 decimal precision)
    HIGH("S&P 500 high", "spgh", true),

    // Current S&P 500 value (double, 2 decimal precision)
    CURRENT("S&P 500 current", "spcr", true),

    // The path for the data directory (string path)
    PATH("data path", "dpth", true),

    // Destination for data directory backup (string path)
    DESTINATION("backup destination", "bpth", true),

    // The backup command
    BACKUP("perform backup", "bpth", false),

    // Presumed to be an option argument (string)
    OTHER("other command argument", null, false);

    // True if an argument is mandatory, false otherwise
    private final boolean argumentMandatory;

    // The name of any argument
    private final String argumentName;

    // A description of the command line argument
    private final String description;

    /**
     * Constructs the command line ID.
     *
     * @param description       A description of the command line argument
     * @param argumentName      The name of any argument
     * @param argumentMandatory True if an argument is mandatory, false otherwise
     */
    CommandLineId(@NotNull String description, String argumentName,
                  boolean argumentMandatory) {

        // Set the member variables.
        this.description = description;
        this.argumentName = argumentName;
        this.argumentMandatory = argumentMandatory;
    }

    /**
     * Gets the name of the argument if any.
     *
     * @return The name of the argument, or null if there is no argument
     */
    public String getArgumentName() {
        return argumentName;
    }

    /**
     * Gets the description of the command line argument.
     *
     * @return The description of the command line argument
     */
    public @NotNull String getDescription() {
        return description;
    }

    /**
     * True if an argument is mandatory, false otherwise.
     *
     * @return True if an argument is mandatory, false otherwise
     */
    public boolean isArgumentMandatory() {
        return argumentMandatory;
    }
}
