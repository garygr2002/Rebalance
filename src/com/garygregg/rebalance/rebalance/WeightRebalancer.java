package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.WeightType;
import com.garygregg.rebalance.hierarchy.Account;
import com.garygregg.rebalance.hierarchy.Ticker;
import org.jetbrains.annotations.NotNull;

class WeightRebalancer extends AccountRebalancer
        implements Ticker.WeightEnumerator {

    // The root rebalance node
    private final RebalanceNode root = new RebalanceNode(WeightType.ALL, 1.);

    // The current rebalance node
    private RebalanceNode currentNode;

    // The current ticker
    private Ticker currentTicker;

    /**
     * Constructs an account rebalancer.
     */
    public WeightRebalancer() {
        initialize();
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
             * There is no existing child for the incoming weight type. Create
             * a new child with the weight type and weight.
             *
             * TODO: Get the weight from a table.
             */
            currentNode.addChild(node = new RebalanceNode(type, 0.));
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

        /*
         * Are we expecting a root node, and is the weight type not equal to
         * the weight type of the root?
         */
        final boolean isRoot = (root == currentNode);
        if (isRoot && !type.equals(root.getType())) {

            /*
             * We are expecting a root node, and the weight type is not equal
             * to the weight type of the root. This is a design error, so throw
             * a new runtime exception.
             */
            throw new RuntimeException(String.format("First weight " +
                    "type is not %s!", WeightType.ALL));
        }

        // Return whether the current node is the root node.
        return isRoot;
    }

    @Override
    protected boolean doRebalance(@NotNull Account account) {

        // Cycle for each ticker in the account.
        for (Ticker ticker : account.getChildren()) {

            /*
             * Clear the root node, and set the current ticker. Enumerate the
             * weight types of the ticker.
             */
            root.clear();
            currentTicker = ticker;
            ticker.enumerate(this);
        }

        /*
         * Reinitialize the rebalancer member variables.
         *
         * TODO: Rebalance the node tree.
         */
        initialize();
        return true;
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
    public void receive(@NotNull WeightType type) {

        /*
         * Get a rebalance node for the incoming weight type, and add the
         * current ticker to the node.
         */
        final RebalanceNode node = checkRoot(type) ? root :
                adjustCurrent(type);
        node.addTicker(currentTicker);
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
