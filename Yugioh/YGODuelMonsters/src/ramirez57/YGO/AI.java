package ramirez57.YGO;

import java.util.Collections;
import java.util.Stack;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.ClickType;

public class AI implements Runnable {

	private static final int DIRECT_ATTACK = 1;
	private static final int DEFEAT_CARD = 2;
	private static final int DEFEND = 3;
	private static final int BATTLE = 4;
	
	public Duelist playingFor;
	public Duel the_duel;
	
	public AI(Duelist duelist, Duel duel) {
		this.playingFor = duelist;
		this.the_duel = duel;
	}
	
	@Override
	public void run() {
		try {
			AI.playDuelFor(this.playingFor, this.the_duel);
		} catch (InterruptedException e) {
			PluginVars.plugin.getLogger().info("AI error! Please report to Bukkit Dev page.");
			return;
		}
	}
	
	public static void playDuelFor(Duelist duelist, Duel duel) throws InterruptedException {
		Stack<Card> canplay = new Stack<Card>();
		Stack<Stack<Card>> fusions = new Stack<Stack<Card>>();
		int c;
		int timer = 800;
		int selection = 0;
		int phase = 1;
		int goal = 0;
		boolean simpleton = true;
		Card card;
		MonsterCard mc;
		EquipCard ec;
		SpellCard sc;
		FieldCard fc;
		Stack<MonsterCard> oppmonsters = new Stack<MonsterCard>();
		Stack<MonsterCard> mymonsters = new Stack<MonsterCard>();
		if (simpleton) {
			goal = 0;
			canplay.clear();
			while(true) {
				//System.out.println("Yes, this is cat: " + goal);
				if(goal == 0) {
					for (c = 0; c < duelist.hand.size(); c++) {
						if (FieldCard.class.isInstance(duelist.hand.cards.get(c))) {
							fc = FieldCard.class.cast(duelist.hand.cards.get(c));
							if (fc.terrain != duel.terrain) {
								AI.doInput(timer, duel, duelist, 45 + c, ClickType.LEFT);
								Thread.sleep(timer);
								AI.doInput(timer, duel, duelist, 26, ClickType.LEFT);
								Thread.sleep(timer);
								goal = AI.BATTLE;
							}
						}
					}
					if(!duelist.field.emptyMonsterZones()) {
						if(PluginVars.random.nextBoolean()) {
							for(c = 0; c < duelist.hand.size(); c++) {
								if(SpellCard.class.isInstance(duelist.hand.cards.get(c))) {
									sc = SpellCard.class.cast(duelist.hand.cards.get(c));
									if(sc.shouldActivate(duel, duelist)) {
										AI.doInput(timer, duel, duelist, 45+c, ClickType.LEFT);
										Thread.sleep(timer);
										AI.doInput(timer, duel, duelist, 26, ClickType.LEFT);
										Thread.sleep(timer);
										if(RitualCard.class.isInstance(sc)) {
											AI.doInput(timer, duel, duelist, 26, ClickType.LEFT);
											Thread.sleep(timer);
										}
										goal = AI.BATTLE;
										c=10;
									} else {
										AI.doInput(timer, duel, duelist, 45+c, ClickType.LEFT);
										Thread.sleep(timer);
										try {
											AI.doInput(timer, duel, duelist, 36+duelist.field.getPos(duelist.field.getFirstOpenMagicZone()), ClickType.LEFT);
										} catch (NoZoneOpenException e) {
											AI.doInput(timer, duel, duelist, 36+PluginVars.random.nextInt(5), ClickType.LEFT);
										}
										Thread.sleep(timer);
										goal = AI.BATTLE;
										c=10;
									}
								}
							}
						}
					}
					if(goal == AI.BATTLE) continue;
					if(duelist.opponent.field.emptyMonsterZones()) {
						goal = AI.DIRECT_ATTACK;
					} else {
						goal = AI.DEFEAT_CARD;
					}
				} else if(goal == AI.DIRECT_ATTACK) {
					canplay.clear();
					fusions = AI.howToDefeat(1, null, duel, duelist);
					if(fusions.empty()) {
						AI.playFrom(canplay, duel, duelist, timer, false, null);
					} else {
						canplay = fusions.get(PluginVars.random.nextInt(fusions.size()));
						if(canplay.size() <= 1) {
							AI.playFrom(canplay, duel, duelist, timer, false, null);
						} else {
							AI.doFusion(canplay, duel, duelist, null, timer);
						}
					}
					
					goal = AI.BATTLE;
				} else if(goal == AI.DEFEAT_CARD) {
					canplay.clear();
					oppmonsters = AI.getCardList(duelist.opponent);
					mc = oppmonsters.pop();
					if(mc.position == MonsterPosition.ATTACK) {
						fusions = AI.howToDefeat(mc.getAtk(), mc.star, duel, duelist);
					} else {
						fusions = AI.howToDefeat(mc.getDef(), mc.star, duel, duelist);
					}
					
					if(fusions.empty()) {
						if(PluginVars.hard_mode) {
							// AI IS A CHEATER LOL
							oppmonsters = AI.getCardList(duelist.opponent);
							mc = oppmonsters.pop();
							for(int p = 0; p < duelist.deck.cards.size(); p++) {
								card = duelist.deck.cards.get(p);
								if(MonsterCard.class.isInstance(card)) {
									MonsterCard mymc = MonsterCard.class.cast(card);
									//swaps for a better card
									if(mc.position == MonsterPosition.DEFENSE) {
										if(mymc.atk > mc.def + mc.bonus) {
											Card card2 = duelist.deck.cards.get(p);
											duelist.deck.cards.set(p, duelist.hand.cards.get(0));
											duelist.hand.cards.set(0, card2);
											canplay.push(card2);
											AI.playFrom(canplay, duel, duelist, timer, false, null);
											goal = AI.BATTLE;
											break;
										}
									} else {
										if(mymc.atk > mc.atk + mc.bonus) {
											Card card2 = duelist.deck.cards.get(p);
											duelist.deck.cards.set(p, duelist.hand.cards.get(0));
											duelist.hand.cards.set(0, card2);
											canplay.push(card2);
											AI.playFrom(canplay, duel, duelist, timer, false, null);
											goal = AI.BATTLE;
											break;
										}
									}
								}
							}
							if(goal == AI.BATTLE) continue; 
						}
						goal = AI.DEFEND;
						continue;
					} else {
						canplay = fusions.get(PluginVars.random.nextInt(fusions.size()));
						if(canplay.size() <= 1) {
							AI.playFrom(canplay, duel, duelist, timer, false, null);
						} else {
							AI.doFusion(canplay, duel, duelist, null, timer);
						}
					}
					goal = AI.BATTLE;
				} else if(goal == AI.DEFEND) {
					canplay.clear();
					for(c = 0; c < duelist.hand.size(); c++) {
						if(MonsterCard.class.isInstance(duelist.hand.cards.elementAt(c))) {
							canplay.push(duelist.hand.cards.elementAt(c));
						}
					}
					AI.playFrom(canplay, duel, duelist, timer, true, null);
					goal = AI.BATTLE;
				} else if(goal == AI.BATTLE) {
					for(c = 0; c < duelist.field.magiczones.length; c++) {
						if(!duelist.field.magiczones[c].isOpen()) {
							if(SpellCard.class.isInstance(duelist.field.magiczones[c].card)) {
								sc = SpellCard.class.cast(duelist.field.magiczones[c].card);
								if(sc.shouldActivate(duel, duelist)) {
									AI.doInput(timer, duel, duelist, 36+c, ClickType.LEFT);
									Thread.sleep(timer);
								}
								if(RitualCard.class.isInstance(sc)) {
									AI.doInput(timer, duel, duelist, 26, ClickType.LEFT);
									Thread.sleep(timer);
								}
							} else if(EquipCard.class.isInstance(duelist.field.magiczones[c].card)) {
								ec = EquipCard.class.cast(duelist.field.magiczones[c].card);
								mymonsters = AI.getCardList(duelist);
								while(!mymonsters.empty()) {
									mc = mymonsters.pop();
									if(mc.canEquip(ec)) {
										AI.doInput(timer, duel, duelist, 36+c, ClickType.LEFT);
										Thread.sleep(timer);
										try {
											AI.doInput(timer, duel, duelist, 27+duelist.field.getPos(duelist.field.getZoneWithCard(mc)), ClickType.LEFT);
										} catch (NoZoneOpenException e) {
											//TODO: Cancel this event
											System.out.println("WHAT?");
										}
										Thread.sleep(timer);
									}
								}
							}
						}
					}
					if(PluginVars.monster_effects) { 
						for(c = 0; c < duelist.field.monsterzones.length; c++) {
							if(!duelist.field.monsterzones[c].isOpen()) {
								mc = MonsterCard.class.cast(duelist.field.monsterzones[c].card);
								if(!mc.attacked && mc.hasEffect() && !mc.usedEffect) {
									if(mc.shouldActivate(duel, duelist)) {
										AI.doInput(timer, duel, duelist, 27+c, ClickType.LEFT);
										Thread.sleep(timer);
										AI.doInput(timer, duel, duelist, 26, ClickType.LEFT);
										Thread.sleep(timer);
									}
								}
							}
						}
					}
					oppmonsters = AI.getCardList(duelist.opponent);
					while(duelist.swords == 0 && !AI.cardsAttacked(duelist)) {
						//System.out.println("Acknowledged");
						if(oppmonsters.empty()) {
							if(duelist.opponent.field.emptyMonsterZones()) {
								mymonsters = AI.getCardList(duelist);
								while(!mymonsters.empty()) {
									mc = mymonsters.pop();
									if(!mc.attacked && duelist.swords == 0 && mc.atk > 0) {
										try {
											if(mc.position == MonsterPosition.DEFENSE) {
												AI.doInput(timer, duel, duelist, 27+duelist.field.getPos(duelist.field.getZoneWithCard(mc)), ClickType.RIGHT);
												Thread.sleep(timer);
											}
											AI.doInput(timer, duel, duelist, 27+duelist.field.getPos(duelist.field.getZoneWithCard(mc)), ClickType.LEFT);
											Thread.sleep(timer);
											AI.doInput(timer, duel, duelist, 20, ClickType.LEFT);
											Thread.sleep(timer);
										} catch (NoZoneOpenException e) {
											System.out.println("WHAT?");
										}
									}
								}
								AI.doInput(timer, duel, duelist, 42, ClickType.LEFT);
								Thread.sleep(timer);
								break;
							} else {
								mymonsters = AI.getCardList(duelist);
								while(!mymonsters.empty()) {
									mc = mymonsters.pop();
									if(!mc.attacked && mc.atk < 3000 && mc.position != MonsterPosition.DEFENSE) {
										try {
											AI.doInput(timer, duel, duelist, 27+duelist.field.getPos(duelist.field.getZoneWithCard(mc)), ClickType.RIGHT);
										} catch (NoZoneOpenException e) {
											System.out.println("WHAT?");
										}
										Thread.sleep(timer);
									}
								}
								AI.doInput(timer, duel, duelist, 42, ClickType.LEFT);
								Thread.sleep(timer);
								break;
							}
						} else {
							MonsterCard toDefeat = MonsterCard.class.cast(oppmonsters.pop());
							//System.out.println("Wanna defeat " + toDefeat.name);
							mymonsters = AI.getCardList(duelist);
							for(c = 0; c < mymonsters.size(); c++) {
								if(mymonsters.get(c).attacked)
									continue;
								else if(!toDefeat.faceup) {
									if(duelist.swords == 0 && PluginVars.random.nextBoolean()) {
										try {
											if(mymonsters.get(c).position == MonsterPosition.DEFENSE) {
												AI.doInput(timer, duel, duelist, 27+duelist.field.getPos(duelist.field.getZoneWithCard(mymonsters.get(c))), ClickType.RIGHT);
												Thread.sleep(timer);
											}
											AI.doInput(timer, duel, duelist, 27+duelist.field.getPos(duelist.field.getZoneWithCard(mymonsters.get(c))), ClickType.LEFT);
											Thread.sleep(timer);
											AI.doInput(timer, duel, duelist, 22-duelist.opponent.field.getPos(duelist.opponent.field.getZoneWithCard(toDefeat)), ClickType.LEFT);
											Thread.sleep(timer);
										} catch (NoZoneOpenException e) {
											System.out.println("WHAT?");
										}
										break;
									}
								} else if(duelist.swords == 0 && mymonsters.get(c).atk > 0 && AI.canDefeat(mymonsters.get(c), toDefeat)) {
									try {
										if(mymonsters.get(c).position != MonsterPosition.ATTACK) {
											AI.doInput(timer, duel, duelist, 27+duelist.field.getPos(duelist.field.getZoneWithCard(mymonsters.get(c))), ClickType.RIGHT);
											Thread.sleep(timer);
										}
										AI.doInput(timer, duel, duelist, 27+duelist.field.getPos(duelist.field.getZoneWithCard(mymonsters.get(c))), ClickType.LEFT);
										Thread.sleep(timer);
										AI.doInput(timer, duel, duelist, 22-duelist.opponent.field.getPos(duelist.opponent.field.getZoneWithCard(toDefeat)), ClickType.LEFT);
										Thread.sleep(timer);
									} catch (NoZoneOpenException e) {
										System.out.println("WHAT?");
									}
									break;
								}
							}
						}
					}
					break;
				}
			}
			AI.doInput(timer, duel, duelist, 42, ClickType.LEFT);
			Thread.sleep(timer);
			return;
		}
	}
	
