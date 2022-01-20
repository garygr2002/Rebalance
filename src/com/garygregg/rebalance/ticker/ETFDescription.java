package com.garygregg.rebalance.ticker;

import org.jetbrains.annotations.NotNull;

public class ETFDescription extends TickerDescription {

    /**
     * Constructs the ETF description.
     *
     * @param ticker          The ticker tag
     * @param number          The number
     * @param name            The name
     * @param minimum         The minimum investment in the ticker
     * @param balanceRounding The preferred round number of shares to hold
     */
    ETFDescription(@NotNull String ticker, Integer number, String name,
                    double minimum, double balanceRounding) {
        super(ticker, number, name, minimum, balanceRounding);
    }

    @Override
    public boolean isMoneyFund() {

        // ETFs are not money funds.
        return false;
    }
}
