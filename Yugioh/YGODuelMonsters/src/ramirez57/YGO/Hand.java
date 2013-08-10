package ramirez57.YGO;

import java.util.Stack;

public class Hand {
	public Stack<Card> cards;
	
	public Hand() {
		this.cards = new Stack<Card>();
	}
	
	public void discard(Duel duel, Card c) {
		duel.graveyard.push(c);
		this.cards.removeElement(c);
	}
	
	public void drawFrom(Deck deck) {
		this.cards.push(deck.cards.pop());
	}
	
	public void addCard(Card c) {
		this.cards.push(c);
	}
	
	public void removeCard(Card c) {
		this.cards.removeElement(c);
	}
	
	public int getCardPos(Card c) {
		return this.cards.indexOf(c);
	}
	
	public boolean containsCardId(int id) {
		int c;
		for(c = 0; c < this.cards.size(); c++) {
			if(this.cards.elementAt(c).id == id)
				return true;
		}
		return false;
	}
	
	public int size() {
		return this.cards.size();
	}
}
