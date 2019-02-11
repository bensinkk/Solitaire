import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.*;
public class CellCard extends Card
{
    public CellCard(){
        super(14);
        BufferedImage newBack = null;
        try{
           newBack = ImageIO.read(new File("Cards/blank-card-hi.png"));
        }catch(IOException e){}
        setBack(newBack);
    }
    public boolean legalMove(Card other){
        return true;
    }
    public void faceDown(){}
    public void faceUp(){}
}
