package ramirez57.YGO;

public class TrapEventAttack extends TrapEvent {
	
	public Duelist duelistAttacking;
	public Card cardAttacking;
	public Duelist duelistDefending;
	public Card cardDefending;
	public int maxatk;
	
	public TrapEventAttack(Duel duel, Duelist oneWithTrap, Duelist oneWhoTriggered, Duelist attacker, Card attacking, Duelist defender, Card defending) {
		super(duel, oneWithTrap, oneWhoTriggered);
		this.duelistAttacking = attacker;
		this.cardAttacking = attacking;
		this.duelistDefending = defender;
		this.cardDefending = defending;
	}
}
