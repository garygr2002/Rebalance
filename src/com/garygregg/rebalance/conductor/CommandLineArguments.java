package com.garygregg.rebalance.conductor;

import com.garygregg.rebalance.CommandLineId;
import com.garygregg.rebalance.Pair;
import com.garygregg.rebalance.cla.CLAException;
import com.garygregg.rebalance.cla.Dispatch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class CommandLineArguments<TokenType extends Enum<TokenType>> {

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
     * @param onNone       A dispatch action to take when there are no command
     *                     line options (null for no action)
     */
    public CommandLineArguments(@NotNull Iterable<Dispatch<TokenType>> dispatchList,
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
     * Determines whether there is a match between a character sequence and a
     * string (in a case-insensitive way).
     *
     * @param sequence The character sequence (assumed to be lower case)
     * @param string   The string (not assumed to be lower case)
     * @return True if the character sequence and the string match; false
     * otherwise
     */
    private static boolean match(@NotNull CharSequence sequence,
                                 @NotNull String string) {

        // Initialize the result. Convert the string to lower case.
        boolean result = true;
        string = string.toLowerCase();

        /*
         * Get the length of the smaller of the two arguments. Cycle for each
         * character position that both arguments have, or until there fails to
         * be a match at any position.
         */
        final int length = Math.min(sequence.length(), string.length());
        for (int i = 0; (i < length) && result; ++i) {

            // Compare the first/next character position.
            result = sequence.charAt(i) == string.charAt(i);
        }

        // Return the result.
        return result;
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
     * @param tokens   A collection of tokens to receive the command line
     *                 argument
     * @param argument The command line argument
     */
    private void processArgument(@NotNull Collection<? super Token<TokenType>> tokens,
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
             * Lookup the option ID for the argument. Add a new token with the
             * ID and the argument.
             */
            final TokenType id = optionLookup.get(match(argument.toLowerCase()));
            tokens.add(new Token<>(id, argument));
        }
    }

    /**
     * Processes a command line option.
     *
     * @param tokens   A collection of tokens to receive the command line
     *                 option
     * @param argument The command line argument containing the option
     */
    private void processOption(@NotNull Collection<? super Token<TokenType>>
                                       tokens,
                               @NotNull String argument) {

        /*
         * Find the separator in the command line option. Is there no
         * separator?
         */
        final int equalsIndex = argument.indexOf('=');
        if (0 > equalsIndex) {

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
