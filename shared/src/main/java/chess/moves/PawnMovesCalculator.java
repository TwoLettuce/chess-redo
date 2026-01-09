package chess.moves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMovesCalculator extends MovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition pos) {
        ArrayList<ChessMove> legalMoves = new ArrayList<>();
        int row = pos.getRow();
        int col = pos.getColumn();
        int startingRow;
        int endingRow;
        int direction;
        ChessGame.TeamColor color = board.getPiece(pos).getTeamColor();

        if (color == ChessGame.TeamColor.WHITE){
            direction = 1;
            startingRow = 2;
            endingRow = 8;
        } else {
            direction = -1;
            startingRow = 7;
            endingRow = 1;
        }

        for (int colOffset : new int[]{-1, 0, 1}){
            if (!isInBounds(row+direction, col+colOffset)){
                continue;
            }
            ChessPosition potentialPos = new ChessPosition(row + direction, col+colOffset);
            if (colOffset != 0 && board.getPiece(potentialPos) != null && board.getPiece(potentialPos).getTeamColor() != color){
                legalMoves.add(new ChessMove(pos, potentialPos, null));
            } else if (colOffset == 0 && board.getPiece(potentialPos) == null){
                legalMoves.add(new ChessMove(pos, potentialPos, null));
                ChessPosition ffPos = new ChessPosition(row + direction*2, col);
                if (pos.getRow() == startingRow && board.getPiece(ffPos) == null){
                    legalMoves.add(new ChessMove(pos, ffPos, null));
                }
            }
        }
        ArrayList<ChessMove> legalMovesCopy = new ArrayList<>(legalMoves);

        for (var move : legalMovesCopy){
            if (move.getEndPosition().getRow() == endingRow){
                legalMoves.addAll(listOfPromotions(move));
                legalMoves.remove(move);
            }
        }
        return legalMoves;
    }

    private Collection<? extends ChessMove> listOfPromotions(ChessMove chessMove) {
        ArrayList<ChessMove> promotions = new ArrayList<>();
        for (ChessPiece.PieceType type : new ChessPiece.PieceType[]{
                ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.ROOK, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT}){
            promotions.add(new ChessMove(chessMove.getStartPosition(), chessMove.getEndPosition(), type));
        }
        return promotions;
    }
}
