package com.garygregg.rebalance.distinguished;

import com.garygregg.rebalance.portfolio.PortfolioLibrary;
import org.jetbrains.annotations.NotNull;

public final class DistinguishedPortfolioLibrary extends
        DistinguishedLibrary<DistinguishedPortfolio,
                DistinguishedPortfolioDescription, String> {

    // The singleton distinguished portfolio library
    private static final DistinguishedPortfolioLibrary library =
            new DistinguishedPortfolioLibrary();

    /**
     * Constructs the distinguished portfolio library.
     */
    private DistinguishedPortfolioLibrary() {

        // Nothing to add to the line codes.
    }

    /**
     * Gets a distinguished portfolio library instance.
     *
     * @return A distinguished portfolio library instance
     */
    public static @NotNull DistinguishedPortfolioLibrary getInstance() {
        return library;
    }

    @Override
    public boolean areKeyElementsOkay(String @NotNull ... elements) {
        return PortfolioLibrary.getInstance().areKeyElementsOkay(elements);
    }

    @Override
    public DistinguishedPortfolioDescription[] getCatalog() {
        return getDescriptions().toArray(
                new DistinguishedPortfolioDescription[0]);
    }

    @Override
    public @NotNull DistinguishedPortfolio getDefaultKey() {
        return DistinguishedPortfolio.DEFAULT;
    }

    @Override
    public int getElementCount() {
        return DistinguishedFields.values().length;
    }

    @Override
    public String getValue(@NotNull DistinguishedPortfolio key) {

        /*
         * Get the description mapped to the key. Return null if there is no
         * such description, otherwise return the value of the description.
         */
        final DistinguishedPortfolioDescription description = getDescription(key);
        return (null == description) ? null : description.getValue();
    }
}
