package com.garygregg.rebalance;

public enum SynthesizerType {

    // An annuity, pension, or Social Security
    ANNUITY(null),

    // An averager of other account values
    AVERAGER(null),

    // CPI-adjusted annuity
    CPI_ANNUITY(ANNUITY),

    // No-CPI-adjusted annuity
    NO_CPI_ANNUITY(ANNUITY),

    // A percent of one or more other accounts
    PERCENTAGE(AVERAGER),

    // Social Security
    SOCIAL_SECURITY(CPI_ANNUITY),

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
