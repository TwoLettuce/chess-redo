package chess;

import chess.moves.*;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    PieceType type;
    ChessGame.TeamColor color;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.type = type;
        color = pieceColor;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return color;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if (board.getPiece(myPosition) == null){
            return List.of();
        }
        return switch (board.getPiece(myPosition).getPieceType()) {
            case PAWN -> new PawnMovesCalculator().pieceMoves(board, myPosition);
            case ROOK -> new RookMovesCalculator().pieceMoves(board, myPosition);
            case KNIGHT -> new KnightMovesCalculator().pieceMoves(board, myPosition);
            case BISHOP -> new BishopMovesCalculator().pieceMoves(board, myPosition);
            case QUEEN -> new QueenMovesCalculator().pieceMoves(board, myPosition);
            case KING -> new KingMovesCalculator().pieceMoves(board, myPosition);
        };
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return type == that.type && color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, color);
    }
}
