package ramirez57.YGO;

import org.bukkit.event.inventory.ClickType;

public class AIAction implements Runnable {

	public Duel duel;
	public Duelist duelist;
	public int slot;
	public ClickType action;
	
	public AIAction(Duel duel, Duelist duelist, int slot, ClickType action) {
		this.duel = duel;
		this.duelist = duelist;
		this.slot = slot;
		this.action = action;
	}
	
	@Override
	public void run() {
		//TODO: Add checks if duel is still available
		if(PluginVars.duel_list.contains(this.duel)) {
			//System.out.println("Yes, this is cat: " + this.slot);
			this.duel.input(this.duelist, this.slot, this.action);
		}
	}

}
