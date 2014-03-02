package ramirez57.YGO;

public class Field {
	public MonsterZone[] monsterzones;
	public MagicZone[] magiczones;

	public Field() {
		this.monsterzones = new MonsterZone[5];
		for(int c = 0; c < 5; c++) {
			this.monsterzones[c] = new MonsterZone();
		}
		this.magiczones = new MagicZone[5];
		for(int c = 0; c < 5; c++) {
			this.magiczones[c] = new MagicZone();
		}
	}
	
	public boolean containsCard(Card card) {
		int c;
		for(c=0; c < 5; c++) {
			if(this.monsterzones[c].card == card)
				return true;
			if(this.magiczones[c].card == card)
				return true;
		}
		return false;
	}
	
	public boolean containsCardId(int id) {
		int c;
		for(c=0; c < 5; c++) {
			if(!this.monsterzones[c].isOpen())
				if(this.monsterzones[c].card.id == id)
					return true;
			if(!this.magiczones[c].isOpen())
				if(this.magiczones[c].card.id == id)
					return true;
		}
		return false;
	}
	
	public boolean containsCardId(int id, int amnt) {
		int c;
		int count = 0;
		for(c=0; c < 5; c++) {
			if(!this.monsterzones[c].isOpen())
				if(this.monsterzones[c].card.id == id)
					count++;
			if(!this.magiczones[c].isOpen())
				if(this.magiczones[c].card.id == id)
					count++;
		}
		return (count >= amnt);
	}

	public MonsterZone getFirstOpenMonsterZone() throws NoZoneOpenException {
		int i;
		for (i = 0; i < 5; i++) {
			if (this.monsterzones[i].isOpen()) {
				return this.monsterzones[i];
			}
		}
		throw new NoZoneOpenException();
	}
	
	public int getPos(Zone zone) throws NoZoneOpenException {
		int i;
		for(i = 0; i < 5; i++) {
			if(zone == this.monsterzones[i] || zone == this.magiczones[i])
				return i;
		}
		throw new NoZoneOpenException();
	}
	
	public boolean emptyMonsterZones() {
		int c;
		for(c = 0; c < 5; c++) {
			if(this.monsterzones[c].card != null)
				return false;
		}
		return true;
	}
	
	public boolean emptyMagicZones() {
		int c;
		for(c = 0; c < 5; c++) {
			if(this.magiczones[c].card != null)
				return false;
		}
		return true;
	}
	
	public Zone getZoneWithCard(Card card) throws NoZoneOpenException {
		int c;
		for(c = 0; c < 5; c++) {
			if(this.monsterzones[c].card == card)
				return this.monsterzones[c];
			else if(this.magiczones[c].card == card)
				return this.magiczones[c];
		}
		throw new NoZoneOpenException();
	}
	
	public Zone getZoneWithCardId(int id) throws NoZoneOpenException {
		int c;
		for(c = 0; c < 5; c++) {
			if(!this.monsterzones[c].isOpen()) {
				if(this.monsterzones[c].card.id == id) {
					return this.monsterzones[c];
				}
			} else if(!this.magiczones[c].isOpen()) {
				if(this.magiczones[c].card.id == id) {
					return this.magiczones[c];
				}
			}
		}
		throw new NoZoneOpenException();
	}
	
	public MagicZone getFirstOpenMagicZone() throws NoZoneOpenException {
		int i;
		for(i = 0; i < 5; i++) {
			if(this.magiczones[i].isOpen()) {
				return this.magiczones[i];
			}
		}
		throw new NoZoneOpenException();
	}
	
	public void destroyCard(Card card, Duel duel, Duelist rewardFor) {
		try {
			this.getZoneWithCard(card).toGraveyard(duel, rewardFor);
		} catch (NoZoneOpenException e) {
			
		}
	}
	
	public void removeCard(Card card) {
		try {
			this.getZoneWithCard(card).remove();
		} catch (NoZoneOpenException e) {
		}
	}
	
	public void removeCardId(int id, Duel duel, Duelist rewardFor) {
		try {
			this.getZoneWithCardId(id).toGraveyard(duel, rewardFor);
		} catch (NoZoneOpenException e) {
		}
	}
	
	public MonsterZone pickMonsterZone() { //Picks highest atk monster zone (for effects)
		int highest_atk = -1;
		MonsterZone mz = null;
		MonsterCard mc = null;
		for(MonsterZone zone : this.monsterzones) {
			if(!zone.isOpen()) {
				mc = MonsterCard.class.cast(zone.card);
				if(mc.getAtk() > highest_atk) {
					highest_atk = mc.getAtk();
					mz = zone;
				}
			}
		}
		return mz;
	}
}
