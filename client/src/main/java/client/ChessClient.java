package client;

import model.GameData;
import model.UserData;
import model.request.LoginRequest;
import server.ServerFacade;
import ui.ChessBoardDrawer;
import ui.EscapeSequences;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ChessClient {
    private final ChessBoardDrawer drawer = new ChessBoardDrawer();
    private final String notLoggedInStatus = "Not Logged in >> ";
    private String loggedInStatus;
    private final ArrayList<String> validBasicCommands = new ArrayList<>(List.of("h", "help", "q", "quit", "l", "login", "r", "register"));
    private final ServerFacade serverFacade;
    private String authToken = "";
    private ArrayList<GameData> games = new ArrayList<>();


    public ChessClient(String serverUrl) {
        serverFacade = new ServerFacade(serverUrl);
    }

    public void run(){
        replNotLoggedIn();
    }

    private void replNotLoggedIn(){
        quit:
        while(true){
            System.out.print(EscapeSequences.SET_TEXT_COLOR_GREEN);
            System.out.print(notLoggedInStatus);
            String[] args;
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            try {
                args = parse(input);
            } catch (InvalidCommandException ex){
                System.out.print(EscapeSequences.SET_TEXT_COLOR_RED);
                System.out.println(ex.getMessage());
                continue;
            }
            switch (args[0]){
                case "q","quit":
                    System.out.println(EscapeSequences.SET_TEXT_COLOR_YELLOW+ "C-ya!");
                    break quit;
                case "h","help":
                    displayHelpMessage();
                    break;
                case "l","login":
                    login(args);
                    break;
                case "r", "register":
                    register(args);
                    break;
                case "clear":
                    try {
                        serverFacade.clear();
                    } catch (Exception ex){
                        System.out.println("clear unsuccessful");
                    }
                    break;
                default:
            }
        }


    }

    private void register(String[] args) {
        UserData userData = new UserData(args[1], args[2], args[3]);
        try {
            authToken = serverFacade.register(userData);
            System.out.printf("Now logged in as %s!%n", userData.username());
            if (authToken != null) {
                loggedInStatus = String.format("[%s] >> ", userData.username());
                replLoggedIn();
            }
        } catch (Exception ex){
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + ex.getMessage());
        }
    }

    private void replLoggedIn() {
        logout:
        while(true) {
            System.out.print(EscapeSequences.SET_TEXT_COLOR_GREEN);
            System.out.print(loggedInStatus);
            String[] args;
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            try {
                args = parse(input);
            } catch (InvalidCommandException ex){
                System.out.print(EscapeSequences.SET_TEXT_COLOR_RED);
                System.out.println(ex.getMessage());
                continue;
            }
            switch (input){
                case "help", "h":
                    displayLoggedInHelpMessage();
                    break;
                case "logout":
                    try {
                        serverFacade.logout(authToken);
                        break logout;
                    } catch (Exception ex){
                        System.out.println("Logout unsuccessful");
                    }
                case "create", "c":
                    try {
                    int gameID = serverFacade.createGame(authToken, args[1]);
                    System.out.printf("%s created with Game No. %d%n", args[1], gameID);
                    } catch (Exception ex){
                        System.out.println("create game unsuccessful");
                    }
                    break;
                case "list", "l":
                    list();

            }
        }
    }

    private void list() {
        games = (ArrayList<GameData>) serverFacade.listGames(authToken);
        for (GameData game : games){
            System.out.printf(EscapeSequences.SET_TEXT_COLOR_MAGENTA + "Game No. %d%n", game.gameID());
            System.out.printf(EscapeSequences.SET_TEXT_COLOR_BLUE + "%s%n", game.gameName());
            System.out.printf(EscapeSequences.SET_TEXT_COLOR_WHITE + "White: %s%n", game.whiteUsername());
            System.out.printf("\u001b[38;5;94m" + "Black: %s%n", game.blackUsername());
        }
    }


    private void login(String[] args) {
        LoginRequest loginRequest = new LoginRequest(args[1], args[2]);
        try {
            serverFacade.login(loginRequest);
            System.out.printf("Logged in as %s!%n", loginRequest.username());
        } catch (Exception ex){
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + ex.getMessage());
        }

    }

    private void displayHelpMessage() {
        String help = "Commands and Usages:\n" + EscapeSequences.SET_TEXT_UNDERLINE +
                "help - display the help menu\n" + EscapeSequences.RESET_TEXT_UNDERLINE +
                ">> help\n" + EscapeSequences.SET_TEXT_UNDERLINE +
                "register - register a new user.\n" + EscapeSequences.RESET_TEXT_UNDERLINE +
                ">> register <username> <password> <email>\n" + EscapeSequences.SET_TEXT_UNDERLINE +
                "login - login as an existing user.\n" + EscapeSequences.RESET_TEXT_UNDERLINE +
                ">> login <username> <password>\n" + EscapeSequences.SET_TEXT_UNDERLINE +
                "quit - exit the chess client\n" + EscapeSequences.RESET_TEXT_UNDERLINE +
                ">> quit";
        System.out.println(EscapeSequences.SET_TEXT_COLOR_YELLOW + help);
    }

    private void displayLoggedInHelpMessage() {
        String help = "Commands and Usages:\n" + EscapeSequences.SET_TEXT_UNDERLINE +
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
        System.out.println(EscapeSequences.SET_TEXT_COLOR_YELLOW + help);
    }

    private String[] parse(String input) {
        String[] args = input.split(" ");
        if (!validBasicCommands.contains(args[0])){
            throw new InvalidCommandException("Error: invalid command. Type 'help' for more info");
        } else if (!validateArgs(args)) {
            throw new InvalidCommandException("Invalid usage for command: " + args[0]);
        }
        return args;
    }

    private boolean validateArgs(String[] args) {
        return switch (args[0]) {
            case "h", "help", "q", "quit" -> args.length == 1;
            case "l", "login" -> args.length == 3;
            case "r", "register" -> args.length == 4;
            default -> false;
        };
    }
}
