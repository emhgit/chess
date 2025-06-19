package pieces;


import game.BoardManager;
import game.Game;
import game.GameUtils;

public class Pawn extends Piece {
    public Pawn(Piece.Color color, String position) {
        super(Piece.Type.PAWN, color, position);
    }

    @Override
    public boolean canMove(int[] current, int[] end, BoardManager boardManager, Game game) {
        System.out.println("Attempting to move piece: " + getColor() + " " +  getType() + " from " + getPosition() + " to " + GameUtils.notationFromPosition(end));

        if (PieceUtils.isMoveValid(current, end)) {
            Piece[][] simulationBoard = GameUtils.copyBoard(boardManager.getBoard());

            Piece targetPiece = boardManager.getPieceAt(end);

            int moveValue;
            if (this.getColor() == Piece.Color.WHITE) {
                moveValue = -1;
            } else {
                moveValue = 1;
            }

            // If the file stays the same and the rank changes by 1 or 2, return true
            if (current[1] == end[1] && end[0] == (current[0] + moveValue)) {
                if ((end[0] == 0 && this.getColor() == Piece.Color.WHITE) ||
                        (end[0] == 7 && this.getColor() == Piece.Color.BLACK)) {
                    canPromote = true;
                    return true;
                }
                if (simulationBoard[end[0]][end[1]] == null) {
                    this.setHasMoved(true);
                    return true;
                }
            } else if (this.getHasMoved() == false && current[1] == end[1] &&
                    end[0] == (current[0] + (moveValue * 2))) {
                if (simulationBoard[end[0]][end[1]] == null) {
                    this.setHasMoved(true);
                    this.setHasTwoStepped(true);
                    System.out.println("Pawn has two stepped: " + this.hasTwoStepped());
                    return true;
                }
            }
            // if rank changes by move value and the file changes by 1, capture piece and return true
            else if (end[0] == (current[0] + moveValue) && Math.abs(current[1] - end[1]) == 1) {
                System.out.println("Checking capture logic...");
                // Capture logic
                if (targetPiece != null) {
                    if (targetPiece.getColor() != this.getColor()) {
                        System.out.println(this.getType() + " Captured " + targetPiece.getType() + " at "
                                + GameUtils.notationFromPosition(end));
                        setHasMoved(true);
                        isCapture = true;
                        targetPiece = game.getBoardManager().getPieceAt(end);
                        setTargetPiece(targetPiece);
                        System.out.println("Target piece: " + targetPiece.getType());

                        if ((end[0] == 0 && this.getColor() == Piece.Color.WHITE) ||
                        (end[0] == 7 && this.getColor() == Piece.Color.BLACK)) {
                            canPromote = true;
                            return true;
                        }
                        
                        return true;
                    } 
                } 
                //en passant
                else {
                    System.out.println("End position: " + end[0] + " " + end[1]);
                    targetPiece = simulationBoard[end[0] + (-moveValue)][end[1]];
                    System.out.println(targetPiece.getColor() + " " + targetPiece.getType() + " is the target piece");
                    System.out.println("target piece has two stepped: " + targetPiece.hasTwoStepped());

                    if (targetPiece != null && targetPiece.hasTwoStepped()) {
                        int[] epp = {end[0] + (-moveValue), end[1]};
                        isCapture = true;
                        targetPiece = game.getBoardManager().getPieceAt(epp);
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