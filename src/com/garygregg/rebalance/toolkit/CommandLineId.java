package com.garygregg.rebalance.toolkit;

import org.jetbrains.annotations.NotNull;

public enum CommandLineId {

    /*
     * Note: values are ordered according to how they will be acted upon from
     * the command line.
     */

    // Resets all preferences
    RESET("reset preferences to defaults", null, false),

    // Minimum running settings using expected values
    MINIMUM("minimum running settings using expected values", null,
            false),

    // List the preference settings
    PREFERENCE("list the preference settings", null, false),

    /*
     * The logging level (valid Level values) at which logging messages begin to
     * appear
     */
    @SuppressWarnings("SpellCheckingInspection")
    LEVEL("ALL, CONFIG, FINE, FINER, FINEST, INFO, OFF, SEVERE or WARNING",
            "lglv", true),

    // The logging level for ordinary informational messages
    @SuppressWarnings("SpellCheckingInspection")
    ORDINARY("ALL, CONFIG, FINE, FINER, FINEST, INFO, OFF,", "rdnr",
            true),

    // The logging level for extraordinary informational messages
    @SuppressWarnings("SpellCheckingInspection")
    EXTRAORDINARY("ALL, CONFIG, FINE, FINER, FINEST, INFO, OFF,", "xtrd",
            true),

    // The annual expected inflation (double, the highest precision)
    @SuppressWarnings("SpellCheckingInspection")
    INFLATION("annual inflation rate", "fltn", true),

    // High S&P 500 (double, 2 decimal precision)
    @SuppressWarnings("SpellCheckingInspection")
    HIGH("S&P 500 high", "sphg", true),

    // Last close of the S&P 500 (double, 2 decimal precision)
    @SuppressWarnings("SpellCheckingInspection")
    CLOSE("S&P 500 last close", "spcl", true),

    // The S&P 500 today (double, 2 decimal precision)
    @SuppressWarnings("SpellCheckingInspection")
    TODAY("S&P 500 today", "sptd", true),

    // The limit of allowed receiver delegates
    @SuppressWarnings("SpellCheckingInspection")
    X("rebalance limit of funds per account", "ncnt", true),

    // Source data directory (string path)
    @SuppressWarnings("SpellCheckingInspection")
    SOURCE("data source", "spth", true),

    // Destination for data directory backup (string path)
    @SuppressWarnings("SpellCheckingInspection")
    DESTINATION("backup destination", "dpth", true),

    // Use expected prefix and suffix for data directory backup (string)
    USE("use expected prefix and suffix for given backup destination",
            "link", true),

    // The backup command
    @SuppressWarnings("SpellCheckingInspection")
    BACKUP("perform backup now; dpth if no bpth specified (see above)", "bpth",
            false),

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
