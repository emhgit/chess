package game;

import java.util.ArrayList;

import pieces.*;

public class Game implements Runnable{
    //CLASSES
    BoardManager boardManager;
    
    //BOOLEANS
    private boolean gameOver;
    private boolean isKingInCheck = false;

    //COLOR **Use enum from piece class for modularity
    //private static final int WHITE = 1;
    //private static final int BLACK = 0;
    //private int currentColor = WHITE;

    private Piece.Color WHITE = Piece.Color.WHITE;
    private Piece.Color BLACK = Piece.Color.BLACK;
    private Piece.Color currentColor = Piece.Color.WHITE;

    //COLLECTIONS
    private Piece[][] board;

    private ArrayList<String> moveLog = new ArrayList<>();

    Thread gameThread;
    ChessGUI gui;

    //CONSTRUCTOR
    public Game(){
        board = new Piece[8][8];
        boardManager = new BoardManager(this, board);
    }

    //METHODS
    public void startGame(){
        gameOver = false;
        boardManager.initializeBoard();
        gui = new ChessGUI(this);
        gameThread = new Thread(this);
        gameThread.start();  
        
        new Thread(() -> {
            while (!gameOver) {
                try {
                    Thread.sleep(500); // Reduce CPU usage
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("Game has ended.");
            gameOver(true);
           
            
        }).start();
        
    }

    @Override
    public void run(){
        //GAME LOOP
        int FPS = 60;
        double drawInterval = 1000000000/FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(!gameOver){
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime)/drawInterval;
            lastTime = currentTime;

            if(delta> 1){
                gui.repaint();
                delta--;
            }
        }
    }

    public void changePlayer(){
        if(currentColor == WHITE){
            currentColor = BLACK;
        } else {
            currentColor = WHITE;
        }
    }

    public void gameOver(boolean gameOver) {
        this.gameOver = gameOver;
        if (gui != null) {
            gui.disableBoard();
        }
    }

    public Piece.Color getCurrentColor(){
        return currentColor;
    }

    public ArrayList<String> getMoveLog(){
        return moveLog;
    }

    public void setGameOver(boolean gameOver){
        this.gameOver = gameOver;
    }

    public boolean isGameOver(){
        return gameOver;
    }
    
    public Piece[][] getBoard(){
        return board;
    }

    public BoardManager getBoardManager(){
        return boardManager;
}
    
    public boolean movePiece(String currentNotation, String endNotation){
        System.out.println("****movePiece()*****");
        try {
            int[] currentPosition = GameUtils.positionFromNotation(currentNotation);
            int[] endPosition = GameUtils.positionFromNotation(endNotation);
            Piece piece = boardManager.getPieceAt(currentPosition);

            if (piece == null) {
                throw new IllegalArgumentException("No piece at the starting position.");
            }
            
            // Check if the move is valid for the piece and the color matches the current turn
            if(piece.getColor() == currentColor){
                if ((piece.canMove(currentPosition, endPosition, boardManager, this)) && !GameStateEvaluator.isIllegal(piece, currentPosition, endPosition, currentColor, this)){
                    // Update position
                    if(isKingInCheck){
                        System.out.println("Your king is in check");
                    }
            
                    boardManager.executeMove(currentColor, piece, currentPosition, endPosition);

                    System.out.println(piece.getColor() + " " + piece.getType() + " has moved to " + endNotation);
                    boardManager.printBoard();

                    isKingInCheck = false;
                    boardManager.setCheckingPiece(null);

                    return true;
                } else {
                    System.out.println("Invalid move for the piece: " + piece.getColor() + " " + piece.getType());
                    return false;
                }
            } else{
                System.out.println("Not the current color's piece, returning false for move");
                return false;
            }  
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid position input. Try again.");
            return false;
        }
    }

    public boolean isKingCurrentlyInCheck(){
        return isKingInCheck;
    }

    public void setIsKingInCheck(boolean isKingCurrentlyInCheck){
        this.isKingInCheck = isKingCurrentlyInCheck;
    }
}