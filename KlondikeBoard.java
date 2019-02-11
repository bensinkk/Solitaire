import java.util.*;
import javax.swing.JLayeredPane;
import java.awt.*;
import javax.swing.*;
import java.io.*;

import javax.swing.event.MouseInputAdapter;
import java.awt.event.*;
import java.awt.Point;
public class KlondikeBoard extends JLayeredPane
{
    private class DragListener extends MouseInputAdapter
    {
        ArrayList<Card> cards;
        Point location, originalLocation;
        MouseEvent clicked;
        Card card, moveTo;
        
        public void mousePressed(MouseEvent e){
            card = (Card)e.getComponent();
            cards = generateList(card);
            originalLocation = card.getLocation(originalLocation);
            clicked = e;
        }
        public void mouseDragged(MouseEvent e){
            if(card!= null && card.isFaceUp()){
                for(int i = 0;i<cards.size();i++){
                    setLayer(cards.get(i),400);
                    location = cards.get(i).getLocation(location);
                    int x = location.x - clicked.getX() + e.getX();
                    int y = location.y - clicked.getY() + e.getY();
                    cards.get(i).setLocation(x,y);
                }
            }
            else
                card = null;
        }
        public void mouseReleased(MouseEvent e){
            boolean tryDeck = false;
            int startingStack = -1;
            int endingStack = -1;
            int specific = -1;
            boolean keepGoing = true;
            int which = 0;
            if(card!=null && card.isFaceUp()){
                for(Card i : cards)
                    setLayer(i,0);
                try{
                    Component idk = getComponentAt(getMousePosition());
                    moveTo = (Card)(idk);
                    for(int k = 0;k<everything.size();k++){
                        for(int i = 0;i<everything.get(k).size();i++){
                            if(moveTo.equals(getCard(i,0,k))){
                                endingStack = i;
                                which = k;
                            }
                            else if(everything.get(k).get(i).indexOf(card)>-1){
                                startingStack = i;
                                specific = everything.get(k).get(i).indexOf(card);
                            }
                        }
                    }
                    if(deck.contains(card)){
                        tryDeck = true;
                    }
                    if(!tryDeck && startingStack>-1 && endingStack>-1 && moveTo.legalMove(card)){
                        for(int i = specific;i>=0;i--){
                            everything.get(STACKS).get(startingStack).remove(0);
                            if(everything.get(which).get(endingStack).size()==1)
                                everything.get(which).get(endingStack).get(0).faceDown();
                            everything.get(which).get(endingStack).add(0,cards.get(i));
                        }
                        if(which == FOUNDATIONS){
                            card.founded();
                            score+=5;
                        }
                        score+=5;
                        getCard(startingStack,0,STACKS).faceUp();
                        moves++;
                    }
                    else if(tryDeck && moveTo.legalMove(card)){
                        deck.remove(deck.find(card));
                        if(everything.get(which).get(endingStack).size()==1)
                            everything.get(which).get(endingStack).get(0).faceDown();
                        getPile(endingStack,which).add(0,card);
                        score+=5;
                        if(which == FOUNDATIONS){
                            card.founded();
                            score+=5;
                        }
                        moves++;
                    }
                }catch(Exception ex){}
            }
            else if(card!=null){
                if(deck.contains(card))
                    deck.nextCard();
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
    
    public KlondikeBoard(){
        deck = new Deck();
        everything = new ArrayList<ArrayList<ArrayList<Card>>>();
        ArrayList<ArrayList<Card>> stacks = new ArrayList<ArrayList<Card>>();
        for(int i = 0;i<7;i++)
            stacks.add(new ArrayList<Card>());
        everything.add(stacks);
        ArrayList<ArrayList<Card>> foundations = new ArrayList<ArrayList<Card>>();
        for(int i = 0;i<4;i++)
            foundations.add(new ArrayList<Card>());
        everything.add(foundations);
        this.setup();
        
  
        ImageIcon img = new ImageIcon("Cards/blank-card-hi.png");
        deck_button = new JButton(img);
        deck_button.setSize(Card.WIDTH,Card.HEIGHT);
        deck_button.setBackground(Color.white);
        deck_button.addActionListener(new DeckFlipper());
        
        new_game = new JButton("NEW GAME");
        new_game.setSize(Card.HEIGHT*3/2,Card.WIDTH);
        new_game.setLocation(Card.STACK_GAP*7,TEXT_START + TEXT_GAP*3);
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
        for(int i = 0; i<everything.get(STACKS).size(); i++){
            for(int j = 0; j<=i; j++){
                getPile(i,STACKS).add(deck.remove(0));
                getCard(i,j,STACKS).addMouseListener(d);
                getCard(i,j,STACKS).addMouseMotionListener(d);
            }
            getCard(i,0,STACKS).faceUp();
            getPile(i,STACKS).add(new BaseCard());
        }
        for(int i = 0; i<everything.get(FOUNDATIONS).size(); i++){
            getPile(i,FOUNDATIONS).add(new FoundationCard());
            getCard(i,0,FOUNDATIONS).addMouseListener(d);
        }
        for(int i = 0; i<deck.size();i++){
            deck.get(i).addMouseListener(d);
            deck.get(i).addMouseMotionListener(d);
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
    public ArrayList<Card> generateList(Card card){
        int index = -1;
        int specific = -1;
        boolean keepGoing = true;
        int which = 0;
        for(int k = 0; k<everything.size();k++){
            for(int i = 0;i<everything.get(k).size();i++){
                for(int j = 0;j<everything.get(k).get(i).size();j++){
                    if(card.equals(getCard(i,j,k))){
                        keepGoing = false;
                        index = i;
                        specific = j;
                        which = k;
                        break;
                    }
                }
            }
        }
        ArrayList<Card> output = new ArrayList<Card>();
        for(int i = specific;i>=0;i--)
            output.add(0,getCard(index,i,which));
        if(output.size()<1)
            output.add(card);
        return output;
    }
    
    //Display Stuff
    private int time = 0;
    private javax.swing.Timer t;
    private JButton deck_button,new_game;
    private boolean win = false;
    
    public static final int TEXT_GAP = 15, TEXT_START = 25;
    private class DeckFlipper implements ActionListener{
        public void actionPerformed(ActionEvent e){
            getDeck().flipOver();
            refresh();
            score-=100;
            if(score<0)
                score = 0;
        }
    }
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
        for(int i = 0; i<7; i++){
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
                this1.setLocation(Card.STACK_GAP*(i+3),0);
                add(this1,new Integer(10));
                moveToBack(this1);
            }
        }
        for(int i = getDeck().size()-1; i>=0; i--){
            Card card = getDeck().get(i);
            if(card.isFaceUp())
                card.setLocation(Card.STACK_GAP,0);
            else
                card.setLocation(0,0);
            card.setSize(Card.WIDTH,Card.HEIGHT);
            add(card, new Integer(10));
        }
        add(deck_button,new Integer(5));
        add(new_game,new Integer(5));
        return output==7;
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.setFont(new Font("Arial",Font.BOLD,15));
        g.drawString(String.format("TIME: %d",time),Card.STACK_GAP*7,TEXT_START);
        g.drawString(String.format("MOVES: %d",moves),Card.STACK_GAP*7,TEXT_START + TEXT_GAP);
        g.drawString(String.format("SCORE: %d",score),Card.STACK_GAP*7,TEXT_START + TEXT_GAP*2);
        if(win){
            g.setFont(new Font("Arial",Font.BOLD,100));
            g.drawString("YOU WIN!!",100,400);
            t.stop();
        }
    }
}