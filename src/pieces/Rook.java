package pieces;

import game.BoardManager;
import game.Game;
import game.GameUtils;

public class Rook extends Piece {
    public Rook(Piece.Color color, String position) {
        super(Piece.Type.ROOK, color, position);
    }

    public boolean canMove(int[] current, int[] end, BoardManager boardManager, Game game) {
    
    
        System.out.println("Attempting to move piece: " + getColor() + " " +  getType() + " from " + getPosition() + " to " + GameUtils.notationFromPosition(end));

        if (PieceUtils.isMoveValid(current, end)) {
            Piece targetPiece = boardManager.getPieceAt(end);

            // If the rank or file stays the same, return true
            if ((current[0] == end[0] || current[1] == end[1]) && !PieceUtils.pieceisOnStraightLine(current, end, boardManager)) {
                if (targetPiece == null) {
                    setHasMoved(true);
                    return true;
                } else if (targetPiece != null) {
                    if (targetPiece.getColor() != this.getColor()) {
                        setHasMoved(true);
                        isCapture = true;
                        targetPiece = game.getBoardManager().getPieceAt(end);
                        setTargetPiece(targetPiece);

                        return true;
                    }
                }
            }
        }
        System.out.println(this.getColor() + " " + this.getType() + " at " + GameUtils.notationFromPosition(current) + " cannot move to " + GameUtils.notationFromPosition(end));
        return false;
    }
}