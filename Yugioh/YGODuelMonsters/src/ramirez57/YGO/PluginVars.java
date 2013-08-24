package ramirez57.YGO;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Stack;
import java.util.UUID;
import java.util.logging.Logger;

import javax.script.*;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class PluginVars {
	public static File dirCards = null;
	public static Random random = new Random(System.nanoTime());
	public static List<Duel> duel_list = new ArrayList<Duel>();
	public static HashMap<Player, DeckEditor> editing = new HashMap<Player, DeckEditor>();
	public static Logger logger = null;
	public static Main plugin = null;
	public static ScriptEngineManager engineMgr;
	public static ScriptEngine engine;
	public static Invocable engineinv;
	public static HashMap<String, List<Integer>> player_decks = new HashMap<String, List<Integer>>();
	public static HashMap<String, Stack<Integer>> player_trunks;
	public static File saveData = null;
	public static List<List<Integer>> starter_sets = new ArrayList<List<Integer>>();
	public static List<Player> duel_mode = new ArrayList<Player>();
	public static List<Player> admin_edit = new ArrayList<Player>();
	public static HashMap<UUID, List<Integer>> npc_decks = new HashMap<UUID, List<Integer>>();
	public static List<UUID> npc_nonduelists = new ArrayList<UUID>();
	public static HashMap<String, Integer> player_chips = new HashMap<String, Integer>();
	public static List<DuelRequest> requests = new ArrayList<DuelRequest>();
	public static List<String> ignore_requests = new ArrayList<String>();
	
	public static void newYgoPlayer(Player p) {
		PluginVars.player_decks.put(p.getName(), new Stack<Integer>());
	}
	
	public static void addAdminEditor(Player p) {
		PluginVars.admin_edit.add(p);
	}
	
	public static boolean isAdminEditor(Player p) {
		return PluginVars.admin_edit.contains(p);
	}
	
	public static boolean removeAdminEditor(Player p) {
		return PluginVars.admin_edit.remove(p);
	}
	
	public static boolean ignoreRequests(Player p) {
		if(ignore_requests.contains(p.getName())) {
			ignore_requests.remove(p.getName());
			return false;
		} else {
			ignore_requests.add(p.getName());
			return true;
		}
	}
	
	public static boolean ignoringRequests(Player p) {
		return ignore_requests.contains(p.getName());
	}
	
	public static boolean hasRequest(Player p) {
		for(DuelRequest req : PluginVars.requests) {
			if(req.requested == p)
				return true;
		}
		return false;
	}
	
	public static DuelRequest getRequestFrom(Player p) {
		for(DuelRequest req : PluginVars.requests) {
			if(req.requester == p)
				return req;
		}
		return null;
	}
	
	public static boolean requesting(Player p) {
		for(DuelRequest req : PluginVars.requests) {
			if(req.requester == p)
				return true;
		}
		return false;
	}
	
	public static DuelRequest getRequestFor(Player p) {
		for(DuelRequest req : PluginVars.requests) {
			if(req.requested == p)
				return req;
		}
		return null;
	}
	
	public static boolean removeRequest(DuelRequest req) {
		return PluginVars.requests.remove(req);
	}
	
	public static int getStarchips(Player p) {
		PluginVars.checkStarchips(p);
		return PluginVars.player_chips.get(p.getName());
	}
	
	public static void giveStarchips(Player p, int amnt) {
		PluginVars.checkStarchips(p);
		PluginVars.player_chips.put(p.getName(), PluginVars.player_chips.get(p.getName())+amnt);
		PluginVars.checkStarchips(p);
		PluginVars.save();
	}
	
	public static void checkStarchips(Player p) {
		if(PluginVars.player_chips.get(p.getName()) == null || PluginVars.player_chips.get(p.getName()) < 0) {
			PluginVars.player_chips.put(p.getName(), 0);
		}
	}
	
	public static void takeStarchips(Player p, int amnt) {
		PluginVars.checkStarchips(p);
		PluginVars.player_chips.put(p.getName(), PluginVars.getStarchips(p)-amnt);
		PluginVars.checkStarchips(p);
		PluginVars.save();
	}
	
	public static void giveStarterDeck(Player p) {
		List<Card> starter = new DeckGenerator().generateStarter();
		List<Integer> saved = new ArrayList<Integer>();
		for(Card card : starter) {
			saved.add(card.id);
		}
		PluginVars.player_decks.put(p.getName(), saved);
		PluginVars.save();
	}
	
	public static boolean isDuelist(UUID uuid) {
		return !npc_nonduelists.contains(uuid);
	}
	
	public static boolean hasDeck(Player p) {
		return (player_decks.get(p.getName()) != null);
	}
	
	public static boolean hasDeck(UUID uuid) {
		return (npc_decks.get(uuid) != null);
	}
	
	public static void save() {
		try {
			FileOutputStream fos = new FileOutputStream(PluginVars.saveData);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(player_decks);
			oos.writeObject(npc_decks);
			oos.writeObject(npc_nonduelists);
			oos.writeObject(player_chips);
			oos.close();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void load() {
		if(!saveData.exists()) {
			try {
				saveData.createNewFile();
				PluginVars.save();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return;
		}
		try {
			FileInputStream fis = new FileInputStream(PluginVars.saveData);
			ObjectInputStream ois = new ObjectInputStream(fis);
			try {
				PluginVars.player_decks = (HashMap<String, List<Integer>>) ois.readObject();
				PluginVars.npc_decks = (HashMap<UUID, List<Integer>>) ois.readObject();
				PluginVars.npc_nonduelists = (List<UUID>) ois.readObject();
				PluginVars.player_chips = (HashMap<String, Integer>) ois.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			ois.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static List<Integer> getDeckFor(Player p) throws NoDeckException {
		List<Integer> deck = PluginVars.player_decks.get(p.getName());
		if(deck == null)
			throw new NoDeckException();
		return deck;
	}
	
	public static List<Integer> getDeckFor(UUID uuid) throws NoDeckException {
		List<Integer> deck = PluginVars.npc_decks.get(uuid);
		if(deck == null)
			throw new NoDeckException();
		return deck;
	}
	
	public static Stack<Integer> getTrunkFor(Player p) throws NoDeckException {
		Stack<Integer> deck = PluginVars.player_trunks.get(p.getName());
		if(deck == null)
			throw new NoDeckException();
		return deck;
	}
	
	public static Duel createDuel(Player p1, Inventory i1, Player p2, Inventory i2, UUID uuid) {
		Duel duel = Duel.createDuel(p1, i1, p2, i2, uuid);
		PluginVars.duel_list.add(duel);
		if(p1 != null)
			PluginVars.plugin.dueling.add(p1.getName());
		if(p2 != null)
			PluginVars.plugin.dueling.add(p2.getName());
		return duel;
	}

	public static void loadStarterDeck(File file) {
		YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
		for(int c = 0; c < 7; c++) {
			DeckGenerator.starter_amounts[c] = config.getInt("set." + (c+1) + ".amount");
			PluginVars.starter_sets.add(config.getIntegerList("set." + (c+1) + ".cards"));
		}
		DeckGenerator.starter_sets = PluginVars.starter_sets;
	}

	public static boolean isDueling(Player p) {
		return PluginVars.plugin.dueling.contains(p.getName());
	}
}
