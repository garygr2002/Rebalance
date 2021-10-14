package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.WeightType;
import com.garygregg.rebalance.account.AccountDescription;
import com.garygregg.rebalance.hierarchy.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class Rebalancer implements Ticker.WeightEnumerator {

    // A rebalancer instance
    private static final Rebalancer instance = new Rebalancer();

    // The root rebalance node
    private final RebalanceNode root = new RebalanceNode(WeightType.ALL, 1.);

    // The current rebalance node
    private RebalanceNode currentNode;

    // The current ticker
    private Ticker currentTicker;

    // A rebalance action for accounts
    private final Action<Hierarchy, Account> accountAction =
            new Action<>() {

                @Override
                public @NotNull Collection<Account> getChildren(
                        @NotNull Hierarchy hierarchy) {
                    return createAccountList(hierarchy.getAccounts());
                }

                @Override
                public boolean perform(@NotNull Account child) {
                    return rebalance(child);
                }
            };

    // A rebalance action for an institution
    private final Action<Institution, Account> institutionAction =
            new Action<>() {

                @Override
                public @NotNull Collection<Account> getChildren(
                        @NotNull Institution institution) {
                    return createAccountList(institution.getChildren());
                }

                @Override
                public boolean perform(@NotNull Account child) {
                    return rebalance(child);
                }
            };

    // A rebalance action for a portfolio
    private final Action<Portfolio, Institution> portfolioAction =
            new Action<>() {

                @Override
                public @NotNull Collection<Institution> getChildren(
                        @NotNull Portfolio portfolio) {
                    return portfolio.getChildren();
                }

                @Override
                public boolean perform(@NotNull Institution child) {
                    return rebalance(child);
                }
            };

    // A rebalance action for a hierarchy
    private final Action<Hierarchy, Portfolio> hierarchyAction =
            new Action<>() {

                @Override
                public @NotNull Collection<Portfolio> getChildren(
                        @NotNull Hierarchy hierarchy) {
                    return hierarchy.getPortfolios();
                }

                @Override
                public boolean perform(@NotNull Portfolio child) {
                    return rebalance(child);
                }
            };

    /**
     * Constructs a rebalancer.
     */
    private Rebalancer() {
        initialize();
    }

    /**
     * Creates an account list from an account collection.
     *
     * @param collection A collection of accounts.
     */
    private static @NotNull List<Account> createAccountList(
            @NotNull Collection<Account> collection) {

        /*
         * Load the collection of accounts into a list. Sort the list by
         * rebalance order. Return the sorted list of accounts.
         */
        final List<Account> accounts = new ArrayList<>(collection);
        accounts.sort(Comparator.comparingLong(Rebalancer::getRebalanceOrder));
        return accounts;
    }

    /**
     * Gets an instance of the rebalancer.
     *
     * @return An instance of the rebalancer
     */
    public static @NotNull Rebalancer getInstance() {
        return instance;
    }

    /**
     * Gets the rebalance order of an account.
     *
     * @param account The account
     * @return The rebalance order of the account
     */
    private static long getRebalanceOrder(@NotNull Account account) {

        /*
         * Get the description of the account. Return the minimum value if the
         * description is null. Otherwise, return the rebalance order from the
         * description.
         */
        final AccountDescription description = account.getDescription();
        return (null == description) ? Long.MIN_VALUE :
                description.getRebalanceOrder();
    }

    /**
     * Performs an action on the children of a parent.
     *
     * @param parent       The parent
     * @param action       The action to perform
     * @param <ParentType> The type of the parent
     * @param <ChildType>  The type of the child
     * @return True if the action succeeded; false otherwise
     */
    private static <ParentType, ChildType> boolean perform(
            @NotNull ParentType parent,
            @NotNull Action<ParentType, ChildType> action) {

        // Declare and initialize the result. Cycle for each child.
        boolean result = true;
        for (ChildType child : action.getChildren(parent)) {

            // Perform the action and re-initialize the result.
            result = action.perform(child) && result;
        }

        // Return the result.
        return result;
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

    /**
     * Initializes member variables.
     */
    private void initialize() {

        // Clear the current node and current ticker.
        currentNode = null;
        currentTicker = null;
    }

    /**
     * Rebalances each institution in a portfolio
     *
     * @param portfolio The portfolio to rebalance
     * @return True if each institution was successfully rebalanced; false
     * otherwise
     */
    private boolean rebalance(@NotNull Portfolio portfolio) {
        return perform(portfolio, portfolioAction);
    }

    /**
     * Rebalances each account in an institution.
     *
     * @param institution The institution to rebalance
     * @return True if each account was successfully rebalanced; false
     * otherwise
     */
    private boolean rebalance(@NotNull Institution institution) {
        return perform(institution, institutionAction);
    }

    /**
     * Rebalances an account.
     *
     * @param account The account to rebalance
     * @return True if the account was successfully rebalanced; false
     * otherwise
     */
    private boolean rebalance(@NotNull Account account) {

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
         * Reinitialize the rebalancer member variables. TODO:
         *
         * Pass...
         */
        initialize();
        return true;
    }

    /**
     * Rebalances by account each portfolio in a hierarchy.
     *
     * @param hierarchy The hierarchy to rebalance
     * @return True if each portfolio was successfully rebalanced; false
     * otherwise
     */
    public boolean rebalanceByAccount(@NotNull Hierarchy hierarchy) {
        return perform(hierarchy, accountAction);
    }

    /**
     * Rebalances by account each portfolio in a default hierarchy.
     *
     * @return True if each portfolio was successfully rebalanced; false
     * otherwise
     */
    public boolean rebalanceByAccount() {
        return this.rebalanceByAccount(Hierarchy.getInstance());
    }

    /**
     * Rebalances by institution each portfolio in a hierarchy.
     *
     * @param hierarchy The hierarchy to rebalance
     * @return True if each portfolio was successfully rebalanced; false
     * otherwise
     */
    public boolean rebalanceByInstitution(@NotNull Hierarchy hierarchy) {
        return perform(hierarchy, hierarchyAction);
    }

    /**
     * Rebalances by institution each portfolio in a default hierarchy.
     *
     * @return True if each portfolio was successfully rebalanced; false
     * otherwise
     */
    @SuppressWarnings("unused")
    public boolean rebalanceByInstitution() {
        return this.rebalanceByInstitution(Hierarchy.getInstance());
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

    private interface Action<ParentType, ChildType> {

        /**
         * Gets the children of the parent.
         *
         * @param parent The container
         * @return The children of the parent
         */
        @NotNull Collection<ChildType> getChildren(
                @NotNull ParentType parent);

        /**
         * Performs an action on a child.
         *
         * @param child The child
         * @return True if the action was successful; false otherwise
         */
        boolean perform(@NotNull ChildType child);
    }
}
