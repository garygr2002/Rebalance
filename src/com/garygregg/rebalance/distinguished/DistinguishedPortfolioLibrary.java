package com.garygregg.rebalance.distinguished;

import com.garygregg.rebalance.portfolio.PortfolioLibrary;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class DistinguishedPortfolioLibrary extends
        DistinguishedLibrary<DistinguishedPortfolio, DistinguishedPortfolioDescription, String> {

    // The singleton distinguished portfolio library
    private static final DistinguishedPortfolioLibrary library =
            new DistinguishedPortfolioLibrary();

    // A map of distinguished portfolios to their descriptions
    private final Map<DistinguishedPortfolio, DistinguishedPortfolioDescription>
            distinguishedPortfolios = new TreeMap<>();

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

    /**
     * Adds a distinguished portfolio description to the library.
     *
     * @param description The distinguished portfolio description to add to
     *                    the library
     * @return An existing distinguished portfolio description that was
     * displaced in the library because it had the same key
     */
    @SuppressWarnings("UnusedReturnValue")
    DistinguishedPortfolioDescription addDescription(
            @NotNull DistinguishedPortfolioDescription description) {
        return distinguishedPortfolios.put(description.getKey(), description);
    }

    @Override
    public boolean areKeyElementsOkay(String... elements) {
        return PortfolioLibrary.getInstance().areKeyElementsOkay(elements);
    }

    @Override
    public boolean areKeysSorted() {
        return (distinguishedPortfolios instanceof SortedMap);
    }

    @Override
    protected void clearDescriptions() {
        distinguishedPortfolios.clear();
    }

    @Override
    public DistinguishedPortfolioDescription[] getCatalog() {
        return distinguishedPortfolios.values().toArray(
                new DistinguishedPortfolioDescription[0]);
    }

    @Override
    public @NotNull DistinguishedPortfolio getDefaultKey() {
        return DistinguishedPortfolio.DEFAULT;
    }

    @Override
    public DistinguishedPortfolioDescription getDescription(DistinguishedPortfolio key) {
        return distinguishedPortfolios.get(key);
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
