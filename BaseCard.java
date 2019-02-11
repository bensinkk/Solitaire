import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.*;
public class BaseCard extends Card
{
    public BaseCard(){
        super(14);
        this.setVisible(false);
        BufferedImage newBack = null;
        try{
           newBack = ImageIO.read(new File("Cards/blank-card-hi.png"));
        }catch(IOException e){System.out.println("couldnt read blank");}
        setBack(newBack);
}
    public boolean legalMove(Card other){
        return this.getValue()-1 == other.getValue();
    }
    public void faceDown(){
        this.setVisible(false);
    }
    public void faceUp(){
        this.setVisible(true);
    }
}