	public static boolean cardsAttacked(Duelist duelist) {
		int c;
		MonsterCard mc;
		for(c = 0; c < duelist.field.monsterzones.length; c++) {
			if(!duelist.field.monsterzones[c].isOpen()) {
				if(MonsterCard.class.isInstance(duelist.field.monsterzones[c].card)) {
					mc = MonsterCard.class.cast(duelist.field.monsterzones[c].card);
					if(!mc.attacked)
						return false;
				}
			}
		}
		return true;
	}
	
	public static Stack<MonsterCard> getCardList(Duelist duelist) {
		int c;
		Stack<MonsterCard> strongest = new Stack<MonsterCard>();
		MonsterCard mc = null;
		for(c = 0; c < duelist.field.monsterzones.length; c++) {
			if(duelist.field.monsterzones[c].isOpen()) {
				continue;
			} else {
				mc = MonsterCard.class.cast(duelist.field.monsterzones[c].card);
				strongest.push(mc);
			}
		}
		
		Collections.sort(strongest);
		return strongest;
	}
	
	public static Card battleWith(Card card, Duelist duelist) {
		int c;
		MonsterCard mc;
		MonsterCard mc2 = null;
		if(MonsterCard.class.isInstance(card)) {
			mc = MonsterCard.class.cast(card);
			for(c=0; c < duelist.opponent.field.monsterzones.length; c++) {
				if(duelist.opponent.field.monsterzones[c].isOpen()) {
					continue;
				} else {
					mc2 = MonsterCard.class.cast(duelist.opponent.field.monsterzones[c].card);
					if(mc2.faceup) {
						if(mc2.position == MonsterPosition.ATTACK) {
							if(mc.atk > mc2.atk)
								return mc2;
							else
								return null;
						}
					} else return mc2;
				}
			}
		}
		return null;
	}
	
