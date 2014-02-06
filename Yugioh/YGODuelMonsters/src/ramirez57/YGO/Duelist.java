package ramirez57.YGO;

import java.util.Iterator;
import java.util.Stack;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Duelist {
	public Player player;
	public Duelist opponent;
	public Deck deck;
	public Hand hand;
	public Field field;
	public Inventory duelInterface;
	public int phase;
	public int lp;
	public Card selectedCard;
	public int selectedStar;
	public boolean faceup;
	public Stack<Card> fusion_mat;
	public boolean fused;
	public int selectedZone;
	public Stack<Card> rewards;
	public int swords;
	public int trigger_trap;
	public int card_destruction;
	public int pure_magic;
	public int equip_magic;
	public int initiate_fusion;
	public int combo_plays;
	public int change_field;
	public int turns;
	
	public Duelist() {
		this.fusion_mat = new Stack<Card>();
		this.rewards = new Stack<Card>();
		this.swords = 0;
		this.card_destruction = 0;
		this.trigger_trap = 0;
		this.pure_magic = 0;
		this.initiate_fusion = 0;
		this.combo_plays = 0;
		this.equip_magic = 0;
		this.change_field = 0;
		this.turns = 0;
	}
	
	public static Duelist fromPlayer(Player p, Inventory interf, UUID uuid) throws NoDeckException {
		Duelist d = new Duelist();
		d.player = p;
		if(p == null)
			d.deck = Deck.fromUUID(d, uuid);
		else
			d.deck = Deck.fromPlayer(d);
		d.field = new Field();
		d.hand = new Hand();
		if(p != null && interf != null)
			d.duelInterface = interf;
		return d;
	}
	
	public static Duel getDuelFor(Player p) throws NotDuelingException {
		Iterator<Duel> duels = PluginVars.duel_list.iterator();
		Duel d = null;
		while(duels.hasNext()) {
			d = duels.next();
			if(d.containsPlayer(p)) {
				return d;
			}
		}
		throw new NotDuelingException();
	}
	
	public boolean drawUntil(int amnt) {
		while(this.hand.size() < amnt) {
			if(this.deck.cards.empty())
				return false;
			else
				this.hand.drawFrom(this.deck);
		}
		return true;
	}
	
	public boolean cardInHand(Card card) {
		int c;
		for(c=0;c < this.hand.size(); c++) {
			if(this.hand.cards.elementAt(c) == card)
				return true;
		}
		return false;
	}
	
	public boolean playCardFromHand(Duel duel, Card c, boolean faceup) {
		if(MonsterCard.class.isInstance(c)) {
			try {
				this.field.getFirstOpenMonsterZone().put(c);
				c.faceup = faceup;
			} catch (NoZoneOpenException e) {
				return false;
			}
		} else {
			return false;
		}
		this.hand.removeCard(c);
		return true;
	}
	
	public static void createOpponentCardInfo(ItemStack item, Card c) {
		if(MonsterCard.class.isInstance(c)) {
			MonsterCard mc = MonsterCard.class.cast(c);
			if(mc.faceup)
				Duelist.createCardInfo(item, c);
			else {
				Main.setItemName(item, "?");
				Main.giveLore(item, 4);
				Main.setItemData(item, 0, "[Monster] ?/?");
				Main.setItemData(item, 1, "?/?");
				Main.setItemData(item, 2, "Face-down " + mc.position.toString());
				Main.setItemData(item, 3, mc.star.toString());
			}
		} else if(SpellCard.class.isInstance(c)) {
			SpellCard sc = SpellCard.class.cast(c);
			if(sc.faceup)
				Duelist.createCardInfo(item, c);
			else {
				Main.setItemName(item, "?");
				Main.giveLore(item, 2);
				Main.setItemData(item, 0, "[Spell/Trap]");
				Main.setItemData(item, 1, "Face-down");
			}
		} else if(EquipCard.class.isInstance(c)) {
			EquipCard ec = EquipCard.class.cast(c);
			if(ec.faceup)
				Duelist.createCardInfo(item, c);
			else {
				Main.setItemName(item, "?");
				Main.giveLore(item, 2);
				Main.setItemData(item, 0, "[Spell/Trap]");
				Main.setItemData(item, 1, "Face-down");
			}
		}
	}
	
	public static void createCardInfo(ItemStack item, Card c) {
		int i;
		if(MonsterCard.class.isInstance(c)) {
			MonsterCard mc = MonsterCard.class.cast(c);
			Main.setItemName(item, mc.name);
			Main.giveLore(item, 5+mc.desc.length);
			Main.setItemData(item, 0, ChatColor.GOLD + "[Monster] " + mc.type.toString() + "/" + mc.attribute.toString());
			String s = "[";
			for(int l = 0; l < mc.level; l++) {
				s += '*';
			}
			s+="] ";
			s+=(mc.getAtk()) + "/" + (mc.getDef());
			Main.setItemData(item, 1, ChatColor.GOLD + s);
			if(c.faceup) {
				s = "Face-up ";
			} else {
				s = "Face-down ";
			}
			Main.setItemData(item, 2, ChatColor.GOLD + (s+mc.position.toString()));
			Main.setItemData(item, 3, ChatColor.GOLD + mc.star.toString());
			Main.setItemData(item, 4, ChatColor.GOLD + "+--------------+");
			for(i = 0; i < mc.desc.length; i++) {
				Main.setItemData(item, 5+i, ChatColor.GOLD + mc.desc[i]);
			}
		} else if(TrapCard.class.isInstance(c)) {
			TrapCard tc = TrapCard.class.cast(c);
			Main.setItemName(item, tc.name);
			Main.giveLore(item, 2+tc.desc.length);
			Main.setItemData(item, 0, ChatColor.RED + "[Trap/Normal]");
			for(i = 0; i < tc.desc.length; i++) {
				Main.setItemData(item, 1+i, ChatColor.RED + tc.desc[i]);
			}
			if(tc.faceup)
				Main.setItemData(item, 1+tc.desc.length, ChatColor.RED + "Face-up");
			else
				Main.setItemData(item, 1+tc.desc.length, ChatColor.RED + "Face-down");
		} else if(SpellCard.class.isInstance(c)) {
			SpellCard sc = SpellCard.class.cast(c);
			Main.setItemName(item, sc.name);
			Main.giveLore(item, 2+sc.desc.length);
			Main.setItemData(item, 0, ChatColor.DARK_GREEN + "[Spell/Normal]");
			for(i = 0; i < sc.desc.length; i++) {
				Main.setItemData(item, 1+i, ChatColor.DARK_GREEN + sc.desc[i]);
			}
			if(sc.faceup)
				Main.setItemData(item, 1+sc.desc.length, ChatColor.DARK_GREEN + "Face-up");
			else
				Main.setItemData(item, 1+sc.desc.length, ChatColor.DARK_GREEN + "Face-down");
		} else if(EquipCard.class.isInstance(c)) {
			EquipCard ec = EquipCard.class.cast(c);
			Main.setItemName(item, ec.name);
			Main.giveLore(item, 2+ec.desc.length);
			Main.setItemData(item, 0, ChatColor.DARK_AQUA + "[Spell/Equip]");
			for(i = 0; i < ec.desc.length; i++) {
				Main.setItemData(item, 1+i, ChatColor.DARK_AQUA + ec.desc[i]);
			}
			if(ec.faceup)
				Main.setItemData(item, 1+ec.desc.length, ChatColor.DARK_GREEN + "Face-up");
			else
				Main.setItemData(item, 1+ec.desc.length, ChatColor.DARK_GREEN + "Face-down");
		}
	}
	
	public static void createOwnedCardInfo(ItemStack item, Card c) {
		int i;
		if(MonsterCard.class.isInstance(c)) {
			MonsterCard mc = MonsterCard.class.cast(c);
			Main.setItemName(item, mc.name);
			Main.giveLore(item, 5+mc.desc.length);
			Main.setItemData(item, 0, "#" + c.id);
			Main.setItemData(item, 1, ChatColor.GOLD + "[Monster] " + mc.type.toString() + "/" + mc.attribute.toString());
			String s = "[";
			for(int l = 0; l < mc.level; l++) {
				s += '*';
			}
			s+="] ";
			s+=(mc.getAtk()) + "/" + (mc.getDef());
			Main.setItemData(item, 2, ChatColor.GOLD + s);
			Main.setItemData(item, 3, ChatColor.GOLD + mc.stars[0].toString() + " / " + mc.stars[1].toString());
			Main.setItemData(item, 4, ChatColor.GOLD + "+--------------+");
			for(i = 0; i < mc.desc.length; i++) {
				Main.setItemData(item, 5+i, ChatColor.GOLD + mc.desc[i]);
			}
		} else if(TrapCard.class.isInstance(c)) {
			TrapCard tc = TrapCard.class.cast(c);
			Main.setItemName(item, tc.name);
			Main.giveLore(item, 2+tc.desc.length);
			Main.setItemData(item, 0, "#" + c.id);
			Main.setItemData(item, 1, ChatColor.RED + "[Trap/Normal]");
			for(i = 0; i < tc.desc.length; i++) {
				Main.setItemData(item, 2+i, ChatColor.RED + tc.desc[i]);
			}
		} else if(SpellCard.class.isInstance(c)) {
			SpellCard sc = SpellCard.class.cast(c);
			Main.setItemName(item, sc.name);
			Main.giveLore(item, 2+sc.desc.length);
			Main.setItemData(item, 0, "#" + c.id);
			Main.setItemData(item, 1, ChatColor.DARK_GREEN + "[Spell/Normal]");
			for(i = 0; i < sc.desc.length; i++) {
				Main.setItemData(item, 2+i, ChatColor.DARK_GREEN + sc.desc[i]);
			}
		} else if(EquipCard.class.isInstance(c)) {
			EquipCard ec = EquipCard.class.cast(c);
			Main.setItemName(item, ec.name);
			Main.giveLore(item, 2+ec.desc.length);
			Main.setItemData(item, 0, "#" + c.id);
			Main.setItemData(item, 1, ChatColor.DARK_AQUA + "[Spell/Equip]");
			for(i = 0; i < ec.desc.length; i++) {
				Main.setItemData(item, 2+i, ChatColor.DARK_AQUA + ec.desc[i]);
			}
		}
	}
	
	public static void createHandCardInfo(ItemStack item, Card c) {
		int i;
		//System.out.println("Info for " + c.name + "(" + c.password + ")");
		if(MonsterCard.class.isInstance(c)) {
			MonsterCard mc = MonsterCard.class.cast(c);
			Main.setItemName(item, mc.name);
			Main.giveLore(item, 4+mc.desc.length);
			Main.setItemData(item, 0, ChatColor.GOLD + "[Monster] " + mc.type.toString() + "/" + mc.attribute.toString());
			String s = "[";
			for(int l = 0; l < mc.level; l++) {
				s += '*';
			}
			s+="] ";
			s+=(mc.getAtk()) + "/" + (mc.getDef());
			Main.setItemData(item, 1, ChatColor.GOLD + s);
			Main.setItemData(item, 2, ChatColor.GOLD + mc.stars[0].toString() + " / " + mc.stars[1].toString());
			Main.setItemData(item, 3, ChatColor.GOLD + "+--------------+");
			for(i = 0; i < mc.desc.length; i++) {
				Main.setItemData(item, 4+i, ChatColor.GOLD + mc.desc[i]);
			}
		} else if(TrapCard.class.isInstance(c)) {
			TrapCard tc = TrapCard.class.cast(c);
			Main.setItemName(item, tc.name);
			Main.giveLore(item, 1+tc.desc.length);
			Main.setItemData(item, 0, ChatColor.RED + "[Trap/Normal]");
			for(i = 0; i < tc.desc.length; i++) {
				Main.setItemData(item, 1+i, ChatColor.RED + tc.desc[i]);
			}
		} else if(SpellCard.class.isInstance(c)) {
			SpellCard sc = SpellCard.class.cast(c);
			Main.setItemName(item, sc.name);
			Main.giveLore(item, 1+sc.desc.length);
			Main.setItemData(item, 0, ChatColor.DARK_GREEN + "[Spell/Normal]");
			for(i = 0; i < sc.desc.length; i++) {
				Main.setItemData(item, 1+i, ChatColor.DARK_GREEN + sc.desc[i]);
			}
		} else if(EquipCard.class.isInstance(c)) {
			EquipCard ec = EquipCard.class.cast(c);
			Main.setItemName(item, ec.name);
			Main.giveLore(item, 1+ec.desc.length);
			Main.setItemData(item, 0, ChatColor.DARK_AQUA + "[Spell/Equip]");
			for(i = 0; i < ec.desc.length; i++) {
				Main.setItemData(item, 1+i, ChatColor.DARK_AQUA + ec.desc[i]);
			}
		}
	}
	
	public void updateInterface(Terrain terrain, Stack<Card> graveyard) {
		if(this.player == null)
			return; //NULL player = AI is dueling!
		this.duelInterface.clear();
		int c;
		Inventory i = this.duelInterface;
		ItemStack item;
		if(terrain.hasTexture()) {
			for(c = 0; c < 5; c++) {
				for(int d = 1; d < 5; d++) {
					i.setItem((d*9)+c, terrain.getTexture());
				}
			}
		}
		if(this.phase == 0) {
			item = new ItemStack(Material.SIGN);
			Main.setItemName(item, "Info");
			Main.giveLore(item, 1);
			Main.setItemData(item, 0, "Wait your turn...");
			i.setItem(5, item);
		}
		for(c=0; c < this.opponent.hand.size(); c++) {
			item = new ItemStack(Material.PAPER);
			Main.setItemName(item, "?");
			i.setItem(c, item);
		}
		for(c=0; c < this.hand.size(); c++) {
			item = new ItemStack(Material.PAPER);
			Duelist.createHandCardInfo(item, this.hand.cards.elementAt(c));
			i.setItem(45+c, item);
		}
		for(c=0; c < this.field.monsterzones.length; c++) {
			if(this.field.monsterzones[c].card != null) {
				item = new ItemStack(Material.PAPER);
				Duelist.createCardInfo(item, this.field.monsterzones[c].card);
				i.setItem(27+c, item);
			}
			if(this.field.magiczones[c].card != null) {
				item = new ItemStack(Material.PAPER);
				Duelist.createCardInfo(item, this.field.magiczones[c].card);
				i.setItem(36+c, item);
			}
			if(this.opponent.field.monsterzones[c].card != null) {
				item = new ItemStack(Material.PAPER);
				Duelist.createOpponentCardInfo(item, this.opponent.field.monsterzones[c].card);
				i.setItem(22-c, item);
			}
			if(this.opponent.field.magiczones[c].card != null) {
				item = new ItemStack(Material.PAPER);
				Duelist.createOpponentCardInfo(item, this.opponent.field.magiczones[c].card);
				i.setItem(13-c, item);
			}
		}
		if(this.swords > 0) {
			item = new ItemStack(Material.PAPER);
			Main.setItemName(item, "Swords of Revealing Light: " + this.swords);
			i.setItem(53, item);
		}
		if(this.phase == 1) {
			item = new ItemStack(Material.SIGN);
			Main.setItemName(item, "Info");
			Main.giveLore(item, 2);
			Main.setItemData(item, 0, "<-----");
			Main.setItemData(item, 1, "Select card to play");
			i.setItem(50, item);
			if(this.fusion_mat.size() >= 1) {
				item = new ItemStack(Material.SIGN);
				Main.setItemName(item, "Info");
				Main.giveLore(item, 2);
				Main.setItemData(item, 0, "<-----");
				Main.setItemData(item, 1, "Select zone to fuse.");
				i.setItem(32, item);
				for(c=0; c < this.fusion_mat.size(); c++) {
					item = new ItemStack(Material.PAPER);
					Main.setItemName(item, (c+1) + ". " + this.fusion_mat.get(c).name);
					i.setItem(8+(c*9), item);
				}
			}
		}
		if(this.phase == 2) {
			if(MonsterCard.class.isInstance(this.selectedCard)) {
				MonsterCard mc = MonsterCard.class.cast(this.selectedCard);
				if(this.fused) {
					item = new ItemStack(Material.SIGN);
					Duelist.createHandCardInfo(item, this.selectedCard);
					i.setItem(25, item);
					item = new ItemStack(Material.PAPER);
					Main.setItemName(item, "Confirm");
					i.setItem(26, item);
					item = new ItemStack(Material.PAPER);
					Main.setItemName(item, "Guardian Star");
					Main.giveLore(item, 3);
					if(this.selectedStar == 0)
						Main.setItemData(item, 0, "[" + mc.stars[0].toString() + "]");
					else
						Main.setItemData(item, 0, mc.stars[0].toString());
					if(this.selectedStar == 1)
						Main.setItemData(item, 1, "[" + mc.stars[1].toString() + "]");
					else
						Main.setItemData(item, 1, mc.stars[1].toString());
					Main.setItemData(item, 2, "Click to change");
					i.setItem(35, item);
				} else {
					item = new ItemStack(Material.PAPER);
					Main.setItemName(item, "Position");
					Main.giveLore(item, 2);
					if(this.faceup)
						Main.setItemData(item, 0, "Face-up");
					else
						Main.setItemData(item, 0, "Face-down");
					Main.setItemData(item, 1, "Click to change");
					i.setItem(26, item);
					
					item = new ItemStack(Material.PAPER);
					Main.setItemName(item, "Guardian Star");
					Main.giveLore(item, 3);
					if(this.selectedStar == 0)
						Main.setItemData(item, 0, "[" + mc.stars[0].toString() + "]");
					else
						Main.setItemData(item, 0, mc.stars[0].toString());
					if(this.selectedStar == 1)
						Main.setItemData(item, 1, "[" + mc.stars[1].toString() + "]");
					else
						Main.setItemData(item, 1, mc.stars[1].toString());
					Main.setItemData(item, 2, "Click to change");
					i.setItem(35, item);
					item = new ItemStack(Material.PAPER);
					Main.setItemName(item, "Cancel");
					i.setItem(44, item);
					item = new ItemStack(Material.SIGN);
					Main.setItemName(item, "Info");
					Main.giveLore(item, 2);
					Main.setItemData(item, 0, "<-----");
					Main.setItemData(item, 1, "Select monster card zone");
					i.setItem(32, item);
				}
			}
		}
		item = new ItemStack(Material.PAPER);
		Main.setItemName(item, "DUEL INFO");
		Main.giveLore(item, 5);
		Main.setItemData(item, 0, "Opponent's LP: " + this.opponent.lp);
		Main.setItemData(item, 1, "Opponent's Deck: " + this.opponent.deck.cards.size());
		Main.setItemData(item, 2, "Your LP: " + this.lp);
		Main.setItemData(item, 3, "Your Deck: " + this.deck.cards.size());
		Main.setItemData(item, 4, "Field: " + terrain.toString());
		i.setItem(7, item);
		if(this.phase == 3) {
			item = new ItemStack(Material.PAPER);
			Main.setItemName(item, "End Turn");
			i.setItem(42,item);
			item = new ItemStack(Material.SIGN);
			Main.setItemName(item, "Info");
			Main.giveLore(item, 2);
			Main.setItemData(item, 0, "Left click to battle.");
			Main.setItemData(item, 1, "Right click to change position.");
			i.setItem(32,item);
			item = new ItemStack(Material.SIGN);
			Main.setItemName(item, "Info");
			Main.giveLore(item, 1);
			Main.setItemData(item, 0, "Left click to activate spell/traps.");
			i.setItem(41, item);
		}
		if(this.phase == 4) {
			item = new ItemStack(Material.PAPER);
			Main.setItemName(item, "Activate now");
			i.setItem(26, item);
			item = new ItemStack(Material.PAPER);
			Main.setItemName(item, "Cancel");
			i.setItem(35, item);
			item = new ItemStack(Material.SIGN);
			Main.setItemName(item, "Info");
			Main.giveLore(item, 2);
			Main.setItemData(item, 0, "<-----");
			Main.setItemData(item, 1, "Click S/T Zone to set");
			i.setItem(41, item);
		}
		if(this.phase == 5) {
			item = new ItemStack(Material.SIGN);
			Main.setItemName(item, "Info");
			Main.giveLore(item, 1);
			Main.setItemData(item, 0, "Select card to attack.");
			i.setItem(23,item);
		}
		if(this.phase == 6) {
			item = new ItemStack(Material.SIGN);
			Main.setItemName(item, "Info");
			Main.giveLore(item, 2);
			Main.setItemData(item, 0, "<-----");
			Main.setItemData(item, 1, "Select monster to equip.");
			i.setItem(32, item);
			Main.setItemData(item, 0, "<-----");
			Main.setItemData(item, 1, "Select S/T Zone to set.");
			i.setItem(41, item);
			item = new ItemStack(Material.PAPER);
			Main.setItemName(item, "Cancel");
			i.setItem(26, item);
		} else if(this.phase == 7) {
			item = new ItemStack(Material.SIGN);
			Main.setItemName(item, "Info");
			Main.giveLore(item, 2);
			Main.setItemData(item, 0, "<-----");
			Main.setItemData(item, 1, "Select monster to equip.");
			i.setItem(32, item);
			item = new ItemStack(Material.PAPER);
			Main.setItemName(item, "Cancel");
			i.setItem(26, item);
		}
		if(this.phase == 100) {
			item = new ItemStack(Material.SIGN);
			Main.setItemName(item, "YOU WIN");
			i.setItem(22,item);
		}
		item = new ItemStack(Material.SIGN);
		Main.setItemName(item, "Graveyard");
		Main.giveLore(item, graveyard.size());
		for(c = 0; c < graveyard.size(); c++) {
			Main.setItemData(item, c, graveyard.elementAt(c).name);
		}
		i.setItem(16, item);
	}
}
