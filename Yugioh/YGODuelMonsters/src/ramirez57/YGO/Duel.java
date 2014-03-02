package ramirez57.YGO;

import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;

public class Duel {
	public Duelist[] duelists;
	public Stack<Card> graveyard;
	public int turn;
	public boolean firstturn;
	public String broadcast;
	public Terrain terrain;
	public Tournament tournament;
	public boolean ended;

	public static Duel createDuel(Player p1, Inventory i1, UUID uuid1, Player p2,
			Inventory i2, UUID uuid2) {
		Duel duel = new Duel();
		duel.duelists = new Duelist[2];
		try {
			duel.duelists[0] = Duelist.fromPlayer(p1, i1, uuid1);
			duel.duelists[1] = Duelist.fromPlayer(p2, i2, uuid2);
		} catch (NoDeckException e) {
			duel.endDuel(null, WinReason.SURRENDER);
		}
		duel.duelists[0].opponent = duel.duelists[1];
		duel.duelists[1].opponent = duel.duelists[0];
		duel.firstturn = true;
		duel.graveyard = new Stack<Card>();
		duel.broadcast = "Game Start";
		duel.terrain = Terrain.NORMAL;
		duel.ended = false;
		return duel;
	}
	
	public int getDuelistId(Duelist duelist) throws NotDuelingException {
		int i;
		for(i = 0; i < 2; i++) {
			if(this.duelists[i] == duelist)
				return i;
		}
		throw new NotDuelingException();
	}
	
	public static Duel createEmptyDuel() {
		Duel duel = new Duel();
		duel.duelists = new Duelist[2];
		duel.terrain = Terrain.NORMAL;
		duel.broadcast = "Game Start";
		duel.graveyard = new Stack<Card>();
		duel.firstturn = true;
		return duel;
	}
	
	public void setDuelist(int num, Entity entity, Inventory i, UUID uuid) {
		try {
			if(entity.getType() == EntityType.PLAYER) {
				this.duelists[num] = Duelist.fromPlayer(Player.class.cast(entity), i, null);
			} else {
				this.duelists[num] = Duelist.fromPlayer(null, i, uuid);
			}
		} catch(NoDeckException e) {
			this.endDuel(null, WinReason.SURRENDER);
		}
	}
	
	public Zone summonMonster(Field field, int id) {
		MonsterZone mz = null;
		try {
			mz = field.getFirstOpenMonsterZone();
		} catch (NoZoneOpenException e) {
			return null;
		}
		MonsterCard mc = MonsterCard.class.cast(Card.fromId(id).copy());
		mc.faceup = false;
		mc.bonus = 0;
		mc.attacked = false;
		mc.usedEffect = false;
		mc.position = MonsterPosition.ATTACK;
		mc.star = mc.stars[0];
		this.applyTerrain(this.terrain, mc, false);
		mz.put(mc);
		return mz;
	}
	
	public MonsterCard popMonsterFromGraveyard() {
		int i = this.graveyard.size();
		while(i != 0) {
			i--;
			if(MonsterCard.class.isInstance(this.graveyard.get(i))) {
				MonsterCard mc = MonsterCard.class.cast(this.graveyard.remove(i));
				return mc;
			}
		}
		return null;
	}

	public void changeTerrain(Terrain terrain) {
		Terrain oldTerrain = this.terrain;
		for (int i = 0; i < 2; i++) {
			for (Card card : this.duelists[i].deck.cards) {
				this.applyTerrain(oldTerrain, card, true);
			}
			for (Card card : this.duelists[i].hand.cards) {
				this.applyTerrain(oldTerrain, card, true);
			}
			for (MonsterZone mz : this.duelists[i].field.monsterzones) {
				this.applyTerrain(oldTerrain, mz.card, true);
			}
		}
		this.terrain = terrain;
		for (int i = 0; i < 2; i++) {
			for (Card card : this.duelists[i].deck.cards) {
				this.applyTerrain(terrain, card, false);
			}
			for (Card card : this.duelists[i].hand.cards) {
				this.applyTerrain(terrain, card, false);
			}
			for (MonsterZone mz : this.duelists[i].field.monsterzones) {
				this.applyTerrain(terrain, mz.card, false);
			}
		}
	}

