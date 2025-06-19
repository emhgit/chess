package pieces;

import java.net.URL;
import java.util.ArrayList;

import javax.swing.ImageIcon;

import game.BoardManager;
import game.Game;
import game.GameUtils;

abstract public class Piece {
    public enum Type{
        KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN
    }

    public enum Color{
        WHITE, BLACK
    }
    
    //VARIABLE DECLARATION
    private Piece targetPiece;
    protected BoardManager boardManager;

    private Type type;
    private Color color;
    private String position;
   
    private boolean hasMoved;
    private boolean kingsideCastle;
    private boolean queensideCastle;
    protected boolean canPromote;
    protected boolean twoStepped;
    protected boolean isCapture;

    private int squareSize = 75;
    protected PieceLabel pieceLabel;
    protected ImageIcon image;

    //CONSTRUCTOR
    public Piece(Type type, Color color, String position){
        this.type = type;
        this.color = color;
        this.position = position;
        
       
        hasMoved = false;
        pieceLabel = new PieceLabel(this);
    }

    //METHODS
    public Type getType() { return type; }
    public Color getColor() { return color; }
    public String getPosition() { return position; }
    public boolean getHasMoved() { return hasMoved; }
    public PieceLabel getPieceLabel() { return pieceLabel; }
    public boolean hasTwoStepped(){ return twoStepped; }
    public boolean isCapture(){ return isCapture;}
    public boolean canPromote(){ return canPromote; }
    

    public int getX(){
        return getFile() * squareSize;
    }

    public int getY(){
        return getRank() * squareSize;
    }

    public int getRank(){
        int[] pieceCoordinates = GameUtils.positionFromNotation(getPosition());
        int y = pieceCoordinates[0];
        return y;
    }

    public int getFile(){
        int[] pieceCoordinates = GameUtils.positionFromNotation(getPosition());
        int x = pieceCoordinates[1];
        return x;
    }

    public ImageIcon getImage(){
        String fileName = "res/"+color+"_"+type+".png";
    
        URL imageUrl = getClass().getClassLoader().getResource(fileName);
        
        if (imageUrl != null) {
            this.image = new ImageIcon(imageUrl);
        } else {
            System.out.println("Error: Image not found - " + fileName);
        }

        return image;
    }

    public int[] getPositionArray(){
        int[] currentPosition = GameUtils.positionFromNotation(position);
        return currentPosition;
        
    }

    public void setCapture(boolean capture){
        this.isCapture = capture;
    }
    public void setPosition(String position){
        this.position = position;
    }

    public void setHasMoved(boolean hasMoved){
        this.hasMoved = hasMoved;
    }

    public void setCanPromote(boolean canPromote){
        this.canPromote = canPromote;
    }

    public void setHasTwoStepped(boolean hasTwoStepped){
        twoStepped = hasTwoStepped;
    }

    public boolean canMove(int[] current, int[] end, BoardManager boardManager, Game game){
        return false;
    }

    public ArrayList<int[]> getAllPossibleMoves(BoardManager boardManager, Game game){
        ArrayList<int[]> validMoves = new ArrayList<>();

        // Get the current position of the piece
        String currentNotation = this.getPosition();
        int[] currentPosition = GameUtils.positionFromNotation(currentNotation);

        // Iterate through every position on the board
        for (int file = 0; file < 8; file++) {
            for (int rank = 0; rank < 8; rank++) {
                int[] targetPosition = {file, rank};
                
                // Check if the piece can move to this position
                if (this.canMove(currentPosition, targetPosition, boardManager, game)) {
                    validMoves.add(targetPosition);
                }
            }
        }
        
        return validMoves;
    }

