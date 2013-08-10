package ramirez57.YGO;

public class CardPair {
	public int id1;
	public int id2;
	
	public CardPair(int id1, int id2) {
		this.id1 = id1;
		this.id2 = id2;
	}
	
	public boolean settles(Card c1, Card c2) {
		if(c1.id == this.id1 && c2.id == this.id2)
			return true;
		if(c1.id == this.id2 && c2.id == this.id1)
			return true;
		return false;
	}
}
