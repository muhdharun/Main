package seedu.addressbook.parser;

import seedu.addressbook.commands.*;
import seedu.addressbook.data.exception.IllegalValueException;
import seedu.addressbook.data.person.NRIC;
import seedu.addressbook.data.person.Offense;

import java.io.IOException;
import java.util.*;
import java.util.logging.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static seedu.addressbook.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

/**
 * Parses user input.
 */
public class Parser {
    private final static Logger logr = Logger.getLogger( Parser.class.getName() );

    public static final Pattern PERSON_INDEX_ARGS_FORMAT = Pattern.compile("(?<targetIndex>.+)");

    public static final Pattern KEYWORDS_ARGS_FORMAT =
            Pattern.compile("(?<keywords>\\S+(?:\\s+\\S+)*)"); // one or more keywords separated by whitespace
//@@author muhdharun -reused
    public static final Pattern PERSON_DATA_ARGS_FORMAT = // '/' forward slashes are reserved for delimiter prefixes
            Pattern.compile("(?<name>[^/]+)"
                    + " n/(?<nric>[^/]+)"
                    + " d/(?<dateOfBirth>[^/]+)"
                    + " p/(?<postalCode>[^/]+)"
                    + " s/(?<status>[^/]+)"
                    + " w/(?<wantedFor>[^/]+)"
                    + "(?<pastOffenseArguments>(?: o/[^/]+)*)"); // variable number of offenses
//@@author
    public static final Pattern EDIT_DATA_ARGS_FORMAT =
            Pattern.compile("n/(?<nric>[^/]+)"
                    + " p/(?<postalCode>[^/]+)"
                    + " s/(?<status>[^/]+)"
                    + " w/(?<wantedFor>[^/]+)"
                    + "(?<pastOffenseArguments>(?: o/[^/]+)*)"); // variable number of offenses

    public static final Pattern PERSON_NAME_FORMAT = Pattern.compile("(?<name>[^/]+)");
    public static final Pattern PERSON_NRIC_FORMAT = Pattern.compile("(?<nric>[^/]+)");

    /**
     * Signals that the user input could not be parsed.
     */
    public static class ParseException extends Exception {
        ParseException(String message) {
            super(message);
        }
    }

    /**
     * Used for initial separation of command word and args.
     */
    public static final Pattern BASIC_COMMAND_FORMAT = Pattern.compile("(?<commandWord>\\S+)(?<arguments>.*)");

    private static void setupLogger() {
        LogManager.getLogManager().reset();
        logr.setLevel(Level.ALL);

        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(Level.INFO);
        logr.addHandler(ch);

        try {
            FileHandler fh = new FileHandler("parserLog.log");
            fh.setLevel(Level.FINE);
            logr.addHandler(fh);
        } catch (IOException ioe) {
            logr.log(Level.SEVERE, "File logger not working.", ioe);
        }
    }

    /**
     * Parses user input into command for execution.
     *
     * @param userInput full user input string
     * @return the command based on the user input
     */
    public Command parseCommand(String userInput) {
        setupLogger();
        final Matcher matcher = BASIC_COMMAND_FORMAT.matcher(userInput.trim());

        /*if (!matcher.matches()) {
            switch (result) {
                case AddCommand.COMMAND_WORD:
                    return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));

                case DeleteCommand.COMMAND_WORD:
                    return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));

                case EditCommand.COMMAND_WORD:
                    return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));

                case ClearCommand.COMMAND_WORD:
                    return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ClearCommand.MESSAGE_USAGE));

                case FindCommand.COMMAND_WORD:
                    return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));

                case ListCommand.COMMAND_WORD:
                    return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));

                case ViewCommand.COMMAND_WORD:
                    return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewCommand.MESSAGE_USAGE));

                case ViewAllCommand.COMMAND_WORD:
                    return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewAllCommand.MESSAGE_USAGE));

                case ExitCommand.COMMAND_WORD:
                    return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ExitCommand.MESSAGE_USAGE));

                case LockCommand.COMMAND_WORD:
                    return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, LockCommand.MESSAGE_USAGE));

                case HelpCommand.COMMAND_WORD: // Fallthrough
                default:
                    return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
            }
        }*/

        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
        }

        final String commandWord = matcher.group("commandWord");
        final String arguments = matcher.group("arguments");

        logr.info("Parsed the user input and matching commands.");

        switch (commandWord) {
            case AddCommand.COMMAND_WORD:
                return prepareAdd(arguments);

            case DeleteCommand.COMMAND_WORD:
                return prepareDelete(arguments);

            case EditCommand.COMMAND_WORD:
                return prepareEdit(arguments);

            case ClearCommand.COMMAND_WORD:
                return new ClearCommand();

            case FindCommand.COMMAND_WORD:
                return prepareFindOrCheck(arguments,false);

            case CheckCommand.COMMAND_WORD:
                return prepareFindOrCheck(arguments,true);

            case ListCommand.COMMAND_WORD:
                return new ListCommand();

            case InboxCommand.COMMAND_WORD:
                return new InboxCommand();

            case ViewAllCommand.COMMAND_WORD:
                return prepareViewAll(arguments);

            case RequestHelp.COMMAND_WORD:
                return prepareRequest(arguments);

            case ExitCommand.COMMAND_WORD:
                return new ExitCommand();

            case LockCommand.COMMAND_WORD:
                return new LockCommand();

            case HelpCommand.COMMAND_WORD: // Fallthrough
            default:
                return new HelpCommand();
        }
    }
