package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.*;
import org.jetbrains.annotations.NotNull;

public class Institution extends SuperAggregate<
        String,
        Account,
        Description<String>> {

    // A factory for producing artificial institutions
    private static final Factory<Institution> factory =
            Institution::getNewArtificial;

    // A lazy boy for an artificial institution
    private static final LazyBoy<Institution> lazyBoy = new LazyBoy<>(factory);

    // The last account in the institution
    private Account last = null;

    /**
     * Constructs the institution hierarchy object.
     *
     * @param mnemonic The key of the institution hierarchy object
     */
    Institution(@NotNull String mnemonic) {
        super(mnemonic);
    }

    /**
     * Gets an artificial institution.
     *
     * @return An artificial institution
     */
    public static @NotNull Institution getArtificial() {
        return lazyBoy.getLazily();
    }

    /**
     * Gets a new artificial institution.
     *
     * @return A new artificial institution
     */
    private static @NotNull Institution getNewArtificial() {
        return new Institution(Library.getDefaultStringKey());
    }

    @Override
    Account addChild(@NotNull Common<?, ?, ?> hierarchyObject)
            throws ClassCastException {

        /*
         * Cast the incoming hierarchy object to an account. Is the last
         * account null, or does the last account compare less than the
         * incoming account?
         */
        final Account account = (Account) hierarchyObject;
        if ((null == last) || (0 < account.compareTo(last))) {

            /*
             * The last account is null, or the last account compares less than
             * the incoming account. Save the incoming account as the last
             * account.
             */
            last = account;
        }

        // Call the superclass method to add the incoming account.
        return super.addChild(hierarchyObject);
    }

    @Override
    protected @NotNull Account getArtificialChild() {
        return Account.getArtificial();
    }

    @Override
    public Account getLast() {
        return last;
    }

    @Override
    public @NotNull HoldingLineType getLineType() {
        return HoldingLineType.INSTITUTION;
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
