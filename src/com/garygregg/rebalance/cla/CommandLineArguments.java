package com.garygregg.rebalance.cla;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Level;

public class CommandLineArguments<TokenType extends Enum<TokenType>> {

    // A map of token types to their matching dispatch actions
    private final Map<TokenType, Dispatch> dispatchMap;

    // A distinguished token type for ordinary command line tokens
    private final TokenType distinguished;

    // The name of this class
    private final String name = CommandLineArguments.class.getSimpleName();

    /*
     * A dispatch action to take when there are no command line options;
     * null for no action
     */
    private final Dispatch onNone;

    /*
     * A lookup of string representations to their matching enumerated token
     * type
     */
    private final Map<String, TokenType> optionLookup = new HashMap<>();

    // The set of recognized command line options
    private final Set<String> recognizedSet = optionLookup.keySet();

    /**
     * Constructs the command line arguments parser.
     *
     * @param dispatchMap   A map of token types to their matching dispatch
     *                      actions
     * @param distinguished A distinguished token type for ordinary command
     *                      line tokens
     * @param onNone        A dispatch action to take when there are no command line
     *                      options (null for no action)
     */
    public CommandLineArguments(@NotNull Map<TokenType, Dispatch> dispatchMap,
                                @NotNull TokenType distinguished,
                                Dispatch onNone) {

        // Set the dispatch map, and cycle for each key in the map.
        this.dispatchMap = dispatchMap;
        for (TokenType key : dispatchMap.keySet()) {

            /*
             * Add an option lookup to consist of the string representation of
             * the first/next key mapped to the key itself.
             */
            optionLookup.put(key.toString().toLowerCase(), key);
        }

        // Set the remaining member variables.
        this.distinguished = distinguished;
        this.onNone = onNone;
    }

    /**
     * Tests the command line arguments parser.
     *
     * @param arguments Command line arguments to test
     */
    public static void main(@NotNull String[] arguments) {

        /*
         * Declare a message format, and an 'on destination' dispatch
         * action.
         */
        final String format = "Processing %s with argument of '%s'.%n";
        final Dispatch onDestination =
                argument -> System.out.printf(format, "destination", argument);

        // Declare an 'on inflation' dispatch action.
        final Dispatch onInflation =
                argument -> System.out.printf(format, "inflation", argument);

        // Declare an 'on level' dispatch action.
        final Dispatch onLevel = argument -> {

            // Print the entry message. Declare a logging level variable.
            System.out.printf(format, "level", argument);
            Level level;
            try {

                // Try to parse the logging level variable.
                level = Level.parse(argument.toUpperCase());
                System.out.printf("Successfully parsed level " +
                        "type as '%s'.%n", level);
            }

            // Oops. Bad logging level.
            catch (IllegalArgumentException exception) {
                throw new CLAException(exception);
            }
        };

        // Declare an 'on none' dispatch action.
        final Dispatch onNone =
                argument -> System.out.printf(format, "none", argument);

        // Declare an 'on path' dispatch action.
        final Dispatch onPath =
                argument -> System.out.printf(format, "path", argument);

        /*
         * Declare and initialize a dispatch map for token IDs, then add a
         * dispatch action for four types.
         */
        final Map<TokenId, Dispatch> dispatchMap = new HashMap<>();
        dispatchMap.put(TokenId.DESTINATION, onDestination);
        dispatchMap.put(TokenId.INFLATION, onInflation);
        dispatchMap.put(TokenId.LEVEL, onLevel);
        dispatchMap.put(TokenId.PATH, onPath);

        /*
         * Create a command line argument processor using a distinguished type
         * that has no dispatch action. Use an 'on none' action for this
         * purpose.
         */
        final CommandLineArguments<TokenId> cla =
                new CommandLineArguments<>(dispatchMap, TokenId.OTHER, onNone);
        try {

            // Process the command line arguments.
            cla.process(arguments);
        }

        // Catch any CLA exception that may occur.
        catch (CLAException exception) {
            System.err.println(exception.getMessage());
        }
    }

    /**
     * Determines whether there is a match between two strings (in a case
     * insensitive way).
     *
     * @param first  The first string (assumed to be lower case)
     * @param second The second string (not assumed to be lower case)
     * @return True if the strings match, false otherwise
     */
    private static boolean match(@NotNull String first,
                                 @NotNull String second) {

        // Initialize the result. Convert the second string to lower case.
        boolean result = true;
        second = second.toLowerCase();

        /*
         * Get the length of the smaller of the two strings. Cycle for each
         * character position that both strings have, or until there fails to
         * be a match at any position.
         */
        final int length = Math.min(first.length(), second.length());
        for (int i = 0; (i < length) && result; ++i) {

            // Compare the first/next character position.
            result = first.charAt(i) == second.charAt(i);
        }

        // Return the result.
        return result;
    }

    /**
     * Gets the distinguished token type for ordinary command line tokens.
     *
     * @return The distinguished token type for ordinary command line tokens
     */
    public @NotNull TokenType getDistinguished() {
        return distinguished;
    }

