package ramirez57.YGO;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class DuelRequest implements Runnable {
	public Player requester;
	public Player requested;
	
	public DuelRequest(Player requester, Player requested) {
		this.requester = requester;
		this.requested = requested;
		if(PluginVars.ignoringRequests(this.requested)) {
			this.requester.sendMessage("That player is ignoring requests.");
			return;
		}
		if(!PluginVars.hasDeck(this.requested)) {
			this.requester.sendMessage("That player does not have a deck.");
		} else if(!PluginVars.hasDeck(this.requester)) {
			this.requester.sendMessage("You do not have a deck.");
		} else if(!PluginVars.hasRequest(this.requested)) {
			PluginVars.requests.add(this);
			this.requester.sendMessage("Sent request to player " + this.requested.getDisplayName());
			this.requested.sendMessage(ChatColor.YELLOW + "Received duel request from " + this.requester.getDisplayName());
			this.requested.sendMessage(ChatColor.YELLOW + "It will expire in 2 minutes");
			this.requested.sendMessage(ChatColor.YELLOW + "Type '/ygo accept' to accept the request");
			Bukkit.getScheduler().scheduleSyncDelayedTask(PluginVars.plugin, this, 20*120);
		} else
			this.requester.sendMessage("That player has a request. Try again later.");
	}
	
	public void accept() {
		PluginVars.removeRequest(this);
		if(!this.requester.isOnline() || !this.requested.isOnline())
			return;
		if(PluginVars.isDueling(this.requester) || PluginVars.isDueling(this.requested)) {
			this.requested.sendMessage(this.requester.getName() + " is in a duel.");
			return;
		}
		try {
			if(DeckGenerator.checkDeckInt(PluginVars.getDeckFor(this.requester)) && DeckGenerator.checkDeckInt(PluginVars.getDeckFor(this.requested))) {
				Inventory i1 = Bukkit.getServer().createInventory(null, 54, "Duel Monsters");
				Inventory i2 = Bukkit.getServer().createInventory(null, 54, "Duel Monsters");
				Duel duel = PluginVars.createDuel(this.requester, i1, this.requested, i2, null);
				this.requester.openInventory(i1);
				this.requested.openInventory(i2);
				duel.startDuel();
			} else {
				this.requester.sendMessage("Failed to start duel. Do you have a legal deck?");
				this.requested.sendMessage("Failed to start duel. Do you have a legal deck?");
			}
		} catch (NoDeckException e) {
			this.requester.sendMessage("Failed to start duel.");
			this.requested.sendMessage("Failed to start duel.");
		}
	}
	
	public void decline() {
		PluginVars.removeRequest(this);
	}

	@Override
	public void run() {
		if(PluginVars.requests.contains(this)) {
			this.requester.sendMessage("Duel Request expired.");
			this.decline();
		}
	}
}