	public void applyTerrain(Terrain terrain, Card card, boolean inverse) {
		MonsterCard mc = null;
		if (MonsterCard.class.isInstance(card)) {
			mc = MonsterCard.class.cast(card);
			if (terrain.favors.contains(mc.type)) {
				if (inverse)
					mc.bonus -= 500;
				else
					mc.bonus += 500;
			} else if (terrain.unfavors.contains(mc.type)) {
				if (inverse)
					mc.bonus += 500;
				else
					mc.bonus -= 500;
			}
		}
	}

	public void startDuel() {
		int i;
		this.ended = false;
		this.duelists[0].opponent = this.duelists[1];
		this.duelists[1].opponent = this.duelists[0];
		for (i = 0; i < 2; i++) {
			this.duelists[i].lp = 8000;
			//this.duelists[i].avg_lv = this.duelists[i].deck.getAvgLv();
			this.duelists[i].deck.shuffle();
			this.duelists[i].swords = 0;
		}
		this.playerTurn(PluginVars.random.nextInt(2));
	}

	public void endDuel(Duelist winner, WinReason reason) {
		MonsterCard mc;
		Card c;
		Iterator<Card> iterator;
		PluginVars.duel_list.remove(this);
		if(this.ended)
			return;
		this.ended = true;
		if (winner != null) {
			if (winner.player != null) {
				winner.player.closeInventory();
				PluginVars.closeSpectators(winner.player, true, reason);
				winner.player.sendMessage("YOU WIN" + " by " + ChatColor.GOLD + reason.name + ChatColor.WHITE + " (" + winner.turns + " turns)");
				if (winner.opponent.player == null) {
					boolean pow = true;
					int sc = 1;
					if (reason == WinReason.ATTRITION) {
						pow = false;
						sc = 5;
						winner.rewards = this.graveyard;
					} else {
						if (winner.deck.cards.size() < 13) {
							pow = false;
						}
						if (pow) {
							sc = 5;
							if (winner.deck.cards.size() <= 27)
								sc--;
							if (winner.trigger_trap > 0)
								sc--;
							if (winner.pure_magic > 0)
								sc--;
							if (winner.equip_magic >= 3)
								sc--;
							if (winner.card_destruction > 0 || winner.lp < 8000)
								sc--;
							if (winner.deck.cards.size() <= 24)
								sc--;
						} else {
							sc = -1;
							if (winner.lp < 1000)
								sc++;
							if (winner.trigger_trap > 0)
								sc++;
							if (winner.pure_magic > 0)
								sc++;
							if (winner.equip_magic > 0)
								sc++;
							if (winner.initiate_fusion >= 10)
								sc++;
							if (winner.pure_magic >= 10)
								sc++;
							if (winner.equip_magic >= 10)
								sc++;
							if(winner.trigger_trap >= 7)
								sc++;
						}
						if (sc <= 0)
							sc = 1;
						else if (sc >= 6)
							sc = 5;
						if (sc >= 4) {
							if(pow) {
								iterator = winner.rewards.iterator();
								while(iterator.hasNext()) {
									c = iterator.next();
									if(TrapCard.class.isInstance(c)) {
										iterator.remove();
									}
								}
							} else {
								winner.rewards = this.graveyard;
							}
						} else {
							if(pow) {
								iterator = this.graveyard.iterator();
								while (iterator.hasNext()) {
									c = iterator.next();
									if (FieldCard.class.isInstance(c)) {
										winner.rewards.add(c);
									}
								}
							} else {
								iterator = winner.rewards.iterator();
								while(iterator.hasNext()) {
									c = iterator.next();
									if(TrapCard.class.isInstance(c)) {
										iterator.remove();
									}
								}
								iterator = this.graveyard.iterator();
								while (iterator.hasNext()) {
									c = iterator.next();
									if (EquipCard.class.isInstance(c)) {
										winner.rewards.add(c);
									}
								}
							}
						}
					}
					iterator = winner.rewards.iterator();
					while (iterator.hasNext()) {
						if (!iterator.next().obtainable) {
							iterator.remove();
						}
					}
					winner.rewards.push(Card.fromId(690).copy());
					Card reward = winner.rewards.get(
							PluginVars.random.nextInt(winner.rewards.size()))
							.freshCopy();
					PluginVars.giveStarchips(winner.player, sc);
					Main.giveReward(winner.player, reward.id);
					winner.player.sendMessage(
							"Cards Used: " + (40 - winner.deck.cards.size()) + "\n" +
							"Card Destruction: " + winner.opponent.card_destruction + "\n" +
							"Combo Plays: " + winner.combo_plays + "\n" +
							"Initiate Fusion: " + winner.initiate_fusion + "\n" +
							"Equip Magic: " + winner.equip_magic + "\n" +
							"Change Field: " + winner.change_field + "\n" + 
							"Pure Magic: " + winner.pure_magic + "\n" + 
							"Trigger Trap: " + winner.trigger_trap + "\n" +
							"RANK: " + Rank.getColored(sc, pow, ChatColor.WHITE) + "\n" +
							"Obtained: " + reward.name);
				}
			}
			if (winner.opponent.player != null) {
				winner.opponent.player.closeInventory();
				PluginVars.closeSpectators(winner.opponent.player, false, reason);
				winner.opponent.player.sendMessage("YOU LOSE by "
						+ ChatColor.RED + reason.toString());
			}
		}
		if (this.duelists[0].player != null) {
			PluginVars.plugin.dueling.removeElement(this.duelists[0].player
					.getName());
			this.duelists[0].player.closeInventory();
		}
		if (this.duelists[1].player != null) {
			PluginVars.plugin.dueling.removeElement(this.duelists[1].player
					.getName());
			this.duelists[1].player.closeInventory();
		}
		if(this.tournament != null) {
			this.tournament.winner(winner);
			this.tournament.nextDuel();
			this.tournament = null;
		}
	}
	
