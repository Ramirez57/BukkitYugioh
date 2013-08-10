package ramirez57.YGO;

import java.util.List;

public class FieldCard extends SpellCard {
	public Terrain terrain;
	
	public static FieldCard create(String name, List<MonsterType> favors, List<MonsterType> unfavors) {
		FieldCard fc = new FieldCard();
		fc.name = name;
		fc.terrain = Terrain.make(name, favors, unfavors);
		return fc;
	}
	
	public boolean shouldActivate(Duel duel, Duelist duelist) {
		return false;
	}
	
	public void activate(Duel duel, Duelist duelist) {
		duel.changeTerrain(this.terrain);
	}
}
