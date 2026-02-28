package client;

import exception.DataAccessException;
import model.GameData;
import model.UserData;
import model.request.JoinRequest;
import model.request.LoginRequest;
import server.ServerFacade;
import ui.ChessBoardDrawer;
import ui.EscapeSequences;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

import static ui.HelpfulStrings.*;

public class ChessClient {
    private final ChessBoardDrawer drawer = new ChessBoardDrawer();
    private String loggedInStatus;

    private String helpMessage = helpMessageLoggedOut;
    private final ServerFacade serverFacade;
    private String authToken = "";
    private ArrayList<GameData> games = new ArrayList<>();
    private String status;


    public ChessClient(String serverUrl) {
        serverFacade = new ServerFacade(serverUrl);
        status = notLoggedInStatus;
    }

    public void run(){
        replNotLoggedIn();
    }

    private void replNotLoggedIn(){
        quit:
        while(true){
            System.out.print(EscapeSequences.SET_TEXT_COLOR_GREEN);
            System.out.print(status);
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            String[] args = input.split(" ");
            try {
                args = parse(input);
            } catch (InvalidCommandException ex){

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
                    try {
                        int gameID = serverFacade.createGame(authToken, args[1]);
                        System.out.printf("%s created with Game No. %d%n", args[1], gameID);
                    } catch (DataAccessException ex){
                        printErrorToUser(ex, "create");
                    }
                    break;
                case "list":
                    list();
                    break;
                case "join", "j":
                    JoinRequest joinRequest = new JoinRequest(args[2].toUpperCase(), Integer.parseInt(args[1]));
                    try {
                        serverFacade.joinGame(authToken, joinRequest);
                    } catch (DataAccessException ex){
                        printErrorToUser(ex, "join");
                    }
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
            System.out.printf("Now logged in as %s!%n", userData.username());
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
                System.out.printf("Logged in as %s!%n", loginRequest.username());
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
            authToken = null;
        } catch (DataAccessException ex) {
            printErrorToUser(ex, "quit");
        }
    }

    private void list() {
        try {
            games = (ArrayList<GameData>) serverFacade.listGames(authToken);
            for (GameData game : games){
                System.out.printf(EscapeSequences.SET_TEXT_COLOR_MAGENTA + "Game No. %d%n", game.gameID());
                System.out.printf(EscapeSequences.SET_TEXT_COLOR_BLUE + "%s%n", game.gameName());
                System.out.printf(EscapeSequences.SET_TEXT_COLOR_WHITE + "White: %s%n", game.whiteUsername());
                System.out.printf("\u001b[38;5;94m" + "Black: %s%n", game.blackUsername());
            }
        } catch (DataAccessException ex){
            printErrorToUser(ex, "list");
        }
    }

    private void printErrorToUser(DataAccessException ex, String command) {
        String errorMessage = ex.getMessage();
        switch (ex.httpCode){
            case 400:
                errorMessage = "Invalid usage for command: " + command + ". try 'help'";
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

    private String[] parse(String input) {
        String[] args = input.split(" ");
        if (!validBasicCommands.contains(args[0])){
            throw new InvalidCommandException("Error: " + args[0] + " is an invalid command. Type 'help' for more info");
        }
        return args;
    }

    private boolean checkMaxArgs(String cmd, String[] args) {
        return switch (cmd){
            case "register" -> args.length <= 3;
            case "login", "join" -> args.length <= 2;
            case "create" -> args.length <= 1;
            case "clear", "quit", "list" -> args.length == 0;
            default -> true;
        };
    }
}
