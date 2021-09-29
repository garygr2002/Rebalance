package com.garygregg.rebalance;

public enum SynthesizerType {

    // An annuity, pension, or Social Security
    ANNUITY(null),

    // CPI-adjusted annuity
    CPI_ANNUITY(ANNUITY),

    // Social Security
    SOCIAL_SECURITY(CPI_ANNUITY),

    // No-CPI-adjusted annuity
    NO_CPI_ANNUITY(ANNUITY),

    // An averaging of other account values
    AVERAGING(null),

    // An averaging synthesizer that negates its result
    NEGATION(AVERAGING),

    // A percent of one or more other accounts
    PERCENTAGE(AVERAGING),

    // Real estate agent commission
    COMMISSION(PERCENTAGE),

    // Capital gains tax on property sale
    US_CAPITAL_GAINS(PERCENTAGE),

    // Washington Real Estate Excise Tax
    WASHINGTON_REET(PERCENTAGE);

    // The parent synthesizer type
    private final SynthesizerType parent;

    /**
     * Constructs the synthesizer type.
     *
     * @param parent The parent synthesizer type
     */
    SynthesizerType(SynthesizerType parent) {
        this.parent = parent;
    }

    /**
     * Gets the parent synthesizer type.
     *
     * @return The parent synthesizer type (or null for top-level type)
     */
    public SynthesizerType getParent() {
        return parent;
    }
}
