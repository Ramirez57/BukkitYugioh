package ramirez57.YGO;

public class TrapEventActivate extends TrapEvent {

	public Card card;
	
	public TrapEventActivate(Duel duel, Duelist oneWithTrap, Duelist oneWhoTriggered, Card card) {
		super(duel, oneWithTrap, oneWhoTriggered);
		this.card = card;
	}
	
}
