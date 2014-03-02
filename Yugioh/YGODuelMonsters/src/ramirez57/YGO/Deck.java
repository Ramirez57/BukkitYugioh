package ramirez57.YGO;

import java.util.Collections;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

import org.bukkit.entity.Player;

public class Deck {
	public Stack<Card> cards;
	//public Duelist owner; Do not use

	public int getAvgLv() {
		int lv = 0;
		int num = 0;
		for(Card c : cards) {
			if(MonsterCard.class.isInstance(c)) {
				lv += MonsterCard.class.cast(c).level;
				num++;
			}
		}
		if(num != 0)
			return (lv/num);
		else
			return 0;
	}
	
	public void shuffle() {
		Collections.shuffle(cards, PluginVars.random);
	}

	public static Deck fromUUID(Duelist d, UUID uuid) {
		Deck deck = new Deck();
		deck.cards = new Stack<Card>();
		Player p = d.player;
		if (p == null) {
			if (PluginVars.hasDeck(uuid)) {
				for (Integer card : PluginVars.npc_decks.get(uuid)) {
					deck.cards.push(Card.fromId(card).freshCopy());
				}
			} else {
				for (Integer card : PluginVars.npc_decks.get(uuid)) {
					deck.cards.push(Card.fromId(card).freshCopy());
				}
				PluginVars.save();
			}
		}
		return deck;
	}

	public static Deck fromPlayer(Duelist d) throws NoDeckException {
		Deck deck = new Deck();
		deck.cards = new Stack<Card>();
		Player p = d.player;
		if (p == null) {
			List<Card> npcdeck = new DeckGenerator().generateThemed();
			for (int i = 0; i < 40; i++) {
				deck.cards.push(npcdeck.get(i).freshCopy());
			}
		} else {
			List<Integer> player_deck = null;
			player_deck = PluginVars.getDeckFor(p);
			if (player_deck.size() == 40) {
				for (int i = 0; i < 40; i++) {
					deck.cards
							.push(Card.fromId(player_deck.get(i)).freshCopy());
				}
			}
		}
		return deck;
	}

	public void draw(Duelist drawer, int amnt) {
		drawer.hand.addCard(this.cards.pop());
	}
}
