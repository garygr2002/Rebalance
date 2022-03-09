# Rebalance

A Java Project to Rebalance Investment Portfolios

The Rebalance Project is one that I undertook beginning in February 2022. It is a Java-based, command-line software tool that rebalances the investment holdings of one or more investors. The tool is currently driven by a series of comma-separated files (csv's). The tool will rebalance the portfolio holdings of each investor across institutions, accounts, mutual funds, ETFs and individual securities based on declared weight preferences.

Output from the tool is a series of text files for each investor:

1. A report of the current state of the portfolio
2. A report of the state of the portfolio after proposed changes
3. A file that shows currency differences between proposed and current holdings
4. A file that describes the reallocation actions required to effect the proposed changes   

### Motivation

I have been interested in investing and investments for many years, and have sought throughout my professional career to save and invest diligently. During that time, I have periodically undertaken the task of rebalancing my personal investments by hand using spreadsheets and a calculator. I found this to be an imprecise, tedious, and error-prone task. 

### Why this Project?

Beginning at the end of calendar year 2020, I changed my career focus from full-time employment, and contract assignments to a long term status as a freelance software developer and data scientist. In my capacity in this role, I undertook this project for two reasons. First, I desired a mostly automated tool to undertake the chore of rebalancing my investments. Second, I desired to showcase a large, publicly accessible software project that was conceived, designed, coded and documented solely by myself.

In service of both goals, I decided to produce my own custom tool for the task. Another option might have been to use existing commercial software, or an existing open-source project. I rejected a commercial software solution for two reasons, the first being the one-time, or ongoing expense. I also felt commercial software might not be exactly what I needed. I rejected open-source solutions for two reasons, the first being a lack of complete trust in any solution I might discover. The second reason was that I felt my time would be better spent professionally in developing my own software rather than in researching open-source solutions. After all, at the end of the day I could demonstrate a unique professionally accomplishment when developing my own tool.

### Input

The input to the tool is a series of sixteen CSV files that are coded by date in their names (format: yyyymmdd). Sample files are located in the data directory of this project. There you will find a series of subdirectories. Some subdirectories contain one or more input files of the same type, differing by date, or one or more output files differing by date.
These sixteen input files are:

1. Account: A description of accounts unique by institution and account number
2. Basis: A hierarchy of expenses used for calculating capital gains tax
3. Code: A description of single-character alphabetic codes used internally by the tool
4. Detailed: Fine-grained weight preferences specific to account
5. Distinguished: Key-value pairs that map programmatically accessible keys to sensitive portfolio keys, institution names, and account numbers
6. Holdings: The most important file, a hierarchy of holding valuations organized by portfolios, institutions, accounts, and securities
7. Capital Gains Tax Rates (4 files): Current capital gains tax rates, one file for each of head-of-household, joint, separate and single filers
8. Income Tax Rates (4 files): Current income tax rates, one file for each of head-of-household, joint, separate and single filers
9. Portfolio: A description of portfolios unique by a key
10. Ticker: A description of tickers unique by key (funds, ETFs or individual security) that describe the type of securities held (e.g., stock, bond, cash or real estate)

As well as sample data files, the reader will also find in each data subdirectory a text file describing in column order the fields in the CSV files of that type. Each row in the CSV files of the given type is assumed to have a unique key. The key field(s) are so indicated in the description file. 

I have left as an enhancement the possibility that input to the tool may be changed to SQL tables indexed by a combination of date, and existing keys in the CSV files. An additional enhancement would be a graphical user interface by which the SQL tables may be edited. The current tool does not have these features.

### State of the Project

After approximately 13 months of work, I have deemed the design, code and deployment activities of the project to be complete. I have allocated one man-month to fully document the project for professional showcase. My intention is to complete this work by the conclusion of March 2022. An observer will note progressive activity and growth in this markdown file and related documents during this time. Below you will find some portions of the markdown template that remain unedited. Stop by often for updates!

## Table of Contents

- [Installation](#installation)
- [Usage](#usage)
- [Command Line Options](#command-line-options)
- [Credits](#credits)
- [Warranty](#warranty)
- [License](#license)

## Installation

This project was designed and coded in an Ubuntu Linux environment using the IntelliJ IDE and Java version 11. It should be possible to install and run, or even build the software in a Windows, or MacOS environment. At this time I have decided against investigating the steps required for installing the product in non-Linux environments. You will need the Java 11 jre to run the software.

Among the releases for this product, you will find the following files:

1. An executable jar containing the software
2. An executable file with a command to start the software
3. The source code as a zip file
4. The source code as a tar file

Download at least the executable jar, and the executable file. Ensure that the executable file has the executable permission set for at least the user of the file. To do this, use a terminal and change directory to the directory containing your download. Then use the command: <pre>chmod u+x rebalance</pre> to set the executable permission. Return to your user home directory, and edit the .bash_alias file to include a line similar to the following:<pre>export PATH={path to your download}:$PATH</pre> and save the file. You will now need to close your terminal, and reopen a new terminal to make the rebalance command available.  

Alternatively, you may build the software locally. You will need a Java jdk of at least version 11. In the directory containing this markdown file, you will find <i>build.xml</i> and <i>build.properties</i> files suitable for building the software using ant. These files were automatically generated using the Ant Build Generation plugin from Jetbrains, version 203.4. I desired to edit these files minimally to create an external build using ant. As such, you will only need to edit the <i>build.properties</i> file locally. See below. 

Clone the repository for this project using a Linux terminal. On the command line, type: <pre>git clone https://github.com/garygr2002/Rebalance.git </pre>

If ant is not installed in your Linux environment, you may install it using the following command: <pre>sudo apt install ant</pre>

The software also uses the jetbrains annotations jar, version 20.1.0. To build the software, you will need to acquire the annotations jar from an IntelliJ installation, or directly from the Internet. Only the jar is required, but it must be available from a relative path of <i>...org/jetbrains/annotations/20.1.0/</i> from where you install it. Call this installation directory '<i>x</i>'. Edit the <i>build.properties</i> file in this directory, and follow the instructions therein to set the <i>path.variable.maven_repository</i> variable with the location of your annotations jar, '<i>x</i>'. Set the <i>jdk.home.11</i> variable with the path to your Java 11+ jdk. You should now be ready to build the software using the ant command from a terminal in the directory containing this markdown file.

## Usage

The software uses Java preferences to maintain persistent settings. Currently, settings are not user-specific. If preferences have not been previously set, you will see something similar to the following terminal when running the software with the 'p' option: 

![alt text](assets/images/preferences_reset.png)

Minimum runtime settings are achieved by using the 'm' option, which will set the logging levels and thresholds to sane values. The option will also set the known, long-term inflation percentage, and the rebalance limit for same fund-types. Finally, this option sets a default as the source for the data files. You will see something similar to the following terminal when running the software after the 'm' and 'p' options: 

![alt text](assets/images/minimum_preferences.png)

You may reset the source directory for the data file using the 's' option. You will see something similar to the following terminal when running the software after the 's' and 'p' options:

![alt text](assets/images/data_set.png)

You may set the critical component of the destination directory for backup using the 'u' option. For this option, the same prefix for the data source directory is assumed. You will see something similar to the following terminal when running the software after the 'u' and 'p' options:

![alt text](assets/images/use_set.png)

You may set current and historical valuations for the Standard and Poor 500 using the 'c', 'h' and 't' options. These options stand for 'close (last)', 'high' and 'today' respectively. The software uses these settings to automatically adjust investor-specific equity weights given in the portfolio csv file. The software will always make adjustments for today's valuation of the S&P 500 versus last close, but an equity adjustment of today's setting versus historical high is a per-investor preference. Read further in this document, or explore the description of the csv files for more information. Please note that it is not required to set valuations for the S&P 500 in order for the software to run correctly. When one or more of these preferences are not set, the software will skip the adjustment. You will see something similar to the following terminal when running the software after the 'c', 'h', 't' and 'p' options: 

![alt text](assets/images/SandP500_set.png)

A screen snap is omitted here for the 'b' option, which backs up the current source directory to the current destination directory. As well, a screen snap is omitted here for running the software with no option, which causes the software to rebalance investor portfolios given the current csv input files.

## Command Line Options

Below find an exhaustive list of command line options. As noted previously, running the software with no option causes it to read input csv files from the designated source directory, and subdirectories. The software will use the csv files to rebalance the indicated portfolios, and produce output. For the command line options listed below, any prefix of the full word positively identifies the option. Options may be specified with a single preceding hyphen such the very next token after any intervening whitespace is accepted as an argument. Alternatively, options may be specified with two preceding hyphens such that the user specifies an argument with a connecting equals sign, and no intervening whitespace. Note: If more than one command line option is present, the software evaluates the options, and will set or show a preference in the order given below. In this way, it is possible for a set preference to clobber a preference that was previously set on the same command line. This is a necessary feature, not a bug! Preference are currently global, and not user-specific. 

### -reset

Resets all preferences to defaults. The default is null when null has a meaning for any preference.

### -minimum

Sets all preferences required for the software to run in some fashion. This assumes a default for the source directory for the data files.

### -preference

Lists the current settings for all the preferences.

### -level lglv

Sets the minimum level at which messages appear in the log file. Messages generated by the software below this level will not appear in the log file. The <i>lglv</i> argument may be one of : ALL, CONFIG, FINE, FINER, FINEST, INFO, OFF, SEVERE or WARNING. The argument is required.

### -ordinary rdnr

The logging level for messages the software deems to be "ordinary". The <i>rdnr</i> argument may be one of ALL, CONFIG, FINE, FINER, FINEST, INFO or OFF. The argument is required.

### -extraordinary xtrd

The logging level for messages the software deems to be "extraordinary". The <i>xtrd</i> argument may be one of ALL, CONFIG, FINE, FINER, FINEST, INFO or OFF. The argument is required.

### -inflation fltn

The annual expected rate of inflation, expressed as a percentage. The <i>fltn</i> argument must be a number, possibly with a decimal point, and it is required. Among other possible uses, the software uses this value to compute the accumulated value of pensions that have no cost-of-living increase. For this purpose, the software assumes no inflation if this preference has not been set. 

### -high sphg

The record high of the Standard & Poor 500. The <i>sphg</i> argument must be a non-negative number, possibly with a decimal point, and it is required. The software will use this value, if set, and the value of the Standard & Poor 500 today (see option, below) to upward-adjust the percentage of a portfolio allocated to equity investments. It will only do this for portfolios that so specify in the portfolio csv file.

### -close spcl

The last close of the Standard & Poor 500. The <i>spcl</i> argument must be a non-negative number, possibly with a decimal point, and it is required. The software will use this value, if set, and the value of the Standard and Poor 500 today (see option, below) to adjust the percentage of equity investment allocations in all portfolios. To skip this adjustment, the user may leave this preference unset.

### -today sptd

The value of the Standard & Poor 500 on the day the software is run. The <i>sptd</i> argument must be a non-negative number, possibly with a decimal point, and it is required. The software will use this value, if set, to perform adjustments to the equity investment allocations in all portfolios. It does this in conjunction with the S&P 500 high, and S&P 500 last close preferences previously discussed. 

### -x ncnt

The <i>ncnt</i> argument must be a non-negative integer, and is required. It is the rebalance limit of same-type investments per account. This takes a little explanation: When a value (in dollars, for example) is allocated to a certain "type" of investment (i.e., small capitalization value stocks), the software may not be able to perfectly distribute the amount to the investments in that category. A "perfect" distribution is one that exactly matches - to the penny - the known weight preferences for investments of that type. If the perfect allocation cannot be accomplished, the software will attempt to redistribute the residual to a subset of the investments. It will do this for each possible subset, looking for a subset that minimizes the residual. For minimum residuals, the software will choose an allocation that minimizes the standard deviation from an allocation that perfectly matches the desired weights for the investments in that category. Make sense?  

Why would an allocation not be able to exactly match the specified weights? At the level of individual funds in the account, there are two reasons. First, the fund may have a minimum investment which may not be met with a certain weight allocation. Second, for an ETF or stock, an investment may have a requirement, or preference for some whole or round number of shares that may not be met with a certain weight allocation.

The thing is, checking all the subsets of an investment category in an account requires a runtime that is two to the power of the number of investments in the category. This is prohibitively expensive for even a modest number of investments. A situation where this would come into play would be when an account contains, for example, twenty small-cap value stock ETFs, all with a preferred round number of shares. You can see that it will take no small amount of runtime to examine two to the twentieth power of investment subsets. If the number of investments of a certain type exceed this preference, the software will not even try to rebalance them. The alternative is a program that appears to hang indefinitely when run. Note that the software applies this same limit as it works up the tree of same-type investments. At the coarsest level - level one - the software needs a minimum of four in this setting to rebalance equities, bonds, cash and real-estate.

There is a dynamic programming algorithm that can find a best-fit for investments subsets as discussed here. This is faster than exhaustively searching all subsets as is currently done by the software. I will move on to discuss hierarchies of investments types, and the weights assigned to subtypes further in this discussion. In the meantime, you may wish to leave this preference to its reasonable default.

### -source spth

The source directory for the csv files that this software reads. The source directory must contain the following subdirectories: account, basis, code, detailed, distinguished, gains_head, gains_joint, gains_separate, gains_single, holding, income_head, income_joint, income_separate, income_single, portfolio and ticker. The <i>spth</i> argument may be any valid absolute path, or valid path relative to the current directory. The argument is required.

### -destination dpth

The default destination used by the backup command. See the backup command below. The <i>dpth</i> argument may be any valid absolute path, or valid path relative to the current directory. The argument is required.

### -use link

Sets the destination directory to a concatenation of the home directory of the user, followed by the argument, followed by the existing source path. The <i>link</i> argument may be any alphanumeric string such that the destination directory resolves to any valid absolute path. The argument is required.

### -backup [bpth]

Backs up the files in the source path. The <i>bpth</i> argument is optional. If given, it may be any valid absolute path, or valid path relative to the current directory. If not given, the backup command uses the existing destination path preference.

### -assistance

Displays the command line usage text.

## Credits

I selfishly credit only myself with the design, coding and documentation of this project.

### Warranty

No warranty, or guarantee of the correctness or suitability of this product for any purpose is given, either explicitly or implicitly. I have used the software for my own purposes, but it has not been exhaustively tested. Use at your own risk. Feedback and bug reports are welcome!

### License

License of this project and the resulting tool is covered by the GNU General Public License v3.0.

---

🏆 The previous sections are the bare minimum, and your project will ultimately determine the content of this document. You might also want to consider adding the following sections.

## Badges

![badmath](https://img.shields.io/github/languages/top/lernantino/badmath)

Badges aren't necessary, per se, but they demonstrate street cred. Badges let other developers know that you know what you're doing. Check out the badges hosted by [shields.io](https://shields.io/). You may not understand what they all represent now, but you will in time.

## Features

If your project has a lot of features, list them here.

## How to Contribute

If you created an application or package and would like other developers to contribute it, you can include guidelines for how to do so. The [Contributor Covenant](https://www.contributor-covenant.org/) is an industry standard, but you can always write your own if you'd prefer.

## Tests

Go the extra mile and write tests for your application. Then provide examples on how to run them here.