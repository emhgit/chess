package pieces;

import game.BoardManager;
import game.Game;
import game.GameUtils;

public class King extends Piece{
    public King(Piece.Color color, String position){
        super(Piece.Type.KING, color, position);
    }

    @Override
    public boolean canMove(int[] current, int[] end, BoardManager boardManager, Game game){
        System.out.println("Attempting to move piece: " + getColor() + " " +  getType() + " from " + 
        GameUtils.notationFromPosition(current) + " to " + GameUtils.notationFromPosition(end));
        
        if(PieceUtils.isMoveValid(current, end)){
            Piece targetPiece = boardManager.getPieceAt(end);

            //If the change in rank and file in any direction is only one, return true
            if((Math.abs(current[0] - end[0]) == 1 && current[1] == end[1]) || (Math.abs(current[1] - end[1]) == 1 && current[0] == end[0])
             || (Math.abs(current[0] - end[0]) == 1 && Math.abs(current[1] - end[1]) == 1)){
                if(targetPiece == null){
                    setTargetPiece(null);
                    return true; 
                } else if (targetPiece != null) {
                    if (targetPiece.getColor() != this.getColor()) {
                        isCapture = true;
                        targetPiece = game.getBoardManager().getPieceAt(end);
                        setTargetPiece(targetPiece);
                        System.out.println("target piece position : " + targetPiece.getPosition() + " end position: " + GameUtils.notationFromPosition(end));

                        return true;
                    } 
                } 
            } 

            System.out.println("Processing if move to " + GameUtils.notationFromPosition(end) + " for " + this.getColor() + " " + this.getType() + " is castling...");
            //Check for castling
            if((end[0] == current[0] && end[1] == current[1] +2) || (end[0] == current[0] && end[1] == current[1]-2)){
                if(canCastle(current, end, game)){
                    return true;
                }
            }
        }

        System.out.println(this.getColor() + " " + this.getType() + " at " + GameUtils.notationFromPosition(current) + " cannot move to " + GameUtils.notationFromPosition(end));
        return false;
    }
}