package ramirez57.YGO;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;

public class Fusion {

	public Duel duel;
	public Duelist duelist;
	public Card card1;
	public Card card2;
	public Card result;
	public static File fusionsFile;
	public static HashMap<TraitPair, List<Card>> typefusions = new HashMap<TraitPair, List<Card>>();
	public static HashMap<CardTraitPair, List<Card>> semitypefusions = new HashMap<CardTraitPair, List<Card>>();
	public static HashMap<CardPair, Card> specificfusions = new HashMap<CardPair, Card>();
	
	public Fusion() {
		
	}
	
	public Card initiate(boolean destroy) {
		MonsterCard mc1;
		MonsterCard mc2;
		EquipCard ec1;
		EquipCard ec2;
		List<Card> results;
		
		this.result = card2;
		int max = 0;
		if(MonsterCard.class.isInstance(this.card1) && MonsterCard.class.isInstance(this.card2)) {
			mc1 = MonsterCard.class.cast(this.card1);
			mc2 = MonsterCard.class.cast(this.card2);
			if(mc1.atk > mc2.atk)
				max = mc1.atk;
			else
				max = mc2.atk;
		} else if(MonsterCard.class.isInstance(this.card1))
			max = MonsterCard.class.cast(this.card1).atk;
		else if(MonsterCard.class.isInstance(this.card2))
			max = MonsterCard.class.cast(this.card2).atk;
		
		Iterator<CardPair> citerator = specificfusions.keySet().iterator();
		while(citerator.hasNext()) {
			CardPair cp = citerator.next();
			if(cp.settles(this.card1, this.card2)) {
				this.result = specificfusions.get(cp).freshCopy();
				this.discardBoth(destroy);
				duel.applyTerrain(duel.terrain, this.result, false);
				if(destroy) {
					duelist.initiate_fusion++;
				}
				return this.result;
			}
		}
		
		Iterator<CardTraitPair> ctpiterator = semitypefusions.keySet().iterator();
		while(ctpiterator.hasNext()) {
			CardTraitPair ctp = ctpiterator.next();
			if(ctp.settles(this.card1, this.card2)) {
				results = semitypefusions.get(ctp);
				for(int c = 0; c < results.size(); c++) {
					if(MonsterCard.class.isInstance(results.get(c))) {
						MonsterCard mc = MonsterCard.class.cast(results.get(c));
						if(max < mc.atk) {
							this.discardBoth(destroy);
							this.result = mc.freshCopy();
							duel.applyTerrain(duel.terrain, this.result, false);
							if(destroy) {
								duelist.initiate_fusion++;
							}
							return this.result;
						}
					} else {
						this.discardBoth(destroy);
						this.result = results.get(c).freshCopy();
						duel.applyTerrain(duel.terrain, this.result, false);
						if(destroy) {
							duelist.initiate_fusion++;
						}
						return this.result;
					}
				}
			}
		}
		
		if(MonsterCard.class.isInstance(this.card1) && MonsterCard.class.isInstance(this.card2)) {
			mc1 = MonsterCard.class.cast(this.card1);
			mc2 = MonsterCard.class.cast(this.card2);
			if((mc1.atk > mc2.atk)) {
				max = mc1.atk;
			} else {
				max = mc2.atk;
			}
			Iterator<TraitPair> iterator = typefusions.keySet().iterator();
			while(iterator.hasNext()) {
				TraitPair tp = iterator.next();
				boolean strict = true;
				for(int i = 0; i < 2; i++) {
					if(tp.hasTraits(mc1, mc2, strict)) {
						results = typefusions.get(tp);
						for(int c = 0; c < results.size(); c++) {
							if(MonsterCard.class.isInstance(results.get(c))) {
								MonsterCard mc = MonsterCard.class.cast(results.get(c));
								if(max < mc.atk) {
									this.discardBoth(destroy);
									this.result = mc.freshCopy();
									duel.applyTerrain(duel.terrain, this.result, false);
									if(destroy) {
										duelist.initiate_fusion++;
									}
									return this.result;
								}
							} else {
								this.discardBoth(destroy);
								this.result = results.get(c).freshCopy();
								duel.applyTerrain(duel.terrain, this.result, false);
								if(destroy) {
									duelist.initiate_fusion++;
								}
								return this.result;
							}
						}
					}
					strict = false;
				}
			}
		}
		
		if(MonsterCard.class.isInstance(this.card1)) {
			mc1 = MonsterCard.class.cast(this.card1);
			if(EquipCard.class.isInstance(this.card2)) {
				ec2 = EquipCard.class.cast(this.card2);
				if(mc1.canEquip(ec2)) {
					mc1 = (MonsterCard) mc1.copy();
					duel.increasePower(this.duelist, mc1, ec2.incrementBy);
					if(destroy) {
						this.duelist.equip_magic++;
					}
				}
				if(destroy)
					this.discard(ec2);
				this.result = mc1;
				return this.result;
			} else if(SpellCard.class.isInstance(this.card2)) {
				if(destroy)
					this.discard(this.card2);
				this.result = mc1;
				return this.result;
			}
		} else if(EquipCard.class.isInstance(this.card1)) {
			ec1 = EquipCard.class.cast(this.card1);
			if(MonsterCard.class.isInstance(this.card2)) {
				mc2 = MonsterCard.class.cast(this.card2);
				if(mc2.canEquip(ec1)) {
					mc2 = (MonsterCard) mc2.copy();
					duel.increasePower(this.duelist, mc2, ec1.incrementBy);
					if(destroy) {
						this.duelist.equip_magic++;
					}
				}
				if(destroy)
					this.discard(ec1);
				this.result = mc2;
				return this.result;
			}
		}
		
		if(destroy)
			this.discard(card1);
		return this.result;
	}
	
