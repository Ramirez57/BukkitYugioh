package ramirez57.YGO;

import java.util.List;

public class EquipCard extends Card {
	
	public int incrementBy;
	public List<Integer> equipsTo;
	
	public static EquipCard create(String name, int incrementor, List<Integer> equipsTo) {
		EquipCard ec = new EquipCard();
		ec.name = name;
		ec.incrementBy = incrementor;
		ec.equipsTo = equipsTo;
		return ec;
	}
	
	public boolean canEquip(MonsterCard mc) {
		return this.equipsTo.contains(mc.id);
	}
}
