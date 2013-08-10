package ramirez57.YGO;

import java.util.HashMap;

public class Trait {

	public static HashMap<String, Trait> BY_NAME = new HashMap<String, Trait>();
	
	public static Trait ANIMAL = new Trait("Animal");
	public static Trait AQUA = new Trait("Aqua");
	public static Trait BEAST = new Trait("Beast");
	public static Trait BEASTWARRIOR = new Trait("BeastWarrior");
	public static Trait DARKMAGIC = new Trait("DarkMagic");
	public static Trait DARKSPELLCASTER = new Trait("DarkSpellcaster");
	public static Trait DINOSAUR = new Trait("Dinosaur");
	public static Trait DRAGON = new Trait("Dragon");
	public static Trait ELF = new Trait("Elf");
	public static Trait FAIRY = new Trait("Fairy");
	public static Trait FEMALE = new Trait("Female");
	public static Trait FIEND = new Trait("Fiend");
	public static Trait FISH = new Trait("Fish");
	public static Trait INSECT = new Trait("Insect");
	public static Trait JAR = new Trait("Jar");
	public static Trait MACHINE = new Trait("Machine");
	public static Trait PLANT = new Trait("Plant");
	public static Trait PYRO = new Trait("Pyro");
	public static Trait REPTILE = new Trait("Reptile");
	public static Trait ROCK = new Trait("Rock");
	public static Trait SEASERPENT = new Trait("SeaSerpent");
	public static Trait SPECIAL_A = new Trait("Special-A");
	public static Trait SPECIAL_B = new Trait("Special-B");
	public static Trait SPECIAL_C = new Trait("Special-C");
	public static Trait SPECIAL_D = new Trait("Special-D");
	public static Trait SPELLCASTER = new Trait("Spellcaster");
	public static Trait THUNDER = new Trait("Thunder");
	public static Trait TURTLE = new Trait("Turtle");
	public static Trait WARRIOR = new Trait("Warrior");
	public static Trait WINGEDBEAST = new Trait("WingedBeast");
	public static Trait ZOMBIE = new Trait("Zombie");
	
	public String name;
	
	public Trait(String name) {
		this.name = name;
		Trait.BY_NAME.put(name.toUpperCase(), this);
	}
	
	public static Trait fromString(String s) {
		return Trait.BY_NAME.get(s.toUpperCase());
	}
	
	public String toString() {
		return this.name;
	}
	
}
