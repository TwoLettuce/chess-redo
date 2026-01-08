package chess.moves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class KnightMovesCalculator extends MovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition pos) {
        ArrayList<ChessMove> legalMoves = new ArrayList<>();
        int row = pos.getRow();
        int col = pos.getColumn();
        ChessGame.TeamColor color = board.getPiece(pos).getTeamColor();

        for (int rowOffset : new int[]{-2, -1, 1, 2}){
            for (int colOffset : new int[]{-2, -1, 1, 2}){
                if (Math.abs(rowOffset) != Math.abs(colOffset)){
                    if (!isInBounds(row+rowOffset, col+colOffset)){
                        continue;
                    }
                    ChessPosition potentialPos = new ChessPosition(row + rowOffset, col + colOffset);
                    if (isBlocked(board, potentialPos, color)){
                        continue;
                    } else {
                        legalMoves.add(new ChessMove(pos, potentialPos, null));
                    }

                }
            }
        }
        return legalMoves;
    }
}
