package ramirez57.YGO;

public class MonsterPosition {
	
	public static MonsterPosition ATTACK = new MonsterPosition("Attack Mode");
	public static MonsterPosition DEFENSE = new MonsterPosition("Defense Mode");
	
	public String name;
	
	public MonsterPosition(String name) {
		this.name = name;
	}
	
	public String toString() {
		return this.name;
	}
}