	public boolean activateSpell(SpellCard sc, Duelist activator) {
		TrapEventActivate tea = new TrapEventActivate(this, activator.opponent, activator, sc);
		if(!this.triggerTraps(activator.opponent, activator, tea)) {
			this.sfx(Sound.FIRE_IGNITE);
			sc.activate(this, activator);
			return false;
		}
		return true;
	}

	public void input(Duelist duelist, int slot, ClickType action) {
		/*
		 * 0-4, Opponent's hand 9-13, Opponent's S/T zone 18-22, Opponent's
		 * Monster Zone 27-31, Your Monster Zone 36-40, Your S/T Zone 45-49,
		 * Your Hand
		 */
		if (this.duelists[this.turn] != duelist) {
			if (duelist.player != null)
				duelist.player.sendMessage("NOT YOUR TURN");
			return;
		}
		if (duelist.player != null) {
			duelist.player.playSound(duelist.player.getLocation(), Sound.CLICK,
					1.0f, 1.0f);
		}
		if (duelist.phase == 1) {
			if (slot >= 0 && slot <= 4) {
				// Yes. This is hand.
			} else if (slot >= 9 && slot <= 13) {
				// Yes. This is S/T zone.
			} else if (slot >= 18 && slot <= 22) {
				// Yes. This is Monster Zone.
			} else if (slot >= 27 && slot <= 31) {
				if (duelist.fusion_mat.size() >= 2) {
					if (!duelist.field.monsterzones[slot - 27].isOpen()) {
						duelist.fusion_mat.insertElementAt(
								duelist.field.monsterzones[slot - 27].card, 0);
						duelist.field.monsterzones[slot - 27].remove();
					}
					Card result = null;
					duelist.combo_plays++;
					while (duelist.fusion_mat.size() > 1) {
						result = Fusion.createFusion(this, duelist,
								duelist.fusion_mat.get(0),
								duelist.fusion_mat.get(1)).initiate(true);
						duelist.fusion_mat.remove(0);
						duelist.fusion_mat.remove(0);
						duelist.fusion_mat.insertElementAt(result, 0);

					}
					duelist.selectedCard = result;
					duelist.fusion_mat.clear();
					duelist.selectedStar = 0;
					if (MonsterCard.class.isInstance(duelist.selectedCard)) {
						MonsterCard mc = MonsterCard.class
								.cast(duelist.selectedCard);
						mc.faceup = true;
						mc.position = MonsterPosition.ATTACK;
						mc.star = mc.stars[0];
						duelist.fused = true;
						duelist.selectedZone = slot - 27;
						duelist.phase = 2;
					} else if (SpellCard.class.isInstance(duelist.selectedCard)) {
						SpellCard sc = SpellCard.class
								.cast(duelist.selectedCard);
						this.graveyard.push(sc);
						this.activateSpell(sc, duelist);
						duelist.phase = 3;
					} else if (EquipCard.class.isInstance(duelist.selectedCard)) {
						EquipCard ec = EquipCard.class
								.cast(duelist.selectedCard);
						this.graveyard.push(ec);
						duelist.phase = 3;
					}
				}
			} else if (slot >= 36 && slot <= 40) {
				// Yes. This is your s/t zone.
			} else if (slot >= 45 && slot <= 49) {
				if (action == ClickType.LEFT) {
					if (duelist.fusion_mat.size() == 0) {
						duelist.selectedCard = duelist.hand.cards
								.elementAt(slot - 45);
						if (MonsterCard.class.isInstance(duelist.selectedCard))
							duelist.phase = 2;
						else if (SpellCard.class
								.isInstance(duelist.selectedCard))
							duelist.phase = 4;
						else if (EquipCard.class
								.isInstance(duelist.selectedCard))
							duelist.phase = 6;
						duelist.faceup = false;
						duelist.selectedStar = 0;
					}
				} else if (action == ClickType.RIGHT) {
					if (duelist.hand.size() >= (slot - 44)) {
						duelist.selectedCard = duelist.hand.cards
								.elementAt(slot - 45);
						if (!duelist.fusion_mat.contains(duelist.selectedCard)) {
							duelist.fusion_mat.push(duelist.selectedCard);
							duelist.hand.removeCard(duelist.selectedCard);
						}
					}
				}
			} else if (slot == 8 || slot == 17 || slot == 26 || slot == 35
					|| slot == 44) {
				if (((slot - 8) / 9) + 1 <= duelist.fusion_mat.size()) {
					Card card = duelist.fusion_mat.get((slot - 8) / 9);
					duelist.fusion_mat.removeElement(card);
					duelist.hand.addCard(card);
				}
			}
		} else if (duelist.phase == 2) {
			if (MonsterCard.class.isInstance(duelist.selectedCard)) {
				MonsterCard mc = MonsterCard.class.cast(duelist.selectedCard);
				if (slot >= 27 && slot <= 31) {
					if (!duelist.fused) {
						if (duelist.field.monsterzones[slot - 27].isOpen()) {
							duelist.field.monsterzones[slot - 27]
									.put(duelist.selectedCard);
							mc.star = mc.stars[duelist.selectedStar];
							mc.position = MonsterPosition.ATTACK;
							mc.faceup = duelist.faceup;
							duelist.hand.cards
									.removeElement(duelist.selectedCard);
							duelist.phase = 3;
						} else {
							duelist.fusion_mat.insertElementAt(
									duelist.field.monsterzones[slot - 27].card,
									0);
							duelist.fusion_mat.push(duelist.selectedCard);
							duelist.hand.removeCard(duelist.selectedCard);
							duelist.field.monsterzones[slot - 27].remove();
							duelist.combo_plays++;
							duelist.selectedCard = Fusion.createFusion(this,
									duelist, duelist.fusion_mat.get(0),
									duelist.fusion_mat.get(1)).initiate(true);
							duelist.fusion_mat.clear();
							duelist.selectedZone = slot - 27;
							duelist.fused = true;
							if (MonsterCard.class
									.isInstance(duelist.selectedCard)) {
								mc = MonsterCard.class
										.cast(duelist.selectedCard);
								mc.faceup = true;
								mc.position = MonsterPosition.ATTACK;
								duelist.faceup = true;
								mc.star = mc.stars[0];
								duelist.selectedStar = 0;

							} else if (SpellCard.class
									.isInstance(duelist.selectedCard)) {
								SpellCard sc = SpellCard.class
										.cast(duelist.selectedCard);
								this.graveyard.push(sc);
								this.activateSpell(sc, duelist);
								duelist.phase = 3;
							} else if (EquipCard.class
									.isInstance(duelist.selectedCard)) {
								this.graveyard.push(duelist.selectedCard);
								duelist.phase = 3;
							}
						}
					}
				} else if (slot == 26) {
					if (!duelist.fused)
						duelist.faceup = !duelist.faceup;
					else {
						duelist.field.monsterzones[duelist.selectedZone]
								.put(duelist.selectedCard);
						mc.star = mc.stars[duelist.selectedStar];
						mc.faceup = true;
						mc.position = MonsterPosition.ATTACK;
						duelist.phase = 3;
					}
				} else if (slot == 35) {
					if (MonsterCard.class.isInstance(duelist.selectedCard)) {
						if (duelist.selectedStar == 0)
							duelist.selectedStar = 1;
						else
							duelist.selectedStar = 0;
					}
				} else if (slot == 44) {
					if (!duelist.fused)
						duelist.phase = 1;
				} else if (action == ClickType.RIGHT) {
					if (!duelist.fused)
						duelist.phase = 1;
				}
			}
		} else if (duelist.phase == 3) {
			if (slot == 42) {
				this.sfx(Sound.NOTE_PIANO);
				this.swapTurn();
			} else if (slot >= 27 && slot <= 31) {
				if (!duelist.field.monsterzones[slot - 27].isOpen()) {
					duelist.selectedCard = duelist.field.monsterzones[slot - 27].card;
					MonsterCard mc = MonsterCard.class
							.cast(duelist.selectedCard);
					if (action == ClickType.LEFT && duelist.swords <= 0) {
						if (!this.firstturn) {
							if (!mc.attacked
									&& mc.position == MonsterPosition.ATTACK) {
								duelist.phase = 5;
							}
						}
					} else if (action == ClickType.RIGHT) {
						if (!mc.attacked) {
							mc.changePosition();
						}
					}
				}
			} else if (slot >= 36 && slot <= 40) {
				if (!duelist.field.magiczones[slot - 36].isOpen()) {
					duelist.selectedCard = duelist.field.magiczones[slot - 36].card;
					if (SpellCard.class.isInstance(duelist.selectedCard)) {
						SpellCard sc = SpellCard.class
								.cast(duelist.selectedCard);
						duelist.field.magiczones[slot - 36].toGraveyard(this,
								null);
						this.activateSpell(sc, duelist);
					} else if (EquipCard.class.isInstance(duelist.selectedCard)) {
						duelist.phase = 7;
					}
				}
			}
		} else if (duelist.phase == 4) {
			if (action == ClickType.RIGHT) {
				duelist.phase = 1;
			} else if (SpellCard.class.isInstance(duelist.selectedCard)) {
				SpellCard sc = SpellCard.class.cast(duelist.selectedCard);
				if (slot == 26) {
					duelist.phase = 3;
					this.graveyard.push(sc);
					duelist.hand.cards.removeElement(sc);
					this.activateSpell(sc, duelist);
				} else if (slot >= 36 && slot <= 40) {
					if (duelist.field.magiczones[slot - 36].isOpen()) {
						duelist.field.magiczones[slot - 36].put(sc);
						sc.faceup = false;
						duelist.hand.cards.removeElement(sc);
						duelist.phase = 3;
					}
				} else if (slot == 35) {
					duelist.phase = 1;
				}
			}
		} else if (duelist.phase == 5) {
			if (!MonsterCard.class.isInstance(duelist.selectedCard)) {
				duelist.phase = 3;
			} else {
				MonsterCard mc = MonsterCard.class.cast(duelist.selectedCard);
				if (slot >= 18 && slot <= 22 && action == ClickType.LEFT) {
					if (duelist.opponent.field.emptyMonsterZones()) {
						this.battle(duelist, mc, duelist.opponent, null);
						duelist.phase = 3;
					} else {
						if (!duelist.opponent.field.monsterzones[22 - slot]
								.isOpen()) {
							this.battle(
									duelist,
									mc,
									duelist.opponent,
									MonsterCard.class
											.cast(duelist.opponent.field.monsterzones[22 - slot].card));
							duelist.phase = 3;
						}
					}
				} else if(slot == 26) {
					if(mc.hasEffect() && !mc.usedEffect) {
						this.sfx(Sound.FIRE_IGNITE);
						mc.faceup = true;
						mc.attacked = true;
						mc.usedEffect = true;
						mc.activate(this, duelist);
						duelist.phase = 3;
					}
				}
			}
			if (action == ClickType.RIGHT) {
				duelist.phase = 3;
			}
		} else if (duelist.phase == 6) {
			if (action == ClickType.RIGHT) {
				duelist.phase = 1;
			} else if (!EquipCard.class.isInstance(duelist.selectedCard)) {
				duelist.phase = 1;
			} else {
				if (slot >= 27 && slot <= 31) {
					if (!duelist.field.monsterzones[slot - 27].isOpen()) {
						duelist.hand.removeCard(duelist.selectedCard);
						duelist.fusion_mat
								.push(duelist.field.monsterzones[slot - 27].card);
						duelist.fusion_mat.push(duelist.selectedCard);
						duelist.combo_plays++;
						duelist.selectedCard = Fusion.createFusion(this,
								duelist, duelist.fusion_mat.get(0),
								duelist.fusion_mat.get(1)).initiate(true);
						duelist.selectedCard.faceup = true;
						duelist.field.monsterzones[slot - 27]
								.put(duelist.selectedCard);
						duelist.fusion_mat.clear();
						duelist.phase = 3;
					}
				} else if (slot >= 36 && slot <= 40) {
					if (duelist.field.magiczones[slot - 36].isOpen()) {
						duelist.field.magiczones[slot - 36]
								.put(duelist.selectedCard);
						duelist.selectedCard.faceup = false;
						duelist.hand.cards.remove(duelist.selectedCard);
						duelist.phase = 3;
					}
				} else if (slot == 26) {
					duelist.phase = 1;
				}
			}
		} else if (duelist.phase == 7) {
			if (action == ClickType.RIGHT) {
				duelist.phase = 3;
			} else if (EquipCard.class.isInstance(duelist.selectedCard)) {
				EquipCard ec = EquipCard.class.cast(duelist.selectedCard);
				if (slot >= 27 && slot <= 31) {
					if (!duelist.field.monsterzones[slot - 27].isOpen()) {
						duelist.fusion_mat.insertElementAt(
								duelist.field.monsterzones[slot - 27].card, 0);
						duelist.fusion_mat.push(ec);
						duelist.field.removeCard(duelist.selectedCard);
						duelist.combo_plays++;
						duelist.selectedCard = Fusion.createFusion(this,
								duelist, duelist.fusion_mat.get(0),
								duelist.fusion_mat.get(1)).initiate(true);
						duelist.selectedCard.faceup = true;
						duelist.field.monsterzones[slot - 27]
								.put(duelist.selectedCard);
						duelist.fusion_mat.clear();
						duelist.phase = 3;
					}
				} else if (slot == 26) {
					duelist.phase = 3;
				}
			}
		}
		this.updateInterfaces();
	}

