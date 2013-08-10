package ramirez57.YGO;

import java.util.HashMap;

public class MonsterType {
	
	public static HashMap<String,MonsterType> BY_NAME = new HashMap<String,MonsterType>();
	
	public static MonsterType AQUA = new MonsterType("Aqua");
	public static MonsterType BEAST = new MonsterType("Beast");
	public static MonsterType BEASTWARRIOR = new MonsterType("BeastWarrior");
	public static MonsterType DINOSAUR = new MonsterType("Dinosaur");
	public static MonsterType DRAGON = new MonsterType("Dragon");
	public static MonsterType FAIRY = new MonsterType("Fairy");
	public static MonsterType FIEND = new MonsterType("Fiend");
	public static MonsterType FISH = new MonsterType("Fish");
	public static MonsterType INSECT = new MonsterType("Insect");
	public static MonsterType MACHINE = new MonsterType("Machine");
	public static MonsterType PLANT = new MonsterType("Plant");
	public static MonsterType PYRO = new MonsterType("Pyro");
	public static MonsterType REPTILE = new MonsterType("Reptile");
	public static MonsterType ROCK = new MonsterType("Rock");
	public static MonsterType SEASERPENT = new MonsterType("SeaSerpent");
	public static MonsterType SPELLCASTER = new MonsterType("Spellcaster");
	public static MonsterType THUNDER = new MonsterType("Thunder");
	public static MonsterType WARRIOR = new MonsterType("Warrior");
	public static MonsterType WINGEDBEAST = new MonsterType("WingedBeast");
	public static MonsterType ZOMBIE = new MonsterType("Zombie");
	
	public String name;
	public MonsterType(String name) {
		this.name = name;
		MonsterType.BY_NAME.put(name.toUpperCase(), this);
	}
	
	public String toString() {
		return this.name;
	}
	
	public static MonsterType fromString(String s) {
		return MonsterType.BY_NAME.get(s.toUpperCase());
	}
}
