package ramirez57.YGO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Terrain {
	
	public static HashMap<String, Terrain> BY_NAME = new HashMap<String, Terrain>();
	public static Terrain NORMAL = Terrain.make("Normal", new ArrayList<MonsterType>(0), new ArrayList<MonsterType>(0), null);
	public String name;
	public List<MonsterType> favors;
	public List<MonsterType> unfavors;
	public ItemStack texture;
	
	public static Terrain make(String name, List<MonsterType> favors, List<MonsterType> unfavors, Material texture) {
		Terrain terrain = new Terrain();
		terrain.name = name;
		terrain.favors = favors;
		terrain.unfavors = unfavors;
		if(texture == null)
			terrain.texture = null;
		else
			terrain.texture = new ItemStack(texture);
		Terrain.BY_NAME.put(name, terrain);
		return terrain;
	}
	
	public String toString() {
		return this.name;
	}

	public ItemStack getTexture() {
		return this.texture.clone();
	}

	public boolean hasTexture() {
		return (this.texture != null);
	}
}