	public void discard(Card card) {
		this.duel.graveyard.push(card);
	}
	
	public void discardBoth(boolean really) {
		if(really) {
			this.duel.graveyard.push(this.card1);
			this.duel.graveyard.push(this.card2);
		}
	}
	
	public static Fusion createFusion(Duel duel, Duelist duelist, Card material1, Card material2) {
		Fusion fusion = new Fusion();
		fusion.duel = duel;
		fusion.duelist = duelist;
		fusion.card1 = material1;
		fusion.card2 = material2;
		fusion.result = null;
		return fusion;
	}
	
	public static void loadFusions(File fin) {
		YamlConfiguration config = YamlConfiguration.loadConfiguration(fin);
		Set<String> keys = config.getConfigurationSection("type").getKeys(false);
		Set<String> keys2 = null;
		Iterator<String> iterator = keys.iterator();
		Iterator<String> iterator2 = null;
		String s;
		String s2;
		TraitPair pair = null;
		List<Card> results = null;
		List<Integer> intresults = null;
		CardTraitPair ctpair = null;
		CardPair cpair = null;
		while(iterator.hasNext()) {
			s = iterator.next();
			keys2 = config.getConfigurationSection("type." + s).getKeys(false);
			iterator2 = keys2.iterator();
			while(iterator2.hasNext()) {
				s2 = iterator2.next();
				pair = new TraitPair(Trait.fromString(s), Trait.fromString(s2));
				intresults = config.getIntegerList("type." + s + "." + s2);
				results = new ArrayList<Card>();
				for(int i = 0; i < intresults.size(); i++) {
					results.add(Card.fromId(intresults.get(i)));
				}
				Fusion.typefusions.put(pair, results);
				//System.out.println(pair.t1 + " + " + pair.t2 + " = " + results.get(0).name);
			}
		}
		
		keys = config.getConfigurationSection("semitype").getKeys(false);
		iterator = keys.iterator();
		while(iterator.hasNext()) {
			s = iterator.next();
			keys2 = config.getConfigurationSection("semitype." + s).getKeys(false);
			iterator2 = keys2.iterator();
			while(iterator2.hasNext()) {
				s2 = iterator2.next();
				ctpair = new CardTraitPair(Integer.parseInt(s), Trait.fromString(s2));
				intresults = config.getIntegerList("semitype." + s + "." + s2);
				results = new ArrayList<Card>();
				for(int i = 0; i < intresults.size(); i++) {
					results.add(Card.fromId(intresults.get(i)));
				}
				Fusion.semitypefusions.put(ctpair, results);
				//System.out.println(Card.fromId(ctpair.id).name + " + " + ctpair.trait + " = " + results.get(0).name);
			}
		}
		
		keys = config.getConfigurationSection("specific").getKeys(false);
		iterator = keys.iterator();
		while(iterator.hasNext()) {
			s = iterator.next();
			keys2 = config.getConfigurationSection("specific." + s).getKeys(false);
			iterator2 = keys2.iterator();
			while(iterator2.hasNext()) {
				s2 = iterator2.next();
				cpair = new CardPair(Integer.parseInt(s), Integer.parseInt(s2));
				Fusion.specificfusions.put(cpair, Card.fromId(config.getInt("specific." + s + "." + s2)).freshCopy());
				//System.out.println(Card.fromId(cpair.id1).name + " + " + Card.fromId(cpair.id2).name + " = " + Fusion.specificfusions.get(cpair).name);
			}
		}
		
		int nType = Fusion.typefusions.size();
		int nSemiType = Fusion.semitypefusions.size();
		int nSpecific = Fusion.specificfusions.size();
		
		PluginVars.plugin.getLogger().info((nType + nSemiType + nSpecific) + " fusions: " + nType + " type fusions, " + nSemiType + " semi-type fusions, " + nSpecific + " specific fusions.");
	}
}
