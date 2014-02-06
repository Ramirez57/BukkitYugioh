package ramirez57.YGO;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class NPCGenerator {

	public NPCGenerator() {
		
	}
	
	public void generate(Entity entity, UUID uuid) {
		if(PluginVars.hasDeck(uuid)) {
			return;
		} else {
			List<Card> newdeck = new ArrayList<Card>();
			List<Integer> newint = new ArrayList<Integer>();
			if(entity.getType() == EntityType.ENDERMAN) {
				if(PluginVars.random.nextBoolean()) {
					newdeck = new DeckGenerator().generateSuperThemed();
				} else {
					PluginVars.npc_nonduelists.add(uuid);
				}
			} else if(entity.getType() == EntityType.ENDER_DRAGON) {
				newdeck = new DeckGenerator().generateSPOW();
			} else {
				int rnd = PluginVars.random.nextInt(6);
				if (rnd == 0) {
					newdeck = new DeckGenerator().generateThemed();
				} else if (rnd == 1) {
					newdeck = new DeckGenerator().generateStarter();
				} else if (rnd == 2) {
					newdeck = new DeckGenerator()
							.generateLeveled(PluginVars.random.nextInt(8) + 1);
				} else if (rnd == 3) {
					newdeck = new DeckGenerator().generatePOW(PluginVars.random
							.nextInt(10) * 500);
				} else {
					PluginVars.npc_nonduelists.add(uuid);
				}
			}
			for (Card card : newdeck) {
				newint.add(card.id);
			}
			PluginVars.npc_decks.put(uuid, newint);
			PluginVars.save();
		}
	}
	
}
