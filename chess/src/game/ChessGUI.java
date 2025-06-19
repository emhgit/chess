package game;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import java.awt.Color;

import pieces.Bishop;
import pieces.Knight;
import pieces.Piece;
import pieces.PieceLabel;
import pieces.Queen;
import pieces.Rook;


public class ChessGUI extends JFrame implements ActionListener{
    private Game game;
    private JButton[][] squares = new JButton[8][8];
    private JLayeredPane layeredPane;  // JLayeredPane to hold pieces above buttons
    private JPanel board;
    private JScrollPane scrollPane;
    private JTextArea textArea;
    private JButton queenButton, rookButton, bishopButton, knightButton;
    private Piece.Type choice;

    private Mouse mouse;
    public int mouseX, mouseY;
    int[] current, end;

    Font mainFont = new Font("Arial", Font.PLAIN, 24);
    public Color dark = new Color(64, 64, 64);
    public Color light = new Color(255, 255, 255);
    public Color selectedColor = new Color(177, 201, 137);



    public ChessGUI(Game game) {
        this.game = game;
    
        setTitle("Chess Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        layeredPane = new JLayeredPane();  
        layeredPane.setLayout(null); // Allows manual placement of elements
        layeredPane.setPreferredSize(new Dimension(800, 600));
        setContentPane(layeredPane);

        mouse = new Mouse(this, layeredPane, game);

        addMouseListener(mouse);
        layeredPane.addMouseListener(mouse);
        layeredPane.addMouseMotionListener(mouse);

        scrollPane = new JScrollPane();
        scrollPane.setBounds(600, 0, 200, 600);
        scrollPane.setBackground(Color.BLACK);
        scrollPane.setAlignmentX(CENTER_ALIGNMENT);
        textArea = new JTextArea();
        textArea.setFocusable(false);
        textArea.setEditable(false);
        textArea.setBounds(0,0, 200, 600);
        textArea.setBackground(Color.BLACK);
        textArea.setFont(mainFont);
        textArea.setForeground(Color.WHITE);
        scrollPane.setViewportView(textArea);
      
        layeredPane.add(scrollPane, JLayeredPane.DEFAULT_LAYER);
    
        initializeBoard();
        initializePieces();
        
        pack();
        setVisible(true);
    }

    private void initializeBoard() {
        board = new JPanel();
        board.setLayout(new GridLayout(8, 8));
        board.setBounds(0, 0, 600, 600);

        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                squares[rank][file] = new JButton();
                squares[rank][file].setPreferredSize(new Dimension(75, 75));
                squares[rank][file].setOpaque(true);
                squares[rank][file].setBorderPainted(false);
                squares[rank][file].setFocusable(false);
                squares[rank][file].setBackground((rank + file) % 2 == 0 ? light: dark);

                board.add(squares[rank][file]);
            }
        }
        layeredPane.add(board, JLayeredPane.DEFAULT_LAYER);
    }

    private void initializePieces() {
        Piece[][] board = game.getBoardManager().getBoard();
        for (int rank = 0; rank < board.length; rank++) {
            for (int file = 0; file < board[rank].length; file++) { // Fixed: use board[rank].length instead of board.length
                if (board[rank][file] != null) {
                    Piece piece = board[rank][file];
                    PieceLabel pieceLabel = piece.getPieceLabel();
                    pieceLabel.addMouseListener(mouse);
                    pieceLabel.addMouseMotionListener(mouse);
                    layeredPane.add(pieceLabel, JLayeredPane.PALETTE_LAYER);
                    System.out.println("Added piece label at: " + pieceLabel.getLocation());
                    System.out.println(pieceLabel.getParent());
                }
            }
        }
    }

    public Piece.Type promote(Piece piece, int[] current, int[] end, Game game) {
        this.current = current;
        this.end = end;
        choice = null; // Reset choice

        // Create modal dialog for promotion
        JDialog promotionDialog = new JDialog(this, "Promote Pawn", true); // 'true' makes it modal
        promotionDialog.setSize(300, 100);
        promotionDialog.setLayout(new FlowLayout());
        promotionDialog.setLocationRelativeTo(this); // Center it

        queenButton = createPromotionButton(new Queen(game.getCurrentColor(), "a1"), Piece.Type.QUEEN, promotionDialog);
        rookButton = createPromotionButton(new Rook(game.getCurrentColor(), "a1"), Piece.Type.ROOK, promotionDialog);
        bishopButton = createPromotionButton(new Bishop(game.getCurrentColor(), "a1"), Piece.Type.BISHOP, promotionDialog);
        knightButton = createPromotionButton(new Knight(game.getCurrentColor(), "a1"), Piece.Type.KNIGHT, promotionDialog);

        promotionDialog.add(queenButton);
        promotionDialog.add(rookButton);
        promotionDialog.add(bishopButton);
        promotionDialog.add(knightButton);
        promotionDialog.pack();

        promotionDialog.setVisible(true); // Show and block until a button is clicked

        return choice; // Return the user's choice after dialog is closed
    }

    public void checkmate(){
        JDialog checkmateDialog = new JDialog(this, "Game Over", true); // 'true' makes it modal
        checkmateDialog.setSize(300, 100);
        checkmateDialog.setLayout(new FlowLayout());
        checkmateDialog.setLocationRelativeTo(this); // Center it

        JTextArea checkmateText = new JTextArea("Checkmate!");
        checkmateText.setFont(new Font("Arial", Font.BOLD, 24));
        checkmateText.setOpaque(false);
        checkmateText.setFocusable(false);
        checkmateText.setEditable(false);
        checkmateDialog.add(checkmateText);

        checkmateDialog.setVisible(true); // Show and block until a button is clicked
    }

    public void stalemate(){
        JDialog stalemateDialog = new JDialog(this, "Game Over", true); // 'true' makes it modal
        stalemateDialog.setSize(300, 100);
        stalemateDialog.setLayout(new FlowLayout());
        stalemateDialog.setLocationRelativeTo(this); // Center it

        JTextArea stalemateText = new JTextArea("Stalmate!");
        stalemateText.setFont(new Font("Arial", Font.BOLD, 24));
        stalemateText.setOpaque(false);
        stalemateText.setFocusable(false);
        stalemateText.setEditable(false);
        stalemateDialog.add(stalemateText);

        stalemateDialog.setVisible(true); // Show and block until a button is clicked
    }

    private JButton createPromotionButton(Piece piece, Piece.Type type, JDialog dialog) {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(65, 65));
        button.add(new JLabel(piece.getImage()));
        button.addActionListener(e -> {
            choice = type;
            dialog.dispose(); // Close the dialog when selected
        });
        return button;
    }

    public Piece.Type getChoice(){
        return choice;
    }

    public void removePieceLabel(PieceLabel pieceLabel){
        if (pieceLabel != null) {
            if (layeredPane.isAncestorOf(pieceLabel)) { // Ensure it's part of the GUI
                System.out.println("Removing piece at: " + pieceLabel.getLocation());
                layeredPane.remove(pieceLabel);
                layeredPane.revalidate();  // Revalidate layout
                layeredPane.repaint();     // Redraw GUI
                System.out.println("Piece removed successfully.");
            } else {
                System.out.println("Error: Target piece label is not in layeredPane.");
            }
        } else {
            System.out.println("Error: Tried to remove a null PieceLabel.");
        }
    }

    public void movePieceLabel(PieceLabel pieceLabel, int X, int Y){
        pieceLabel.setLocation(X, Y);
        repaint();
    }

    public void disableBoard() {
        for (int rank = 0; rank < 8; rank++) {
            for (int file = 0; file < 8; file++) {
                squares[rank][file].setEnabled(false); // Disable buttons
            }
        }
        removeMouseListener(mouse);
        layeredPane.removeMouseListener(mouse);
        layeredPane.removeMouseMotionListener(mouse);
    }
    

    public JButton[][] getSquares(){
        return squares;
    }

    public JTextArea getTextArea(){
        return textArea;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == queenButton){
            System.out.println("Queen is choice");
            choice = Piece.Type.QUEEN;
        }

        if(e.getSource() == rookButton){
            choice = Piece.Type.ROOK;
            
        }

        if(e.getSource() == bishopButton){
            choice = Piece.Type.BISHOP;
            
        }

        if(e.getSource() == knightButton){
            choice = Piece.Type.KNIGHT;
            
        }
    }
}