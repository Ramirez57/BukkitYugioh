package ramirez57.YGO;

import java.util.HashMap;

public class MonsterAttribute {

	public static HashMap<String,MonsterAttribute> BY_NAME = new HashMap<String,MonsterAttribute>();
	
	public static MonsterAttribute FIRE = new MonsterAttribute("Fire");
	public static MonsterAttribute WATER = new MonsterAttribute("Water");
	public static MonsterAttribute WIND = new MonsterAttribute("Wind");
	public static MonsterAttribute EARTH = new MonsterAttribute("Earth");
	public static MonsterAttribute LIGHT = new MonsterAttribute("Light");
	public static MonsterAttribute DARK = new MonsterAttribute("Dark");
	
	public String name;
	
	public MonsterAttribute(String name) {
		this.name = name;
		MonsterAttribute.BY_NAME.put(name.toUpperCase(), this);
	}
	
	public String toString() {
		return this.name;
	}
	
	public static MonsterAttribute fromString(String s) {
		return MonsterAttribute.BY_NAME.get(s.toUpperCase());
	}
}
