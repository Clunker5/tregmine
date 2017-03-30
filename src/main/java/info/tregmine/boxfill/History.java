package info.tregmine.boxfill;

import info.tregmine.api.GenericPlayer;
import info.tregmine.api.TregminePlayer;

import java.util.HashMap;
import java.util.Map;

public class History {
    private Map<GenericPlayer, SavedBlocks> currentState;

    public History() {
        currentState = new HashMap<>();
    }

    public SavedBlocks get(GenericPlayer player) {
        return currentState.get(player);
    }

    public void set(GenericPlayer player, SavedBlocks blocks) {
        currentState.put(player, blocks);
    }
}
