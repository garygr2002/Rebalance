package com.garygregg.rebalance.ticker;

import com.garygregg.rebalance.toolkit.Library;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public final class TickerLibrary extends Library<String, TickerDescription> {

    // The singleton ticker library
    private static final TickerLibrary library = new TickerLibrary();

    // A map of ticker symbols to ticker description objects
    private final Map<String, TickerDescription> tickers = new TreeMap<>();

    /**
     * Constructs the ticker library.
     */
    private TickerLibrary() {

        /*
         * Add all the ticker line codes: F = Open-end; J = Home equity, loan
         * or pension; Q = Individual stock; X = ETFs.
         */
        addLineCode(getFundCode());
        addLineCode(getNotConsideredCode());
        addLineCode(getStockCode());
        addLineCode(getETFCode());
    }

    /**
     * Gets the ETF line code.
     *
     * @return The ETF line code
     */
    public static @NotNull Character getETFCode() {
        return Character.toUpperCase('X');
    }

    /**
     * Gets the fund line code.
     *
     * @return The fund line code
     */
    public static @NotNull Character getFundCode() {
        return Character.toUpperCase('F');
    }

    /**
     * Gets a ticker library instance.
     *
     * @return A ticker library instance
     */
    public static @NotNull TickerLibrary getInstance() {
        return library;
    }

    /**
     * Gets the "Not Considered" line code.
     *
     * @return The "Not Considered" line code
     */
    public static @NotNull Character getNotConsideredCode() {
        return Character.toUpperCase('J');
    }

    /**
     * Gets the single stock line code.
     *
     * @return The single stock line code
     */
    public static @NotNull Character getStockCode() {
        return Character.toUpperCase('Q');
    }

    /**
     * Adds a ticker description to the library.
     *
     * @param description The ticker description to add to the library
     * @return An existing ticker description that was displaced in the
     * library because it had the same ticker
     */
    TickerDescription addDescription(@NotNull TickerDescription description) {
        return tickers.put(description.getTicker(), description);
    }

    @Override
    public boolean areKeysSorted() {
        return (tickers instanceof SortedMap);
    }

    @Override
    protected void clearDescriptions() {
        tickers.clear();
    }

    @Override
    public TickerDescription[] getCatalog() {
        return tickers.values().toArray(new TickerDescription[0]);
    }

    @Override
    public @NotNull String getDefaultKey() {
        return getDefaultStringKey();
    }

    @Override
    public TickerDescription getDescription(@NotNull String key) {
        return tickers.get(key);
    }

    @Override
    public int getElementCount() {
        return TickerFields.values().length;
    }
}
