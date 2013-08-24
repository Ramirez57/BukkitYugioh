package ramirez57.YGO;

import java.util.Collections;
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
	
	public DeckEditor() {
		
	}
	
	public static DeckEditor open(Player editor, Player deckOwner) throws NoDeckException {
		DeckEditor de = new DeckEditor();
		de.editing = editor;
		de.deckOwner = deckOwner;
		de.deck = PluginVars.getDeckFor(deckOwner);
		de.inventory = Bukkit.getServer().createInventory(editor, 45, "Deck Editor");
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
		if(slot >= 0 && slot <= 44) {
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
		}
		PluginVars.save();
		this.updateInterface();
	}
	
	public void sfx(Sound sound) {
		this.editing.playSound(this.editing.getLocation(), sound, 1.0f, 1.0f);
	}
	
	public void updateInterface() {
		this.inventory.clear();
		Collections.sort(this.deck);
		for(Integer card : this.deck) {
			ItemStack is = new ItemStack(Material.PAPER);
			Duelist.createOwnedCardInfo(is, Card.fromId(card).copy());
			this.inventory.addItem(is);
		}
	}
	
	public void addToDeck(int cardId) {
		
	}
}
