package client;

import exception.DataAccessException;
import model.GameData;
import model.UserData;
import model.request.JoinRequest;
import model.request.LoginRequest;
import server.ServerFacade;
import ui.ChessBoardDrawer;
import ui.EscapeSequences;

import java.util.*;

import static ui.HelpfulStrings.*;

public class ChessClient {
    private final ChessBoardDrawer drawer = new ChessBoardDrawer();
    private String loggedInStatus;

    private String helpMessage = helpMessageLoggedOut;
    private final ServerFacade serverFacade;
    private String authToken = null;
    private Collection<GameData> games = new ArrayList<>();
    private String status;


    public ChessClient(String serverUrl) {
        serverFacade = new ServerFacade(serverUrl);
        status = notLoggedInStatus;
    }

    public void run(){
        repl();
    }

    private void repl(){
        quit:
        while(true){
            System.out.print(EscapeSequences.SET_TEXT_COLOR_GREEN);
            System.out.print(status);
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            String[] args;
            try {
                args = parse(input);
            } catch (InvalidCommandException ex){
                System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + ex.getMessage());
                continue;
            }
            switch (args[0]){
                case "q","quit":
                    if(authToken != null){
                        logout();
                    }
                    break quit;
                case "h","help":
                    help();
                    break;
                case "l","login":
                    login(args);
                    break;
                case "r", "register":
                    register(args);
                    break;
                case "clear":
                    clear();
                    break;
                case "logout":
                    logout();
                    break;
                case "create", "c":
                    create(args);
                    break;
                case "list":
                    list();
                    break;
                case "join", "j":
                    join(args);
                    break;
                default:
                    System.out.print(EscapeSequences.SET_TEXT_COLOR_RED);
                    System.out.println("Error: " + args[0] + " is an invalid command. Type 'help' for more info");
            }
        }
        System.out.println(EscapeSequences.SET_TEXT_COLOR_YELLOW+ "C-ya!");
        System.exit(0);
    }



    private void help() {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_YELLOW + helpMessage);
    }

    private void clear() {
        try {
            serverFacade.clear();
        } catch (Exception ex) {
            System.out.println("clear unsuccessful");
        }
    }

    private void register(String[] args) {
        UserData userData;
        try {
            userData = new UserData(args[1], args[2], args[3]);
        } catch (ArrayIndexOutOfBoundsException ex){
            userData = new UserData(null, null, null);
        }

        try {
            authToken = serverFacade.register(userData);
            System.out.printf(EscapeSequences.SET_TEXT_COLOR_YELLOW + "Now logged in as %s!%n", userData.username());
            if (authToken != null) {
                loggedInStatus = String.format("[%s] >> ", userData.username());
                status = loggedInStatus;
                helpMessage = helpMessageLoggedIn;
            }
        } catch (DataAccessException ex){
            printErrorToUser(ex, "register");
        }
    }

    private void login(String[] args) {
        LoginRequest loginRequest;
        try {
            loginRequest = new LoginRequest(args[1], args[2]);
        } catch (ArrayIndexOutOfBoundsException ex) {
            loginRequest = new LoginRequest(null, null);
        }

        try {
            authToken = serverFacade.login(loginRequest);
            if (authToken != null) {
                System.out.printf(EscapeSequences.SET_TEXT_COLOR_YELLOW + "Logged in as %s!%n", loginRequest.username());
                loggedInStatus = String.format("[%s] >> ", loginRequest.username());
                status = loggedInStatus;
                helpMessage = helpMessageLoggedIn;
            }
        } catch (DataAccessException ex){
            printErrorToUser(ex, "login");
        }
    }

    private void logout() {
        try {
            serverFacade.logout(authToken);
            helpMessage = helpMessageLoggedOut;
            status = notLoggedInStatus;
            authToken = null;
            System.out.println(EscapeSequences.SET_TEXT_COLOR_YELLOW + "Logout Successful!");
        } catch (DataAccessException ex) {
            printErrorToUser(ex, "logout");
        }
    }

    private void list() {
        try {
            games = serverFacade.listGames(authToken);
            drawer.listGames(games);
            if (games.isEmpty()){
                System.out.println(EscapeSequences.SET_TEXT_COLOR_YELLOW + "No games yet! use 'create <name>' to create one!");
            }
        } catch (DataAccessException ex){
            printErrorToUser(ex, "list");
        }
    }
    
    private void join(String[] args) {
        JoinRequest joinRequest = new JoinRequest(args[2].toUpperCase(), Integer.parseInt(args[1]));
        try {
            serverFacade.joinGame(authToken, joinRequest);
        } catch (DataAccessException ex){
            printErrorToUser(ex, "join");
        }
    }

    private void printErrorToUser(DataAccessException ex, String command) {
        String errorMessage = ex.getMessage();
        switch (ex.httpCode){
            case 400:
                errorMessage = "Invalid usage for command: '" + command + "'. Type 'help' for more info";
                break;
            case 401:
                if (Objects.equals(command, "login")){
                    errorMessage = "Invalid username or password.";
                } else {
                    errorMessage = "Please log in first!";
                }
                break;
            case 403:
                if (Objects.equals(command, "register")){
                    errorMessage = "Username already taken.";
                } else if (Objects.equals(command, "")) {
                    errorMessage = "Color already taken.";
                }
                break;
            case 500:
                errorMessage = "Internal server error. Whoops!";
                break;
        }
        System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + errorMessage);
    }

    private void create(String[] args){
        try {
            String gameName;
            try {
                gameName = args[1];
            }catch (ArrayIndexOutOfBoundsException ex){
                gameName = null;
            }
            int gameID = serverFacade.createGame(authToken, gameName);
            System.out.printf("%s created with Game No. %d%n", args[1], gameID);
        } catch (DataAccessException ex){
            printErrorToUser(ex, "create");
        }
    }

    private String[] parse(String input) {
        String[] args = input.split(" ");
        if (!validBasicCommands.contains(args[0])){
            throw new InvalidCommandException("Error: '" + args[0] + "' is an invalid command. Type 'help' for more info");
        } else if (!checkMaxArgs(args[0], Arrays.copyOfRange(args, 1, args.length))){
            throw new InvalidCommandException("Invalid usage for command: '" + args[0] + "'. Type 'help' for more info");
        }
        return args;
    }

    private boolean checkMaxArgs(String cmd, String[] args) {
        return switch (cmd){
            case "register" -> args.length <= 3;
            case "login", "join" -> args.length <= 2;
            case "create", "observe" -> args.length <= 1;
            case "clear", "quit", "list", "help", "logout" -> args.length == 0;
            default -> false;
        };
    }
}