	public void increaseLP(Duelist duelist, int amnt) {
		if (this.duelists[0] == duelist || this.duelists[1] == duelist) {
			TrapEventLPIncrease teli = new TrapEventLPIncrease(this,
					duelist.opponent, duelist, duelist, amnt);
			if (!this.triggerTraps(duelist.opponent, duelist, teli)) {
				duelist.lp += amnt;
				if (duelist.lp > 8000)
					duelist.lp = 8000;
			}
		}
		this.checkLPs();
	}

	public void decreaseLP(Duelist duelist, int amnt) {
		if (this.duelists[0] == duelist || this.duelists[1] == duelist) {
			TrapEventLPDecrease teld = new TrapEventLPDecrease(this, duelist,
					duelist.opponent, duelist, amnt);
			if (!this.triggerTraps(duelist, duelist.opponent, teld)) {
				duelist.lp -= amnt;
			}
		}
		this.checkLPs();
	}

	public boolean checkLPs() {
		Duelist attacker = this.duelists[0];
		Duelist defender = this.duelists[1];
		if (attacker.lp <= 0 && defender.lp <= 0) {
			this.endDuel(null, null);
			return true;
		} else if (attacker.lp <= 0) {
			this.endDuel(defender, WinReason.TOTAL_ANNIHILATION);
			return true;
		} else if (defender.lp <= 0) {
			this.endDuel(attacker, WinReason.TOTAL_ANNIHILATION);
			return true;
		}
		return false;
	}

