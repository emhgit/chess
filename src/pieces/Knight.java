package pieces;

import game.BoardManager;
import game.Game;
import game.GameUtils;

public class Knight extends Piece{
    public Knight( Piece.Color color, String position){
        super(Piece.Type.KNIGHT, color, position);
    }
 
    public boolean canMove(int[] current, int[] end, BoardManager boardManager, Game game){
    
    
        System.out.println("Attempting to move piece: " + getColor() + " " +  getType() + " from " + getPosition() + " to " + GameUtils.notationFromPosition(end));

        if(PieceUtils.isMoveValid(current, end) == true){
            Piece targetPiece = boardManager.getPieceAt(end);

            //If rank changes by 2 and column by 1 or rank changes by 1 and column by 2, return true
            if((Math.abs(current[0] - end[0]) == 2 && Math.abs(current[1] - end[1]) == 1) || 
            (Math.abs(current[0] - end[0]) == 1 && Math.abs(current[1] - end[1]) == 2)){
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