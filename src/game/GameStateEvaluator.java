package game;

import java.util.ArrayList;
import java.util.Arrays;

import pieces.*;

public class GameStateEvaluator{

    public static boolean isCheckmate(Piece.Color color, Game game){
        System.out.println("****isCheckmate()*****\n");

        Piece[][] simulationBoard = GameUtils.copyBoard(game.getBoardManager().getBoard());
        BoardManager boardManager = new BoardManager(game, simulationBoard);
        boardManager.setPiecesInArray();
        ArrayList<Piece> pieces = boardManager.getPieces();
        System.out.println(pieces.toString());
        Piece.Color nextColor = color == Piece.Color.WHITE ? Piece.Color.BLACK : Piece.Color.WHITE;

        Piece king = boardManager.getPieceAt(game.getBoardManager().getKingPosition(color));
        String ogKingPosition = game.getBoardManager().getKingPosition(color);
        boardManager.setKingPosition(color, ogKingPosition);
        boardManager.setKingPosition(nextColor, game.getBoardManager().getKingPosition(nextColor));
        int[] kingPosition = GameUtils.positionFromNotation(ogKingPosition);

        //check if king has legal moves
        if(!kingCanMove(kingPosition, king, color, game)){
            Piece checkingPiece = game.getBoardManager().getCheckingPiece();

            int[] checkingPiecePosition = GameUtils.positionFromNotation(checkingPiece.getPosition());

            //get the abs value checking pieces rank & file minus the king's rank & file
            int rankDifference = Math.abs(checkingPiecePosition[0] - kingPosition[0]);
            int fileDifference = Math.abs(checkingPiecePosition[1] - kingPosition[1]);
            
            //if the ranks are equal to 0, then its a vertical attack
            if(fileDifference == 0){
                //checking piece is above king
                if(checkingPiecePosition[0] < kingPosition[0]){
                    for(int rank = checkingPiecePosition[0]; rank < kingPosition[0]; rank++){
                        int[] endPosition = {rank, kingPosition[1]};
                        for(Piece piece: pieces){
                                if(piece.getColor() == color && piece.canMove(GameUtils.positionFromNotation(piece.getPosition()), endPosition, boardManager, game)&& piece.getType() != Piece.Type.KING){
                                    System.out.println(piece.getPosition() +piece.getType() + " can block the upper check, so this is not checkmate.");
                                    return false;
                            }
                        }
                    }
                }
                //checking piece is below king
                if(checkingPiecePosition[0] > kingPosition[0]){
                    for(int rank = checkingPiecePosition[0]; rank > kingPosition[0]; rank--){
                        int[] endPosition = {rank, kingPosition[1]};
                        for(Piece piece: pieces){
                                if(piece.getColor() == color && piece.canMove(GameUtils.positionFromNotation(piece.getPosition()), endPosition, boardManager, game)&& piece.getType() != Piece.Type.KING){
                                    System.out.println(piece.getPosition() +piece.getType() + " can block the lower check, so this is not checkmate.");
                                    return false;
                            }
                        }
                    }
                }
            } 
            //if the file is equal to 0, its a horizontal attack
            else if(rankDifference == 0){
                //checking piece is left of king
                if(checkingPiecePosition[1] < kingPosition[1]){
                    for(int file = checkingPiecePosition[1]; file < kingPosition[1]; file++){
                        int[] endPosition = {kingPosition[0], file};
                        for(Piece piece: pieces){
                                if(piece.getColor() == color && piece.canMove(GameUtils.positionFromNotation(piece.getPosition()), endPosition, boardManager, game)&& piece.getType() != Piece.Type.KING){
                                    System.out.println(piece.getPosition() +piece.getType() + " can block the left check, so this is not checkmate.");
                                    return false;
                                }
                            }
                        }
                    }
                    //checking piece is right of king
                if(checkingPiecePosition[1] > kingPosition[1]){
                    for(int file = checkingPiecePosition[1]; file > kingPosition[1]; file--){
                        int[] endPosition = {kingPosition[0], file};
                        for(Piece piece: pieces){
                                if(piece.getColor() == color && piece.canMove(GameUtils.positionFromNotation(piece.getPosition()), endPosition, boardManager, game)&& piece.getType() != Piece.Type.KING){
                                    System.out.println(piece.getPosition() +piece.getType() + " can block the right check, so this is not checkmate.");
                                    return false;
                                }
                            }
                        }
                    }
                }
                    //if the rank & file are equal then its a diagonal attack
                    else if(rankDifference == fileDifference){
                    //up left
                    if(checkingPiecePosition[0] < kingPosition[0] && checkingPiecePosition[1] < kingPosition[1]){
                        for(int rank = checkingPiecePosition[0], file = checkingPiecePosition[1]; rank < kingPosition[0]; rank++, file++){
                            int[] endPosition = {rank, file};
                            for(Piece piece: pieces){
                                    if(piece.getColor() == color && piece.canMove(GameUtils.positionFromNotation(piece.getPosition()), endPosition, boardManager, game)&& piece.getType() != Piece.Type.KING){
                                        System.out.println(piece.getPosition() +piece.getType() + " can block the upper left check, so this is not checkmate.");
                                        return false;
                                    }
                            }
                    }
                }
                    //up right
                    if(checkingPiecePosition[0] < kingPosition[0] && checkingPiecePosition[1] > kingPosition[1]){
                            for(int rank = checkingPiecePosition[0], file = checkingPiecePosition[1]; rank < kingPosition[0]; rank++, file--){
                            int[] endPosition = {rank, file};
                            for(Piece piece: pieces){
                                    if(piece.getColor() == color && piece.canMove(GameUtils.positionFromNotation(piece.getPosition()), endPosition, boardManager, game)&& piece.getType() != Piece.Type.KING){
                                        System.out.println(piece.getPosition() +piece.getType() + " can block the upper right check, so this is not checkmate.");
                                        return false;
                                    }
                            }              
                    }
                }
                    //bottom left
                    if(checkingPiecePosition[0] > kingPosition[0] && checkingPiecePosition[1] < kingPosition[1]){
                            for(int rank = checkingPiecePosition[0], file = checkingPiecePosition[1]; rank > kingPosition[0]; rank--, file++){
                            int[] endPosition = {rank, file};
                            for(Piece piece: pieces){
                                    if(piece.getColor() == color && piece.canMove(GameUtils.positionFromNotation(piece.getPosition()), endPosition, boardManager, game) && piece.getType() != Piece.Type.KING){
                                        System.out.println(piece.getPosition() +piece.getType() + " can block the lower left check, so this is not checkmate.");
                                        return false;
                                    }
                            }                
                    }
                }
                    //bottom right
                    if(checkingPiecePosition[0] > kingPosition[0] && checkingPiecePosition[1] > kingPosition[1]){
                            for(int rank = checkingPiecePosition[0], file = checkingPiecePosition[1]; rank > kingPosition[0]; rank--, file--){
                            int[] endPosition = {rank, file};
                            for(Piece piece: pieces){
                                    if(piece.getColor() == color && piece.canMove(GameUtils.positionFromNotation(piece.getPosition()), endPosition, boardManager, game) && piece.getType() != Piece.Type.KING){
                                        System.out.println(piece.getPosition() + piece.getType() + " can block the lower right check, so this is not checkmate.");
                                        return false;
                                    }
                            }                 
                    }
                }
            }
                //else, its a knight attack
                else{
                    System.out.println("KNIGHT has checkmated the king.");
                    return true;
                }
            } else{
                System.out.println("King can move, this is not checkmate.");
                return false;
            }
            System.out.println("King is in checkmate, returning true.");
            return true;
        }   
    
