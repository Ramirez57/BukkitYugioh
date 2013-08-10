package ramirez57.YGO;

public class CardTraitPair {

	public int id;
	public Trait trait;
	
	public CardTraitPair(int id, Trait trait) {
		this.id = id;
		this.trait = trait;
	}
	
	public boolean settles(Card mc, Card card) {
		if(mc.hasTrait(this.trait) && card.id == id)
			return true;
		if(mc.id == id && card.hasTrait(this.trait))
			return true;
		return false;
	}
	
}