	public boolean triggerTraps(Duelist duelist, Duelist triggerer, TrapEvent e) {
		for (MagicZone mz : duelist.field.magiczones) {
			if (mz.isOpen())
				continue;
			if (TrapCard.class.isInstance(mz.card)) {
				TrapCard tc = TrapCard.class.cast(mz.card);
				if (tc.trigger(this, duelist, triggerer, e)) {
					this.sfx(Sound.FIRE_IGNITE);
					duelist.field.destroyCard(tc, this, null);
					duelist.trigger_trap++;
					duelist.opponent.rewards.add(tc);
					return true;
				}
			}
		}
		return false;
	}

	public void sfx(Sound sound) {
		List<Spectator> spectators = null;
		if (this.duelists[0].player != null) {
			this.duelists[0].player.playSound(
					this.duelists[0].player.getLocation(), sound, 1.0f, 1.0f);
		}
		if (this.duelists[1].player != null) {
			this.duelists[1].player.playSound(
					this.duelists[1].player.getLocation(), sound, 1.0f, 1.0f);
		}
		spectators = PluginVars.getSpectators(this.duelists[0]);
		for(Spectator s : spectators) {
			s.spectator.playSound(s.spectator.getLocation(), sound, 1.0f, 1.0f);
		}
		spectators = PluginVars.getSpectators(this.duelists[1]);
		for(Spectator s : spectators) {
			s.spectator.playSound(s.spectator.getLocation(), sound, 1.0f, 1.0f);
		}
	}

