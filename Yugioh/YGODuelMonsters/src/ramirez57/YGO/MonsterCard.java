package ramirez57.YGO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import javax.script.ScriptException;

public class MonsterCard extends Card implements Comparable<MonsterCard> {
	public boolean attacked;
	public int atk;
	public int def;
	public int level;
	public MonsterType type;
	public MonsterAttribute attribute;
	public GuardianStar[] stars;
	public GuardianStar star;
	public MonsterPosition position = MonsterPosition.ATTACK;
	public File effectFile;
	public boolean usedEffect;
	public String[] effectDesc;
	public List<Integer> equips;
	
	public MonsterCard() {
		super();
		this.stars = new GuardianStar[2];
	}
	
	public static MonsterCard create(String name) {
		MonsterCard mc = new MonsterCard();
		mc.name = name;
		mc.attacked = false;
		mc.usedEffect = false;
		return mc;
	}
	
	public boolean hasTrait(Trait trait) {
		int c;
		if(MonsterType.fromString(trait.toString()) == this.type)
			return true;
		for(c = 0; c < this.traits.length; c++) {
			if(this.traits[c] == trait)
				return true;
		}
		return false;
	}
	
	public void changePosition() {
		if(this.position == MonsterPosition.ATTACK) {
			this.position = MonsterPosition.DEFENSE;
		} else {
			this.position = MonsterPosition.ATTACK;
		}
	}
	
	public boolean hasEffect() {
		return PluginVars.monster_effects && (this.effectFile != null);
	}

	public int compareTo(MonsterCard o) {
		if(this.position == MonsterPosition.ATTACK) {
			if(o.position == MonsterPosition.DEFENSE) {
				return (this.atk + this.bonus) - (o.def + o.bonus);
			} else {
				return (this.atk + this.bonus) - (o.atk + o.bonus);
			}
		} else {
			if(o.position == MonsterPosition.DEFENSE){
				return (this.def + this.bonus) - (o.def + o.bonus);
			} else {
				return (this.def + this.bonus) - (o.atk + o.bonus);
			}
		}
	}
	
	public void activate(Duel duel, Duelist duelist) {
		if(this.effectFile != null) {
			try {
				PluginVars.engine.eval(new FileReader(this.effectFile));
				try {
					PluginVars.engineinv.invokeFunction("effect", duel, duelist);
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ScriptException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public int getAtk() {
		if(this.atk + this.bonus < 0)
			return 0;
		else return (this.atk + this.bonus);
	}
	
	public int getDef() {
		if(this.def + this.bonus < 0)
			return 0;
		else return (this.def + this.bonus);
	}
	
	public boolean canEquip(EquipCard ec) {
		return(ec.equipsToAll || this.equips.contains(ec.id));
	}
	
	public boolean shouldActivate(Duel duel, Duelist duelist) {
		if(!this.hasEffect()) return false;
		try {
			PluginVars.engine.eval(new FileReader(this.effectFile));
			try {
				Object ret = PluginVars.engineinv.invokeFunction("shouldActivate", duel, duelist);
				if(Boolean.class.isInstance(ret)) {
					Boolean retval = Boolean.class.cast(ret);
					//System.out.println("Return is " + retval);
					return retval;
				}
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean canDefeat(MonsterCard opp) {
		int mypow;
		int oppow;
		if(opp.position == MonsterPosition.ATTACK) {
			oppow = opp.getAtk(); 
		} else {
			oppow = opp.getDef();
		}
		mypow = this.getAtk();
		if(this.star.isSuperiorTo(opp.star)) {
			mypow += 500;
		} else if(opp.star.isSuperiorTo(this.star)) {
			oppow += 500;
		}
		if(mypow > oppow) {
			return true;
		}
		return false;
	}
}
