package ramirez57.YGO;

import org.bukkit.ChatColor;

public class Rank {

	public static String getLetter(int i) {
		String s = "";
		if(i == 1)
			s += "D";
		if(i == 2)
			s += "C";
		if(i == 3)
			s += "B";
		if(i == 4)
			s += "A";
		if(i == 5)
			s += "S";
		return s;
	}
	
	public static String getColoredLetter(int i) {
		String s = "";
		if(i == 1)
			s += ChatColor.DARK_BLUE + "D";
		if(i == 2)
			s += ChatColor.DARK_GREEN + "C";
		if(i == 3)
			s += ChatColor.GOLD + "B";
		if(i == 4)
			s += ChatColor.RED + "A";
		if(i == 5)
			s += ChatColor.LIGHT_PURPLE + "S";
		return s;
	}
	
	public static String getPOW(boolean pow) {
		String s = "";
		if(pow)
			s += "POW";
		else
			s += "TEC";
		return s;
	}
	
	public static String getColoredPOW(boolean pow) {
		String s = "";
		if(pow)
			s += ChatColor.RED + "POW";
		else
			s += ChatColor.BLUE + "TEC";
		return s;
	}
	
	public static String get(int i, boolean pow) {
		String s = "";
		if(i == 1)
			s += "D ";
		if(i == 2)
			s += "C ";
		if(i == 3)
			s += "B ";
		if(i == 4)
			s += "A ";
		if(i == 5)
			s += "S ";
		
		if(pow)
			s += "POW (";
		else
			s += "TEC (";
		
		for(int c=0; c < i; c++) {
			s+="*";
		}
		s+=")";
		return s;
	}
	
	public static String getColored(int i, boolean pow, ChatColor defcolor) {
		String s = "";
		if(i == 1)
			s += ChatColor.DARK_BLUE + "D ";
		if(i == 2)
			s += ChatColor.DARK_GREEN + "C ";
		if(i == 3)
			s += ChatColor.GOLD + "B ";
		if(i == 4)
			s += ChatColor.RED + "A ";
		if(i == 5)
			s += ChatColor.LIGHT_PURPLE + "S ";
		
		if(pow)
			s += ChatColor.RED + "POW" + defcolor + " (";
		else
			s += ChatColor.BLUE + "TEC" + defcolor + " (";
		
		s += ChatColor.GOLD;
		for(int c=0; c < i; c++) {
			s+="*";
		}
		s+= defcolor + ")";
		return s;
	}
	
}
