import java.util.*;
public class Deck
{
    private Card[] list;
    private List<Card> useable;
    
    public Deck(){
        list = new Card[52];
        for(int i = 1;i<14;i++)
            list[i-1] = new Card(i,"hearts");
        for(int i = 1;i<14;i++)
            list[i+12] = new Card(i,"diamonds");
        for(int i = 1;i<14;i++)
            list[i+25] = new Card(i,"clubs");
        for(int i = 1;i<14;i++)
            list[i+38] = new Card(i,"spades");
        useable = new ArrayList<Card>();
        shuffle();
    }
    
    public void shuffle(){
        useable.removeAll(useable);
        List<Integer> usedNums = new ArrayList<Integer>();
        while(usedNums.size()<52){
            int index = (int)(52*Math.random());
            if(!usedNums.contains(index)){
                useable.add(list[index]);
                usedNums.add(index);
            }
        }
    }
    public Card get(int index){
        return useable.get(index);
    }
    public Card remove(int index){
        return useable.remove(index);
    }
    public List<Card> getRemaining(){
        return useable;
    }
    public boolean contains(Card c){
        return useable.contains(c);
    }
    public void nextCard(){
        int i = 0;
        while(i<useable.size()){
            if(!useable.get(i).isFaceUp())
                break;
            i++;
        }
        useable.get(i).faceUp();
    }
    public int size(){
        return useable.size();
    }
    public int find(Card c){
        return useable.indexOf(c);
    }
    public void flipOver(){
        for(Card i : useable){
            i.faceDown();
        }
    }
}
