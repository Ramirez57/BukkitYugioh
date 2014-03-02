package ramirez57.YGO;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Spectator {
	public Player spectator;
	public Duelist duelist;
	
	public Spectator() {
		
	}
	
	public static Spectator open(Player player, Player duelist) throws NotDuelingException {
		Spectator spec = new Spectator();
		spec.spectator = player;
		spec.duelist = Duelist.getDuelFor(duelist).getDuelistFromPlayer(duelist);
		spec.spectator.openInventory(spec.duelist.duelInterface);
		return spec;
	}
	
	public static Spectator open(Player player, Duelist duelist) {
		Spectator spec = new Spectator();
		spec.spectator = player;
		spec.duelist = duelist;
		spec.spectator.openInventory(duelist.duelInterface);
		return spec;
	}
	
	public void close() {
		PluginVars.spectating.remove(this.spectator);
		this.spectator.closeInventory();
	}
	
	public void close(boolean won, WinReason why) {
		Player p = null;
		PluginVars.spectating.remove(this.spectator);
		this.spectator.closeInventory();
		if(this.duelist.player != null) {
			if(won) {
				this.spectator.sendMessage(this.duelist.player.getDisplayName() + " won by " + ChatColor.GOLD + why.name);
			} else {
				this.spectator.sendMessage(this.duelist.player.getDisplayName() + " lost by " + ChatColor.RED + why.name);
			}
		}
	}
}
