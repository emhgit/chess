package game;

import java.util.ArrayList;

import pieces.*;
import pieces.Piece.Color;

public class BoardManager{
    private Piece[][] board;
    private ArrayList<Piece> pieces;
    private Piece checkingPiece;
    private Piece castlingPiece;

    private String whiteKingPosition;
    private String blackKingPosition;
     private boolean kingsideCastle;
    private boolean queensideCastle;

    public BoardManager(Game game, Piece[][] board){
            this.board = board;

            whiteKingPosition = "e1"; //remove king posiiton logic in piece class & handle it in game class
            blackKingPosition = "e8";
        }

    public void initializeBoard() {
        initializePawns(1, Piece.Color.BLACK);
        initializePawns(6, Piece.Color.WHITE);
        initializeHeavyPieces(0, Piece.Color.BLACK);
        initializeHeavyPieces(7, Piece.Color.WHITE);
        setPiecesInArray();
    }

    public void setPiecesInArray(){
        pieces = new ArrayList<>();
        for(Piece[] rank: board){
            for(Piece piece : rank){
                if(piece != null){
                    pieces.add(piece);
                }
            }
        }
    }

    public ArrayList<Piece> getPieces(){
        return pieces;
    }

    private void initializePawns(int rank, Piece.Color color) {
        for (int file = 0; file < 8; file++) {
            String position = "" + (char) ('a' + file) + (8 - rank);
            board[rank][file] = new Pawn(color, position);
        }
    }

    private void initializeHeavyPieces(int rank, Piece.Color color) {
        String[] positions = {"a", "b", "c", "d", "e", "f", "g", "h"};
        Piece[] pieces = {
            new Rook(color, positions[0] + (8 - rank)),
            new Knight(color, positions[1] + (8 - rank)),
            new Bishop(color, positions[2] + (8 - rank)),
            new Queen(color, positions[3] + (8 - rank)),
            new King(color, positions[4] + (8 - rank)),
            new Bishop(color, positions[5] + (8 - rank)),
            new Knight(color, positions[6] + (8 - rank)),
            new Rook(color, positions[7] + (8 - rank))
        };
    
        for (int file = 0; file < 8; file++) {
            board[rank][file] = pieces[file];
        }
    }
    
    public Piece[][] getBoard(){
        return board;
    }

    public Piece getPieceAt(String desiredPosition){
        int[] position = GameUtils.positionFromNotation(desiredPosition);
        if(getPieceAt(position) == null){
            System.out.println("No piece at " + desiredPosition + ", returning null.");
            return null;
        } else{
            Piece piece = getPieceAt(position);
            return piece;
        }
    }

    public Piece getPieceAt(int[] position){
        return board[position[0]][position[1]];
    }

    public Piece getCheckingPiece(){
        return checkingPiece;
    }

    public void setCheckingPiece(Piece checkingPiece){
        this.checkingPiece = checkingPiece;
    }

    public Piece getCastlingPiece(){
        return castlingPiece;
    }

    public void setCastlingPiece(Piece castlingPiece){
        this.castlingPiece = castlingPiece;
    }

     public String getKingPosition(Piece.Color color){
        return color == Color.WHITE ? this.whiteKingPosition : this.blackKingPosition;
    }

    public void setKingPosition(Piece.Color color, String kingPosition){
        if(color == Piece.Color.WHITE){
            this.whiteKingPosition = kingPosition;
        } else{
            this.blackKingPosition = kingPosition;
        }
    }

    public void setPiecesInArray(ArrayList<Piece> pieces){
        this.pieces = pieces;
    }

    public void executeMove(Piece.Color color, Piece piece, int[] start, int[] end) {
        System.out.println("******executeMove()");
        if(piece.getTargetPiece() != null){
            int[] targetPosition = GameUtils.positionFromNotation(piece.getTargetPiece().getPosition());
            board[targetPosition[0]][targetPosition[1]] = null;
            pieces.remove(piece.getTargetPiece());
            System.out.println(piece.getType() + " captured " + piece.getTargetPiece().getType() + " at " + GameUtils.notationFromPosition(end));
        }

        if(castlingPiece != null){
            if(kingsideCastle){
                int[] kingsideRookPosition = {start[0] , start[1] + 3};
                int[] finalKingsideRookPosition = {start[0], start[1] + 1};
                
                board[finalKingsideRookPosition[0]][finalKingsideRookPosition[1]] = castlingPiece;
                System.out.println("Before Move: " + castlingPiece.getType() + " at " + castlingPiece.getPosition());
                castlingPiece.setPosition(GameUtils.notationFromPosition(finalKingsideRookPosition));
                System.out.println("After Move: " + castlingPiece.getType() + " at " + castlingPiece.getPosition());
                board[kingsideRookPosition[0]][kingsideRookPosition[1]] = null;

                System.out.println("Rook Moved to kingside");
            } else{
                int[] queensideRookPosition = {start[0] , start[1] -4};
                int[] finalQueensideRookPosition = {start[0], start[1] - 1};
                
                board[finalQueensideRookPosition[0]][finalQueensideRookPosition[1]] = castlingPiece;
                System.out.println("Before Move: " + castlingPiece.getType() + " at " + castlingPiece.getPosition());
                castlingPiece.setPosition(GameUtils.notationFromPosition(finalQueensideRookPosition));
                System.out.println("After Move: " + castlingPiece.getType() + " at " + castlingPiece.getPosition());
                board[queensideRookPosition[0]][queensideRookPosition[1]] = null;

                System.out.println("Rook Moved to queenside");
            }
        }
        
        board[start[0]][start[1]] = null;
        board[end[0]][end[1]] = piece;
        piece.setPosition(GameUtils.notationFromPosition(end));
        piece.setHasMoved(true);
        printBoard();
        System.out.println("Move for " + piece.getType()  + " has been executed.");

         // Ensure King is not accidentally overwritten
         if (piece.getType() == Piece.Type.KING) {
            setKingPosition(color, GameUtils.notationFromPosition(end));
            System.out.println(color + " king position has been set to: " + getKingPosition(color));
        }
    }

 public void printBoard() {
        for (int rank = 0; rank < board.length; rank++) {  // Iterate over ranks (rows)
            for (int file = 0; file < board[rank].length; file++) {  // Iterate over files (columns)
                if (board[rank][file] == null) {
                    System.out.print(".   ");  // Print null if there's no piece
                } else {
                    if(board[rank][file].getType() == Piece.Type.KNIGHT){
                     System.out.print("N" + "   ");  // Print the type of the piece
                    } else{
                        System.out.print(board[rank][file].getType().name().substring(0, 1) + "   ");  // Print the type of the piece
                    }
                }
            }
            System.out.println();  // Move to the next line after printing a row
        }
    } 

    public boolean isKingsideCastle(){
        return kingsideCastle;
    }

    public boolean isQueensideCastle(){
        return queensideCastle;
    }

    public void setKingsideCastle(boolean kingsideCastle){
    this.kingsideCastle = kingsideCastle;
    }

    public void setQueensideCastle(boolean queensideCastle){
    this.queensideCastle = queensideCastle;
    }

}