package com.garygregg.rebalance.ticker;

import com.garygregg.rebalance.Description;
import com.garygregg.rebalance.FundType;
import com.garygregg.rebalance.countable.Currency;
import com.garygregg.rebalance.countable.Shares;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.TreeSet;

public class TickerDescription implements Description<String> {

    // The default balance rounding
    private final Shares balanceRounding;

    // The minimum investment in the ticker
    private final Currency minimum;

    // The name
    private final String name;

    // The number
    private final Integer number;

    // The ticker
    private final String ticker;

    // A set of types for the ticker
    private final Set<FundType> types = new TreeSet<>();

    /**
     * Constructs the ticker description with a default minimum investment.
     *
     * @param ticker The ticker tag
     * @param number The number
     * @param name   The name
     */
    TickerDescription(@NotNull String ticker, Integer number, String name) {
        this(ticker, number, name, 0.);
    }

    /**
     * Constructs the ticker description with a default balance rounding.
     *
     * @param ticker  The ticker tag
     * @param number  The number
     * @param name    The name
     * @param minimum The minimum investment in the ticker
     */
    TickerDescription(@NotNull String ticker, Integer number, String name,
                      double minimum) {
        this(ticker, number, name, minimum, 0.001);
    }

    /**
     * Constructs the ticker description.
     *
     * @param ticker          The ticker tag
     * @param number          The number
     * @param name            The name
     * @param minimum         The minimum investment in the ticker
     * @param balanceRounding The preferred round number of shares to hold
     */
    TickerDescription(@NotNull String ticker, Integer number, String name,
                      double minimum, double balanceRounding) {

        // Set the member variables.
        this.balanceRounding = new Shares(balanceRounding);
        this.minimum = new Currency(minimum);
        this.name = name;
        this.number = number;
        this.ticker = ticker;
    }

    /**
     * Adds implied supertypes to the ticker description.
     *
     * @param type A fund subtype
     */
    @SuppressWarnings("unused")
    private void addSuperTypes(@NotNull FundType type) {

        // Switch on the supertype.
        switch (type) {

            /*
             * Add the bond type for the corporate, inflation-adjusted,
             * mortgage, short, and treasury types.
             */
            case CORPORATE:
            case INFLATION:
            case MORTGAGE:
            case SHORT:
            case TREASURY:
                types.add(FundType.BOND);
                break;

            /*
             * Add the stock type for growth, growth-and-value, large,
             * medium, not-large, small, and value types.
             */
            case GROWTH:
            case GROWTH_AND_VALUE:
            case LARGE:
            case MEDIUM:
            case NOT_LARGE:
            case SMALL:
            case VALUE:
                types.add(FundType.STOCK);
                break;

            /*
             * Add the bond and corporate types for the high-yield type.
             */
            case HIGH:
                types.add(FundType.BOND);
                types.add(FundType.CORPORATE);
                break;
        }
    }

    /**
     * Adds a type to the fund or ETF description.
     *
     * @param type The type to add to the fund or ETF description
     */
    protected void addType(@NotNull FundType type) {

        /*
         * Add the given type. For now, do not add implied supertypes, but
         * keep the code around.
         */
        types.add(type);
        // addSuperTypes(type);
    }

    /**
     * Gets the preferred balance rounding.
     *
     * @return The preferred balance rounding
     */
    public Shares getBalanceRounding() {
        return balanceRounding;
    }

    @Override
    public @NotNull String getKey() {
        return getTicker();
    }

    /**
     * Gets the fund or ETF minimum investment.
     *
     * @return The fund or ETF minimum investment
     */
    public Currency getMinimum() {
        return minimum;
    }

    /**
     * Gets the name of the ticker.
     *
     * @return The name of the ticker
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the number of the ticker.
     *
     * @return The number of the ticker
     */
    public Integer getNumber() {
        return number;
    }

    /**
     * Gets the ticker.
     *
     * @return The ticker
     */
    public String getTicker() {
        return ticker;
    }

    /**
     * Determine if the fund or ETF has a given type.
     *
     * @param type The type in question
     * @return True if the fund or ETF has the given type, false otherwise
     */
    public boolean hasType(FundType type) {
        return types.contains(type);
    }

    /**
     * Returns true if the ticker description is considered, false otherwise.
     *
     * @return True if the ticker description is considered, false otherwise
     */
    public boolean isConsidered() {
        return true;
    }

    /**
     * Determines whether this ticker is a money fund.
     *
     * @return True if the ticker is a money fund, false otherwise
     */
    public boolean isMoneyFund() {

        /*
         * Money funds must have a cash fund type. Declare and initialize
         * the result thusly. Does the ticker have a cash fund type?
         */
        boolean result = hasType(FundType.CASH);
        if (result) {

            /*
             * The ticker has the cash fund type. Get the preferred balance
             * rounding. The balance rounding must not be null, and must be
             * the minimum. Re-initialize the result of our test thusly.
             */
            final Shares balanceRounding = getBalanceRounding();
            result = (null != balanceRounding) &&
                    balanceRounding.equals(Shares.getMinimum());
        }

        // Return the result.
        return result;
    }
}
