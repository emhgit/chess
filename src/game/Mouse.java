package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import pieces.PieceLabel;
import pieces.Queen;
import pieces.Rook;
import pieces.Bishop;
import pieces.Knight;
import pieces.Piece;

public class Mouse extends MouseAdapter {
    private PieceLabel selectedPiece;
    private Piece.Type choice;
    private JLayeredPane layeredPane;
    private BoardManager boardManager;
    private ChessGUI gui;
    private Game game;
    private Sound sound;

    private int offsetX, offsetY;
    private int originalX, originalY;
    private int whiteKingsideRookX = 565, whiteKingsideRookY = 565;
    private int whiteQueensideRookX = 0, whiteQueensideRookY = 565;
    private int blackKingsideRookX = 565, blackKingsideRookY = 0;
    private int blackQueensideRookX = 0, blackQueenideRookY = 0;


    JButton previousSq;
    JButton currentSq;    
    JButton releasedSq;

    public Mouse(ChessGUI gui, JLayeredPane pane, Game game) {
        this.gui = gui;
        this.layeredPane = pane;
        this.game = game;
        this.boardManager = game.getBoardManager();
        sound = new Sound();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (game.isGameOver()) return;
        // Convert mouse position to JLayeredPane coordinates
        Point point = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), layeredPane);
    
        Component clicked = layeredPane.findComponentAt(point);

        System.out.println("Moused pressed at x: " + point.x + " y: " + point.y);
        if (clicked instanceof PieceLabel) {
            selectedPiece = (PieceLabel) clicked;
            System.out.println("Selected Piece: " + selectedPiece.getPiece().getColor() + " " + selectedPiece.getPiece().getType());

            // Find the square under this piece
            int file = selectedPiece.getX() / 75;
            int rank = selectedPiece.getY() / 75;

            // Change square color
            currentSq = gui.getSquares()[rank][file];
            currentSq.setBackground(gui.selectedColor);

            layeredPane.setLayer(selectedPiece, JLayeredPane.DRAG_LAYER); // Move piece to top layer
            if(previousSq != null){
                previousSq.setBackground((previousSq.getX()/75 + previousSq.getY()/75) % 2 == 0 ? gui.light : gui.dark);
            }

            if(releasedSq != null){
                releasedSq.setBackground((releasedSq.getX() + releasedSq.getY()) % 2 == 0 ? gui.light : gui.dark);
            }
                
            // Store offset relative to where the piece was clicked
            offsetX = point.x - selectedPiece.getX();
            offsetY = point.y - selectedPiece.getY();

            // Save the original position in case of invalid move
            originalX = selectedPiece.getX();
            originalY = selectedPiece.getY();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (game.isGameOver()) return;

        if (selectedPiece != null) {
            Point newPoint = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), layeredPane);
        
            int newX = newPoint.x - offsetX;
            int newY = newPoint.y - offsetY;

            // Keep the piece inside the board
            newX = Math.max(0, Math.min(newX, 525));
            newY = Math.max(0, Math.min(newY, 525));

            selectedPiece.setLocation(newX, newY);
            layeredPane.repaint();
        }
    }



    @Override
    public void mouseReleased(MouseEvent e) {
        if (game.isGameOver()) return;
        
        if (selectedPiece != null) {
            Point releasePoint = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(), layeredPane);

            // Snap to the closest square
            int newX = (releasePoint.x / 75) * 75;
            int newY = (releasePoint.y / 75) * 75;
    

            // Keep inside bounds
            newX = Math.max(0, Math.min(newX, 525));
            newY = Math.max(0, Math.min(newY, 525));
            System.out.println("Moused released at x: " + newX + " y: " + newY);

            int file = newX / 75;
            int rank = newY/ 75;

            Piece piece = selectedPiece.getPiece();

            String currentPosition = piece.getPosition();
            System.out.println("current position: "+ currentPosition);

            int[] endCoordinates = {rank, file};
            String endPosition = GameUtils.notationFromPosition(endCoordinates);
            System.out.println("end position: "+ endPosition);

            // Validate move (for now, allow all moves)
            if (game.movePiece(currentPosition, endPosition)) {
                if(piece.getTargetPiece() != null){
                    System.out.println("Removing Target Piece: " + piece.getTargetPiece());
                    PieceLabel targetPieceLabel = piece.getTargetPiece().getPieceLabel();
                    piece.setTargetPiece(null);
                    
                    //targetPieceLabel.setLocation(0,0);
                    gui.removePieceLabel(targetPieceLabel);
                    boardManager.getPieces().remove(piece.getTargetPiece());
                    System.out.println("target label removed: " + (layeredPane.getIndexOf(targetPieceLabel) != -1));
                } else if (boardManager.getCastlingPiece() != null) {
                    if(game.getCurrentColor() == Piece.Color.WHITE){
                        if(boardManager.isKingsideCastle()){
                            // Remove any existing rook label to avoid duplicates
                            layeredPane.remove(layeredPane.getComponentAt(whiteKingsideRookX, whiteKingsideRookY));
                            layeredPane.revalidate();
                            layeredPane.repaint();
                        } else{
                            // Remove any existing rook label to avoid duplicates
                            layeredPane.remove(layeredPane.getComponentAt(whiteQueensideRookX, whiteQueensideRookY));
                            layeredPane.revalidate();
                            layeredPane.repaint();
                        }
                    } else{
                        if(boardManager.isKingsideCastle()){
                            // Remove any existing rook label to avoid duplicates
                            layeredPane.remove(layeredPane.getComponentAt(blackKingsideRookX, blackKingsideRookY));
                            layeredPane.revalidate();
                            layeredPane.repaint();
                        } else{
                            // Remove any existing rook label to avoid duplicates
                            layeredPane.remove(layeredPane.getComponentAt(blackQueensideRookX, blackQueenideRookY));
                            layeredPane.revalidate();
                            layeredPane.repaint();
                        }
                    }

                    Piece castlingRook = game.getBoardManager().getCastlingPiece();
                    PieceLabel rookLabel = castlingRook.getPieceLabel();

                    System.out.println("Castling piece: " + castlingRook.getColor() + " " + castlingRook.getType());
                    System.out.println("Rook label parent before move: " + (rookLabel.getParent() != null ? rookLabel.getParent() : "NULL"));

                    // Re-add rook label to layeredPane
                    layeredPane.add(rookLabel, JLayeredPane.PALETTE_LAYER);

                    // Calculate the correct new position
                    int[] rookNewPosition = GameUtils.positionFromNotation(castlingRook.getPosition());
                    int rookX = rookNewPosition[1] * 75;
                    int rookY = rookNewPosition[0] * 75;

                    System.out.println("Moving rook to: " + rookX + ", " + rookY);

                    // Move the rook label to its new position
                    rookLabel.setLocation(rookX, rookY);
                    rookLabel.addMouseListener(this);
                    rookLabel.addMouseMotionListener(this);

                    // Force GUI refresh
                    SwingUtilities.invokeLater(() -> {
                        layeredPane.revalidate();
                        layeredPane.repaint();
                    });

                    boardManager.setCastlingPiece(null); // Reset castling state
                }                
                   
                selectedPiece.setLocation(newX, newY);
                releasedSq = gui.getSquares()[rank][file];
                releasedSq.setBackground(gui.selectedColor);
                previousSq = currentSq;
                playSE(0);

                layeredPane.repaint();

                Piece.Color nextColor = game.getCurrentColor() == Piece.Color.WHITE ? Piece.Color.BLACK : Piece.Color.WHITE;
                game.setIsKingInCheck(GameStateEvaluator.isKingInCheck(nextColor, game)); //change to next color
                boolean isCheckmate = false;
                boolean isStalemate = false;

                if(game.isKingCurrentlyInCheck()){
                    playSE(1);
                    isCheckmate = GameStateEvaluator.isCheckmate(nextColor, game);
                    if(isCheckmate)  gui.checkmate(); game.setGameOver(isCheckmate);
                    
                } else{
                    isStalemate = GameStateEvaluator.isStalemate(nextColor, game);
                    if(isStalemate) gui.stalemate(); game.setGameOver(isStalemate);

                    //CHECK FOR PAWN PROMOTION
                    if(piece.canPromote()){
                        System.out.println("Starting promotion...");
                        choice = gui.promote(piece, GameUtils.positionFromNotation(currentPosition), endCoordinates, game);
                        Piece[][] board = boardManager.getBoard();
                        
                        switch(choice){
                            case Piece.Type.QUEEN:
                                boardManager.getPieces().remove(piece);
                                board[endCoordinates[0]][endCoordinates[1]] = new Queen(game.getCurrentColor(), endPosition);
                                boardManager.getPieces().add(board[endCoordinates[0]][endCoordinates[1]]);

                                layeredPane.remove(selectedPiece);
                                PieceLabel queenLabel = board[endCoordinates[0]][endCoordinates[1]].getPieceLabel();
                                layeredPane.add(queenLabel, JLayeredPane.PALETTE_LAYER);
                                queenLabel.setLocation(endCoordinates[1] * 75, endCoordinates[0] * 75);
                                queenLabel.addMouseListener(this);
                                queenLabel.addMouseMotionListener(this);

                                System.out.println(piece.getColor() + " " + piece.getType() + " promoted to Queen");
                                break;
                            case Piece.Type.ROOK:
                                boardManager.getPieces().remove(piece);
                                board[endCoordinates[0]][endCoordinates[1]] = new Rook(game.getCurrentColor(), endPosition);
                                boardManager.getPieces().add(board[endCoordinates[0]][endCoordinates[1]]);

                                layeredPane.remove(selectedPiece);
                                PieceLabel rookLabel = board[endCoordinates[0]][endCoordinates[1]].getPieceLabel();
                                layeredPane.add(rookLabel, JLayeredPane.PALETTE_LAYER);
                                rookLabel.setLocation(endCoordinates[1] * 75, endCoordinates[0] * 75);
                                rookLabel.addMouseListener(this);
                                rookLabel.addMouseMotionListener(this);

                                System.out.println(piece.getColor() + " " + piece.getType() + " promoted to rook");
                                break;
                            case Piece.Type.BISHOP:
                                boardManager.getPieces().remove(piece);
                                board[endCoordinates[0]][endCoordinates[1]] = new Bishop(game.getCurrentColor(), endPosition);
                                boardManager.getPieces().add(board[endCoordinates[0]][endCoordinates[1]]);

                                layeredPane.remove(selectedPiece);
                                PieceLabel bishopLabel = board[endCoordinates[0]][endCoordinates[1]].getPieceLabel();
                                layeredPane.add(bishopLabel, JLayeredPane.PALETTE_LAYER);
                                bishopLabel.setLocation(endCoordinates[1] * 75, endCoordinates[0] * 75);
                                bishopLabel.addMouseListener(this);
                                bishopLabel.addMouseMotionListener(this);

                                System.out.println(piece.getColor() + " " + piece.getType() + " promoted to bishop");
                                break;
                            case Piece.Type.KNIGHT:
                                boardManager.getPieces().remove(piece);
                                board[endCoordinates[0]][endCoordinates[1]] = new Knight(game.getCurrentColor(), endPosition);
                                boardManager.getPieces().add(board[endCoordinates[0]][endCoordinates[1]]);

                                layeredPane.remove(selectedPiece);
                                PieceLabel knightLabel = board[endCoordinates[0]][endCoordinates[1]].getPieceLabel();
                                layeredPane.add(knightLabel);
                                knightLabel.setLocation(endCoordinates[1] * 75, endCoordinates[0] * 75);
                                knightLabel.addMouseListener(this);
                                knightLabel.addMouseMotionListener(this);

                                System.out.println(piece.getColor() + " " + piece.getType() + " promoted to knight");
                                break;
                            default:
                                boardManager.getPieces().remove(piece);
                                board[endCoordinates[0]][endCoordinates[1]] = new Queen(game.getCurrentColor(), endPosition);

                                layeredPane.remove(selectedPiece);
                                PieceLabel label = board[endCoordinates[0]][endCoordinates[1]].getPieceLabel();
                                layeredPane.add(label, JLayeredPane.PALETTE_LAYER);
                                label.setLocation(endCoordinates[1] * 75, endCoordinates[0] * 75);
                                label.addMouseListener(this);
                                label.addMouseMotionListener(this);

                                System.out.println(piece.getColor() + " " + piece.getType() + " promoted to Queen");
                        }
                    }
                }
                
                System.out.println("****Converting move to algebraic notation...");
                String move = GameUtils.convertToAlgebraic(piece, piece.getPositionArray(), GameUtils.positionFromNotation(endPosition), piece.isCapture(), game.isKingCurrentlyInCheck(), isCheckmate, choice, game);
                game.getMoveLog().add(move);
                System.out.println("Move converted to algebraic notation!");
                printMoveLog();

                piece.setCapture(false);
                piece.canPromote();
                boardManager.setKingsideCastle(false);
                boardManager.setQueensideCastle(false);

                game.changePlayer();

            } else {
                // Revert to original position if the move is invalid
                selectedPiece.setLocation(originalX, originalY);
            }

            // Reset layer
            layeredPane.setLayer(selectedPiece, JLayeredPane.PALETTE_LAYER);
            selectedPiece = null;
        }
    }

    public void promote(Piece.Type type, int[] current, int[] end){
        layeredPane.getComponentAt(end[1] * 75, end[0] * 75);
    }

    public void printMoveLog() {
        gui.getTextArea().setText("");

        for (int i = 0; i < game.getMoveLog().size(); i++) {
            if (i % 2 == 0) {
                gui.getTextArea().append((i / 2 + 1) + ". ");// Add turn number
            }

            // Print the current move (either White's or Black's move)
            gui.getTextArea().append(game.getMoveLog().get(i) + " ");
            
            if (i % 2 != 0) {
                gui.getTextArea().append("\n");// New line after each pair of moves
            }
        }
    }

    public void playSE(int i){
        sound.play(i);
    }
}