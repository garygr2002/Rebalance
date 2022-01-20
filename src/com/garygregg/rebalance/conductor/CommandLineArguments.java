package com.garygregg.rebalance.conductor;

import com.garygregg.rebalance.CommandLineId;
import com.garygregg.rebalance.MessageLogger;
import com.garygregg.rebalance.Pair;
import com.garygregg.rebalance.cla.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintStream;
import java.util.*;
import java.util.prefs.Preferences;

public class CommandLineArguments<TokenType extends Enum<TokenType>> {

    // The output stream
    private static final PrintStream stream = MessageLogger.getOutputStream();

    // A map of token types to a list of matching dispatch actions
    private final Map<TokenType, List<Dispatch<TokenType>>> dispatchMap =
            new HashMap<>();

    // The name of this class
    private final String name = CommandLineArguments.class.getSimpleName();

    /*
     * A dispatch action to take when there are no command line options;
     * null for no action
     */
    private final Dispatch<TokenType> onNone;

    /*
     * A lookup of string representations to their matching enumerated token
     * types
     */
    private final Map<String, TokenType> optionLookup = new HashMap<>();

    // The set of recognized command line options
    private final Set<String> recognizedSet = optionLookup.keySet();

    /**
     * Constructs the command line arguments parser.
     *
     * @param dispatchList A list of dispatch actions
     * @param onNone       A dispatch action to take when there are no command line
     *                     options (null for no action)
     */
    public CommandLineArguments(@NotNull List<Dispatch<TokenType>> dispatchList,
                                Dispatch<TokenType> onNone) {

        /*
         * Declare a variable to hold a token type, and cycle for each dispatch
         * action in the given dispatch list.
         */
        TokenType type;
        for (Dispatch<TokenType> dispatch : dispatchList) {

            /*
             * Add the dispatch action to the list for its type. Add an option
             * lookup to consist of the lowercase string representation of the
             * type mapped to the type itself.
             */
            dispatchMap.computeIfAbsent(type = dispatch.getKey(),
                    k -> new ArrayList<>()).add(dispatch);
            optionLookup.put(type.toString().toLowerCase(), type);
        }

        /*
         * Set the dispatch option to take when there are no command line
         * options.
         */
        this.onNone = onNone;
    }

