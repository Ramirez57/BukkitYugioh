package ramirez57.YGO;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class CommuFusion {
	public Player editing;
	public Player receiver;
	public Inventory inventory;
	public List<Integer> recipe;
	
	public CommuFusion() {
		
	}
	
	public static CommuFusion open(Player editor, Player receiver) {
		CommuFusion cf = new CommuFusion();
		cf.editing = editor;
		cf.receiver = receiver;
		cf.inventory = Bukkit.getServer().createInventory(editor, 9, "Commu Fusion (3 or 4 cards)");
		cf.recipe = new ArrayList<Integer>();
		editor.openInventory(cf.inventory);
		cf.updateInterface();
		return cf;
	}
	
	public void input(int slot, ClickType action) {
		ItemStack is = null;
		if(slot >= 0 && slot <= 8) {
			if(slot == 8) {
				this.sfx(Sound.CLICK);
				this.close(true);
				return;
			}
			is = this.inventory.getItem(slot);
			if(is != null) {
				String s = Main.getItemData(is, 0);
				if(!s.isEmpty()) {
					if(s.startsWith("#")) {
						try {
							int id = Integer.parseInt(s.substring(1));
							this.sfx(Sound.CLICK);
							this.recipe.remove((Object)id);
							Main.giveReward(this.editing, id);
						} catch (NumberFormatException e) {
							
						}
					}
				}
			}
		} else if(slot >= 9 && slot <= 45) {
			if(slot >= 9 && slot <= 35) {
				is = this.editing.getInventory().getItem(slot);
			} else if(slot >= 36 && slot <= 45) {
				is = this.editing.getInventory().getItem(slot-36);
			}
			if(is != null) {
				String s = Main.getItemData(is, 0);
				if(!s.isEmpty()) {
					if(s.startsWith("#")) {
						try {
							int id = Integer.parseInt(s.substring(1));
							if(this.recipe.size() >= 4) {
								this.editing.sendMessage("Cannot fuse more than 4 cards.");
								this.sfx(Sound.FIRE_IGNITE);
								return;
							}
							this.recipe.add(id);
							this.sfx(Sound.CLICK);
							is = is.clone();
							is.setAmount(1);
							this.editing.getInventory().removeItem(is);
						} catch (NumberFormatException e) {
							
						}
					}
				}
			}
		}
		this.updateInterface();
	}
	
	public void returnCards() {
		for(Integer card : this.recipe) {
			Main.giveReward(this.editing, card);
		}
	}
	
	public int fuse() throws NoCardCreatedException {
		HashMap<GuardianStar, Integer> stars = new HashMap<GuardianStar, Integer>();
		HashMap<MonsterType, Integer> types = new HashMap<MonsterType, Integer>();
		List<GuardianStar> mode_star = new ArrayList<GuardianStar>();
		List<MonsterType> mode_type = new ArrayList<MonsterType>();
		int avg_atk = 0;
		int avg_def = 0;
		int avg_lv = 0;
		
		int counter;
		
		Card c = null;
		MonsterCard mc = null;
		RitualCard rc = null;
		EquipCard ec = null;
		
		List<MonsterCard> monster_cards = new ArrayList<MonsterCard>();
		List<SpellCard> spell_cards = new ArrayList<SpellCard>();
		List<RitualCard> ritual_cards = new ArrayList<RitualCard>();
		List<EquipCard> equip_cards = new ArrayList<EquipCard>();
		List<FieldCard> field_cards = new ArrayList<FieldCard>();
		List<TrapCard> trap_cards = new ArrayList<TrapCard>();
		
		// sort the cards first
		for(Integer card : this.recipe) {
			c = Card.fromId(card).copy();
			if(MonsterCard.class.isInstance(c)) {
				monster_cards.add(MonsterCard.class.cast(c));
			} else if(EquipCard.class.isInstance(c)) {
				equip_cards.add(EquipCard.class.cast(c));
			} else if(RitualCard.class.isInstance(c)) {
				ritual_cards.add(RitualCard.class.cast(c));
			} else if(FieldCard.class.isInstance(c)) {
				field_cards.add(FieldCard.class.cast(c));
			} else if(TrapCard.class.isInstance(c)) {
				trap_cards.add(TrapCard.class.cast(c));
			} else if(SpellCard.class.isInstance(c)) {
				spell_cards.add(SpellCard.class.cast(c));
			}
		}
		
		int i;
		
		for(i = 0; i < monster_cards.size(); i++) {
			mc = monster_cards.get(i);
			avg_atk += mc.atk;
			avg_def += mc.def;
			avg_lv += mc.level;
			for(counter = 0; counter < 2; counter++) {
				if(stars.get(mc.stars[counter]) == null) {
					stars.put(mc.stars[counter], mc.level);
				} else {
					stars.put(mc.stars[counter], stars.get(mc.stars[counter])+mc.level);
				}
			}
			if(types.get(mc.type) == null) {
				types.put(mc.type, mc.level);
			} else {
				types.put(mc.type, types.get(mc.type)+mc.level);
			}
		}
		
		//calculate results
		if(monster_cards.size() > 0) {
			avg_atk /= monster_cards.size();
			avg_def /= monster_cards.size();
			avg_lv /= monster_cards.size();
		} else {
			avg_atk = 0;
			avg_def = 0;
			avg_lv = 0;
		}
		
		int maxFreq = 0;
		
		//modes
		//get the max frequency first in case of multiple modes
		for(Map.Entry<GuardianStar, Integer> entry : stars.entrySet()) {
			if(entry.getValue() > maxFreq)
				maxFreq = entry.getValue();
		}
		
		for(Map.Entry<GuardianStar, Integer> entry : stars.entrySet()) {
			if(entry.getValue() == maxFreq)
				mode_star.add(entry.getKey());
		}
		
		maxFreq = 0;
		
		//and then types
		for(Map.Entry<MonsterType, Integer> entry : types.entrySet()) {
			if(entry.getValue() > maxFreq)
				maxFreq = entry.getValue();
		}
		
		for(Map.Entry<MonsterType, Integer> entry : types.entrySet()) {
			if(entry.getValue() == maxFreq)
				mode_type.add(entry.getKey());
		}
		
		//magics remove star/type limitation
		if(spell_cards.size() > 0) {
			mode_type = null;
			mode_star = null;
		}
		
		//traps remove level limitation
		if(trap_cards.size() > 0) {
			avg_lv = -1;
		}
		
		//equips add half bonus to avg atk/def for every working monster
		for(i = 0; i < equip_cards.size(); i++) {
			for(int j = 0; j < monster_cards.size(); j++) {
				mc = monster_cards.get(j);
				ec = equip_cards.get(i);
				if(mc.canEquip(ec)) {
					avg_atk += (ec.incrementBy / 2);
					avg_def += (ec.incrementBy / 2);
				}
			}
		}
		
		//and finally get result cards
		int min_atk = avg_atk - 250;
		int max_atk = avg_atk + 250;
		int min_def = avg_def - 250;
		int max_def = avg_def + 250;
		List<Integer> results = new ArrayList<Integer>();
		
		boolean get_ritual = false;
		boolean get_trap = false;
		boolean get_equip = false;
		boolean get_spell = false;
		
		List<Integer> rm = new ArrayList<Integer>();
		
		if(ritual_cards.size() > 0) { //Ritual cards can override result
			rm.clear();
			for(i = 0; i < ritual_cards.size(); i++) {
				rc = ritual_cards.get(i);
				for(int j : rc.materials) {
					rm.add(j);
				}
				for(int j = 0 ; j < recipe.size(); j++) {
					if(rm.contains(recipe.get(j))) {
						rm.remove(recipe.get(j));
					}
				}
				if(rm.isEmpty()) {
					//SUCCESS
					return rc.result;
				}
			}
		}
		
		if(min_atk <= 0 && min_def <= 0) { //magic cards possible
			if(spell_cards.size() >= 2) {
				get_ritual = true;
			}
			if(spell_cards.size() >= 3) {
				get_spell = true;
			}
			if(trap_cards.size() >= 2) {
				get_trap = true;
			}
			if(equip_cards.size() >= 3) {
				get_equip = true;
				get_trap = true;
			}
		}
		
		/*this.editing.sendMessage("ATK: " + avg_atk + " (" + min_atk + " - " + max_atk + ")" +
				"\n DEF: " + avg_def + " (" + min_def + " - " + max_def + ")" +
				"\n LV: " + avg_lv + 
				"\n STAR: " + mode_star.get(0).name +
				"\n TYPE: " + mode_type.get(0).name);*/
		
		for(i = 1; i < Card.cards.length+1; i++) {
			c = Card.fromId(i).copy();
			if(get_ritual) {
				if(RitualCard.class.isInstance(c)) {
					results.add(c.id);
				}
			}
			if(get_spell) {
				if(SpellCard.class.isInstance(c)
						&& !RitualCard.class.isInstance(c)
						&& !EquipCard.class.isInstance(c)
						&& !TrapCard.class.isInstance(c)) {
					results.add(c.id);
				}
			}
			if(get_trap) {
				if(TrapCard.class.isInstance(c)) {
					results.add(c.id);
				}
			}
			if(get_equip) {
				if(EquipCard.class.isInstance(c)) {
					results.add(c.id);
				}
			}
			if(MonsterCard.class.isInstance(c)) {
				mc = MonsterCard.class.cast(c);
				if(		(mc.atk >= min_atk && mc.atk <= max_atk)
						&& (mc.def >= min_def && mc.def <= max_def)
						&& (avg_lv == -1 || mc.level == avg_lv)
						&& (mode_type == null || mode_type.contains(mc.type))
						&& (mode_star == null || mode_star.contains(mc.stars[0]) || mode_star.contains(mc.stars[1]))) {
					results.add(mc.id);
				}
			}
		}
		
		//Now finally get a result
		if(results.isEmpty()) {
			throw new NoCardCreatedException();
		} else {
			return results.get(PluginVars.random.nextInt(results.size()));
		}
	}
	
	public void close(boolean fuse) {
		PluginVars.commu_mode.remove(this.editing);
		this.editing.closeInventory();
		if(fuse) {
			if(!this.editing.isOnline()) {
				this.returnCards();
				return;
			} else if(!this.receiver.isOnline()) {
				this.editing.sendMessage("Player " + this.receiver.getDisplayName() + ChatColor.WHITE + " has gone offline");
				this.returnCards();
				return;
			}
			if(this.recipe.size() > 4 || this.recipe.size() < 3) {
				this.editing.sendMessage("You must use 3 or 4 cards in a commu fusion!");
				this.returnCards();
				return;
			} else {
				if(this.editing.getLocation().distance(this.receiver.getLocation()) > 6.0d) {
					this.editing.sendMessage("MISS!");
					this.editing.sendMessage("Player " + this.receiver.getDisplayName() + " has gone out of reach.");
					this.returnCards();
					return;
				}
				this.editing.getWorld().playSound(this.editing.getLocation(), Sound.FIREWORK_BLAST, 1.0f, 1.0f);
				this.receiver.getWorld().playSound(this.receiver.getLocation(), Sound.FIREWORK_BLAST, 1.0f, 1.0f);
				try {
					int result = this.fuse();
					Main.giveReward(this.receiver, result);
					this.receiver.sendMessage("Commu Fusion: " + Card.fromId(result).name);
					this.editing.sendMessage(ChatColor.GREEN + "SUCCESS!!!");
				} catch (NoCardCreatedException e) {
					this.editing.sendMessage(ChatColor.RED + "FAILED!!!\n" + ChatColor.YELLOW + "\nNo card created.");
				}
			}
		} else {
			this.editing.sendMessage("Cancelled fusion");
			this.returnCards();
		}
	}
	
	public void sfx(Sound sound) {
		this.editing.playSound(this.editing.getLocation(), sound, 1.0f, 1.0f);
	}
	
	public void updateInterface() {
		ItemStack is = null;
		this.inventory.clear();
		for(Integer card : this.recipe) {
			is = new ItemStack(Material.PAPER);
			Duelist.createOwnedCardInfo(is, Card.fromId(card).copy());
			this.inventory.addItem(is);
		}
		is = new ItemStack(Material.PAPER);
		Main.setItemName(is, "Confirm");
		Main.giveLore(is, 1);
		Main.setItemData(is, 0, "Initiate Fusion");
		this.inventory.setItem(8, is);
	}
}