//@@author muhdharun -reused
    /**
     * Parses arguments in the context of the add person command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareAdd(String args){
        final Matcher matcher = PERSON_DATA_ARGS_FORMAT.matcher(args.trim());
        // Validate arg string format
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }
        try {
            return new AddCommand(
                    matcher.group("name"),
                    matcher.group("nric"),
                    matcher.group("dateOfBirth"),
                    matcher.group("postalCode"),
                    matcher.group("status"),
                    matcher.group("wantedFor"),
                    getTagsFromArgs(matcher.group("pastOffenseArguments"))
            );
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(ive.getMessage());
        }
    }
//@@author
    /**
     * Checks whether the private prefix of a contact detail in the add command's arguments string is present.
     */
    private static boolean isPrivatePrefixPresent(String matchedPrefix) {
        return matchedPrefix.equals("p");
    }

    /**
     * Extracts the new person's tags from the add command's tag arguments string.
     * Merges duplicate tag strings.
     */
    private static Set<String> getTagsFromArgs(String tagArguments) throws IllegalValueException {
        // no tags
        if (tagArguments.isEmpty()) {
            return Collections.emptySet();
        }
        // replace first delimiter prefix, then split
        final Collection<String> tagStrings = Arrays.asList(tagArguments.replaceFirst(" o/", "").split(" o/"));
        return new HashSet<>(tagStrings);
    }

//@@author muhdharun -reused
    /**
     * Parses arguments in the context of the delete person command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareDelete(String args){
        try {
            final String nric = parseArgsAsNric(args);

            return new DeleteCommand(new NRIC(nric));
        } catch (ParseException e) {
            logr.log(Level.WARNING, "Invalid delete command format.", e);
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));



        } catch (IllegalValueException ive) {
            logr.log(Level.WARNING, "Invalid name/id inputted.", ive);
            return new IncorrectCommand(ive.getMessage());
        }
    }
//@@author
    /**
     * Parses arguments in the context of the edit person command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    // TODO: Refactor prepareEdit and prepareAdd
    private Command prepareEdit(String args) {
        final Matcher matcher = EDIT_DATA_ARGS_FORMAT.matcher(args.trim());
        // Validate arg string format
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE));
        }
        try {
            return new EditCommand(
                    matcher.group("nric"),
                    matcher.group("postalCode"),
                    matcher.group("status"),
                    matcher.group("wantedFor"),
                    getTagsFromArgs(matcher.group("pastOffenseArguments"))
            );
        } catch (IllegalValueException ive) {
            logr.log(Level.WARNING, "Invalid edit command format.", ive);
            return new IncorrectCommand(ive.getMessage());
        }
    }

    /**
     * Parses arguments in the context of the view command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareView(String args) {
        try {
            final int targetIndex = parseArgsAsDisplayedIndex(args);
            return new ViewAllCommand(targetIndex);
        } catch (ParseException | NumberFormatException e) {
            logr.log(Level.WARNING, "Invalid view command format.", e);
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ViewAllCommand.MESSAGE_USAGE));
        }
    }

    /**
     * Parses arguments in the context of the view all command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareViewAll(String args) {

        try {
            final int targetIndex = parseArgsAsDisplayedIndex(args);
            return new ViewAllCommand(targetIndex);
        } catch (ParseException | NumberFormatException e) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    ViewAllCommand.MESSAGE_USAGE));
        }
    }

    /**
     * Parses the given arguments string as a single index number.
     *
     * @param args arguments string to parse as index number
     * @return the parsed index number
     * @throws ParseException if no region of the args string could be found for the index
     * @throws NumberFormatException the args string region is not a valid number
     */
    private int parseArgsAsDisplayedIndex(String args) throws ParseException, NumberFormatException {
        final Matcher matcher = PERSON_INDEX_ARGS_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            logr.warning("Index number does not exist in argument");
            throw new ParseException("Could not find index number to parse");
        }
        return Integer.parseInt(matcher.group("targetIndex"));
    }
//@@author muhdharun
    private String parseArgsAsNric(String args) throws ParseException {
        final Matcher matcher = PERSON_NRIC_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            logr.warning("NRIC does not exist in argument");
            throw new ParseException("Could not find NRIC to parse");
        }
        return matcher.group(0);
    }

    /**
     * Parses arguments in the context of the find person command.
     *
     * @param args full command args string
     * @return the prepared command
     */
    private Command prepareFindOrCheck(String args, boolean isChecking) {
        args = args.trim();
        try{
            NRIC keyPerson = new NRIC(args);

            return ((isChecking == true) ? new CheckCommand(args) : new FindCommand(args));
        }
        catch (IllegalValueException invalidNric){
            logr.warning("NRIC argument is invalid");
            return ((isChecking == true) ? new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,CheckCommand.MESSAGE_USAGE)):
                    new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,FindCommand.MESSAGE_USAGE)));
         }
    }
//@@author
    /**
     * Parses arguments in context of request help command.
     *
     * @param args full command args string
     * @return the prepared request command
     */
    private Command prepareRequest(String args) {
        String caseName, message;
        String[] argParts = args.trim().split(" ", 2);

        if (argParts.length < 2) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    RequestHelp.MESSAGE_USAGE));
        }

        caseName = argParts[0];
        message = argParts[1];

        try {
            return new RequestHelp(caseName, message);
        } catch (IllegalValueException ive) {
            return new IncorrectCommand(Offense.MESSAGE_OFFENSE_INVALID);
        }
    }

}
