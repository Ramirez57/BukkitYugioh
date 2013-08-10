package ramirez57.YGO;

public class TrapEventPowerUp extends TrapEvent {

	public Duelist duelist;
	public MonsterCard card;
	public int amnt;
	
	public TrapEventPowerUp(Duel duel, Duelist oneWithTrap,
			Duelist oneWhoTriggered, Duelist powerer, MonsterCard mc, int amnt) {
		super(duel, oneWithTrap, oneWhoTriggered);
		this.duelist = powerer;
		this.card = mc;
		this.amnt = amnt;
	}

}
