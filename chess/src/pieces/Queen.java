package pieces;

import game.BoardManager;
import game.Game;
import game.GameUtils;

public class Queen extends Piece{
    public Queen(Piece.Color color, String position){
        super(Piece.Type.QUEEN, color, position);
    }

    public boolean canMove(int[] current, int[] end, BoardManager boardManager, Game game){
    
    
        System.out.println("Attempting to move piece: " + getColor() + " " +  getType() + " from " + getPosition() + " to " + GameUtils.notationFromPosition(end));

        if(PieceUtils.isMoveValid(current, end)){
            Piece targetPiece = boardManager.getPieceAt(end);

            //Vertical && Horizontal Movement
            if((current[0] == end[0] || current[1] == end[1]) && !PieceUtils.pieceisOnStraightLine(current, end, boardManager)){
                if(targetPiece == null){
                        return true; 
                    } else if (targetPiece != null) {
                        if (targetPiece.getColor() != this.getColor()) {
                            isCapture = true;
                            targetPiece = game.getBoardManager().getPieceAt(end);
                            setTargetPiece(targetPiece);

                            return true;
                    } 
                } 
            }
            //Diagonal Movement
            if(Math.abs(current[0] - end[0]) == Math.abs(current[1] - end[1]) && !PieceUtils.pieceisOnDiagonalLine(current, end, boardManager)){
                if(targetPiece == null){
                    return true; 
                } else if (targetPiece != null) {
                    if (targetPiece.getColor() != this.getColor()) {
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