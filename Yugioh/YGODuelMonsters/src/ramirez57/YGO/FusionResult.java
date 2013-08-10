package ramirez57.YGO;

import java.util.Stack;

public class FusionResult {
	
	public Card result;
	public Stack<Card> materials;
	
	public FusionResult(Card result, Card ... materials) {
		this.result = result;
		int c;
		for(c = 0; c < materials.length; c++) {
			this.materials.push(materials[c]);
		}
	}
}