    public static boolean isIllegal(Piece movingPiece, int[] currentPosition, int[] endPosition, Piece.Color color, Game game){
        System.out.println("****isIllegal()*****");

        Piece[][] simulationBoard = GameUtils.copyBoard(game.getBoardManager().getBoard());
        BoardManager boardManager = new BoardManager(game, simulationBoard);
        boardManager.setPiecesInArray();
        boardManager.setKingPosition(Piece.Color.WHITE, game.getBoardManager().getKingPosition(Piece.Color.WHITE));
        boardManager.setKingPosition(Piece.Color.BLACK, game.getBoardManager().getKingPosition(Piece.Color.BLACK));
        ArrayList<Piece> pieces = boardManager.getPieces();

        boardManager.executeMove(color, movingPiece, currentPosition, endPosition);

        for(Piece piece: pieces){
            if(piece != null){
                if(piece.getColor() != color){
                    if(movingPiece.getTargetPiece() != null){
                        if(piece.getPosition() == movingPiece.getTargetPiece().getPosition()){
                            System.out.println("skipping target piece: "+ movingPiece.getTargetPiece().getType());
                            continue;
                        }
                    }

                    int[] currentPiecePosition = piece.getPositionArray();
                    if(piece.canMove(currentPiecePosition, GameUtils.positionFromNotation(boardManager.getKingPosition(color)), boardManager, game)){
                        System.out.println(piece.getColor() + " " + piece.getType() + " can attack the other king. " + GameUtils.notationFromPosition(endPosition) + " is an illegal move.");
                        return true;
                    }
                }
            }
        }
        
        return false;
    }

