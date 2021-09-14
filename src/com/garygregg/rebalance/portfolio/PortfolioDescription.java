package com.garygregg.rebalance.portfolio;

import com.garygregg.rebalance.Description;
import com.garygregg.rebalance.WeightType;
import com.garygregg.rebalance.countable.Currency;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PortfolioDescription implements Description<String> {

    // A map of weight types to their desired allocation weights
    private final Map<WeightType, Double> allocation = new HashMap<>();

    // The mnemonic of the portfolio
    private final String mnemonic;

    // The birthdate of the portfolio owner
    private Date birthDate;

    // The monthly income that is CPI adjusted
    private Currency cpiAdjusted;

    // The projected mortality date of the portfolio owner
    private Date mortalityDate;

    // The name associated with the portfolio
    private String name;

    // The monthly income that is *not* CPI adjusted
    private Currency nonCpiAdjusted;

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
    public Currency getCpiAdjusted() {
        return cpiAdjusted;
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
    public Currency getNonCpiAdjusted() {
        return nonCpiAdjusted;
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
     * @param cpiAdjusted CPI adjusted monthly income
     */
    void setCpiAdjusted(Currency cpiAdjusted) {
        this.cpiAdjusted = cpiAdjusted;
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
     * @param nonCpiAdjusted Non-CPI adjusted monthly income
     */
    void setNonCpiAdjusted(Currency nonCpiAdjusted) {
        this.nonCpiAdjusted = nonCpiAdjusted;
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
}
