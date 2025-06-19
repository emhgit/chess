package pieces;

import java.util.Arrays;

import game.BoardManager;

public class PieceUtils{
    public static boolean isMoveValid(int[] current, int[] end){
            if(isWithinBoard(end) == true && isSameSquare(current, end) == false) return true;
            else return false;
        }
    
        private static boolean isWithinBoard(int[] end){
            if((end[0] < 0 || end[0] > 7 ) || end[1] < 0 || end[1] > 7 ) return false;
            else return true;
        }
    
        public static boolean isSameSquare(int[] current, int[] end){
            return Arrays.equals(current, end);
        }
    
    public static boolean pieceisOnStraightLine(int[] current, int[] end, BoardManager boardManager){
            Piece[][] board = boardManager.getBoard();
    
            // Check if piece is on horizontal line
            if (current[0] == end[0]) {
                int startFile = Math.min(current[1], end[1]);
                int endFile = Math.max(current[1], end[1]);
                for (int file = startFile + 1; file < endFile; file++) {
                    if (board[current[0]][file] != null) {
                        return true;
                    }
                }
            }
        
            // Check if piece is on vertical line
            if (current[1] == end[1]) {
                int startRank = Math.min(current[0], end[0]);
                int endRank = Math.max(current[0], end[0]);
                for (int rank = startRank + 1; rank < endRank; rank++) {
                    if (board[rank][current[1]] != null) {
                        System.out.println("Found Piece on vertical at " + current[1] + " "+ rank);
                        return true;
                    }
                }
            }
        
            return false;
        }
    
        public static boolean pieceisOnDiagonalLine(int[] current, int[] end, BoardManager boardManager){
            Piece[][] simulationBoard = boardManager.getBoard();
            boardManager.printBoard();
    
            // Check if the move is diagonal
            if (Math.abs(current[0] - end[0]) == Math.abs(current[1] - end[1])) {
                int fileDirection = (end[0] - current[0]) > 0 ? 1 : -1;
                int rankDirection = (end[1] - current[1]) > 0 ? 1 : -1;
    
                int file = current[0] + fileDirection;
                int rank = current[1] + rankDirection;
    
                while (file != end[0] && rank != end[1]) {
                    if (simulationBoard[file][rank] != null) {
                        System.out.println("Found Piece on diagonal at " + file + " " + rank);
                        return true;
                    }
                    file += fileDirection;
                    rank += rankDirection;
                }
            }
    
            return false;
        }
    }
    