    /**
     * Tests the command line arguments parser.
     *
     * @param arguments Command line arguments to test
     */
    private static void doTest(@NotNull String[] arguments) {

        /*
         * TODO : Delete this method.
         *
         * Declare and initialize a preferences object for this class.
         */
        final Preferences preferences =
                Preferences.userRoot().node(
                        CommandLineArguments.class.getName());

        // Declare an 'on current' dispatch action.
        final Dispatch<CommandLineId> onCurrent =
                new DoublePreferenceDispatch<>(CommandLineId.CURRENT,
                        preferences, stream, false);

        // Declare an 'on destination' dispatch action.
        final Dispatch<CommandLineId> onDestination =
                new PreferenceDispatch<>(CommandLineId.DESTINATION,
                        preferences, stream);

        // Declare an 'on extraordinary' dispatch action.
        final Dispatch<CommandLineId> onExtraordinary =
                new LimitedPreferenceDispatch<>(CommandLineId.EXTRAORDINARY,
                        preferences, stream);

        // Declare an 'on high' dispatch action.
        final Dispatch<CommandLineId> onHigh =
                new DoublePreferenceDispatch<>(CommandLineId.HIGH, preferences,
                        stream, false);

        // Declare an 'on inflation' dispatch action.
        final Dispatch<CommandLineId> onInflation =
                new DoublePreferenceDispatch<>(CommandLineId.INFLATION, preferences,
                        stream, true);

        // Declare an 'on level' dispatch action.
        final Dispatch<CommandLineId> onLevel =
                new LevelPreferenceDispatch<>(CommandLineId.LEVEL, preferences,
                        stream);

        // Declare an 'on ordinary' dispatch action.
        final Dispatch<CommandLineId> onOrdinary =
                new LimitedPreferenceDispatch<>(CommandLineId.ORDINARY,
                        preferences, stream);

        // Declare an 'on none' dispatch action.
        final Dispatch<CommandLineId> onNone = new Dispatch<>() {

            @Override
            public void dispatch(String argument) {
                receive(getKey(), argument);
            }

            @Override
            public @NotNull CommandLineId getKey() {
                return CommandLineId.OTHER;
            }
        };

        // Declare an 'on source' dispatch action.
        final Dispatch<CommandLineId> onSource =
                new PathPreferenceDispatch<>(CommandLineId.SOURCE, preferences,
                        stream);

        // Declare an 'on limit' dispatch action.
        final Dispatch<CommandLineId> onXLimit =
                new IntPreferenceDispatch<>(CommandLineId.X, preferences,
                        stream, false);

        /*
         * Declare and initialize a dispatch list for token IDs, then add a
         * dispatch action for the 'on current' argument.
         */
        final List<Dispatch<CommandLineId>> dispatchList = new ArrayList<>();
        dispatchList.add(onCurrent);

        /*
         * Add dispatch actions for the 'on destination' and 'on extraordinary'
         * arguments.
         */
        dispatchList.add(onDestination);
        dispatchList.add(onExtraordinary);

        // Add dispatch actions for the 'on high' and 'on inflation' arguments.
        dispatchList.add(onHigh);
        dispatchList.add(onInflation);

        // Add dispatch actions for the 'on level' and 'on ordinary' arguments.
        dispatchList.add(onLevel);
        dispatchList.add(onOrdinary);

        // Add dispatch actions for 'on source' and 'on limit' arguments.
        dispatchList.add(onSource);
        dispatchList.add(onXLimit);

        // Create a command line argument processor using the 'on none' action.
        final CommandLineArguments<CommandLineId> cla =
                new CommandLineArguments<>(dispatchList, onNone);
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
     * Tests the command line arguments parser.
     *
     * @param arguments Command line arguments to test
     */
    public static void main(@NotNull String[] arguments) {

        /*
         * TODO : Delete this method.
         *
         * Declare tests.
         */
        final String[][] tests = {
                {},
                {"--p=/my_path_1", "--p"},
                {"-path", "/my_path_2", "-p"},
                {"--d=/my_destination", "--p=/my_path_3", "--c=4327.16",
                        "--h=4393.68", "--i=1.01", "--l=warning", "-c"},
                {"--p=/my_p\0ath_4", "--p"},
                {"-l", "bad_level"}
        };

        // Declare a test counter, and perform the tests.
        int i = 0;
        for (String[] test : tests) {

            // Describe the test, perform it, then print a newline.
            stream.printf("Performing test %d...%n", i++);
            doTest(test);
            stream.println();
        }

        // Perform the last test with the command line arguments.
        stream.printf("Performing last test, %d, with command line " +
                "arguments...%n", i);
        doTest(arguments);
    }

    /**
     * Determines whether there is a match between two strings (in a
     * case-insensitive way).
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
     * Receives an argument associated with a key.
     *
     * @param key      The key
     * @param argument The argument associated with the key
     * @param <T>      The type of the key
     * @param <U>      The type of the argument
     */
    private static <T, U> void receive(T key, @NotNull U argument) {

        // TODO: Delete this method.
        stream.printf("Processing %s with argument of '%s'.%n",
                (null == key) ? null : key.toString().toLowerCase(),
                argument);
    }

    /**
     * Gets the distinguished token type for ordinary command line tokens.
     *
     * @return The distinguished token type for ordinary command line tokens
     */
    public TokenType getDistinguished() {
        return (null == onNone) ? null : onNone.getKey();
    }

    /**
     * Returns the string representation of a recognized option, or null if
     * no option is recognized.
     *
     * @param option A potential option
     * @return The string representation of a recognized option, or null if
     * none is recognized.
     */
    private @Nullable String match(@NotNull String option) {

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

        // Declare argument and dispatch list.
        String argument;
        List<Dispatch<TokenType>> dispatchList;

        // Declare token and token type.
        Token<TokenType> token;
        TokenType type;

        // Declare and initialize the call list. Tokenize the arguments.
        final List<Pair<Dispatch<TokenType>, String>> calls =
                new ArrayList<>();
        final List<Token<TokenType>> tokens = tokenize(arguments);

        // Get the size of the token list, and cycle while tokens exist.
        final int size = tokens.size();
        for (int i = 0; i < size; ) {

            /*
             * Try to get a dispatch action list for the first/next token. Are
             * there no available actions?
             */
            dispatchList = dispatchMap.get(type = (token =
                    tokens.get(i++)).getId());
            if (null == dispatchList) {

                /*
                 * There is no available action; this is an unrecognized
                 * option.
                 */
                throw new CLAException(String.format("%s: Unrecognized " +
                        "option '%s'", name, token.getValue()));
            }

            // The option exists, but does it have an argument?
            else if ((i < size && ((token =
                    tokens.get(i)).getId()).equals(CommandLineId.OTHER))) {

                /*
                 * The option has an argument. Increment the index to point to
                 * the next option.
                 */
                ++i;
            }

            // The option has no argument. Give it a default null argument.
            else {
                token = new Token<>(type, null);
            }

            /*
             * Get the dispatch action argument from the token. Cycle for each
             * dispatch action in the current dispatch list.
             */
            argument = token.getValue();
            for (Dispatch<TokenType> dispatch : dispatchList) {

                /*
                 * Add the dispatch action to the call list along with the
                 * argument.
                 */
                calls.add(new Pair<>(dispatch, argument));
            }
        }

        // Dispatch the 'on none' action if there were no options...
        if ((null != onNone) && (calls.isEmpty())) {
            calls.add(new Pair<>(onNone, ""));
        }

        // ...there were options.
        else {

            // Sort the calls of the options.
            calls.sort(Comparator.comparing(dispatchStringPair ->
                    dispatchStringPair.getFirst().getKey()));
        }

        // Call the sorted dispatchers.
        for (Pair<Dispatch<TokenType>, String> pair : calls) {
            pair.getFirst().dispatch(pair.getSecond());
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
         * First assume the argument is a numeric value that had been negative,
         * and that its preceding hyphen had been misinterpreted as an option
         * flag.
         */
        try {

            /*
             * Try to parse the argument as a double. If that works, add a
             * token with the distinguished token ID and an argument with the
             * minus sign put back in.
             */
            Double.parseDouble(argument);
            tokens.add(new Token<>(getDistinguished(), "-" + argument));
        }

        // The argument could not be parsed as a double.
        catch (@NotNull NumberFormatException exception) {

            /*
             * Lookup the option ID for the argument. Add a new token with the ID
             * and the argument.
             */
            final TokenType id = optionLookup.get(match(argument.toLowerCase()));
            tokens.add(new Token<>(id, argument));
        }
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
    private @NotNull List<Token<TokenType>> tokenize(
            @NotNull String @NotNull [] arguments) throws CLAException {

        // Declare local variables.
        String[] split;
        int splitLength;

        // Declare the token list, and cycle for each command line argument.
        final List<Token<TokenType>> tokens = new ArrayList<>();
        for (String string : arguments) {

            // Identify how many hyphens precede the command.
            split = string.trim().split("^-|(?<=^-)-");
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
