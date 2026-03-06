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

    private String helpMessage = HELP_MESSAGE_LOGGED_OUT;
    private final ServerFacade serverFacade;
    private String authToken = null;
    private final HashMap<Integer, GameData> games = new HashMap<>();
    private String status;


    public ChessClient(String serverUrl) {
        serverFacade = new ServerFacade(serverUrl);
        status = NOT_LOGGED_IN;
    }

    public void run(){
        repl();
    }

    private void repl(){
        Scanner scanner = new Scanner(System.in);
        String[] args;
        quit:
        while(true){
            System.out.print(EscapeSequences.SET_TEXT_COLOR_GREEN + status);
            String input = scanner.nextLine();
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
                case "observe", "o":
                    observe(args);
                    break;
                default:
                    System.out.print(EscapeSequences.SET_TEXT_COLOR_RED);
                    System.out.println("Error: '" + args[0] + "' is an invalid command. Type 'help' for more info");
            }
        }
        System.out.println(EscapeSequences.SET_TEXT_COLOR_YELLOW+ "C-ya!");
        System.exit(0);
    }



    private void help() {
        System.out.println(EscapeSequences.SET_TEXT_COLOR_YELLOW + helpMessage);
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
                helpMessage = HELP_MESSAGE_LOGGED_IN;
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
                helpMessage = HELP_MESSAGE_LOGGED_IN;
            }
        } catch (DataAccessException ex){
            printErrorToUser(ex, "login");
        }
    }

    private void logout() {
        try {
            serverFacade.logout(authToken);
            helpMessage = HELP_MESSAGE_LOGGED_OUT;
            status = NOT_LOGGED_IN;
            authToken = null;
            System.out.println(EscapeSequences.SET_TEXT_COLOR_YELLOW + "Logout Successful!");
        } catch (DataAccessException ex) {
            printErrorToUser(ex, "logout");
        }
    }

    private void list() {
        try {
            Collection<GameData> recentGames = serverFacade.listGames(authToken);
            if (recentGames.isEmpty()){
                games.clear();
                System.out.println(EscapeSequences.SET_TEXT_COLOR_YELLOW + "No games yet! use 'create <name>' to create one!");
                return;
            }
            GameData[] recentGamesArray = recentGames.toArray(new GameData[0]);
            for (int i = 0; i < recentGames.size(); i++){
                games.put(i+1, recentGamesArray[i]);
            }
            drawer.listGames(games);
        } catch (DataAccessException ex){
            printErrorToUser(ex, "list");
        }
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
            System.out.printf(EscapeSequences.SET_TEXT_COLOR_YELLOW + "%s created!%n", args[1]);
        } catch (DataAccessException ex){
            printErrorToUser(ex, "create");
        }
    }

    private void join(String[] args) {
        String color;
        int gameID;
        try {
            color = args[2].toUpperCase();
            gameID = Integer.parseInt(args[1]);
        } catch (NumberFormatException ex){
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Invalid usage for command: '" + args[0] + "'. Type 'help' for more info");
            return;
        } catch (ArrayIndexOutOfBoundsException ex){
            color = null;
            gameID = -1;
        }
        JoinRequest joinRequest = new JoinRequest(color, gameID);
        try {
            serverFacade.joinGame(authToken, joinRequest);
            System.out.printf(EscapeSequences.SET_TEXT_COLOR_YELLOW + "Joined game %d as %s%n", gameID, color);
            draw(gameID, color);
        } catch (DataAccessException ex){
            printErrorToUser(ex, "join");
        }
    }

    private void observe(String[] args){
        int gameID;
        try {
            gameID = Integer.parseInt(args[1]);
        } catch (NumberFormatException ex){
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Invalid usage for command: '" + args[0] + "'. Type 'help' for more info");
            return;
        } catch (ArrayIndexOutOfBoundsException ex){
            gameID = -1;
        }
        draw(gameID, "white");
    }

    private void clear() {
        if (authToken != null) {
            logout();
        }
        try {
            serverFacade.clear();
        } catch (Exception ex) {
            System.out.println("clear unsuccessful");
        }
    }

    private String[] parse(String input) {
        String[] args = input.split(" ");
        if (!checkMaxArgs(args[0], Arrays.copyOfRange(args, 1, args.length))){
            throw new InvalidCommandException("Invalid usage for command: '" + args[0] + "'. Type 'help' for more info");
        }
        return args;
    }

    private boolean checkMaxArgs(String cmd, String[] args) {
        return switch (cmd){
            case "r", "register" -> args.length <= 3;
            case "l", "login", "j", "join" -> args.length <= 2;
            case "c", "create", "o", "observe" -> args.length <= 1;
            case "clear", "q", "quit", "list", "h", "help", "logout" -> args.length == 0;
            default -> true;
        };
    }

    private void printErrorToUser(DataAccessException ex, String command) {
        String errorMessage = ex.getMessage();
        switch (ex.httpCode){
            case 400:
                if (Objects.equals(command, "join")){
                    errorMessage = ex.getMessage();
                } else {
                    errorMessage = "Invalid usage for command: '" + command + "'. Type 'help' for more info";
                }
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
                } else if (Objects.equals(command, "join")) {
                    errorMessage = "Color already taken.";
                }
                break;
            case 500:
                errorMessage = "Internal server error. Whoops!";
                break;
        }
        System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + errorMessage);
    }

    private void draw(int gameID, String color) {
        try {
            System.out.println(drawer.drawBoard(games.get(gameID).game().getBoard(), color.toUpperCase()));
        } catch (NullPointerException e) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_RED + "Use 'list' first after a game has been created to see games to join!");
        }

    }
}
