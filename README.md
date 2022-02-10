# Rebalance
A Java Project to Rebalance Portfolios

The Rebalance project is one that I undertook beginning in February 2021. This project is a Java-based, command-line software tool that rebalances the holdings of
one or more investors. The tool is currently driven by comma-separated-value files (csv's). The tool will rebalance the portfolio holdings of each investor across
institutions, accounts, mutual funds, ETFs and stocks based on the declared preferences of each investor. Allowances are made to automatically adjust the declared
preferences according to market activity - based on the valuation of the S&P 500, as an index - on the day a rebalance is undertaken (this versus valuations taken
at last market close). As well, optional adjustments may be made to adjust allocations according to the current market level (again, measured by the value of the
S&P 500) on the day of a rebalance (this versus the absolute high for the index). My goal for this project is to allocate approximately one man-month of time to
fully document the tool, including design considerations & description, code download, build, data setup, and use. At the conclusion of this activity (scheduled
for 1 March 2022), I will consider the project complete. At that time I will fully describe my work on this project - as a professional accomplishment - for my
LinkedIn profile. The tool should be general enough for anyone who wants to use it.

