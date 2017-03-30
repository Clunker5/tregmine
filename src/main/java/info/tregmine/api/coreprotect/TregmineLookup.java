package info.tregmine.api.coreprotect;

import net.coreprotect.Functions;
import net.coreprotect.database.Database;
import net.coreprotect.database.Lookup;
import net.coreprotect.model.Config;
import org.bukkit.block.Block;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ericrabil on 3/26/17.
 */
public class TregmineLookup extends Lookup {

    public static List<String[]> block_lookup_api(Block block) {
        ArrayList<String[]> result = new ArrayList<String[]>();

        try {
            if (block == null) {
                return result;
            }

            int x = block.getX();
            int y = block.getY();
            int z = block.getZ();
            int wid = Functions.getWorldId(block.getWorld().getName());

            Connection connection = Database.getConnection(false);
            if (connection == null) {
                return result;
            }

            Statement statement = connection.createStatement();
            String query = "SELECT time,user,action,type,data,rolled_back FROM " + Config.prefix + "block WHERE wid = '" + wid + "' AND x = '" + x + "' AND z = '" + z + "' AND y = '" + y + "' ORDER BY rowid DESC";
            ResultSet rs = statement.executeQuery(query);

            while (rs.next()) {
                int result_time = rs.getInt("time");
                int result_userid = rs.getInt("user");
                int result_action = rs.getInt("action");
                int result_type = rs.getInt("type");
                int result_data = rs.getInt("data");
                int result_rolled_back = rs.getInt("rolled_back");
                if (Config.player_id_cache_reversed.get(result_userid) == null) {
                    Database.loadUserName(connection, result_userid);
                }

                String result_user = Config.player_id_cache_reversed.get(result_userid);
                String line = result_time + "," + result_user + "," + x + "." + y + "." + z + "," + result_type + "," + result_data + "," + result_action + "," + result_rolled_back + "," + wid + ",";
                result.add(Functions.toStringArray(line));
            }

            rs.close();
            statement.close();
            connection.close();
        } catch (Exception var22) {
            var22.printStackTrace();
        }

        return result;
    }

}
