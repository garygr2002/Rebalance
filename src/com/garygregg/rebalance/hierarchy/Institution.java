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
    protected @NotNull Account getArtificialChild() {
        return Account.getArtificial();
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
