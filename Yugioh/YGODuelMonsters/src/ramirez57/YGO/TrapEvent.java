package ramirez57.YGO;

//Events for trap card triggers/effects
//Inheritable class for all trap events

public class TrapEvent {
	public Object[] data;
	public Duel duel;
	public Duelist duelistWithTrap;
	public Duelist duelistTriggerer;
	
	public TrapEvent(Duel duel, Duelist oneWithTrap, Duelist oneWhoTriggered) {
		this.duel = duel;
		this.duelistWithTrap = oneWithTrap;
		this.duelistTriggerer = oneWhoTriggered;
	}
	
	public static Class<? extends TrapEvent> fromString(String s) {
		if(s.equalsIgnoreCase("attack"))
			return TrapEventAttack.class;
		else if(s.equalsIgnoreCase("lpincrease"))
			return TrapEventLPIncrease.class;
		else if(s.equalsIgnoreCase("lpdecrease"))
			return TrapEventLPDecrease.class;
		else if(s.equalsIgnoreCase("powerup"))
			return TrapEventPowerUp.class;
		else if(s.equalsIgnoreCase("activate"))
			return TrapEventActivate.class;
		return TrapEvent.class;
	}
	
	public boolean willTrigger(TrapCard tc) {
		return true;
	}
}
