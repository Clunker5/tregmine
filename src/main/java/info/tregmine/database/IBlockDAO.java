package info.tregmine.database;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Map;

public interface IBlockDAO {

    int blockValue(Block a) throws DAOException;

    Map<Material, Integer> loadBlockMinePrices() throws DAOException;
}
