package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.hierarchy.Account;
import com.garygregg.rebalance.hierarchy.Ticker;
import com.garygregg.rebalance.portfolio.PortfolioDescription;
import com.garygregg.rebalance.toolkit.WeightType;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

class WeightRebalancer extends AccountRebalancer
        implements Ticker.WeightEnumerator {

    // The value of zero currency
    private static final Currency zero = Currency.getZero();

    // The root rebalance node
    private final RebalanceNode root = new RebalanceNode(WeightType.ALL, 1.);

    // The current rebalance node
    private RebalanceNode currentNode;

    // The current ticker
    private Ticker currentTicker;

    // A map of weight types to weights
    private Map<WeightType, Double> weightMap;

    /**
     * Constructs an account rebalancer.
     */
    public WeightRebalancer() {
        initialize();
    }

    /**
     * Determines whether an account should have an equity weight adjustment.
     *
     * @param account An account
     * @return True if the account should have an equity weight adjustment;
     * false otherwise
     */
    private static boolean shouldAdjust(@NotNull Account account) {

        /*
         * Get the portfolio description from the account. Return true if the
         * portfolio description is not null, and it indicates that the
         * adjustment should occur.
         */
        final PortfolioDescription description =
                account.getPortfolioDescription();
        return ((null != description) && description.shouldAdjust());
    }

    /**
     * Adjust the current node based on an incoming weight type.
     *
     * @param type The incoming weight type
     * @return The current node
     */
    private @NotNull RebalanceNode adjustCurrent(@NotNull WeightType type) {

        /*
         * Get any existing child of the current node with the incoming weight
         * type. Is there no existing child for the incoming weight type?
         */
        RebalanceNode node = currentNode.getChild(type);
        if (null == node) {

            /*
             * There is no existing child for the incoming weight type. Get the
             * weight map. Create a new child with the weight type and weight
             * from the weight map if the weight map is not null. Otherwise,
             * use a default weight.
             */
            final Map<WeightType, Double> weightMap = getWeightMap();
            currentNode.addChild(node = new RebalanceNode(type,
                    (null == weightMap) ? 1. : weightMap.get(type)));
        }

        // Set the new current node, and return it.
        return currentNode = node;
    }

    /**
     * Checks the root node.
     *
     * @param type A received weight type
     * @return True if the root node is the current node
     */
    private boolean checkRoot(@NotNull WeightType type) {
        return type.equals(root.getType());
    }

    @Override
    protected Currency doRebalance(@NotNull Account account) {

        /*
         * Set the account key in the rebalance node class. Clear the root
         * node.
         */
        RebalanceNode.setAccountKey(account.getKey());
        root.clear();

        /*
         * Set the weight map based on the given account, and cycle for each
         * ticker in the account.
         */
        setWeightMap(getWeights(account, shouldAdjust(account)));
        for (Ticker ticker : account.getChildren()) {

            /*
             * Set the current ticker, and enumerate the weight types of the
             * ticker.
             */
            currentTicker = ticker;
            ticker.enumerate(this);
        }

        /*
         * Reinitialize the rebalancer member variables. Get the proposed value
         * of the account.
         */
        initialize();
        Currency currency = account.getProposed();

        /*
         * Set the absolute (not-relative) proposed value of the root node the
         * same as that of the account, receiving a residual. Return null if
         * rebalance node class had a problem. Otherwise, return the received
         * residual.
         */
        currency = root.setProposed((null == currency) ?
                zero : currency, false);
        return RebalanceNode.hadProblem() ? null : currency;
    }

    /**
     * Gets the map of weight types to weight.
     *
     * @return The map of weight types to weight
     */
    private Map<WeightType, Double> getWeightMap() {
        return weightMap;
    }

    /**
     * Creates a weight map for a rebalance.
     *
     * @param account The account for which to create a weight map
     * @param adjust  True if the map should be adjusted for relative market
     *                valuation; false otherwise
     * @return A weight map for the account
     */
    protected @NotNull Map<WeightType, Double> getWeights(
            @NotNull Account account, boolean adjust) {

        /*
         * The default is to get weights for percentage. Override this method
         * in a subclass to do something else.
         */
        return getWeightsForPercentage(account, shouldAdjust(account));
    }

    /**
     * Initializes member variables.
     */
    private void initialize() {

        // Clear the current node and current ticker.
        currentNode = null;
        currentTicker = null;
    }

    @Override
    public void receive(@NotNull Ticker ticker) {

        /*
         * TODO:
         *
         * Let the current node know that we are at a leaf of the weight tree
         * for the current ticker.
         */
    }

    @Override
    public void receive(@NotNull WeightType type) {

        /*
         * Get a rebalance node for the incoming weight type, and add the
         * current ticker to the node.
         */
        final RebalanceNode node = checkRoot(type) ? root :
                adjustCurrent(type);
        node.addTicker(currentTicker);
    }

    /**
     * Sets the map of weight types to weight.
     *
     * @param weightMap The map of weight type to weight
     */
    private void setWeightMap(Map<WeightType, Double> weightMap) {
        this.weightMap = weightMap;
    }

    @Override
    public void start() {
        currentNode = root;
    }

    @Override
    public void stop() {

        // Currently, there is nothing to do here.
    }
}
