
public class FoundationCard extends Card
{
    public FoundationCard(){
        super(0);
    }
    public boolean legalMove(Card other){
        return this.getValue()+1 == other.getValue();
    }
    public void faceUp(){}
}
