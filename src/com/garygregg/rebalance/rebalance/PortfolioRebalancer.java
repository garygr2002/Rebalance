package com.garygregg.rebalance.rebalance;

import com.garygregg.rebalance.account.AccountDescription;
import com.garygregg.rebalance.distinguished.DistinguishedAccount;
import com.garygregg.rebalance.distinguished.DistinguishedAccountLibrary;
import com.garygregg.rebalance.hierarchy.Account;
import com.garygregg.rebalance.hierarchy.Hierarchy;
import com.garygregg.rebalance.hierarchy.Institution;
import com.garygregg.rebalance.hierarchy.Portfolio;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@SuppressWarnings("EmptyClassInitializer")
public class PortfolioRebalancer extends Rebalancer {

    // The default rebalancer
    private static final AccountRebalancer defaultRebalancer =
            new PassThroughRebalancer();

    // A rebalancer instance
    private static final PortfolioRebalancer instance =
            new PortfolioRebalancer();

    // The rebalancer for an account that is last
    private static final AccountRebalancer lastRebalancer = defaultRebalancer;

    // The rebalancer for an account that is not last
    private static final AccountRebalancer notLastRebalancer = defaultRebalancer;

    /*
     * A map of distinguished accounts to account rebalancers
     *
     * TODO: Delete this.
     */
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static final Map<DistinguishedAccount, AccountRebalancer> rebalancerMap =
            new HashMap<>();

    static {

        /*
         * TODO: Put test rebalancers in this static block...
         *
         * ...but delete the block later, as well as all the other test code.
         */
    }

    // A rebalance action for accounts
    private final Action<Hierarchy, Account> accountAction =
            new Action<>() {

                @Override
                public @NotNull Collection<Account> getChildren(
                        @NotNull Hierarchy hierarchy) {
                    return createAccountList(hierarchy.getAccounts());
                }

                @Override
                public boolean perform(@NotNull Account child,
                                       boolean isLast) {
                    return rebalance(child, isLast);
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
                public boolean perform(@NotNull Account child,
                                       boolean isLast) {
                    return rebalance(child, isLast);
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
                public boolean perform(@NotNull Institution child,
                                       boolean isLast) {
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
                public boolean perform(@NotNull Portfolio child,
                                       boolean isLast) {
                    return rebalance(child);
                }
            };

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
     * @param isLast  True if this is the last child; false otherwise
     * @return True if the account was successfully rebalanced; false
     * otherwise
     */
    private boolean rebalance(@NotNull Account account, boolean isLast) {

        /*
         * Try to get an explicit rebalancer from the rebalancer map.
         *
         * TODO: Remove this logic when testing is complete.
         */
        AccountRebalancer rebalancer = rebalancerMap.get(
                DistinguishedAccountLibrary.getInstance().getKey(
                        account.getKey()));

        /*
         * Use either the default 'last' rebalancer or the default 'not last'
         * rebalancer if there is no explicit rebalancer.
         */
        if (null == rebalancer) {
            rebalancer = isLast ? lastRebalancer : notLastRebalancer;
        }


        // Rebalance the account with the designated account rebalancer.
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
        return perform(hierarchy, accountAction);
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
