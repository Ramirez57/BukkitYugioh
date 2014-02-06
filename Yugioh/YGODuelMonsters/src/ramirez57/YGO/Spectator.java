package ramirez57.YGO;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Spectator {
	public Player spectator;
	public Player duelist;
	
	public Spectator() {
		
	}
	
	public static Spectator open(Player player, Player duelist) throws NotDuelingException {
		Spectator spec = new Spectator();
		spec.spectator = player;
		spec.duelist = duelist;
		spec.spectator.openInventory(Duelist.getDuelFor(duelist).getDuelistFromPlayer(duelist).duelInterface);
		return spec;
	}
	
	public void close() {
		PluginVars.spectating.remove(this.spectator);
		this.spectator.closeInventory();
	}
	
	public void close(boolean won, WinReason why) {
		PluginVars.spectating.remove(this.spectator);
		this.spectator.closeInventory();
		if(won) {
			this.spectator.sendMessage(this.duelist.getDisplayName() + " won by " + ChatColor.GOLD + why.name);
		} else {
			this.spectator.sendMessage(this.duelist.getDisplayName() + " lost by " + ChatColor.RED + why.name);
		}
	}
}
