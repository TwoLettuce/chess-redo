package chess.moves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class RookMovesCalculator extends MovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition pos) {
        ArrayList<ChessMove> legalMoves = new ArrayList<>();
        int row = pos.getRow();
        int col = pos.getColumn();
        ChessGame.TeamColor color = board.getPiece(pos).getTeamColor();

        row++;
        while (row <= 8){
            ChessPosition potentialPos = new ChessPosition(row, col);
            if(!isInBounds(row, col) || isBlocked(board, potentialPos, color)){
                break;
            } else {
                legalMoves.add(new ChessMove(pos, potentialPos, null));
                if (board.getPiece(potentialPos) != null){
                    break;
                }
            }
            row++;
        }

        row = pos.getRow();
        col = pos.getColumn();
        col++;
        while (col <= 8){
            ChessPosition potentialPos = new ChessPosition(row, col);
            if(!isInBounds(row, col) || isBlocked(board, potentialPos, color)){
                break;
            } else {
                legalMoves.add(new ChessMove(pos, potentialPos, null));
                if (board.getPiece(potentialPos) != null){
                    break;
                }
            }
            col++;
        }

        row = pos.getRow();
        col = pos.getColumn();
        row--;
        while (row >= 1){
            ChessPosition potentialPos = new ChessPosition(row, col);
            if(!isInBounds(row, col) || isBlocked(board, potentialPos, color)){
                break;
            } else {
                legalMoves.add(new ChessMove(pos, potentialPos, null));
                if (board.getPiece(potentialPos) != null){
                    break;
                }
            }
            row--;
        }

        row = pos.getRow();
        col = pos.getColumn();
        col--;
        while (col >= 1){
            ChessPosition potentialPos = new ChessPosition(row, col);
            if(!isInBounds(row, col) || isBlocked(board, potentialPos, color)){
                break;
            } else {
                legalMoves.add(new ChessMove(pos, potentialPos, null));
                if (board.getPiece(potentialPos) != null){
                    break;
                }
            }
            col--;
        }
        return legalMoves;
    }
}
