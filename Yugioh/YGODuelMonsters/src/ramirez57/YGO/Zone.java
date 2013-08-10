package ramirez57.YGO;

public class Zone {
	public Card card;
	
	public void put(Card c) {
		this.card = c;
	}
	
	public void remove() {
		this.card = null;
	}
	
	public boolean isOpen() {
		return this.card == null;
	}
	
	public void toGraveyard(Duel duel, Duelist rewardFor) {
		if(this.card != null) {
			duel.graveyard.push(this.card);
			if(rewardFor != null && this.card.obtainable)
				rewardFor.rewards.push(this.card);
		}
		this.card = null;
	}
}
