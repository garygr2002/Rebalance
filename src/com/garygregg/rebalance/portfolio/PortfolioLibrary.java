package com.garygregg.rebalance.portfolio;

import com.garygregg.rebalance.Library;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class PortfolioLibrary extends Library<String, PortfolioDescription> {

    // The singleton portfolio library
    private static final PortfolioLibrary library = new PortfolioLibrary();

    // A map of portfolio names to portfolio description objects
    private final Map<String, PortfolioDescription> portfolios = new TreeMap<>();

    /**
     * Constructs the portfolio library.
     */
    private PortfolioLibrary() {
        addLineCode('P');
    }

    /**
     * Gets a portfolio library instance.
     *
     * @return A portfolio library instance
     */
    public static @NotNull PortfolioLibrary getInstance() {
        return library;
    }

    /**
     * Adds a portfolio description to the library.
     *
     * @param description The portfolio description to add to the library
     * @return An existing portfolio description that was displaced in the
     * library because it had the same name
     */
    PortfolioDescription addDescription(
            @NotNull PortfolioDescription description) {
        return portfolios.put(description.getKey(), description);
    }

    @Override
    public boolean areKeysSorted() {
        return (portfolios instanceof SortedMap);
    }

    @Override
    protected void clearDescriptions() {
        portfolios.clear();
    }

    @Override
    public PortfolioDescription[] getCatalog() {
        return portfolios.values().toArray(new PortfolioDescription[0]);
    }

    @Override
    public @NotNull String getDefaultKey() {
        return getDefaultStringKey();
    }

    @Override
    public PortfolioDescription getDescription(@NotNull String key) {
        return portfolios.get(key);
    }

    @Override
    public int getElementCount() {
        return PortfolioFields.values().length;
    }
}
