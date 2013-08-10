package ramirez57.YGO;

public class Rank {

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
	
}
