package com.garygregg.rebalance.portfolio;

import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.toolkit.Description;
import com.garygregg.rebalance.toolkit.FilingStatus;
import com.garygregg.rebalance.toolkit.WeightType;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.EnumMap;
import java.util.Map;

public class PortfolioDescription implements Description<String> {

    // A map of weight types to their desired allocation weights
    private final Map<WeightType, Double> allocation =
            new EnumMap<>(WeightType.class);

    // The mnemonic of the portfolio
    private final String mnemonic;

    // Adjust desired investment allocations for relative market valuation
    private Boolean adjust;

    // The birthdate of the portfolio owner
    private Date birthDate;

    // The monthly income that is CPI adjusted
    private Currency cpiMonthly;

    // The taxpayer filing status
    private FilingStatus filingStatus;

    // The projected mortality date of the portfolio owner
    private Date mortalityDate;

    // The name associated with the portfolio
    private String name;

    // The monthly income that is *not* CPI adjusted
    private Currency nonCpiMonthly;

    // The monthly Social Security income starting at age 62
    private Currency socialSecurityMonthly;

    // Taxable annual income of the portfolio owner
    private Currency taxableAnnual;

    /**
     * Constructs the portfolio description.
     *
     * @param mnemonic The mnemonic of the portfolio
     */
    PortfolioDescription(@NotNull String mnemonic) {
        this.mnemonic = mnemonic;
    }

    /**
     * Adjusts the desired allocation weight of the designated fund type.
     *
     * @param type  The designated weight type
     * @param value The desired allocation weight
     */
    void adjustAllocation(@NotNull WeightType type, double value) {
        allocation.put(type, value);
    }

    /**
     * Gets the desired allocation weight of the designated fund type.
     *
     * @param type The designated fund type
     * @return The desired allocation weight for the designated fund type
     */
    public Double getAllocation(WeightType type) {
        return allocation.get(type);
    }

    /**
     * Gets the birthdate of the portfolio owner.
     *
     * @return The birthdate of the portfolio owner
     */
    public Date getBirthdate() {
        return birthDate;
    }

    /**
     * Gets CPI adjusted monthly income.
     *
     * @return CPI adjusted monthly income
     */
    public Currency getCpiMonthly() {
        return cpiMonthly;
    }

    /**
     * Gets the taxpayer filing status.
     *
     * @return The taxpayer filing status
     */
    public FilingStatus getFilingStatus() {
        return filingStatus;
    }

    @Override
    public @NotNull String getKey() {
        return getMnemonic();
    }

    /**
     * Gets the mnemonic of the account.
     *
     * @return The mnemonic of the account
     */
    public @NotNull String getMnemonic() {
        return mnemonic;
    }

    /**
     * Gets the projected mortality date of the portfolio owner.
     *
     * @return The projected mortality date of the portfolio owner
     */
    public Date getMortalityDate() {
        return mortalityDate;
    }

    @Override
    public String getName() {
        return name;
    }

    /**
     * Gets non-CPI adjusted monthly income.
     *
     * @return Non-CPI adjusted monthly income
     */
    public Currency getNonCpiMonthly() {
        return nonCpiMonthly;
    }

    /**
     * Gets the Social Security monthly income starting at age 62.
     *
     * @return The Social Security monthly income starting at age 62
     */
    public Currency getSocialSecurityMonthly() {
        return socialSecurityMonthly;
    }

    /**
     * Gets the taxable annual income of the portfolio owner.
     *
     * @return The taxable annual income of the portfolio owner
     */
    public Currency getTaxableAnnual() {
        return taxableAnnual;
    }

    /**
     * Sets the adjustment flag.
     *
     * @param adjust The adjustment flag
     */
    void setAdjust(Boolean adjust) {
        this.adjust = adjust;
    }

    /**
     * Sets the birthdate of the portfolio owner.
     *
     * @param birthDate The birthdate of the portfolio owner
     */
    void setBirthdate(Date birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * Sets CPI adjusted monthly income.
     *
     * @param cpiMonthly CPI adjusted monthly income
     */
    void setCpiMonthly(Currency cpiMonthly) {
        this.cpiMonthly = cpiMonthly;
    }

    /**
     * Sets the taxpayer filing status.
     *
     * @param filingStatus The taxpayer filing status
     */
    void setFilingStatus(FilingStatus filingStatus) {
        this.filingStatus = filingStatus;
    }

    /**
     * Sets the projected mortality date of the portfolio owner.
     *
     * @param mortalityDate The projected mortality date of the portfolio owner
     */
    void setMortalityDate(Date mortalityDate) {
        this.mortalityDate = mortalityDate;
    }

    /**
     * Sets the name associated with the portfolio.
     *
     * @param name The name associated with the portfolio
     */
    void setName(String name) {
        this.name = name;
    }

    /**
     * Sets non-CPI adjusted monthly income.
     *
     * @param nonCpiMonthly Non-CPI adjusted monthly income
     */
    void setNonCpiMonthly(Currency nonCpiMonthly) {
        this.nonCpiMonthly = nonCpiMonthly;
    }

    /**
     * Sets the monthly Social Security income starting at age 62.
     *
     * @param socialSecurityMonthly The monthly Social Security income starting
     *                              at age 62
     */
    void setSocialSecurityMonthly(Currency socialSecurityMonthly) {
        this.socialSecurityMonthly = socialSecurityMonthly;
    }

    /**
     * Sets the taxable annual income of the portfolio owner.
     *
     * @param taxableAnnual The taxable annual income of the portfolio owner
     */
    void setTaxableAnnual(Currency taxableAnnual) {
        this.taxableAnnual = taxableAnnual;
    }

    /**
     * Indicates whether the desired investment allocations should be adjusted
     * for relative market valuation.
     *
     * @return True if the desired investment allocations should be adjusted
     * relative market valuation; false otherwise
     */
    public boolean shouldAdjust() {
        return (null != adjust) && adjust;
    }
}
