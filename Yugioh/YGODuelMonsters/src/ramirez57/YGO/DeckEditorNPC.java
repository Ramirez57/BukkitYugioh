package ramirez57.YGO;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DeckEditorNPC extends DeckEditor {
	public UUID deckOwner;

	public DeckEditorNPC() {

	}

	public static DeckEditorNPC open(Player editor, UUID deckOwner)
			throws NoDeckException {
		DeckEditorNPC de = new DeckEditorNPC();
		de.editing = editor;
		de.deckOwner = deckOwner;
		de.deck = PluginVars.getDeckFor(deckOwner);
		de.inventory = Bukkit.getServer().createInventory(editor, 45, "Deck Editor");
		editor.openInventory(de.inventory);
		PluginVars.editing.put(editor, de);
		de.updateInterface();
		return de;
	}
}
