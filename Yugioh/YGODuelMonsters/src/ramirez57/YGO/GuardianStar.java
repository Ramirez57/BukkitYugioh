package ramirez57.YGO;

import java.util.HashMap;

public class GuardianStar {
	public static HashMap<String,GuardianStar> BY_NAME = new HashMap<String,GuardianStar>();
	
	public static GuardianStar SUN = new GuardianStar("Sun", GuardianStar.MOON);
	public static GuardianStar MOON = new GuardianStar("Moon", GuardianStar.VENUS);
	public static GuardianStar VENUS = new GuardianStar("Venus", GuardianStar.MERCURY);
	public static GuardianStar MERCURY = new GuardianStar("Mercury", GuardianStar.SUN);
	
	public static GuardianStar JUPITER = new GuardianStar("Jupiter", GuardianStar.SATURN);
	public static GuardianStar SATURN = new GuardianStar("Saturn", GuardianStar.URANUS);
	public static GuardianStar URANUS = new GuardianStar("Uranus", GuardianStar.PLUTO);
	public static GuardianStar PLUTO = new GuardianStar("Pluto", GuardianStar.NEPTUNE);
	public static GuardianStar NEPTUNE = new GuardianStar("Neptune", GuardianStar.MARS);
	public static GuardianStar MARS = new GuardianStar("Mars", GuardianStar.JUPITER);
	
	public GuardianStar inferiorStar;
	public String name;
	public GuardianStar(String name, GuardianStar inferiorStar) {
		this.inferiorStar = inferiorStar;
		this.name = name;
		GuardianStar.BY_NAME.put(name.toUpperCase(), this);
	}
	
	public boolean isSuperiorTo(GuardianStar star) {
		return this.inferiorStar == star;
	}
	
	public static void init() {
		GuardianStar.SUN.inferiorStar = GuardianStar.MOON;
		GuardianStar.MOON.inferiorStar = GuardianStar.VENUS;
		GuardianStar.VENUS.inferiorStar = GuardianStar.MERCURY;
		GuardianStar.MERCURY.inferiorStar = GuardianStar.SUN;
		
		GuardianStar.JUPITER.inferiorStar = GuardianStar.SATURN;
		GuardianStar.SATURN.inferiorStar = GuardianStar.URANUS;
		GuardianStar.URANUS.inferiorStar = GuardianStar.PLUTO;
		GuardianStar.PLUTO.inferiorStar = GuardianStar.NEPTUNE;
		GuardianStar.NEPTUNE.inferiorStar = GuardianStar.MARS;
		GuardianStar.MARS.inferiorStar = GuardianStar.JUPITER;
	}
	
	public static GuardianStar fromString(String name) {
		return GuardianStar.BY_NAME.get(name.toUpperCase());
	}
	
	public String toString() {
		return this.name;
	}
}
