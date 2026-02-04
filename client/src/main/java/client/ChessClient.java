package client;

import chess.ChessGame;
import ui.ChessBoardDrawer;

public class ChessClient {

    public ChessClient() {
        replNotLoggedIn();
    }

    private void replNotLoggedIn(){
        ChessGame game = new ChessGame();
        ChessBoardDrawer drawer = new ChessBoardDrawer();
        drawer.drawBoard(game.getBoard(), game.getTeamTurn());
    }
}