	public static boolean canDefeat(MonsterCard attacker, MonsterCard target) {
		int ap = attacker.getAtk();
		int tp = 0;
		if(target.position == MonsterPosition.ATTACK)
			tp = target.getAtk();
		else
			tp = target.getDef();
		if(attacker.star.isSuperiorTo(target.star))
			ap+=500;
		else if(target.star.isSuperiorTo(attacker.star))
			tp+=500;
		return ap > tp;
	}
	
	@SuppressWarnings("unchecked")
	public static Stack<Stack<Card>> howToDefeat(int number, GuardianStar gs, Duel duel, Duelist duelist) {
		Stack<Stack<Card>> mycards = new Stack<Stack<Card>>();
		MonsterCard mymc;
		Stack<Card> workspace = new Stack<Card>();
		int c;
		int d;
		/*for(c = 0; c < duelist.hand.size(); c++) {
			workspace = new Stack<Card>();
			if(MonsterCard.class.isInstance(duelist.hand.cards.elementAt(c))) {
				mymc = MonsterCard.class.cast(duelist.hand.cards.elementAt(c));
				int pow = mymc.getAtk();
				if(mymc.stars[0].isSuperiorTo(mc.star))
					pow += 500;
				else if(mc.star.isSuperiorTo(mymc.stars[0]))
					pow -= 500;
				if(pow >= number) {
					workspace.push(mymc);
					mycards.push(workspace);
				} else {
					//search for equips
					workspace.push(mymc);
					for(d = 0; d < duelist.hand.size(); d++) {
						if(EquipCard.class.isInstance(duelist.hand.cards.elementAt(d))) {
							EquipCard ec = EquipCard.class.cast(duelist.hand.cards.elementAt(d));
							if(mymc.canEquip(ec)) {
								if(pow + ec.incrementBy >= number) {
									workspace.push(ec);
									mycards.push(workspace);
								}
							}
						}
					}
				}
			}
		}*/
		Stack<Card> hand = (Stack<Card>) duelist.hand.cards.clone();
		Stack<Card> fusion = null;
		Card card1 = null;
		Card card2 = null;
		Card result = null;
		for(c = 0; c < 5; c++) {
			fusion = new Stack<Card>();
			hand = (Stack<Card>) duelist.hand.cards.clone();
			card1 = hand.remove(c);
			fusion.push(card1);
			result = card1;
			while(result != null) {
				for(d = 0; d < hand.size(); d++) {
					card2 = hand.get(d);
					result = Fusion.createFusion(duel, duelist, card1, card2).initiate(false);
					if(result != card1 && result != card2) {
						fusion.push(card2);
						hand.remove(d);
						card1 = result;
						break;
					}
					result = null;
				}
			}
			result = card1;
			if(MonsterCard.class.isInstance(result)) {
				mymc = MonsterCard.class.cast(result);
				int pow = mymc.getAtk();
				if(gs != null) {
					if(mymc.stars[0].isSuperiorTo(gs)) {
						pow += 500;
					} else if(gs.isSuperiorTo(mymc.star)) {
						pow -= 500;
					}
				}
				if(pow >= number) {
					mycards.push(fusion);
				} else {
					for(int e = 0; e < hand.size(); e++) {
						if(EquipCard.class.isInstance(hand.get(e))) {
							EquipCard ec = EquipCard.class.cast(hand.get(e));
							if(mymc.canEquip(ec)) {
								if(pow + ec.incrementBy >= number) {
									fusion.push(ec);
									mycards.push(fusion);
								}
							}
						}
					}
				}
			} else if(EquipCard.class.isInstance(result)) {
				EquipCard ec = EquipCard.class.cast(result);
				for(int e = 0; e < hand.size(); e++) {
					if(MonsterCard.class.isInstance(hand.get(e))) {
						mymc = MonsterCard.class.cast(hand.get(e));
						int pow = mymc.getAtk();
						if(mymc.canEquip(ec)) {
							if(pow + ec.incrementBy >= number) {
								fusion.push(mymc);
								mycards.push(fusion);
							}
						}
					}
				}
			} else if(SpellCard.class.isInstance(result)) {
				SpellCard sc = SpellCard.class.cast(result);
				if((!duelist.field.emptyMonsterZones()) && sc.shouldActivate(duel, duelist)) {
					mycards.push(fusion);
				}
			}
		}
		return mycards;
	}

