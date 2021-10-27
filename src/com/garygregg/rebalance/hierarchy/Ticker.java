package com.garygregg.rebalance.hierarchy;

import com.garygregg.rebalance.*;
import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.countable.Price;
import com.garygregg.rebalance.countable.Purse;
import com.garygregg.rebalance.countable.Shares;
import com.garygregg.rebalance.ticker.TickerDescription;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Ticker extends
        Common<String, Common<?, ?, ?>, TickerDescription> {

    // A factory for producing artificial tickers
    private static final Factory<Ticker> factory = Ticker::getNewArtificial;

    // A lazy boy for an artificial ticker
    private static final LazyBoy<Ticker> lazyBoy = new LazyBoy<>(factory);

    // Our message logger
    private static final MessageLogger logger = new MessageLogger();

    static {
        logger.setLogger(Logger.getLogger(Ticker.class.getCanonicalName()));
    }

    // The map of weight type to activities
    private final Map<WeightType, Activity> associationMap = new HashMap<>();

    // Our ceiling function
    private final SharesFunction ceiling = new SharesFunction() {

        @Override
        public double perform(@NotNull Double argument) {
            return Math.ceil(argument);
        }
    };

    // The considered value of the ticker
    private final Purse considered = new Purse();

    // Our breakdown manager for the weight type
    private final FullValueBreakdownManager<Ticker> fullValueManager =
            new FullValueBreakdownManager<>();

    // The "not considered" value of the ticker
    private final Purse notConsidered = new Purse();

    // The proposed value of the ticker
    private final Purse proposed = new Purse();

    // Our round function
    private final SharesFunction round = new SharesFunction() {

        @Override
        public double perform(@NotNull Double argument) {
            return Math.round(argument);
        }
    };

    {

        /*
         * This common initialization block builds the association map of
         * weight type to activities.
         */

        // Level 0: Bond, cash, real-estate or stock.
        WeightType type;
        associationMap.put(type = WeightType.ALL, new Activity(type, null,
                new Association(FundType.BOND, WeightType.BOND),
                new Association(FundType.CASH, WeightType.CASH),
                new Association(FundType.REAL_ESTATE, WeightType.REAL_ESTATE),
                new Association(FundType.STOCK, WeightType.STOCK)));

        /*
         * Level 1 (Bond): Uncategorized, corporate, foreign, short, treasury,
         * high-yield, inflation or mortgage.
         *
         * (Let 'short' go before 'government'; see WeightType.)
         */
        associationMap.put(type = WeightType.BOND, new Activity(type,
                WeightType.BOND_UNCATEGORIZED,
                new Association(FundType.CORPORATE, WeightType.BOND_CORPORATE),
                new Association(FundType.FOREIGN, WeightType.BOND_FOREIGN),
                new Association(FundType.SHORT, WeightType.BOND_SHORT),
                new Association(FundType.TREASURY, WeightType.BOND_GOVERNMENT),
//              new Association(FundType.HIGH, WeightType.BOND_HIGH),
                new Association(FundType.INFLATION, WeightType.BOND_INFLATION),
                new Association(FundType.MORTGAGE, WeightType.BOND_MORTGAGE)));

        // Level 2 (Bond subtypes).
        associationMap.put(type = WeightType.BOND_CORPORATE,
                new Activity(type, null,
                        new Association(FundType.HIGH, WeightType.BOND_HIGH)));
        associationMap.put(type = WeightType.BOND_FOREIGN,
                new Activity(type, null));
        associationMap.put(type = WeightType.BOND_SHORT,
                new Activity(type, null));
        associationMap.put(type = WeightType.BOND_GOVERNMENT,
                new Activity(type, null));
        associationMap.put(type = WeightType.BOND_HIGH,
                new Activity(type, null));
        associationMap.put(type = WeightType.BOND_INFLATION,
                new Activity(type, null));
        associationMap.put(type = WeightType.BOND_MORTGAGE,
                new Activity(type, null));
        associationMap.put(type = WeightType.BOND_UNCATEGORIZED,
                new Activity(type, null));

        // Level 1 (Cash): Uncategorized or treasury.
        associationMap.put(type = WeightType.CASH,
                new Activity(type, WeightType.CASH_UNCATEGORIZED,
                        new Association(FundType.TREASURY, WeightType.CASH_GOVERNMENT)));

        // Level 2 (Cash subtypes).
        associationMap.put(type = WeightType.CASH_GOVERNMENT,
                new Activity(type, null));
        associationMap.put(type = WeightType.CASH_UNCATEGORIZED,
                new Activity(type, null));

        // Level 1 (Real-estate).
        associationMap.put(type = WeightType.REAL_ESTATE,
                new Activity(type, null));

        // Level 1 (Stock): Domestic or foreign.
        associationMap.put(type = WeightType.STOCK, new Activity(type, null,
                new Association(FundType.DOMESTIC, WeightType.STOCK_DOMESTIC),
                new Association(FundType.FOREIGN, WeightType.STOCK_FOREIGN)));

        /*
         * Level 2 (Stock subtype - Domestic stock): Large, not-large, medium
         * or small.
         */
        associationMap.put(type = WeightType.STOCK_DOMESTIC,
                new Activity(type, null,
                        new Association(FundType.LARGE, WeightType.STOCK_LARGE),
                        new Association(FundType.NOT_LARGE,
                                WeightType.STOCK_NOT_LARGE),
                        new Association(FundType.MEDIUM, WeightType.STOCK_NOT_LARGE),
                        new Association(FundType.SMALL, WeightType.STOCK_NOT_LARGE)));

        /*
         * Level 2 (Stock subtype - Foreign stock): Large, not-large, medium
         * or small.
         */
        associationMap.put(type = WeightType.STOCK_FOREIGN,
                new Activity(type, null,
                        new Association(FundType.LARGE, WeightType.STOCK_LARGE),
                        new Association(FundType.NOT_LARGE,
                                WeightType.STOCK_NOT_LARGE),
                        new Association(FundType.MEDIUM, WeightType.STOCK_NOT_LARGE),
                        new Association(FundType.SMALL, WeightType.STOCK_NOT_LARGE)));

        /*
         * Level 3 (Domestic or foreign stock subtype - Large stocks): Growth
         * or value.
         */
        associationMap.put(type = WeightType.STOCK_LARGE,
                new Activity(type, null,
                        new Association(FundType.GROWTH, WeightType.STOCK_GROWTH),
                        new Association(FundType.VALUE, WeightType.STOCK_VALUE)));

        /*
         * Level 3 (Domestic or foreign stock subtype - Not-large stocks):
         * Growth or value.
         */
        associationMap.put(type = WeightType.STOCK_NOT_LARGE,
                new Activity(type, null,
                        new Association(FundType.GROWTH, WeightType.STOCK_GROWTH),
                        new Association(FundType.VALUE, WeightType.STOCK_VALUE)));

        /*
         * Level 3 (Domestic or foreign stock subtype - Medium stocks): Growth
         * or value.
         */
        associationMap.put(type = WeightType.STOCK_MEDIUM,
                new Activity(type, null,
                        new Association(FundType.GROWTH, WeightType.STOCK_GROWTH),
                        new Association(FundType.VALUE, WeightType.STOCK_VALUE)));

        /*
         * Level 3 (Domestic or foreign stock subtype - Small stocks): Growth
         * or value.
         */
        associationMap.put(type = WeightType.STOCK_SMALL,
                new Activity(type, null,
                        new Association(FundType.GROWTH, WeightType.STOCK_GROWTH),
                        new Association(FundType.VALUE, WeightType.STOCK_VALUE)));

        // Level 4 (Large, not-large, medium or small stock subtypes)
        associationMap.put(type = WeightType.STOCK_GROWTH,
                new Activity(type, null));
        associationMap.put(type = WeightType.STOCK_VALUE,
                new Activity(type, null));
    }

    /**
     * Creates the ticker hierarchy object.
     *
     * @param ticker The key of the ticker hierarchy object
     */
    Ticker(@NotNull String ticker) {
        super(ticker);
    }

    /**
     * Gets an artificial ticker.
     *
     * @return An artificial ticker
     */
    static @NotNull Ticker getArtificial() {
        return lazyBoy.getLazily();
    }

    /**
     * Gets a new artificial ticker.
     *
     * @return A new artificial ticker
     */
    private static @NotNull Ticker getNewArtificial() {
        return new Ticker(Library.getDefaultStringKey());
    }

    @Override
    void breakdown() {
        performActivity(WeightType.ALL);
    }

    /**
     * Calculates the proposed number of shares required to result in a given
     * value, considering the current price.
     *
     * @param value The given value
     * @return The proposed number of shares required to result in a given
     * value considering the current price
     */
    public Double calculateShares(@NotNull Currency value) {
        return proposed.calculateShares(value);
    }

    @Override
    void clear() {
        getFullValueManager().clear();
    }

    /**
     * Enumerates the weight types.
     *
     * @param enumerator The enumerator receiving the weight types
     */
    public void enumerate(@NotNull WeightEnumerator enumerator) {

        /*
         * Inform the enumerator of the start of enumeration. Perform the
         * enumeration, then inform the enumerator of enumeration stop.
         */
        enumerator.start();
        enumerate(WeightType.ALL, enumerator);
        enumerator.stop();
    }

    /**
     * Enumerates the weight types.
     *
     * @param enumerator The enumerator receiving the weight types
     */
    private void enumerate(@NotNull WeightType type,
                           @NotNull WeightEnumerator enumerator) {
        associationMap.get(type).enumerate(enumerator);
    }

    /**
     * Gets the preferred balance rounding.
     *
     * @return The preferred balance rounding
     */
    public Shares getBalanceRounding() {

        /*
         * Get the ticker description. Return the minimum number of shares if
         * the ticker description is null. Otherwise, return the balance
         * rounding of the ticker description.
         */
        final TickerDescription description = getDescription();
        return (null == description) ? Shares.getMinimum() :
                description.getBalanceRounding();
    }

    @Override
    public Collection<Common<?, ?, ?>> getChildren() {
        return null;
    }

    @Override
    public @NotNull Currency getConsidered(@NotNull WeightType type) {
        return getFullValueManager().getConsidered(type);
    }

    @Override
    public Currency getConsidered() {
        return considered.getValue();
    }

    /**
     * Gets the shares of the queryable that can be considered for rebalance.
     *
     * @return The shares of the queryable that can be considered for rebalance
     */
    public Shares getConsideredShares() {
        return considered.getShares();
    }

    /**
     * Gets the breakdown manager.
     *
     * @return The breakdown manager
     */
    private @NotNull FullValueBreakdownManager<Ticker>
    getFullValueManager() {
        return fullValueManager;
    }

    @Override
    public @NotNull HoldingLineType getLineType() {
        return HoldingLineType.TICKER;
    }

    @Override
    public @NotNull Currency getNotConsidered(@NotNull WeightType type) {
        return getFullValueManager().getNotConsidered(type);
    }

    @Override
    public Currency getNotConsidered() {
        return notConsidered.getValue();
    }

    /**
     * Gets the shares of the queryable that cannot be considered for
     * rebalance.
     *
     * @return The shares of the queryable that cannot be considered for
     * rebalance
     */
    public @NotNull Shares getNotConsideredShares() {
        return notConsidered.getShares();
    }

    /**
     * Gets the price of the ticker.
     *
     * @return The price of the ticker
     */
    public Price getPrice() {
        return considered.getPrice();
    }

    @Override
    public @NotNull Currency getProposed(@NotNull WeightType type) {
        return getFullValueManager().getProposed(type);
    }

    @Override
    public Currency getProposed() {
        return proposed.getValue();
    }

    /**
     * Gets the proposed shares of the ticker.
     *
     * @return The proposed shares of the ticker
     */
    public Shares getProposedShares() {
        return proposed.getShares();
    }

    @Override
    public boolean hasCategoryType(@NotNull CategoryType type) {
        return type.equals(CategoryType.NOT_AN_ACCOUNT);
    }

    @Override
    public boolean hasFundType(@NotNull FundType type) {

        /*
         * Return true if the ticker description is not null, and also contains
         * the indicated fund type.
         */
        final TickerDescription description = getDescription();
        return (null != description) && description.hasType(type);
    }

    @Override
    public boolean hasTaxType(@NotNull TaxType type) {
        return type.equals(TaxType.NOT_AN_ACCOUNT);
    }

    /**
     * Determines if the ticker has the indicated weight type.
     *
     * @param type The indicated weight type
     * @return True if the ticker has the indicated weight type, false
     * otherwise
     */
    public boolean hasWeightType(@NotNull WeightType type) {

        /*
         * Declare and initialize the result. Get the activity corresponding
         * to the indicated weight type. Is there such an activity?
         */
        boolean result = false;
        final Activity activity = associationMap.get(type);
        if (null != activity) {

            /*
             * There is an activity associated with the indicated weight type.
             * Get the associations for the weight type. Cycle for each
             * association, or until a fund type match has been found.
             */
            final Association[] associations = activity.getAssociations();
            final int length = associations.length;
            for (int i = 0; (i < length) && (!result); ++i) {

                /*
                 * The result becomes true if the ticker has the fund type
                 * of the first/next association. If so, we stop.
                 */
                result = hasFundType(associations[i].getFirst());
            }
        }

        // Return the result.
        return result;
    }

    /**
     * Passes the number of considered shares through to the number of
     * proposed shares.
     */
    public void passThrough() {

        /*
         * Get the number of considered shares. Is the number of considered
         * shares not null?
         */
        final Shares consideredShares = getConsideredShares();
        if (null != consideredShares) {

            /*
             * The number of considered shares is not null. Set the number of
             * proposed shares to the same value.
             */
            proposed.setShares(consideredShares.getValue());
        }
    }

    /**
     * Performs an activity for a weight type.
     *
     * @param type The given weight type
     */
    private void performActivity(@NotNull WeightType type) {
        associationMap.get(type).performActivity();
    }

    @Override
    void setConsidered(double value) {
        considered.setValueAdjustShares(value);
    }

    /**
     * Sets the number of shares of the ticker holding that is available for
     * rebalancing.
     *
     * @param shares The of shares of the ticker holding that is available for
     *               rebalancing
     */
    void setConsideredShares(double shares) {
        considered.setShares(shares);
    }

    @Override
    void setCurrent() {
        getFullValueManager().setCurrent();
    }

    @Override
    void setNotConsidered(double value) {
        notConsidered.setValueAdjustShares(value);
    }

    /**
     * Sets the number of shares of the ticker holding that is not available
     * for rebalancing.
     *
     * @param shares The of shares of the ticker holding that is not available
     *               for rebalancing
     */
    void setNotConsideredShares(double shares) {
        notConsidered.setShares(shares);
    }

    /**
     * Sets the price of the ticker holding.
     *
     * @param price The price of the ticker holding
     */
    void setPrice(double price) {

        // Keep price consistent in all purses.
        considered.setPrice(price);
        notConsidered.setPrice(price);
        proposed.setPrice(price);
    }

    @Override
    void setProposed() {
        getFullValueManager().setProposed();
    }

    /**
     * Sets the proposed value of the ticker.
     *
     * @param value The proposed value of the ticker, relative to the value of
     *              the ticker that is considered for rebalance
     */
    void setProposed(double value) {
        proposed.setValueAdjustShares(value);
    }

    /**
     * Sets the proposed value of the ticker.
     *
     * @param currency The proposed value of the ticker
     */
    public void setProposed(@NotNull Currency currency) {

        // Calculate the proposed shares. Are the proposed shares not null?
        final Double proposedShares = calculateShares(currency);
        if (null != proposedShares) {

            // The proposed shares are not null. Set them.
            setProposedShares(proposedShares);
        }
    }

    /**
     * Sets the proposed number of shares of the ticker holding.
     */
    public void setProposedShares(double shares) {

        /*
         * Declare and initialize a logging level for ordinary messages. Format
         * a prefix for logger messages. Round the given number of shares based
         * on the preference rounding.
         */
        final Level ordinary = Level.FINE;
        final String prefix = String.format("Ticker '%s': ", getKey());
        final double rounded = round.getRoundedShares(shares);

        /*
         * Are the rounded number of shares not equal to the given number of
         * shares?
         */
        final Shares roundedShares = new Shares(rounded);
        if (roundedShares.areNotEqual(shares)) {

            /*
             * The rounded number of shares are not equal to the given number
             * of shares. Log this finding.
             */
            logger.logMessage(ordinary, String.format("%s%s shares were " +
                            "requested, but I needed to round it to %s " +
                            "shares.", prefix, Shares.format(shares),
                    roundedShares));
        }

        /*
         * Declare a variable to receive a minimum number of shares. Get the
         * price of the ticker. Is the price null?
         */
        double minimumShares;
        final Price price = getPrice();
        if (null == price) {

            /*
             * The price is null. Take a chance by setting the minimum required
             * number of shares to zero. Log a warning.
             */
            minimumShares = 0.;
            logger.logMessage(Level.WARNING, String.format("%sCannot " +
                    "calculate whether the minimum value requirement is " +
                    "met with a null price; taking a chance by assuming %s " +
                    "minimum shares.", prefix, minimumShares));
        }

        // The price is not null, but is it zero?
        else if (Price.getZero().equals(price)) {

            /*
             * The price is zero. Set the minimum number of shares to the
             * maximum possible value. Warn that there is no way to meet a
             * minimum value requirement with zero value price.
             */
            minimumShares = Double.MAX_VALUE;
            logger.logMessage(Level.WARNING, String.format("%sNo way to " +
                            "meet minimum value requirements with zero price.",
                    prefix));
        }

        // The price is non-null and non-zero.
        else {

            /*
             * Get the ticker description and the minimum value from the
             * description.
             */
            final TickerDescription description = getDescription();
            final Currency minimumFromDescription = (null == description) ?
                    null : description.getMinimum();

            /*
             * Calculate the minimum value from the minimum value of the ticker
             * description.
             */
            final Currency minimumValue = (null == minimumFromDescription) ?
                    Currency.getZero() : minimumFromDescription;

            // Now calculate the minimum number of shares.
            minimumShares = ceiling.getRoundedShares(minimumValue.getValue() /
                    price.getValue());
        }

        /*
         * We now have our minimum shares. Assume the rounded number of shares
         * are what we will set as the proposed number of shares. But is this
         * less than the minimum?
         */
        shares = rounded;
        if (shares < minimumShares) {

            /*
             * The rounded number of shares is less than the minimum. Set zero
             * shares if the rounded number of shares is less than half the
             * minimum. Otherwise, use the minimum number of shares.
             */
            shares = (shares < (minimumShares / 2.)) ?
                    Shares.getZero().getValue() : minimumShares;

            /*
             * Log a message about adjusting the requested number of shares
             * because of the minimum.
             */
            logger.logMessage(ordinary, String.format("%s The rounded " +
                            "number of shares, %s, is less than the minimum " +
                            "required, %s; using %s.", prefix,
                    Shares.format(rounded), Shares.format(minimumShares),
                    Shares.format(shares)));
        }

        /*
         * Reset any problems noted by the logger, and set the (possibly
         * modified) number of shares.
         */
        logger.resetProblem();
        proposed.setShares(shares);
    }

    @Override
    protected void transferValue(@NotNull Queryable<?, ?> queryable) {

        /*
         * Call the superclass method, then set the proposed value to the same
         * value as that contained in the queryable.
         */
        super.transferValue(queryable);
        setProposed(getValue(queryable.getProposed()));
    }

    private interface Function<T> {

        /**
         * Performs the function.
         *
         * @param argument The argument to the function
         * @return The result of the function
         */
        double perform(@NotNull T argument);
    }

    public interface WeightEnumerator {

        /**
         * Receives a weight type.
         *
         * @param type A weight type
         */
        void receive(@NotNull WeightType type);

        /**
         * Indicates that the enumeration has started.
         */
        void start();

        /**
         * Indicates that the enumeration has stopped.
         */
        void stop();
    }

    private static class Association extends Pair<FundType, WeightType> {

        /**
         * Constructs an association of fund type to weight type.
         *
         * @param fundType   The fund type
         * @param weightType The weight type
         */
        public Association(@NotNull FundType fundType,
                           @NotNull WeightType weightType) {
            super(fundType, weightType);
        }
    }

    private abstract class SharesFunction implements Function<Double> {

        /**
         * Gets the rounded number of shares.
         *
         * @param shares The input number of shares
         * @return The rounded number of shares
         */
        public double getRoundedShares(double shares) {

            /*
             * Get the balance rounding. Use one if the balance rounding is
             * null. Otherwise, use the given balance rounding.
             */
            final Shares balanceRounding = getBalanceRounding();
            final double roundingValue = (null == balanceRounding) ?
                    Shares.getOne().getValue() : balanceRounding.getValue();

            // Perform the shares function, and return the result.
            return perform(shares / roundingValue) * roundingValue;
        }
    }

    private class Activity {

        // Associations of contained fund types to weight types
        private final Association[] associations;

        // The default child weight type if there are no fund type matches
        private final WeightType defaultChild;

        // The weight type associated with the activity
        private final WeightType weightType;

        /**
         * Constructs the activity.
         *
         * @param weightType   The weight type associated with the activity
         * @param defaultChild The child weight type if there are no fund type
         *                     matches
         * @param association  Associations of contained fund type to weight
         *                     types
         */
        public Activity(@NotNull WeightType weightType,
                        WeightType defaultChild,
                        @NotNull Association... association) {

            // Assign the member variables.
            this.associations = association;
            this.defaultChild = defaultChild;
            this.weightType = weightType;
        }

        /**
         * Enumerates the weight types of a ticker.
         *
         * @param enumerator The enumerator receiving the weight types
         */
        public void enumerate(@NotNull WeightEnumerator enumerator) {

            /*
             * Tell the enumerator about the current weight type. Get a child
             * weight type. Is the child weight type not null?
             */
            enumerator.receive(getWeightType());
            WeightType child = getChild();
            if (null != child) {

                /*
                 * The child weight type is not null. Perform the enumeration
                 * for the non-null child weight type.
                 */
                Ticker.this.enumerate(child, enumerator);
            }
        }

        /**
         * Gets the associations of contained fund types to weight types.
         *
         * @return The associations of contained fund types to weight types
         */
        public @NotNull Association[] getAssociations() {
            return associations;
        }

        /**
         * Gets a child weight type.
         *
         * @return A child weight type
         */
        private WeightType getChild() {

            /*
             * Declare a variable to hold an association. Get the variable
             * length array of associations of fund type to weight types.
             * Determine the length of this array.
             */
            Association association;
            final Association[] associations = getAssociations();
            final int associationsLength = associations.length;

            /*
             * Cycle until we locate a child weight type, or until the possible
             * matches of children are exhausted.
             */
            WeightType child = null;
            for (int i = 0; (i < associationsLength) && (null == child); ++i) {

                /*
                 * Get the first/next association. Does this ticker have the
                 * indicated fund type?
                 */
                association = associations[i];
                if (hasFundType(association.getFirst())) {

                    /*
                     * The ticker has the associated fund type. Set the child
                     * weight type.
                     */
                    child = association.getSecond();
                }
            }

            /*
             * Set the child weight type to the default child if the child
             * weight type is null.
             */
            if (null == child) {
                child = getDefaultChild();
            }

            // Return the child weight type.
            return child;
        }

        /**
         * Gets the default child weight type if there are no fund type
         * matches.
         *
         * @return The default child weight type if there are no fund type
         * matches
         */
        public WeightType getDefaultChild() {
            return defaultChild;
        }

        /**
         * Gets the weight type associated with the activity.
         *
         * @return The weight type associated with the activity
         */
        public @NotNull WeightType getWeightType() {
            return weightType;
        }

        /**
         * Performs the activity.
         */
        public void performActivity() {

            // Get a child weight type. Is the child weight type not null?
            WeightType child = getChild();
            if (null != child) {

                /*
                 * The child weight type is not null. Perform an activity for
                 * the non-null child weight type.
                 */
                Ticker.this.performActivity(child);
            }

            // Add value for the weight type.
            getFullValueManager().add(getWeightType(), Ticker.this);
        }
    }
}
