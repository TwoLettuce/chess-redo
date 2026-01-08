package chess.moves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

public abstract class MovesCalculator {
    public abstract Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition pos);

    boolean isInBounds(int row, int col) {
        return !(row > 8 || row < 1 || col > 8 || col < 1);
    }

    boolean isBlocked(ChessBoard board, ChessPosition potentialPos, ChessGame.TeamColor color){
        return board.getPiece(potentialPos) != null && board.getPiece(potentialPos).getTeamColor() == color;
    }
}