	public void battle(Duelist attacker, MonsterCard attacking,
			Duelist defender, MonsterCard defending) {
		attacking.attacked = true;
		attacking.faceup = true;
		TrapEventAttack tea = new TrapEventAttack(this, defender, attacker,
				attacker, attacking, defender, defending);
		if (this.triggerTraps(defender, attacker, tea)) {
			return;
		}
		if (defending == null) {
			defender.lp -= (attacking.getAtk());
			this.sfx(Sound.CREEPER_DEATH);
		} else {
			int bonus = attacking.bonus;
			int bonus2 = defending.bonus;
			attacking.faceup = true;
			int aap, adp, dap, ddp;
			aap = attacking.getAtk();
			adp = attacking.getDef();
			dap = defending.getAtk();
			ddp = defending.getDef();

			defending.faceup = true;
			if (attacking.star.isSuperiorTo(defending.star)) {
				// System.out.println("IS SUPERIOR");
				aap += 500;
				adp += 500;
				bonus += 500;
			} else if (defending.star.isSuperiorTo(attacking.star)) {
				dap += 500;
				ddp += 500;
				bonus2 += 500;
			}
			this.sfx(Sound.CREEPER_DEATH);
			if (defending.position == MonsterPosition.ATTACK) {
				if (aap > dap) {
					defender.lp -= (aap - dap);
					try {
						defender.field.getZoneWithCard(defending).toGraveyard(
								this, attacker);
						defender.card_destruction++;
					} catch (NoZoneOpenException e) {
					}
				} else if (aap == dap) {
					try {
						attacker.field.getZoneWithCard(attacking).toGraveyard(
								this, defender);
						attacker.card_destruction++;
					} catch (NoZoneOpenException e) {
					}
					try {
						defender.field.getZoneWithCard(defending).toGraveyard(
								this, attacker);
						defender.card_destruction++;
					} catch (NoZoneOpenException e) {
					}
				} else if (aap < dap) {
					try {
						attacker.field.getZoneWithCard(attacking).toGraveyard(
								this, defender);
						attacker.card_destruction++;
					} catch (NoZoneOpenException e) {
					}
					attacker.lp -= (dap - aap);
				}
			} else if (defending.position == MonsterPosition.DEFENSE) {
				if (aap > ddp) {
					try {
						defender.field.getZoneWithCard(defending).toGraveyard(
								this, attacker);
						defender.card_destruction++;
					} catch (NoZoneOpenException e) {
					}
				} else if (aap < ddp) {
					attacker.lp -= (ddp - aap);
				}
			}
		}

		if (attacker.lp <= 0 && defender.lp <= 0)
			this.endDuel(null, null);
		else if (attacker.lp <= 0) {
			this.endDuel(defender, WinReason.TOTAL_ANNIHILATION);
		} else if (defender.lp <= 0) {
			this.endDuel(attacker, WinReason.TOTAL_ANNIHILATION);
		}
	}

