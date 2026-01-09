package chess.moves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class KingMovesCalculator extends MovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition pos) {
        ArrayList<ChessMove> legalMoves = new ArrayList<>();
        int row = pos.getRow();
        int col = pos.getColumn();
        ChessGame.TeamColor color = board.getPiece(pos).getTeamColor();

        for (int i : new int[]{-1,0,1}){
            for (int j : new int[]{-1,0,1}){
                ChessPosition potentialPos = new ChessPosition(row+i, col+j);
                if (!isInBounds(row+i, col+j) || isBlocked(board, potentialPos, color) || (i==0 && j==0)){
                    continue;
                }
                legalMoves.add(new ChessMove(pos, potentialPos, null));
            }
        }
        return legalMoves;
    }
}