	public static boolean considerOffense(Card card, Duelist duelist) {
		int c;
		if (MonsterCard.class.isInstance(card)) {
			MonsterCard mc = MonsterCard.class.cast(card);
			MonsterCard mc2 = null;
			for (c = 0; c < duelist.opponent.field.monsterzones.length; c++) {
				if (duelist.opponent.field.monsterzones[c].isOpen()) {
					continue;
				} else {
					mc2 = MonsterCard.class.cast(duelist.opponent.field.monsterzones[c].card);
					if (mc2.position == MonsterPosition.ATTACK) {
						if (mc.atk > mc2.atk)
							return true;
					} else {
						if (mc.atk > mc2.def)
							return true;
					}
				}
			}
		}
		return false;
	}
	
	public static int doFusion(Stack<Card> tofuse, Duel duel, Duelist duelist, Card fuseWith, int timer) throws InterruptedException {
		int c;
		for(c = 0; c < tofuse.size(); c++) {
			AI.doInput(timer, duel, duelist, 45+duelist.hand.getCardPos(tofuse.elementAt(c)), ClickType.RIGHT);
			Thread.sleep(timer);
		}
		if(fuseWith == null) {
			try {
				AI.doInput(timer, duel, duelist, 27+duelist.field.getPos(duelist.field.getFirstOpenMonsterZone()), ClickType.LEFT);
			} catch (NoZoneOpenException e) {
				AI.doInput(timer, duel, duelist, 27+PluginVars.random.nextInt(5), ClickType.LEFT);
			}
			Thread.sleep(timer);
			AI.doInput(timer, duel, duelist, 26, ClickType.LEFT);
			Thread.sleep(timer);
		} else {
			try {
				AI.doInput(timer, duel, duelist, 27+duelist.field.getPos(duelist.field.getZoneWithCard(fuseWith)), ClickType.LEFT);
			} catch (NoZoneOpenException e) {
				AI.doInput(timer, duel, duelist, 27+PluginVars.random.nextInt(5), ClickType.LEFT);
			}
			Thread.sleep(timer);
			AI.doInput(timer, duel, duelist, 26, ClickType.LEFT);
			Thread.sleep(timer);
		}
		return timer;
	}

