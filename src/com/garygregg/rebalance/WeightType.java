package com.garygregg.rebalance;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public enum WeightType {

    // No parent for 'all'.
    ALL("All"),

    // Tickers must contain fund type CASH.
    CASH("Cash general", ALL),

    // Tickers must contain fund type CASH and fund type TREASURY.
    CASH_GOVERNMENT("Cash government", CASH),

    // Tickers must contain fund type CASH and not fund type TREASURY.
    CASH_UNCATEGORIZED("Cash commercial", CASH),

    // Tickers must contain fund type BOND.
    BOND("Bond general", ALL),

    // Tickers must contain fund type CORPORATE.
    BOND_CORPORATE("Bond corporate", BOND),

    // Tickers must contain fund type BOND and fund type FOREIGN.
    BOND_FOREIGN("Bond foreign", BOND),

    // Tickers must contain fund type BOND and fund type TREASURY.
    BOND_GOVERNMENT("Bond government", BOND),

    // Tickers must contain fund type HIGH. Necessarily these are 'corporate'.
    BOND_HIGH("Bond high-yield", BOND),

    // Tickers must contain fund type INFLATION.
    BOND_INFLATION("Bond inflation", BOND),

    // Tickers must contain fund type MORTGAGE.
    BOND_MORTGAGE("Bond mortgage", BOND),

    /*
     * Tickers must contain fund type BOND and fund type DOMESTIC, but no
     * other.
     */
    BOND_UNCATEGORIZED("Bond unspecific", BOND),

    // Tickers must contain fund type SHORT.
    BOND_SHORT("Bond short-term", BOND),

    // Tickers must contain fund type REAL_ESTATE.
    REAL_ESTATE("Real-estate", ALL),

    // Tickers must contain fund type STOCK.
    STOCK("Stock general", ALL),

    // Tickers must contain fund type STOCK and fund type DOMESTIC.
    STOCK_DOMESTIC("Stock domestic", STOCK),

    // Tickers must contain fund type STOCK and fund type FOREIGN.
    STOCK_FOREIGN("Stock foreign", STOCK),

    // Tickers must contain fund type LARGE.
    STOCK_LARGE("Stock large-cap", STOCK_DOMESTIC, STOCK_FOREIGN),

    // Tickers must contain fund type NOT_LARGE.
    STOCK_NOT_LARGE("Stock not large", STOCK_DOMESTIC, STOCK_FOREIGN),

    // Tickers must contain fund type MEDIUM.
    STOCK_MEDIUM("Stock mid-cap", STOCK_NOT_LARGE),

    // Tickers must contain fund type SMALL.
    STOCK_SMALL("Stock small-cap", STOCK_NOT_LARGE),

    // Tickers must contain fund type GROWTH.
    STOCK_GROWTH("Stock growth", STOCK_LARGE, STOCK_NOT_LARGE, STOCK_MEDIUM,
            STOCK_SMALL),

    // Tickers must contain fund type VALUE.
    STOCK_VALUE("Stock value", STOCK_LARGE, STOCK_NOT_LARGE, STOCK_MEDIUM,
            STOCK_SMALL);

    // A list of level one weight types
    private static final List<WeightType> levelOne;

    // A list of level zero weight types
    private static final List<WeightType> levelZero = List.of(WeightType.ALL);

    // The number of children of the weight type that has the most
    private static final int maxChildren;

    static {

        /*
         * Build the list of level one weight types. Get a map entry containing
         * the weight type that has the most children. Extract the type and set
         * the ceiling of its log2 if the entry is not null. Otherwise, set 1.
         */
        levelOne = buildLevelOneList();
        final Map.Entry<WeightType, Integer> entry = getMaxEntry();
        maxChildren = (null == entry) ? 1 : entry.getValue();
    }

    // The parents for the weight type
    private final WeightType[] parents;

    // The 'soft' name of the type
    private final String softName;

    /**
     * Constructs the weight type.
     *
     * @param parents The parents for the weight type
     */
    WeightType(@NotNull String softName, WeightType... parents) {

        // Assign the member variables.
        this.parents = parents;
        this.softName = softName;
    }

    /**
     * Builds a list of level zero weight types.
     *
     * @return A list of level zero weight types
     */
    private static @NotNull @UnmodifiableView List<WeightType>
    buildLevelOneList() {

        /*
         * Create an empty, modifiable list to hold the level one weight types.
         * Cycle for each weight type.
         */
        final List<WeightType> levelOne = new ArrayList<>();
        for (WeightType type : WeightType.values()) {

            /*
             * Add the first/next weight type to the level one list if it
             * passes the level one test.
             */
            if (testLevelOne(type)) {
                levelOne.add(type);
            }
        }

        // Return the level one weight list as an unmodifiable list.
        return Collections.unmodifiableList(levelOne);
    }

    /**
     * Returns a list of level one weight types.
     *
     * @return A list of level one weight types
     */
    public static @NotNull List<WeightType> getLevelOne() {
        return levelOne;
    }

    /**
     * Returns a list of level zero weight types.
     *
     * @return A list of level zero weight types
     */
    public static @NotNull List<WeightType> getLevelZero() {
        return levelZero;
    }

    /**
     * Gets the number of children of the weight type that has the most.
     *
     * @return The number of children of the weight type that has the
     * most
     */
    public static int getMaxChildren() {
        return maxChildren;
    }

    /**
     * Gets a map entry containing the weight type that has the most
     * children.
     *
     * @return A map entry containing the weight type that has the most
     * children
     */
    private static Map.@Nullable Entry<WeightType, Integer> getMaxEntry() {

        /*
         * Declare and initialize a map with weight type keys and integer
         * values. Declare an integer to receive a count.
         */
        final Map<WeightType, Integer> map = new HashMap<>();
        Integer count;

        /*
         * Declare a variable to receive an array of weight type parents. Cycle
         * for each weight type.
         */
        WeightType[] parents;
        for (WeightType type : WeightType.values()) {

            // Get the parents of the first/next type. Cycle for each parent.
            parents = type.getParents();
            for (WeightType parent : parents) {

                /*
                 * Get the count of the first next parent from the map. Put the
                 * parent back in the map with one if the existing count is
                 * null. Otherwise, increment the count.
                 */
                count = map.get(parent);
                map.put(parent, (null == count) ? 1 : ++count);
            }
        }

        /*
         * Return the map key/value entry for the weight type that has the
         * highest count if the map is not empty. Otherwise, return null.
         */
        return (map.isEmpty()) ? null : Collections.max(map.entrySet(),
                Comparator.comparingInt(Map.Entry::getValue));
    }

    /**
     * Determine if a weight type is greater than level one.
     *
     * @param type A weight type to test
     * @return True if the weight type is greater than level one; false
     * otherwise
     */
    public static boolean isHighLevel(@NotNull WeightType type) {
        return !(isLevelZero(type) || isLevelOne(type));
    }

    /**
     * Determines if a weight type is a level one weight type.
     *
     * @param type A weight type to test
     * @return True if the weight type is a level one weight type; false
     * otherwise
     */
    public static boolean isLevelOne(@NotNull WeightType type) {
        return levelOne.contains(type);
    }

    /**
     * Determines if a weight type is a level zero weight type.
     *
     * @param type A weight type to test
     * @return True if the weight type is a level zero weight type; false
     * otherwise
     */
    public static boolean isLevelZero(@NotNull WeightType type) {
        return levelZero.contains(type);
    }

    /**
     * Tests a weight type to determine if it is a level one weight type.
     *
     * @param type A weight type to test
     * @return True if the weight type is a level one weight type; false
     * otherwise
     */
    private static boolean testLevelOne(@NotNull WeightType type) {

        /*
         * Get the parents of the given type. Declare and initialize a variable
         * to a test if there is just one parent. Is there just one parent?
         */
        final WeightType[] parents = type.getParents();
        boolean result = (1 == parents.length);
        if (result) {

            /*
             * There is just one parent. Get the parent, and test to be certain
             * that it is a level zero type.
             */
            final WeightType parent = parents[0];
            result = (null != parent) && isLevelZero(parent);
        }

        // Return the result.
        return result;
    }

    /**
     * Gets the parents for the weight type.
     *
     * @return The parents for the weight type
     */
    public @NotNull WeightType[] getParents() {
        return parents;
    }

    /**
     * Gets the 'soft' name of the type.
     *
     * @return The 'soft' name of the type
     */
    public @NotNull String getSoftName() {
        return softName;
    }

    /**
     * Determines if a weight type is a high level weight type.
     *
     * @return True if the weight type is a high level weight type
     */
    public boolean isHighLevel() {
        return isHighLevel(this);
    }

    /**
     * Determines if the weight type is a level one weight type.
     *
     * @return True if the weight type is a level one weight type
     */
    public boolean isLevelOne() {
        return isLevelOne(this);
    }

    /**
     * Determines if the weight type is a level zero weight type.
     *
     * @return True if the weight type is a level zero weight type
     */
    public boolean isLevelZero() {
        return isLevelZero(this);
    }
}
