package ramirez57.YGO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeckGenerator {
	public static List<List<Integer>> starter_sets;
	public static int[] starter_amounts = new int[7];
	public static int THEMED = 0;
	public static int STARTER = 1;
	public static int LEVEL = 2;
	public static int POW = 3;
	public int data;
	
	public DeckGenerator() {
		
	}
	
	public static boolean checkDeckInt(List<Integer> deck) {
		if(deck.size() != 40)
			return false;
		for(Integer card : deck) {
			if(Collections.frequency(deck, card) > 3) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean checkDeck(List<Card> deck) {
		if(deck.size() != 40)
			return false;
		List<Integer> cards = new ArrayList<Integer>();
		for(Card card : deck) {
			cards.add(card.id);
		}
		for(Integer card : cards) {
			if(Collections.frequency(cards, card) > 3) {
				return false;
			}
		}
		return true;
	}
	
	public List<Card> generatePOW(int pow) {
		List<Card> result = new ArrayList<Card>();
		int numMagics = PluginVars.random.nextInt(18);
		int numMobs = 40 - numMagics;
		while(!checkDeck(result)) {
			result.clear();
			List<Card> mcards = new ArrayList<Card>();
			List<Card> scards = new ArrayList<Card>();
			if(numMagics < 40) {
				numMagics++;
				numMobs--;
			}
			
			for(Card card : Card.cards) {
				if(MonsterCard.class.isInstance(card)) {
					MonsterCard mc = MonsterCard.class.cast(card);
					if((mc.atk >= (pow-500) && mc.atk <= (pow+500)) || (mc.def >= (pow-500) && mc.def <= (pow+500))) {
						mcards.add(card);
					}
				}
			}
			
			for(Card card : Card.cards) {
				if(SpellCard.class.isInstance(card) || EquipCard.class.isInstance(card)) {
					if(EquipCard.class.isInstance(card)) {
						EquipCard ec = EquipCard.class.cast(card);
						for(Card toequip : mcards) {
							MonsterCard mc = MonsterCard.class.cast(toequip);
							if(mc.canEquip(ec)) {
								scards.add(card);
								break;
							}
						}
					} else if(!FieldCard.class.isInstance(card)) {
						scards.add(card);
					}
				}
			}
			
			for(int i = 0; i < numMobs; i++) {
				result.add(mcards.get(PluginVars.random.nextInt(mcards.size())).freshCopy());
			}
			for(int i = 0; i < numMagics; i++) {
				result.add(scards.get(PluginVars.random.nextInt(scards.size())).freshCopy());
			}
		}
		return result;
	}
	
	public List<Card> generateLeveled(int avg) {
		List<Card> result = new ArrayList<Card>();
		int numMagics = PluginVars.random.nextInt(17);
		int numMobs = 40 - numMagics;
		while(!checkDeck(result)) {
			result.clear();
			List<Card> mcards = new ArrayList<Card>();
			List<Card> scards = new ArrayList<Card>();
			if(numMagics < 40) {
				numMagics++;
				numMobs--;
			}
			for(Card card : Card.cards) {
				if(MonsterCard.class.isInstance(card)) {
					MonsterCard mc = MonsterCard.class.cast(card);
					if(avg >= 8) {
						if(mc.level >= 7)
							mcards.add(card);
					} else {
						if(mc.level >= (avg-1) && mc.level <= (avg-1)) {
							mcards.add(card);
						}
					}
				}
			}
			
			for(Card card : Card.cards) {
				if(SpellCard.class.isInstance(card) || EquipCard.class.isInstance(card)) {
					if(EquipCard.class.isInstance(card)) {
						EquipCard ec = EquipCard.class.cast(card);
						for(Card toequip : mcards) {
							MonsterCard mc = MonsterCard.class.cast(toequip);
							if(mc.canEquip(ec)) {
								scards.add(card);
								break;
							}
						}
					} else if(!FieldCard.class.isInstance(card)) {
						scards.add(card);
					}
				}
			}
			
			
			
			for(int i = 0; i < numMobs; i++) {
				result.add(mcards.get(PluginVars.random.nextInt(mcards.size())).freshCopy());
			}
			for(int i = 0; i < numMagics; i++) {
				result.add(scards.get(PluginVars.random.nextInt(scards.size())).freshCopy());
			}
		}
		return result;
	}
	
	public List<Card> generateThemed() {
		List<Card> result = new ArrayList<Card>();
		while(!checkDeck(result)) {
			result.clear();
			List<FieldCard> fields = new ArrayList<FieldCard>();
			for(Card card : Card.cards) {
				if(FieldCard.class.isInstance(card)) {
					fields.add(FieldCard.class.cast(card.freshCopy()));
				}
			}
			FieldCard fc = fields.get(PluginVars.random.nextInt(fields.size()));
			List<MonsterType> favors = fc.terrain.favors;
			List<Card> mcards = new ArrayList<Card>();
			List<Card> scards = new ArrayList<Card>();
			int numMagics = PluginVars.random.nextInt(15);
			
			for(Card card : Card.cards) {
				if(MonsterCard.class.isInstance(card)) {
					if(favors.contains(MonsterCard.class.cast(card).type)) {
						mcards.add(card);
					}
				}
			}
			
			for(Card card : Card.cards) {
				 if(SpellCard.class.isInstance(card) || EquipCard.class.isInstance(card)) {
					if(EquipCard.class.isInstance(card)) {
						EquipCard ec = EquipCard.class.cast(card);
						for(Card toequip : mcards) {
							MonsterCard mc = MonsterCard.class.cast(toequip);
							if(mc.canEquip(ec)) {
								scards.add(card);
								break;
							}
						}
					} else if(!FieldCard.class.isInstance(card)) {
						scards.add(card);
					}
				}
			}
			
			int numMobs = 37 - numMagics;
			for(int i = 0; i < numMobs; i++) {
				result.add(mcards.get(PluginVars.random.nextInt(mcards.size())).freshCopy());
			}
			for(int i = 0; i < numMagics; i++) {
				result.add(scards.get(PluginVars.random.nextInt(scards.size())).freshCopy());
			}
			for(int i = 0; i < 3; i++) {
				result.add(fc.freshCopy());
			}
		}
		return result;
	}
	
	public List<Card> generateRandom() {
		List<Card> result = new ArrayList<Card>();
		while(!DeckGenerator.checkDeck(result)) {
			result.clear();
			for(int i = 0; i < 40; i++) {
				result.add(Card.fromId(PluginVars.random.nextInt(Card.cards.length)).freshCopy());
			}
		}
		return result;
	}
	
	public List<Card> generateStarter() {
		List<Card> result = new ArrayList<Card>();
		while(!DeckGenerator.checkDeck(result)) {
			result.clear();
			for(int i = 0; i < 7; i++) {
				for(int j = 0; j < starter_amounts[i]; j++) {
					result.add(Card.fromId(starter_sets.get(i).get(PluginVars.random.nextInt(starter_sets.get(i).size()))).freshCopy());
				}
			}
		}
		return result;
	}
}
