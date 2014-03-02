package ramirez57.YGO;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

public class Card implements Cloneable {
	public String name;
	public String password;
	public boolean faceup;
	public String[] desc;
	public int cost;
	public boolean obtainable;
	public int bonus;
	public int id;
	//public Duelist defeatedBy; Do not use
	public Trait[] traits;
	public int stolen;
	
	public static Card cards[]; //Use this to iterate over all cards. Do not use this to reference by ID!
	public static HashMap<String, Card> BY_PASSWORD;
	public static HashMap<Integer, Card> BY_ID;
	public static Card ANY = new Card();

	public Card() {
		this.bonus = 0;
		this.stolen = 0;
	}
	
	public static Card create(String n) {
		Card c = new Card();
		c.name = n;
		c.faceup = true;
		return c;
	}
	
	public void resetBonus(Duel duel) {
		this.bonus = 0;
		duel.applyTerrain(duel.terrain, this, false);
	}
	
	public Card copy() {
		try {
			return (Card)this.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public Card freshCopy() {
		return Card.fromId(this.id).copy();
	}
	
	//RETURNS COPY OF THE CARD FROM PASSWORD
	public static Card fromPassword(String pw) {
		Card card = Card.BY_PASSWORD.get(pw);
		if(card != null)
			return card.copy();
		else return null;
	}
	
	public static Card fromId(int id) {
		Card card = Card.BY_ID.get(id);
		if(card != null)
			return card.copy();
		else return null;
	}
	
	public boolean hasTrait(Trait trait) {
		int c;
		if(this.traits == null)
			return false;
		for(c = 0; c < this.traits.length; c++) {
			if(this.traits[c] == trait)
				return true;
		}
		return false;
	}
	
	public static String[] splitEvery(int x, String s) {
		int i = 0;
		StringBuilder sb = new StringBuilder(s);
		while((i = sb.indexOf(" ", i + x)) != -1) {
			sb.replace(i, i + 1, "\n");
		}
		return sb.toString().split("\\n");
	}
	
	public static void loadCards() {
		int c;
		int i = 0;
		MonsterCard mc;
		SpellCard sc;
		EquipCard ec;
		FieldCard fc;
		RitualCard rc;
		TrapCard tc;
		Card card;
		Card.BY_PASSWORD = new HashMap<String, Card>();
		Card.BY_ID = new HashMap<Integer, Card>();
		File[] files = PluginVars.dirCards.listFiles();
		List<YamlConfiguration> card_configs = new ArrayList<YamlConfiguration>();
		int numCards = 0;
		List<String> set_names = new ArrayList<String>();
		for(File f : files) {
			if(!f.getName().substring(0,4).equalsIgnoreCase("set_")) continue;
			card_configs.add(YamlConfiguration.loadConfiguration(f));
			set_names.add(f.getName());
		}
		for(YamlConfiguration config : card_configs) {
			numCards += config.getKeys(false).size();
		}
		String foundsets = "Found sets: ";
		for(c = 0; c < set_names.size(); c++) {
			if(c+1 >= set_names.size())
				foundsets += set_names.get(c) + ".";
			else
				foundsets += set_names.get(c) + ", ";
		}
		PluginVars.plugin.getLogger().info(foundsets + " (" + numCards + " cards)");
		Card.cards = new Card[numCards];
		c = 0;
		for(YamlConfiguration config : card_configs) {
			Set<String> cardconfigs = config.getKeys(false);
			Iterator<String> iterator = cardconfigs.iterator();
			while(iterator.hasNext()) {
				String prefix = iterator.next();
				//System.out.println(prefix);
				String s = config.getString(prefix + ".kind");
				if(s.equalsIgnoreCase("monster")) {
					mc = new MonsterCard();
					mc.id = config.getInt(prefix + ".id");
					//System.out.println("Loading " + mc.id);
					mc.name = config.getString(prefix + ".name");
					mc.atk = config.getInt(prefix + ".atk");
					mc.def = config.getInt(prefix + ".def");
					mc.level = config.getInt(prefix + ".level");
					mc.type = MonsterType.fromString(config.getString(prefix + ".type"));
					mc.attribute = MonsterAttribute.fromString(config.getString(prefix + ".attribute"));
					List<String> ls = config.getStringList(prefix + ".stars");
					mc.stars[0] = GuardianStar.fromString(ls.get(0));
					mc.stars[1] = GuardianStar.fromString(ls.get(1));
					mc.desc = Card.splitEvery(16, config.getString(prefix + ".description"));
					mc.password = prefix;
					mc.obtainable = config.getBoolean(prefix + ".obtainable");
					mc.cost = config.getInt(prefix + ".cost");
					ls = config.getStringList(prefix + ".traits");
					mc.traits = new Trait[ls.size()+1];
					for(i = 0; i < ls.size(); i++) {
						mc.traits[i] = Trait.fromString(ls.get(i));
					}
					mc.traits[ls.size()] = Trait.fromString(mc.attribute.toString());
					mc.equips = config.getIntegerList(prefix + ".equips");
					if(config.isSet(prefix + ".effect")) {
						mc.effectFile = new File(PluginVars.dirCards, config.getString(prefix + ".effect"));
						mc.effectDesc = Card.splitEvery(16, config.getString(prefix + ".effectDesc"));
					} else {
						mc.effectFile = null;
						mc.effectDesc = null;
					}
					Card.cards[c] = mc;
				} else if(s.equalsIgnoreCase("spell")) {
					sc = SpellCard.create(config.getString(prefix + ".name"), new File(PluginVars.dirCards, config.getString(prefix + ".effect")));
					sc.id = config.getInt(prefix + ".id");
					sc.desc = Card.splitEvery(16, config.getString(prefix + ".description"));
					sc.password = prefix;
					sc.obtainable = config.getBoolean(prefix + ".obtainable");
					sc.cost = config.getInt(prefix + ".cost");
					Card.cards[c] = sc;
				} else if(s.equalsIgnoreCase("equip")) {
					ec = EquipCard.create(config.getString(prefix + ".name"), config.getInt(prefix + ".power"));
					ec.id = config.getInt(prefix + ".id");
					ec.desc = Card.splitEvery(16, config.getString(prefix + ".description"));
					ec.password = prefix;
					ec.obtainable = config.getBoolean(prefix + ".obtainable");
					ec.cost = config.getInt(prefix + ".cost");
					if(config.isSet(prefix + ".equipsTo")) {
						ec.equipsTo = config.getIntegerList(prefix + ".equipsTo");
					}
					Card.cards[c] = ec;
				} else if(s.equalsIgnoreCase("field")) {
					List<String> szfavors = config.getStringList(prefix + ".favors");
					List<MonsterType> favors = new ArrayList<MonsterType>();
					for(i=0;i < szfavors.size(); i++) {
						favors.add(MonsterType.fromString(szfavors.get(i)));
					}
					szfavors = config.getStringList(prefix + ".unfavors");
					List<MonsterType> unfavors = new ArrayList<MonsterType>();
					for(i=0;i < szfavors.size(); i++) {
						unfavors.add(MonsterType.fromString(szfavors.get(i)));
					}
					fc = FieldCard.create(config.getString(prefix + ".name"), favors, unfavors, Material.matchMaterial(config.getString(prefix + ".texture")));
					fc.id = config.getInt(prefix + ".id");
					fc.desc = Card.splitEvery(16, config.getString(prefix + ".description"));
					fc.password = prefix;
					fc.obtainable = config.getBoolean(prefix + ".obtainable");
					fc.cost = config.getInt(prefix + ".cost");
					Card.cards[c] = fc;
				} else if(s.equalsIgnoreCase("ritual")) {
					rc = RitualCard.create(config.getString(prefix + ".name"), config.getIntegerList(prefix + ".materials"), config.getInt(prefix + ".result"));
					rc.id = config.getInt(prefix + ".id");
					rc.desc = Card.splitEvery(16, config.getString(prefix + ".description"));
					rc.password = prefix;
					rc.obtainable = config.getBoolean(prefix + ".obtainable");
					rc.cost = config.getInt(prefix + ".cost");
					Card.cards[c] = rc;
				} else if(s.equalsIgnoreCase("trap")) {
					tc = TrapCard.create(config.getString(prefix + ".name"), TrapEvent.fromString(config.getString(prefix + ".trigger")), new File(PluginVars.dirCards, config.getString(prefix + ".effect")));
					tc.id = config.getInt(prefix + ".id");
					tc.desc = Card.splitEvery(16, config.getString(prefix + ".description"));
					tc.password = prefix;
					tc.obtainable = config.getBoolean(prefix + ".obtainable");
					tc.cost = config.getInt(prefix + ".cost");
					Card.cards[c] = tc;
				}
				c++;
			}
		}
		for(c=0; c < Card.cards.length; c++) {
			Card.BY_PASSWORD.put(Card.cards[c].password, Card.cards[c]);
			Card.BY_ID.put(Card.cards[c].id, Card.cards[c]);
			if(c>1)
			if(Card.cards[c-1].id != c) {
				//System.out.println("Missing Card? " + c);
			}
		}
		for(c = 0; c < Card.cards.length; c++) {
			if(EquipCard.class.isInstance(Card.cards[c])) {
				ec = EquipCard.class.cast(Card.cards[c]);
				if(ec.equipsTo != null) {
					for(Integer id : ec.equipsTo) {
						if(id == -1) {
							ec.equipsToAll = true;
						} else {
							mc = MonsterCard.class.cast(Card.fromId(id));
							if(mc != null) {
								if(!mc.equips.contains(ec.id)) {
									mc.equips.add(ec.id);
								}
							}
						}
					}
				}
				ec.equipsTo = null; //nullify reference to save space
			}
		}
		PluginVars.logger.info("Loaded " + numCards + " cards.");
		//Card.printStatistics();
	}
	
	public static void printStatistics() {
		int numMonster = 0;
		int numEffect = 0;
		int numSpell = 0;
		int numRitual = 0;
		int numEquip = 0;
		int numTrap = 0;
		int numField = 0;
		int numObtainable = 0;
		for(Card c : cards) {
			if(c.obtainable) {
				numObtainable++;
			}
			if(c instanceof MonsterCard) {
				numMonster++;
				if(MonsterCard.class.cast(c).effectFile != null) {
					numEffect++;
				}
			} else if(c instanceof SpellCard) {
				numSpell++;
				if(c instanceof RitualCard) {
					numRitual++;
				} else if(c instanceof TrapCard) {
					numTrap++;
				} else if(c instanceof FieldCard) {
					numField++;
				}
			} else if(c instanceof EquipCard) {
				numEquip++;
			}
		}
		PluginVars.logger.info(numMonster + " monsters (" + numEffect + " effect)\n" +
				numSpell + " spells (" + numField + " field, " + numRitual + " ritual, " + numTrap + " trap)\n" +
				numEquip + " equips.\n" +
				numObtainable + " obtainble.");
	}
}
