package com.garygregg.rebalance.ticker;

import org.jetbrains.annotations.NotNull;

public class StockDescription extends TickerDescription {

    /**
     * Constructs the stock description with a default minimum investment.
     *
     * @param ticker The ticker tag
     * @param number The number
     * @param name   The name
     */
    StockDescription(@NotNull String ticker, @NotNull Integer number,
                     @NotNull String name) {
        super(ticker, number, name);
    }

    /**
     * Constructs the stock description with a default balance rounding.
     *
     * @param ticker  The ticker tag
     * @param number  The number
     * @param name    The name
     * @param minimum The minimum investment in the fund
     */
    StockDescription(@NotNull String ticker, @NotNull Integer number,
                     @NotNull String name, double minimum) {
        super(ticker, number, name, minimum);
    }

    /**
     * Constructs the stock description.
     *
     * @param ticker          The ticker tag
     * @param number          The number
     * @param name            The name
     * @param minimum         The minimum investment in the ticker
     * @param balanceRounding The preferred round number of shares to hold
     */
    StockDescription(@NotNull String ticker, Integer number, String name,
                     double minimum, double balanceRounding) {
        super(ticker, number, name, minimum, balanceRounding);
    }

    @Override
    public boolean isConsidered() {
        return false;
    }

    @Override
    public boolean isMoneyFund() {

        // Stocks are not money funds.
        return false;
    }
}