	public static int playFrom(Stack<Card> canplay, Duel duel, Duelist duelist,
			int timer, boolean activate, Card fuseWith) throws InterruptedException {
		int selection = 0;
		if (canplay.empty()) {
			selection = PluginVars.random.nextInt(duelist.hand.size());
		} else {
			selection = duelist.hand.getCardPos(canplay.get(PluginVars.random
					.nextInt(canplay.size())));
			if(selection == -1) selection = 0;
		}
		Card card = duelist.hand.cards.elementAt(selection);
		AI.doInput(timer, duel, duelist, 45 + selection, ClickType.LEFT);
		Thread.sleep(timer);
		int c;
		if (MonsterCard.class.isInstance(card)) {
			try {
				AI.doInput(timer, duel, duelist, 27 + duelist.field
						.getPos(duelist.field.getFirstOpenMonsterZone()),
						ClickType.LEFT);
			} catch (NoZoneOpenException e) {
				AI.doInput(timer, duel, duelist, 27 + PluginVars.random.nextInt(duelist.field.monsterzones.length), ClickType.LEFT);
				Thread.sleep(timer);
				AI.doInput(timer, duel, duelist, 26, ClickType.LEFT);
			}
			Thread.sleep(timer);
		} else if (SpellCard.class.isInstance(card)) {
			if (!TrapCard.class.isInstance(card) && activate) {
				AI.doInput(timer, duel, duelist, 26, ClickType.LEFT);
				Thread.sleep(timer);
			} else {
				try {
					AI.doInput(timer, duel, duelist, 36 + duelist.field
							.getPos(duelist.field.getFirstOpenMagicZone()),
							ClickType.LEFT);
				} catch (NoZoneOpenException e) {
					AI.doInput(timer, duel, duelist, 36 + PluginVars.random.nextInt(duelist.field.magiczones.length), ClickType.LEFT);
					Thread.sleep(timer);
					AI.doInput(timer, duel, duelist, 26, ClickType.LEFT);
				}
				Thread.sleep(timer);
			}
		} else if (EquipCard.class.isInstance(card)) {
			try {
				if(fuseWith != null)
					AI.doInput(timer, duel, duelist, 27 + duelist.field
							.getPos(duelist.field.getZoneWithCard(fuseWith)),
							ClickType.LEFT);
				else
					AI.doInput(timer, duel, duelist, 36 + duelist.field.getPos(duelist.field.getFirstOpenMagicZone()), ClickType.LEFT);
			} catch (NoZoneOpenException e) {
				AI.doInput(timer, duel, duelist, 36 + PluginVars.random.nextInt(duelist.field.magiczones.length), ClickType.LEFT);
				Thread.sleep(timer);
				AI.doInput(timer, duel, duelist, 26, ClickType.LEFT);
			}
			Thread.sleep(timer);
		}
		return timer;
	}

	public static void doInput(int delay, Duel duel, Duelist duelist, int slot,
			ClickType action) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(PluginVars.plugin,
				new AIAction(duel, duelist, slot, action), delay / 50);
	}
}
