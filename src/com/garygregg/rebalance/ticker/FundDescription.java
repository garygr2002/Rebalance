package com.garygregg.rebalance.ticker;

import org.jetbrains.annotations.NotNull;

public class FundDescription extends TickerDescription {

    /**
     * Constructs the fund description with a default minimum investment.
     *
     * @param ticker The ticker tag
     * @param number The number
     * @param name   The name
     */
    FundDescription(@NotNull String ticker, @NotNull Integer number,
                    @NotNull String name) {
        super(ticker, number, name);
    }

    /**
     * Constructs the fund description with a default balance rounding.
     *
     * @param ticker  The ticker tag
     * @param number  The number
     * @param name    The name
     * @param minimum The minimum investment in the fund
     */
    FundDescription(@NotNull String ticker, @NotNull Integer number,
                    @NotNull String name, double minimum) {
        super(ticker, number, name, minimum);
    }

    /**
     * Constructs the fund description.
     *
     * @param ticker          The ticker tag
     * @param number          The number
     * @param name            The name
     * @param minimum         The minimum investment in the ticker
     * @param balanceRounding The preferred round number of shares to hold
     */
    FundDescription(@NotNull String ticker, Integer number, String name,
                    double minimum, double balanceRounding) {
        super(ticker, number, name, minimum, balanceRounding);
    }
}
