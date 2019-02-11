import java.awt.image.BufferedImage;
import javax.imageio.*;
import java.io.*;
import javax.swing.JComponent;
import java.awt.*;
public class Card extends JComponent
{
    private BufferedImage front, back;
    private int num;
    private String suit;
    private boolean color, up, inFoundation, inCell;
    
    public static final int HEIGHT = 69, WIDTH = 50, STACK_GAP = 75, OFFSET = 30;
    
    public Card(int value, String suit){
        this.suit = suit;
        switch(suit){
            case "hearts": case "diamonds": color = true;
                                            break;
            case "spades": case "clubs": color = false;
                                         break;
        }
        num = value;
        String filePart = "";
        switch(value){
            case 2: case 3: case 4: case 5: case 6:
                case 7: case 8: case 9: case 10: filePart = "" + value;
                                                 break;
            case 11: filePart = "Jack";
                     break;
            case 12: filePart = "Queen";
                     break;
            case 13: filePart = "King";
                     break;
            case 1: filePart = "Ace";
                    break;
        }
        try{
         front = ImageIO.read(new File("Cards/nicubunu_Ornamental_deck_"+filePart+"_of_"+suit+".jpg"));
        }catch(IOException e){System.out.println(filePart + " " + suit);}
        try{
         back = ImageIO.read(new File("Cards/nicubunu_Card_backs_grid_blue.jpg"));
        }catch(IOException e){System.out.println("couldnt read blue back");}
        up = false;
        inFoundation = false;
        inCell = false;
        
    }
    public Card(int n){
        num = n;
        front = null;
        try{
                     back = ImageIO.read(new File("Cards/nicubunu_Card_backs_simple_red.jpg"));
        }catch(IOException e){System.out.println("couldnt read red back");}
        suit = "foundation";
    }
    public Card(String s){
        num = 0;
        front = null;
        try{
            back = ImageIO.read(new File(s));
        }catch(IOException e){}
    }
    
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(this.getFace(),0,0,WIDTH,HEIGHT,null);
    }
    public int getValue(){
        return num;
    }
    public boolean getColor(){
        return color;
    }
    public BufferedImage getFront(){
        return front;
    }
    public BufferedImage getBack(){
        return back;
    }
    public BufferedImage getFace(){
        if(up)
            return getFront();
        return getBack();
    }
    public boolean isFaceUp(){
        return up;
    }
    public void faceUp(){
        up = true;
    }
    public void faceDown(){
        up = false;
    }
    public String toString(){
        return num + " of " + suit;
    }
    public boolean legalMove(Card other){
        if(inCell)
            return false;
        if(inFoundation){
            return this.getSuit().equals(other.getSuit()) && this.getValue()+1 == other.getValue();
        }
        return (this.getColor()!=other.getColor()) && (this.getValue()-1==other.getValue());
    }
    public String getSuit(){
        return suit;
    }
    public void founded(){
        inFoundation = true;
    }
    public void setBack(BufferedImage bi){
        back = bi;
    }
    public void celled(){
        inCell = !inCell;
    }
}
