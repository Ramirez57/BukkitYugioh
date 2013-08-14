package ramirez57.YGO;

import java.util.List;

public class RitualCard extends SpellCard {
	
	public List<Integer> materials;
	public int result;
	
	public static RitualCard create(String name, List<Integer> materials, int result) {
		RitualCard rc = new RitualCard();
		rc.name = name;
		rc.materials = materials;
		rc.result = result;
		return rc;
	}
	
	public boolean shouldActivate(Duel duel, Duelist duelist) {
		if(this.threeOfAKind()) {
			if(!duelist.field.containsCardId(this.materials.get(0), 3))
				return false;
		} else {
			for(Integer card : materials) {
				if(!duelist.field.containsCardId(card))
					return false;
			}
		}
		return true;
	}
	
	public boolean threeOfAKind() {
		return ((this.materials.get(0) == this.materials.get(1)) && (this.materials.get(1) == this.materials.get(2)));
	}
	
	public void activate(Duel duel, Duelist duelist) {
		int c;
		int materialCount = 0;
		if(this.threeOfAKind()) {
			if(duelist.field.containsCardId(this.materials.get(0), 3))
				materialCount = 3;
		} else {
			for(Integer card : materials) {
				if(duelist.field.containsCardId(card)) {
					materialCount++;
				}
			}
		}
		int zone = -1;
		if(materialCount >= this.materials.size()) {
			materialCount = this.materials.size();
			for(Integer card : this.materials) {
				if(zone == -1) {
					try {
						zone = duelist.field.getPos(duelist.field.getZoneWithCardId(card));
					} catch (NoZoneOpenException e) {
					}
				}
				duelist.field.removeCardId(card, duel, null);
			}
			MonsterCard mc = MonsterCard.class.cast(Card.fromId(this.result).freshCopy());
			mc.star = mc.stars[0];
			duelist.selectedStar = 0;
			duelist.fused = true;
			duelist.faceup = true;
			duelist.selectedCard = mc;
			duelist.selectedZone = zone;
			duelist.phase = 2;
			duel.applyTerrain(duel.terrain, mc, false);
		}
	}
}
