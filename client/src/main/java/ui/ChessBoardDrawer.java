package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.GameData;

import java.lang.reflect.InaccessibleObjectException;
import java.util.HashMap;
import java.util.Objects;

public class ChessBoardDrawer {

    String emptySpace = "   ";
    char[] headerChars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
    char[] reverseHeader = {'h', 'g', 'f', 'e', 'd', 'c', 'b', 'a'};

    public String drawBoard(ChessBoard board, String team){
        ChessGame.TeamColor perspectiveOf;
        if (Objects.equals(team, "WHITE")){
            perspectiveOf = ChessGame.TeamColor.WHITE;
        } else if (Objects.equals(team, "BLACK")){
            perspectiveOf = ChessGame.TeamColor.BLACK;
        } else {
            throw new InaccessibleObjectException("Not permissible to join as " + team);
        }
        StringBuilder prettyBoard = new StringBuilder();
        prettyBoard.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
        prettyBoard.append(buildHeader(perspectiveOf));
        for (int i = 1; i <=8; i++) {
            prettyBoard.append(buildRow(board, getRowLabel(perspectiveOf), i-1));
        }
        prettyBoard.append(buildHeader(perspectiveOf));
        return prettyBoard.toString();
    }

    private String buildHeader(ChessGame.TeamColor perspectiveOf) {
        StringBuilder header = new StringBuilder();
        header.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY).append(EscapeSequences.SET_TEXT_ITALIC).append(EscapeSequences.SET_TEXT_BOLD);
        char[] headerChars;
        if (perspectiveOf== ChessGame.TeamColor.WHITE){
            headerChars=this.headerChars;
        } else {
            headerChars=reverseHeader;
        }
        header.append(emptySpace);
        for (char colHeader : headerChars) {
            header.append(" ").append(colHeader).append(" ");
        }
        header.append(emptySpace);
        header.append(EscapeSequences.RESET_BG_COLOR)
                .append(EscapeSequences.RESET_TEXT_ITALIC)
                .append(EscapeSequences.RESET_TEXT_BOLD_FAINT)
                .append("\n");
        return header.toString();
    }

    private int[] getRowLabel(ChessGame.TeamColor perspectiveOf) {
        return perspectiveOf==ChessGame.TeamColor.WHITE ? new int[]{8, 7, 6, 5, 4, 3, 2, 1} : new int[]{1, 2, 3, 4, 5, 6, 7, 8};
    }

    private String buildRow(ChessBoard board, int[] rowLabels, int rowNumber) {
        StringBuilder row = new StringBuilder();
        row.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY).append(EscapeSequences.SET_TEXT_ITALIC).append(EscapeSequences.SET_TEXT_BOLD);
        row.append(" ").append(rowLabels[rowNumber]).append(" ");
        row.append(EscapeSequences.RESET_TEXT_ITALIC).append(EscapeSequences.RESET_TEXT_BOLD_FAINT);


        for (int i = 1; i<=8; i++){
            ChessPiece thisPiece = board.getPiece(new ChessPosition(rowLabels[rowNumber], rowLabels[8-i]));
            boolean isWhiteSquare = i % 2 + new int[]{8, 7, 6, 5, 4, 3, 2, 1}[rowNumber] % 2 == 1;
            String pieceAsString;
            if (thisPiece != null){
                pieceAsString = thisPiece.toString();
                if (thisPiece.getTeamColor() == ChessGame.TeamColor.WHITE){
                    row.append(EscapeSequences.SET_TEXT_COLOR_YELLOW);
                } else {
                    row.append("\u001b[38;5;55m");
                }
            } else {
                pieceAsString = " ";
            }
            if (isWhiteSquare){
                row.append("\u001b[48;5;249m");
                row.append(" ").append(pieceAsString).append(" ");
            } else {
                row.append("\u001b[48;5;94m");
                row.append(" ").append(pieceAsString).append(" ");
            }
        }

        row.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
        row.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY).append(EscapeSequences.SET_TEXT_ITALIC).append(EscapeSequences.SET_TEXT_BOLD);
        row.append(" ").append(rowLabels[rowNumber]).append(" ");
        row.append(EscapeSequences.RESET_BG_COLOR).append(EscapeSequences.RESET_TEXT_BOLD_FAINT).append(EscapeSequences.RESET_TEXT_ITALIC);
        row.append("\n");
        return row.toString();
    }

    public void listGames(HashMap<Integer, GameData> games){
        for (Integer gameNo : games.keySet()){
            GameData game = games.get(gameNo);
            System.out.printf(EscapeSequences.SET_TEXT_COLOR_MAGENTA + "Game No. %d | ", gameNo);
            System.out.printf(EscapeSequences.SET_TEXT_COLOR_BLUE + "%s | ", game.gameName());
            System.out.printf(EscapeSequences.SET_TEXT_COLOR_WHITE + "White: %s | ", game.whiteUsername());
            System.out.printf("\u001b[38;5;94m" + "Black: %s%n", game.blackUsername());
        }
    }
}
