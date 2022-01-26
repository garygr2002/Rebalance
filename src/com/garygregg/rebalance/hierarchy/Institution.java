package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.toolkit.*;
import org.jetbrains.annotations.NotNull;

public class Institution extends SuperAggregate<String, Account,
        Description<String>> {

    // A factory for producing artificial institutions
    private static final Factory<Institution> factoryForInstitutions =
            Institution::getNewArtificial;

    // A lazy boy for an artificial institution
    private static final LazyBoy<Institution> lazyBoyForInstitutions =
            new LazyBoy<>(factoryForInstitutions);

    // A factory for producing the last account
    private final Factory<Account> factoryForAccounts = () -> {

        /*
         * Declare a variable to receive the last account. Cycle for each
         * account.
         */
        Account last = null;
        for (Account candidate : getChildren()) {

            /*
             * Compare the candidate to the last account. Does the current last
             * account compare less than the candidate?
             */
            if ((null == last) || (0 < candidate.compareTo(last))) {

                /*
                 * The current last account compares less than the
                 * candidate. Set the candidate as the new last.
                 */
                last = candidate;
            }
        }

        /*
         * Return the last account, or an artificial account if the last
         * account is null.
         */
        return (null == last) ? Account.getArtificial() : last;
    };

    // A lazy boy for producing the last account
    private final LazyBoy<Account> lazyBoyForAccounts =
            new LazyBoy<>(factoryForAccounts);

    // The name of the institution
    private final String name;

    /**
     * Constructs the institution hierarchy object.
     *
     * @param mnemonic The key of the institution hierarchy object
     * @param name     The name of the institution
     */
    Institution(@NotNull String mnemonic, String name) {

        /*
         * Call the superclass constructor with the institution key, and set
         * the name of the institution.
         */
        super(mnemonic);
        this.name = name;
    }

    /**
     * Gets an artificial institution.
     *
     * @return An artificial institution
     */
    public static @NotNull Institution getArtificial() {
        return lazyBoyForInstitutions.getLazily();
    }

    /**
     * Gets a new artificial institution.
     *
     * @return A new artificial institution
     */
    private static @NotNull Institution getNewArtificial() {
        return new Institution(Library.getDefaultStringKey(), null);
    }

    @Override
    Account addChild(@NotNull Common<?, ?, ?> hierarchyObject)
            throws ClassCastException {

        // Clear the lazy boy for accounts, and add the hierarchy object.
        lazyBoyForAccounts.clear();
        return super.addChild(hierarchyObject);
    }

    @Override
    protected @NotNull Account getArtificialChild() {
        return Account.getArtificial();
    }

    @Override
    public @NotNull Account getLastToBeRebalanced() {
        return lazyBoyForAccounts.getLazily();
    }

    @Override
    public @NotNull HoldingLineType getLineType() {
        return HoldingLineType.INSTITUTION;
    }

    /**
     * Gets the name of the institution.
     *
     * @return The name of the institution
     */
    public String getName() {
        return name;
    }

    @Override
    public boolean hasFundType(@NotNull FundType type) {
        return type.equals(FundType.NOT_A_FUND);
    }

    @Override
    protected void setDescription(Description<String> description) {

        // Right now you cannot set an institution description.
        throw new UnsupportedOperationException("Attempt to set institution " +
                "description; institutions currently have no description.");
    }
}
