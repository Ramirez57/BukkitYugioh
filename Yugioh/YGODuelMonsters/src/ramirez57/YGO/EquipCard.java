package ramirez57.YGO;

import java.util.List;

public class EquipCard extends Card {
	
	public int incrementBy;
	public boolean equipsToAll;
	public List<Integer> equipsTo; //temporary field. Do not use!
	
	public static EquipCard create(String name, int incrementor) {
		EquipCard ec = new EquipCard();
		ec.name = name;
		ec.incrementBy = incrementor;
		ec.equipsToAll = false;
		ec.equipsTo = null;
		return ec;
	}
	
	@Deprecated
	public boolean canEquip(MonsterCard mc) {
		//return this.equipsTo.contains(mc.id);
		return false;
	}
}
