package info.tregmine.api.coreprotect;

import info.tregmine.Tregmine;
import net.coreprotect.CoreProtectAPI;
import org.bukkit.block.Block;

import java.util.Iterator;
import java.util.List;

/**
 * Created by ericrabil on 3/26/17.
 */
public class TregmineCoreProtectAPI extends CoreProtectAPI {
    public List<String[]> lookup(Block block) {
        return TregmineLookup.block_lookup_api(block);
    }

    public boolean isPlaced(Block block) {
        if (!functionality())
            return false;
        List<String[]> check = this.lookup(block);
        Iterator i$ = check.iterator();
        return i$.hasNext();
    }

    private boolean functionality() {
        return Tregmine.coreProtectEnabled();
    }
}
