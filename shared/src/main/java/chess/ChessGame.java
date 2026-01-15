package chess;

import chess.moves.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    ChessBoard board;
    TeamColor teamTurn;
    public ChessGame() {
        teamTurn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        if (board.getPiece(startPosition) == null){
            return null;
        }
        ChessBoard currentBoard = new ChessBoard(board);
        TeamColor color = board.getPiece(startPosition).getTeamColor();
        ArrayList<ChessMove> validMoves = new ArrayList<>();
        for (var move : Objects.requireNonNull(getPotentialMoves(startPosition))){
            board.makeMove(move);
            if (!isInCheck(color) && !isInCheckmate(color)){
                validMoves.add(move);
            }
            board = new ChessBoard(currentBoard);
        }

        return validMoves;


    }

    private Collection<ChessMove> getPotentialMoves(ChessPosition startPos) {
        switch (board.getPiece(startPos).getPieceType()){
            case KNIGHT -> {return new KnightMovesCalculator().pieceMoves(board, startPos);}
            case PAWN -> {return new PawnMovesCalculator().pieceMoves(board, startPos);}
            case ROOK -> {return new RookMovesCalculator().pieceMoves(board, startPos);}
            case QUEEN -> {return new QueenMovesCalculator().pieceMoves(board, startPos);}
            case BISHOP -> {return new BishopMovesCalculator().pieceMoves(board, startPos);}
            case KING -> {return new KingMovesCalculator().pieceMoves(board, startPos);}
            default -> {return null;}
        }
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (board.getPiece(move.getStartPosition()) == null){
            throw new InvalidMoveException("Invalid Move: " + move);
        }
        if (teamTurn != board.getPiece(move.getStartPosition()).getTeamColor()){
            throw new InvalidMoveException("Not your turn!");
        }
        if (validMoves(move.getStartPosition()).contains(move)){
            board.makeMove(move);
            flipTeamTurn();
        } else {
            throw new InvalidMoveException("Invalid Move: " + move);
        }
    }

    private void flipTeamTurn() {
        if (teamTurn == TeamColor.WHITE){
            teamTurn = TeamColor.BLACK;
        } else {
            teamTurn = TeamColor.WHITE;
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = findKing(teamColor);
        for (int i = 1; i <=8; i++){
            for (int j = 1; j <=8; j++){
                var thisPos = new ChessPosition(i, j);
                if (determineCheck(thisPos, kingPos)){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean determineCheck(ChessPosition pos, ChessPosition kingPos){
        if (board.getPiece(pos) != null && board.getPiece(pos).getTeamColor() != board.getPiece(kingPos).getTeamColor()){
            for (ChessMove move : Objects.requireNonNull(getPotentialMoves(pos))){
                if (move.getEndPosition().equals(kingPos)){
                    return true;
                }
            }
        }
        return false;
    }

    private ChessPosition findKing(TeamColor teamColor) {
        for (int i = 1; i <=8; i++){
            for (int j = 1; j <=8; j++){
                var thisPos = new ChessPosition(i, j);
                var thisPiece = board.getPiece(thisPos);
                if (thisPiece != null && thisPiece.getPieceType() == ChessPiece.PieceType.KING && thisPiece.getTeamColor() == teamColor){
                    return thisPos;
                }
            }
        }
        return null;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && !hasValidMove(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && !hasValidMove(teamColor);
    }


    private boolean hasValidMove(TeamColor teamColor){
        for (int i = 1; i <= 8; i++){
            for (int j = 1; j <=8; j++){
                ChessPosition thisPos = new ChessPosition(i, j);
                if (board.getPiece(thisPos) != null && board.getPiece(thisPos).getTeamColor() == teamColor){
                    if (!validMoves(thisPos).isEmpty()){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = new ChessBoard(board);
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, teamTurn);
    }
}
