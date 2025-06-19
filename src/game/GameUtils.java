package game;

import java.util.ArrayList;

import pieces.*;

public class GameUtils{

    public static int[] positionFromNotation(String notation){
            // Validate the input
            if (notation == null || notation.length() != 2) {
                throw new IllegalArgumentException("Invalid chess notation");
            }
        
            // Extract file and rank
            char file = notation.charAt(0); // 'a' to 'h'
            char rank = notation.charAt(1); // '1' to '8'
        
            // Validate file and rank
            if (file < 'a' || file > 'h' || rank < '1' || rank > '8') {
                throw new IllegalArgumentException("Invalid chess notation");
            }
        
            // Convert file and rank to array indices
            int[] position = new int[2];
            position[1] = file - 'a';       // File ('a' -> 0, 'b' -> 1, ..., 'h' -> 7)
            position[0] = 7 - (rank - '1'); // Rank ('1' -> 7, '2' -> 6, ..., '8' -> 0)
        
            return position;
        }
    
        public static String notationFromPosition(int[] position){
            StringBuilder notation = new StringBuilder();
    
            //Validate the input
            if(position == null || position.length != 2){
                throw new IllegalArgumentException("Invalid chess position");
            }
    
            // Extract file and rank
            char file = (char) ('a' + position[1]); // 'a' to 'h'
            char rank = (char) ('8' - position[0]); // '1' to '8'
    
            //Validate file and rank
            if(file < 'a' || file > 'h' || rank < '1' || rank > '8'){
                throw new IllegalArgumentException("Invalid chess notation. file: " + file + " rank: " + rank);
            }
    
            //Appending file and rank to string builder
            notation.append(file);
            notation.append(rank);
    
            return notation.toString();
        }

        public static String convertToAlgebraic(Piece piece, int[] start, int[] end, boolean isCapture, boolean isCheck, boolean isMate, Piece.Type choice, Game game) {
            StringBuilder notation = new StringBuilder();
        
            // Add piece type
            if (piece.getType() != Piece.Type.PAWN && (!game.getBoardManager().isKingsideCastle() || !game.getBoardManager().isQueensideCastle())) {
                if(piece.getType() == Piece.Type.KNIGHT){
                    notation.append("N");
                } else{
                    notation.append(piece.getType().name().charAt(0)); // e.g., 'K' for King
                }
            }
        
            // Handle captures
            if (isCapture) {
                if (piece.getType() == Piece.Type.PAWN) {
                    notation.append((char) ('a' + start[1])); // File of the pawn
                }
                notation.append("x");
            }
        
            // Destination square
            if(!game.getBoardManager().isKingsideCastle() || !game.getBoardManager().isQueensideCastle()){
                notation.append((char) ('a' + end[1])).append(8 - end[0]);
            }
        
            // Handle special cases
            if (piece.canPromote()) {
                switch(choice){
                    case Piece.Type.QUEEN: notation.append("=Q"); break;
                    case Piece.Type.ROOK: notation.append("=R"); break;
                    case Piece.Type.BISHOP: notation.append("=B"); break;
                    case Piece.Type.KNIGHT: notation.append("=N"); break;
                    case Piece.Type.PAWN: break;
                    case Piece.Type.KING: break;
                }
            }
            if (isCheck) {
                notation.append("+");
            }
            if (isMate) {
                notation.append("#");
            }
            if(game.getBoardManager().isKingsideCastle()){
                notation.delete(0, notation.length());
                notation.append("O-O");
            }
            if(game.getBoardManager().isQueensideCastle()){
                notation.delete(0, notation.length());
                notation.append("O-O-O");
            }
        
            return notation.toString();
        }
    
        /*public void printMoveLog() {
            for (int i = 0; i < moveLog.size(); i++) {
                if (i % 2 == 0) {
                    System.out.print((i / 2 + 1) + ". "); // Add turn number
                }
    
                // Print the current move (either White's or Black's move)
                System.out.print(moveLog.get(i) + " ");
    
                
                if (i % 2 != 0) {
                    System.out.println(); // New line after each pair of moves
                }
            }
        }*/
    
    public static Piece[][] copyBoard(Piece[][] originalBoard) {
            Piece[][] copy = new Piece[8][8];
            for (int rank = 0; rank < originalBoard.length; rank++) {
                for (int file = 0; file < originalBoard[rank].length; file++) {
                    if (originalBoard[rank][file] != null) {
                        Piece originalPiece = originalBoard[rank][file];
                        // Clone the piece (create a new instance with the same attributes)
                        copy[rank][file] = clonePiece(originalPiece);
                    }
                }
            }
            return copy;
        }

        public static ArrayList<Piece> copyPieces(ArrayList<Piece> ogPieces){
            ArrayList<Piece> piecesCopy = new ArrayList<>();
            for(Piece piece: ogPieces){
                if(piece != null){
                    piecesCopy.add(clonePiece(piece));
                }
            }
            return piecesCopy;
        }
    
        public static Piece clonePiece(Piece original) {
            switch(original.getType()){
                case KING:
                    return new King(original.getColor(), original.getPosition()); 
                case QUEEN:
                    return new Queen(original.getColor(), original.getPosition()); 
                case ROOK:
                    Piece rook = new Rook(original.getColor(), original.getPosition()); 
                    rook.setHasMoved(original.getHasMoved());
                    return rook;
                case BISHOP:
                    return new Bishop(original.getColor(), original.getPosition()); 
                case KNIGHT:
                    return new Knight(original.getColor(), original.getPosition()); 
                case PAWN: 
                    Piece pawn = new Pawn(original.getColor(), original.getPosition());
                    pawn.setHasMoved(original.getHasMoved());
                    pawn.setHasTwoStepped(original.hasTwoStepped());
                    return pawn;
                default: System.out.println("No piece type found in clonePiece(), returning null. "); return null;
            }
        } 
    }