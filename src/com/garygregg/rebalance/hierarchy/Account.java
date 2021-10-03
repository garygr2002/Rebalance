package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.*;
import com.garygregg.rebalance.account.AccountDescription;
import com.garygregg.rebalance.portfolio.PortfolioDescription;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class Account extends
        Aggregate<AccountKey, Ticker, AccountDescription> {

    // A factory for producing artificial accounts
    private static final Factory<Account> factory = Account::getNewArtificial;

    // A lazy boy for an artificial account
    private static final LazyBoy<Account> lazyBoy = new LazyBoy<>(factory);

    // A map of synthesizer types to synthesizer instances
    private static final Map<SynthesizerType, Synthesizer>
            synthesizerMap = new HashMap<>();

    static {

        /*
         * Load up the synthesizer map with the synthesizers that can be
         * instantiated.
         *
         * TODO: Add new synthesizers to the map as they are developed.
         */
        synthesizerMap.put(SynthesizerType.AVERAGING, new Averaging());
        synthesizerMap.put(SynthesizerType.CPI_ANNUITY, new CpiAnnuity());
        synthesizerMap.put(SynthesizerType.NEGATION, new Negation());
        synthesizerMap.put(SynthesizerType.NO_CPI_ANNUITY, new NoCpiAnnuity());
        synthesizerMap.put(SynthesizerType.SOCIAL_SECURITY, new SocialSecurity());
    }

    // The portfolio description associated with this account
    private PortfolioDescription portfolioDescription;

    // Is this ticker synthesized (or programmatically modified)?
    private boolean synthesized;

    /**
     * Constructs the account hierarchy object.
     *
     * @param key The key of the account hierarchy object
     */
    Account(@NotNull AccountKey key) {
        super(key);
    }

    /**
     * Gets an artificial account.
     *
     * @return An artificial account
     */
    public static @NotNull Account getArtificial() {
        return lazyBoy.getLazily();
    }

    /**
     * Gets a new artificial account.
     *
     * @return A new artificial account
     */
    private static @NotNull Account getNewArtificial() {
        return new Account(new AccountKey(Library.getDefaultStringKey(),
                AccountKeyLibrary.getDefaultAccountNumber()));
    }

    @Override
    protected @NotNull Ticker getArtificialChild() {
        return Ticker.getArtificial();
    }

    /**
     * Gets the category type.
     *
     * @return The category type
     */
    public CategoryType getCategoryType() {

        /*
         * Get the tax type. Return 'unknown' category type if the tax type is
         * null, otherwise return the category type indicated by the tax type.
         */
        final TaxType type = getTaxType();
        return (null == type) ? null : type.getCategory();
    }

    @Override
    public @NotNull HoldingLineType getLineType() {
        return HoldingLineType.ACCOUNT;
    }

    /**
     * Gets the portfolio description associated with this account.
     *
     * @return The portfolio description associated with this account
     */
    public PortfolioDescription getPortfolioDescription() {
        return portfolioDescription;
    }

    /**
     * Gets the tax type from the account description.
     *
     * @return The tax type from the account description
     */
    public TaxType getTaxType() {

        /*
         * Get the account description. Return null if the description is null,
         * otherwise return the single tax type.
         */
        final AccountDescription description = getDescription();
        return (null == description) ? null : description.getType();
    }

    @Override
    public boolean hasCategoryType(@NotNull CategoryType type) {

        /*
         * Get the category type in the account description. Return true if the
         * category type is not null, and equal to the given type.
         */
        final CategoryType descriptionType = getCategoryType();
        return (null != descriptionType) && descriptionType.equals(type);
    }

    @Override
    public boolean hasFundType(@NotNull FundType type) {
        return type.equals(FundType.NOT_A_FUND);
    }

    @Override
    public boolean hasTaxType(@NotNull TaxType type) {

        /*
         * Get the tax type in the account description. Return true if the tax
         * type is not null, and equal to the given type.
         */
        final TaxType descriptionType = getTaxType();
        return (null != descriptionType) && descriptionType.equals(type);
    }

    /**
     * Gets whether this account is synthesized, or programmatically modified.
     *
     * @return True if the ticker is synthesized, or programmatically
     * modified; false otherwise
     */
    public boolean isSynthesized() {
        return synthesized;
    }

    /**
     * Sets the portfolio description associated with this account.
     *
     * @param portfolioDescription The portfolio description associated with
     *                             this account
     */
    void setPortfolioDescription(PortfolioDescription portfolioDescription) {
        this.portfolioDescription = portfolioDescription;
    }

    /**
     * Sets the account as synthesized, or programmatically modified.
     */
    void setSynthesized() {
        this.synthesized = true;
    }

    @Override
    boolean synthesizeIf() {

        /*
         * Call the superclass method. Did the superclass method succeed,
         * and not either of: 1) value set in the account, or; 2) the account
         * has children?
         */
        boolean result = super.synthesizeIf();
        if (result && !(hasValueBeenSet() || hasChildren())) {

            /*
             * The superclass method succeeded, and the account has neither
             * existing value nor children. Get the account description, and
             * use the synthesizer type of the account description to get a
             * synthesizer for the account. Is the synthesizer not null?
             */
            AccountDescription description = getDescription();
            Synthesizer synthesizer =
                    synthesizerMap.get((null == description) ?
                            null : description.getSynthesizerType());
            if (null != synthesizer) {

                /*
                 * The synthesizer is not null. Use the synthesizer to
                 * synthesize value for the account.
                 */
                result = synthesizer.synthesize(this);
            }
        }

        // Return the result of synthesis.
        return result;
    }
}
