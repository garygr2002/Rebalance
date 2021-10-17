package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.*;
import com.garygregg.rebalance.account.AccountDescription;
import com.garygregg.rebalance.countable.Percent;
import com.garygregg.rebalance.detailed.DetailedDescription;
import com.garygregg.rebalance.detailed.DetailedLibrary;
import com.garygregg.rebalance.hierarchy.Account;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class AccountRebalancer extends Rebalancer {

    // A list of weight types to account valuation pairs
    private final static List<Pair<WeightType, ValueFromAccount>> accountList =
            new ArrayList<>();

    // Our factory for creating value-from-account objects
    private static final Factory<FundType, ValueFromAccount>
            accountValueFactory = ValueFromAccount::new;

    // A list of weight types to detailed valuation pairs
    private final static List<Pair<WeightType, ValueFromDetailed>>
            detailedList = new ArrayList<>();

    // Our factory for creating value-from-detailed objects
    private static final Factory<WeightType, ValueFromDetailed>
            detailedValueFactory = ValueFromDetailed::new;

    // The distinguished value for everything
    private static final double everything =
            Percent.getOneHundred().getValue();

    // The distinguished value for nothing
    private static final double nothing = Percent.getZero().getValue();

    // A map of fund types to account description valuation objects
    private static final Map<FundType, ValueFromAccount> valueFromAccountMap =
            createMap(FundType.values(), accountValueFactory);

    // A map of weight types to detailed description valuation objects
    private static final Map<WeightType, ValueFromDetailed>
            valueFromDetailedMap = createMap(WeightType.values(),
            detailedValueFactory);

    static {

        // Build the account list and detailed list.
        buildAccountList();
        buildDetailedList();
    }

    /**
     * Adjust a weight map for relative market valuation.
     *
     * @param weightMap A weight map
     */
    private static void adjust(@NotNull Map<WeightType, Double> weightMap) {

        /*
         * Get the preference manager, and the current S&P 500 setting from the
         * manager. Is the current S&P 500 value set?
         */
        final PreferenceManager manager = PreferenceManager.getInstance();
        final Double current = manager.getCurrent();
        if (null != current) {

            /*
             * The current S&P 500 value is set. Get the high S&P 500 setting
             * from the manager. Is the high S&P 500 value set, and is its
             * value not zero?
             */
            final Double high = manager.getHigh();
            if (!((null == high) || (0. == high))) {

                /*
                 * The current S&P 500 value is set, and its value is not zero.
                 * Divide the current value by the high value and adjust the
                 * weights in the map with this ratio.
                 */
                adjust(weightMap, current / high);
            }
        }
    }

    /**
     * Adjust a weight map for relative market valuation.
     *
     * @param weightMap A weight map
     * @param ratio     The ratio for adjustment
     */
    @SuppressWarnings("unused")
    private static void adjust(@NotNull Map<WeightType, Double> weightMap,
                               double ratio) {

        /*
         * TODO: Fill this out.
         *
         * 1) Sum bond, cash and real-estate weights and save as other_sum;
         * 2) Add stock weight and save as total_sum;
         * 3) multiply stock weight by S&P current, and divide by high; save as
         * stock weight;
         * 4) subtract stock weight from total_sum and save as modified_other;
         * 5) Multiply bond, cash and real-estate weights by modified_other,
         * and divide by other_sum, saving the result in each non-stock weight.
         */
    }

    /**
     * Builds the account list.
     */
    private static void buildAccountList() {

        // 1
        accountList.add(new Pair<>(WeightType.BOND,
                valueFromAccountMap.get(FundType.BOND)));

        // 2
        accountList.add(new Pair<>(WeightType.CASH,
                valueFromAccountMap.get(FundType.CASH)));

        // 3
        accountList.add(new Pair<>(WeightType.REAL_ESTATE,
                valueFromAccountMap.get(FundType.REAL_ESTATE)));

        // 4
        accountList.add(new Pair<>(WeightType.STOCK,
                valueFromAccountMap.get(FundType.STOCK)));
    }

    /**
     * Builds the detailed list.
     */
    private static void buildDetailedList() {

        // 1
        WeightType type = WeightType.BOND;
        detailedList.add(new Pair<>(type, valueFromDetailedMap.get(type)));

        // 2
        type = WeightType.BOND_CORPORATE;
        detailedList.add(new Pair<>(type, valueFromDetailedMap.get(type)));

        // 3
        type = WeightType.BOND_FOREIGN;
        detailedList.add(new Pair<>(type, valueFromDetailedMap.get(type)));

        // 4
        type = WeightType.BOND_GOVERNMENT;
        detailedList.add(new Pair<>(type, valueFromDetailedMap.get(type)));

        // 5
        type = WeightType.BOND_HIGH;
        detailedList.add(new Pair<>(type, valueFromDetailedMap.get(type)));

        // 6
        type = WeightType.BOND_INFLATION;
        detailedList.add(new Pair<>(type, valueFromDetailedMap.get(type)));

        // 7
        type = WeightType.BOND_MORTGAGE;
        detailedList.add(new Pair<>(type, valueFromDetailedMap.get(type)));

        // 8
        type = WeightType.BOND_SHORT;
        detailedList.add(new Pair<>(type, valueFromDetailedMap.get(type)));

        // 9
        type = WeightType.BOND_UNCATEGORIZED;
        detailedList.add(new Pair<>(type, valueFromDetailedMap.get(type)));

        // 10
        type = WeightType.CASH;
        detailedList.add(new Pair<>(type, valueFromDetailedMap.get(type)));

        // 11
        type = WeightType.CASH_GOVERNMENT;
        detailedList.add(new Pair<>(type, valueFromDetailedMap.get(type)));

        // 12
        type = WeightType.CASH_UNCATEGORIZED;
        detailedList.add(new Pair<>(type, valueFromDetailedMap.get(type)));

        // 13
        type = WeightType.REAL_ESTATE;
        detailedList.add(new Pair<>(type, valueFromDetailedMap.get(type)));

        // 14
        type = WeightType.STOCK;
        detailedList.add(new Pair<>(type, valueFromDetailedMap.get(type)));

        // 15
        type = WeightType.STOCK_DOMESTIC;
        detailedList.add(new Pair<>(type, valueFromDetailedMap.get(type)));

        // 16
        type = WeightType.STOCK_FOREIGN;
        detailedList.add(new Pair<>(type, valueFromDetailedMap.get(type)));

        // 17
        type = WeightType.STOCK_LARGE;
        detailedList.add(new Pair<>(type, valueFromDetailedMap.get(type)));

        // 18
        type = WeightType.STOCK_GROWTH;
        detailedList.add(new Pair<>(type, valueFromDetailedMap.get(type)));

        // 19
        type = WeightType.STOCK_MEDIUM;
        detailedList.add(new Pair<>(type, valueFromDetailedMap.get(type)));

        // 20
        type = WeightType.STOCK_NOT_LARGE;
        detailedList.add(new Pair<>(type, valueFromDetailedMap.get(type)));

        // 21
        type = WeightType.STOCK_SMALL;
        detailedList.add(new Pair<>(type, valueFromDetailedMap.get(type)));

        // 22
        type = WeightType.STOCK_VALUE;
        detailedList.add(new Pair<>(type, valueFromDetailedMap.get(type)));
    }

    /**
     * Creates a map of identifier types to description valuation objects.
     *
     * @param values           An array of all possible identifier values
     * @param factory          A factory for producing description valuation
     *                         objects
     * @param <IdentifierType> The identifier type
     * @param <ProductType>    The description valuation type
     * @return A map of identifier types to description valuation objects
     */
    private static <IdentifierType, ProductType> @NotNull
            Map<IdentifierType, ProductType> createMap(
            @NotNull IdentifierType @NotNull [] values,
            @NotNull Factory<IdentifierType, ProductType> factory) {

        // Create a new map, and cycle for each possible value.
        final Map<IdentifierType, ProductType> map = new HashMap<>();
        for (IdentifierType type : values) {

            /*
             * Add a new description valuation object to the map index by
             * identifier type.
             */
            map.put(type, factory.produce(type));
        }

        // Return the map.
        return map;
    }

    /**
     * Creates a weight map for an account.
     *
     * @param account The account for which to create a weight map
     * @param adjust  True if the map should be adjusted for relative market
     *                valuation; false otherwise
     * @return A weight map for the account
     */
    @SuppressWarnings("unused")
    protected static @NotNull Map<WeightType, Double> getWeights(
            @NotNull Account account, boolean adjust) {

        // Declare an empty weight map, and initialize it.
        final Map<WeightType, Double> weightMap = new HashMap<>();
        initializeMap(weightMap);

        /*
         * Set the descriptions in the account list, and overlay the weight
         * map.
         */
        setDescription(accountList, account.getDescription());
        overlay(weightMap, accountList);

        /*
         * Set the descriptions in the detailed list, and overlay the weight
         * map a second time.
         */
        setDescription(detailedList, DetailedLibrary.getInstance().
                getDescription(account.getKey()));
        overlay(weightMap, detailedList);

        // Adjust the weight map for market valuation if so indicated.
        if (adjust) {
            adjust(weightMap);
        }

        // Return the weight map.
        return weightMap;
    }

    /**
     * Initializes a weight map to default values.
     *
     * @param weightMap A weight map
     */
    private static void initializeMap(@NotNull Map<WeightType, Double>
                                              weightMap) {

        // This is the only place default weight values should be hard-coded.
        weightMap.put(WeightType.ALL, everything);
        weightMap.put(WeightType.BOND, 36.);
        weightMap.put(WeightType.BOND_CORPORATE, 12.5);
        weightMap.put(WeightType.BOND_FOREIGN, 7.);
        weightMap.put(WeightType.BOND_GOVERNMENT, nothing);
        weightMap.put(WeightType.BOND_HIGH, 5.);
        weightMap.put(WeightType.BOND_INFLATION, 5.);
        weightMap.put(WeightType.BOND_MORTGAGE, 8.);
        weightMap.put(WeightType.BOND_UNCATEGORIZED, 12.5);
        weightMap.put(WeightType.BOND_SHORT, 50.);
        weightMap.put(WeightType.CASH, 10.);
        weightMap.put(WeightType.CASH_GOVERNMENT, 50.);
        weightMap.put(WeightType.CASH_UNCATEGORIZED, 50.);
        weightMap.put(WeightType.REAL_ESTATE, 4.);
        weightMap.put(WeightType.STOCK, 50.);
        weightMap.put(WeightType.STOCK_DOMESTIC, 60.);
        weightMap.put(WeightType.STOCK_FOREIGN, 40.);
        weightMap.put(WeightType.STOCK_GROWTH, 40.);
        weightMap.put(WeightType.STOCK_LARGE, 60.);
        weightMap.put(WeightType.STOCK_MEDIUM, 20.);
        weightMap.put(WeightType.STOCK_NOT_LARGE, 40.);
        weightMap.put(WeightType.STOCK_SMALL, 20.);
        weightMap.put(WeightType.STOCK_VALUE, 60.);
    }

    /**
     * Overlays values in a weight map.
     *
     * @param weightMap         The weight map to modify
     * @param pairs             A list of weight type to value wrapper pairs
     * @param <DescriptionType> The description type
     * @param <WrapperType>     The wrapper type
     */
    private static <DescriptionType extends Description<?>,
            WrapperType extends ValueWrapper<?, DescriptionType>>
    void overlay(@NotNull Map<WeightType, Double> weightMap,
                 @NotNull List<Pair<WeightType, WrapperType>> pairs) {

        // Cycle for each pair in the list, and overlay the value.
        for (Pair<WeightType, WrapperType> pair : pairs) {
            overlayValue(weightMap, pair.getFirst(), pair.getSecond());
        }
    }

    /**
     * Overlays value in a weight map.
     *
     * @param weightMap  The weight map to modify
     * @param weightType The weight type to modify
     * @param wrapper    A value wrapper
     */
    private static void overlayValue(
            @NotNull Map<WeightType, Double> weightMap,
            @NotNull WeightType weightType,
            @NotNull ValueWrapper<?, ?> wrapper) {

        // Get value from the wrapper. Is the value not null?
        final Double value = wrapper.getValue();
        if (null != value) {

            /*
             * The value is not null. Modify the weight map using the weight
             * type and the value.
             */
            weightMap.put(weightType, value);
        }
    }

    /**
     * Sets the description in a list of value wrappers.
     *
     * @param pairs             A list of weight type to value wrapper pairs
     * @param <DescriptionType> The description type
     * @param <WrapperType>     The wrapper type
     */
    private static <DescriptionType extends Description<?>,
            WrapperType extends ValueWrapper<?, DescriptionType>>
    void setDescription(@NotNull List<Pair<WeightType, WrapperType>> pairs,
                        DescriptionType description) {

        // Set the description in the second element of each pair.
        for (Pair<WeightType, WrapperType> pair : pairs) {
            pair.getSecond().setDescription(description);
        }
    }

    /**
     * Rebalances an account.
     *
     * @param account The account to rebalance
     * @return True if the account was successfully rebalanced; false
     * otherwise
     */
    public abstract boolean rebalance(@NotNull Account account);

    private interface Factory<IdentifierType, ProductType> {

        /**
         * Produces a product of the indicated type.
         *
         * @param identifierType The identifier for the product
         * @return A product of the indicated type
         */
        @NotNull ProductType produce(@NotNull IdentifierType identifierType);
    }

    private static class ValueFromAccount extends
            ValueWrapper<FundType, AccountDescription> {

        /**
         * Constructs the value-from-account valuator.
         *
         * @param type A fund type for the valuator
         */
        public ValueFromAccount(@NotNull FundType type) {
            super(type);
        }

        @Contract(pure = true)
        @Override
        public @Nullable Double getValue() {

            /*
             * Get the account description. Return null if the description is
             * null. Otherwise, return the allocation indicated by the
             * identifier.
             */
            final AccountDescription description = getDescription();
            return (null == description) ? null :
                    description.getAllocation(getIdentifier());
        }
    }

    private static class ValueFromDetailed extends ValueWrapper<WeightType, DetailedDescription> {

        /**
         * Constructs the value-from-detailed valuator.
         *
         * @param type A weight type for the valuator
         */
        public ValueFromDetailed(@NotNull WeightType type) {
            super(type);
        }

        @Contract(pure = true)
        @Override
        public @Nullable Double getValue() {

            /*
             * Get the detailed description. Return null if the description is
             * null. Otherwise, return the allocation indicated by the
             * identifier.
             */
            final DetailedDescription description = getDescription();
            return (null == description) ? null :
                    description.getAllocation(getIdentifier());
        }
    }

    private static abstract class ValueWrapper<IdentifierType,
            DescriptionType extends Description<?>> {

        // The identifier for the wrapper
        private final IdentifierType identifier;

        // The wrapped description
        private DescriptionType description;

        /**
         * Constructs the value wrapper.
         *
         * @param identifier The identifier for the wrapper
         */
        protected ValueWrapper(@NotNull IdentifierType identifier) {
            this.identifier = identifier;
        }

        /**
         * Gets the wrapped description.
         *
         * @return The wrapped description
         */
        protected DescriptionType getDescription() {
            return description;
        }

        /**
         * Gets the identifier assigned to the wrapper.
         *
         * @return The identifier assigned to the wrapper
         */
        public @NotNull IdentifierType getIdentifier() {
            return identifier;
        }

        /**
         * Gets the value from the description.
         *
         * @return The value from the description
         */
        public abstract Double getValue();

        /**
         * Sets the description.
         *
         * @param description The description
         */
        public void setDescription(DescriptionType description) {
            this.description = description;
        }
    }
}
