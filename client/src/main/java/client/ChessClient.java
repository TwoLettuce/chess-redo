package client;

import chess.ChessGame;
import ui.ChessBoardDrawer;
import ui.EscapeSequences;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class ChessClient {
    private final ChessBoardDrawer drawer = new ChessBoardDrawer();
    private String status = "Not Logged in >> ";
    private final ArrayList<String> validBasicCommands = new ArrayList<>(List.of("h", "help", "q", "quit", "l", "login", "r", "register"));

    public ChessClient() {
        replNotLoggedIn();
    }

    private void replNotLoggedIn(){
        while(true){
            System.out.print(EscapeSequences.SET_TEXT_COLOR_GREEN);
            System.out.print(status);
            String[] args;
            Scanner scanner = new Scanner(System.in);
            String input = scanner.next();
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
                    break;
                case "h","help":
                    displayHelpMessage();
                    case ""

            }
            if (Objects.equals(args[0], "quit")){
                break;
            } else {
                System.out.println("Hi! not implemented :P");
            }
        }


    }

    private String[] parse(String input) {
        String[] args = input.split(" ");
        if (!validBasicCommands.contains(args[0])){
            throw new InvalidCommandException("Error: invalid command");
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