    /**
     * Returns the string representation of a recognized option, or null if
     * no option is recognized.
     *
     * @param option A potential option
     * @return The string representation of a recognized option, or null if
     * none is recognized.
     */
    private String match(@NotNull String option) {

        // Declare and initialize local variables.
        String candidate = null;
        boolean found = false;

        /*
         * Get an iterator from the recognized set. Cycle while options exist,
         * and while none is recognized.
         */
        final Iterator<String> iterator = recognizedSet.iterator();
        while (iterator.hasNext() && (!found)) {

            // Try to match the first/next candidate.
            found = match(candidate = iterator.next(), option);
        }

        // Return a matched candidate, or null if none is recognized.
        return found ? candidate : null;
    }

    /**
     * Processes command line arguments.
     *
     * @param arguments The command line arguments
     * @throws CLAException Indicates that there is something wrong with the
     *                      command line arguments
     */
    public void process(@NotNull String[] arguments) throws CLAException {

        // Declare local variables, and initialize them where necessary.
        int optionsCount = 0;
        Token<TokenType> token;
        Dispatch dispatch;

        /*
         * Tokenize the arguments, and get an iterator for the tokens. Cycle
         * while tokens exist.
         */
        final List<Token<TokenType>> tokens = tokenize(arguments);
        final Iterator<Token<TokenType>> iterator = tokens.iterator();
        while (iterator.hasNext()) {

            /*
             * Increment the options count, and try to get a dispatch action
             * for the first/next token. Is there no available action?
             */
            ++optionsCount;
            dispatch = dispatchMap.get((token = iterator.next()).getId());
            if (null == dispatch) {

                /*
                 * There is no available action; this is an unrecognized
                 * option.
                 */
                throw new CLAException(String.format("%s: Unrecognized " +
                        "option '%s'", name, token.getValue()));
            }

            // The option exists, but does it have an argument?
            else if (!(iterator.hasNext() &&
                    ((token = iterator.next()).getId()).
                            equals(TokenId.OTHER))) {

                // The option has no argument.
                throw new CLAException(String.format("%s: Option '%s' " +
                        "has no argument", name, token.getValue()));
            }

            // Dispatch the option with its argument.
            dispatch.dispatch(token.getValue());
        }

        // Dispatch the 'on none' action if there were no options.
        if (!((null == onNone) || (0 < optionsCount))) {
            onNone.dispatch("");
        }
    }

    /**
     * Processes a command line argument.
     *
     * @param tokens   A list of tokens to receive the command line argument
     * @param argument The command line argument
     */
    private void processArgument(@NotNull List<Token<TokenType>> tokens,
                                 @NotNull String argument) {

        /*
         * Lookup the option ID for the argument. Add a new token with the ID
         * and the argument.
         */
        final TokenType id = optionLookup.get(match(argument.toLowerCase()));
        tokens.add(new Token<>(id, argument));
    }

    /**
     * Processes a command line option.
     *
     * @param tokens   A list of tokens to receive the command line option
     * @param argument The command line argument containing the option
     */
    private void processOption(@NotNull List<Token<TokenType>> tokens,
                               @NotNull String argument) {

        /*
         * Find the separator in the command line option. Is there no
         * separator?
         */
        final int equalsIndex = argument.indexOf("=");
        if (equalsIndex < 0) {

            /*
             * There is no separator. Treat the whole option as a single
             * command line argument.
             */
            processArgument(tokens, argument);
        }

        // There is a separator.
        else {

            // Add a token if there is one in front of the separator.
            if (0 < equalsIndex) {
                processArgument(tokens, argument.substring(0, equalsIndex));
            }

            // Add a token if there is one after the separator.
            if (equalsIndex < argument.length()) {
                tokens.add(new Token<>(getDistinguished(),
                        argument.substring(equalsIndex + 1)));
            }
        }
    }

    /**
     * Tokenizes command line arguments.
     *
     * @param arguments The command line arguments
     * @return An ordered list of tokenized command line arguments
     * @throws CLAException Indicates that there is something wrong with the
     *                      command line arguments
     */
    private @NotNull List<Token<TokenType>> tokenize(@NotNull String[] arguments)
            throws CLAException {

        // Declare local variables.
        String[] split;
        int splitLength;

        // Declare the token list, and cycle for each command line argument.
        final List<Token<TokenType>> tokens = new ArrayList<>();
        for (String string : arguments) {

            /*
             * Split the first/next command line argument around any hyphen.
             * How many hyphens are there?
             */
            split = string.split("(?<=-)");
            switch (splitLength = split.length) {

                case 0:

                    // Bad regular expression. This should not happen.
                    throw new RuntimeException("Encountered ill-formed " +
                            "regular expression for hyphens");

                case 1:

                    /*
                     * No hyphens. Add a new token for the whole command line
                     * argument using the distinguished token ID.
                     */
                    tokens.add(new Token<>(getDistinguished(), split[0]));
                    break;

                case 2:

                    // One hyphen. Process the single command line argument.
                    processArgument(tokens, split[splitLength - 1]);
                    break;

                case 3:

                    /*
                     * Two hyphens. Process the single command line argument as
                     * an option.
                     */
                    processOption(tokens, split[splitLength - 1]);
                    break;

                default:

                    /*
                     * There are more than two hyphens. This is not an argument
                     * that we recognize.
                     */
                    throw new CLAException(String.format("%s: Unrecognized " +
                            "option '%s'", name, string));
            }
        }

        // Return the token list.
        return tokens;
    }
}
