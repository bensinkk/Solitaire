import java.util.*;
import javax.swing.JLayeredPane;
import java.awt.*;
import javax.swing.*;
import java.io.*;

import javax.swing.event.MouseInputAdapter;
import java.awt.event.*;
import java.awt.Point;
public class FreecellBoard extends JLayeredPane
{
    private class DragListener extends MouseInputAdapter
    {
        Point location;
        MouseEvent clicked;
        Card card, moveTo;
        
        public void mousePressed(MouseEvent e){
            card = (Card)e.getComponent();
            if(!checkCard(card))
                card = null;
            clicked = e;
        }
        public void mouseDragged(MouseEvent e){
            if(card!= null && card.isFaceUp()){
                    setLayer(card,400);
                    location = card.getLocation(location);
                    int x = location.x - clicked.getX() + e.getX();
                    int y = location.y - clicked.getY() + e.getY();
                    card.setLocation(x,y);
            }
            else
                card = null;
        }
        public void mouseReleased(MouseEvent e){
            int startingStack = -1;
            int endingStack = -1;
            int specific = -1;
            int whichStart = 0;
            int whichEnd = 0;
            if(card!=null && card.isFaceUp()){
                setLayer(card,0);
                try{
                    Component idk = getComponentAt(getMousePosition());
                    moveTo = (Card)(idk);
                    for(int k = 0;k<everything.size();k++){
                        for(int i = 0;i<everything.get(k).size();i++){
                            if(moveTo.equals(getCard(i,0,k))){
                                endingStack = i;
                                whichEnd = k;
                            }
                            else if(card.equals(getCard(i,0,k))){
                                startingStack = i;
                                whichStart = k;
                            }
                        }
                    }
                    if(startingStack>-1 && endingStack>-1 && moveTo.legalMove(card)){
                        getPile(startingStack,whichStart).remove(0);
                        if(getSize(endingStack,whichEnd)==1)
                            getCard(endingStack,0,whichEnd).faceDown();
                        if(getSize(startingStack,whichStart)==1)
                            getCard(startingStack,0,whichStart).faceUp();
                        getPile(endingStack,whichEnd).add(0,card);
                        if(whichEnd == FOUNDATIONS){
                            card.founded();
                            score+=5;
                        }
                        if((whichStart == CELLS || whichEnd == CELLS) && whichStart!=whichEnd)
                            card.celled();
                        score+=5;
                        moves++;
                    }
                }catch(Exception ex){}
            }
            refresh();
            card = null;
            location = null;
            clicked = null;
        }
    }
    //Game Logic Stuff
    private Deck deck;
    private ArrayList<ArrayList<ArrayList<Card>>> everything;
    private int moves, score;
    
    public static final int STACKS = 0;
    public static final int FOUNDATIONS = 1;
    public static final int CELLS = 2;
    
    public FreecellBoard(){
        deck = new Deck();
        everything = new ArrayList<ArrayList<ArrayList<Card>>>();
        ArrayList<ArrayList<Card>> stacks = new ArrayList<ArrayList<Card>>();
        for(int i = 0;i<8;i++)
            stacks.add(new ArrayList<Card>());
        everything.add(stacks);
        ArrayList<ArrayList<Card>> foundations = new ArrayList<ArrayList<Card>>();
        for(int i = 0;i<4;i++)
            foundations.add(new ArrayList<Card>());
        ArrayList<ArrayList<Card>> cells = new ArrayList<ArrayList<Card>>();
        for(int i = 0;i<4;i++)
            cells.add(new ArrayList<Card>());
        everything.add(foundations);
        everything.add(cells);
        this.setup();
        
        new_game = new JButton("NEW GAME");
        new_game.setSize(Card.HEIGHT*3/2,Card.WIDTH);
        new_game.setLocation(Card.STACK_GAP*8,TEXT_START + TEXT_GAP*3);
        new_game.addActionListener(new BoardReset());
        
        refresh();
        Stopwatch s = new Stopwatch();
        t = new javax.swing.Timer(1000,s);
        t.start();
    }
    