    public static boolean isKingInCheck(Piece.Color color, Game game){
        System.out.println("****isKingInCheck()*****\n");

        Piece[][] simulationBoard = GameUtils.copyBoard(game.getBoardManager().getBoard());
        BoardManager boardManager = new BoardManager(game, simulationBoard);
        boardManager.setPiecesInArray();
        ArrayList<Piece> pieces = boardManager.getPieces();
        Piece.Color nextColor = color == Piece.Color.WHITE ? Piece.Color.BLACK : Piece.Color.WHITE;


        Piece king = boardManager.getPieceAt(game.getBoardManager().getKingPosition(color));
        int[] kingPosition = GameUtils.positionFromNotation(king.getPosition());

        boardManager.setKingPosition(color, game.getBoardManager().getKingPosition(color));
        boardManager.setKingPosition(nextColor, game.getBoardManager().getKingPosition(nextColor));

        for(Piece piece : pieces){
            int[] currentPosition = GameUtils.positionFromNotation(piece.getPosition());
            if(piece.getColor() != color && piece.canMove(currentPosition, kingPosition, boardManager, game) && piece.getType() != Piece.Type.KING){
                System.out.println("Checking piece: " + piece.getColor() + " " + piece.getType());
                game.getBoardManager().setCheckingPiece(piece);
                return true;
            }
        }
        System.out.println(color + " KING isn't in check.");
        return false;
    }

    //TO DO
    public static boolean isStalemate(Piece.Color color, Game game){
        System.out.println("****isStalemate()*****\n");

        Piece[][] simulationBoard = GameUtils.copyBoard(game.getBoardManager().getBoard());
        BoardManager boardManager = new BoardManager(game, simulationBoard);
        boardManager.setPiecesInArray();
        ArrayList<Piece> pieces = boardManager.getPieces();
        Piece.Color nextColor = color == Piece.Color.WHITE ? Piece.Color.BLACK : Piece.Color.WHITE;

        boardManager.setKingPosition(color, game.getBoardManager().getKingPosition(color));
        boardManager.setKingPosition(nextColor, game.getBoardManager().getKingPosition(nextColor));
        Piece king = boardManager.getPieceAt(boardManager.getKingPosition(color));
        System.out.println("king is null: " + (king == null));

        int[] kingPosition = GameUtils.positionFromNotation(boardManager.getKingPosition(color));
        boolean canKingMove = kingCanMove(kingPosition, king, color, game);

        int count = 0;
        int otherCount = 0;
        
        for(Piece piece: pieces){
            if(piece.getColor() == color){
                count++;
            } else{
                otherCount++;
            }
        }

        if(count == 1 && otherCount == 1){
            System.out.println("Only kings are on the board, stalemate!");
            return true;
        }

        if(!canKingMove){
            if(count == 1){
                return true;
            } else{
                for(Piece piece : pieces){
                    if(piece.getColor() == color){
                        ArrayList<int[]> allPossibleMoves = piece.getAllPossibleMoves(boardManager, game);
                        for(int[] move: allPossibleMoves){
                            if(piece.canMove(piece.getPositionArray(), move, boardManager, game)){
                                System.out.println(color + " " + piece.getType() + " can move, this is not stalemate.");
                                return false;
                            }
                        }
                    }
                }
            }
        } else{
            System.out.println("king can move, returning false");
            return false;
        }

        return true;
    }

    //change to kingCanMove()
    private static boolean kingCanMove(int[] kingPosition, Piece king, Piece.Color currentColor, Game game) {
        System.out.println("****kingCanMove()*****\n");
        
        BoardManager boardManager = game.getBoardManager();
        // Generate all possible king moves (8 surrounding squares)
        int[][] kingMoves = {
            {kingPosition[0] - 1, kingPosition[1] - 1}, {kingPosition[0] - 1, kingPosition[1]},
            {kingPosition[0] - 1, kingPosition[1] + 1}, {kingPosition[0], kingPosition[1] - 1},
            {kingPosition[0], kingPosition[1] + 1}, {kingPosition[0] + 1, kingPosition[1] - 1},
            {kingPosition[0] + 1, kingPosition[1]}, {kingPosition[0] + 1, kingPosition[1] + 1}
        };

        
        System.out.println("Checking to see if king can move...");
        for (int[] move : kingMoves) {
            try{
                System.out.println("King pos: " + Arrays.toString(kingPosition) + " " + GameUtils.notationFromPosition(kingPosition));
                if(king.canMove(kingPosition, move, boardManager, game)) {
                    // Simulate the move
                    boolean isMoveIllegal = isIllegal(king, kingPosition, move, king.getColor(), game);
        
                    if (!isMoveIllegal) {
                        System.out.println("The King can escape with move: " + GameUtils.notationFromPosition(move));
                        return true; // The king can escape
                    } else{
                        System.out.println("Move was illegal, trying next move.");
                    }
                }
            } catch(IllegalArgumentException e){
                System.out.println(e);
                continue;
            }
        }
        
        System.out.println(king.getColor() + " " + king.getType() + " cannot move");
        
        return false; // No valid king moves found
    }
}