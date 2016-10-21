package info.tregmine.database;

import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;

import info.tregmine.api.TregminePlayer;

public interface IBlockDAO {
	public void addPlaced(Block a, TregminePlayer player) throws DAOException;

	public void blockDestroyed(Block a) throws DAOException;

	int blockValue(Block a) throws DAOException;

	public boolean isPlaced(Block a) throws DAOException;

	public Map<Material, Integer> loadBlockMinePrices() throws DAOException;
}
