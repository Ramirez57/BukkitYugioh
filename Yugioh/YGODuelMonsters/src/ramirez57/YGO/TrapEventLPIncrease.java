package ramirez57.YGO;

public class TrapEventLPIncrease extends TrapEvent {

	public Duelist receiver;
	public int amnt;
	
	public TrapEventLPIncrease(Duel duel, Duelist oneWithTrap,
			Duelist oneWhoTriggered, Duelist increasing, int amnt) {
		super(duel, oneWithTrap, oneWhoTriggered);
		this.receiver = increasing;
		this.amnt = amnt;
	}

}
