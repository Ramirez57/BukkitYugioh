package ramirez57.YGO;

import java.util.Stack;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class Tournament {

	public Stack<Entity> duelists;
	public Duel currentDuel;
	public Entity host;
	public int match_count;
	public Entity[] dueling;

	public Tournament() {

	}

	public static Tournament create(Entity host) {
		Tournament tournament = new Tournament();
		tournament.host = host;
		tournament.duelists = new Stack<Entity>();
		tournament.dueling = new Entity[2];
		tournament.duelists.push(host);
		tournament.currentDuel = null;
		tournament.match_count = 0;
		return tournament;
	}

	public void winner(Duelist duelist) {
		int num;
		if(this.currentDuel.duelists[0] == duelist)
			num = 0;
		else
			num = 1;
		this.duelists.insertElementAt(this.dueling[num], 0);
	}

	public boolean nextDuel() {
		Duel duel = null;
		Entity ent = null;
		Inventory[] i = new Inventory[2];
		int c = 0;
		if (this.duelists.size() >= 2) {
			this.match_count++;
			duel = Duel.createEmptyDuel();
			this.currentDuel = duel;
			PluginVars.duel_list.add(duel);
			duel.tournament = this;
			
			i[0] = Bukkit.createInventory(null, 54, "Tournament: Duel " + this.match_count);
			i[1] = Bukkit.createInventory(null, 54, "Tournament: Duel " + this.match_count);
			for(c = 0; c <= 1; c++) {
				ent = this.duelists.pop();
				this.dueling[c] = ent;
				duel.setDuelist(c, ent, i[c], ent.getUniqueId());
				if(ent instanceof Player) {
					((Player) ent).openInventory(i[c]);
					PluginVars.plugin.dueling.add(((Player) ent).getName());
				}
			}
			
			for(c = 0; c < this.duelists.size(); c++) {
				if(Player.class.isInstance(this.duelists.get(c))) {
					PluginVars.spectating.put(
							Player.class.cast(this.duelists.get(c)),
							Spectator.open(Player.class.cast(this.duelists.get(c)), duel.duelists[0]));
				}
			}
			duel.startDuel();
			return true;
		} else {
			if(this.duelists.size() == 1) {
				this.declareWinner(this.duelists.pop());
			}
		}
		return false;
	}
	
	public void declareWinner(Entity ent) {
		Player p = null;
		if(Player.class.isInstance(ent)) {
			p = Player.class.cast(ent);
			p.sendMessage("You are the winner! LOL");
		}
	}
}
