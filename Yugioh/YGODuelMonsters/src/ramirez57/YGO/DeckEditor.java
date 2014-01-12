package ramirez57.YGO;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class DeckEditor {
	public Player editing;
	public Player deckOwner;
	public Inventory inventory;
	public List<Integer> deck;
	public int sort_mode;
	//TODO: Have sorters as their own classes for tidyness
	public static int SORT_NUMBER = 0;
	public static int SORT_ABC = 1;
	public static int SORT_ATK = 2;
	public static int SORT_DEF = 3;
	public static int SORT_TYPE = 4;
	public static int SORT_SHUFFLE = 5;
	public static Comparator<Integer> SORTER_ABC = new Comparator<Integer>() {
		@Override
		public int compare(final Integer o1, final Integer o2) {
			return Card.fromId(o1).name.compareToIgnoreCase(Card.fromId(o2).name);
		}
	};
	public static Comparator<Integer> SORTER_ATK = new Comparator<Integer>() {
		@Override
		public int compare(final Integer o1, final Integer o2) {
			Card c1,c2;
			MonsterCard mc1,mc2;
			c1 = Card.fromId(o1);
			c2 = Card.fromId(o2);
			if(SpellCard.class.isInstance(c1) || EquipCard.class.isInstance(c1)) {
				return -1;
			} else if(SpellCard.class.isInstance(c2) || EquipCard.class.isInstance(c2)) {
				return 1;
			} else {
				mc1 = MonsterCard.class.cast(c1);
				mc2 = MonsterCard.class.cast(c2);
				return Integer.compare(mc1.atk, mc2.atk);
			}
		}
	};
	public static Comparator<Integer> SORTER_DEF = new Comparator<Integer>() {
		@Override
		public int compare(final Integer o1, final Integer o2) {
			Card c1,c2;
			MonsterCard mc1,mc2;
			c1 = Card.fromId(o1);
			c2 = Card.fromId(o2);
			if(SpellCard.class.isInstance(c1) || EquipCard.class.isInstance(c1)) {
				return -1;
			} else if(SpellCard.class.isInstance(c2) || EquipCard.class.isInstance(c2)) {
				return 1;
			} else {
				mc1 = MonsterCard.class.cast(c1);
				mc2 = MonsterCard.class.cast(c2);
				return Integer.compare(mc1.def, mc2.def);
			}
		}
	};
	public static Comparator<Integer> SORTER_TYPE = new Comparator<Integer>() {
		@Override
		public int compare(final Integer o1, final Integer o2) {
			Card c1,c2;
			MonsterCard mc1,mc2;
			c1 = Card.fromId(o1);
			c2 = Card.fromId(o2);
			if(SpellCard.class.isInstance(c1) || EquipCard.class.isInstance(c1)) {
				return 1;
			} else if(SpellCard.class.isInstance(c2) || EquipCard.class.isInstance(c2)) {
				return -1;
			} else {
				mc1 = MonsterCard.class.cast(c1);
				mc2 = MonsterCard.class.cast(c2);
				return mc1.type.toString().compareToIgnoreCase(mc2.type.toString());
			}
		}
	};
	
	public DeckEditor() {
		
	}
	
	public static int nextSortValue(int x) {
		if(x == 5)
			return 0;
		else
			return x+1;
	}
	
	public static int prevSortValue(int x) {
		if(x == 0)
			return 5;
		else
			return x-1;
	}
	
	public static DeckEditor open(Player editor, Player deckOwner) throws NoDeckException {
		DeckEditor de = new DeckEditor();
		de.editing = editor;
		de.deckOwner = deckOwner;
		de.deck = PluginVars.getDeckFor(deckOwner);
		de.inventory = Bukkit.getServer().createInventory(editor, 45, "Deck Editor");
		de.sort_mode = DeckEditor.SORT_NUMBER;
		editor.openInventory(de.inventory);
		PluginVars.editing.put(editor, de);
		de.updateInterface();
		return de;
	}
	
	public void close() {
		PluginVars.editing.remove(this.editing);
		this.editing.closeInventory();
		if(!DeckGenerator.checkDeckInt(this.deck)) {
			this.editing.sendMessage("WARNING! Deck is illegal!");
			if(this.deck.size() != 40) {
				this.editing.sendMessage("You must have exactly 40 cards in your deck.");
			} else {
				this.editing.sendMessage("You can only have up to 3 copies of a single card in your deck.");
			}
		}
	}
	
	public void input(int slot, ClickType action) {
		ItemStack is = null;
		if(slot >= 0 && slot <= 43) {
			is = this.inventory.getItem(slot);
			if(is != null) {
				String s = Main.getItemData(is, 0);
				if(!s.isEmpty()) {
					if(s.startsWith("#")) {
						try {
							int id = Integer.parseInt(s.substring(1));
							this.sfx(Sound.CLICK);
							this.deck.remove((Object)id);
							Main.giveReward(this.editing, id);
						} catch (NumberFormatException e) {
							
						}
					}
				}
			}
		} else if(slot >= 45 && slot <= 80) {
			if(slot >= 45 && slot <= 71) {
				is = this.editing.getInventory().getItem(slot-36);
			} else if(slot >= 72 && slot <= 80) {
				is = this.editing.getInventory().getItem(slot-72);
			}
			if(is != null) {
				String s = Main.getItemData(is, 0);
				if(!s.isEmpty()) {
					if(s.startsWith("#")) {
						try {
							int id = Integer.parseInt(s.substring(1));
							if(this.deck.size() >= 40) {
								this.editing.sendMessage("Cannot add more than 40 cards.");
								this.sfx(Sound.FIRE_IGNITE);
								return;
							} else if(Collections.frequency(this.deck, id) >= 3) {
								this.editing.sendMessage("Cannot have more than 3 copies of a single card.");
								this.sfx(Sound.FIRE_IGNITE);
								return;
							}
							this.deck.add(id);
							this.sfx(Sound.CLICK);
							is = is.clone();
							is.setAmount(1);
							this.editing.getInventory().removeItem(is);
						} catch (NumberFormatException e) {
							
						}
					}
				}
			}
		} else if(slot == 44) {
			if(action == ClickType.LEFT)
				this.sort_mode = DeckEditor.nextSortValue(this.sort_mode);
			else
				this.sort_mode = DeckEditor.prevSortValue(this.sort_mode);
			this.sfx(Sound.CLICK);
		}
		PluginVars.save();
		this.updateInterface();
	}
	
	public void sfx(Sound sound) {
		this.editing.playSound(this.editing.getLocation(), sound, 1.0f, 1.0f);
	}
	
	public void updateInterface() {
		ItemStack is = null;
		this.inventory.clear();
		if(this.sort_mode == DeckEditor.SORT_NUMBER) {
			Collections.sort(this.deck);
		} else if(this.sort_mode == DeckEditor.SORT_ABC) {
			Collections.sort(this.deck, DeckEditor.SORTER_ABC);
		} else if(this.sort_mode == DeckEditor.SORT_ATK) {
			Collections.sort(this.deck, DeckEditor.SORTER_ATK);
			Collections.reverse(this.deck);
		} else if(this.sort_mode == DeckEditor.SORT_DEF) {
			Collections.sort(this.deck, DeckEditor.SORTER_DEF);
			Collections.reverse(this.deck);
		} else if(this.sort_mode == DeckEditor.SORT_TYPE) {
			Collections.sort(this.deck);
			Collections.sort(this.deck, DeckEditor.SORTER_TYPE);
		} else if(this.sort_mode == DeckEditor.SORT_SHUFFLE) {
			Collections.shuffle(this.deck);
		}
		int p = 0;
		for(Integer card : this.deck) {
			is = new ItemStack(Material.PAPER);
			Duelist.createOwnedCardInfo(is, Card.fromId(card).copy());
			if(this.sort_mode == DeckEditor.SORT_SHUFFLE) {
				this.inventory.setItem(p, is);
				p++;
			} else {
				this.inventory.addItem(is);
			}
		}
		is = new ItemStack(Material.PAPER);
		Main.setItemName(is, "Sort by: " + DeckEditor.getSortName(this.sort_mode));
		Main.giveLore(is, 1);
		Main.setItemData(is, 0, "Click to change");
		this.inventory.setItem(44, is);
	}
	
	public static String getSortName(int x) {
		if(x == DeckEditor.SORT_NUMBER) {
			return "NUMBER";
		} else if(x == DeckEditor.SORT_ABC) {
			return "NAME";
		} else if(x == DeckEditor.SORT_ATK) {
			return "ATK";
		} else if(x == DeckEditor.SORT_DEF) {
			return "DEF";
		} else if(x == DeckEditor.SORT_TYPE) {
			return "TYPE";
		} else if(x == DeckEditor.SORT_SHUFFLE) {
			return "SHUFFLE";
		}
		return "N/A";
	}
	
	public void addToDeck(int cardId) {
		
	}
}
