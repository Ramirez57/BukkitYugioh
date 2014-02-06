package ramirez57.YGO;

import java.util.List;

import org.bukkit.Material;

public class FieldCard extends SpellCard {
	public Terrain terrain;
	
	public static FieldCard create(String name, List<MonsterType> favors, List<MonsterType> unfavors, Material texture) {
		FieldCard fc = new FieldCard();
		fc.name = name;
		fc.terrain = Terrain.make(name, favors, unfavors, texture);
		return fc;
	}
	
	public boolean shouldActivate(Duel duel, Duelist duelist) {
		return false;
	}
	
	public void activate(Duel duel, Duelist duelist) {
		duelist.change_field++;
		duelist.pure_magic++;
		duel.changeTerrain(this.terrain);
	}
}
