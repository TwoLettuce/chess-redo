package chess;

import java.util.Arrays;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {
    private ChessPiece[][] board;

    public ChessBoard() {
        board = new ChessPiece[8][8];
    }

    public ChessBoard(ChessBoard board){
        this.board = new ChessPiece[8][8];
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                this.board[i][j] = board.getPiece(new ChessPosition(i, j));
            }
        }
    }

    public void makeMove(ChessMove move){
        var startPos = move.getStartPosition();
        var endPos = move.getEndPosition();
        var promoPiece = move.getPromotionPiece();
        if (promoPiece == null){
            board[endPos.getRow()][endPos.getColumn()] = getPiece(startPos);
        } else {
            board[endPos.getRow()][endPos.getColumn()] = new ChessPiece(getPiece(startPos).getTeamColor(), promoPiece);
        }
        board[startPos.getRow()][startPos.getColumn()] = null;
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        ChessGame.TeamColor color;
        ChessPiece.PieceType type;
        for (int row = 0; row < 8; row++){
            for (int col = 0; col < 8; col++){
                color = determinePieceColor(row);
                if (color == null){
                    continue;
                }
                type = determinePieceType(row, col);
                board[row][col] = new ChessPiece(color, type);
            }
        }
    }
    private ChessGame.TeamColor determinePieceColor(int row){
        if (row <= 1){
            return ChessGame.TeamColor.WHITE;
        } else if (row >= 6) {
            return ChessGame.TeamColor.BLACK;
        }
        return null;
    }

    private ChessPiece.PieceType determinePieceType(int row, int col){
        if (row == 1 || row == 6){
            return ChessPiece.PieceType.PAWN;
        } else {
            return switch (col) {
                case 0, 7 -> ChessPiece.PieceType.ROOK;
                case 1, 6 -> ChessPiece.PieceType.KNIGHT;
                case 2, 5 -> ChessPiece.PieceType.BISHOP;
                case 3 -> ChessPiece.PieceType.QUEEN;
                case 4 -> ChessPiece.PieceType.KING;
                default -> throw new RuntimeException();
            };
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        int hashTotal = 0;
        for (int row = 0; row < 8; row++){
            for (int col = 0; col < 8; col++){
                if (board[row][col] == null){
                    continue;
                }
                hashTotal += board[row][col].hashCode()*row*col;
            }
        }
        return hashTotal;
    }

    @Override
    public String toString() {
        StringBuilder s_board = new StringBuilder();
        for (int row = 7; row >= 0; row --){
            for (int col = 0; col < 8; col ++){
                s_board.append(pieceToChar(board[row][col])).append(" ");
            }
            s_board.append("\n");
        }
        return s_board.toString();
    }

    private String pieceToChar(ChessPiece piece){
        String c_piece;
        if (piece == null){
            return "-";
        }
        switch (piece.getPieceType()){
            case PAWN -> c_piece = "p";
            case ROOK -> c_piece = "r";
            case BISHOP -> c_piece = "b";
            case KNIGHT -> c_piece = "n";
            case KING -> c_piece = "k";
            case QUEEN -> c_piece = "q";
            default -> c_piece = "-";
        }
        if (!c_piece.equals("-") && piece.getTeamColor() == ChessGame.TeamColor.WHITE){
            return c_piece.toUpperCase();
        }
        return c_piece;
    }
}
