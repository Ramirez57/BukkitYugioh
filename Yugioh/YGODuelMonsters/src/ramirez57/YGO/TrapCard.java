package ramirez57.YGO;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.script.ScriptException;

public class TrapCard extends SpellCard {

	public Class<? extends TrapEvent> trigger;
	public File effectFile;
	
	public static TrapCard create(String name, Class<? extends TrapEvent> trigger, File effect) {
		TrapCard tc = new TrapCard();
		tc.name = name;
		tc.trigger = trigger;
		tc.effectFile = effect;
		return tc;
	}
	
	public boolean shouldActivate(Duel duel, Duelist duelist) {
		return false;
	}
	
	public void activate(Duel duel, Duelist duelist) {
		return; //Trap activation does nothing.
	}
	
	public boolean trigger(Duel duel, Duelist oneWithTrap, Duelist triggerer, TrapEvent e) {
		if(this.trigger.isInstance(e)) {
			try {
				e.duelistWithTrap = oneWithTrap;
				e.duelistTriggerer = triggerer;
				PluginVars.engine.eval(new FileReader(this.effectFile));
				try {
					return Boolean.class.cast(PluginVars.engineinv.invokeFunction("effect", duel, e));
				} catch (NoSuchMethodException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (ScriptException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		return false;
	}
}
