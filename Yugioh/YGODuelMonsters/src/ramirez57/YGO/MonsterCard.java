package ramirez57.YGO;

public class MonsterCard extends Card implements Comparable<MonsterCard> {
	public boolean attacked;
	public int atk;
	public int def;
	public int level;
	public MonsterType type;
	public MonsterAttribute attribute;
	public GuardianStar[] stars;
	public GuardianStar star;
	public MonsterPosition position = MonsterPosition.ATTACK;
	
	public MonsterCard() {
		super();
		this.stars = new GuardianStar[2];
	}
	
	public static MonsterCard create(String name) {
		MonsterCard mc = new MonsterCard();
		mc.name = name;
		mc.attacked = false;
		return mc;
	}
	
	public boolean hasTrait(Trait trait) {
		int c;
		if(MonsterType.fromString(trait.toString()) == this.type)
			return true;
		for(c = 0; c < this.traits.length; c++) {
			if(this.traits[c] == trait)
				return true;
		}
		return false;
	}
	
	public void changePosition() {
		if(this.position == MonsterPosition.ATTACK) {
			this.position = MonsterPosition.DEFENSE;
		} else {
			this.position = MonsterPosition.ATTACK;
		}
	}

	public int compareTo(MonsterCard o) {
		if(this.position == MonsterPosition.ATTACK) {
			if(o.position == MonsterPosition.DEFENSE) {
				return (this.atk + this.bonus) - (o.def + o.bonus);
			} else {
				return (this.atk + this.bonus) - (o.atk + o.bonus);
			}
		} else {
			if(o.position == MonsterPosition.DEFENSE){
				return (this.def + this.bonus) - (o.def + o.bonus);
			} else {
				return (this.def + this.bonus) - (o.atk + o.bonus);
			}
		}
	}
	
	public int getAtk() {
		if(this.atk + this.bonus < 0)
			return 0;
		else return (this.atk + this.bonus);
	}
	
	public int getDef() {
		if(this.def + this.bonus < 0)
			return 0;
		else return (this.def + this.bonus);
	}
	
	public boolean canEquip(EquipCard ec) {
		if(ec.equipsTo.contains(-1))
			return true;
		return ec.equipsTo.contains(this.id);
	}
}