	public boolean containsPlayer(Player p) {
		if (this.duelists[0].player == p)
			return true;
		else if (this.duelists[1].player == p)
			return true;
		else
			return false;
	}

	public Duelist getDuelist(Player p) throws NotDuelingException {
		if (this.duelists[0].player == p)
			return this.duelists[0];
		else if (this.duelists[1].player == p)
			return this.duelists[1];
		throw new NotDuelingException();
	}

	public boolean hasExodia(Duelist duelist) {
		for (int c = 17; c <= 21; c++) {
			if (!this.duelists[turn].hand.containsCardId(c))
				return false;
		}
		return true;
	}

	public void playerTurn(int turn) {
		int c;
		Duelist duelist = this.duelists[turn];
		Duelist opponent = this.duelists[turn].opponent;
		MonsterZone mz;
		this.turn = turn;
		for (c = 0; c < opponent.field.monsterzones.length; c++) {
			if (opponent.field.monsterzones[c].card != null) {
				MonsterCard mc = MonsterCard.class
						.cast(opponent.field.monsterzones[c].card);
				mc.attacked = false;
				if(mc.stolen % 2 == 1) {
					try {
						mz = duelist.field.getFirstOpenMonsterZone();
						mz.put(opponent.field.monsterzones[c].card);
						opponent.field.monsterzones[c].remove();
					} catch (NoZoneOpenException e) {
						opponent.field.monsterzones[c].toGraveyard(this, opponent);
					}
				}
			}
		}
		duelist.hand.revealed = false;
		if (!duelist.drawUntil(5)) {
			this.endDuel(this.duelists[turn].opponent, WinReason.ATTRITION);
		}
		if (this.hasExodia(duelist)) {
			this.endDuel(duelist, WinReason.EXODIA);
		}
		duelist.phase = 1;
		duelist.fused = false;
		
		this.updateInterfaces();
		if (duelist.player == null)
			new Thread(new AI(duelist, this)).start();
	}

	public void swapTurn() {
		this.firstturn = false;
		this.duelists[this.turn].phase = 0;
		this.duelists[this.turn].turns++;
		if (this.duelists[this.turn].swords > 0)
			this.duelists[this.turn].swords--;
		if (this.turn == 0)
			this.turn = 1;
		else
			this.turn = 0;
		this.playerTurn(this.turn);
	}

	public void updateInterfaces() {
		this.duelists[0].updateInterface(this.terrain, this.graveyard);
		this.duelists[1].updateInterface(this.terrain, this.graveyard);
	}

	public Duelist getDuelistFromPlayer(Player p) throws NotDuelingException {
		if (this.duelists[0].player == p)
			return this.duelists[0];
		if (this.duelists[1].player == p)
			return this.duelists[1];
		throw new NotDuelingException();
	}

	public void increasePower(Duelist powering, MonsterCard mc, int amnt) {
		if (!this.triggerTraps(powering.opponent, powering,
				new TrapEventPowerUp(this, powering.opponent, powering,
						powering, mc, amnt))) {
			mc.bonus += amnt;
		}
	}
}
