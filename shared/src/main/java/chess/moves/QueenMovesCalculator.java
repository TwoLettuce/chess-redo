package chess.moves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class QueenMovesCalculator extends MovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition pos) {
        Collection<ChessMove> diagonals = new BishopMovesCalculator().pieceMoves(board, pos);
        Collection<ChessMove> cardinals = new RookMovesCalculator().pieceMoves(board, pos);
        Collection<ChessMove> legalMoves = diagonals;
        legalMoves.addAll(cardinals);
        return legalMoves;
    }
}
