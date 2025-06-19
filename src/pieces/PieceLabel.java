package pieces;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class PieceLabel extends JLabel{
    Piece piece;

    public PieceLabel(Piece piece){
        this.piece = piece;
        ImageIcon icon = piece.getImage();

        if (icon != null) {
            Image scaledImage = icon.getImage().getScaledInstance(75, 75, Image.SCALE_SMOOTH);
            
            setIcon(new ImageIcon(scaledImage));
            setBounds(piece.getX(), piece.getY(), 75, 75);
            setOpaque(false);
        }
    }

    public Piece getPiece(){
        return piece;
    }
}
