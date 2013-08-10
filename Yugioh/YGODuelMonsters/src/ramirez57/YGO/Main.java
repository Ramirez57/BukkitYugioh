package ramirez57.YGO;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

import javax.script.*;

public class Main extends JavaPlugin implements Listener {
	public Stack<String> dueling;

	public static void giveReward(Player player, int id) {
		ItemStack card = new ItemStack(Material.PAPER);
		Card reward = Card.fromId(id).freshCopy();
		Duelist.createOwnedCardInfo(card, reward);
		card.setDurability((short)1021);
		HashMap<Integer, ItemStack> extra = player.getInventory().addItem(card);
		if(!extra.isEmpty()) {
			player.getWorld().dropItem(player.getLocation(), extra.get(0));
		}
	}
	
	public void onEnable() {
		this.dueling = new Stack<String>();
		GuardianStar.init();
		PluginVars.dirCards = new File(this.getDataFolder(), "cards");
		PluginVars.saveData = new File(this.getDataFolder(), "SAVEDATA");
		PluginVars.logger = this.getLogger();
		PluginVars.plugin = this;
		PluginVars.engineMgr = new ScriptEngineManager();
		PluginVars.engine = PluginVars.engineMgr.getEngineByName("JavaScript");
		PluginVars.engineinv = (Invocable)PluginVars.engine;
		try {
			PluginVars.engine.eval("importPackage(Packages.ramirez57.YGO);\n");
		} catch (ScriptException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.getServer().getPluginManager().registerEvents(this, this);
		Card.loadCards();
		PluginVars.loadStarterDeck(new File(this.getDataFolder(), "starter.yml"));
		PluginVars.load();
		Fusion.loadFusions(new File(this.getDataFolder(), "fusions.yml"));
	}

	public void onDisable() {

	}

	public static void giveLore(ItemStack i, int amnt) {
		ItemMeta m;
		List<String> lore = new ArrayList<String>();
		for (int j = 0; j < amnt; j++) {
			lore.add("0");
		}
		m = i.getItemMeta();
		m.setLore(lore);
		i.setItemMeta(m);
	}

	public static String getItemData(ItemStack i, int pos) {
		ItemMeta m;
		m = i.getItemMeta();
		if (m.hasLore()) {
			return m.getLore().get(pos);
		}
		return "";
	}

	public static void setItemData(ItemStack i, int pos, String s) {
		ItemMeta m;
		List<String> lore;
		m = i.getItemMeta();
		if (m.hasLore()) {
			lore = m.getLore();
			lore.set(pos, s);
			m.setLore(lore);
			i.setItemMeta(m);
		} else {
			m = i.getItemMeta();
			lore = new ArrayList<String>(pos + 1);
			lore.set(pos, s);
			m.setLore(lore);
			i.setItemMeta(m);
		}
	}

	public static String getItemName(ItemStack i) {
		return i.getItemMeta().getDisplayName();
	}

	public static void setItemName(ItemStack i, String s) {
		ItemMeta m;
		m = i.getItemMeta();
		m.setDisplayName(s);
		i.setItemMeta(m);
	}
	
	@EventHandler
	public void duelreq(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		if(e.getRightClicked().getType() == EntityType.VILLAGER) {
			if(!PluginVars.isDuelist(e.getRightClicked().getUniqueId())) {
				if(!PluginVars.hasDeck(p)) {
					PluginVars.newYgoPlayer(p);
					PluginVars.giveStarterDeck(p);
					p.sendMessage("Obtained starter deck");
				}
			}
		}
		if(PluginVars.duel_mode.contains(p)) {
			if(e.getRightClicked().getType() == EntityType.VILLAGER) {
				UUID uuid = e.getRightClicked().getUniqueId();
				e.setCancelled(true);
				new NPCGenerator().generate(uuid);
				if(PluginVars.isDuelist(uuid)) {
					try {
						if(DeckGenerator.checkDeckInt(PluginVars.getDeckFor(p))) {
							Inventory i = Bukkit.createInventory(null, 54, "Duel Monsters");
							Duel duel = PluginVars.createDuel(e.getPlayer(), i, null, null, e.getRightClicked().getUniqueId());
							e.getPlayer().openInventory(i);
							duel.startDuel();
						} else {
							p.sendMessage("Deck is illegal! Please re-arrange it before dueling.");
						}
					} catch (NoDeckException e1) {
						p.sendMessage("You don't have a deck!");
					}
				} else {
					if(PluginVars.hasDeck(p)) {
						p.sendMessage("Doesn't play");
					} else {
						PluginVars.newYgoPlayer(p);
						PluginVars.giveStarterDeck(p);
						p.sendMessage("Obtained starter deck");
					}
				}
				
			} else if(e.getRightClicked().getType() == EntityType.PLAYER) {
				Player p2 = Player.class.cast(e.getRightClicked());
				new DuelRequest(p, p2);
			}
		}
	}
	
	@EventHandler
	public void onclick(InventoryClickEvent e) {
		Player p = (Player)e.getWhoClicked();
		if(!e.isCancelled()) {
			if(e.getInventory().getType() == InventoryType.ANVIL){
				if(e.getRawSlot() == e.getView().convertSlot(e.getRawSlot())) {
					int slot = e.getRawSlot();
					if(slot == 2) {
						ItemStack is = e.getCurrentItem();
						if(is != null) {
							ItemMeta im = is.getItemMeta();
							if(im != null) {
								if(im.hasDisplayName()) {
									String s = im.getDisplayName();
									Card card = Card.fromPassword(s);
									if(card != null) {
										Main.giveLore(is, 2);
										Main.setItemData(is, 0, card.name);
										if(card.cost == -1)
											Main.setItemData(is, 1, "Cost: N/A");
										else
											Main.setItemData(is, 1, "Cost: " + card.cost + " starchips");
									}
								}
							}
						}
					}
				}
			}
		}
		if (this.dueling.contains(e.getWhoClicked().getName())) {
			Duel duel;
			try {
				duel = Duelist.getDuelFor(p);
			} catch (NotDuelingException e3) {
				this.dueling.remove(p);
				e.setCancelled(true);
				return;
			}
			Duelist duelist = null;
			try {
				duelist = duel.getDuelistFromPlayer(p);
			} catch (NotDuelingException e2) {
				this.dueling.remove(p.getName());
				e.setCancelled(true);
				return;
			}
			if (e.getInventory().getType() == InventoryType.CHEST
					&& e.getRawSlot() <= 53
					&& e.getRawSlot() != InventoryView.OUTSIDE) {
				//p.sendMessage("PLAYING FIELD: " + e.getRawSlot());
				/*
				 * ItemStack i = e.getInventory().getItem(e.getRawSlot());
				 * if(Main.getItemName(i).equalsIgnoreCase("Position")) {
				 * if(Main.getItemData(i, 0).equalsIgnoreCase("Face-down")) {
				 * Main.setItemData(i, 0, "Face-up"); } else {
				 * Main.setItemData(i, 0, "Face-down"); } } else
				 * if(Main.getItemName(i).equalsIgnoreCase("End Turn")) { try {
				 * Duelist.getDuelFor(p).swapTurn(); } catch
				 * (NotDuelingException e1) { } } else
				 * if(Main.getItemName(i).equalsIgnoreCase("Monster Card")) {
				 * if(duel.duelists[0].cardInHand()) }
				 */
				duel.input(duelist, e.getRawSlot(), e.getClick());
			}
			if(e.getRawSlot() == InventoryView.OUTSIDE) {
				try {
					duel = Duelist.getDuelFor(p);
					duel.endDuel(duel.getDuelistFromPlayer(p).opponent, WinReason.SURRENDER);
				} catch (NotDuelingException e1) {
					// TODO Auto-generated catch block
				}
				this.dueling.removeElement(p.getName());
			}
			e.setCancelled(true);
		} else if(PluginVars.editing.get(p) != null) {
			if(e.getRawSlot() == InventoryView.OUTSIDE) {
				PluginVars.editing.get(p).close();
				e.setCancelled(true);
			} else {
				//p.sendMessage("DECKEDIT: " + e.getRawSlot());
				PluginVars.editing.get(p).input(e.getRawSlot(), e.getClick());
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void closeInventory(InventoryCloseEvent e) {
		Player p = Player.class.cast(e.getPlayer());
		if(PluginVars.editing.get(p) != null) {
			PluginVars.editing.get(p).close();
		}
		try {
			Duel duel = Duelist.getDuelFor(p);
			duel.endDuel(duel.getDuelistFromPlayer(p).opponent, WinReason.SURRENDER);
		} catch (NotDuelingException e1) {
			// TODO Auto-generated catch block
		}
		this.dueling.removeElement(p.getName());
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("ygo")) {
			if(args.length <= 0) {
				this.helpMenu(sender);
			} else if (args[0].equalsIgnoreCase("test")) {
				/*if(Player.class.isInstance(sender)) {
					Player p = (Player) sender;
					try {
						if(DeckGenerator.checkDeckInt(PluginVars.getDeckFor(p))) {
							Inventory i = this.getServer().createInventory(null, 54,
									"Duel Monsters");
							this.dueling.add(p.getName());
							Duel duel = PluginVars.createDuel(p, i, null, null, null);
							p.openInventory(i);
							duel.startDuel();
						} else {
							p.sendMessage("INVALID DECK");
						}
					} catch (NoDeckException e) {
						p.sendMessage("NO DECK");
					}
				} else {
					sender.sendMessage("Players only");
				}*/
				sender.sendMessage("Yes, hello.");
			} else if(args[0].equalsIgnoreCase("duel")) {
				if(Player.class.isInstance(sender)) {
					Player p = (Player) sender;
					if(PluginVars.duel_mode.contains(p)) {
						PluginVars.duel_mode.remove(p);
						p.sendMessage("Duelist mode: OFF");
					} else {
						PluginVars.duel_mode.add(p);
						p.sendMessage("Duelist mode: ON");
					}
				} else {
					sender.sendMessage("Only players can duel.");
				}
			} else if(args[0].equalsIgnoreCase("deck")) {
				if(Player.class.isInstance(sender)) {
					Player p = (Player) sender;
					try {
						DeckEditor.open(p, p);
					} catch (NoDeckException e) {
						p.sendMessage("You don't have a deck.");
						p.sendMessage("Right-click villagers in duelist mode until you");
						p.sendMessage("have a starter deck!");
					}
				} else {
					sender.sendMessage("Only players have decks!");
				}
			} else if(args[0].equalsIgnoreCase("convert")) {
				if(Player.class.isInstance(sender)) {
					Player p = Player.class.cast(sender);
					ItemStack is = p.getItemInHand();
					if(is.getType() == Material.PAPER) {
						String s = Main.getItemName(is);
						if(s == "") {
							p.sendMessage("You must set the password using an anvil.");
						} else {
							Card card = Card.fromPassword(s);
							if(card != null) {
								if(card.cost == -1) {
									p.sendMessage("You cannot redeem " + card.name + ".");
								} else {
									if(PluginVars.getStarchips(p) >= card.cost) {
										is = is.clone();
										is.setAmount(1);
										p.getInventory().removeItem(is);
										Main.giveReward(p, card.id);
										PluginVars.takeStarchips(p, card.cost);
									} else {
										p.sendMessage("Not enough starchips (you have " + PluginVars.getStarchips(p) + ")");
									}
								}
							} else {
								p.sendMessage("INVALID PASSWORD");
							}
						}
					} else {
						p.sendMessage("Put the password on PAPER using an anvil.");
					}
				}
			} else if(args[0].equalsIgnoreCase("starchips")) {
				if(Player.class.isInstance(sender)) {
					Player p = Player.class.cast(sender);
					p.sendMessage(PluginVars.getStarchips(p) + " starchips");
				}
			} else if(args[0].equalsIgnoreCase("help")) {
				this.helpMenu(sender);
			} else if(args[0].equalsIgnoreCase("accept")) {
				if(Player.class.isInstance(sender)) {
					Player p = Player.class.cast(sender);
					if(!this.dueling.contains(p)) {
						if(PluginVars.hasRequest(p)) {
							PluginVars.getRequestFor(p).accept();
						} else {
							p.sendMessage("You do not have any requests.");
						}
					}
				}
			} else if(args[0].equalsIgnoreCase("check")) {
				if(args.length == 2) {
					Card card = Card.fromPassword(args[1]);
					if(card == null) {
						sender.sendMessage("Invalid Password");
					} else {
						sender.sendMessage(card.name);
						if(card.cost == -1)
							sender.sendMessage("Not redeemable");
						else
							sender.sendMessage("Cost: " + card.cost + " starchips");
					}
				} else {
					sender.sendMessage("/ygo check [password]");
				}
			} else if(args[0].equalsIgnoreCase("ignore")) {
				if(Player.class.isInstance(sender)) {
					Player p = Player.class.cast(sender);
					if(PluginVars.ignoreRequests(p)) {
						p.sendMessage("Ignore Requests: ON");
					} else {
						p.sendMessage("Ignore Requests: OFF");
					}
				}
			} else if(args[0].equalsIgnoreCase("decline")) {
				if(Player.class.isInstance(sender)) {
					Player p = Player.class.cast(sender);
					if(PluginVars.hasRequest(p)) {
						PluginVars.removeRequest(PluginVars.getRequestFor(p));
						p.sendMessage("Cancelled the request");
					} else
						p.sendMessage("You do not have any requests.");
				}
			}
		} else if(cmd.getName().equalsIgnoreCase("ygoadmin")) {
			if(args.length <= 0) {
				this.helpMenuAdmin(sender);
			} else if(args[0].equalsIgnoreCase("help")) {
				this.helpMenuAdmin(sender);
			} else if(args[0].equalsIgnoreCase("get")) {
				if(Player.class.isInstance(sender)) {
					if(args.length == 2) {
						Player p = Player.class.cast(sender);
						Card card = Card.fromPassword(args[1]);
						if(card != null) {
							Main.giveReward(p, card.id);
						} else {
							p.sendMessage("Invalid password");
						}
					} else {
						sender.sendMessage("/ygoadmin get [password]");
					}
				}
			} else if(args[0].equalsIgnoreCase("give")) {
				if(args.length == 3) {
					if(Bukkit.getPlayer(args[1]).isOnline()) {
						Player p = Bukkit.getPlayer(args[1]);
						Card card = Card.fromPassword(args[2]);
						if(card != null) {
							Main.giveReward(p, card.id);
						} else {
							sender.sendMessage("Invalid password");
						}
					} else {
						sender.sendMessage("That player is not online.");
					}
				} else {
					sender.sendMessage("/ygoadmin give [player] [password]");
				}
			} else if(args[0].equalsIgnoreCase("givesc")) {
				if(args.length == 3) {
					if(Bukkit.getPlayer(args[1]) != null) {
						PluginVars.giveStarchips(Bukkit.getPlayer(args[1]), Integer.parseInt(args[2]));
					} else {
						sender.sendMessage("That player does not exist.");
					}
				} else {
					sender.sendMessage("/ygoadmin givesc [player] [amount]");
				}
			}
		}
		return true;
	}
	
	public void helpMenu(Player player) {
		this.helpMenu(CommandSender.class.cast(player));
	}
	
	public void helpMenuAdmin(CommandSender sender) {
		sender.sendMessage("Comamnds: ");
		sender.sendMessage("/ygoadmin help - Bring up this menu");
		sender.sendMessage("/ygoadmin get [password] - Get a card by its password");
		sender.sendMessage("/ygoadmin give [player] [password] - Give a card to a player");
		sender.sendMessage("/ygoadmin givesc [player] [password] - Give starchips to a player");
	}
	
	public void helpMenu(CommandSender sender) {
		sender.sendMessage("Commands:");
		sender.sendMessage("/ygo help - Bring up this menu");
		sender.sendMessage("/ygo deck - Deck editor");
		sender.sendMessage("/ygo duel - Toggle duelist mode");
		sender.sendMessage("/ygo accept - Accept duel request");
		sender.sendMessage("/ygo decline - Decline duel request");
		sender.sendMessage("/ygo ignore - Ignore all duel requests");
		sender.sendMessage("/ygo starchips - Check starchip count");
		sender.sendMessage("/ygo convert - Convert PAPER to Duel Monsters card");
		sender.sendMessage("/ygo check [password] - Check card password cost");
	}

}