# Rebalance

A Java Project to Rebalance Investment Portfolios

The Rebalance Project is one that I undertook beginning in February 2021. It is a Java-based, command-line software tool that rebalances the investment holdings of one or more investors. While doing so, it also categorizes and sums the assets of each investor. The tool will produce reports that give a complete breakdown of the holdings of the investor, and their net worth. It will also produce instructions for each investor for rebalancing his or her portfolio. 

The tool is currently driven by a series of comma-separated (CSV) files. The tool will rebalance the portfolio holdings of each investor across institutions, accounts, mutual funds, ETFs and individual securities based on declared weight preferences.

Output from the tool consists of a [Log File](#log-file) that describes errors and warnings encountered by the software. The [Log File](#log-file) will also include helpful information for the user of the software. As well, the software will produce four text files for each investor identified in its input:

1. A report of the state of the portfolio <i>before</i> proposed changes, see [Report File](#report-file)
2. A report of the state of the portfolio <i>after</i> proposed changes, see [Proposed File](#proposed-file)
3. A file that shows currency differences between proposed and current holdings, see [Difference File](#difference-file)
4. A file that describes the reallocation actions required to effect the proposed changes, see [Action File](#action-file)

The software does not affix a currency indicator to any monetary value written to an output file. The user will find no '$', '£', or '€' symbols in the output files, for example. There are three reasons for this: 1) the software assumes the currency type is uniform, specific to each investor, and that the investor knows what it is; 2) currency type is irrelevant to how this software works; 3) currency indicators tend to clutter the output files.

### Motivation

I have been interested in investing and investments for many years, and have sought throughout my professional career to save and invest diligently. During that time, I have periodically undertaken the task of rebalancing my personal investments by hand using spreadsheets and a calculator. I found this to be an imprecise, tedious, and error-prone task. 

### Why This Project?

Beginning at the end of calendar year 2020, I changed my career focus from full-time employment, and contract assignments to a long term status as a freelance software developer and data scientist. In my capacity in this role, I undertook this project for two reasons. First, I desired a mostly automated tool to undertake the chore of rebalancing my investments. Second, I desired to showcase a large, publicly accessible software project that was conceived, designed, coded and documented solely by myself.

In service of both goals, I decided to produce my own custom tool for the task of rebalancing my investments. Another option might have been to use existing commercial software, or an existing open-source project. I rejected a commercial software solution for two reasons:

* The one-time, or ongoing expense for commercial software (I am a cheapskate)
* I felt that commercial software might not be exactly what I needed, or wanted

I rejected existing open-source solutions, also for two reasons:

* Lack of complete trust in any solution I might discover (I do not blindly trust free software; sorry)
* I felt my time would be better spent professionally in developing my own software rather than in researching open-source solutions. After all, at the end of the day I could demonstrate a unique, professional accomplishment by developing my own tool

### Input

The input to the software is a series of sixteen CSV files that are coded by date as a suffix in their names (format: yyyymmdd). Sample files are located in the data directory of this project. There you will find a series of subdirectories. Some subdirectories contain one or more input files of the same type, differing by date, or one or more output files differing by date. The [Basis File](#basis-file), [Gains Files](#gains-files), [Holding File](#holding-file), [Income Files](#income-files), [Portfolio File](#portfolio-file), and the [Ticker File](#ticker-file) all contain currency fields that are assumed to be of the same type, but are nowhere assumed specific. For example, the holdings of any investor may be in U.S. dollars, British pounds, euros or any other currency, but the currency is assumed to be uniform throughout. Currency fields are so indicated in the descriptions of fields in the input files.  

These sixteen input files are:

1. [Account File](#account-file): A description of accounts unique by institution and account number
2. [Basis File](#basis-file): A hierarchy of expenses used for calculating capital gains tax
3. [Code File](#code-file): A description of single-character alphabetic codes used internally by the tool
4. [Detailed File](#detailed-file): Fine-grained weight preferences specific to account
5. [Distinguished File](#distinguished-file): Key-value pairs that map programmatically accessible keys to sensitive portfolio keys, institution names, and account numbers
6. [Gains File](#gains-files) (4 files): Current capital gains tax rates, one file for each of head-of-household, joint, separate and single filers
7. [Holding File](#holding-file): The most important file, a hierarchy of holding valuations organized by portfolios, institutions, accounts, and securities 
8. [Income Files](#income-files) (4 files): Current income tax rates, one file for each of head-of-household, joint, separate and single filers
9. [Portfolio File](#portfolio-file): A description of investors and their portfolio preferences unique by a key
10. [Ticker File](#ticker-file): A description of tickers unique by exchange-traded symbols (funds, ETFs or individual securities) that describe the type of securities held (e.g., stocks, bonds, cash or real estate)

As well as sample data files, the reader will also find in each data subdirectory a text file describing in column order the fields in the CSV files of that type. This markdown file also contains the column descriptions. The software assumes that each row in any CSV file has a unique key, and will report an error if this is not the case. This markdown file and the relevant text description file explicitly identify the key field(s). 

### Tool Set

I used the following hardware in this project:

* [Dell](https://www.dell.com/en-us) XPS 13 9300 laptop (256GB memory) with factory installed Ubuntu Linux version 18.04 at delivery
* [Dell](https://www.dell.com/en-us) Thunderbolt docking station WD19TB
* [Dell](https://www.dell.com/en-us) UltraSharp 38 curved monitor U3818DW
* [Kinesis](https://kinesis-ergo.com/) Freestyle2 ergonomic keyboard for PC
* [Anker](https://us.anker.com/) 2.4G wireless vertical ergonomic optical mouse

And I used the following software. All software versions here specified were those installed at the declaration of project complete, 31 March 2022:

* [Ubuntu Linux](https://ubuntu.com/) (version 21.10)
* [Java JDK](https://www.java.com/en/) (version 11.0.14)
* [IntelliJ IDEA Community Edition](https://www.jetbrains.com/idea/) (version 2021.3.3)
* [Ant Build Generation Plugin](https://plugins.jetbrains.com/plugin/14169-ant-build-generation) (version 203.4)
* [Apache Ant](https://ant.apache.org/)
* [GitHub](https://github.com/)
* [Cloc](http://cloc.sourceforge.net/) (version 1.82)
* [Dia](https://wiki.gnome.org/Apps/Dia/) (version 0.97.2)

### Code Metrics

As of version v1.4.1 (16 June 2022), the software consists of the following:

* 212 Java code files organized into 16 packages
* 5,164 blank lines inserted for code readability
* 12,959 comment lines written for code understandability (take a look and tell me if I succeeded)
* 13,897 Java code lines

Thanks to [Cloc](http://cloc.sourceforge.net/) for bringing us the metrics for blank, comment, and code line counts. I arrived at the package count by hand (or more precisely, eyeball) using the Project View in the [IntelliJ IDEA Community Edition](https://www.jetbrains.com/idea/).   

There is currently only one Java entry point (main method).  You will find it in <b>com.garygregg.rebalance.conductor.Conductor.java</b>. I have crafted the Java packages such that there are no dependency loops. You will find package dependency graphs in the file entitled <i>dependencies.txt</i> in the <i>data</i> directory.

I can only approximate the amount of time I have spent on this project. I am guessing an average of 20 hours per week over 60 weeks for a total of 1,200 man-hours. This project is solely my own work.

## Table of Contents

- [Installation](#installation)
- [Usage](#usage)
- [Command Line Options](#command-line-options)
- [Account File](#account-file)
- [Basis File](#basis-file)
- [Code File](#code-file)
- [Detailed File](#detailed-file)
- [Distinguished File](#distinguished-file)
- [Gains Files](#gains-files)
- [Holding File](#holding-file)
- [Income Files](#income-files)
- [Portfolio File](#portfolio-file)
- [Ticker File](#ticker-file)
- [Output Files](#output-files)
- [How Does Rebalancing Work?](#how-does-rebalancing-work)
- [What Do I Do with Debts?](#what-do-i-do-with-debts)
- [Room for Enhancement](#room-for-enhancement)
- [Credits](#credits)
- [Warranty](#warranty)
- [License](#license)

## Installation

I designed and coded the software in an [Ubuntu Linux](https://ubuntu.com/) environment using the [IntelliJ IDEA Community Edition](https://www.jetbrains.com/idea/) and [Java JDK](https://www.java.com/en/) version 11. It should be possible to install and run, or even build the software in a Windows, or macOS environment. At this time I have decided against investigating the steps required for installing the product in non-Linux environments. You will need the [Java 11 JRE](https://www.java.com/en/) to run the software.

Among the releases for this product, you will find the following files:

1. An executable jar containing the software
2. An executable file with a command to start the software
3. The source code as a zip file
4. The source code as a tar file

Download at least the executable jar, and the executable file. Ensure that the executable file has the executable permission set for at least the user of the file. To do this, open a Linux terminal and change directory to the directory containing your download. Then use the command: <pre>chmod u+x rebalance</pre> to set the executable permission. Return to your user home directory, and edit the <i>.bash_alias</i> file to include a line similar to the following:<pre>export PATH={path to your download}:$PATH</pre> and save the file. You will now need to close your terminal, and reopen a new terminal to make the rebalance command available.  

Alternatively, you may build the software locally. You will need a [Java JDK](https://www.java.com/en/) of at least version 11. In the directory containing this markdown file, you will find <i>build.xml</i> and <i>build.properties</i> files suitable for building the software using [ant](https://ant.apache.org/). The [Ant Build Generation Plugin](https://plugins.jetbrains.com/plugin/14169-ant-build-generation) (version 203.4) generated these files, and I desired to have minimal need to edit them to create an external build. As such, you will only need to edit the <i>build.properties</i> file. See below. 

Clone the repository for this project using a Linux terminal. On the command line, type: <pre>git clone https://github.com/garygr2002/Rebalance.git </pre>

If [ant](https://ant.apache.org/) is not installed in your Linux environment, you may install it using the following command: <pre>sudo apt install ant</pre>

The software also uses the jetbrains annotations jar, version 20.1.0. To build the software, you will need to acquire the annotations jar from an IntelliJ installation, or directly from the Internet. Only the jar is required, but it must be available from a relative path of <i>...org/jetbrains/annotations/20.1.0/</i> from where you install it. Call this installation directory '<i>x</i>'. Edit the <i>build.properties</i> file in this directory, and follow the instructions therein to set the <i>path.variable.maven_repository</i> variable with the path to your annotations jar, '<i>x</i>'. Set the <i>JDK.home.11</i> variable with the path to your [Java 11+ JDK](https://www.java.com/en/). You should now be ready to build the software using the [ant](https://ant.apache.org/) command from a Linux terminal in the directory containing this markdown file in your cloned repository.

## Usage

The software uses Java preferences to maintain persistent settings. Currently, settings are not user-specific. If a user has not previously set the preferences, you will see something similar to the following terminal window when running the software with the [-p](#-preference) option: 

![alt text](assets/images/preferences_reset.png)

<sub><sup>Figure 1: software preferences when rebalance software newly installed</sup></sub>

A user achieves minimum runtime settings using the [-m](#-minimum) option, which will set the logging levels and thresholds to sane values. The option will also set the known, long-term inflation percentage, and the rebalance limit for same-fund types. Finally, this option sets a default as the source for the data files. You will see something similar to the following terminal window when running the software after the [-m](#-minimum) and [-p](#-preference) options: 

![alt text](assets/images/minimum_preferences.png)

<sub><sup>Figure 2: software preferences after setting minimums for runtime</sup></sub>

You may reset the source directory for the data file using the [-s spth](#-source-spth) option. The argument to the [-s spth](#-source-spth) option must be a valid absolute path, or valid path relative to the current directory. You will see something similar to the following terminal window when running the software after the [-s spth](#-source-spth) and [-p](#-preference) options:

![alt text](assets/images/data_set.png)

<sub><sup>Figure 3: software preferences after setting source directory</sup></sub>

You may set the critical component of the destination directory for backup using the [-u link](#-use-link) option. For this option, the software assumes the home directory of the user is a prefix, and the final element of the data source directory is a suffix. You will see something similar to the following terminal window when running the software after the [-u link](#-use-link) and [-p](#-preference) options:

![alt text](assets/images/use_set.png)

<sub><sup>Figure 4: software preferences after setting default destination directory for backup with assumed name and default location</sup></sub>

You may set current and historical valuations for the Standard and Poor 500 (S&P 500) using the [-c spcl](#-close-spcl), [-h sphg](#-high-sphg) and [-t sptd](#-today-sptd) options. These options stand for 'close (last)', 'high', and 'today' respectively. The software uses these settings to automatically adjust investor-specific equity weights given in the portfolio CSV file. The software will make the adjustment for today's valuation of the S&P 500 versus last close for every portfolio. However, an equity adjustment of today's setting versus historical high is a per-portfolio preference. Read further in this document, or explore the description of the CSV files for more information. Please note that it is not required to set valuations for the S&P 500 in order for the software to run correctly. When a user has not set one or more of these preferences, the software will skip the adjustments. You will see something similar to the following terminal window when running the software after the [-c spcl](#-close-spcl), [-h sphg](#-high-sphg), [-t sptd](#-today-sptd) and [-p](#-preference) options: 

![alt text](assets/images/SandP500_set.png)

<sub><sup>Figure 5: software preferences after setting explicit values for S&P 500 last close, high, and today values</sup></sub>

A screen snap is omitted here for the [-b \[bpth\]](#-backup-bpth) option, which backs up the current source directory to the argument-specified, or current default destination directory. As well, a screen snap is omitted here for running the software with no option, which causes the software to rebalance investor portfolios given the current CSV input files in the designated source directory.

## Command Line Options

Below find an exhaustive list of command line options. As noted previously, running the software with no option causes it to read input CSV files from the designated source directory, and subdirectories. The software will use the CSV files to rebalance the indicated portfolios, and produce output. For the command line options this document lists below, any prefix of the full word positively identifies the option. A user may specify options with a single preceding hyphen such that the software accepts the very next token after any intervening whitespace as an argument. Alternatively, a user may specify with two hyphens preceding the option that an argument follows a connecting equals sign. In this case there is no intervening whitespace. Note: If the software finds more than one command line option, it evaluates the options, and will set (or show) a preference in the order that this document presents, below. In this way, it is possible for a preference setting to clobber a preference that was previously set on the same command line. This is a necessary feature, not a bug! Preferences are currently global, and not user-specific. 

### -reset

Resets all preferences to defaults. The default is null when null has a meaning for any preference.

### -minimum

Sets all preferences required for the software to run in some fashion. This assumes a default for the source directory of the data files. The software does not assume that this default directory exists. The action of this option is different from the [-s spth](#-source-spth) command line option, where the explicitly identified argument must be an absolute, or relative directory path that <b>must</b> exist.

### -preference

Lists the current settings for all the preferences.

### -level lglv

Sets the minimum level at which messages appear in the log file. Messages generated by the software below this level will not appear in the log file. The <i>lglv</i> argument may be one of : ALL, CONFIG, FINE, FINER, FINEST, INFO, OFF, SEVERE or WARNING. The software requires the argument.

### -ordinary rdnr

Sets the logging level for messages the software deems to be "ordinary". The <i>rdnr</i> argument may be one of ALL, CONFIG, FINE, FINER, FINEST, INFO or OFF. The software requires the argument.

### -extraordinary xtrd

Sets the logging level for messages the software deems to be "extraordinary". The <i>xtrd</i> argument may be one of ALL, CONFIG, FINE, FINER, FINEST, INFO or OFF. The software requires the argument.

### -inflation fltn

Sets the annual expected rate of inflation, expressed as a percentage. The <i>fltn</i> argument must be a number, possibly with a decimal point, and the software requires it. Among other possible uses, the software uses this value to compute the accumulated value of pensions that have no cost-of-living increase. For this purpose, the software assumes no inflation if this preference has not been set. 

### -high sphg

Sets the record high of the S&P 500. The <i>sphg</i> argument must be a non-negative number, possibly with a decimal point, and the software requires it. The software will use this value, if set, and the [-t sptd](#-today-sptd) option to upward-adjust the percentage of a portfolio allocated to equity investments. It will only do this for portfolios that so specify in the [Portfolio File](#portfolio-file).

### -close spcl

Sets the last close of the S&P 500. The <i>spcl</i> argument must be a non-negative number, possibly with a decimal point, and the software requires it. The software will use this value, if set, and the value of the [-t sptd](#-today-sptd) option to adjust the percentage of equity investment allocations in all portfolios. To skip this adjustment, the user may leave this preference unset.

### -today sptd

Sets the value of the S&P 500 on the day the software is run. The <i>sptd</i> argument must be a non-negative number, possibly with a decimal point, and the software requires it. The software will use this value, if set, to perform adjustments to the equity investment allocations in all portfolios. It does this in conjunction with the [-h sphg](#-high-sphg), and [-c spcl](#-close-spcl) preferences previously discussed. Note that if a user does not set this preference, the software will rely  on per-portfolio specifications for equity weight, and will perform no adjustments. 

### -x ncnt

The <i>ncnt</i> argument must be a non-negative integer, and the software requires it. It is the limit of reallocation attempts per account. This takes a little explanation: When a currency value is allocated to a certain weight-type of investment (e.g., small capitalization value stocks), the software may not be able to perfectly distribute the amount to the investments in that category. A "perfect" distribution is one that exactly matches - to the penny - the known weight preferences for investments of that type. If the software cannot accomplish a perfect allocation, it will attempt to redistribute the residual to a subset of the investments. It will do this for each possible subset, looking for a subset that minimizes the residual. For minimum residuals, the software will choose an allocation that minimizes the standard deviation from an allocation that perfectly matches the desired weights for the investments in that category. Make sense?  

Why would an allocation not be able to exactly match the specified weights? At the level of individual funds in the account, there are two reasons. First, the fund may have a minimum investment which may not be met with a certain weight allocation. Second, for an ETF or stock, an investment may have a requirement, or preference for some whole, or round number of shares that may not be met with a certain weight allocation. See the [Minimum Investment](#minimum-investment) and [Preferred Rounding](#preferred-rounding) fields in the [Ticker File](#ticker-file).

The thing is, checking all the subsets of an investment category in an account requires a runtime that is two to the power of the number of investments in the category. This is prohibitively expensive for even a modest number of investments. A situation where this would come into play would be when an account contains, for example, twenty small cap value stock ETFs, all with a different preferred round number of shares. You can see that it will take no small amount of runtime to examine two to the twentieth power of investment subsets.

If the count of reallocation iterations reaches this preference setting, the behavior of the software depends on whether it has already found an account reallocation that results in zero residual. If it has, the software stops trying to minimize the standard deviation from an optimal reallocation. It just accepts the closet reallocation it has thus far found. If the software has <i>not</i> found a reallocation that results in a zero residual, it will keep trying a little longer. However, what it will do is try to place any remaining residual in a maximum of two investments in the category. This should quickly result in examining the residual placement possibilities that have the best possibility of getting to zero residual. Getting to zero is a priority, but so is getting the reallocation done in a timely fashion. For more information on how rebalancing works, see [How Does Rebalancing Work](#how-does-rebalancing-work).     

### -y mxrt

<b><i>New for release v1.3.0!</i></b>

The <i>mxrt</i> argument must be a non-negative integer, and the software requires it. After some thought, I decided that there was a need for a new rebalance turning parameter to prevent the software from getting into a situation where it takes too long to complete an acceptable rebalance. Please read, and understand the use of the [-x ncnt](#-x-ncnt) command line argument. You may - in the interest of an acceptable runtime - set the <b>ncnt</b> parameter to some reasonable value, say 256. However, that limit applies to <i>each</i> weight-type node and ticker collection (review [How Does Rebalancing Work](#how-does-rebalancing-work)). What if, let us say, a rebalance node uses 256 iterations to achieve a near-optimal rebalance, and what if one of its children does the same thing? That would be a total of 2<sup>8</sup> * 2<sup>8</sup> (or 256 * 256) = 2<sup>16</sup> (or 65,536) iterations. The problem becomes compounded if multiple children use 2<sup>8</sup> iterations. Now imagine if more than two generations of weight-type nodes use this same runtime. I hope you live long enough to see your optimal rebalance report!

In what situation would you see this problem manifest itself? If you are an individual that invests across the board in exchange-traded funds (ETFs) <b><i>and</i></b> you have reported that each of these funds has a preferred round number of shares, you will likely have an issue. I devised the <b>mxrt</b> tuning parameter to deal with this. The parameter limits the depth where a weight-type node will perform more than one rebalance iteration. For example, if you set <b>mxrt</b> to zero, no node below the root will attempt more than one rebalance iteration. Only the root node will attempt it. Now the root node handles the most coarse-grained investment categorizations: stocks, bonds, cash & real-estate. If you are completely invested in exchange-traded ETFs, you will likely have a money fund (cash investment) in your brokerage account that has no required round number of shares. That is where the software will place any residual from rebalancing the holdings in your ETFs.

If you are like me, I have ETFs mixed in with open-ended mutual funds at each level of investment categorization. These open-ended funds also have no preferred round number of shares. For me, I can set <b>mxrt</b> to something higher than zero, and still have the software resolve any residuals below the root weight-type node. This prevents excessively long runtimes for the software. Here is how I would tune the software for certain situations: for portfolios where ETFs are concentrated in one category (let us say, small cap value), I would limit the [-x ncnt](#-x-ncnt) parameter. But for situations where ETFs are distributed across categories, <i>and</i> there is no investment in the same categories that can receive a residual (like an open-ended fund), I would limit <b>mxrt</b>. Know that setting both <b>ncnt</b> and <b>mxrt</b> to high values lets you acquire a more precise investment rebalance, <b>if</b> you can afford the runtime. If you will grow old waiting for an answer, try limiting one parameter, or the other.    

Since this command-line parameter is new for release v1.3.0, figures 1 through 5 do not show a value for 'y'. I do not have the inclination to take new screen snaps to fix this. 

### -source spth

Sets the source directory for the CSV files that this software reads. The source directory must contain the following subdirectories: account, basis, code, detailed, distinguished, gains_head, gains_joint, gains_separate, gains_single, holding, income_head, income_joint, income_separate, income_single, portfolio and ticker. The <i>spth</i> argument may be any valid absolute path, or valid path relative to the current directory. The software requires the argument.

### -destination dpth

Sets the default destination used by the backup command. See the [-b \[bpth\]](#-backup-bpth) option. The software requires the <i>dpth</i> argument. Note that the [-u link](#-use-link) option also sets the default destination, but modifies, and makes certain assumptions about the absolute path it constructs. 

### -use link

Sets the default destination directory to a concatenation of the home directory of the user, followed by the argument, followed by the final element of the existing source path. See the [-b \[bpth\]](#-backup-bpth) option. The software requires the <i>link</i> argument. The [-d dpth](#-destination-dpth) option also sets default destination, but it accepts its argument as an unmodified absolute or relative path.

### -backup [bpth]

Backs up the files in the source path. The <i>bpth</i> argument is optional. If not given, the backup command uses the existing destination path preference.

### -assistance

Displays the command line usage text.

## Account File

The account file is one of sixteen CSV files that act as input to the software. Files in this format are located in a subdirectory named "account" located in the directory identified in the source preference. Files of this type have the prefix "account_" followed by a date designation in the format "yyyymmdd", and a file type of ".csv". When run with no command line options, the software will read, and use the account file that has the latest date that is not later than the date of the latest [Holding File](#holding-file). The account file contains information that is required to identify, characterize, and rebalance accounts. The key for rows in the account file is a concatenation of [Account Institution](#account-institution) and [Account Number](#account-number), and the combination of the two should be unique. The following are the fields of an account file row. 

### Account Institution

The account institution mnemonic begins in column 1, and may be up to 12 characters long. Its content is not constrained as long as it is not blank. The institution mnemonic identifies the institution where the account is held.

### Account Number

The account number begins in column 14, and may be up to 16 characters long. Its content is constrained to a positive integer. The account number uniquely identifies an account within an institution. 

### Rebalance Order

The rebalance order begins at column 31, and may be up to 8 characters long. Its content is constrained to a non-negative integer. The rebalance order is the order in which the software will rebalance the account within a portfolio. I suggest that the rebalance order be a unique integer, but this is not required. If the specified rebalance order is not unique, then the software will rebalance same-portfolio accounts in the order in which they are specified in the [Account File](#account-file).  

### Account Name

The account name begins at column 40, and may be up to 42 characters long. Its content is not constrained. The account name is for visual identification purposes, and I suggest it be unique. However, uniqueness is not required. The account name should match the [Holding Name](#holding-name) in the [Holding File](#holding-file) for rows that reference it. The [Holding File](#holding-file) is where the account receives its value.

### Tax Type

The tax type begins at column 83, and may be up to 14 characters long. Its content is constrained to one of the following strings: "Credit", "HSA", "Inherited_IRA", "Non_Roth_401k", "Non_Roth_Annuity", "Non_Roth_IRA", "Pension", "Real_Estate", "Roth_401k", "Roth_Annuity", "Roth_IRA", or "Taxable". Case is not important. The tax type categorizes the tax status of the account. 

### Rebalance Procedure

The rebalance procedure begins at column 98, and may be up to 14 characters long. Its content is constrained to one of the following strings: "Percent" or "Redistribute". Case is not important. The rebalance procedure is used to report what actions to take to rebalance the account. For percent accounts, the software will report percent allocations to funds. For redistribute accounts, the software will report specific currency amounts to shift between funds.

### Account Weight Stock

The weight stock begins at column 113, and may be up to 6 characters long. Its content is constrained to a non-negative number, possibly with a decimal point. Although conveniently specified as a percent, the value is actually a weight assigned to equity (or stock) investments. If this field, and that for bonds, cash and real estate sum to 100, then the specified weight is actually a percent assigned to stocks.

### Account Weight Bond

The weight bond begins at column 120, and may be up to 6 characters long. Its content is constrained to a non-negative number, possibly with a decimal point. Although conveniently specified as a percent, the value is actually a weight assigned to bond investments. If this field, and that for stocks, cash and real estate sum to 100, then the specified weight is actually a percent assigned to bonds.

### Account Weight Cash

The weight cash begins at column 127, and may be up to 6 characters long. Its content is constrained to a non-negative number, possibly with a decimal point. Although conveniently specified as a percent, the value is actually a weight assigned to cash investments (e.g. checking or savings accounts, or CDs). If this field, and that for stocks, bonds and real estate sum to 100, then the specified weight is actually a percent assigned to cash.

### Account Weight Real-Estate

The weight real estate begins at column 134, and may be up to 6 characters long. Its content is constrained to a non-negative number, possibly with a decimal point. Although conveniently specified as a percent, the value is actually a weight assigned to real estate investments (e.g. real property or shares in a trust). If this field, and that for stocks, bonds and cash sum to 100, then the specified weight is actually a percent assigned to real estate.

### Synthesizer Type

The synthesizer type begins at column 141, and may be up to 16 characters long. The field is typically not specified for accounts where the value does not need to be synthesized. Accounts whose value may need to be synthesized include annuities, social security, or pensions. If specified, the content is constrained to one of the following strings: "Averaging", "CPI_Annuity", "Negation", "No_CPI_Annuity", or "Social_Security". Case is not important.

Monthly annuitized payments for CPI-adjusted, Non-CPI-adjusted and Social Security are given per-investor in the [Portfolio File](#portfolio-file). If the value of the account is not explicitly specified in the [Holding File](#holding-file), then the software will attempt to synthesize the value of the account using the indicated synthesizer. It will do this based on the relevant monthly payment, the life expectancy of the investor (also given in the [Portfolio File](#portfolio-file)), and the expected rate of inflation (for non-CPI adjusted annuities).

For accounts that estimate valuations of real estate, the user may create a synthesized account that is the negated sum of the estimates, plus their average. The sum of the synthesized account and all the estimates will produce a single, positive average. This valuation of this type of account is the work of an averaging synthesizer. See the [Referenced Accounts](#referenced-accounts) field.  

### Referenced Accounts

Referenced accounts begin at column 158, and maybe up to 16 characters long per referenced account. Their content is constrained to positive integers. The referenced accounts may be used by the synthesizer indicated in the [Synthesizer Type](#synthesizer-type). For example, an averaging synthesizer will need to reference the accounts it is expected to average. I have used this field to reference accounts that correspond to different valuations of real estate investments, for example home price estimates from [Redfin](https://www.redfin.com/), or [Zillow](https://www.zillow.com/). An averaging synthesizer can average these estimates.  

## Basis File

Note: The rows, fields, and consistency rules stated below are applicable to both the basis file, and the [Holding File](#holding-file). The basis file contains prices paid for tickers, and the sums of prices paid for all tickers in accounts, institutions and portfolios. The software may use the basis file for calculating capital gains tax. See [Holding File](#holding-file) for a discussion of the holding file.

The basis file is one of sixteen CSV files that act as input to the software. Files in this format are located in a subdirectory named "basis_" followed by a date designation in the format "yyyymmdd", and a file type of ".csv". When run with no command line options, the software will read, and use the basis file that has the latest date that is not later than the date of the latest [Holding File](#holding-file). The basis file contains a hierarchy of investment bases. At the highest level are portfolios, followed by institutions, followed by accounts, followed by tickers. Each row of the basis file corresponds to one of these, and is coded to indicate its type. The software assumes:

1. All ticker rows are a part of the most recently listed account row
2. All account rows are a part of the most recently listed institution row
3. All institution rows are a part of the most recently listed portfolio row

The software assumes that "orphaned" rows (tickers, accounts or institutions with no parent) are errors, and reports these as such. If a user specifies the value field for an account, he should match the sum of the ticker rows of the account. If a user specifies the value field of an institution, he should match the sum of the account rows of the institution. If a user specifies the value field of a portfolio, he should match the sum of the institution rows of the portfolio. The software will consider it an error if the value field in any row - if provided - does not match the sum of its children.

The concatenation of the foreign key of a parent row, and the foreign key of a child row in the basis file should be unique. For portfolios, the unique combination would be a distinguished value concatenated with the portfolio mnemonic. For institutions, the unique combination would be the portfolio mnemonic concatenated with an institution mnemonic. For accounts, the unique combination would be the institution mnemonic concatenated with the account number. For tickers, the unique combination would be the account key (institution mnemonic/account number combination) concatenated with the ticker symbol.

The portfolio mnemonic specified in a basis row must match a [Portfolio File](#portfolio-file) row. The institution mnemonic/account number combination must match an [Account File](#account-file) row. The ticker symbol must match a [Ticker File](#ticker-file) row. The primary key for rows in the basis file is the ordinal number of the row. The following are the fields of a basis file row.

### Basis Type

The basis type begins in column 1, and is 1 character. Its content is constrained to one of the following characters: 'A' (an account row), 'F' (a row for a fund available for rebalance), 'I' (an institution row), 'J' (a row for a fund <b><i>not</i>,</b> available for rebalance), 'P' (a portfolio row), 'Q', (a row for a single bond or stock), 'X' (a row for an exchange-trade fund). I hope the use of the basis type is self-explanatory.

### Basis Foreign

The foreign key begins in column 3, and may be 16 characters long. Its content is unconstrained as long as it references:

1. A portfolio mnemonic (see [Portfolio File](#portfolio-file)) for rows with a 'P' [Basis Type](#basis-type)
2. An institution mnemonic (see [Account File](#account-file)) for rows with an 'I' [Basis Type](#basis-type)
3. An account number (see [Account File](#account-file)) for rows with an 'A' [Basis Type](#basis-type)
4. A ticker symbol (see [Ticker File](#ticker-file)) for rows with an 'F', 'J', 'Q' or 'X' [Basis Type](#basis-type)

### Basis Name

The basis name begins in column 20, and may be 42 characters long. Its content is unconstrained, but must match the [Investor Name](#investor-name) for rows with a 'P' [Basis Type](#basis-type), an [Account Name](#account-name) for rows with an 'A' [Basis Type](#basis-type), or the [Ticker Name](#ticker-name) for rows with an 'F, 'J', 'Q', or 'X' [Basis Type](#basis-type). Institution names (rows with an 'I' [Basis Type](#basis-type)) are specified in this file or the [Holding File](#holding-file) only. The software only uses this field for readability in its reports.

### Basis Shares

The basis shares begins in column 63, and may be 18 characters long. Its content is constrained to a non-negative number, possibly with a decimal point. If left blank, the software will attempt to infer the basis shares by dividing the [Basis Value](#basis-value) by the [Basis Price](#basis-price), but only if both are specified and the [Basis Price](#basis-price) is not zero. The basis shares only has meaning for rows of the 'F', 'J', 'Q' or 'X' types, and should be left blank for 'A', 'I' and 'P' rows. If not blank for 'A', 'I' or 'P' rows, the software may use the basis shares to calculate a [Basis Value](#basis-value) that was left blank, but for no other purpose.

### Basis Price

The basis price begins in column 82, and may be 18 characters long. Its content is constrained to a number, possibly with a decimal point. If left blank, the software will attempt to infer the basis price by dividing the [Basis Value](#basis-value) by the [Basis Shares](#basis-shares), but only if both are specified and the [Basis Shares](#basis-shares) is not zero. The basis price only has meaning for rows of the 'F', 'J', 'Q' or 'X' types, and should be left blank for 'A', 'I' and 'P' rows. If not blank for 'A', 'I' or 'P' rows, the software may use the basis price to calculate a [Basis Value](#basis-value) that was left blank, but for no other purpose. <i>Basis price is a currency field with no currency indicator.</i>

### Basis Value

The basis value begins in column 101, and may be 18 characters long. Its content is constrained to a number, possibly with a decimal point. If left blank, the software will attempt to infer the basis value by multiplying the [Basis Shares](#basis-shares) by the [Basis Price](#basis-price), but only if both are specified. <i>Basis value is a currency field with no currency indicator.</i> If specified, the basis value must:

1. For 'A' rows, match the sum of all 'F', 'J', 'Q' and 'X' rows given as children
2. For 'I' rows, match the sum of all 'A' rows given as children
3. For 'P' rows, match the sum of all 'I' rows given as children

### Basis Weight

The basis weight begins in column 120, and may be 8 characters if specified. Its content is a non-negative number, possibly with a decimal point. For consistency with the similarly-formatted [Holding File](#holding-file), the software reads the basis weight. However, it currently uses the basis weight for no purpose.

## Code File

Unless they intend to programmatically modify the software, the code file and the [Distinguished File](#distinguished-file) are two files that a casual user will not need to edit. Borrow the example if you do not need to edit the code file.

The code file is one of sixteen CSV files that act as input to the software. Files in this format are located in a subdirectory named "code" located in the directory identified in the source preference. Files of this type have the prefix "code_" followed by a date designation in the format "yyyymmdd", and a file type of ".csv". When run with no command line options, the software will read, and use the code file that has the latest date that is not later than the date of the latest [Holding File](#holding-file). The code file contains descriptive information about the character codes used by the software. The character codes fall into one of two categories:

1. Codes that describe the holding hierarchy, namely 'A' (an account), 'F' (a fund available for rebalance), 'I' (an institution), 'J' (a fund <b><i>not</i>,</b> available for rebalance), 'P' (a portfolio), 'Q', (a single bond or stock), 'X' (an exchange-trade fund).
2. Codes that describe fund types, e.g., 'B' for bond, 'C' for cash, 'R' for real-estate, and 'S' for stock.

Currently, the needed number of codes is almost exactly equal to the number of characters in the Latin alphabet. Therefore, the software only uses uppercase Latin letters for codes. The software uses the code library for no programmatic purpose, however, it remains available for a purpose such as report output. The software overloads the meaning of the character 'P' to specify the holding hierarchy type "portfolio," and also to code a portfolio-specific investment project for funds, ETFs, bonds or stocks. The key for the rows in the code file is the [Code Character](#code-character), and it should be unique. The following are the fields of a code file row. 

### Code Character

The code character begins in column 1, and is 1 character. Its content is unconstrained, but the existing codes are currently all uppercase Latin characters.

### Code Name

The code name begin in column 3, and may be up to 22 characters long. Its content is unconstrained. The name is a readable mnemonic for the code, giving it an easily understandable meaning.

### Code Subcode 1

The first subcode begins in column 26, is 1 character. Its content is unconstrained, but should reference a related code in this code file.

### Code Subcode 2

The second subcode begins in column 28, is 1 character. Its content is unconstrained, but should reference a related code in this code file.

### Code Subcode 3

The third subcode begins in column 30, is 1 character. Its content is unconstrained, but should reference a related code in this code file.

### Code Subcode 4

The fourth subcode begins in column 32, is 1 character. Its content is unconstrained, but should reference a related code in this code file.

### Code Subcode 5

The fifth subcode begins in column 34, is 1 character. Its content is unconstrained, but should reference a related code in this code file.

### Code Description

The code description begins column 36, and may be 80 characters long. Its content is unconstrained except that it may not contain a comma, ',' (this is, after all, a CSV file). I recommend using semicolons in place of commas in the description. Actual semicolons have little use here, and they may be programmatically replaced with commas. The description contains more information about the meaning of the code than is given in its name.  

## Detailed File

The detailed file is one of sixteen CSV files that act as input to the software. Files in this format are located in a subdirectory named "detailed" located in the directory identified in the source preference. Files of this type have the prefix "detailed_" followed by a date designation in the format "yyyymmdd", and a file type of ".csv". When run with no command line options, the software will read, and use the detailed file that has the latest date that is not later than the date of the latest [Holding File](#holding-file). The detailed file contains fine-grained investment category weights for rebalancing accounts. The combination of [Detailed Institution](#detailed-institution) and [Detailed Number](#detailed-number) should match a combination of [Account Institution](#account-institution) and [Account Number](#account-number) in the [Account File](#account-file). The [Detailed Name](#detailed-name) should match the [Account Name](#account-name) in the corresponding [Account File](#account-file) row, but this is not required. The [Detailed Name](#detailed-name) is a field in this file only for fast visual identification of the account. It is not used by the software for any purpose.

The level 1 category weights specified in the detailed file - stock, bond, cash, and real-estate - replace these course-grained weights specified in the [Account File](#account-file). The user of the software may override these course-grained weights for any number of accounts, and at the same time specify finer-grained category <b>Levels 2 through 6</b> using a row in the detailed file (see [How Does Rebalancing Work](#how-does-rebalancing-work)). The user may override the weights for any number of rows: no rows, one row, or all the account rows. The software reads rows in the detailed file that match no row in the [Account File](#account-file), but ignores them. If the software seems to be ignoring course-grained weight settings in the [Account File](#account-file), the user might first check whether there exists an overriding row in the detailed file. If the software seems to be ignoring fine-grained weight settings in the detailed file, the user might first check whether the [Detailed Institution](#detailed-institution) and [Detailed Number](#detailed-number) precisely match the corresponding fields in the [Account File](#account-file). The key for rows in the detailed file is a concatenation of [Detailed Institution](#detailed-institution) and [Detailed Number](#detailed-number), and the combination of the two should be unique. The following are the fields of a detailed file row.

### Detailed Institution

The detailed institution mnemonic begins in column 1, and may be up to 12 characters long. Its content is not constrained as long as it is not blank. The institution mnemonic identifies the institution where the account is held.

### Detailed Number

The detailed account number begins in column 14, and may be up to 16 characters long. Its content is constrained to a positive integer. The account number uniquely identifies an account within an institution.

### Detailed Name

The detailed account name begins at column 31, and may be up to 42 characters long. Its content is not constrained. The account name is for visual identification purposes, and should match the corresponding field in the account file.

### Detailed Weight Stock

The stock weight begins in column 74, and may be 6 characters long. Its content is a non-negative number, possibly with a decimal point. Although conveniently specified as a percent, the value is actually a weight assigned to stocks (equities). If this field, and that for other weights in the same rebalance level sum to 100, then the specified weight is actually a percent assigned to stocks. The stock weight is a <b>Level 1</b> weight category.

### Weight Stock Domestic

The domestic stock weight begins in column 81, and may be 6 characters long. Its content is a non-negative number, possibly with a decimal point. Although conveniently specified as a percent, the value is actually a weight assigned to domestic stocks. If this field, and that for other weights in the same rebalance level sum to 100, then the specified weight is actually a percent assigned to domestic stocks. The domestic stock weight is a <b>Level 2</b> weight category under stock investments.

### Weight Stock Foreign

The foreign stock weight begins in column 88, and may be 6 characters long. Its content is a non-negative number, possibly with a decimal point. Although conveniently specified as a percent, the value is actually a weight assigned to foreign stocks. If this field, and that for other weights in the same rebalance level sum to 100, then the specified weight is actually a percent assigned to foreign stocks. The foreign stock weight is a <b>Level 2</b> weight category under stock investments.

### Weight Large

The large stock weight begins in column 95, and may be 6 characters long. Its content is a non-negative number, possibly with a decimal point. Although conveniently specified as a percent, the value is actually a weight assigned to large cap stocks. If this field, and that for other weights in the same rebalance level sum to 100, then the specified weight is actually a percent assigned to large cap stocks. The large stock weight is a <b>Level 3</b> weight category under: 1) domestic stock investments, and; 2) foreign stock investments.

### Weight Not Large

The not-large stock weight begins in column 102, and may be 6 characters long. Its content is a non-negative number, possibly with a decimal point. Although conveniently specified as a percent, the value is actually a weight assigned to medium cap and small cap stocks. If this field, and that for other weights in the same rebalance level sum to 100, then the specified weight is actually a percent assigned to not-large (medium and small cap) stocks. The not-large stock weight is a <b>Level 3</b> weight category under: 1) domestic stock investments, and; 2) foreign stock investments.

### Weight Medium

The medium stock weight begins in column 109, and may be 6 characters long. Its content is a non-negative number, possibly with a decimal point. Although conveniently specified as a percent, the value is actually a weight assigned to medium cap stocks. If this field, and that for other weights in the same rebalance level sum to 100, then the specified weight is actually a percent assigned to medium cap stocks. The medium stock weight is a <b>Level 4</b> weight category under not-large (medium and small cap) stock investments.

### Weight Small

The small stock weight begins in column 116, and may be 6 characters long. Its content is a non-negative number, possibly with a decimal point. Although conveniently specified as a percent, the value is actually a weight assigned to small cap stocks. If this field, and that for other weights in the same rebalance level sum to 100, then the specified weight is actually a percent assigned to small cap stocks. The small stock weight is a <b>Level 4</b> weight category under not-large (medium and small cap) stock investments.

### Weight Growth and Value

The growth-and-value stock weight begins in column 123, and may be 6 characters long. Its content is a non-negative number, possibly with a decimal point. Although conveniently specified as a percent, the value is actually a weight assigned to growth-and-value stocks. If this field, and that for other weights in the same rebalance level sum to 100, then the specified weight is actually a percent assigned to growth-and-value stocks. The growth-and-value stock weight is a <b>Level 4</b> weight category under: 1) large cap stock investments, and; 2) not-large cap stock investments. Alternatively, it is a <b>Level 5</b> weight category under: 1) medium cap stock investments, and; 2) small cap stock investments.

### Weight Growth or Value

The growth-or-value stock weight begins in column 130, and may be 6 characters long. Its content is a non-negative number, possibly with a decimal point. Although conveniently specified as a percent, the value is actually a weight assigned to growth-or-value (one or the other <i>only</i>) stocks. If this field, and that for other weights in the same rebalance level sum to 100, then the specified weight is actually a percent assigned to growth-or-value stocks. The growth-or-value stock weight is a <b>Level 4</b> weight category under: 1) large cap stock investments, and; 2) not-large cap stock investments. Alternatively, it is a <b>Level 5</b> weight category under: 1) medium cap stock investments, and; 2) small cap stock investments.

### Weight Growth

The growth stock weight begins in column 137, and may be 6 characters long. Its content is a non-negative number, possibly with a decimal point. Although conveniently specified as a percent, the value is actually a weight assigned to growth stocks. If this field, and that for other weights in the same rebalance level sum to 100, then the specified weight is actually a percent assigned to growth stocks. The growth stock weight is a <b>Level 5 or 6</b> weight category under growth-or-value stocks depending on the category of that parent.

### Weight Value

The value stock weight begins in column 144, and may be 6 characters long. Its content is a non-negative number, possibly with a decimal point. Although conveniently specified as a percent, the value is actually a weight assigned to value stocks. If this field, and that for other weights in the same rebalance level sum to 100, then the specified weight is actually a percent assigned to value stocks. The value stock weight is a <b>Level 5 or 6</b> weight category under growth-or-value stocks depending on the category of that parent.

### Detailed Weight Bond

The bond weight begins in column 151, and may be 6 characters long. Its content is a non-negative number, possibly with a decimal point. Although conveniently specified as a percent, the value is actually a weight assigned to bonds. If this field, and that for other weights in the same rebalance level sum to 100, then the specified weight is actually a percent assigned to bonds. The bond weight is a <b>Level 1</b> weight category.

### Weight Corporate

The corporate bond weight begins in column 158, and may be 6 characters long. Its content is a non-negative number, possibly with a decimal point. Although conveniently specified as a percent, the value is actually a weight assigned to corporate bonds only. If this field, and that for other weights in the same rebalance level sum to 100, then the specified weight is actually a percent assigned to corporate bonds. The corporate bond weight is a <b>Level 2</b> weight category under bond investments.

### Weight Bond Foreign

The foreign bond weight begins in column 165, and may be 6 characters long. Its content is a non-negative number, possibly with a decimal point. Although conveniently specified as a percent, the value is actually a weight assigned to foreign bonds only. If this field, and that for other weights in the same rebalance level sum to 100, then the specified weight is actually a percent assigned to foreign bonds. The foreign bond weight is a <b>Level 2</b> weight category under bond investments.

### Weight Bond Government

The government bond weight begins in column 172, and may be 6 characters long. Its content is a non-negative number, possibly with a decimal point. Although conveniently specified as a percent, the value is actually a weight assigned to government bonds only. If this field, and that for other weights in the same rebalance level sum to 100, then the specified weight is actually a percent assigned to government bonds. The government bond weight is a <b>Level 2</b> weight category under bond investments.

### Weight High Yield

The high-yield bond weight begins in column 179, and may be 6 characters long. Its content is a non-negative number, possibly with a decimal point. Although conveniently specified as a percent, the value is actually a weight assigned to high-yield bonds only. If this field, and that for other weights in the same rebalance level sum to 100, then the specified weight is actually a percent assigned to high-yield bonds. The high-yield bond weight is a <b>Level 2</b> weight category under bond investments.

### Weight Inflation Protected

The inflation-protected bond weight begins in column 186, and may be 6 characters long. Its content is a non-negative number, possibly with a decimal point. Although conveniently specified as a percent, the value is actually a weight assigned to inflation-protected bonds only. If this field, and that for other weights in the same rebalance level sum to 100, then the specified weight is actually a percent assigned to inflation-protected bonds. The inflation-protected bond weight is a <b>Level 2</b> weight category under bond investments.

### Weight Mortgage

The mortgage-backed bond weight begins in column 193, and may be 6 characters long. Its content is a non-negative number, possibly with a decimal point. Although conveniently specified as a percent, the value is actually a weight assigned to mortgage-backed bonds only. If this field, and that for other weights in the same rebalance level sum to 100, then the specified weight is actually a percent assigned to mortgage-backed bonds. The mortgage-backed bond weight is a <b>Level 2</b> weight category under bond investments.

### Weight Bond Short

The short-term bond weight begins in column 200, and may be 6 characters long. Its content is a non-negative number, possibly with a decimal point. Although conveniently specified as a percent, the value is actually a weight assigned to short-term bonds only. If this field, and that for other weights in the same rebalance level sum to 100, then the specified weight is actually a percent assigned to short-term bonds. The short-term bond weight is a <b>Level 2</b> weight category under bond investments.

### Weight Bond Uncategorized

The uncategorized bond weight begins in column 207, and may be 6 characters long. Its content is a non-negative number, possibly with a decimal point. Although conveniently specified as a percent, the value is actually a weight assigned to uncategorized (mixed type) bonds. If this field, and that for other weights in the same rebalance level sum to 100, then the specified weight is actually a percent assigned to uncategorized bonds. The uncategorized bond weight is a <b>Level 2</b> weight category under bond investments.

### Detailed Weight Cash

The cash weight begins in column 214, and may be 6 characters long. Its content is a non-negative number, possibly with a decimal point. Although conveniently specified as a percent, the value is actually a weight assigned to cash. If this field, and that for other weights in the same rebalance level sum to 100, then the specified weight is actually a percent assigned to cash. The cash weight is a <b>Level 1</b> weight category.

### Weight Cash Government

The government cash weight begins in column 221, and may be 6 characters long. Its content is a non-negative number, possibly with a decimal point. Although conveniently specified as a percent, the value is actually a weight assigned to government-issued cash only. If this field, and that for other weights in the same rebalance level sum to 100, then the specified weight is actually a percent assigned to government cash. The government cash weight is a <b>Level 2</b> weight category under cash investments. 

### Weight Cash Uncategorized

The uncategorized cash weight begins in column 228, and may be 6 characters long. Its content is a non-negative number, possibly with a decimal point. Although conveniently specified as a percent, the value is actually a weight assigned to uncategorized (mixed type) cash. If this field, and that for other weights in the same rebalance level sum to 100, then the specified weight is actually a percent assigned to uncategorized cash. The uncategorized cash weight is a <b>Level 2</b> weight category under cash investments.

### Detailed Weight Real-Estate

The real estate weight begins in column 235, and may be 6 characters long. Its content is a non-negative number, possibly with a decimal point. Although conveniently specified as a percent, the value is actually a weight assigned to real estate. If this field, and that for other weights in the same rebalance level sum to 100, then the specified weight is actually a percent assigned to real estate. The real estate weight is a <b>Level 1</b> weight category.

## Distinguished File

Unless they intend to programmatically modify the software, the [Code File](#code-file) and the distinguished file are two files that a casual user will not need to edit. If you do not need to edit the distinguished file, create an empty file, or borrow the empty example.

The distinguished file is one of sixteen CSV files that act as input to the software. Files in this format are located in a subdirectory named "distinguished" located in the directory identified in the source preference. Files of this type have the prefix "distinguished_" followed by a date designation in the format "yyyymmdd", and a file type of ".csv". When run with no command line options, the software will read, and use the distinguished file that has the latest date that is not later than the date of the latest [Holding File](#holding-file). The distinguished file maps well-known, non-sensitive mnemonics for portfolios, institutions and accounts to sensitive keys. The programmer/user will then be able to add code that references these mnemonics to a publicly accessible code base without concern that personally sensitive information will be compromised. The distinguished file is a hierarchy. At the highest level are portfolios, followed by institutions, followed by accounts. Each row of the distinguished file corresponds to one of these, and is coded to indicate its type. The software assumes: 

* All account rows are a part of the most recently listed institution row
* All institution rows are a part of the most recently listed portfolio row

The software assumes that "orphaned" rows (accounts or institutions with no parent) are errors, and reports these as such. Before referencing the programmatically accessible mnemonics in the distinguished file, the programmer/user will need to modify the software thusly:

* For account references, modify the code file: <b>com.garygregg.rebalance.distinguished.DistinguishedAccount.java</b>, and add the needed mnemonic
* For institution references,  modify the code file: <b>com.garygregg.rebalance.distinguished.DistinguishedInstitution.java</b>, and add the needed mnemonic
* For portfolio references,  modify the code file: <b>com.garygregg.rebalance.distinguished.DistinguishedPortfolio.java</b>, and add the needed mnemonic

Once the programmer/user accomplishes this, he may then reference the added mnemonic programmatically using the [Distinguished Key](#distinguished-key) field in a row in the distinguished file. The values for the keys will then be programmatically accessible from the <b>DistinguishedAccountLibrary</b>, <b>DistinguishedInstitutionLibrary</b>, or the <b>DistinguishedPortfolioLibrary</b>, depending on the type of [Distinguished Key](#distinguished-key) identified by the [Distinguished Type](#distinguished-type). The key for rows in the distinguished file is a concatenation of the [Distinguished Type](#distinguished-type) and [Distinguished Key](#distinguished-key), and the combination of the two should be unique. The following are the fields of a distinguished file row.

### Distinguished Type

The distinguished type begins in column 1, and is 1 character. Its content is constrained to one of the following characters: 'A' (an account row), 'I' (an institution row), or 'P' (a portfolio row). The line code distinguishes the type of row.

### Distinguished Key

The distinguished key begins in column 3, and is up to 24 characters long. Its content is constrained to one of the case-insensitive mnemonics in the <b>DistinguishedAccount</b>, <b>DistinguishedInstitution</b>, or <b>DistinguishedPortfolio</b> classes, as discussed above. The [Distinguished Type](#distinguished-type) governs the class used for the set of acceptable strings, and case is not important. 

### Distinguished Foreign

The foreign key begins in column 28, and is up to 52 characters long. Its content is constrained to the type of key required for accounts, institutions, or portfolios, depending on the [Distinguished Type](#distinguished-type). The foreign key is a sensitive value that will appear in the [Distinguished File](#distinguished-file) and referenced in other CSV files, but need not appear in any publicly accessible code base.

## Gains Files

Note: The rows, fields and consistency rules stated below are applicable to all four gains files, and the [Income Files](#income-files).

The gains files are four of sixteen CSV files that act as input to the software. Files in this format are located in the following subdirectories in the directory identified in the source preference:

1. "gains_head", for head-of-house hold filers
2. "gains_joint", for married-filing-jointly filers
3. "gains_separate", for married-filing-separately filers
4. "gains_single", for single filers

Files of this type have the prefix "gains_head_", "gains_joint_", "gains_separate_", or "gains_single_", depending on the subdirectory where they are located. The name is followed by a date designation in the format "yyyymmdd", and a file type of ".csv". When run with no command line options, the software will read, and use the gains files that have the latest dates that are not later than the date of the latest [Holding File](#holding-file). The gains files contain capital gains tax brackets for taxpayers in the various filing statuses. The key for rows in the gains files is the [Gains Threshold](#gains-threshold), and it should be unique. The following are the fields of a gains file row.

### Gains Threshold

The income threshold begins in column 1, and may be 16 characters long. Its content is constrained to a non-negative number, possibly with a decimal point. The income threshold is the minimum income where the capital [Gains Rate](#gains-rate) takes effect. <i>Gains threshold is a currency field with no currency indicator.</i>

### Gains Rate

The tax rate begins in column 18, and may be 8 characters long. Its content is constrained to a non-negative number no greater than 100, possibly with a decimal point. The rate applies to capital gains beginning at the associated [Gains Threshold](#gains-threshold).

## Holding File

Note: The rows, fields, and consistency rules stated below are applicable to both the [Basis File](#basis-file), and the holding file. The holding file contains valuations of accounts and tickers as of the date of the file. See [Basis File](#basis-file) for a discussion of the basis file.  

The holding file is one of sixteen CSV files that act as input to the software. Files in this format are located in a subdirectory named "holding_" followed by a date designation in the format "yyyymmdd", and a file type of ".csv". When run with no command line options, the software will read, and use the holding file that has the latest date. The holding file contains a hierarchy of investment valuations. At the highest level are portfolios, followed by institutions, followed by accounts, followed by tickers. Each row of the holding file corresponds to one of these, and is coded to indicated its type. The software assumes:

1. All ticker rows are a part of the most recently listed account row
2. All account rows are a part of the most recently listed institution row
3. All institution rows are a part of the most recently listed portfolio row

The software assumes that "orphaned" rows (tickers, accounts or institutions with no parent) are errors, and reports these as such. If a user specifies the value field for an account, he should match the sum of the ticker rows of the account. If a user specifies the value field of an institution, he should match the sum of the account rows of the institution. If a user specifies the value field of a portfolio, he should match the sum of the institution rows of the portfolio. The software will consider it an error if the value field in any row - if provided - does not match the sum of its children.

The concatenation of the foreign key of a parent row, and the foreign key of a child row in the holding file should be unique. For portfolios, the unique combination would be a distinguished value concatenated with the portfolio mnemonic. For institutions, the unique combination would be the portfolio mnemonic concatenated with an institution mnemonic. For accounts, the unique combination would be the institution mnemonic concatenated with the account number. For tickers, the unique combination would be the account key (institution mnemonic/account number combination) concatenated with the ticker symbol.

The portfolio mnemonic specified in a holding row must match a [Portfolio File](#portfolio-file) row. The institution mnemonic/account number combination must match an [Account File](#account-file). The ticker symbol must match a [Ticker File](#ticker-file) row. The primary key for rows in the holding file is the ordinal number of the row. The following are the fields of a holding file row.

### Holding Type

The holding type begins in column 1, and is 1 character. Its content is constrained to one of the following characters: 'A' (an account row), 'F' (a row for a fund available for rebalance), 'I' (an institution row), 'J' (a row for a fund <b><i>not</i>,</b> available for rebalance), 'P' (a portfolio row), 'Q', (a row for a single bond or stock), 'X' (a row for an exchange-trade fund). I hope the use of the holding type is self-explanatory.

### Holding Foreign

The foreign key begins in column 3, and may be 16 characters long. Its content is unconstrained as long as it references:

1. A portfolio mnemonic (see [Portfolio File](#portfolio-file)) for rows with a 'P' [Holding Type](#holding-type)
2. An institution mnemonic (see [Account File](#account-file)) for rows with an 'I' [Holding Type](#holding-type)
3. An account number (see [Account File](#account-file)) for rows with an 'A' [Holding Type](#holding-type)
4. A ticker symbol (see [Ticker File](#ticker-file)) for rows with an 'F', 'J', 'Q' or 'X' [Holding Type](#holding-type)

### Holding Name

The holding name begins in column 20, and may be 42 characters long. Its content is unconstrained, but must match the [Investor Name](#investor-name) for rows with a 'P' [Holding Type](#holding-type), an [Account Name](#account-name) for rows with an 'A' [Holding Type](#holding-type), or the [Ticker Name](#ticker-name) for rows with an 'F, 'J', 'Q', or 'X' [Holding Type](#holding-type). Institution names (rows with an 'I' [Holding Type](#holding-type)) are specified in this file or the [Basis File](#basis-file) only. The software only uses this field for readability in its reports.

### Holding Shares

The holding shares begins in column 63, and may be 18 characters long. Its content is constrained to a non-negative number, possibly with a decimal point. If left blank, the software will attempt to infer the holding shares by dividing the [Holding Value](#holding-value) by the [Holding Price](#holding-price), but only if both are specified and the [Holding Price](#holding-price) is not zero. The holding shares only has meaning for rows of the 'F', 'J', 'Q' or 'X' types, and should be left blank for 'A', 'I' and 'P' rows. If not blank for 'A', 'I' or 'P' rows, the software may use the holding shares to calculate a [Holding Value](#holding-value) that was left blank, but for no other purpose. 

### Holding Price

The holding price begins in column 82, and may be 18 characters long. Its content is constrained to a number, possibly with a decimal point. If left blank, the software will attempt to infer the holding price by dividing the [Holding Value](#holding-value) by the [Holding Shares](#holding-shares), but only if both are specified and the [Holding Shares](#holding-shares) is not zero. The holding price only has meaning for rows of the 'F', 'J', 'Q' or 'X' types, and should be left blank for 'A', 'I' and 'P' rows. If not blank for 'A', 'I' or 'P' rows, the software may use the holding price to calculate a [Holding Value](#holding-value) that was left blank, but for no other purpose. <i>Holding price is a currency field with no currency indicator.</i>

### Holding Value

The holding value begins in column 101, and may be 18 characters long. Its content is constrained to a number, possibly with a decimal point. If left blank, the software will attempt to infer the holding value by multiplying the [Holding Shares](#holding-shares) by the [Holding Price](#holding-price), but only if both are specified. <i>Holding value is a currency field with no currency indicator.</i> If specified, the holding value must:

1. For 'A' rows, match the sum of all 'F', 'J', 'Q' and 'X' rows given as children
2. For 'I' rows, match the sum of all 'A' rows given as children
3. For 'P' rows, match the sum of all 'I' rows given as children

### Holding Weight

The holding weight begins in column 120, and may be 8 characters if specified. Its content is a non-negative number, possibly with a decimal point. The holding weight is the value that the software will use when rebalancing the holding identified by the current row with other holdings of exactly the same type in the same account. The software assumes a default weight of one, meaning the software will equally distribute value to same-type holdings in the same account if the weight is not explicitly specified for any. A convenient way to withhold value from any holding during rebalance is to here set its weight to zero.

It is possible to bypass investment-type [Weight Rebalancing](#weight-rebalancer), and rely on the holding weight alone. How?

* For all but the last rebalanced account in a portfolio (see [Rebalance Order](#rebalance-order)), set to zero all weights for the account in the [Account File](#account-file), assuming there is no row for the account in the [Detailed File](#detailed-file). If there is a row for the account in the [Detailed File](#detailed-file), set to zero the <b>Level 1</b> weights in that row (only [Detailed Weight Stock](#detailed-weight-stock), [Detailed Weight Bonds](#detailed-weight-bond), [Detailed Weight Cash](#detailed-weight-cash), and [Detailed Weight Real-Estate](#detailed-weight-real-estate) need to be set)
* For the last rebalanced account in a portfolio, set to zero all weights for the portfolio in the [Portfolio File](#portfolio-file)

If the user wishes to rebalance in this way, he/she must take care that the holding weight for at least one ticker in the account is something other than zero, otherwise the software will not be able to assign the value of the account to any ticker.

## Income Files

Note: The rows, fields and consistency rules stated below are applicable to all four income files, and the [Gains Files](#gains-files).  

The income files are four of sixteen CSV files that act as input to the software. Files in this format are located in the following subdirectories in the directory identified in the source preference:

1. "income_head", for head-of-house hold filers
2. "income_joint", for married-filing-jointly filers
3. "income_separate", for married-filing-separately filers
4. "income_single", for single filers

Files of this type have the prefix "income_head_", "income_joint_", "income_separate_", or "income_single_", depending on the subdirectory where they are located. The name is followed by a date designation in the format "yyyymmdd", and a file type of ".csv". When run with no command line options, the software will read, and use the income files that have the latest dates that are not later than the date of the latest [Holding File](#holding-file). The income files contain standard deduction, and income tax brackets for taxpayers in the various filing statuses. The key for rows in the income files is the [Income Threshold](#income-threshold), and it should be unique unless the row is a distinguished standard deduction row ([Income Rate](#income-rate) equal to zero). The following are the fields of an income file row.

### Income Threshold

The income threshold begins in column 1, and may be 16 characters long. Its content is constrained to a non-negative number, possibly with a decimal point. For associated [Income Rates](#income-rate) that are positive, the income threshold is the minimum income where the [Income Rate](#income-rate) takes effect. For associated [Income Rate](#income-rate) of zero, the software interprets that income threshold as the standard deduction for a taxpayer with the indicated filing status. <i>Income threshold is a currency field with no currency indicator.</i>

### Income Rate

The tax rate begins in column 18, and may be 8 characters long. Its content is constrained to a non-negative number no greater than 100, possibly with a decimal point. The software interprets a tax rate of zero to mean that the indicated [Income Threshold](#income-threshold) is actually the standard deduction for a taxpayer in the indicated filing status. A non-zero tax rate applies to income beginning at the associated [Income Threshold](#income-threshold). 

## Portfolio File

The portfolio file is one of sixteen CSV files that act as input to the software. Files in this format are located in a directory named "portfolio" located in the directory identified in the source preference. Files of this type have the prefix "portfolio_" followed by a date designation in the format "yyyymmdd", and a file type of ".csv". When run with no command line options, the software will read, and use the portfolio file that has the latest date that is not later than the date of the latest [Holding File](#holding-file). The portfolio contains information that is required to identify, valuate and rebalance portfolios. The key for rows in the portfolio file is the [Portfolio Mnemonic](#portfolio-mnemonic), and it should uniquely identify the investor associated with the portfolio. The following are the fields of a portfolio file row.

### Portfolio Mnemonic

The portfolio mnemonic begins in column 1, and may be 16 characters long. Its content is not constrained as long as it is not blank. The portfolio mnemonic uniquely identifies the investor associated with the portfolio. 

### Investor Name

The investor name begins in column 18, and may be 20 characters long. Its content is not constrained. The software uses the investor name (rather than the investor mnemonic) in portfolio report files to make those files more readable. The investor name should match the corresponding field in the [Holding File](#holding-file). The [Holding File](#holding-file) is where the portfolio receives its value.

### Investor Birthdate

The investor birthdate begins in column 39, and is 10 characters long. Its content must be in the format yyyy-mm-dd, the year, month and day of the investor's birth. The software uses this date to determine the investor's earliest eligibility for Social Security.

### Investor Mortality Date

The investor mortality date begins in column 50, and is 10 characters long. Its content must be in the format yyyy-mm-dd, the year, month and day of the investor's estimated mortality date. I recommend using Social Security life expectancy tables and a date calculator to determine this date. The software uses this date to determine the value of annuities, pensions and Social Security.

### Filing Status

The filing status begins in column 61, and is 8 characters long. Its content is constrained to one of the following strings: "Head", "Joint", "Separate", or "Single". Case is not important. The filing status characterizes the income tax filing status of the investor. The possible values mean, respectively: head-of-household, married-filing-jointly, married-filing-separately, or single. The software uses the filing status to determine income tax and capital gains tax, as necessary. 

### Social Security

The monthly Social Security income begins in column 70, and may be up to 8 characters long. Its content is constrained to a non-negative number, possibly with a decimal point. The monthly Social Security income is either: 1) the anticipated Social Security benefit beginning at investor age 62, or; 2) the known benefit as of the current date. The software will use the latter of the two dates, as necessary. If the [Holding File](#holding-file) does not specify an explicit valuation for a Social Security account, the software will synthesize the total value of Social Security using this amount, as well as the birthdate and expected mortality date of the investor. <i>Social Security is a currency field with no currency indicator.</i>

### CPI Adjusted

The monthly CPI adjusted income begins in column 79, and may be up to 8 characters long. Its content is constrained to a non-negative number, possibly with a decimal point. The monthly CPI adjusted income is the total CPI adjusted annuity and/or pension income of the investor. The software assumes the income is current, and will continue through the lifetime of the investor. If the [Holding File](#holding-file) does not specify an explicit valuation for a CPI adjusted account, the software will synthesize the total value of the annuity/pension income using this amount and the expected mortality date of the investor. <i>CPI adjusted is a currency field with no currency indicator.</i>

### Non-CPI Adjusted

The monthly non-CPI adjusted income begins in column 88, and may be up to 8 characters long. Its content is constrained to a non-negative number, possibly with a decimal point. The monthly non-CPI adjusted income is the total non-CPI adjusted annuity and/or pension income of the investor. The software assumes the income is current, and will continue through the lifetime of the investor. If the [Holding File](#holding-file) does not specify an explicit valuation for a non-CPI adjusted account, the software will synthesize the total value of the annuity/pension income using this amount and the expected mortality date of the investor. It will also use the expected annual rate of inflation to reduce the real purchasing value of the monthly benefit over time. See the [-i fltn](#-inflation-fltn) command line option. <i>Non-CPI adjusted is a currency field with no currency indicator.</i>

### Taxable Income

The annual taxable income begins in column 97, and may be up to 10 characters long. Its content is constrained to a non-negative number, possibly with a decimal point. The software uses the annual taxable income for calculating income and capital gains tax, where necessary, and in portfolio reports. <i>Taxable income is a currency field with no currency indicator.</i>

### Portfolio Weight Stock

The weight stock begins at column 108, and may be up to 6 characters long. Its content is constrained to a non-negative number, possibly with a decimal point. Although conveniently specified as a percent, the value is actually a weight assigned to equity (or stock) investments. If this field, and that for bonds, cash and real estate sum to 100, then the specified weight is actually a percent assigned to stocks.

### Portfolio Weight Bond

The weight bond begins at column 115, and may be up to 6 characters long. Its content is constrained to a non-negative number, possibly with a decimal point. Although conveniently specified as a percent, the value is actually a weight assigned to bond investments. If this field, and that for stocks, cash and real estate sum to 100, then the specified weight is actually a percent assigned to bonds.

### Portfolio Weight Cash

The weight cash begins at column 122, and may be up to 6 characters long. Its content is constrained to a non-negative number, possibly with a decimal point. Although conveniently specified as a percent, the value is actually a weight assigned to cash investments (e.g. checking or savings accounts, or CDs). If this field, and that for stocks, bonds and real estate sum to 100, then the specified weight is actually a percent assigned to cash.

### Portfolio Weight Real-Estate

The weight real estate begins at column 129, and may be up to 6 characters long. Its content is constrained to a non-negative number, possibly with a decimal point. Although conveniently specified as a percent, the value is actually a weight assigned to real estate investments (e.g. real property or shares in a trust). If this field, and that for stocks, bonds and cash sum to 100, then the specified weight is actually a percent assigned to real estate.

### Increase at Zero

The percent increase-at-zero begins at column 136, and may be up to 6 characters long if specified. Its content is constrained to a positive number, possibly with a decimal point. This number is a percent delta, not a weight. A specification for this field indicates to the software that it should make an upward-revision of the preferred equity weight of the portfolio based on a ratio of today's value of the S&P 500 versus the high for the index. We assume by definition that the value of the S&P 500 today cannot be higher than its high. While the software will always make an equity weight adjustment for the ratio of the S&P 500 today versus its last close (assuming these preferences are both set), it will skip an additional today-versus-high adjustment on a per-portfolio basis if the S&P 500 high is not set (see the [-h sphg](#-high-sphg) command line option), or this value is not specified. Note that the user may skip all equity adjustments by simply not setting the preference for the S&P 500 today. See the [-t sptd](#-today-sptd) command line option. 

We calculate the portfolio percent stock as [Portfolio Weight Stock](#portfolio-weight-stock) divided by the sum of all level one weights. For the purposes of a discussion of a today-versus-high adjustment, the portfolio percent stock applies to the equity target the software will use when the market is at its high. If the market is off its high, the software will make an adjustment. It uses the portfolio percent stock, the percent increase-at-zero, and the [Increase at Bear](#increase-at-bear) to construct a hyperbolic curve with a decreasing slope from market high to a hypothetical market zero. Note that we say "hypothetical market zero" since stocks would presumably be worth nothing at a market valuation of zero. They thus could not make a positive contribution to the value of a portfolio. The percent increase-at-zero is a delta that is added to the portfolio percent stock at market zero, and caps the maximum percent of equities the software will attempt to target.   

The equity adjustment that the software makes with regard to the S&P 500 today versus last close is a different beast. The software will make this adjustment for all accounts in all portfolios, and the adjustment is not affected by the setting of increase-at-zero for any portfolio. The software assumes that the user has declared valuations of all accounts and portfolios in the [Holding File](#holding-file) as of S&P 500 last close. It therefore assumes - perhaps in a naive way - that the stated valuations will decrease, or increase in proportion to the change reflected in the S&P 500 today. The way the user turns off this adjustment is not to set the value of the S&P 500 last close or S&P 500 today. If the user wants to preserve the ability to adjust the portfolio versus last high, however, the only option is not to set the S&P 500 last close. Let us summarize what can be done in a table by clearing or setting the proper preference:

| User Wants                | S&P 500 Today            | S&P 500 Last Close       | S&P 500 High                         |
|---------------------------|--------------------------|--------------------------|--------------------------------------|
| No Adjustment             | Clear <b>sptd</b>        | Clear or set <b>spcl</b> | Clear or set <b>sphg</b>             |
| No Adjustment (alternate) | Clear or set <b>sptd</b> | Clear <b>spcl</b>        | Clear <b>sphg</b>                    | 
| Only Today vs Close       | Set <b>sptd</b>          | Set <b>spcl</b>          | Clear <b>sphg</b>                    |
| Only Today vs High        | Set <b>sptd</b>          | Clear <b>spcl</b>        | Set <b>sphg</b>                      |
| Both Adjustments          | Set <b>sptd</b>          | Set <b>spcl</b>          | Set <b>sphg</b>                      |

<sub><sup>Table 1: preference controls for equity weight adjustments</sup></sub>

The software comes with all S&P 500 valuation preferences clear. Currently, once the user sets any of the S&P 500 preferences, the only way to clear them is for a user to reset all the preferences with the [-r](#-reset) option. What a user can do instead is set an S&P value preference equal to one of the other preferences. For example, setting <b>spcl</b> equal <b>sptd</b> has the effect of preventing an adjustment for the current day's market action. If the user desires both equity adjustments, he should know that the software performs the today-vs-close adjustment before the today-vs-high adjustment.

### Increase at Bear

The percent increase-at-bear begins at column 143, and may be up to 6 characters long if specified. Its content is constrained to a positive number, possibly with a decimal point. The number is a percent delta, not a weight. The software uses a specification for this field in conjunction with the [Increase at Zero](#increase-at-zero) setting to construct the aforementioned hyperbolic curve. If the user specifies [Increase at Zero](#increase-at-zero) but not the percent increase-at-bear, the software will use a default percent increase-at-bear that is one half [Increase at Zero](#increase-at-zero). The percent increase-at-bear is a delta that is added to the portfolio percent stock at the bear market threshold (20% drop off market high). A sane value for the percent increase-at-bear is greater than one-fifth [Increase at Zero](#increase-at-zero). If this is not the case, the aforementioned slope from market high to market zero will not be decreasing. You may not like the result.    

## Ticker File

The ticker file is one of sixteen CSV files that act as input to the software. Files in this format are located in a directory named "ticker" located in the directory identified in the source preference. Files of this type have the prefix "ticker_" followed by a date designation in the format "yyyymmdd", and a file type of ".csv". When run with no command line options, the software will read, and use the ticker file that has the latest date that is not later than the date of the latest [Holding File](#holding-file). The ticker contains information that is required to identify and categorize investments by type. "Tickers" is a catch-all name for open-end mutual funds, exchange-traded funds, individual stocks, individual bonds, or any sub-grouping of holding that occurs within an account. The key for the rows in the file is a symbol that should uniquely identify the investment in the institution where the ticker exists, or on the exchanges where it trades. It can also be a unique mnemonic that has meaning only to the investor who owns the account. Tickers may appear in one or more accounts in the [Holding File](#holding-file) with valuations that are not the same. This would indicate different holdings in different accounts within the same, or different portfolio. To summarize, the key for rows in the ticker file is the [Ticker Symbol](#ticker-symbol), and it should be unique. The following are the fields of a ticker file row.  

### Ticker Character

The ticker character begins in column 1, and is 1 character. Its content is constrained to one of the following characters: 'F' (a fund that is available for rebalance), 'J' (a fund that is not available for rebalance), 'Q' (a single stock or bond), or 'X' (an exchange traded fund). I hope the use of the code is self-explanatory. The software assumes single stocks or bonds are not available for rebalance, but it assumes exchange-traded funds (ETFs) <b><i>are</i></b> available for rebalance.

### Ticker Symbol

The ticker symbol begins in column 3, and may be up to 5 characters long. Its content is unconstrained as long as it is not blank. The symbol uniquely identifies the ticker in the institution where it exists, or on an exchange where it trades. It can also be a unique mnemonic that has meaning only to the investor that holds it. Securities can be sub-grouped and categorized within an account using ticker symbols.

### Ticker Number

The ticker number begins in column 9, and may be up to 4 characters long. Its content is constrained to a non-negative integer. This number may uniquely identify the ticker within some institutions, but its existence and meaning is not assumed to be universal at all institutions. Assuming a ticker number does not exist, I recommend the user make up a unique ticker number to pair with the symbol. The software only uses the ticker number to help identify tickers in rebalance reports.   

### Ticker Name

The ticker name begins in column 14, and may be up to 42 characters long. Its content is not constrained. The ticker name helps identify the ticker in rebalance reports. I recommend that the name be unique, and describe the ticker in an identifiable way. The ticker name should match the corresponding field in the [Holding File](#holding-file). The [Holding File](#holding-file) is where the ticker receives its value.

### Minimum Investment

The minimum investment begins in column 58, and is up to 10 characters long. Its content is constrained to a number, possibly with a decimal point. The ticker minimum is a currency-denominated minimum investment in the ticker. In its effort to rebalance an account, the software will place either no value in any ticker, or not less than this minimum. Non-balanceable debts may be specified with a negative minimum, which we assume to be a credit limit. Although some institutions place limitations only on initial investments in a fund, the software currently assumes that minimums are applicable to all future transfers into, or out of a fund. An investor may also arbitrarily opt for a self-imposed minimum in any fund. Use this field to do that. <i>Minimum investment is a currency field with no currency indicator.</i>

### Preferred Rounding

The preferred rounding begins in column 69, and is up to 8 characters long. Its content is constrained to a non-negative number, possibly with a decimal point. The preferred rounding is the round number of shares the software will choose for any ticker. Some institutions will require that ETFs be purchased or sold in whole shares, for example. As well, I myself typically like to trade ETFs or stocks in lots of 5 shares. Use this field to make a specification for preferred, or required rounding of shares in the ticker. 

### Ticker Subcode 1

The first subcode field begins in column 78, and is one character. Its content is constrained to one of the following characters indicating fund type characteristic:

1. '_' (placeholder for no entry)
2. 'B' (all bonds)
3. 'C' (all cash, e.g. bank accounts, CDs, or money-market funds)
4. 'D' (domestic securities)
5. 'E' (mortgage-backed securities)
6. 'G' (growth stocks)
7. 'H' (inflation protected securities)
8. 'K' (both growth and value stocks)
9. 'L' (large cap stocks)
10. 'M' (medium cap stocks)
11. 'N' (medium and small cap stocks)
12. 'O' (foreign securities)
13. 'P' (investor-specific non-balanceable investment project)
14. 'R' (all real estate)
15. 'S' (all stocks)
16. 'T' (corporate bonds)
17. 'U' (treasury securities)
18. 'V' (value stocks)
19. 'W' (small cap stocks)
20. 'Y' (high-yield bonds)
21. 'Z' (short-term bonds)

The first subcode, in conjunction with [Ticker Subcode 2](#ticker-subcode-2), [Ticker Subcode 3](#ticker-subcode-3), and [Ticker Subcode 4](#ticker-subcode-4) determine the characteristics of the security holding(s) of the ticker. The software checks for inconsistencies between the various subcodes, and reports an error in its log if inconsistencies exist. For example, a stock ticker cannot hold corporate bonds. The use of the 'S' and 'T' codes together is therefore not consistent, and the software will tell the user as much if these codes are used for the same ticker.  

Currently, the software does not have the capability to specify blended, or multi-assets funds. If a ticker falls in this category, it is possible to give the ticker no entries in any subcode field ('_' for all five subcodes). The software would then assign to the ticker a value apportionment equal to 1 / n, where 'n' is the number of tickers in the account. This assumes that each ticker has the default [Holding Weight](#holding-weight) of 1.00.  

A use of subcodes one through four may be demonstrated as follows: A large cap growth fund - holding domestic securities - may be specified with the subcodes 'S', 'D', 'L' and 'G'. This completely specifies the balanceable characteristics of the ticker in a way that is possible, and consistent. Currently, the software requires no more than four subcodes to completely characterize a ticker for its rebalancing effort.

### Ticker Subcode 2

The second subcode begins in column 80, and is one character. Its content is constrained to one of the characters described in [Ticker Subcode 1](#ticker-subcode-1). The second subcode, in conjunction with subcodes [Ticker Subcode 1](#ticker-subcode-1), [Ticker Subcode 3](#ticker-subcode-3), and [Ticker Subcode 4](#ticker-subcode-4) determine the characteristics of the securities holding(s) of the ticker. The software checks for inconsistencies between the various subcodes, and reports an error in its log if inconsistencies exist. For example, a stock ticker cannot hold corporate bonds, etc.

### Ticker Subcode 3

The third subcode begins in column 82, and is one character. Its content is constrained to one of the characters described in [Ticker Subcode 1](#ticker-subcode-1). The third subcode, in conjunction with subcodes [Ticker Subcode 1](#ticker-subcode-1), [Ticker Subcode 2](#ticker-subcode-2), and [Ticker Subcode 4](#ticker-subcode-4) determine the characteristics of the securities holding(s) of the ticker. The software checks for inconsistencies between the various subcodes, and reports an error in its log if inconsistencies exist. For example, a stock ticker cannot hold corporate bonds, etc.

### Ticker Subcode 4

The fourth subcode begins in column 84, and is one character. Its content is constrained to one of the characters described in [Ticker Subcode 1](#ticker-subcode-1). The fourth subcode, in conjunction with subcodes [Ticker Subcode 1](#ticker-subcode-1), [Ticker Subcode 2](#ticker-subcode-2), and [Ticker Subcode 3](#ticker-subcode-3) determine the characteristics of the securities holding(s) of the ticker. The software checks for inconsistencies between the various subcodes, and reports an error in its log if inconsistencies exist. For example, a stock ticker cannot hold corporate bonds, etc.

## Output Files

The software writes all output files except its log file in type-specific subdirectories in the directory identified in the source preference. The output files are described below. In each type-specific subdirectory, the user will find nested subdirectories specific to the [Portfolio Mnemonics](#portfolio-mnemonic) specified in the [Portfolio File](#portfolio-file), and referenced in the [Holding File](#holding-file). Put succinctly, this means that the software organizes the reports by specific investor using the directory hierarchy. The output files are as follows:  

### Log File

The log file is not investor specific. Files in this format are located in a directory named "log" in the directory identified in the source preference. Files of this type have the prefix "log_" followed by the date designation in the format "yyyymmdd", and a file type of ".txt". Unlike other output files that the software generates, the software keys the date of the log to the day the user ran the software, not the date of the [Holding File](#holding-file) that it read.

The log file contains log entries made by the software that are at or above the log level preference (see [-l lglv](#-level-lglv)). These can include errors, warnings or information that occurred in the most recent run of the software. Subsequent runs of the software on the same day overwrite the log file.

### Action File

The action file is one of four investor-specific text files that are output by the software. Files in this format are located in a directory named "action" in the directory identified in the source preference. Files of this type have the prefix "action_" followed by the date designation in the format "yyyymmdd", and a file type of ".txt". When run with no command line options, the software will write an action file for each investor that has holdings in the [Holding File](#holding-file). The date of the action files will match that from the source [Holding File](#holding-file).

The action file contains user-readable instructions for rebalancing a portfolio. The software breaks down the file into sections first by institution, then by account. For each account, the software will first report the number of shares of individual stocks, bonds, or ETFs that the user will need to buy or sell in order to realize the rebalance.

For accounts that have a [Rebalance Procedure](#rebalance-procedure) of "percent", the software will then report the reallocation percentages per ticker needed to rebalance the account. For accounts that have a [Rebalance Procedure](#rebalance-procedure) of "redistribute", the software will report mutual fund transfers organized by the fund from which assets are coming, and then in order of decreasing value. If there are stock, bond or ETF purchases or sales in the same account, the software may report either purchases or sales of mutual funds using these assets. Assets coming from stock/bond/ETF purchases or sales would not be indicated as transferred from any other mutual fund. 

### Difference File

The difference file is one of four investor-specific text files that are output by the software. Files in this format are located in a directory named "difference" in the directory identified in the source preference. Files of this type have the prefix "difference_" followed by the date designation in the format "yyyymmdd", and a file type of ".txt". When run with no command line options, the software will write a difference file for each investor that has holdings in the [Holding File](#holding-file). The date of the difference files will match that from the source [Holding File](#holding-file).

The difference file is organized just the same as the [Holding File](#holding-file), a hierarchy of institutions, account, and tickers owned by the investor. In fact - although it is a text file - it is also a perfectly usable CSV file in its own right. In the [Holding File](#holding-file) the three numeric values at the end of holding rows are shares, price and value, respectively. In the difference file, these numbers are, in column order:

* Proposed (rebalanced) value of the holding
* Current value of the holding
* The difference between the first two columns

If the software was able to successfully do its job, the difference value for institution and account rows will always be zero. For ticker holdings, however, the difference - plus or minus - is the proposed value to be transferred into, or out of the ticker.

### Proposed File

The proposed file is one of four investor-specific text files that are output by the software. Files in this format are located in a directory named "proposed" in the directory identified in the source preference. Files of this type have the prefix "proposed_" followed by the date designation in the format "yyyymmdd", and a file type of ".txt". When run with no command line options, the software will write a proposed file for each investor that has holdings in the [Holding File](#holding-file). The date of the proposed files will match that from the source [Holding File](#holding-file).

The proposed file is a readable report of the state of the portfolio of the investor. It has several sections:

* A report of the date of each source CSV file used
* Investor specific information from the [Portfolio File](#portfolio-file), including [Investor Birthdate](#investor-birthdate), [Investor Mortality Date](#investor-mortality-date), monthly [CPI Adjusted](#cpi-adjusted) income, monthly [Non-CPI Adjusted](#non-cpi-adjusted) income, monthly [Social Security](#social-security) income, and annual [Taxable Income](#taxable-income)
* Non-investor specific information to include expected rate of inflation, S&P 500 high, S&P 500 last close, and S&P 500 today (if these are specified)
* A table of balanceable assets, broken down by institutions in rows, and tax-types in columns
* A summary of <b>Level 1</b> investment percentages <b>after</b> rebalance, i.e., stock, bond, cash, and real-estate percentages
* A summary of non-balanceable assets reported by account or ticker, and value

The proposed file is formatted exactly the same as a [Report File](#report-file), but with <b>Level 1</b> investment percentages reported <i>after</i> rebalance. 

### Report File

The report file is one of four investor-specific text files that are output by the software. Files in this format are located in a directory named "report" in the directory identified in the source preference. Files of this type have the prefix "report_" followed by the date designation in the format "yyyymmdd", and a file type of ".txt". When run with no command line options, the software will write a report file for each investor that has holdings in the [Holding File](#holding-file). The date of the report files will match that from the source [Holding File](#holding-file).

The report file is a readable report of the state of the portfolio of the investor. It has several sections:

* A report of the date of each source CSV file used
* Investor specific information from the [Portfolio File](#portfolio-file), including [Investor Birthdate](#investor-birthdate), [Investor Mortality Date](#investor-mortality-date), monthly [CPI Adjusted](#cpi-adjusted) income, monthly [Non-CPI Adjusted](#non-cpi-adjusted) income, monthly [Social Security](#social-security) income, and annual [Taxable Income](#taxable-income)
* Non-investor specific information to include expected rate of inflation, S&P 500 high, S&P 500 last close, and S&P 500 today (if these are specified)
* A table of balanceable assets, broken down by institutions in rows, and tax-types in columns
* A summary of <b>Level 1</b> investment percentages <b>before</b> rebalance, i.e., stock, bond, cash, and real-estate percentages
* A summary of non-balanceable assets reported by account or ticker, and value

The report file is formatted exactly the same as a [Proposed File](#proposed-file), but with <b>Level 1</b> investment percentages reported <i>before</i> rebalance.

## How Does Rebalancing Work?

The software rebalances holdings on a per-account basis. The user specifies the order in which the software rebalances accounts using the [Rebalance Order](#rebalance-order) field of the [Account File](#account-file). I have recommended that the [Rebalance Order](#rebalance-order) of an account be a unique integer, but in the event that it is not, the software will break ties by rebalancing accounts as they are declared in the [Account File](#account-file). The declaration of accounts in the [Account File](#account-file) does not specify to which portfolio the account belongs. However, the [Rebalance Order](#rebalance-order) always forms a total rebalance order in whichever portfolio the accounts may appear.  

Currently, the [Rebalance Order](#rebalance-order) of an account is meaningless unless the account is the last rebalanced account in a portfolio. For all but the last rebalanced account in a portfolio, the software will use a [Weight Rebalancer](#weight-rebalancer) to accomplish its work. For the last balanced account in a portfolio, the software will use a specialized [Weight Rebalancer](#weight-rebalancer) called a [Closure Rebalancer](#closure-rebalancer).

### Weight Rebalancer

The weight rebalancer begins with a table whose elements correspond in meaning to the weight values specified in the [Detailed File](#detailed-file). The table below contains these elements, the weight level of its investment category, and the initial weights:

| Category                  | Weight Level | Initial Value |
|---------------------------|--------------|---------------|
| All Investments           | 0            | 100.0         |
| Bond                      | 1            | 36.0          |
| Bond Corporate            | 2            | 12.5          |
| Both Foreign              | 2            | 7.0           |
| Bond Government           | 2            | 0.0           |
| Bond High Yield           | 2            | 5.0           |
| Bond Inflation Protected  | 2            | 5.0           |
| Bond Mortgage Instruments | 2            | 8.0           |
| Bond Uncategorized        | 2            | 12.5          |
| Bond Short                | 2            | 50.0          |
| Cash                      | 1            | 10.0          |
| Cash Government           | 2            | 50.0          |
| Cash Uncategorized        | 2            | 50.0          |
| Real Estate               | 1            | 4.0           |
| Stock                     | 1            | 50.0          |
| Stock Domestic            | 2            | 60.0          |
| Stock Foreign             | 2            | 40.0          |
| Stock Growth              | 5 or 6       | 40.0          |
| Stock Growth and Value    | 4 or 5       | 50.0          |
| Stock Growth or Value     | 4 or 5       | 50.0          |
| Stock Large               | 3            | 60.0          |
| Stock Medium              | 4            | 50.0          |
| Stock Not Large           | 3            | 40.0          |
| Stock Small               | 4            | 50.0          |
| Stock Value               | 5 or 6       | 60.0          |

<sub><sup>Table 2: Weight Rebalancer weight table with weight-type levels and initial values</sup></sub>

What is a weight level? The software rebalances accounts by comparing weights of investment categories against each other. It does this in a tree of weight-type nodes. At the root of the tree is one node, the <b>Level 0</b> (zero) node, representing <b>all investments</b>. Children of the root are <b>Level 1</b> investments, the most-coarse grained characterization: stocks, bonds, cash or real estate. <b>Levels 2, 3, 4, 5 and 6</b> are increasingly fine-grained characterizations of these core investment groups:

* Bonds are characterized at <b>Level 2</b> in various ways (see the table 2)
* Cash is characterized at <b>Level 2</b> as Government, or Uncategorized
* Stocks are characterized at <b>Level 2</b> as Domestic, or Foreign
* Domestic and Foreign Stocks are characterized at <b>Level 3</b> as Large, or Not-Large
* Large Stocks are characterized at <b>Level 4</b> as Growth and Value, or Growth or Value
* Not-Large Stocks are characterized at <b>Level 4</b> as Medium, or Small
* Not-Large Stocks are also characterized at <b>Level 4</b> as Growth and Value, or Growth or Value
* Medium and Small Stocks are characterized at <b>Level 5</b> as Growth and Value, or Growth or Value
* Growth or Value Stocks are characterized at <b>Level 5</b> or <b>Level 6</b> as Growth, or Value

My decision to characterize growth or value stocks at a lower level than company size was somewhat arbitrary. However, I have not seen a mutual fund characterized as strictly growth, or value without the size of the companies in which it invests already specified. 

When rebalancing any currency value passed to it, a weight-type node does the following for each of its children: It multiplies the received currency value by the weight assigned to the child, then divides the result by the weight sum of all its children. The node then pushes the result down to the child node. At the leaves of the tree a weight-type node will find no children, but it <i>will</i> find a collection of one or more tickers of that same investment type. Using the [Holding Weight](#holding-weight) assigned to the ticker, the weight-type node will perform exactly the same calculation: it multiplies the received currency value by the [Holding Weight](#holding-weight) of the ticker, and divides the result by the weight sum of all the tickers. In truth, a weight-type node does not differentiate between child weight-type nodes, and tickers when it pushes currency value down the tree. I have abstracted the two to look the same. 

The weight assigned to the <b>Level 0</b> root weight-type node is arbitrary. I have assigned 100 (see the table) to be evocative of 100%. Since the software compares the weight of the root node against nothing else, it can be any positive value.

The way a weight-type node determines if it has children is to ask if any child has weight. If the answer is no, it may mean there are no children, or it may mean existing children have no weight. In this way, a user can ask the software to disregard weight rebalancing in favor of the [Holding Weight](#holding-weight) of the tickers by setting all <b>Level 1</b> weights to zero. This forces the root node to allocate any received currency directly to its contained tickers, and not to any weight-type child node. See the discussion in [Holding Weight](#holding-weight). For a weight rebalancer that is not a [Closure Rebalancer](#closure-rebalancer), the user would zero all the weights in the [Account File](#account-file), or all <b>Level 1</b> weights in the [Detailed File](#detailed-file), but only if a detailed entry exists for the account.   

Back to the table of weights in the weight rebalancer: Before rebalancing any account, the software overlays the <b>Level 1</b> weights in the table with the weights it read for the account in the [Account File](#account-file). If the account has an entry in the [Detailed File](#detailed-file), the software overlays the table weights a second time. This time, however, it overlays <i>all</i> of the weights in the table. It can do this since the [Detailed File](#detailed-file) contains an explicit entry for each. After both weight overlays, the software applies an equity (stock) weight adjustment using the S&P 500 today preference setting (see [-t sptd](#-today-sptd)) versus the S&P 500 last-close (see [-c spcl](#-close-spcl)) preference setting. Each weight-type node then references this table when it decides how to allocate currency to its children.  

### Closure Rebalancer

A closure rebalancer is a specialized [Weight Rebalancer](#weight-rebalancer), and the software uses it for the last rebalanced account in any portfolio. In the section under [Weight Rebalancer](#weight-rebalancer), I discussed how the rebalancer overlays its weight table twice, once for the <b>Level 1</b> weights in the [Account File](#account-file), and a second time for all the weights in the [Detailed File](#detailed-file). Before making equity (stock) weight adjustments based on the high, last close, and today's values of the S&P 500, a closure rebalancer overlays the <b>Level 1</b> weights a third time. It does this by summing the <b>Level 1</b> weights that the user specified in the [Portfolio File](#portfolio-file). It multiplies each <b>Level 1</b> weight by the total balanceable value of the portfolio, then divides by the sum of the weights. Finally, the closure rebalancer subtracts the existing balances that ordinary [Weight Rebalancers](#weight-rebalancer) have already assigned to previous investment <b>Level 1</b> investment categories. Non-negative values become the new weights for the corresponding category. Negative values mean that the software has already assigned too much value to the category. The software enters zero for the weight instead, and writes information about the condition to its log file. 

After making the S&P 500 today versus S&P 500 last-close adjustment, the closure rebalancer performs the S&P 500 today versus S&P 500 high (see [-h sphg](#-high-sphg)) adjustment if the user has set the [Increase at Zero](#increase-at-zero) percent delta in the corresponding row of the [Portfolio File](#portfolio-file).

### An Example

My intention with this section was to use [Dia](https://wiki.gnome.org/Apps/Dia/) to create one or more graphics that showed trees of the software's investment weight-type hierarchy. For example, picture a tree with a root node, <b>Level 0</b> (all investments), with four children for <b>Level 1</b>: stocks, bonds, cash, and real estate. Then I would have another graphic for the <b>Level 1</b> subtree for stocks showing branches for domestic and foreign stocks (<b>Level 2</b>) sub-nodes. And then for each of these sub-nodes there would be branches to <b>Level 3</b> sub-nodes: large, and not-large stocks, etc. You get the picture. Also picture another graphic for the bond subtree. And another subtree for cash. And yet another subtree for real estate.

Furthermore, I wanted to show how each ticker in an account was pushed down the investment weight-type tree (using the fund characteristics specified in the [Ticker File](#ticker-file)) until the ticker reached a leaf with its most specific categorization. For example, a ticker only specified as stock would end at the appropriate <b>Level 1</b> node. However, a domestic, small-cap, value stock ticker would be pushed all the way down to <b>Level 6</b>, i.e.: stock (<b>Level 1</b>); domestic stock (<b>Level 2</b>); not-large stock (<b>Level 3</b>); small stock (<b>Level 4</b>); growth-or-value stock (<b>Level 5</b>); value stock (<b>Level 6</b>). And then what I had hoped to do is show how the software was subdividing some whole value of the account (say $10,000) as it went down the tree, finally using the [Holding Weight](#holding-weight) for one or more tickers in a leaf node to subdivide the value given to that weight type. With a default [Holding Weight](#holding-weight) of one for all holdings, the software would therefore equally divide value given to tickers of the same weight type. Does all that make sense?

However, today is the last day of March 2022. I want to finish this project today, and I am running out of time, patience and stamina. I am therefore foregoing - at least for now - the construction of the visualizations I had hoped to create. Please contact me if you have questions about this software, and how it works.

## What Do I Do with Debts?

This software is not meant to rebalance debts. Do you want to know how to rebalance your debts? Investigate the interest rate you pay on your various debts, then move as much debt as you can to the lowest rate account. Repeat with the next lowest rate account until all your debts are reallocated. This procedure assumes that debt can be moved from one account to another, and that is admittedly sometimes difficult for credit card balances. Additionally, moving debt to a mortgage involves refinancing a loan...an expensive proposition. 

All that is beside the point. Although this software does not rebalance debts, it can be used to track them, and take them into account when calculating the whole value of a portfolio. When attempting to reallocate rebalance-able assets in accounts, the software does not really care if the total value of the account is positive, or negative. It just takes the whole value of the account, and reallocates it using desired weights to the tickers that can be rebalanced. These three statements are axiomatic: 1) <i>If the value of an account is positive, no balanceable ticker will have negative value after rebalance</i>; 2) <i>If the value of an account is negative, no balanceable ticker will have positive value after rebalance</i>, and; 3) <i>If the value of the account is zero, no balanceable ticker will have a non-zero value after rebalance</i>. Make sense?     

So how do you track your debts? Firstly - if the user tracks the debt as a ticker - the ticker cannot be balanceable. It should have a 'J' (a fund that is not available for rebalance) code in the [Holding Type](#holding-type) of the [Holding File](#holding-file), and the same in the [Ticker Character](#ticker-character) of the [Ticker File](#ticker-file). Secondly, the [Holding Value](#holding-value) needs a negative value in the [Holding File](#holding-file). In my own holdings, I have accomplished this by leaving the [Holding Value](#holding-value) blank, then giving the [Holding Price](#holding-price) a value of -1.00, and the [Holding Shares](#holding-shares) a value equal to the absolute value of the debt. The software then calculates the [Holding Value](#holding-value) as a negative number equal to the debt. For whatever it may mean to the user, any product of [Holding Shares](#holding-shares) and [Holding Price](#holding-price) that equals the debt is fine. Obviously the shares and price have to have different signs to produce a negative [Holding Value](#holding-value). Another way to do this is give the [Holding Value](#holding-value) an explicit, negative entry. Breaking a debt account (like a credit card balance) into non-balanceable tickers is a convenient way to subdivide the debt for accounting purposes. Keep in mind that a [Ticker Symbol](#ticker-symbol) need not necessarily be a well-known mnemonic on a trading exchange. It can be something that the user makes up.

Another way to report debts is to simply log a negative [Holding Value](#holding-value) for an account row in the [Holding File](#holding-file), and not break down the account by ticker. Since there are no tickers in the account, there is nothing for the software to rebalance. Either way the user does it, the software will report debts in both the [Report File](#report-file) and the [Proposed File](#proposed-file) as negative-valued, unbalanceable assets. If a negative-valued account has tickers, the software will report the individual tickers. If the account has no tickers, the software will simply report the account. Either way, the software subtracts the debts from the net worth calculation for the investor.

## Room for Enhancement

I am proud of my work on this project, but frankly I am tired of working on it. I have already used the tool to rebalance my own portfolio at the end of January 2022. It worked splendidly. By keeping my [Holding Files](#holding-file) in a private directory on my laptop, I can track changes to my net worth. I just update the other input CSV files as needed, and the software uses the ones that are most recent, but not more recent than the latest [Holding File](#holding-file). As well, I have created two soft links to removable media (thumb drives) on my laptop. These symbolic names end up being my argument to the [-u link](#-use-link) command line option. By setting the destination directory in this way, I can back up all my source files every time I update them. By backing up my private data, I have protected it against loss. And the source code of this project is now protected here on [GitHub](https://github.com/). I digress. There are several places where the software could be enhanced, and I may undertake these enhancements in the future:  

1. I have mentioned the [Basis File](#basis-file), [Gains Files](#gains-files) and [Income Files](#income-files), but what does the software do with this tax-applicable information? Currently, nothing. It reads the information into libraries, but does nothing with it. My intention was to create account value synthesizers that produce capital-gains tax. These would be unbalanceable, negative values to count against the net worth of the investor. Investor income, and tax brackets are required for these calculations. The software would then subtract the result from the value of certain holdings, such as a house, or investments outside tax-deferred accounts. The user can currently create negative-valued accounts (or tickers) to account for tax, but he/she would have to calculate the amount of tax by hand. An enhancement would be to create one or more tax value synthesizers of this type. <i>Target: release 2+.x.</i>
2. The CSV files are a bit unwieldy, and hard to edit. I have tried to ameliorate this by structuring the files such that columns have fixed-lengths, no matter what their content. Really, the software does not require this. It trims leading and trailing whitespace from field entries before interpreting it. An enhancement would be to transition the software to use a SQL database for all the input data. There would be a SQL table corresponding to each existing CSV file. The primary key of each table would be the existing key of the CSV file, but prepended with the date that is currently encoded in the name of a CSV file (the 'yyyymmdd' suffix). <i>Target: release 2+.x.</i>
3. A somewhat necessary enhancement that goes along with the last one would include a graphic user (GUI) interface to edit the SQL tables, and make insertions. In truth, this enhancement can occur before the creation of SQL tables, with the edits and insertions occurring in the existing CSV files. Currently, I am using a text editor to make changes to the CSV files, and create new ones. Once a developer transitions the software input to SQL, a user of the software would need to make edits with a generic SQL editor, or a custom GUI for the purpose. <i>Target: release 2+.x.</i>
4. See [An Example](#an-example) section. I think it would be helpful to have graphic examples of how the software creates a tree of investment weight-types. This is documentation-only todo.  <i>Target: release 2.x.</i>

If you have any other ideas for enhancements, please feel free to let me know!

## Credits

I selfishly credit only myself with the design, coding and documentation of this project.

## Warranty

I give no warranty of this product for its stated purpose, either explicitly or implicitly, nor of its correctness when used for this purpose. I have successfully used the software for myself. It has been thoroughly, but not <b>exhaustively</b> tested. Use at your own risk. Feedback and bug reports are welcome!

## License

License of this project and the resulting tool is covered by the GNU General Public License v3.0.