    public void removeAllCards(){
        for(int i = 0;i<everything.size();i++){
            for(int j = 0;j<everything.get(i).size();j++)
                while(getSize(j,i)>0)
                    getPile(j,i).remove(0);
        }
    }
    public void setup(){
        DragListener d = new DragListener();
        removeAllCards();
        for(int i = 0; i<7; i++){
            for(int j = 0; j<8; j++){
                if(deck.size()>0){
                    everything.get(STACKS).get(j).add(deck.remove(0));
                    getCard(j,i,STACKS).addMouseListener(d);
                    getCard(j,i,STACKS).addMouseMotionListener(d);
                    getCard(j,i,STACKS).faceUp();
                }
            }
        }
        for(int i = 0;i<8;i++)
            getPile(i,STACKS).add(new FreecellBaseCard());
        for(int i = 0; i<everything.get(FOUNDATIONS).size(); i++){
            getPile(i,FOUNDATIONS).add(new FoundationCard());
            getCard(i,0,FOUNDATIONS).addMouseListener(d);
        }
        for(int i = 0; i<everything.get(CELLS).size();i++){
            getPile(i,CELLS).add(new CellCard());
            getCard(i,0,CELLS).addMouseListener(d);
            getCard(i,0,CELLS).addMouseMotionListener(d);
        }
        score = 0;
        moves = 0;
    }
    public int getSize(int index, int val){
        return everything.get(val).get(index).size();
    }
    public Card getCard(int pile, int index, int val){
        return everything.get(val).get(pile).get(index);
    }
    public ArrayList<Card> getPile(int index, int val){
        return everything.get(val).get(index);
    }
    public Deck getDeck(){
        return deck;
    }
    public boolean checkCard(Card c){
        for(int i = 0;i<everything.size();i++){
            for(int j = 0;j<everything.get(i).size();j++){
                if(getCard(j,0,i).equals(c))
                    return true;
            }
        }
        return false;
    }
    
    //Display Stuff
    private int time = 0;
    private javax.swing.Timer t;
    private JButton new_game;
    private boolean win = false;
    
    public static final int TEXT_GAP = 15, TEXT_START = 25;
    
    private class BoardReset implements ActionListener{
        public void actionPerformed(ActionEvent e){
            deck = new Deck();
            setup();
            t.restart();
            refresh();
        }
    }
    private class Stopwatch implements ActionListener{
        public void actionPerformed(ActionEvent e){
            time++;
            repaint();
        }
    }
    public void refresh(){
        removeAll();
        win = addAll();
        repaint();
    }
    public boolean addAll(){
        Card this1 = null;
        int output = 0;
        for(int i = 0; i<8; i++){
            for(int j = 0; j<getSize(i,STACKS); j++){
                try{
                this1 = getCard(i,j,STACKS);
                this1.setSize(Card.WIDTH,Card.HEIGHT);
                this1.setLocation(Card.STACK_GAP*i,Card.OFFSET*(getSize(i,STACKS)-j)+Card.OFFSET*2);
                add(this1,new Integer(10));
                moveToBack(this1);
                output++;
            }catch(IndexOutOfBoundsException e){
                System.out.println("i = " + i);
                System.out.println("j = " + j);
                System.out.println("Size: " + getSize(i,STACKS));
            }
            }
        }
        for(int i = 0; i<4; i++){
            for(int j = 0; j< getSize(i,FOUNDATIONS);j++){
                this1 = getCard(i,j,FOUNDATIONS);
                this1.setSize(Card.WIDTH,Card.HEIGHT);
                this1.setLocation(Card.STACK_GAP*(i+4),0);
                add(this1,new Integer(10));
                moveToBack(this1);
            }
        }
        for(int i = 0; i<4; i++){
            for(int j = 0; j< getSize(i,CELLS);j++){
                this1 = getCard(i,j,CELLS);
                this1.setSize(Card.WIDTH,Card.HEIGHT);
                this1.setLocation(Card.STACK_GAP*(i),0);
                add(this1,new Integer(10));
                moveToBack(this1);
            }
        }
        add(new_game,new Integer(5));
        return output==8;
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.setFont(new Font("Arial",Font.BOLD,15));
        g.drawString(String.format("TIME: %d",time),Card.STACK_GAP*8,TEXT_START);
        g.drawString(String.format("MOVES: %d",moves),Card.STACK_GAP*8,TEXT_START + TEXT_GAP);
        g.drawString(String.format("SCORE: %d",score),Card.STACK_GAP*8,TEXT_START + TEXT_GAP*2);
        if(win){
            g.setFont(new Font("Arial",Font.BOLD,100));
            g.drawString("YOU WIN!!",100,400);
            t.stop();
        }
    }
}