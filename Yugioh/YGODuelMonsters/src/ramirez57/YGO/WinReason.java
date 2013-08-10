package ramirez57.YGO;

public class WinReason {

	public static WinReason TOTAL_ANNIHILATION = new WinReason("Total Annihilation");
	public static WinReason ATTRITION = new WinReason("Attrition");
	public static WinReason SURRENDER = new WinReason("Submission");
	public static WinReason EXODIA = new WinReason("Exodia");
	
	public String name;
	
	public WinReason(String name) {
		this.name = name;
	}
	
	public String toString() {
		return this.name;
	}
	
}
