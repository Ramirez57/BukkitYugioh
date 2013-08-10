package ramirez57.YGO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.script.ScriptException;

public class SpellCard extends Card {
	
	public File effectFile;
	
	public static SpellCard create(String name, File effect) {
		SpellCard sc = new SpellCard();
		sc.name = name;
		sc.effectFile = effect;
		return sc;
	}
	
	public void activate(Duel duel, Duelist duelist) {
		try {
			PluginVars.engine.eval(new FileReader(this.effectFile));
			try {
				PluginVars.engineinv.invokeFunction("effect", duel, duelist);
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (ScriptException e) {
			e.printStackTrace();
		}
	}
	
	public boolean shouldActivate(Duel duel, Duelist duelist) {
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
}
