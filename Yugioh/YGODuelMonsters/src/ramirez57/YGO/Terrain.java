package ramirez57.YGO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Terrain {
	
	public static HashMap<String, Terrain> BY_NAME = new HashMap<String, Terrain>();
	public static Terrain NORMAL = Terrain.make("Normal", new ArrayList<MonsterType>(0), new ArrayList<MonsterType>(0));
	public String name;
	public List<MonsterType> favors;
	public List<MonsterType> unfavors;
	
	public static Terrain make(String name, List<MonsterType> favors, List<MonsterType> unfavors) {
		Terrain terrain = new Terrain();
		terrain.name = name;
		terrain.favors = favors;
		terrain.unfavors = unfavors;
		Terrain.BY_NAME.put(name, terrain);
		return terrain;
	}
	
	public String toString() {
		return this.name;
	}
}
