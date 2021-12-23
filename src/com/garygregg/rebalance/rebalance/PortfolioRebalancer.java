package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.BreakdownType;
import com.garygregg.rebalance.account.AccountDescription;
import com.garygregg.rebalance.hierarchy.Account;
import com.garygregg.rebalance.hierarchy.Hierarchy;
import com.garygregg.rebalance.hierarchy.Institution;
import com.garygregg.rebalance.hierarchy.Portfolio;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PortfolioRebalancer extends Rebalancer {

    // A rebalancer instance
    private static final PortfolioRebalancer instance =
            new PortfolioRebalancer();

    // The rebalancer for an account that is last
    private static final AccountRebalancer lastRebalancer =
            new ClosureRebalancer();

    // The rebalancer for an account that is not last
    private static final AccountRebalancer notLastRebalancer =
            new WeightRebalancer();

    // A rebalance action for accounts
    private final Action<Hierarchy, Account> accountAction =
            new Action<>() {

                @Override
                public @NotNull Collection<Account> doGetChildren(
                        @NotNull Hierarchy hierarchy) {
                    return createAccountList(hierarchy.getAccounts());
                }

                @Override
                public boolean perform(@NotNull Account child,
                                       boolean isLast) {
                    return rebalance(child, isLastAccount(child));
                }
            };

    // A rebalance action for an institution
    private final Action<Institution, Account> institutionAction =
            new Action<>() {

                @Override
                public @NotNull Collection<Account> doGetChildren(
                        @NotNull Institution institution) {
                    return createAccountList(institution.getChildren());
                }

                @Override
                public boolean perform(@NotNull Account child,
                                       boolean isLast) {
                    return rebalance(child, isLastAccount(child));
                }
            };

    // A set of last accounts
    private final Set<Account> lastAccounts = new HashSet<>();

    // A rebalance action for a portfolio
    private final Action<Portfolio, Institution> portfolioAction =
            new Action<>() {

                @Override
                public @NotNull Collection<Institution> doGetChildren(
                        @NotNull Portfolio portfolio) {
                    return portfolio.getChildren();
                }

                @Override
                public void onComplete() {
                    breakdownPortfolio(getParent());
                }

                @Override
                public boolean perform(@NotNull Institution child,
                                       boolean isLast) {
                    return rebalance(child);
                }
            };

    // A rebalance action for a hierarchy
    private final Action<Hierarchy, Portfolio> hierarchyAction =
            new Action<>() {

                @Override
                public @NotNull Collection<Portfolio> doGetChildren(
                        @NotNull Hierarchy hierarchy) {
                    return hierarchy.getPortfolios();
                }

                @Override
                public boolean perform(@NotNull Portfolio child,
                                       boolean isLast) {
                    return rebalance(child);
                }
            };

    /**
     * Breaks down a portfolio by proposed values.
     *
     * @param portfolio A portfolio to break down
     */
    private static void breakdownPortfolio(Portfolio portfolio) {

        // Is the portfolio not null?
        if (null != portfolio) {

            /*
             * The portfolio is not null. Break down the portfolio according to
             * proposed valuations.
             */
            portfolio.breakdown(BreakdownType.PROPOSED);
        }
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
        accounts.sort(Comparator.comparingLong(
                PortfolioRebalancer::getRebalanceOrder));
        return accounts;
    }

    /**
     * Gets an instance of the rebalancer.
     *
     * @return An instance of the rebalancer
     */
    public static @NotNull PortfolioRebalancer getInstance() {
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
     * Adds an account to the last accounts set.
     *
     * @param portfolio A portfolio containing the last account
     * @return True if the last account of the portfolio was successfully
     * added to the last account set; false otherwise
     */
    private boolean addLastAccount(@NotNull Portfolio portfolio) {

        /*
         * Get the last account from the portfolio, and add it to the last
         * accounts set.
         */
        final Account last = portfolio.getLast();
        return (null != last) && lastAccounts.add(last);
    }

    /**
     * Adds last accounts to the last accounts set.
     *
     * @param hierarchy A hierarchy
     * @return True if last accounts were added to the last accounts set for
     * each portfolio in the hierarchy
     */
    @SuppressWarnings("UnusedReturnValue")
    private boolean addLastAccounts(@NotNull Hierarchy hierarchy) {

        /*
         * Declare and initialize the return value. Clear all last accounts in
         * the last accounts set. Cycle for each portfolio in the hierarchy.
         */
        boolean result = true;
        clearLastAccounts();
        for (Portfolio portfolio : hierarchy.getPortfolios()) {

            /*
             * Add the last account of the portfolio, and maintain the return
             * value.
             */
            result = addLastAccount(portfolio) && result;
        }

        // Return the result.
        return result;
    }

    /**
     * Clears the last accounts set.
     */
    private void clearLastAccounts() {
        lastAccounts.clear();
    }

    /**
     * Determines if an account is the last account in its portfolio.
     *
     * @param account An account
     * @return True if the account is the last account in its portfolio; false
     * otherwise
     */
    private boolean isLastAccount(@NotNull Account account) {
        return lastAccounts.contains(account);
    }

    /**
     * Rebalances each institution in a portfolio
     *
     * @param portfolio The portfolio to rebalance
     * @return True if each institution was successfully rebalanced; false
     * otherwise
     */
    private boolean rebalance(@NotNull Portfolio portfolio) {

        /*
         * Set the current portfolio. Perform the portfolio rebalance action,
         * and receive a result.
         */
        setCurrent(portfolio);
        final boolean result = perform(portfolio, portfolioAction);

        // Clear the current portfolio and return the result.
        setCurrent(null);
        return result;
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
     * @param isLast  True if this is the last child; false otherwise
     * @return True if the account was successfully rebalanced; false
     * otherwise
     */
    private boolean rebalance(@NotNull Account account, boolean isLast) {

        // Determine which rebalancer to use, and rebalance the account.
        final AccountRebalancer rebalancer = isLast ? lastRebalancer :
                notLastRebalancer;
        return rebalancer.rebalance(account);
    }

    /**
     * Rebalances by account each portfolio in a hierarchy.
     *
     * @param hierarchy The hierarchy to rebalance
     * @return True if each portfolio was successfully rebalanced; false
     * otherwise
     */
    public boolean rebalanceByAccount(@NotNull Hierarchy hierarchy) {

        /*
         * Add last accounts for each portfolio in the hierarchy. Perform the
         * account rebalance action, receiving a result.
         */
        addLastAccounts(hierarchy);
        final boolean result = perform(hierarchy, accountAction);

        // Break down each portfolio in the hierarchy.
        for (Portfolio portfolio : hierarchy.getPortfolios()) {
            breakdownPortfolio(portfolio);
        }

        // Return the result of the rebalance action.
        return result;
    }

    /**
     * Rebalances by account each portfolio in a default hierarchy.
     *
     * @return True if each portfolio was successfully rebalanced; false
     * otherwise
     */
    @SuppressWarnings("unused")
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

        /*
         * Add last accounts for each portfolio in the hierarchy. Perform the
         * hierarchy rebalance action.
         */
        addLastAccounts(hierarchy);
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
}
