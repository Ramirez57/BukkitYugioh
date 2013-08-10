package ramirez57.YGO;

public class TraitPair {

	public Trait t1;
	public Trait t2;
	
	public TraitPair(Trait t1, Trait t2) {
		this.t1 = t1;
		this.t2 = t2;
	}
	
	public boolean hasTraits(MonsterCard mc1, MonsterCard mc2, boolean strict) {
		if(strict) {
			MonsterType mt1 = MonsterType.fromString(this.t1.toString());
			MonsterType mt2 = MonsterType.fromString(this.t2.toString());
			if(mc1.type == mt1 && mc2.type == mt2)
				return true;
			if(mc1.type == mt2 && mc2.type == mt1)
				return true;
			return false;
		}
		if(mc1.hasTrait(this.t1) && mc2.hasTrait(this.t2))
			return true;
		if(mc1.hasTrait(this.t2) && mc2.hasTrait(this.t1))
			return true;
		return false;
	}
	
}
