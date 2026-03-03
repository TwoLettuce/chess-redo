package ui;

public class HelpfulStrings {

    public static final String HELP_MESSAGE_LOGGED_OUT =
            "Commands and Usages:\n" + EscapeSequences.SET_TEXT_UNDERLINE +
                    "help - display the help menu\n" + EscapeSequences.RESET_TEXT_UNDERLINE +
                    ">> help\n" + EscapeSequences.SET_TEXT_UNDERLINE +
                    "register - register a new user.\n" + EscapeSequences.RESET_TEXT_UNDERLINE +
                    ">> register <username> <password> <email>\n" + EscapeSequences.SET_TEXT_UNDERLINE +
                    "login - login as an existing user.\n" + EscapeSequences.RESET_TEXT_UNDERLINE +
                    ">> login <username> <password>\n" + EscapeSequences.SET_TEXT_UNDERLINE +
                    "quit - exit the chess client\n" + EscapeSequences.RESET_TEXT_UNDERLINE +
                    ">> quit";

    public static final String HELP_MESSAGE_LOGGED_IN =
            "Commands and Usages:\n" + EscapeSequences.SET_TEXT_UNDERLINE +
                    "help - display the help menu\n" + EscapeSequences.RESET_TEXT_UNDERLINE +
                    ">> help\n" + EscapeSequences.SET_TEXT_UNDERLINE +
                    "logout - logout\n" + EscapeSequences.RESET_TEXT_UNDERLINE +
                    ">> logout\n" + EscapeSequences.SET_TEXT_UNDERLINE +
                    "create - create a new game and give it a name.\n" + EscapeSequences.RESET_TEXT_UNDERLINE +
                    ">> create <name>\n" + EscapeSequences.SET_TEXT_UNDERLINE +
                    "list - get a list of all games.\n" + EscapeSequences.RESET_TEXT_UNDERLINE +
                    ">> list\n" + EscapeSequences.SET_TEXT_UNDERLINE +
                    "join - join a game and start playing!\n" + EscapeSequences.RESET_TEXT_UNDERLINE +
                    ">> join <Game No.> <WHITE/BLACK>\n" + EscapeSequences.SET_TEXT_UNDERLINE +
                    "observe - observe a game as it unfolds.\n" + EscapeSequences.RESET_TEXT_UNDERLINE +
                    ">> observe <Game No.>\n" + EscapeSequences.SET_TEXT_UNDERLINE +
                    "quit - exit the chess client\n" + EscapeSequences.RESET_TEXT_UNDERLINE +
                    ">> quit";

    public static final String NOT_LOGGED_IN = "[Not Logged in] >> ";
}