    protected boolean canCastle(int[] current, int[] end, Game game){
        Piece[][] simulationBoard = GameUtils.copyBoard(game.getBoard());
        BoardManager boardManager = new BoardManager(game, simulationBoard);
        kingsideCastle = false;
        queensideCastle = false;    

        int[] kingsideRookPosition = {current[0] , current[1] + 3};
        int[] queensideRookPosition = {current[0] , current[1] -4};

        //KINGSIDE CASTLE
        if(end[1] == current[1] +2){
            Piece kingsideRook = boardManager.getPieceAt(kingsideRookPosition);
            if (kingsideRook == null || kingsideRook.getHasMoved()) {
                System.out.println("Invalid kingside castling");
                return false; // Invalid castling
            }

            if(!this.hasMoved && !boardManager.getPieceAt(kingsideRookPosition).getHasMoved()){
                if(simulationBoard[current[0]][current[1] + 1] == null &&
                simulationBoard[current[0]][current[1] + 2] == null){
                    int[] fileplus1 = new int[]{current[0], current[1] + 1};
                    int[] fileplus2 = new int[]{current[0], current[1] + 2};
                    System.out.println("Checking if kingside castling squares are safe...");
                    for(Piece[] rank: simulationBoard){
                        for(Piece piece : rank){
                            if (piece != null && piece.getColor() != simulationBoard[current[0]][current[1]].getColor() && piece.getType() != type) {
                                //Check if piece can move to king position
                                int[] attackingPiecePosition = GameUtils.positionFromNotation(piece.getPosition());
                                if(piece.canMove(attackingPiecePosition, fileplus1, boardManager, game)){
                                    System.out.println(piece.getColor() + " " + piece.getType() + " would check the king at file " + fileplus1[1] + ", cannot kingside castle");
                                    return false; // King would be in check
                                }

                                if(piece.canMove(attackingPiecePosition, fileplus2, boardManager, game)){
                                    System.out.println(piece.getColor() + " " + piece.getType() + " would check the king at file " + fileplus2[1] + ", cannot kingside castle");
                                    return false; // King would be in check
                                }
                            }    
                        }
                    }
                    System.out.println("Kingside Castling...");
                    kingsideCastle = true;
                    game.getBoardManager().setKingsideCastle(kingsideCastle);
                    game.getBoardManager().setCastlingPiece(kingsideRook);
                    return true;// King can kingside castle
                }
            }
        }
        
        //QUEENSIDE CASTLE
        else if(end[1] == current[1] -2){
            Piece queensideRook = boardManager.getPieceAt(queensideRookPosition);
            if (queensideRook == null || queensideRook.getHasMoved()) {
                System.out.println("Invalid queenside castling");
                return false; // Invalid castling
            }

            if(!this.hasMoved && !boardManager.getPieceAt(queensideRookPosition).getHasMoved()){
                if(simulationBoard[current[0]][current[1] - 1] == null &&
                simulationBoard[current[0]][current[1] - 2] == null){
                    
                    int[] fileMinus1 = new int[]{current[0], current[1] - 1};
                    int[] fileMinus2 = new int[]{current[0], current[1] - 2};

                    //Check if castling squares are safe
                    System.out.println("Checking if queenside castling squares are safe...");
                    for(Piece[] rank: simulationBoard){
                        for(Piece piece : rank){
                            if (piece != null && piece.getColor() != simulationBoard[current[0]][current[1]].getColor() && piece.getType() != type) {
                                //Check if piece can move to king position
                                int[] attackingPiecePosition = GameUtils.positionFromNotation(piece.getPosition());
                                if(piece.canMove(attackingPiecePosition, fileMinus1, boardManager, game)){
                                    System.out.println(piece.getColor() + " " + piece.getType() + " would check the king at file " + fileMinus1[1] + ", cannot queenside castle");
                                    return false; // King would be in check
                                }

                                if(piece.canMove(attackingPiecePosition, fileMinus2, boardManager, game)){
                                    System.out.println(piece.getColor() + " " + piece.getType() + " would check the king at file " + fileMinus2[1] + ", cannot queenside castle");
                                    return false; // King would be in check
                                }
                            }    
                        }
                    }
                    System.out.println("Queenside Castling...");
                    queensideCastle = true;
                    
                    game.getBoardManager().setQueensideCastle(queensideCastle);
                    game.getBoardManager().setCastlingPiece(queensideRook);
                    return true;//King can queenside castle
                }
            }
        }

        System.out.println(this.getColor() + " " + this.getType() + " cannot castle. Returning false...");
        kingsideCastle = false;
        queensideCastle = false;     
        return false;
    }

    public void setTargetPiece(Piece targetPiece){
        this.targetPiece = targetPiece;
    }

    public Piece getTargetPiece(){
        return targetPiece;
    }
}