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

    // The birth date of the portfolio owner
    private Date birthDate;

    // Flag to indicate if other monthly annuity income is CPI adjusted
    private Boolean cpiAdjusted;

    // The projected mortality date of the portfolio owner
    private Date mortalityDate;

    // The name associated with the portfolio
    private String name;

    // Other monthly annuity income
    private Currency otherMonthly;

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
     * Gets the birth date of the portfolio owner.
     *
     * @return The birth date of the portfolio owner
     */
    public Date getBirthDate() {
        return birthDate;
    }

    /**
     * Gets the flag to indicate if other monthly annuity income is CPI
     * adjusted.
     *
     * @return The flag to indicate if other monthly annuity income is CPI
     * adjusted
     */
    public Boolean getCpiAdjusted() {
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
     * Gets other monthly annuity income.
     *
     * @return Other monthly annuity income
     */
    public Currency getOtherMonthly() {
        return otherMonthly;
    }

    /**
     * Gets the monthly Social Security income starting at age 62.
     *
     * @return The monthly Social Security income starting at age 62
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
     * Gets the birth date of the portfolio owner.
     *
     * @param birthDate The birth date of the portfolio owner
     */
    void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    /**
     * Sets the flag to indicate if other monthly annuity income is CPI
     * adjusted.
     *
     * @param cpiAdjusted The flag to indicate if other monthly annuity income
     *                    is CPI adjusted
     */
    void setCpiAdjusted(Boolean cpiAdjusted) {
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
     * Sets other monthly annuity income.
     *
     * @param otherMonthly Other monthly annuity income
     */
    void setOtherMonthly(Currency otherMonthly) {
        this.otherMonthly = otherMonthly;
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
